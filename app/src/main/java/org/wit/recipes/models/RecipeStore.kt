package org.wit.recipes.models

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseUser

interface RecipeStore {
    fun findAll(recipeList: MutableLiveData<List<RecipeModel>>)
    fun findAll(userid: String,recipeList: MutableLiveData<List<RecipeModel>>)
    fun findById(userid: String , recipeId: String, recipe: MutableLiveData<RecipeModel>)
    fun findRecipeById(recipeId: String, recipe: MutableLiveData<RecipeModel>)
    fun create(firebaseUser: MutableLiveData<FirebaseUser>, recipe: RecipeModel, context: Context)
    fun update(userid: String, recipeId: String,recipe: RecipeModel, context: Context)
    fun delete(userid: String, recipeId: String)
    fun deleteAll(userid: String)
}