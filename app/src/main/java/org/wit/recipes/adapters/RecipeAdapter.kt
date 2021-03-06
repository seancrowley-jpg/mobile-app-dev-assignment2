package org.wit.recipes.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import org.wit.recipes.databinding.CardRecipeBinding
import org.wit.recipes.models.RecipeModel
import kotlin.collections.ArrayList

interface RecipeListener {
    fun onRecipeClick(recipe: RecipeModel)
    fun onDeleteClick(recipe: RecipeModel)
    fun onEditClick(recipe: RecipeModel)
}

class RecipeAdapter constructor(private var recipes: ArrayList<RecipeModel>, private val listener: RecipeListener, private val readOnly: Boolean) :
    RecyclerView.Adapter<RecipeAdapter.MainHolder>(), Filterable {

    private val recipesFiltered = recipes
    private val searchList = ArrayList<RecipeModel>(recipesFiltered)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        val binding = CardRecipeBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return MainHolder(binding, readOnly)
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
                recipesFiltered.clear()
                recipesFiltered.addAll(p1!!.values as ArrayList<RecipeModel>)
                notifyDataSetChanged()
            }

        }
    }

    fun removeAt(position: Int) {
        recipes.removeAt(position)
        notifyItemRemoved(position)
    }

    class MainHolder(private val binding : CardRecipeBinding, private val readOnly : Boolean) :
        RecyclerView.ViewHolder(binding.root) {
        val readOnlyRow = readOnly
        fun bind(recipe: RecipeModel, listener: RecipeListener) {
            //binding.recipeName.text = recipe.name
            //binding.recipeDescription.text = recipe.description
            binding.recipe = recipe
            binding.root.tag = recipe
            Picasso.get().load(recipe.image.toUri()).resize(200,200).into(binding.imageIcon)
            //binding.btnEditRecipe.setOnClickListener { listener.onEditClick(recipe) }
            binding.root.setOnClickListener { listener.onRecipeClick(recipe) }
            //binding.btnDeleteRecipe.setOnClickListener { listener.onDeleteClick(recipe) }
            binding.executePendingBindings()
        }
    }
}