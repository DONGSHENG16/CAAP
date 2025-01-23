package com.bjfu.springboot.rna_tool.comparison;

import com.bjfu.springboot.rna_tool.config.ConnectLinuxCon;
import com.jcraft.jsch.*;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author DONGSHENG
 * @version 1.0
 */
public class RenameSTARSortBam extends ConnectLinuxCon {
    @Test
    public void rename(String toolPath){
        String command = "cd "+toolPath+";" +
                "mv 03align_out/seq_dataAligned.sortedByCoord.out.bam 03align_out/sorted_output.bam";

        try {
            JSch jsch = new JSch();

            // Create SSH session
            Session session = jsch.getSession(username, host, 22);
            session.setPassword(password);

            // Disable interactive confirmation
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            System.out.println("Connecting to SSH");
            // Connect to SSH
            session.connect();

            // Create SSH channel
            Channel channel = session.openChannel("exec");
            System.out.println("Creating SSH channel");

            // Set the command to be executed
            ((ChannelExec) channel).setCommand(command);

            // Get the command's output stream
            InputStream in = channel.getInputStream();

            // Connect to the SSH channel
            channel.connect();

            // Read the command's output
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) > 0) {
                System.out.print(new String(buffer, 0, bytesRead));
            }
            // Disconnect the channel and session
            channel.disconnect();
            session.disconnect();

        } catch (JSchException | IOException e) {
            e.printStackTrace();
        }
    }
}
