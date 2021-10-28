package org.wit.recipes.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.squareup.picasso.Picasso
import org.wit.recipes.R
import org.wit.recipes.adapters.IngredientAdapter
import org.wit.recipes.adapters.IngredientListener
import org.wit.recipes.adapters.StepListener
import org.wit.recipes.adapters.StepsAdapter
import org.wit.recipes.databinding.ActivityViewRecipeBinding
import org.wit.recipes.main.MainApp
import org.wit.recipes.models.RecipeModel

class ViewRecipeActivity : AppCompatActivity(), IngredientListener, StepListener {
    private lateinit var binding: ActivityViewRecipeBinding
    lateinit var app: MainApp
    var recipe = RecipeModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewRecipeBinding.inflate(layoutInflater)
        recipe = intent.extras?.getParcelable("recipe_view")!!
        setContentView(binding.root)
        val layoutManager = LinearLayoutManager(this)
        val stepsLayoutManager = LinearLayoutManager(this)
        binding.recipeViewName.setText(recipe.name)
        binding.recipeViewDescription.setText(recipe.description)
        binding.recipeMealView.setText(recipe.meal)
        Picasso.get().load(recipe.image).into(binding.recipeViewImage)
        binding.ingredientRecyclerRecipeView.layoutManager = layoutManager
        binding.stepsRecyclerRecipeView.layoutManager = stepsLayoutManager
        binding.ingredientRecyclerRecipeView.adapter = IngredientAdapter(recipe.ingredients, this)
        binding.stepsRecyclerRecipeView.adapter = StepsAdapter(recipe.steps, this )
        binding.toolbarRecipe.title = title
        setSupportActionBar(binding.toolbarRecipe)
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

    override fun onIngredientBtnClick(ingredient: String?) {
    }

    override fun onStepBtnClick(step: String?) {
    }
}