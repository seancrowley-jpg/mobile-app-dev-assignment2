package org.wit.recipes.models

import android.content.Context
import android.net.Uri
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import org.wit.recipes.helpers.exists
import org.wit.recipes.helpers.read
import org.wit.recipes.helpers.write
import timber.log.Timber
import java.lang.reflect.Type

const val USER_JSON_FILE = "users.json"
val userGsonBuilder: Gson = GsonBuilder().setPrettyPrinting().registerTypeAdapter(Uri::class.java, UriParser()).create()
val userListType: Type = object : TypeToken<java.util.ArrayList<UserModel>>() {}.type

class UserJSONStore (private val context: Context) : UserStore{
    var users = mutableListOf<UserModel>()

    init {
        if (exists(context, USER_JSON_FILE)) {
            deserialize()
        }
    }

    override fun findAll(): MutableList<UserModel> {
        logAll()
        return users
    }

    override fun signup(user: UserModel) {
        user.id= getId()
        users.add(user)
        logAll()
        serialize()
    }

    override fun login(user: UserModel): UserModel? {
        var foundUser: UserModel? = users.find { u -> u.email == user.email }
        if (foundUser != null) {
            if (foundUser.password == user.password)
                logAll()
                return foundUser
        }
        return null
    }

    override fun checkPassword(user: UserModel) : Boolean {
        var foundUser: UserModel? = users.find { u -> u.email == user.email }
        if (foundUser != null) {
            if (foundUser.password == user.password)
                return true
        }
        return false
    }

    override fun deleteUser(user: UserModel?) {
        users.remove(user)
        serialize()
    }

    private fun serialize() {
        val jsonString = userGsonBuilder.toJson(users, userListType)
        write(context, USER_JSON_FILE, jsonString)
    }

    private fun deserialize() {
        val jsonString = read(context, USER_JSON_FILE)
        users = userGsonBuilder.fromJson(jsonString, userListType)
    }

    private fun logAll() {
        users.forEach{ Timber.i("${it}") }
    }
}