package org.wit.recipes.ui.recipeList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.wit.recipes.models.RecipeManager
import org.wit.recipes.models.RecipeModel
import org.wit.recipes.models.UserMemStore

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

    fun deleteRecipe(recipeModel: RecipeModel) {
        RecipeManager.delete(recipeModel)
    }

    fun deleteAllRecipes(){
        RecipeManager.deleteAll()
    }
}