package com.bjfu.springboot.rna_tool.Trimmomatic;

import com.bjfu.springboot.rna_tool.config.ConnectLinuxCon;
import com.jcraft.jsch.*;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author DONGSHENG
 * @version 1.0
 */
public class Trimmomatic extends ConnectLinuxCon {
    // Automata method to execute Trimmomatic operation for trimming paired-end reads
    public void automata(int[] arr, String toolPath, String sequence_file1, String sequence_file2) {

        // Construct the command to run Trimmomatic with the specified options
        String command = "cd " + toolPath + "/01raw_data;" +
                "trimmomatic PE -threads 16 -phred33 " + sequence_file1 + " " + sequence_file2 + "  output_pair_" + sequence_file1 + "  output_unpair_" + sequence_file1 + " output_pair_" + sequence_file2 + "  output_unpair_" + sequence_file2 + " ILLUMINACLIP:" + toolPath + "/TruSeq3-PE.fa:2:30:10:1:true SLIDINGWINDOW:4:20 LEADING:3 TRAILING:3 MINLEN:50 TOPHRED33  HEADCROP:" + arr[1] + ";";

        System.out.println("Starting Trimmomatic operation");
        System.out.println("Command: " + command);
        try {
            JSch jsch = new JSch();

            // Create an SSH session
            Session session = jsch.getSession(username, host, 22);
            session.setPassword(password);

            // Disable interactive confirmation
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);

            // Connect to SSH
            session.connect();

            // Create an SSH channel
            Channel channel = session.openChannel("exec");

            // Set the command to execute
            ((ChannelExec) channel).setCommand(command);

            // Get the command's input stream
            InputStream in = channel.getInputStream();

            // Connect to the SSH channel
            channel.connect();

            // Read the command's output
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) > 0) {
                System.out.print(new String(buffer, 0, bytesRead));
            }
            System.out.println("Ending Trimmomatic operation");

            // Disconnect the SSH channel and session
            channel.disconnect();
            session.disconnect();

        } catch (JSchException | IOException e) {
            e.printStackTrace();
        }
    }

    // Method to copy cleaned data to another directory for further processing
    public void cpCleandata(String toolPath, String sequence_file1, String sequence_file2) {
        String command = "cd " + toolPath + "/01raw_data;cp output_pair_" + sequence_file1 + " output_pair_" + sequence_file2 + " " + toolPath + "/02clean_data";
        System.out.println(command);

        System.out.println("Copying the processed data to 02clean_data for alignment");
        try {
            JSch jsch = new JSch();

            // Create an SSH session
            Session session = jsch.getSession(username, host, 22);
            session.setPassword(password);

            // Disable interactive confirmation
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            System.out.println("Connecting to SSH");

            // Connect to SSH
            session.connect();

            // Create an SSH channel
            Channel channel = session.openChannel("exec");
            System.out.println("Creating SSH channel");

            // Set the command to execute
            ((ChannelExec) channel).setCommand(command);

            // Get the command's input stream
            InputStream in = channel.getInputStream();

            // Connect to the SSH channel
            channel.connect();

            // Read the command's output
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) > 0) {
                System.out.print(new String(buffer, 0, bytesRead));
            }

            // Disconnect the SSH channel and session
            channel.disconnect();
            session.disconnect();

        } catch (JSchException | IOException e) {
            e.printStackTrace();
        }
    }

}
