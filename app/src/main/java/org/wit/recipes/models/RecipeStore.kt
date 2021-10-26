package org.wit.recipes.models

interface RecipeStore {
    fun findAll(): MutableList<RecipeModel>
    fun create(recipe: RecipeModel)
    fun update(recipe: RecipeModel)
    fun delete(recipe: RecipeModel)
    fun deleteAll(): MutableList<RecipeModel>
}