package com.dhl.loginmvvm.ui.login

import android.text.Editable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dhl.loginmvvm.data.LoginRepository
import com.dhl.loginmvvm.data.Result

import com.dhl.loginmvvm.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * LoginViewModel
 * @author dhl
    在 LiveData 中，postValue() 和 setValue() 是用于更新 LiveData 数据的两种方法。它们在本质上有以下区别：

   线程安全性：postValue() 方法是线程安全的，可以在任何线程中调用。它会将数据更新操作投递到主线程的消息队列中，在主线程空闲时进行实际的数据更新操作。而 setValue() 方法必须在主线程中调用，否则会抛出异常。

   数据更新时机：postValue() 方法会延迟执行数据更新操作，直到主线程空闲时才会进行实际的更新。这样可以避免在短时间内连续进行多次数据更新导致的频繁界面刷新。而 setValue() 方法会立即执行数据更新操作，并触发相应的观察者通知。

   多次更新合并：postValue() 方法可以处理多次数据更新，并将它们合并成一次更新。如果在多次 postValue() 调用之间存在较短的时间间隔，只会触发一次数据更新和观察者通知。而 setValue() 方法每次调用都会立即触发数据更新和通知。

  综上所述，postValue() 方法适合在后台线程中进行数据更新操作，可以避免线程安全问题，并合并多次更新以提高性能。而 setValue() 方法应在主线程中使用，用于需要立即更新数据并通知观察者的场景。
 */
class LoginViewModel(val loginRepository: LoginRepository) : ViewModel() {

    private val _loginStateLiveData = MutableLiveData<LoginState>()
    val loginStateLivedata: LiveData<LoginState> = _loginStateLiveData

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    /**
     * 默认没有登录
     */
    private val loginState = LoginState(loginState = LoginState.NOT_LOGIN)

    /**
     * login
     */
    fun login(username: String, password: String) {

        viewModelScope.launch {
            val result = loginRepository.login(username, password)

            delay(1000)
            if (result is Result.Success) {
                _loginResult.value = result.data
                _loginStateLiveData.value = LoginState(loginState = LoginState.LOGIN_SUCCESS)
            } else if(result is Result.Fail){
                _loginResult.value = result.data
                _loginStateLiveData.value = LoginState(loginState = LoginState.LOGIN_FAIL)
            }


        }

    }


    fun onUserTextChanged(text: Editable) {
        loginRepository.user.userId = text.toString()
        loginIsValid()
    }


    fun onPasswordTextChanged(text: Editable) {
        loginRepository.user.password = text.toString()
        loginIsValid()
    }

    /**
     * 判断 账号密码是否有效
     * 这里应该用postValue 而不是setVlaue
     *
     */
    fun loginIsValid() {

        viewModelScope.launch {
            withContext(Dispatchers.Default){
                if (loginRepository.user.isValid()) {
                    loginState.loginState = LoginState.LOGIN_VALID
                } else {
                    loginState.loginState = LoginState.NOT_LOGIN
                }
                _loginStateLiveData.postValue(loginState)
            }

        }

    }


    /**
     * btn for login
     */
    fun loginOnClick() {
        _loginStateLiveData.value = LoginState(loginState = LoginState.LOGIN_ING)
        login(loginRepository.user.userId, loginRepository.user.password)
    }


}