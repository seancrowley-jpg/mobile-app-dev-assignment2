package org.wit.recipes.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.ActivityResultLauncher
import com.google.android.material.snackbar.Snackbar
import org.wit.recipes.R
import org.wit.recipes.databinding.ActivityLoginBinding
import org.wit.recipes.main.MainApp
import org.wit.recipes.models.UserModel

class LoginActivity : AppCompatActivity() {
    lateinit var app: MainApp
    private lateinit var binding: ActivityLoginBinding
    var user = UserModel()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        app = application as MainApp
        binding.toolbarLogin.title = resources.getString(R.string.btn_login)
        setSupportActionBar(binding.toolbarLogin)

        binding.btnLogin.setOnClickListener() {
            user.email = binding.loginEmail.text.toString()
            user.password = binding.loginPassword.text.toString()
            if(user.email.isEmpty())
            {
                binding.loginEmail.requestFocus();
                binding.loginEmail.setError("Please enter a email");
            }
            if(user.password.isEmpty())
            {
                binding.loginPassword.requestFocus();
                binding.loginPassword.setError("Please enter a password");
            }
            if (app.users.checkPassword(user)) {
                app.currentUser = app.users.login(user)
                val launcherIntent = Intent(this, RecipeListActivity::class.java)
                startActivity(launcherIntent)
            }
            else
                Snackbar.make(it,"Invalid Login", Snackbar.LENGTH_LONG).show()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_login, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_signup -> {
                val launcherIntent = Intent(this, SignupActivity::class.java)
                startActivity(launcherIntent)
            }
        }
        return super.onOptionsItemSelected(item)
    }
}