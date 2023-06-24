

## 前言

登录作为应用程序的核心功能之一。其代码是否易于理解、维护和测试于应用的稳定性至关重要,为了达到这个目标，开发人员需要选择适当的架构和模式来组织和管理登录流程。

Android Jetpack提供了一套强大的工具和组件，其中包括MVVM（Model-View-ViewModel）架构模式，为我们提供了一种优雅的方式来构建可维护、可扩展且易于测试的应用程序。MVVM架构通过将业务逻辑与界面逻辑分离，以及通过数据绑定机制实现视图和ViewModel的交互，使得开发过程更加简化和高效。

本文将介绍如何使用Android Jetpack的MVVM架构模式来实现登录功能。我们将使用ViewModel作为连接视图和数据的中间层，并结合LiveData和Repository来管理数据和进行异步操作。通过这种方式，我们可以实现响应式的UI更新，避免内存泄漏问题，并使得代码更易于理解、扩展和维护。

在本文中，我们将逐步引导您完成一个登录功能的实现，涵盖以下关键方面：

-   创建ViewModel类并定义LiveData以管理登录状态和结果。
-   实现Repository类来处理登录数据和操作。
-   构建与ViewModel关联的UI界面，通过数据绑定机制实现双向绑定和响应式更新。
-   异步处理登录请求，并通过LiveData将结果返回给UI层。
-   针对不同的登录状态，展示相应的用户界面和反馈。

通过本文的学习，您将掌握如何使用Android Jetpack的MVVM架构模式来实现登录功能，从而加深对该架构的理解和应用能力，并能够在实际项目中灵活运用MVVM模式构建更加优雅和可维护的应用程序。




## Jetpack架构组件
Jetpack提供了多个强大的组件，其中Lifecycle、LiveData和ViewModel是构建MVVM架构的关键组件。在本文中，我们将使用这些组件与Kotlin协程协同工作，以实现更高效的MVVM架构。

1. Lifecycle

Lifecycle是一个用于管理Android组件（如Activity和Fragment）生命周期的库。它提供了一种方便的方式来确保在组件的生命周期发生变化时，相关代码可以自动启动或停止。Lifecycle库通过将组件的生命周期状态与组件的相关操作（如启动和停止服务）进行关联，从而避免了内存泄漏和其他相关问题。

2. LiveData

LiveData是一个具有生命周期感知能力的可观察数据存储类。它提供了一种方便的方式来实现数据驱动的UI，以及确保UI组件和数据存储之间的一致性。LiveData可以感知组件的生命周期状态，并在组件处于激活状态时通知观察者，从而避免了不必要的数据更新和内存泄漏。

3. ViewModel

ViewModel是一个用于管理UI相关数据的类。它提供了一种方便的方式来避免数据丢失和内存泄漏，并确保在组件的生命周期发生变化时，数据可以自动保存和恢复。ViewModel通常与LiveData结合使用，以确保UI组件和数据存储之间的一致性。


5.Kotlin协程

Kotlin 协程是Kotlin语言中的一种轻量级线程库，旨在简化异步编程和并发编程。Kotlin协程是一种非常实用和强大的异步编程和并发编程库，可以帮助开发者简化异步任务的处理和协调，并提高应用程序的性能和稳定性。在MVVM架构中，Kotlin协程通常与ViewModel和LiveData结合使用，以实现更高效、更健壮的数据存储和UI更新。


##  实现MVVM架构
登录界面效果如下面动图所示：


![1687602866132-1.gif](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/aca882fd2fa447c48284a9a9ec295888~tplv-k3u1fbpfcp-watermark.image?)

### 定义Model层，包括 LoginModel，LoginState，LoginDataSource，LoginRepository


####  登录数据模型
LoginModel.kt
```
data class LoginModel(
    var userId: String,
    var password: String
){
    /**
     * 是否合规
     * 不为空而且密码>= 6
     */
    fun isValid():Boolean{
        return !TextUtils.isEmpty(userId) && !TextUtils.isEmpty(password) && password.length >=6
    }
}
```

