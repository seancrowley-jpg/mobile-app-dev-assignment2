package org.wit.recipes.models

import timber.log.Timber.i

var lastId = 0L

internal fun getId(): Long {
    return lastId++
}

object RecipeManager: RecipeStore {
    val recipes = ArrayList<RecipeModel>()

    override fun findAll(): MutableList<RecipeModel> {
        return recipes
    }

    override fun findById(id: Long): RecipeModel? {
        val foundRecipe: RecipeModel? = recipes.find { it.id == id }
        return foundRecipe
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
            foundRecipe.meal = recipe.meal
            foundRecipe.image = recipe.image
            //foundRecipe.ingredients.clear()
            //foundRecipe.ingredients.addAll(recipe.ingredients)
            //foundRecipe.steps.clear()
            //foundRecipe.steps.addAll(recipe.steps)
            logAll()
        }
    }

    override fun delete(recipe: RecipeModel) {
        recipes.remove(recipe)
    }

    override fun deleteAll(): MutableList<RecipeModel>{
        recipes.removeAll(recipes)
        return recipes
    }

    fun logAll() {
        recipes.forEach{ i("${it}") }
    }
}