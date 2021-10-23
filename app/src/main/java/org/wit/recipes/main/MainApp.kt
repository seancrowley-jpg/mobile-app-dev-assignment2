package org.wit.recipes.main

import android.app.Application
import org.wit.recipes.models.*
import timber.log.Timber
import timber.log.Timber.i

class MainApp : Application() {

    //val recipes = ArrayList<RecipeModel>()
    lateinit var recipes: RecipeStore
    val users = UserMemStore()


    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        i("RecipeApp started")
        //recipes = RecipeMemStore()
        recipes = RecipeJSONStore(applicationContext)
        //recipes.add(RecipeModel("One", "About one..."))
        //recipes.add(RecipeModel("Two", "About two..."))
        //recipes.add(RecipeModel("Three", "About three..."))
    }
}