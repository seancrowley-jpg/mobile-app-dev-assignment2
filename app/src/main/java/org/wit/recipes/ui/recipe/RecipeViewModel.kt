package org.wit.recipes.ui.recipe

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import org.wit.recipes.firebase.FirebaseDBManager
import org.wit.recipes.models.RecipeModel

class RecipeViewModel : ViewModel() {
    private val status = MutableLiveData<Boolean>()

    val observableStatus: LiveData<Boolean>
        get() = status

    fun addRecipe(firebaseUser: MutableLiveData<FirebaseUser>,recipe: RecipeModel, context: Context) {
        status.value = try {
            //RecipeManager.create(recipe)
                FirebaseDBManager.create(firebaseUser,recipe, context)
            true
        } catch (e: IllegalArgumentException) {
            false
        }
    }
}