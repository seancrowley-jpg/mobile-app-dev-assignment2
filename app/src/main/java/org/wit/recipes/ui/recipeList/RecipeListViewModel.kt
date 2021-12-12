package org.wit.recipes.ui.recipeList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RecipeListViewModel : ViewModel(){
    private val _text = MutableLiveData<String>().apply {
        value = "This is the RecipeList Fragment"
    }
    val text: LiveData<String> = _text
}