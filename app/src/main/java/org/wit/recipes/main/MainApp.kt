package org.wit.recipes.main

import android.app.Application
import org.wit.recipes.models.*
import timber.log.Timber
import timber.log.Timber.i

class MainApp : Application() {

    //val recipes = ArrayList<RecipeModel>()
    lateinit var recipes: RecipeStore
    lateinit var users : UserStore


    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        i("RecipeApp started")
        //recipes = RecipeMemStore()
        recipes = RecipeJSONStore(applicationContext)
        //users = UserMemStore()
        users = UserJSONStore(applicationContext)
        //users.signup(UserModel(1,"Homer","homer@simpson.com","password"))
    }
}