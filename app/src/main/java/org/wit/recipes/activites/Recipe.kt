package org.wit.recipes.activites

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import org.wit.recipes.R
import org.wit.recipes.databinding.ActivityRecipeBinding
import org.wit.recipes.main.MainApp
import org.wit.recipes.models.RecipeModel
import timber.log.Timber
import timber.log.Timber.i

class Recipe : AppCompatActivity() {
    private lateinit var binding: ActivityRecipeBinding
    var recipe = RecipeModel()
    lateinit var app: MainApp


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe)
        binding = ActivityRecipeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        i("Recipe Activity started..")

        binding.btnAdd.setOnClickListener() {
            recipe.name = binding.recipeName.text.toString()
            recipe.description = binding.recipeDescription.text.toString()
            if (recipe.name.isNotEmpty()) {
                app.recipes.add(recipe.copy())
                i("add Button Pressed: ${recipe}")
                for (i in app.recipes.indices)
                { i("Recipe[$i]:${this.app.recipes[i]}") }
            }
            else {
                Snackbar.make(it,"Please Enter a name", Snackbar.LENGTH_LONG)
                    .show()
            }
            i("add Button Pressed")
        }
    }
}