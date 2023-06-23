package com.dhl.loginmvvm.ui.login

import android.text.Editable
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dhl.loginmvvm.data.LoginRepository
import com.dhl.loginmvvm.data.Result

import com.dhl.loginmvvm.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * LoginViewModel
 * @author dhl
 */
class LoginViewModel( val loginRepository: LoginRepository) : ViewModel() {

    private val _loginState = MutableLiveData<LoginState>()
    val loginState: LiveData<LoginState> = _loginState

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    fun login(username: String, password: String) {
        // can be launched in a separate asynchronous job
        viewModelScope.launch {
            val result = loginRepository.login(username, password)

            delay(1000)
            if (result is Result.Success) {
                _loginResult.value = LoginResult(success = UserInfo(displayName = result.data.email, userId =result.data.password ))
                _loginState.value = LoginState(loginState = LoginState.LOGIN_SUCCESS)
            } else {
                _loginResult.value = LoginResult(error = R.string.login_failed)
                _loginState.value = LoginState(loginState = LoginState.LOGIN_FAIL)
            }

        }

    }


    fun onUserTextChanged(text: Editable) {
        loginRepository.user.email = text.toString()
        loginIsValid()
    }


    fun onPasswordTextChanged(text: Editable) {
        loginRepository.user.password = text.toString()
        loginIsValid()
    }

     fun loginIsValid(){
        if(loginRepository.user.isValid()){
            if(_loginState.value?.loginState != LoginState.LOGIN_VALID){
                _loginState.value = LoginState(loginState = LoginState.LOGIN_VALID)
            }

        }else{
            if(_loginState.value?.loginState != LoginState.NOT_LOGIN){
                _loginState.value = LoginState(loginState = LoginState.NOT_LOGIN)
            }
        }
    }


    fun loginOnClick() {
        Log.e("test", loginRepository.user.password)
        _loginState.value = LoginState(loginState = LoginState.LOGIN_ING)
        login(loginRepository.user.email, loginRepository.user.password)
    }


}