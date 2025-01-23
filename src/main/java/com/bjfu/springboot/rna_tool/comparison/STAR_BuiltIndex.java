package com.bjfu.springboot.rna_tool.comparison;

import com.bjfu.springboot.rna_tool.config.ConnectLinuxCon;
import com.bjfu.springboot.rna_tool.fastqc.AnalyzeFile;
import com.jcraft.jsch.*;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author DONGSHENG
 * @version 1.0
 */
public class STAR_BuiltIndex extends ConnectLinuxCon {
    @Test
    public void SATRsetIndex(String toolPath, String seqFileName, String annotationFileName, String referenceGenome) {

        // Create an instance of AnalyzeFile to compute sequence length
        AnalyzeFile analyzeFile = new AnalyzeFile();
        // Get the length of the sequence (subtract 1 to get sequence length minus 1 for overhang)
        int seqLen = analyzeFile.SeqLength(toolPath, seqFileName) - 1;

        // Construct the STAR genome index creation command
        String command = "cd " + toolPath + ";" +
                "/usr/bin/time -v STAR --runMode genomeGenerate --runThreadN 4 --genomeDir arab_STAR_genome " +
                "--genomeFastaFiles 00ref/" + referenceGenome + " --sjdbGTFfile 00ref/" + annotationFileName +
                " --sjdbOverhang " + seqLen + " 2> SATR_index_log.txt;";

        System.out.println("Starting to build the index");
        System.out.println("Command: " + command);

        try {
            // Initialize the SSH session and connect
            JSch jsch = new JSch();
            Session session = jsch.getSession(username, host, 22);
            session.setPassword(password);

            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);

            System.out.println("Connecting to SSH");
            session.connect();

            // Open the SSH channel and execute the command
            Channel channel = session.openChannel("exec");
            System.out.println("Creating SSH channel");

            ((ChannelExec) channel).setCommand(command);
            InputStream in = channel.getInputStream();

            // Connect to the channel and read the output
            channel.connect();

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
