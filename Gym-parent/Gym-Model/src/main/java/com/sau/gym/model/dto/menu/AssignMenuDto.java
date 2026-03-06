package com.sau.gym.model.dto.menu;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/3/6 20:50
 */
@Data
@Schema(description = "请求参数实体类")
public class AssignMenuDto {

    @Schema(description = "角色id")
    private Long roleId;							// 角色id

    @Schema(description = "选中的菜单id的集合")
    private List<Map<String , Number>> menuIdList;	// 选中的菜单id的集合 , Map中包含了2部分的数据：菜单id，isHalf
}
