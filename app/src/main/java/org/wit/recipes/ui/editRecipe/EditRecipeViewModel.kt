package org.wit.recipes.ui.editRecipe

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.wit.recipes.models.RecipeManager
import org.wit.recipes.models.RecipeModel

class EditRecipeViewModel : ViewModel() {
    private val status = MutableLiveData<Boolean>()
    private val recipe = MutableLiveData<RecipeModel>()

    val observableStatus: LiveData<Boolean>
        get() = status

    var observableRecipe: LiveData<RecipeModel>
        get() = recipe
        set(value) {recipe.value = value.value}

    fun getRecipe(id: Long) {
        recipe.value = RecipeManager.findById(id)

    }

    fun updateRecipe(recipe: RecipeModel) {
        status.value = try {
            RecipeManager.update(recipe)
            true
        } catch (e: IllegalArgumentException) {
            false
        }
    }
}