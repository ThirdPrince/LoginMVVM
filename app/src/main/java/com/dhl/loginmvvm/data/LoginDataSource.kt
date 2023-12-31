package com.dhl.loginmvvm.data

import com.dhl.loginmvvm.data.model.LoginModel
import com.dhl.loginmvvm.ui.login.LoginResult
import com.dhl.loginmvvm.ui.login.UserInfo
import kotlinx.coroutines.delay
import java.io.IOException

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class LoginDataSource {

    var count = 0
    //模拟登录
   suspend fun login(username: String, password: String): Result<LoginResult> {
        return try {
            count++
            delay(100)
            if(count %2 ==0){
                val userInfo = UserInfo(username,"1233333",java.util.UUID.randomUUID().toString())
                val result = LoginResult( userInfo,"")
                Result.Success(result)
            }else{
                val result = LoginResult( null,"登录失败")
                Result.Fail(result)
            }

        } catch (e: Throwable) {
            val result = LoginResult( null,"Error logging in")
            Result.Fail(result)
        }
    }


}