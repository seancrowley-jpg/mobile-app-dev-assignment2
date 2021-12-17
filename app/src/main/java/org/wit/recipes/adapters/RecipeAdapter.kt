package org.wit.recipes.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import org.wit.recipes.databinding.CardRecipeBinding
import org.wit.recipes.models.RecipeModel
import timber.log.Timber
import java.util.*
import kotlin.collections.ArrayList

interface RecipeListener {
    fun onRecipeClick(id: Long)
    fun onDeleteClick(id: Long)
    fun onEditClick(id: Long)
}

class RecipeAdapter constructor(private var recipes: ArrayList<RecipeModel>, private val listener: RecipeListener) :
    RecyclerView.Adapter<RecipeAdapter.MainHolder>(), Filterable {

    private val recipesFiltered = recipes
    private val searchList = ArrayList<RecipeModel>(recipesFiltered)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        val binding = CardRecipeBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return MainHolder(binding)
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val recipe = recipes[holder.adapterPosition]
        holder.bind(recipe, listener)
    }

    override fun getItemCount(): Int = recipes.size

    override fun getFilter(): Filter {
        return object: Filter() {
            override fun performFiltering(p0: CharSequence?): FilterResults {
                val filteredList = ArrayList<RecipeModel>()
                val searchText = p0.toString()
                if(searchText.isBlank() or searchText.isEmpty()){
                    filteredList.addAll(searchList)
                }
                else{
                    searchList.forEach{
                        if(it.name.lowercase().contains(searchText) or it.description.lowercase().contains(searchText)){
                            filteredList.add(it)
                        }
                    }
                }
                val results = FilterResults()
                results.values = filteredList
                return results
            }

            override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
                //recipesFiltered.clear()
                //recipesFiltered.addAll(p1!!.values as ArrayList<RecipeModel>)
                //notifyDataSetChanged()
            }

        }
    }

    fun removeAt(position: Int) {
        recipes.removeAt(position)
        notifyItemRemoved(position)
    }

    class MainHolder(private val binding : CardRecipeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(recipe: RecipeModel, listener: RecipeListener) {
            //binding.recipeName.text = recipe.name
            //binding.recipeDescription.text = recipe.description
            binding.recipe = recipe
            binding.root.tag = recipe.id
            Picasso.get().load(recipe.image).resize(200,200).into(binding.imageIcon)
            binding.btnEditRecipe.setOnClickListener { listener.onEditClick(recipe.id) }
            binding.root.setOnClickListener { listener.onRecipeClick(recipe.id) }
            binding.btnDeleteRecipe.setOnClickListener { listener.onDeleteClick(recipe.id) }
            binding.executePendingBindings()
        }
    }
}