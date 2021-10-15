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
        setContentView(R.layout.activity_recipe)
        binding = ActivityRecipeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbarAdd.title = title
        setSupportActionBar(binding.toolbarAdd)

        i("Recipe Activity started..")

        binding.btnAdd.setOnClickListener() {
            recipe.name = binding.recipeName.text.toString()
            recipe.description = binding.recipeDescription.text.toString()
            if (recipe.name.isNotEmpty()) {
                app.recipes.add(recipe.copy())
                i("add Button Pressed: ${recipe}")
                for (i in app.recipes.indices)
                { i("Recipe[$i]:${this.app.recipes[i]}") }
                setResult(RESULT_OK)
                finish()
            }
            else {
                Snackbar.make(it,"Please Enter a name", Snackbar.LENGTH_LONG)
                    .show()
            }
            i("add Button Pressed")
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