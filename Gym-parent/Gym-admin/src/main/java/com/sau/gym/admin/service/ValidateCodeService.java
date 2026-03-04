package com.sau.gym.admin.service;

import com.sau.gym.model.vo.system.ValidateCodeVo;

public interface ValidateCodeService {

    //获取验证码图片
    ValidateCodeVo generateValidateCode();
}
