package com.sau.gym.model.vo.menu;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 作者:hfj
 * 功能:封装前端所需要的菜单数据
 * 日期: 2026/3/6 21:01
 */
@Data
@Schema(description = "菜单数据实体类")
public class MenuVo {

    private String title;

    private String name;

    private List<MenuVo> children;
}
