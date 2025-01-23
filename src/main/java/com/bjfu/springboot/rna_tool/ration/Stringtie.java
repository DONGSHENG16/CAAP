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
public class Stringtie extends ConnectLinuxCon {
    @Test
    public void stringtie_Expresion(String toolPath,String annotation_fileName){
        String command = "cd "+toolPath+";"+
                "/usr/bin/time -v stringtie 03align_out/sorted_output.bam -G 00ref/"+annotation_fileName+" -o sample2.gtf  -A  04ration/counts.txt -p 8 --rf --conservative -e -B  2> stringtie_Expresion_log.txt";
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
            System.out.println("End of quantitative");

            channel.disconnect();
            session.disconnect();

        } catch (JSchException | IOException e) {
            e.printStackTrace();
        }
    }
}
