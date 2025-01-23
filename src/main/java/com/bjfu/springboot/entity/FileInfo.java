package com.bjfu.springboot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("file_info")
public class FileInfo {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private String fileName;
    private String filePath;
}

