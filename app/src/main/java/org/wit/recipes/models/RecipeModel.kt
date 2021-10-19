package org.wit.recipes.models
import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RecipeModel(var id: Long = 0,var name: String = "", var description: String = "", var image: Uri = Uri.EMPTY,
var ingredients: MutableList<String?> = ArrayList(), var meal: String = "", var steps: MutableList<String?> = ArrayList()) :Parcelable
