package com.dhl.loginmvvm.ui.login

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.afollestad.materialdialogs.MaterialDialog
import com.dhl.loginmvvm.R
import com.dhl.loginmvvm.databinding.Login2Binding
import com.dhl.loginmvvm.ui.login.LoginState.Companion.LOGIN_ING


/**
 * Jetpack MVVM
 * @author dhl
 */
class LoginActivity : AppCompatActivity() {

    private lateinit var loginViewModel: LoginViewModel

    val dialog: MaterialDialog by lazy {
        MaterialDialog.Builder(this)
            .content("登录中...")
            .progress(true, 10)
            .cancelable(false)

            .build()
    }




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = DataBindingUtil.setContentView<Login2Binding>(
            this,
            R.layout.login2
        )
        loginViewModel = ViewModelProvider(this, LoginViewModelFactory())
            .get(LoginViewModel::class.java)
        binding.lifecycleOwner = this
        binding.viewModel = loginViewModel


        loginViewModel.loginState.observe(this,{
            when(it.loginState){
               LOGIN_ING ->{
                    showLoginDialog()
                }
                LoginState.LOGIN_FAIL, LoginState.LOGIN_SUCCESS->{
                    disLoginDialog()
                }


            }
            loginViewModel.loginIsValid()




        })

        loginViewModel.loginResult.observe(this@LoginActivity, Observer {


            if(it.error != null){

            }
            if(it.success != null){
                updateUiWithUser(it.success)
            }
            dialog.dismiss()


        })


    }


    private fun updateUiWithUser(model: UserInfo) {
        val welcome = getString(R.string.welcome)
        val displayName = model.displayName
        Toast.makeText(
            applicationContext,
            "$welcome $displayName",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
    }

    private fun showLoginDialog(){
        dialog.show()

    }
    private fun disLoginDialog(){
        dialog.dismiss()

    }
}

