package com.dhl.loginmvvm.data

import com.dhl.loginmvvm.data.model.LoginModel
import com.dhl.loginmvvm.ui.login.LoginResult

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */

class LoginRepository(val dataSource: LoginDataSource) {

    val user: LoginModel by lazy {
        LoginModel("","")
    }


    suspend fun login(username: String, password: String): Result<LoginResult> {
        return dataSource.login(username, password)
    }


}