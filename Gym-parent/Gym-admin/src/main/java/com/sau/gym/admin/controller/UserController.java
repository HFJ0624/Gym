package com.sau.gym.admin.controller;

import com.sau.gym.admin.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/3/5 16:49
 */
@RestController
@RequestMapping(value = "/admin/system/sysUser")
public class UserController {

    @Autowired
    private UserService userService;
}