其中isValid用于验证数据的合规性。userId 不为空并且password的长度大于等于6,登录按钮才Enable。

#### 定义登录状态
LoginState.kt
 ```
data class LoginState(
    var loginState: Int = NOT_LOGIN
){
    companion object{

        const val NOT_LOGIN = 0
        const val LOGIN_VALID = 1
        const val LOGIN_ING = 2
        const val LOGIN_SUCCESS = 3
        const val LOGIN_FAIL = 4
    }
}
```

登录共5种状态

1，未登录状态： 0 这是初始状态，用户尚未进行登录操作或登录凭据已过期。在这种状态下，用户只能访问应用的有限功能或者需要登录才能访问的功能将被限制。

2，登录Valid状态： 1 当用户输入满足userId 不为空并且password的长度大于等于6,登录按钮才Enable，这个状态登录按钮点击，称为登录Valid 状态。

3，登录中状态： 2 当用户点击登录按钮后，应用会进入登录中状态，此时可能显示一个加载动画或进度条来指示登录过程正在进行中。在这个状态下，用户需要等待登录过程完成。

4，登录成功状态 ：3 如果用户提供的登录凭据验证通过，应用将进入登录成功状态。在这个状态下，用户可以访问登录后的功能，并且应用通常会跳转到主页面或其他授权访问的页面。

5，登录失败状态：4 如果用户提供的登录凭据验证失败，应用将进入登录失败状态。在这个状态下，应用可能会显示错误消息或提供其他方式来帮助用户解决登录问题。

#### 定义登录返回状态
LoginResult.kt
```
data class LoginResult(
    val success: UserInfo? = null,
    val error: String?=null

)
```

登录成功返回UserInfo,失败返回error 信息

#### 定义登录返回UserInfo,包括 token
UserInfo.kt
```
data class UserInfo(
    val displayName: String,
    val userId :String,
    val token:String

)
```
#### 定义登录LoginDataSource,模拟登录，第一次失败，第二次成功。
```
class LoginDataSource {

    var count = 0
    //模拟登录
   suspend fun login(username: String, password: String): Result<LoginResult> {
        return try {
            count++
            delay(100)
            if(count %2 ==0){
                val userInfo = UserInfo(username,"1233333",java.util.UUID.randomUUID().toString())
                val fakeUser = LoginResult( userInfo,"")
                Result.Success(fakeUser)
            }else{
                val result = LoginResult( null,"登录失败")
                Result.Fail(result)
            }

        } catch (e: Throwable) {
            val result = LoginResult( null,"Error logging in")
            Result.Fail(result)
        }
    }
```


#### LoginRepository：ViewModel 访问数据的桥梁。

```
class LoginRepository(val dataSource: LoginDataSource) {

    val user: LoginModel by lazy {
        LoginModel("","")
    }


    suspend fun login(username: String, password: String): Result<LoginResult> {
        return dataSource.login(username, password)
    }


}
```

### 定义View层，包括 Activity,Fragment,Databinding

#### Databinding

