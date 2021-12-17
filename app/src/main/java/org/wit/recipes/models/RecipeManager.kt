package org.wit.recipes.models

import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseUser
import timber.log.Timber.i

var lastId = "0"

internal fun getId(): String {
    return lastId + "1"
}

object RecipeManager: RecipeStore {
    val recipes = ArrayList<RecipeModel>()

    override fun findAll(recipeList: MutableLiveData<List<RecipeModel>>) {
        //return recipes
    }

    override fun findAll(userid: String, recipeList: MutableLiveData<List<RecipeModel>>) {
        TODO("Not yet implemented")
    }

    override fun findById(id: String, recipeId: String, recipe: MutableLiveData<RecipeModel>) {
        val foundRecipe: RecipeModel? = recipes.find { it.uid == id }
        //return foundRecipe
    }

    override fun create(firebaseUser: MutableLiveData<FirebaseUser>, recipe: RecipeModel) {
        //recipe.uid= getId()
        recipes.add(recipe)
        logAll()
    }

    override fun update(userid: String, recipeId: String, recipe: RecipeModel) {
        var foundRecipe: RecipeModel? = recipes.find { r -> r.uid == recipe.uid }
        if (foundRecipe != null) {
            foundRecipe.name = recipe.name
            foundRecipe.description = recipe.description
            foundRecipe.meal = recipe.meal
            foundRecipe.image = recipe.image
            //foundRecipe.ingredients.clear()
            //foundRecipe.ingredients.addAll(recipe.ingredients)
            //foundRecipe.steps.clear()
            //foundRecipe.steps.addAll(recipe.steps)
            logAll()
        }
    }

    override fun delete(id: String, recipeId: String) {
        //recipes.remove(findById(id))
    }

    override fun deleteAll(userid: String) {
        recipes.removeAll(recipes)
    }

    fun logAll() {
        recipes.forEach{ i("${it}") }
    }
}