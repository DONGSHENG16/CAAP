package com.bjfu.springboot.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bjfu.springboot.common.Constants;
import com.bjfu.springboot.common.Result;
import com.bjfu.springboot.controller.dto.UserDTO;
import com.bjfu.springboot.controller.dto.UserPasswordDTO;
import com.bjfu.springboot.entity.User;
import com.bjfu.springboot.service.IUserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.List;

/**
 * <p>
 * Frontend Controller
 * </p>
 *
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Value("${files.upload.path}")
    private String filesUploadPath;

    @Resource
    private IUserService userService;

    @PostMapping("/login")
    public Result login(@RequestBody UserDTO userDTO) {
        String username = userDTO.getUsername();
        String password = userDTO.getPassword();
        if (StrUtil.isBlank(username) || StrUtil.isBlank(password)) {
            return Result.error(Constants.CODE_400, "Invalid parameters");
        }
        UserDTO dto = userService.login(userDTO);
        return Result.success(dto);
    }

    @PostMapping("/register")
    public Result register(@RequestBody UserDTO userDTO) {
        String username = userDTO.getUsername();
        String password = userDTO.getPassword();
        if (StrUtil.isBlank(username) || StrUtil.isBlank(password)) {
            return Result.error(Constants.CODE_400, "Invalid parameters");
        }
        return Result.success(userService.register(userDTO));
    }

    // Add or update
    @PostMapping
    public Result save(@RequestBody User user) {
        if (user.getId() == null && user.getPassword() == null) {  // Default password for new users
            user.setPassword(SecureUtil.md5("123"));
        }
        return Result.success(userService.saveOrUpdate(user));
    }

    /**
     * Change password
     * @param userPasswordDTO
     * @return
     */
    @PostMapping("/password")
    public Result password(@RequestBody UserPasswordDTO userPasswordDTO) {
        userPasswordDTO.setPassword(SecureUtil.md5(userPasswordDTO.getPassword()));
        userPasswordDTO.setNewPassword(SecureUtil.md5(userPasswordDTO.getNewPassword()));
        userService.updatePassword(userPasswordDTO);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        return Result.success(userService.removeById(id));
    }

    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        return Result.success(userService.removeByIds(ids));
    }

    @GetMapping
    public Result findAll() {
        return Result.success(userService.list());
    }

    @GetMapping("/role/{role}")
    public Result findUsersByRole(@PathVariable String role) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("role", role);
        List<User> list = userService.list(queryWrapper);
        return Result.success(list);
    }

    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id) {
        return Result.success(userService.getById(id));
    }

    @GetMapping("/username/{username}")
    public Result findByUsername(@PathVariable String username) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        return Result.success(userService.getOne(queryWrapper));
    }

    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize,
                           @RequestParam(defaultValue = "") String username,
                           @RequestParam(defaultValue = "") String email,
                           @RequestParam(defaultValue = "") String address) {

        return Result.success(userService.findPage(new Page<>(pageNum, pageSize), username, email, address));
    }

    /**
     * Export interface
     */
    @GetMapping("/export")
    public void export(HttpServletResponse response) throws Exception {
        // Query all data from the database
        List<User> list = userService.list();
        // Create writer using the utility class and write to disk path
//        ExcelWriter writer = ExcelUtil.getWriter(filesUploadPath + "/user_info.xlsx");
        // Operate in memory and write to browser
        ExcelWriter writer = ExcelUtil.getWriter(true);
        // Customize header aliases
        writer.addHeaderAlias("username", "Username");
        writer.addHeaderAlias("password", "Password");
        writer.addHeaderAlias("nickname", "Nickname");
        writer.addHeaderAlias("email", "Email");
        writer.addHeaderAlias("phone", "Phone");
        writer.addHeaderAlias("address", "Address");
        writer.addHeaderAlias("createTime", "Creation Time");
        writer.addHeaderAlias("avatarUrl", "Avatar");

        // Write the list of objects to Excel at once, use default style, force output header
        writer.write(list, true);

        // Set browser response format
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
        String fileName = URLEncoder.encode("User Information", "UTF-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");

        ServletOutputStream out = response.getOutputStream();
        writer.flush(out, true);
        out.close();
        writer.close();

    }

    /**
     * Excel import
     * @param file
     * @throws Exception
     */
    @PostMapping("/import")
    public Result imp(MultipartFile file) throws Exception {
        InputStream inputStream = file.getInputStream();
        ExcelReader reader = ExcelUtil.getReader(inputStream);
        // Method 1: (Recommended) Read Excel objects through JavaBeans, requires header to be in English and match JavaBean properties
//        List<User> list = reader.readAll(User.class);

        // Method 2: Ignore Chinese headers, directly read the table contents
        List<List<Object>> list = reader.read(1);
        List<User> users = CollUtil.newArrayList();
        for (List<Object> row : list) {
            User user = new User();
            user.setUsername(row.get(0).toString());
            user.setPassword(row.get(1).toString());
            user.setNickname(row.get(2).toString());
            user.setEmail(row.get(3).toString());
            // Add more mappings as needed
        }
        return Result.success();
    }
}
