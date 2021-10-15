package org.wit.recipes.models
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RecipeModel(var id: Long = 0,var name: String = "", var description: String = "") :Parcelable
