package com.bjfu.springboot.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.log.Log;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bjfu.springboot.common.Constants;
import com.bjfu.springboot.common.RoleEnum;
import com.bjfu.springboot.controller.dto.UserDTO;
import com.bjfu.springboot.controller.dto.UserPasswordDTO;
import com.bjfu.springboot.entity.Menu;
import com.bjfu.springboot.entity.User;
import com.bjfu.springboot.exception.ServiceException;
import com.bjfu.springboot.mapper.RoleMapper;
import com.bjfu.springboot.mapper.RoleMenuMapper;
import com.bjfu.springboot.mapper.UserMapper;
import com.bjfu.springboot.service.IMenuService;
import com.bjfu.springboot.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bjfu.springboot.utils.TokenUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    private static final Log LOG = Log.get();

    @Resource
    private UserMapper userMapper;

    @Resource
    private RoleMapper roleMapper;

    @Resource
    private RoleMenuMapper roleMenuMapper;

    @Resource
    private IMenuService menuService;

    @Override
    public UserDTO login(UserDTO userDTO) {

        userDTO.setPassword(SecureUtil.md5(userDTO.getPassword()));
        User one = getUserInfo(userDTO);
        if (one != null) {
            BeanUtil.copyProperties(one, userDTO, true);

            String token = TokenUtils.genToken(one.getId().toString(), one.getPassword());
            userDTO.setToken(token);

            String role = one.getRole(); // ROLE_ADMIN

            List<Menu> roleMenus = getRoleMenus(role);
            userDTO.setMenus(roleMenus);
            return userDTO;
        } else {
            throw new ServiceException(Constants.CODE_600, "用户名或密码错误");
        }
    }

    @Override
    public User register(UserDTO userDTO) {

        userDTO.setPassword(SecureUtil.md5(userDTO.getPassword()));
        User one = getUserInfo(userDTO);
        if (one == null) {
            one = new User();
            BeanUtil.copyProperties(userDTO, one, true);
            one.setRole(RoleEnum.ROLE_STUDENT.toString());
            if (one.getNickname() == null) {
                one.setNickname(one.getUsername());
            }
            save(one);
        } else {
            throw new ServiceException(Constants.CODE_600, "用户已存在");
        }
        return one;
    }

    @Override
    public void updatePassword(UserPasswordDTO userPasswordDTO) {
        int update = userMapper.updatePassword(userPasswordDTO);
        if (update < 1) {
            throw new ServiceException(Constants.CODE_600, "passwordERR");
        }
    }

    @Override
    public Page<User> findPage(Page<User> page, String username, String email, String address) {
        return userMapper.findPage(page, username, email, address);
    }

    private User getUserInfo(UserDTO userDTO) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", userDTO.getUsername());
        queryWrapper.eq("password", userDTO.getPassword());
        User one;
        try {
            one = getOne(queryWrapper);
        } catch (Exception e) {
            LOG.error(e);
            throw new ServiceException(Constants.CODE_500, "SystemERR");
        }
        return one;
    }

    private List<Menu> getRoleMenus(String roleFlag) {
        Integer roleId = roleMapper.selectByFlag(roleFlag);

        List<Integer> menuIds = roleMenuMapper.selectByRoleId(roleId);

        List<Menu> menus = menuService.findMenus("");

        List<Menu> roleMenus = new ArrayList<>();

        for (Menu menu : menus) {
            if (menuIds.contains(menu.getId())) {
                roleMenus.add(menu);
            }
            List<Menu> children = menu.getChildren();

            children.removeIf(child -> !menuIds.contains(child.getId()));
        }
        return roleMenus;
    }

}
