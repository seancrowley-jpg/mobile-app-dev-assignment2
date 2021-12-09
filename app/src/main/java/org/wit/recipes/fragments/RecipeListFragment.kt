package org.wit.recipes.fragments

import android.os.Bundle
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import org.wit.recipes.R
import org.wit.recipes.adapters.RecipeAdapter
import org.wit.recipes.adapters.RecipeListener
import org.wit.recipes.databinding.FragmentRecipeListBinding
import org.wit.recipes.main.MainApp
import org.wit.recipes.models.RecipeModel

class RecipeListFragment : Fragment(), RecipeListener {
    lateinit var app: MainApp
    private var _fragBinding: FragmentRecipeListBinding? = null
    private val fragBinding get() = _fragBinding!!
    private lateinit var adapter: RecipeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        app = activity?.application as MainApp
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _fragBinding = FragmentRecipeListBinding.inflate(inflater, container, false)
        val root = fragBinding.root
        activity?.title = "Welcome ${app.currentUser?.name}"

        fragBinding.recyclerView.layoutManager = LinearLayoutManager(activity)
        adapter = RecipeAdapter(app.recipes.findAll(),this)
        fragBinding.recyclerView.adapter = adapter

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
                adapter.filter.filter(p0)
                //if(p0.isNullOrBlank()) loadRecipes()
                return true
            }

        })
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.item_delete_all){
            app.recipes.deleteAll()
            fragBinding.recyclerView.adapter?.notifyDataSetChanged()
        }
        return NavigationUI.onNavDestinationSelected(item,
            requireView().findNavController()) || super.onOptionsItemSelected(item)
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            RecipeListFragment().apply {
                arguments = Bundle().apply { }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _fragBinding = null
    }

    override fun onRecipeClick(recipe: RecipeModel) {
        TODO("Not yet implemented")
    }

    override fun onDeleteClick(recipe: RecipeModel) {
        app.recipes.delete(recipe)
        fragBinding.recyclerView.adapter?.notifyDataSetChanged()
    }

    override fun onEditClick(recipe: RecipeModel) {
        TODO("Not yet implemented")
    }
}