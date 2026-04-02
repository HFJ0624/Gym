import request from '@/utils/frontRequest'

// 前台登录接口
export const FrontLogin = data => {
  return request({
    url: '/front/login',
    method: 'post',
    data,
  })
}

// 获取验证码（如果你后端前台也做了 captcha）
export const GetFrontValidateCode = () => {
  return request({
    url: '/front/generateValidateCode',
    method: 'get',
  })
}

//获取前台用户信息
export const GetFrontUserInfo = () => {
  return request({
    url: '/front/getUserInfo',
    method: 'get',
  })
}

//注册用户
export const Register = (data) => {
  return request({
    url: `/front/register`,
    method: "post",
    data
  })
}

