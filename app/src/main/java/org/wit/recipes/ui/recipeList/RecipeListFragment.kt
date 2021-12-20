package org.wit.recipes.ui.recipeList

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.SearchView
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.wit.recipes.R
import org.wit.recipes.adapters.RecipeAdapter
import org.wit.recipes.adapters.RecipeListener
import org.wit.recipes.databinding.FragmentRecipeListBinding
import org.wit.recipes.helpers.createLoader
import org.wit.recipes.helpers.hideLoader
import org.wit.recipes.helpers.showLoader
import org.wit.recipes.main.MainApp
import org.wit.recipes.models.RecipeModel
import org.wit.recipes.ui.auth.LoggedInViewModel
import org.wit.recipes.ui.utils.SwipeToDeleteCallback
import org.wit.recipes.ui.utils.SwipeToEditCallback

class RecipeListFragment : Fragment(), RecipeListener {
    lateinit var app: MainApp
    private var _fragBinding: FragmentRecipeListBinding? = null
    private val fragBinding get() = _fragBinding!!
    //private lateinit var adapter: RecipeAdapter
    private val recipeListViewModel: RecipeListViewModel by activityViewModels()
    private val loggedInViewModel: LoggedInViewModel by activityViewModels()
    lateinit var loader : AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {

        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _fragBinding = FragmentRecipeListBinding.inflate(inflater, container, false)
        val root = fragBinding.root
        loader = createLoader(requireActivity())
        fragBinding.recyclerView.layoutManager = LinearLayoutManager(activity)
        //recipeListViewModel = ViewModelProvider(this).get(RecipeListViewModel::class.java)
        showLoader(loader,"Loading Recipes")
        recipeListViewModel.observableRecipesList.observe(viewLifecycleOwner, Observer {
            recipes -> recipes?.let {
            render(recipes as ArrayList<RecipeModel>)
            hideLoader(loader)}
            checkSwipeRefresh()
        })
        setSwipeRefresh()

        //adapter = RecipeAdapter(app.recipes.findAll(),this)
        //fragBinding.recyclerView.adapter = adapter
        val fab: FloatingActionButton = fragBinding.fab
        fab.setOnClickListener {
            val action = RecipeListFragmentDirections.actionRecipeListFragmentToRecipeFragment()
            findNavController().navigate(action)
        }

        val swipeDeleteHandler = object : SwipeToDeleteCallback(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = fragBinding.recyclerView.adapter as RecipeAdapter
                adapter.removeAt(viewHolder.adapterPosition)
                recipeListViewModel.deleteRecipe(loggedInViewModel.liveFirebaseUser.value?.uid!!,
                    (viewHolder.itemView.tag as RecipeModel))
                hideLoader(loader)
            }
        }
        val itemTouchDeleteHelper = ItemTouchHelper(swipeDeleteHandler)
        itemTouchDeleteHelper.attachToRecyclerView(fragBinding.recyclerView)

        val swipeEditHandler = object : SwipeToEditCallback(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                onEditClick(viewHolder.itemView.tag as RecipeModel)
            }
        }
        val itemTouchEditHelper = ItemTouchHelper(swipeEditHandler)
        itemTouchEditHelper.attachToRecyclerView(fragBinding.recyclerView)

        return root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)
        val searchItem = menu.findItem(R.id.item_search)
        val searchView = searchItem?.actionView as SearchView
        searchView.imeOptions = EditorInfo.IME_ACTION_DONE
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                //adapter.filter.filter(p0)
                //if(p0.isNullOrBlank()) loadRecipes()
                return true
            }

        })

        val item = menu.findItem(R.id.toggleRecipes) as MenuItem
        item.setActionView(R.layout.togglebutton_layout)
        val toggleRecipes: SwitchCompat = item.actionView.findViewById(R.id.toggleButton)
        toggleRecipes.isChecked = false

        toggleRecipes.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) recipeListViewModel.loadAll()
            else recipeListViewModel.load()
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.item_delete_all){
            recipeListViewModel.deleteAllRecipes()
            hideLoader(loader)
        }
        return NavigationUI.onNavDestinationSelected(item,
            requireView().findNavController()) || super.onOptionsItemSelected(item)
    }

    private fun render(recipeList: ArrayList<RecipeModel>) {
        fragBinding.recyclerView.adapter = RecipeAdapter(recipeList, this, recipeListViewModel.readOnly.value!!)
        if (recipeList.isEmpty()) {
            fragBinding.recyclerView.visibility = View.GONE
            fragBinding.recipesNotFound.visibility = View.VISIBLE
        } else {
            fragBinding.recyclerView.visibility = View.VISIBLE
            fragBinding.recipesNotFound.visibility = View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        showLoader(loader,"Loading..")
        loggedInViewModel.liveFirebaseUser.observe(viewLifecycleOwner, Observer { firebaseUser ->
            if (firebaseUser != null) {
                recipeListViewModel.liveFirebaseUser.value = firebaseUser
                recipeListViewModel.load()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _fragBinding = null
    }

    private fun setSwipeRefresh() {
        fragBinding.swiperefresh.setOnRefreshListener {
            fragBinding.swiperefresh.isRefreshing = true
            showLoader(loader, "Loading Recipes")
            if(recipeListViewModel.readOnly.value!!)
                recipeListViewModel.loadAll()
            else
                recipeListViewModel.load()
        }
    }

    private fun checkSwipeRefresh() {
        if (fragBinding.swiperefresh.isRefreshing)
            fragBinding.swiperefresh.isRefreshing = false
    }

    override fun onRecipeClick(recipe: RecipeModel) {
        val action = RecipeListFragmentDirections.actionRecipeListFragmentToViewRecipeFragment(recipe.uid!!)
        findNavController().navigate(action)
    }

    override fun onDeleteClick(recipe: RecipeModel) {
        recipeListViewModel.deleteRecipe(recipeListViewModel.liveFirebaseUser.value?.uid!!,recipe)
        fragBinding.recyclerView.adapter?.notifyDataSetChanged()
    }

    override fun onEditClick(recipe: RecipeModel) {
        val action = RecipeListFragmentDirections.actionRecipeListFragmentToEditRecipeFragment(recipe.uid!!)
        findNavController().navigate(action)
    }
}