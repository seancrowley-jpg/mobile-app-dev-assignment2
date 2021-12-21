package org.wit.recipes.models
import android.net.Uri
import android.os.Parcelable
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.parcelize.Parcelize

@IgnoreExtraProperties
@Parcelize
data class RecipeModel(
    var uid: String? = "",
    var name: String = "",
    var description: String = "",
    var image: String = "",
    var ingredients: MutableList<String?> = ArrayList(),
    var meal: String = "",
    var fid: String = "",
    var steps: MutableList<String?> = ArrayList(), )
    :Parcelable {
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "uid" to uid,
            "name" to name,
            "description" to description,
            "image" to image,
            "ingredients" to ingredients,
            "meal" to meal,
            "fid" to fid,
            "steps" to steps,
        )
    }
}
