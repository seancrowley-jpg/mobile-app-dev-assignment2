package org.wit.recipes.fragments

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import com.squareup.picasso.Picasso
import org.wit.recipes.R
import org.wit.recipes.adapters.IngredientAdapter
import org.wit.recipes.adapters.IngredientListener
import org.wit.recipes.adapters.StepListener
import org.wit.recipes.adapters.StepsAdapter
import org.wit.recipes.databinding.FragmentRecipeBinding
import org.wit.recipes.databinding.FragmentViewRecipeBinding
import org.wit.recipes.main.MainApp
import org.wit.recipes.models.RecipeModel
import java.io.File

class ViewRecipeFragment : Fragment(), IngredientListener, StepListener {
    lateinit var app: MainApp
    private var _fragBinding: FragmentViewRecipeBinding? = null
    private val fragBinding get() = _fragBinding!!
    var recipe = RecipeModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        app = activity?.application as MainApp
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _fragBinding = FragmentViewRecipeBinding.inflate(inflater, container, false)
        val root = fragBinding.root
        activity?.title = getString(R.string.view_recipe_toolbar)
        fragBinding.recipeViewName.setText(recipe.name)
        fragBinding.recipeViewDescription.setText(recipe.description)
        fragBinding.recipeMealView.setText(recipe.meal)
        Picasso.get().load(recipe.image).into(fragBinding.recipeViewImage)
        fragBinding.ingredientRecyclerRecipeView.layoutManager = LinearLayoutManager(activity)
        fragBinding.ingredientRecyclerRecipeView.adapter = IngredientAdapter(recipe.ingredients, this)
        fragBinding.stepsRecyclerRecipeView.layoutManager = LinearLayoutManager(activity)
        fragBinding.stepsRecyclerRecipeView.adapter = StepsAdapter(recipe.steps, this)
        return inflater.inflate(R.layout.fragment_view_recipe, container, false)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_recipe, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.onNavDestinationSelected(item,
            requireView().findNavController()) || super.onOptionsItemSelected(item)
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            RecipeFragment().apply {
                arguments = Bundle().apply {}
            }
    }

    override fun onIngredientBtnClick(ingredient: String?) {

    }

    override fun onStepBtnClick(step: String?) {

    }
}