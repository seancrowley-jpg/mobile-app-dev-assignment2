package org.wit.recipes.models

import timber.log.Timber.i

class RecipeMemStore: RecipeStore {
    val recipes = ArrayList<RecipeModel>()

    override fun findAll(): List<RecipeModel> {
        return recipes
    }

    override fun create(recipe: RecipeModel) {
        recipes.add(recipe)
        logAll()
    }

    fun logAll() {
        recipes.forEach{ i("${it}") }
    }
}