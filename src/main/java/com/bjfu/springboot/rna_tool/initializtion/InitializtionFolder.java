package com.bjfu.springboot.rna_tool.initializtion;

import com.bjfu.springboot.rna_tool.config.ConnectLinuxCon;
import com.jcraft.jsch.*;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author DONGSHENG
 * @version 1.0
 */
public class InitializtionFolder extends ConnectLinuxCon {

    public String GenerateFolder() {

        LocalDateTime now = LocalDateTime.now();


        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");


        String formattedDateTime = now.format(formatter);


        String ProjectFolder = "Project_" + formattedDateTime;

        System.out.println("ProjectFolder: " + ProjectFolder);
        return ProjectFolder;
    }
    @Test
    public String mkdirFolder(){
        System.out.println("Start configuring the environment and initializing the folder");
        String ProjectFolder = GenerateFolder();
        System.out.println(ProjectFolder);

        String command = "cd /home/RSAP_Data;" +
                         "mkdir "+ProjectFolder+";"+
                         "cd "+ProjectFolder+";"+
                         "mkdir 00ref;mkdir 01raw_data;mkdir returnFiles" +
                         "mkdir 02clean_data;mkdir -p 03align_out/seq_data;" +
                         "mkdir 04rsem_out;mkdir arab_RSEM;mkdir 04ration;mkdir genInd;" +
                         "mkdir arab_STAR_genome;mkdir adaptFile;touch TruSeq3-PE.fa";
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
            System.out.println("Environment configuration and folder initialization complete");

            channel.disconnect();
            session.disconnect();

        } catch (JSchException | IOException e) {
            e.printStackTrace();
        }
        return ProjectFolder;
    }
}
