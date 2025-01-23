package com.bjfu.springboot.rna_tool.initializtion;

import com.bjfu.springboot.rna_tool.config.ConnectLinuxCon;
import com.jcraft.jsch.*;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author DONGSHENG
 * @version 1.0
 */


public class LoadingFiles extends ConnectLinuxCon {
    public void uploadFiles(String toolPath,String sequence_file1,String sequence_file2,String annotation_fileName,String reference_genome){
        System.out.println("Start loading files");
        String genome = "Ptr.fa";
        String command = "cd "+toolPath+";" +
                "cp /home/RSAP_Data/resource/"+reference_genome+" /home/RSAP_Data/resource/"+annotation_fileName+" "+toolPath+"/00ref;" +
                "cp /home/RSAP_Data/resource/"+sequence_file1+" /home/RSAP_Data/resource/"+sequence_file2+" "+toolPath+"/01raw_data;";
        System.out.println(command);
        try {
            JSch jsch = new JSch();

            Session session = jsch.getSession(username, host, 22);
            session.setPassword(password);

            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);


            session.connect();

            Channel channel = session.openChannel("exec");


            ((ChannelExec) channel).setCommand(command);

            InputStream in = channel.getInputStream();

            channel.connect();

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) > 0) {
                System.out.print(new String(buffer, 0, bytesRead));
            }
            System.out.println("File loading complete");
            channel.disconnect();
            session.disconnect();

        } catch (JSchException | IOException e) {
            e.printStackTrace();
        }
    }
}
