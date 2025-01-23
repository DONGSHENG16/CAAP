package com.bjfu.springboot.rna_tool.comparison;

import com.bjfu.springboot.rna_tool.config.ConnectLinuxCon;
import com.bjfu.springboot.rna_tool.fastqc.AnalyzeFile;
import com.jcraft.jsch.*;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author DONGSHENG
 * @version 1.0
 */
public class HISAT2_ExecuteComparison extends ConnectLinuxCon {
    public void HISAT2comparison(String toolPath, String sequence_file1, String sequence_file2){

        String command = "cd "+toolPath+";" +
                "/usr/bin/time -v hisat2 -p 8 -x genInd/genome_index -1 02clean_data/output_pair_"+sequence_file1+" -2 02clean_data/output_pair_"+sequence_file2+" -S 03align_out/output.sam 2> HISAT2_EC_log.txt";
        System.out.println("hisat2, starting alignment");
        System.out.println("Command value: " + command);
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
            System.out.println("Alignment completed");

            // Disconnect the channel and session
            channel.disconnect();
            session.disconnect();

        } catch (JSchException | IOException e) {
            e.printStackTrace();
        }
    }
}
