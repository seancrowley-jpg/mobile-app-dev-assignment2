package org.wit.recipes.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import org.wit.recipes.R

class ViewRecipeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_recipe)
    }
}