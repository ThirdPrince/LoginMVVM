package com.dhl.loginmvvm.ui.login

import android.app.Activity
import android.content.Intent
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.afollestad.materialdialogs.MaterialDialog
import com.dhl.loginmvvm.R
import com.dhl.loginmvvm.databinding.ActivityLoginBinding
import com.dhl.loginmvvm.main.MainActivity
import com.dhl.loginmvvm.ui.login.LoginState.Companion.LOGIN_ING


/**
 * Jetpack MVVM
 * @author dhl
 */
class LoginActivity : AppCompatActivity() {

    private lateinit var loginViewModel: LoginViewModel

    private val dialog: MaterialDialog by lazy {
        MaterialDialog.Builder(this)
            .content("登录中...")
            .progress(true, 10)
            .cancelable(false)

            .build()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = DataBindingUtil.setContentView<ActivityLoginBinding>(
            this,
            R.layout.activity_login
        )
        loginViewModel = ViewModelProvider(this, LoginViewModelFactory())
            .get(LoginViewModel::class.java)
        binding.lifecycleOwner = this
        binding.viewModel = loginViewModel


        loginViewModel.loginStateLivedata.observe(this, {
            when (it.loginState) {
                LOGIN_ING -> {
                    showLoginDialog()
                }
                LoginState.LOGIN_FAIL, LoginState.LOGIN_SUCCESS -> {
                    disLoginDialog()
                }


            }
            loginViewModel.loginIsValid()


        })

        loginViewModel.loginResult.observe(this@LoginActivity, Observer {


            if (it.error != null) {
                showToast(it.error)
            }
            if (it.success != null) {
                showToast(it.success.displayName)
                goMain()
            }

        })


    }


    private fun showLoginDialog() {
        dialog.show()

    }

    private fun disLoginDialog() {
        dialog.dismiss()

    }

    /**
     * goMain
     */
    private fun goMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}

fun Activity.showToast(str: String) {
    Toast.makeText(this, str, Toast.LENGTH_SHORT).show()
}

