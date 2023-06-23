package com.dhl.loginmvvm.ui.login

/**
 *LoginState
未登录状态： 0 这是初始状态，用户尚未进行登录操作或登录凭据已过期。在这种状态下，用户只能访问应用的有限功能或者需要登录才能访问的功能将被限制。

登录中状态： 1 当用户点击登录按钮后，应用会进入登录中状态，此时可能显示一个加载动画或进度条来指示登录过程正在进行中。在这个状态下，用户需要等待登录过程完成。

登录成功状态 ：2 如果用户提供的登录凭据验证通过，应用将进入登录成功状态。在这个状态下，用户可以访问登录后的功能，并且应用通常会跳转到主页面或其他授权访问的页面。

登录失败状态：3 如果用户提供的登录凭据验证失败，应用将进入登录失败状态。在这个状态下，应用可能会显示错误消息或提供其他方式来帮助用户解决登录问题。

 */
data class LoginState(
    val loginState: Int = NOT_LOGIN
){
    companion object{

        const val NOT_LOGIN = 0
        const val LOGIN_VALID = 1
        const val LOGIN_ING = 2
        const val LOGIN_SUCCESS = 3
        const val LOGIN_FAIL = 4
    }
}