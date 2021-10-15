package org.wit.recipes.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.wit.recipes.R
import org.wit.recipes.databinding.ActivityRecipeListBinding
import org.wit.recipes.databinding.CardRecipeBinding
import org.wit.recipes.main.MainApp
import org.wit.recipes.models.RecipeModel

class RecipeListActivity : AppCompatActivity() {

    lateinit var app: MainApp

    private lateinit var binding: ActivityRecipeListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecipeListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        app = application as MainApp

        val layoutManager = LinearLayoutManager(this)
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = RecipeAdapter(app.recipes)
    }
}

class RecipeAdapter constructor(private var recipes: List<RecipeModel>) :
    RecyclerView.Adapter<RecipeAdapter.MainHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        val binding = CardRecipeBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return MainHolder(binding)
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val recipe = recipes[holder.adapterPosition]
        holder.bind(recipe)
    }

    override fun getItemCount(): Int = recipes.size

    class MainHolder(private val binding : CardRecipeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(recipe: RecipeModel) {
            binding.recipeName.text = recipe.name
            binding.recipeDescription.text = recipe.description
        }
    }
}