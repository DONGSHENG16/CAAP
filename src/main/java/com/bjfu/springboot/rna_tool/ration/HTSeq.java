package com.bjfu.springboot.rna_tool.ration;

import com.bjfu.springboot.rna_tool.config.ConnectLinuxCon;
import com.jcraft.jsch.*;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author DONGSHENG
 * @version 1.0
 */
public class HTSeq extends ConnectLinuxCon {
    @Test
    public void HTSeqQuantifyExpression(String toolPath,String annotation_fileName) {
        String command = "cd "+toolPath+";"+
                "/usr/bin/time -v htseq-count -f bam -r pos -s no -i gene_id 03align_out/sorted_output.bam 00ref/"+annotation_fileName+" > 04ration/gene_counts.txt 2> HTSeq_log.txt";

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
