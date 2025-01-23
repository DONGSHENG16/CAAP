package com.bjfu.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bjfu.springboot.entity.DataUpload;
import com.bjfu.springboot.entity.Files;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author DONGSHENG
 * @version 1.0
 */
@Mapper
public interface DataUploadMapper extends BaseMapper<DataUpload> {

    @Select("SELECT id FROM data_upload")
    List<DataUpload> findAll();
}
