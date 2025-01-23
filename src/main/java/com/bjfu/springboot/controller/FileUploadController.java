package com.bjfu.springboot.controller;

import com.bjfu.springboot.entity.FileInfo;
import com.bjfu.springboot.service.FileInfoService;
import com.jcraft.jsch.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@RestController
public class FileUploadController {

    @Autowired
    private FileInfoService fileInfoService;

    // Configure your remote server
    private static final String SFTP_HOST = "XXX";
    private static final int SFTP_PORT = 22;
    private static final String SFTP_USER = "XXX";
    private static final String SFTP_PASSWORD = "XXXX";
    private static final String REMOTE_DIR = "/home/RSAP_Data/resource/";

    // Upload a file in chunks
    @PostMapping("/upload")
    public Map<String, String> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("fileName") String fileName,
            @RequestParam("chunkIndex") int chunkIndex,
            @RequestParam("totalChunks") int totalChunks) {

        Map<String, String> response = new HashMap<>();
        String tempDirPath = System.getProperty("java.io.tmpdir") + "/uploads/temp/" + fileName;
        File tempDir = new File(tempDirPath);
        if (!tempDir.exists()) {
            tempDir.mkdirs();
        }

        try {
            File tempFile = new File(tempDir, fileName + "." + chunkIndex);
            file.transferTo(tempFile);

            if (chunkIndex == totalChunks - 1) {
                // All chunks uploaded, proceed with merging
                File finalFile = new File(tempDirPath, fileName);
                try (FileOutputStream fos = new FileOutputStream(finalFile)) {
                    for (int i = 0; i < totalChunks; i++) {
                        File partFile = new File(tempDir, fileName + "." + i);
                        Files.copy(partFile.toPath(), fos);
                        partFile.delete();
                    }
                }

                // Upload to remote server
                Session session = null;
                ChannelSftp channelSftp = null;

                try {
                    JSch jsch = new JSch();
                    session = jsch.getSession(SFTP_USER, SFTP_HOST, SFTP_PORT);
                    session.setPassword(SFTP_PASSWORD);

                    Properties config = new Properties();
                    config.put("StrictHostKeyChecking", "no");
                    session.setConfig(config);
                    session.connect();

                    channelSftp = (ChannelSftp) session.openChannel("sftp");
                    channelSftp.connect();
                    channelSftp.cd(REMOTE_DIR);
                    channelSftp.put(finalFile.getAbsolutePath(), REMOTE_DIR + fileName);

                    String fileUrl = "http://" + SFTP_HOST + "/resourse/" + fileName;
                    fileInfoService.saveFileInfo(fileName, fileUrl);

                    response.put("message", "File uploaded successfully");
                    response.put("fileUrl", fileUrl);
                } finally {
                    if (channelSftp != null) {
                        channelSftp.disconnect();
                    }
                    if (session != null) {
                        session.disconnect();
                    }
                }

            } else {
                response.put("message", "Chunk uploaded successfully");
            }
        } catch (IOException | SftpException e) {
            e.printStackTrace();
            response.put("message", "File upload failed: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            response.put("message", "File upload failed: " + e.getMessage());
        }

        return response;
    }

    // Retrieve file list
    @GetMapping("/files")
    public List<FileInfo> getFileList() {
        return fileInfoService.getAllFiles();
    }

    // Delete a file
    @DeleteMapping("/files/{id}")
    public Map<String, String> deleteFile(@PathVariable Long id) {
        Map<String, String> response = new HashMap<>();
        FileInfo fileInfo = fileInfoService.getById(id);
        if (fileInfo != null) {
            // Delete file from remote server
            Session session = null;
            ChannelSftp channelSftp = null;
            try {
                JSch jsch = new JSch();
                session = jsch.getSession(SFTP_USER, SFTP_HOST, SFTP_PORT);
                session.setPassword(SFTP_PASSWORD);

                Properties config = new Properties();
                config.put("StrictHostKeyChecking", "no");
                session.setConfig(config);
                session.connect();

                channelSftp = (ChannelSftp) session.openChannel("sftp");
                channelSftp.connect();
                channelSftp.cd(REMOTE_DIR);
                channelSftp.rm(fileInfo.getFileName());

                fileInfoService.removeById(id);
                response.put("message", "File deleted successfully");
            } catch (Exception e) {
                e.printStackTrace();
                response.put("message", "File deletion failed: " + e.getMessage());
            } finally {
                if (channelSftp != null) {
                    channelSftp.disconnect();
                }
                if (session != null) {
                    session.disconnect();
                }
            }
        } else {
            response.put("message", "File not found");
        }
        return response;
    }

    // Search for files
    @GetMapping("/files/search")
    public List<FileInfo> searchFiles(@RequestParam String query) {
        return fileInfoService.searchFiles(query);
    }

    // Download a file
    @GetMapping("/files/download/{id}")
    public ResponseEntity<InputStreamResource> downloadFile(@PathVariable Long id) {
        FileInfo fileInfo = fileInfoService.getById(id);
        if (fileInfo == null) {
            return ResponseEntity.notFound().build();
        }

        Session session = null;
        ChannelSftp channelSftp = null;
        InputStream inputStream = null;

        try {
            JSch jsch = new JSch();
            session = jsch.getSession(SFTP_USER, SFTP_HOST, SFTP_PORT);
            session.setPassword(SFTP_PASSWORD);

            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.connect();

            channelSftp = (ChannelSftp) session.openChannel("sftp");
            channelSftp.connect();
            channelSftp.cd(REMOTE_DIR);

            inputStream = channelSftp.get(fileInfo.getFileName());

            InputStreamResource resource = new InputStreamResource(inputStream);

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + fileInfo.getFileName())
                    .body(resource);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        } finally {
            try {
                if (channelSftp != null) {
                    channelSftp.disconnect();
                }
                if (session != null) {
                    session.disconnect();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
