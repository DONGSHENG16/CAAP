package com.bjfu.springboot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("project_info")
public class ProjectInfo {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private String projectName;
    private String gcContent;
    private String alignmentRate;
    private String alignmentTool;
    private String quantificationTool;
    private String sequenceFile1;
    private String sequenceFile2;
    private String annotationFile;
    private String referenceGenome;
    @TableField(value = "create_time")
    private LocalDateTime createTime;
}
