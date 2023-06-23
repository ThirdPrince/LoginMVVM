package com.dhl.loginmvvm.data

import com.dhl.loginmvvm.data.model.LoginModel

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */

class LoginRepository(val dataSource: LoginDataSource) {

    // in-memory cache of the loggedInUser object
    val user: LoginModel by lazy {
        LoginModel("","")
    }





    suspend fun login(username: String, password: String): Result<LoginModel> {
        // handle login
        val result = dataSource.login(username, password)

        if (result is Result.Success) {
           // setLoggedInUser(result.data)
        }

        return result
    }


}