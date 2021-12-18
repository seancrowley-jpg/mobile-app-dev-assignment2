package org.wit.recipes.firebase

import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import org.wit.recipes.models.RecipeModel
import org.wit.recipes.models.RecipeStore
import timber.log.Timber

object FirebaseDBManager : RecipeStore {

    var database: DatabaseReference = FirebaseDatabase.getInstance("https://recipes-app-2f164-default-rtdb.europe-west1.firebasedatabase.app/").reference

    override fun findAll(recipeList: MutableLiveData<List<RecipeModel>>) {
        database.child("recipes")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    Timber.i("Firebase error : ${error.message}")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val localList = ArrayList<RecipeModel>()
                    val children = snapshot.children
                    children.forEach {
                        val recipe = it.getValue(RecipeModel::class.java)
                        localList.add(recipe!!)
                    }
                    database.child("recipes")
                        .removeEventListener(this)

                    recipeList.value = localList
                }
            })
    }

    override fun findAll(userid: String, recipeList: MutableLiveData<List<RecipeModel>>) {
        database.child("user-recipes").child(userid)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    Timber.i("Firebase error : ${error.message}")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val localList = ArrayList<RecipeModel>()
                    val children = snapshot.children
                    children.forEach {
                        val recipe = it.getValue(RecipeModel::class.java)
                        localList.add(recipe!!)
                    }
                    database.child("user-recipes").child(userid)
                        .removeEventListener(this)

                    recipeList.value = localList
                }
            })    }

    override fun findById(userid: String, recipeId: String, recipe: MutableLiveData<RecipeModel>) {
        database.child("user-recipes").child(userid)
            .child(recipeId).get().addOnSuccessListener {
                recipe.value = it.getValue(RecipeModel::class.java)
                Timber.i("firebase Got value ${it.value}")
            }.addOnFailureListener{
                Timber.e("firebase Error getting data $it")
            }
    }

    override fun findRecipeById(recipeId: String, recipe: MutableLiveData<RecipeModel>) {
        database.child("recipes")
            .child(recipeId).get().addOnSuccessListener {
                recipe.value = it.getValue(RecipeModel::class.java)
                Timber.i("firebase Got value ${it.value}")
            }.addOnFailureListener{
                Timber.e("firebase Error getting data $it")
            }
    }

    override fun create(firebaseUser: MutableLiveData<FirebaseUser>, recipe: RecipeModel) {
        Timber.i("Firebase DB Reference : $database")
        val uid = firebaseUser.value!!.uid
        val key = database.child("recipes").push().key
        if (key == null) {
            Timber.i("Firebase Error : Key Empty")
            return
        }
        recipe.uid = key
        val recipeValues = recipe.toMap()
        val childAdd = HashMap<String, Any>()
        childAdd["/recipes/$key"] = recipeValues
        childAdd["/user-recipes/$uid/$key"] = recipeValues
        database.updateChildren(childAdd)
    }

    override fun update(userid: String, recipeId: String, recipe: RecipeModel) {
        val recipeValues = recipe.toMap()
        val childUpdate : MutableMap<String, Any?> = HashMap()
        childUpdate["recipes/$recipeId"] = recipeValues
        childUpdate["user-recipes/$userid/$recipeId"] = recipeValues
        database.updateChildren(childUpdate)
    }

    override fun delete(userid: String, recipeId: String) {
        val childDelete : MutableMap<String, Any?> = HashMap()
        childDelete["/recipes/$recipeId"] = null
        childDelete["/user-recipes/$userid/$recipeId"] = null
        database.updateChildren(childDelete)
    }

    override fun deleteAll(userid: String) {
        val childDelete : MutableMap<String, Any?> = HashMap()
        childDelete["/user-recipes/$userid/"] = null
        database.updateChildren(childDelete)
    }
}