package com.sau.gym.admin.service;

import com.sau.gym.model.entity.system.SignIn;

public interface SignInService {
    
    
    SignIn generateSignInQRCode(String name, String phone);

    String signIn(String token);

    SignIn getSignInByToken(String token);
}
