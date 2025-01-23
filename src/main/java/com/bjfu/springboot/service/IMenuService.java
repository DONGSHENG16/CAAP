package com.bjfu.springboot.service;

import com.bjfu.springboot.entity.Menu;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 */
public interface IMenuService extends IService<Menu> {

    List<Menu> findMenus(String name);
}
