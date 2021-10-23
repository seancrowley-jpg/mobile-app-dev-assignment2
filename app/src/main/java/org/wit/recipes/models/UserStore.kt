package org.wit.recipes.models

interface UserStore {
    fun findAll(): List<UserModel>
    fun signup(user: UserModel)
    fun login(user: UserModel): Boolean
}