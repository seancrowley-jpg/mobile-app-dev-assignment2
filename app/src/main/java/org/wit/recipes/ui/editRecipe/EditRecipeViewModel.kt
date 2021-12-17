package org.wit.recipes.ui.editRecipe

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.wit.recipes.firebase.FirebaseDBManager
import org.wit.recipes.models.RecipeManager
import org.wit.recipes.models.RecipeModel
import timber.log.Timber

class EditRecipeViewModel : ViewModel() {
    private val status = MutableLiveData<Boolean>()
    private val recipe = MutableLiveData<RecipeModel>()

    val observableStatus: LiveData<Boolean>
        get() = status

    var observableRecipe: LiveData<RecipeModel>
        get() = recipe
        set(value) {recipe.value = value.value}

    fun getRecipe(userid:String, id: String) {
        try {
            FirebaseDBManager.findById(userid, id, recipe)
            Timber.i("Success got recipe info : ${recipe.value.toString()}")
        } catch (e: Exception) {
            Timber.i("Error : $e.message")
        }
    }

    fun updateRecipe(recipe: RecipeModel,userid:String, id: String) {
        try {
            FirebaseDBManager.update(userid, id, recipe)
            Timber.i("Success updated recipe : $recipe")
        } catch (e: Exception) {
            Timber.i("Error : $e.message")
        }
    }
}