package org.wit.recipes.ui.viewRecipe

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.wit.recipes.firebase.FirebaseDBManager
import org.wit.recipes.models.RecipeManager
import org.wit.recipes.models.RecipeModel
import timber.log.Timber

class ViewRecipeViewModel: ViewModel() {
    private val recipe = MutableLiveData<RecipeModel>()

    val observableRecipe: LiveData<RecipeModel>
        get() = recipe

    fun getRecipe(userid:String, id: String) {
        try {
            FirebaseDBManager.findById(userid, id, recipe)
            Timber.i("Success got recipe info : ${recipe.value.toString()}")
        } catch (e: Exception) {
            Timber.i("Error : $e.message")
        }
    }
}