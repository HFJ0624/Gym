package com.sau.gym.admin.mapper;

import com.sau.gym.model.entity.system.SignIn;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SignInMapper {

    // 生成签到记录（插入token和姓名）
    int insertSignIn(SignIn signIn);

    // 根据token查询签到记录
    SignIn selectByToken(@Param("token") String token);

    // 更新签到状态（已到场+签到时间）
    int updateStatusByToken(@Param("token") String token);
}
