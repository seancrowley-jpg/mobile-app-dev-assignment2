package org.wit.recipes.models

interface RecipeStore {
    fun findAll(): MutableList<RecipeModel>
    fun findById(id: Long) : RecipeModel?
    fun create(recipe: RecipeModel)
    fun update(recipe: RecipeModel)
    fun delete(id: Long)
    fun deleteAll(): MutableList<RecipeModel>
}