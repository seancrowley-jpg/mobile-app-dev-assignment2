package org.wit.recipes.ui.recipeList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import org.wit.recipes.firebase.FirebaseDBManager
import org.wit.recipes.models.RecipeManager
import org.wit.recipes.models.RecipeModel
import org.wit.recipes.models.UserMemStore
import timber.log.Timber
import java.lang.Exception

class RecipeListViewModel : ViewModel(){
    private val recipeList = MutableLiveData<List<RecipeModel>>()

    var liveFirebaseUser = MutableLiveData<FirebaseUser>()
    var readOnly = MutableLiveData(false)

    val observableRecipesList: LiveData<List<RecipeModel>>
        get() = recipeList

    init {
        load()
    }

    fun load() {
        try {
            Timber.i("Firebase User Id: ${liveFirebaseUser.value?.uid!!}")
            readOnly.value = false
            FirebaseDBManager.findAll(liveFirebaseUser.value?.uid!!,
                recipeList)
            Timber.i("Load Success : ${recipeList.value.toString()}")
        }
        catch (e: Exception) {
            Timber.i("Load Error : $e.message")
        }
    }

    fun loadAll() {
        try {
            readOnly.value = true
            FirebaseDBManager.findAll(recipeList)
            Timber.i("LoadAll Success : ${recipeList.value.toString()}")
        }
        catch (e: Exception) {
            Timber.i("LoadAll Error : $e.message")
        }
    }

    fun deleteRecipe(userid: String,recipe: RecipeModel) {
        try {
            FirebaseDBManager.delete(userid,recipe)
            Timber.i("Report Delete Success")
        }
        catch (e: Exception) {
            Timber.i("Report Delete Error : $e.message")
        }
    }

    fun deleteAllRecipes(){
        try {
            FirebaseDBManager.deleteAll(liveFirebaseUser.value?.uid!!)
            Timber.i("Report DeleteAll Success")
        }
        catch (e: Exception) {
            Timber.i("Report Delete Error : $e.message")
        }    }
}