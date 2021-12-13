package org.wit.recipes.ui.recipeList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.wit.recipes.models.RecipeManager
import org.wit.recipes.models.RecipeModel

class RecipeListViewModel : ViewModel(){
    private val recipeList = MutableLiveData<List<RecipeModel>>()

    val observableRecipesList: LiveData<List<RecipeModel>>
        get() = recipeList

    init {
        load()
    }

    fun load() {
        recipeList.value = RecipeManager.findAll()
    }
}