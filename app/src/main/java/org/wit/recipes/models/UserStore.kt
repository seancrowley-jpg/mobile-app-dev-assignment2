package org.wit.recipes.models

interface UserStore {
    fun findAll(): List<UserModel>
    fun signup(user: UserModel)
    fun login(user: UserModel): UserModel?
    fun checkPassword(user: UserModel): Boolean
    fun deleteUser(user: UserModel?)
}