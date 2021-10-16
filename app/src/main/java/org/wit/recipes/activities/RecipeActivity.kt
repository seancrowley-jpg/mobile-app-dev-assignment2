package org.wit.recipes.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.google.android.material.snackbar.Snackbar
import org.wit.recipes.R
import org.wit.recipes.databinding.ActivityRecipeBinding
import org.wit.recipes.main.MainApp
import org.wit.recipes.models.RecipeModel
import timber.log.Timber.i

class RecipeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRecipeBinding
    var recipe = RecipeModel()
    lateinit var app: MainApp


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecipeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        var edit = false
        binding.toolbarAdd.title = title
        setSupportActionBar(binding.toolbarAdd)

        app = application as MainApp

        i("Recipe Activity started..")

        if (intent.hasExtra("recipe_edit")) {
            edit = true
            recipe = intent.extras?.getParcelable("recipe_edit")!!
            binding.recipeName.setText(recipe.name)
            binding.recipeDescription.setText(recipe.description)
            binding.btnAdd.setText(R.string.save_recipe)
        }
        binding.btnAdd.setOnClickListener() {
            recipe.name = binding.recipeName.text.toString()
            recipe.description = binding.recipeDescription.text.toString()
            if (recipe.name.isEmpty()) {
                Snackbar.make(it,R.string.enter_recipe_name, Snackbar.LENGTH_LONG)
                    .show()
            }
            else {
                if (edit) {
                    app.recipes.update(recipe.copy())
                } else {
                    app.recipes.create(recipe.copy())
                }
            }
            setResult(RESULT_OK)
            finish()
        }
        binding.chooseImage.setOnClickListener {
            i("Select image")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_recipe, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_cancel -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}