package org.wit.recipes.ui.viewRecipe

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import com.squareup.picasso.Picasso
import org.wit.recipes.R
import org.wit.recipes.adapters.IngredientAdapter
import org.wit.recipes.adapters.IngredientListener
import org.wit.recipes.adapters.StepListener
import org.wit.recipes.adapters.StepsAdapter
import org.wit.recipes.databinding.FragmentViewRecipeBinding
import org.wit.recipes.ui.recipe.RecipeFragment
import org.wit.recipes.main.MainApp
import org.wit.recipes.models.RecipeModel
import org.wit.recipes.ui.auth.LoggedInViewModel
import org.wit.recipes.ui.recipeList.RecipeListViewModel
import timber.log.Timber

class ViewRecipeFragment : Fragment(), IngredientListener, StepListener {
    private var _fragBinding: FragmentViewRecipeBinding? = null
    private val fragBinding get() = _fragBinding!!
    private lateinit var viewRecipeViewModel: ViewRecipeViewModel
    private val args by navArgs<ViewRecipeFragmentArgs>()
    private val loggedInViewModel : LoggedInViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _fragBinding = FragmentViewRecipeBinding.inflate(inflater, container, false)
        val root = fragBinding.root
        fragBinding.ingredientRecyclerRecipeView.layoutManager = LinearLayoutManager(activity)
        fragBinding.stepsRecyclerRecipeView.layoutManager = LinearLayoutManager(activity)

        viewRecipeViewModel = ViewModelProvider(this).get(ViewRecipeViewModel::class.java)
        viewRecipeViewModel.observableRecipe.observe(viewLifecycleOwner, Observer {
                recipe -> recipe?.let { render(recipe)}
        })
        return root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_recipe, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.onNavDestinationSelected(item,
            requireView().findNavController()) || super.onOptionsItemSelected(item)
    }

    private fun render(recipe: RecipeModel) {
        fragBinding.recipevm = viewRecipeViewModel
        fragBinding.ingredientRecyclerRecipeView.adapter = IngredientAdapter(recipe.ingredients, this)
        fragBinding.stepsRecyclerRecipeView.adapter = StepsAdapter(recipe.steps, this)
        if (recipe.image != "")
            Picasso.get().load(recipe.image).into(fragBinding.recipeViewImage)
    }

    override fun onResume() {
        super.onResume()
        viewRecipeViewModel.getRecipe(args.recipeid)

    }

    override fun onIngredientBtnClick(ingredient: String?) {

    }

    override fun onStepBtnClick(step: String?) {

    }
}