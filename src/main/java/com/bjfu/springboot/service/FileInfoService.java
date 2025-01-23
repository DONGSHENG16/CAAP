package com.bjfu.springboot.service;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bjfu.springboot.entity.FileInfo;
import com.bjfu.springboot.mapper.FileInfoMapper;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;


@Service
public class FileInfoService extends ServiceImpl<FileInfoMapper, FileInfo> {

    public void saveFileInfo(String fileName, String filePath) {
        FileInfo fileInfo = new FileInfo();
        fileInfo.setFileName(fileName);
        fileInfo.setFilePath(filePath);
        this.save(fileInfo);
    }
    //查询
    public List<FileInfo> getAllFiles() {
        return this.list();
    }
    //搜索
    public List<FileInfo> searchFiles(String query) {
        QueryWrapper<FileInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("file_name", query);
        return this.list(queryWrapper);
    }
}