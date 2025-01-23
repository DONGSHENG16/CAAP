package com.bjfu.springboot.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author DONGSHENG
 * @version 1.0
 */
@Data
@TableName("tra_qc_data")
public class TraQcData {
    private String qcId;
    private String id;
    private String qcUrl1;
    private String qcUrl2;
    private String qcUrl3;
    private MultipartFile[] qcFiles;
    private String toolOpt;
}
