package org.wit.recipes.ui.viewRecipe

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ViewRecipeViewModel: ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "This is the ViewRecipe Fragment"
    }
    val text: LiveData<String> = _text
}