package org.wit.recipes.models

import android.content.Context
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseUser
import com.google.gson.*
import com.google.gson.reflect.TypeToken
import org.wit.recipes.helpers.*
import timber.log.Timber
import java.lang.reflect.Type
import java.util.*

const val JSON_FILE = "recipes.json"
val gsonBuilder: Gson = GsonBuilder().setPrettyPrinting()
    .registerTypeAdapter(Uri::class.java, UriParser())
    .create()
val listType: Type = object : TypeToken<ArrayList<RecipeModel>>() {}.type

fun generateRandomId(): Long {
    return Random().nextLong()
}

class RecipeJSONStore(private val context: Context) : RecipeStore {

    var recipes = mutableListOf<RecipeModel>()

    init {
        if (exists(context, JSON_FILE)) {
            deserialize()
        }
    }

    override fun findAll(recipeList: MutableLiveData<List<RecipeModel>>) {
        logAll()
        //return recipes
    }

    override fun findAll(userid: String, recipeList: MutableLiveData<List<RecipeModel>>) {
        TODO("Not yet implemented")
    }

    override fun findById(id: String, recipeId: String, recipe: MutableLiveData<RecipeModel>) {
        //val foundRecipe: RecipeModel? = RecipeManager.recipes.find { it.uid == id }
        //return foundRecipe
    }

    override fun findRecipeById(recipeId: String, recipe: MutableLiveData<RecipeModel>) {
        TODO("Not yet implemented")
    }

    override fun create(firebaseUser: MutableLiveData<FirebaseUser>, recipe: RecipeModel) {
        recipe.uid = getId()
        recipes.add(recipe)
        serialize()
    }


    override fun update(userid: String, recipeId: String, recipe: RecipeModel) {
        var foundRecipe: RecipeModel? = recipes.find { r -> r.uid == recipe.uid }
        if (foundRecipe != null) {
            foundRecipe.name = recipe.name
            foundRecipe.description = recipe.description
            foundRecipe.meal = recipe.meal
            foundRecipe.image = recipe.image
            foundRecipe.ingredients.clear()
            foundRecipe.ingredients.addAll(recipe.ingredients)
            foundRecipe.steps.clear()
            foundRecipe.steps.addAll(recipe.steps)
            logAll()
            serialize()
        }
    }

    override fun delete(id: String, recipeId: String) {
        //recipes.remove(findById(id))
        serialize()
    }

    override fun deleteAll(userid: String) {
        recipes.removeAll(recipes)
        serialize()
        //return recipes
    }

    private fun serialize() {
        val jsonString = gsonBuilder.toJson(recipes, listType)
        write(context, JSON_FILE, jsonString)
    }

    private fun deserialize() {
        val jsonString = read(context, JSON_FILE)
        recipes = gsonBuilder.fromJson(jsonString, listType)
    }

    private fun logAll() {
        recipes.forEach { Timber.i("$it") }
    }
}

class UriParser : JsonDeserializer<Uri>,JsonSerializer<Uri> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Uri {
        return Uri.parse(json?.asString)
    }

    override fun serialize(
        src: Uri?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        return JsonPrimitive(src.toString())
    }
}