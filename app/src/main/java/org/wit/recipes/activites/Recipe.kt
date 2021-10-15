package org.wit.recipes.activites

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import org.wit.recipes.R
import org.wit.recipes.databinding.ActivityRecipeBinding
import timber.log.Timber
import timber.log.Timber.i

class Recipe : AppCompatActivity() {
    private lateinit var binding: ActivityRecipeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe)
        binding = ActivityRecipeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Timber.plant(Timber.DebugTree())

        i("Recipe Activity started..")

        binding.btnAdd.setOnClickListener() {
            i("add Button Pressed")
        }
    }
}