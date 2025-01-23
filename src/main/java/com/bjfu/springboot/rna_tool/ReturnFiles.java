package com.bjfu.springboot.rna_tool;

import com.bjfu.springboot.rna_tool.config.ConnectLinuxCon;
import com.jcraft.jsch.*;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author DONGSHENG
 * @version 1.0
 */
public class ReturnFiles extends ConnectLinuxCon {
    public void moveFiles(String ProjectFolder){
        String command = "cd /RSAP_Data;" +
                "tar -czvf "+ProjectFolder+".tar.gz /RSAP_Data/"+ProjectFolder+";";
        System.out.println("Start returning files");
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
            // Set command
            ((ChannelExec) channel).setCommand(command);

            // Get the output stream of the command
            InputStream in = channel.getInputStream();

            // Connect to the SSH channel
            channel.connect();

            // Read the command output
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) > 0) {
                System.out.print(new String(buffer, 0, bytesRead));
            }
            System.out.println("Index creation completed");
            // Disconnect the channel
            channel.disconnect();
            session.disconnect();

        } catch (JSchException | IOException e) {
            e.printStackTrace();
        }
    }
}
