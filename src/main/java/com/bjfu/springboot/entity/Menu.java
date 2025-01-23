package com.bjfu.springboot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TableName("sys_menu")
@ApiModel(value = "Menu object", description = "")
public class Menu implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("Name")
    private String name;

    @ApiModelProperty("Path")
    private String path;

    @ApiModelProperty("Icon")
    private String icon;

    @ApiModelProperty("Description")
    private String description;

    @TableField(exist = false)
    private List<Menu> children;

    private Integer pid;

    private String pagePath;
    private String sortNum;

}
