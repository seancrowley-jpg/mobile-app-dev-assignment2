package org.wit.recipes.firebase

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import org.wit.recipes.helpers.readImageFromPath
import org.wit.recipes.models.RecipeModel
import org.wit.recipes.models.RecipeStore
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.io.File

object FirebaseDBManager : RecipeStore {

    var database: DatabaseReference = FirebaseDatabase.getInstance("https://recipes-app-2f164-default-rtdb.europe-west1.firebasedatabase.app/").reference
    var storage = FirebaseStorage.getInstance().reference

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

    override fun create(firebaseUser: MutableLiveData<FirebaseUser>, recipe: RecipeModel, context: Context) {
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
        updateImage(recipe,uid, context)
    }

    override fun update(userid: String, recipeId: String, recipe: RecipeModel, context: Context) {
        val recipeValues = recipe.toMap()
        val childUpdate : MutableMap<String, Any?> = HashMap()
        childUpdate["recipes/$recipeId"] = recipeValues
        childUpdate["user-recipes/$userid/$recipeId"] = recipeValues
        database.updateChildren(childUpdate)
        updateImage(recipe,userid, context)
    }

    override fun delete(userid: String, recipe : RecipeModel ) {
        val childDelete : MutableMap<String, Any?> = HashMap()
        childDelete["/recipes/${recipe.uid}"] = null
        childDelete["/user-recipes/$userid/${recipe.uid}"] = null
        database.updateChildren(childDelete)
        Timber.i("RecipeId ${recipe.uid} , User $userid")
        val fileName = File(recipe.image)
        val imageName = fileName.getName()
        var imageRef = storage.child("$userid/${recipe.uid}/$imageName")
        Timber.i("$imageRef")
        imageRef.delete()
    }

    override fun deleteAll(userid: String) {
        val childDelete : MutableMap<String, Any?> = HashMap()
        childDelete["/user-recipes/$userid/"] = null
        database.updateChildren(childDelete)
    }

    fun updateImage(recipe: RecipeModel, userid: String, context: Context) {
        if (recipe.image != "") {
            val fileName = File(recipe.image)
            val imageName = fileName.getName()

            var imageRef = storage.child("$userid/${recipe.uid}/$imageName")
            val baos = ByteArrayOutputStream()
            val bitmap = readImageFromPath(context, recipe.image)

            bitmap?.let {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                val data = baos.toByteArray()
                val uploadTask = imageRef.putBytes(data)
                uploadTask.addOnFailureListener {
                    println(it.message)
                }.addOnSuccessListener { taskSnapshot ->
                    taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                        recipe.image = it.toString()
                        database.child("recipes").child(recipe.uid!!).child("image").setValue(recipe.image)
                        database.child("user-recipes").child(userid).child(recipe.uid!!).child("image").setValue(recipe.image)
                    }
                }
            }
        }
    }
}