package org.wit.recipes.ui.viewRecipe

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.wit.recipes.models.RecipeManager
import org.wit.recipes.models.RecipeModel

class ViewRecipeViewModel: ViewModel() {
    private val recipe = MutableLiveData<RecipeModel>()

    val observableRecipe: LiveData<RecipeModel>
        get() = recipe

    fun getRecipe(id: Long) {
        recipe.value = RecipeManager.findById(id)
    }
}