```
<?xml version="1.0" encoding="utf-8"?>
<layout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools">

    <data>

        <variable
            name="viewModel"
            type="com.dhl.loginmvvm.ui.login.LoginViewModel" />

        <import type="android.view.View" />

        <import type="com.dhl.loginmvvm.ui.login.LoginState" />
    </data>


    <androidx.core.widget.NestedScrollView
        android:layout_width="match_parent"
        android:layout_height="match_parent"

        >

        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:background="@drawable/loginbkg"
            android:gravity="center"
            android:orientation="vertical">

            <androidx.cardview.widget.CardView
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:layout_margin="30dp"
                android:background="@drawable/custom_edittext"
                app:cardCornerRadius="20dp"
                app:cardElevation="20dp">


                <LinearLayout
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:layout_gravity="center_horizontal"
                    android:focusable="false"
                    android:orientation="vertical"
                    android:padding="24dp">


                    <TextView
                        android:id="@+id/loginText"
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:text="Login"
                        android:textAlignment="center"
                        android:textColor="@color/purple"
                        android:textSize="36sp"
                        android:textStyle="bold" />

                    <EditText
                        android:id="@+id/username"
                        android:layout_width="match_parent"
                        android:layout_height="50dp"
                        android:layout_marginTop="40dp"
                        android:afterTextChanged="@{(text) -> viewModel.onUserTextChanged(text)}"
                        android:background="@drawable/custom_edittext"
                        android:drawableLeft="@drawable/person"
                        android:drawablePadding="8dp"
                        android:hint="Username"
                        android:padding="8dp"
                        android:textColor="@color/black"
                        android:textColorHighlight="@color/cardview_dark_background" />

                    <EditText
                        android:id="@+id/password"
                        android:layout_width="match_parent"
                        android:layout_height="50dp"
                        android:layout_marginTop="20dp"
                        android:afterTextChanged="@{(text) -> viewModel.onPasswordTextChanged(text)}"
                        android:background="@drawable/custom_edittext"
                        android:drawableLeft="@drawable/password"
                        android:drawablePadding="8dp"
                        android:hint="Password"
                        android:inputType="textPassword"
                        android:padding="8dp"
                        android:textColor="@color/black"
                        android:textColorHighlight="@color/cardview_dark_background" />

                    <Button
                        android:id="@+id/loginButton"
                        android:layout_width="match_parent"
                        android:layout_height="60dp"
                        android:layout_marginTop="30dp"
                        android:enabled="@{viewModel.loginStateLivedata.loginState == LoginState.LOGIN_VALID }"
                        android:onClick="@{()->viewModel.loginOnClick()}"
                        android:text="Login"
                        android:textSize="18sp"
                        app:cornerRadius="20dp" />

                </LinearLayout>

            </androidx.cardview.widget.CardView>

            <TextView
                android:id="@+id/signupText"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:layout_marginBottom="20dp"
                android:padding="8dp"
                android:text="Not yet registered? SignUp Now"
                android:textAlignment="center"
                android:textColor="@color/purple"
                android:textSize="14sp" />

        </LinearLayout>
    </androidx.core.widget.NestedScrollView>
</layout>
```

#### Activity
```
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
                showToast("user:${it.success.displayName}")
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
```
通过观察者模式接收ViewModel的数据，用来提示或者进入下一个页面。


### 定义ViewModel



```
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
```




## 源码：

https://github.com/ThirdPrince/LoginMVVM


## 总结
该代码实现了一个使用Jetpack MVVM架构的Android登录界面。它通过Jetpack架构组件（如Lifecycle、LiveData和ViewModel和Databinding）与Kotlin协程，以实现更高效的MVVM架构。

这个登录Dmeo使用了以下7个类：

1.  LoginModel：登录数据模型，包含userId和password字段，并提供了isValid()方法用于验证数据的合规性。
2.  LoginState：登录状态类，包含登录的不同状态常量，如NOT_LOGIN、LOGIN_VALID、LOGIN_ING、LOGIN_SUCCESS和LOGIN_FAIL。
3.  LoginResult：登录结果类，包含登录成功时的UserInfo对象和登录失败时的错误信息。
4.  UserInfo：用户信息类，包含displayName、userId和token字段。
5.  LoginDataSource：登录数据源类，模拟登录过程，通过模拟第一次失败、第二次成功来返回不同的结果。
6.  LoginRepository：登录仓库类，作为ViewModel与数据源之间的桥梁，负责访问数据并返回结果。
7.  LoginActivity：登录界面的Activity类，通过DataBinding将布局文件与ViewModel进行绑定，并处理登录按钮的点击事件和登录状态的变化。
8.  LoginViewModel:这个ViewModel负责处理登录逻辑，并提供相关的LiveData供UI层观察和响应登录状态和结果的变化。

其中1,2,3,4,5,6 都是属于Model层，7属于View层，8属于ViewModel。


