package com.bjfu.springboot.rna_tool.fastqc;

import com.bjfu.springboot.rna_tool.config.ConnectLinuxCon;
import com.jcraft.jsch.*;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author DONGSHENG
 * @version 1.0
 */
public class FastqcCheck extends ConnectLinuxCon {



    public void checkFile(String toolPath,String sequence_file1,String seqFileName){


        String command = "cd "+toolPath+"/01raw_data;fastqc "+sequence_file1+";"+"unzip "+seqFileName+"_fastqc.zip";

        System.out.println("Start performing FastQC analysis");
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

            channel.disconnect();
            session.disconnect();

        } catch (JSchException | IOException e) {
            e.printStackTrace();
        }
    }

}
