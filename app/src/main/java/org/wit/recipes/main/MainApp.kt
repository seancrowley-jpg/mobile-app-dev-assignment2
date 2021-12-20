package org.wit.recipes.main

import android.app.Application
import org.wit.recipes.models.*
import timber.log.Timber
import timber.log.Timber.i

class MainApp : Application() {

    //val recipes = ArrayList<RecipeModel>()
    //lateinit var recipes: RecipeStore
    //lateinit var users : UserStore
    //var currentUser: UserModel? = null


    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        i("RecipeApp started")
        //recipes = RecipeManager()
        //recipes = RecipeJSONStore(applicationContext)
        //users = UserMemStore()
        //users = UserJSONStore(applicationContext)
        //currentUser
    }
}