import request from '@/utils/frontRequest'

// 1. 管理员生成签到二维码
export function generateSignQR(params) {
    return request({
        url: '/front/sign-in/generate-qrcode',
        method: 'post',
        params
    })
}

// 2. 用户执行签到（扫码后调用）
export function doSign(token) {
    return request({
        url: '/front/sign-in/do-sign',
        method: 'get',
        params: { token }
    })
}