package org.wit.recipes.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import org.wit.recipes.adapters.RecipeAdapter
import org.wit.recipes.adapters.RecipeListener
import org.wit.recipes.databinding.FragmentRecipeListBinding
import org.wit.recipes.main.MainApp
import org.wit.recipes.models.RecipeModel

class RecipeListFragment : Fragment(), RecipeListener {
    lateinit var app: MainApp
    private var _fragBinding: FragmentRecipeListBinding? = null
    private val fragBinding get() = _fragBinding!!
    //private lateinit var adapter: RecipeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        app = activity?.application as MainApp
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _fragBinding = FragmentRecipeListBinding.inflate(inflater, container, false)
        val root = fragBinding.root
        activity?.title = "Welcome ${app.currentUser?.name}"

        fragBinding.recyclerView.setLayoutManager(LinearLayoutManager(activity))
        fragBinding.recyclerView.adapter = RecipeAdapter(app.recipes.findAll(),this)

        return root
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
        TODO("Not yet implemented")
    }

    override fun onEditClick(recipe: RecipeModel) {
        TODO("Not yet implemented")
    }
}