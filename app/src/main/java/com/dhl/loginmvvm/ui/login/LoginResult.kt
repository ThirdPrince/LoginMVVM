package com.dhl.loginmvvm.ui.login

/**
 * 登录返回结果
 */
data class LoginResult(
    val success: UserInfo? = null,
    val error: String?=null

)