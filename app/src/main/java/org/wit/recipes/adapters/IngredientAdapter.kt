package org.wit.recipes.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.wit.recipes.databinding.CardIngredientBinding
import org.wit.recipes.models.RecipeModel

interface IngredientListener {
    fun onIngredientBtnClick(ingredient: String?)
}

class IngredientAdapter constructor(private var ingredients: MutableList<String?>, private val listener: IngredientListener) :
    RecyclerView.Adapter<IngredientAdapter.MainHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        val binding = CardIngredientBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return MainHolder(binding)
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        var ingredient = ingredients[holder.adapterPosition]
        holder.bind(ingredient, listener)
    }

    override fun getItemCount(): Int = ingredients.size

    class MainHolder(private val binding: CardIngredientBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(ingredient: String?, listener: IngredientListener) {
            binding.ingredientName.text = ingredient
            binding.btnDeleteIngredient.setOnClickListener{listener.onIngredientBtnClick(ingredient)}
        }
    }
}

