package com.bjfu.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.bjfu.springboot.entity.ProjectInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProjectInfoMapper extends BaseMapper<ProjectInfo> {
}
