package com.dhl.loginmvvm.data.model

import android.text.TextUtils

/**
 *l loginModel
 * @author dhl
 *
 */
data class LoginModel(
    var email: String,
    var password: String
){
    /**
     * 是否合规
     * 不为空而且密码>= 6
     */
    fun isValid():Boolean{
        return !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && password.length >=6
    }
}