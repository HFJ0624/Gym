package com.sau.gym.admin.mapper;

import com.sau.gym.model.dto.system.SignInDto;
import com.sau.gym.model.entity.system.SignIn;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
public interface SignInMapper {

    // 生成签到记录（插入token和姓名）
    int insertSignIn(SignIn signIn);

    // 根据token查询签到记录
    SignIn selectByToken(@Param("token") String token);

    // 更新签到状态（已到场+签到时间）
    int updateStatusByToken(@Param("token") String token,@Param("date") Date date);

    //分页查看所有签到记录
    List<SignIn> findByPage(SignInDto signInDto);
}
