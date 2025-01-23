package com.bjfu.springboot.rna_tool.comparison;

import com.bjfu.springboot.rna_tool.config.ConnectLinuxCon;
import com.bjfu.springboot.rna_tool.fastqc.AnalyzeFile;
import com.jcraft.jsch.*;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author DONGSHENG
 * @version 1.0
 */
public class STAR_ExecuteComparison extends ConnectLinuxCon {
    @Test
    public void SATRcomparison(String toolPath,String sequence_file1,String sequence_file2,String seqFileName){

        AnalyzeFile analyzeFile = new AnalyzeFile();
        int seqLen =  analyzeFile.SeqLength(toolPath,seqFileName) - 1;
        String command = "cd "+toolPath+";" +
                "/usr/bin/time -v STAR  --runThreadN 5 --genomeDir arab_STAR_genome " +
                "--readFilesCommand zcat --readFilesIn  02clean_data/output_pair_"+sequence_file1+"  02clean_data/output_pair_"+sequence_file2+" " +
                "--outFileNamePrefix 03align_out/seq_data --outSAMtype BAM SortedByCoordinate " +
                "--outBAMsortingThreadN 5 --quantMode TranscriptomeSAM GeneCounts 2> SATR_EC_log.txt";

        try {
            JSch jsch = new JSch();

            Session session = jsch.getSession(username, host, 22);
            session.setPassword(password);

            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            System.out.println("Connecting to SSH");

            session.connect();

            Channel channel = session.openChannel("exec");
            System.out.println("Creating SSH channel");

            ((ChannelExec) channel).setCommand(command);

            InputStream in = channel.getInputStream();

            channel.connect();

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) > 0) {
                System.out.print(new String(buffer, 0, bytesRead));
            }

            channel.disconnect();
            session.disconnect();

        } catch (JSchException | IOException e) {
            e.printStackTrace();
        }
    }

    public int readUmReads() {
        int percentageInt = 0;
        try {
            String filePath = "/home/dongsheng/data03/03align_out/seq_dataLog.final.out";

            JSch jsch = new JSch();
            Session session = jsch.getSession(username, host, 22);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();

            Channel channel = session.openChannel("exec");
            ((ChannelExec) channel).setCommand("cat " + filePath);
            channel.setInputStream(null);
            ((ChannelExec) channel).setErrStream(System.err);

            InputStream in = channel.getInputStream();
            channel.connect();

            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            String line;
            Pattern pattern = Pattern.compile("Uniquely mapped reads % \\|\\s+(\\d+\\.\\d+)%");
            while ((line = reader.readLine()) != null) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    String percentageStr = matcher.group(1);
                    double percentage = Double.parseDouble(percentageStr);
                    percentageInt = (int) percentage;
                    System.out.println("Uniquely mapped reads percentage as int: " + percentageInt);
                }
            }

            reader.close();
            channel.disconnect();
            session.disconnect();
        } catch (JSchException | IOException e) {
            e.printStackTrace();
        }
        return percentageInt;
    }

}
