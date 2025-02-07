package com.bjfu.springboot.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bjfu.springboot.controller.dto.UserDTO;
import com.bjfu.springboot.controller.dto.UserPasswordDTO;
import com.bjfu.springboot.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 */
public interface IUserService extends IService<User> {

    UserDTO login(UserDTO userDTO);

    User register(UserDTO userDTO);

    void updatePassword(UserPasswordDTO userPasswordDTO);

    Page<User> findPage(Page<User> objectPage, String username, String email, String address);
}
