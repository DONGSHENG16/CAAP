package com.bjfu.springboot.rna_tool.fastqc;

import com.bjfu.springboot.rna_tool.Trimmomatic.Trimmomatic;
import com.bjfu.springboot.rna_tool.config.ConnectLinuxCon;
import com.jcraft.jsch.*;

import java.io.*;
import java.util.*;

/**
 * @author DONGSHENG
 * @version 1.0
 */
public class AnalyzeFile extends ConnectLinuxCon {

    public void readAnyStr(String toolPath,String seqFileName,String sequence_file1,String sequence_file2) {
        String remoteFilePath = toolPath + "/01raw_data/"+seqFileName+"_fastqc/summary.txt"; // 文件路径

        ArrayList<String> list = new ArrayList<>();
        try {
            JSch jsch = new JSch();

            Session session = jsch.getSession(username, host, 22);
            session.setPassword(password);

            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);

            session.connect();

            Channel channel = session.openChannel("exec");

            ((ChannelExec) channel).setCommand("cut -c1-4 " + remoteFilePath); // 使用cut命令截取前五个字符

            InputStream in = channel.getInputStream();

            channel.connect();

            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = reader.readLine()) != null) {
                list.add(line);
            }
            if (list.get(3).equals("PASS") && list.get(3).equals("PASS") && list.get(4).equals("PASS") && list.get(9).equals("PASS")){
                System.out.println("FastQC passed, proceed to the next step of alignment");
            }else{
               int SClineCount = LineCount(toolPath,seqFileName);
               int arr[] = ColumnDifference(SClineCount,toolPath,seqFileName);
               if (arr[0] == -1){
                   System.out.println("Per base sequence content fail,Due to contamination, the sequence has exceeded 25% of the total sequence, and data preprocessing cannot be performed");
               }else {
                   Trimmomatic trimmomatic = new Trimmomatic();
                   trimmomatic.automata(arr,toolPath,sequence_file1,sequence_file2);
               }

            }

            channel.disconnect();
            session.disconnect();

        } catch (JSchException | IOException e) {
            e.printStackTrace();
        }
    }


    //3.解析summary.txt文件

    //计算行数
    public int LineCount(String toolPath,String seqFileName) {
        String remoteFilePath = toolPath+"/01raw_data/"+seqFileName+"_fastqc/fastqc_data.txt"; // 远程文件路径
        String startMarker = "#Base\tG\tA\tT\tC"; // 起始标志
        String endMarker = "#GC Content\tCount"; // 结束标志
        int lineCount = 0;
        boolean counting = false;

        try {
            JSch jsch = new JSch();
            Session session = jsch.getSession(username, host, 22);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();

            Channel channel = session.openChannel("sftp");
            channel.connect();
            ChannelSftp sftpChannel = (ChannelSftp) channel;

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(sftpChannel.get(remoteFilePath)))) {
                String line;

                // 逐行读取文件
                while ((line = reader.readLine()) != null) {
                    // 检查是否到达起始标志
                    if (line.contains(startMarker)) {
                        counting = true;
                        continue;
                    }

                    // 检查是否到达结束标志
                    if (line.contains(endMarker)) {
                        counting = false;
                        break; // 可以选择在此处退出循环，如果不需要继续读取文件的话
                    }

                    // 如果处于计数状态，则递增行数
                    if (counting) {
                        lineCount++;
                    }
                }
            } finally {
                sftpChannel.disconnect();
                session.disconnect();
            }
        } catch (JSchException | SftpException | IOException e) {
            e.printStackTrace();
        }

        System.out.println("在起始标志和结束标志之间的行数为: " + lineCount);
        return lineCount;
    }
    //读取序列长度
    public int SeqLength(String Path,String seqFileName) {

        String filePath = Path+"/01raw_data/"+seqFileName+"_fastqc/fastqc_data.txt";
        int number = 0;
        try {
            JSch jsch = new JSch();
            Session session = jsch.getSession(username, host, 22);
            session.setConfig("StrictHostKeyChecking", "no");
            session.setPassword(password);
            session.connect();

            Channel channel = session.openChannel("exec");
            ((ChannelExec) channel).setCommand("cat " + filePath);
            channel.setInputStream(null);
            ((ChannelExec) channel).setErrStream(System.err);

            InputStream in = channel.getInputStream();
            channel.connect();

            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Sequence length")) {
                    // 获取“Sequence length”右边的数字
                    String[] parts = line.split("\\s+"); // 使用空格分割行
                    if (parts.length > 2) {
                        String numberStr = parts[2]; // 第三个部分为数字字符串
                        try {
                            number = Integer.parseInt(numberStr);
                            System.out.println("Sequence length: " + number);
                        } catch (NumberFormatException e) {
                            System.err.println("Invalid number format: " + numberStr);
                        }
                    } else {
                        System.err.println("Invalid line format: " + line);
                    }
                }
            }

            reader.close();
            channel.disconnect();
            session.disconnect();
        } catch (JSchException | java.io.IOException e) {
            e.printStackTrace();
        }
        return number;
    }
    //读取GC含量
    public int SeqGCcount(String toolPath,String seqFileName) {

        String filePath = toolPath+"/01raw_data/"+seqFileName+"_fastqc/fastqc_data.txt";
        System.out.println(filePath);
        int gcContent = -1; // 初始化为-1，表示未找到有效的GC内容
        try {
            JSch jsch = new JSch();
            Session session = jsch.getSession(username, host, 22);
            session.setConfig("StrictHostKeyChecking", "no");
            session.setPassword(password);
            session.connect();

            Channel channel = session.openChannel("exec");
            ((ChannelExec) channel).setCommand("cat " + filePath);
            channel.setInputStream(null);
            ((ChannelExec) channel).setErrStream(System.err);

            InputStream in = channel.getInputStream();
            channel.connect();

            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("%GC")) {
                    // 获取“%GC”右边的数字
                    String[] parts = line.split("\\s+"); // 使用空格分割行
                    if (parts.length > 1) {
                        String gcStr = parts[1]; // 第二个部分为数字字符串
                        try {
                            gcContent = Integer.parseInt(gcStr);
                            System.out.println("%GC: " + gcContent);
                        } catch (NumberFormatException e) {
                            System.err.println("Invalid GC content format: " + gcStr);
                        }
                    } else {
                        System.err.println("Invalid line format: " + line);
                    }
                }
            }

            reader.close();
            channel.disconnect();
            session.disconnect();
        } catch (JSchException | java.io.IOException e) {
            e.printStackTrace();
        }
        return gcContent;
    }

    //文件解析
    public int[] ColumnDifference(int lineCount,String toolPath,String seqFileName) {
        System.out.println("开始文件解析ColumnDifference");
        String remoteFilePath = toolPath+"/01raw_data/"+seqFileName+"_fastqc/fastqc_data.txt";
        int[] objectArr = new int[0];
        int LineCount = lineCount - 3;

        try {
            JSch jsch = new JSch();
            Session session = jsch.getSession(username, host, 22);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();

            Channel channel = session.openChannel("sftp");
            channel.connect();
            ChannelSftp sftpChannel = (ChannelSftp) channel;

            InputStream inputStream = sftpChannel.get(remoteFilePath);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            boolean startPrinting = false;
            int markerLineNumber = 0;
            int currentLineNumber = 0;
            Map<Integer, String> resultMap = new TreeMap<>();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                currentLineNumber++;
                if (startPrinting) {
                    String[] columns = line.split("\\s+");
                    if (columns.length >= 5) {
                        double col3 = Double.parseDouble(columns[2]);
                        double col4 = Double.parseDouble(columns[3]);
                        double col2 = Double.parseDouble(columns[1]);
                        double col5 = Double.parseDouble(columns[4]);
                        if ((Math.abs(col3 - col4) / Math.max(col3,col4) ) > 0.2 || (Math.abs(col2 - col5) / Math.max(col2,col5) ) > 0.2) {
                            resultMap.put(currentLineNumber - markerLineNumber, line);
                        }
                    }
                    if (currentLineNumber - markerLineNumber >= LineCount) {
                        break;
                    }
                } else if (line.contains("#Base\tG\tA\tT\tC")) {
                    startPrinting = true;
                    markerLineNumber = currentLineNumber + 1; // 标记位置的下一行为第一行
                }
            }


            // 输出排序后的数据
            for (Map.Entry<Integer, String> entry : resultMap.entrySet()) {
                System.out.println("Line " + (entry.getKey() + 1) + ": " + entry.getValue());
            }

            // Grouping based on ratio
            int size = SeqLength(toolPath,seqFileName);//序列的长度
            int group1 = (int) Math.ceil(0.25 * size);
            int group2 = (int) Math.ceil(0.75 * size);


            Map<Integer, String> group1Map = new TreeMap<>();
            Map<Integer, String> group2Map = new TreeMap<>();
            Map<Integer, String> group3Map = new TreeMap<>();
            int i = 0;
            for (Map.Entry<Integer, String> entry : resultMap.entrySet()) {
                if (entry.getKey() < group1) {
                    group1Map.put(entry.getKey(), entry.getValue());
                } else if (entry.getKey() < group2) {
                    group2Map.put(entry.getKey(), entry.getValue());
                } else {
                    group3Map.put(entry.getKey(), entry.getValue());
                }
            }

            // Printing grouped data
            System.out.println("\nGroup 0-25%:");
            for (Map.Entry<Integer, String> entry : group1Map.entrySet()) {
                System.out.println("Line " + (entry.getKey() + 1) + ": " + entry.getValue());
            }

            System.out.println("\nGroup 25-75%:");
            for (Map.Entry<Integer, String> entry : group2Map.entrySet()) {
                System.out.println("Line " + (entry.getKey() + 1) + ": " + entry.getValue());
            }

            System.out.println("\nGroup 75-100%:");
            for (Map.Entry<Integer, String> entry : group3Map.entrySet()) {
                System.out.println("Line " + (entry.getKey() + 1) + ": " + entry.getValue());
            }
            //获取区间的第一个值和最后一个值的键
            objectArr = new int[4];
            // 获取第一个值
            if (!(group2Map.isEmpty())){
                objectArr[0] = -1;
                return objectArr;
            }
            if (group1Map.isEmpty()) {
                objectArr[0] = 0;
                objectArr[1] = 0;
            } else {
                objectArr[0] = ((TreeMap<Integer, String>) group1Map).navigableKeySet().first() + 1;
                objectArr[1] = ((TreeMap<Integer, String>) group1Map).navigableKeySet().last() + 1;
            }
            if (group3Map.isEmpty()) {
                objectArr[2] = 0;
                objectArr[3] = 0;
            } else {
                objectArr[2] = ((TreeMap<Integer, String>) group3Map).navigableKeySet().first() + 1;
                objectArr[3] = ((TreeMap<Integer, String>) group3Map).navigableKeySet().last() + 1;
            }

            for (int j = 0; j < objectArr.length; j++) {
                System.out.println(objectArr[j]);

            }

            bufferedReader.close();
            sftpChannel.disconnect();
            session.disconnect();
        } catch (JSchException | SftpException | IOException e) {
            e.printStackTrace();
        }
        return objectArr;
    }

}
