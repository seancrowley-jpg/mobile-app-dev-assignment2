package org.wit.recipes.ui.recipe

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RecipeViewModel : ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "This is the Recipe Fragment"
    }
    val text: LiveData<String> = _text
}