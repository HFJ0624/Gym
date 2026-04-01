package com.sau.gym.model.vo.user;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/4/1 20:11
 */
@Data
public class UserExcelVO {

    @ExcelProperty(value = "id" ,index = 0)
    private Long id;

    @ExcelProperty(value = "用户名称" ,index = 1)
    private String username;

    @ExcelProperty(value = "真实姓名" ,index = 2)
    private String realName;

    @ExcelProperty(value = "性别" ,index = 3)
    private String sex;

    @ExcelProperty(value = "电话" ,index = 4)
    private String phone;

    @ExcelProperty(value = "邮件" ,index = 5)
    private String email;

    @ExcelProperty(value = "头像" ,index = 6)
    private String avatar;

    @ExcelProperty(value = "状态" ,index = 7)
    private Integer status;
}
