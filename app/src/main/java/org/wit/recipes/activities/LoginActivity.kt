package org.wit.recipes.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import org.wit.recipes.R
import org.wit.recipes.databinding.ActivityLoginBinding
import org.wit.recipes.main.MainApp
import org.wit.recipes.models.UserModel

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    var user = UserModel()
    lateinit var app: MainApp
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        app = application as MainApp

        binding.btnLogin.setOnClickListener() {
            user.email = binding.loginEmail.text.toString()
            user.password = binding.loginPassword.text.toString()
            if (app.users.login(user)) {
                val launcherIntent = Intent(this, RecipeListActivity::class.java)
                startActivity(launcherIntent)
            }
            else
                Snackbar.make(it,"Invalid Login", Snackbar.LENGTH_LONG).show()
        }

    }
}