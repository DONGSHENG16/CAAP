package com.bjfu.springboot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@TableName("sys_user")
@ApiModel(value = "User object", description = "")
@ToString
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("Username")
    private String username;

    @ApiModelProperty("Password")
    private String password;

    @ApiModelProperty("Nickname")
    private String nickname;

    @ApiModelProperty("Email")
    private String email;

    @ApiModelProperty("Phone")
    private String phone;

    @ApiModelProperty("Address")
    private String address;

    @ApiModelProperty("Creation time")
    private Date createTime;

    @ApiModelProperty("Avatar URL")
    private String avatarUrl;

    @ApiModelProperty("Role")
    private String role;

}
