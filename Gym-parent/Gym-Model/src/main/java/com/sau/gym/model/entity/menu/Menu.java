package com.sau.gym.model.entity.menu;

import com.sau.gym.model.entity.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/3/6 19:10
 */
@Data
@Schema(description = "菜单实体类")
public class Menu extends BaseEntity {

    @Schema(description = "编号")
    private Long parentId;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "组件")
    private String component;

    @Schema(description = "排序")
    private Integer sortValue;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "下级列表")
    private List<Menu> children;
}
