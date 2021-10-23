package org.wit.recipes.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import org.wit.recipes.R
import org.wit.recipes.databinding.ActivitySignupBinding
import org.wit.recipes.main.MainApp
import org.wit.recipes.models.UserModel
import timber.log.Timber

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    var user = UserModel()
    lateinit var app: MainApp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        app = application as MainApp

        binding.btnSignup.setOnClickListener() {
            user.name = binding.signupName.text.toString()
            user.email = binding.signupEmail.text.toString()
            user.password = binding.signupPassword.text.toString()
            app.users.signup(user.copy())
            val launcherIntent = Intent(this, RecipeListActivity::class.java)
            startActivity(launcherIntent)
            Timber.i("users ${app.users}}")
        }
    }
}