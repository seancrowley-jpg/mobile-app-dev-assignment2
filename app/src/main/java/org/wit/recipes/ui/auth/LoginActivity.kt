package org.wit.recipes.ui.auth

import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.Observer
import org.wit.recipes.R
import org.wit.recipes.databinding.ActivityLoginBinding
import org.wit.recipes.ui.home.Home
import timber.log.Timber


class LoginActivity : AppCompatActivity() {

    private lateinit var loginRegisterViewModel : LoginRegisterViewModel
    private lateinit var loginBinding : ActivityLoginBinding

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(loginBinding.root)

        loginBinding.btnLogin.setOnClickListener {
            signIn(loginBinding.emailField.text.toString(),
                loginBinding.passwordField.text.toString())
        }
        loginBinding.btnSignup.setOnClickListener {
            createAccount(loginBinding.emailField.text.toString(),
                loginBinding.passwordField.text.toString())
        }
    }

    public override fun onStart() {
        super.onStart()
        loginRegisterViewModel = ViewModelProvider(this).get(LoginRegisterViewModel::class.java)
        loginRegisterViewModel.liveFirebaseUser.observe(this, Observer
        { firebaseUser -> if (firebaseUser != null)
            startActivity(Intent(this, Home::class.java)) })

        loginRegisterViewModel.firebaseAuthManager.errorStatus.observe(this, Observer
        { status -> checkStatus(status) })
    }

    override fun onBackPressed() {
        super.onBackPressed()
        Toast.makeText(this,"Click again to Close App...",Toast.LENGTH_LONG).show()
        finish()
    }

    private fun createAccount(email: String, password: String) {
        Timber.d("createAccount:$email")
        if (!validateForm()) { return }

        loginRegisterViewModel.register(email,password)
    }

    private fun signIn(email: String, password: String) {
        Timber.d("signIn:$email")
        if (!validateForm()) { return }

        loginRegisterViewModel.login(email,password)
    }

    private fun checkStatus(error:Boolean) {
        if (error)
            Toast.makeText(this,
                getString(R.string.auth_failed),
                Toast.LENGTH_LONG).show()
    }

    private fun validateForm(): Boolean {
        var valid = true

        val email = loginBinding.emailField.text.toString()
        if (TextUtils.isEmpty(email)) {
            loginBinding.emailField.error = "Required."
            valid = false
        } else {
            loginBinding.emailField.error = null
        }

        val password = loginBinding.passwordField.text.toString()
        if (TextUtils.isEmpty(password)) {
            loginBinding.passwordField.error = "Required."
            valid = false
        } else {
            loginBinding.passwordField.error = null
        }
        return valid
    }
}