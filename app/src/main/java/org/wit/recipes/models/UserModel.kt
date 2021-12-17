package org.wit.recipes.models
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserModel(var id: String = "",var name: String = "", var email: String = "", var password: String = "") :
    Parcelable
