package org.wit.recipes.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.wit.recipes.databinding.CardIngredientBinding



class IngredientAdapter constructor(private var ingredients: MutableList<String?>) :
    RecyclerView.Adapter<IngredientAdapter.MainHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        val binding = CardIngredientBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return MainHolder(binding)
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        var ingredient = ingredients[holder.adapterPosition]
        holder.bind(ingredient)
    }

    override fun getItemCount(): Int = ingredients.size

    class MainHolder(private val binding: CardIngredientBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(ingredient: String?) {
            binding.ingredientName.text = ingredient
        }
    }
}

