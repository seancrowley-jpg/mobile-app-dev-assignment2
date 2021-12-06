package org.wit.recipes

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import org.wit.recipes.databinding.FragmentRecipeBinding
import org.wit.recipes.main.MainApp
import org.wit.recipes.models.RecipeModel


class RecipeFragment : Fragment() {
    lateinit var app: MainApp
    private var _fragBinding: FragmentRecipeBinding? = null
    private val fragBinding get() = _fragBinding!!
    var recipe = RecipeModel()
    var edit = false

    override fun onCreate(savedInstanceState: Bundle?) {
        app = activity?.application as MainApp
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _fragBinding = FragmentRecipeBinding.inflate(inflater, container, false)
        val root = fragBinding.root
        activity?.title = getString(R.string.create_recipe_toolbar)

        var meals = resources.getStringArray(R.array.meals)
        fragBinding.mealPicker.minValue = 0
        fragBinding.mealPicker.maxValue= 3
        fragBinding.mealPicker.value = 0
        fragBinding.mealPicker.displayedValues = meals

        fragBinding.mealPicker.setOnValueChangedListener{ _, _, newVal ->
            fragBinding.mealText.setText(meals[newVal])
        }

        setButtonListener(fragBinding)
        return root;
    }

    fun setButtonListener(layout: FragmentRecipeBinding) {
        layout.btnAdd.setOnClickListener() {
            recipe.name = layout.recipeName.text.toString()
            recipe.description = layout.recipeDescription.text.toString()
            recipe.meal = layout.mealText.text.toString()
            if (recipe.name.isEmpty()) {
                layout.recipeName.requestFocus();
                layout.recipeName.setError("Please enter a Name for the recipe");
            }
            else if (recipe.meal.contentEquals("What type of meal is it?")){
                Snackbar.make(it,"Please pick a meal type", Snackbar.LENGTH_LONG).show()
            }
            else {
                if (edit) {
                    app.recipes.create(recipe.copy())
                } else {
                    app.recipes.create(recipe.copy())
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _fragBinding = null
    }

    override fun onResume() {
        super.onResume()

    }

    companion object {
        @JvmStatic
        fun newInstance() =
            RecipeFragment().apply {
                arguments = Bundle().apply {}
            }
    }
}