package com.bjfu.springboot.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author DONGSHENG
 * @version 1.0
 */
@Data
@TableName("data_upload")
public class DataUpload {
    private MultipartFile[] rgFiles;
    private MultipartFile[] annFiles;
    private MultipartFile[] oriFiles;
    private String id;
    private String expname;
    private String expdescribe;
    private String rgurl1;
    private String rgurl2;
    private String rgurl3;
    private String rgname;
    private String rgtype;
    private Long rgsize;
    private Boolean rgIsDelete;
    private String annurl1;
    private String annurl2;
    private String annurl3;
    private String annname;
    private String anntype;
    private Long annsize;
    private Boolean annIsDelete;
    private String oriurl1;
    private String oriurl2;
    private String oriurl3;
    private String oriname;
    private String oritype;
    private Long orisize;
    private Boolean oriIsDelete;

}
