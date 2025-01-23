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
public class Minimap2_ExecuteComparison extends ConnectLinuxCon {
    @Test
    public void Minimap2comparison(String toolPath, String sequence_file1, String sequence_file2){

        String command = "cd "+toolPath+";" +
                "/usr/bin/time -v minimap2 -ax sr genInd/genome.mmi  02clean_data/output_pair_"+sequence_file1+" 02clean_data/output_pair_"+sequence_file2+" > 03align_out/output.sam 2> minimap2_EC_log.txt";

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
