package org.wit.recipes.models

import timber.log.Timber.i

var lastId = 0L

internal fun getId(): Long {
    return lastId++
}

class RecipeMemStore: RecipeStore {
    val recipes = ArrayList<RecipeModel>()

    override fun findAll(): List<RecipeModel> {
        return recipes
    }

    override fun create(recipe: RecipeModel) {
        recipe.id= getId()
        recipes.add(recipe)
        logAll()
    }

    override fun update(recipe: RecipeModel) {
        var foundRecipe: RecipeModel? = recipes.find { r -> r.id == recipe.id }
        if (foundRecipe != null) {
            foundRecipe.name = recipe.name
            foundRecipe.description = recipe.description
            logAll()
        }
    }

    fun logAll() {
        recipes.forEach{ i("${it}") }
    }
}