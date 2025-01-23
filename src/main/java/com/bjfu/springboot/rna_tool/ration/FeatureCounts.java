package com.bjfu.springboot.rna_tool.ration;

import com.bjfu.springboot.rna_tool.config.ConnectLinuxCon;
import com.jcraft.jsch.*;
import com.sun.management.OperatingSystemMXBean;
import jline.internal.TestAccessible;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.lang.management.ManagementFactory;

/**
 * @author DONGSHENG
 * @version 1.0
 */

public class FeatureCounts extends ConnectLinuxCon {
    @Test
    public void featureCountsExpression(String toolPath,String annotation_fileName) {
        String command = "cd "+toolPath+";"+
                "/usr/bin/time -v featureCounts -T 8 -p -t exon -g gene_id -a 00ref/"+annotation_fileName+" -o 04ration/counts.txt 03align_out/sorted_output.bam 2> featureCounts_log.txt";

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
            System.out.println("Quantification complete");

            channel.disconnect();
            session.disconnect();

        } catch (JSchException | IOException e) {
            e.printStackTrace();
        }


    }
}
