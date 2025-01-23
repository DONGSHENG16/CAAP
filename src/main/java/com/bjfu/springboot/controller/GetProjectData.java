package com.bjfu.springboot.controller;


import com.bjfu.springboot.entity.ProjectInfo;
import com.bjfu.springboot.rna_tool.StartAutomation;
import com.bjfu.springboot.service.ProjectInfoService;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import org.apache.catalina.connector.ClientAbortException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.util.Properties;

import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
@RequestMapping("/project-data")
public class GetProjectData {

    @Autowired
    private ProjectInfoService projectInfoService;
    private static final Logger LOGGER = Logger.getLogger(GetProjectData.class.getName());

    private static final String SFTP_HOST = "XXXX";
    private static final int SFTP_PORT = 22;
    private static final String SFTP_USER = "root";
    private static final String SFTP_PASSWORD = "XXXX";
    private static final String REMOTE_DIR = "/RSAP_Data/";

    private Long projectId;
    private String projectName;
    private String gcContent;
    private String alignmentRate;
    private String alignmentTool;
    private String quantificationTool;
    private String sequenceFile1;
    private String sequenceFile2;
    private String annotationFile;
    private String referenceGenome;

    @GetMapping("/{id}")
    public ProjectInfo getProjectById(@PathVariable Long id) {
        return projectInfoService.getById(id);
    }

    @PostMapping("/execute")
    public String executeProject(@RequestParam Long id) {
        ProjectInfo project = projectInfoService.getById(id);
        if (project != null) {
            this.projectId = project.getId();

            this.projectName = project.getProjectName();

            this.gcContent = project.getGcContent();
            this.alignmentRate = project.getAlignmentRate();
            this.alignmentTool = project.getAlignmentTool();
            this.quantificationTool = project.getQuantificationTool();
            this.sequenceFile1 = project.getSequenceFile1();
            this.sequenceFile2 = project.getSequenceFile2();
            this.annotationFile = project.getAnnotationFile();
            this.referenceGenome = project.getReferenceGenome();
            //执行RNA自动化处理项目
            StartAutomation te = new StartAutomation();
            te.runTool(this.gcContent,this.alignmentRate,this.alignmentTool,this.quantificationTool,this.sequenceFile1,this.sequenceFile2,this.annotationFile,this.referenceGenome);
            return "项目执行成功";
        } else {
            return "项目未找到";
        }
    }

    //修改项目信息
    @PutMapping("/{id}")
    public ProjectInfo updateProject(@PathVariable Long id, @RequestBody ProjectInfo updatedProject) {
        ProjectInfo project = projectInfoService.getById(id);
        if (project != null) {
            project.setProjectName(updatedProject.getProjectName());
            project.setGcContent(updatedProject.getGcContent());
            project.setAlignmentRate(updatedProject.getAlignmentRate());
            project.setAlignmentTool(updatedProject.getAlignmentTool());
            project.setQuantificationTool(updatedProject.getQuantificationTool());
            project.setSequenceFile1(updatedProject.getSequenceFile1());
            project.setSequenceFile2(updatedProject.getSequenceFile2());
            project.setAnnotationFile(updatedProject.getAnnotationFile());
            project.setReferenceGenome(updatedProject.getReferenceGenome());
            projectInfoService.updateById(project);
            return project;
        } else {
            return null;
        }
    }

    //下载项目分析文件
    @GetMapping("/download/{id}")
    public ResponseEntity<StreamingResponseBody> downloadProjectResults(@PathVariable Long id) {
        ProjectInfo project = projectInfoService.getById(id);
        if (project == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"archive_name.zip\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(outputStream -> {
                    Session session = null;
                    ChannelSftp channelSftp = null;
                    BufferedInputStream inputStream = null;
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

                        String remoteFilePath = REMOTE_DIR + "archive_name.zip";
                        inputStream = new BufferedInputStream(channelSftp.get(remoteFilePath));

                        byte[] buffer = new byte[1024 * 16]; // Increase buffer size to 16KB
                        int bytesRead;
                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, bytesRead);
                            outputStream.flush();
                        }
                    } catch (ClientAbortException e) {
                        LOGGER.log(Level.WARNING, "Client aborted the download", e);
                    } catch (Exception e) {
                        LOGGER.log(Level.SEVERE, "Error during file download", e);
                    } finally {
                        try {
                            if (inputStream != null) {
                                inputStream.close();
                            }
                            if (channelSftp != null) {
                                channelSftp.disconnect();
                            }
                            if (session != null) {
                                session.disconnect();
                            }
                        } catch (Exception e) {
                            LOGGER.log(Level.SEVERE, "Error closing resources", e);
                        }
                    }
                });
    }

    // 获取存储的项目参数
    public Long getProjectId() {
        return projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getGcContent() {
        return gcContent;
    }

    public String getAlignmentRate() {
        return alignmentRate;
    }

    public String getAlignmentTool() {
        return alignmentTool;
    }

    public String getQuantificationTool() {
        return quantificationTool;
    }

    public String getSequenceFile1() {
        return sequenceFile1;
    }

    public String getSequenceFile2() {
        return sequenceFile2;
    }

    public String getAnnotationFile() {
        return annotationFile;
    }

    public String getReferenceGenome() {
        return referenceGenome;
    }

}
