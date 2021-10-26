package org.wit.recipes.models

import timber.log.Timber
import java.util.*
import kotlin.collections.ArrayList


class UserMemStore : UserStore {
    val users = ArrayList<UserModel>()

    override fun findAll(): MutableList<UserModel> {
        return users
    }

    override fun signup(user: UserModel) {
        user.id= getId()
        users.add(user)
        logAll()
    }

    override fun login(user: UserModel): UserModel?{
        var foundUser: UserModel? = users.find { u -> u.email == user.email }
        if (foundUser != null) {
            if (foundUser.password == user.password)
                logAll()
            return foundUser
        }
        return null
    }

    override fun checkPassword(user: UserModel): Boolean {
        var foundUser: UserModel? = users.find { u -> u.email == user.email }
        if (foundUser != null) {
            if (foundUser.password == user.password)
                return true
        }
        return false
    }

    override fun deleteUser(user: UserModel?) {
        users.remove(user)
    }

    fun logAll() {
        users.forEach{ Timber.i("${it}") }
    }
}