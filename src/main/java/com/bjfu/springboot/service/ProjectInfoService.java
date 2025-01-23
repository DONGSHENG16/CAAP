package com.bjfu.springboot.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.bjfu.springboot.entity.ProjectInfo;
import com.bjfu.springboot.mapper.ProjectInfoMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ProjectInfoService extends ServiceImpl<ProjectInfoMapper, ProjectInfo> {
    @Override
    public boolean save(ProjectInfo entity) {
        entity.setCreateTime(LocalDateTime.now());
        return super.save(entity);
    }
}
