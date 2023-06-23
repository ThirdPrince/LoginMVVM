package com.dhl.loginmvvm.data

import com.dhl.loginmvvm.data.model.LoginModel
import kotlinx.coroutines.delay
import java.io.IOException

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class LoginDataSource {

   suspend fun login(username: String, password: String): Result<LoginModel> {
        try {
            // TODO: handle loggedInUser authentication
                delay(100)
            val fakeUser = LoginModel(java.util.UUID.randomUUID().toString(), "Jane Doe")
            return Result.Success(fakeUser)
        } catch (e: Throwable) {
            return Result.Error(IOException("Error logging in", e))
        }
    }

    fun logout() {
        // TODO: revoke authentication
    }
}