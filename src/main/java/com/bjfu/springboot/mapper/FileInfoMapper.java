package com.bjfu.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.bjfu.springboot.entity.FileInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FileInfoMapper extends BaseMapper<FileInfo> {

}
