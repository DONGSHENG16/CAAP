package com.bjfu.springboot.rna_tool.comparison;

import com.bjfu.springboot.rna_tool.config.ConnectLinuxCon;
import com.jcraft.jsch.*;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author DONGSHENG
 * @version 1.0
 */
public class BamToSort extends ConnectLinuxCon {
    public void Samtools_sort(String toolPath){
        String command = "cd "+toolPath+";" +
                "samtools sort 03align_out/output.bam -o 03align_out/sorted_output.bam";
        try {
            JSch jsch = new JSch();

            Session session = jsch.getSession(username, host, 22);
            session.setPassword(password);

            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            System.out.println("Connecting to SSH");

            session.connect();

            // Create SSH channel
            Channel channel = session.openChannel("exec");
            System.out.println("Creating SSH channel");

            // Set the command to be executed
            ((ChannelExec) channel).setCommand(command);

            // Get the command's output stream
            InputStream in = channel.getInputStream();

            // Connect the SSH channel
            channel.connect();

            // Read the command's output
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) > 0) {
                System.out.print(new String(buffer, 0, bytesRead));
            }
            System.out.println("Index creation completed");

            // Disconnect the channel and session
            channel.disconnect();
            session.disconnect();

        } catch (JSchException | IOException e) {
            e.printStackTrace();
        }
    }
}
