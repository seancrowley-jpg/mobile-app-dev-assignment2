package org.wit.recipes.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.EditorInfo
import android.widget.SearchView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isEmpty
import androidx.recyclerview.widget.LinearLayoutManager
import org.wit.recipes.R
import org.wit.recipes.adapters.RecipeAdapter
import org.wit.recipes.adapters.RecipeListener
import org.wit.recipes.databinding.ActivityRecipeListBinding
import org.wit.recipes.main.MainApp
import org.wit.recipes.models.RecipeModel


class RecipeListActivity : AppCompatActivity(), RecipeListener {

    lateinit var app: MainApp

    private lateinit var binding: ActivityRecipeListBinding
    private lateinit var refreshIntentLauncher : ActivityResultLauncher<Intent>
    private lateinit var adapter: RecipeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerRefreshCallback()
        binding = ActivityRecipeListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        app = application as MainApp

        val layoutManager = LinearLayoutManager(this)
        binding.recyclerView.layoutManager = layoutManager
        loadRecipes()
        binding.recyclerView.adapter = adapter
        binding.toolbar.title = "Welcome ${app.currentUser?.name}"
        setSupportActionBar(binding.toolbar)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
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
     return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_add -> {
                val launcherIntent = Intent(this, RecipeActivity::class.java)
                refreshIntentLauncher.launch(launcherIntent)
            }
            R.id.item_logout -> {
                val launcherIntent = Intent(this, LoginActivity::class.java)
                refreshIntentLauncher.launch(launcherIntent)
            }
            R.id.item_delete_all -> {
                app.recipes.deleteAll()
                binding.recyclerView.adapter?.notifyDataSetChanged()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onRecipeClick(recipe: RecipeModel) {
        val launcherIntent = Intent(this, RecipeActivity::class.java)
        launcherIntent.putExtra("recipe_edit", recipe)
        refreshIntentLauncher.launch(launcherIntent)
    }

    override fun onDeleteClick(recipe: RecipeModel) {
        app.recipes.delete(recipe)
        binding.recyclerView.adapter?.notifyDataSetChanged()
    }

    private fun loadRecipes() {
        showRecipes(app.recipes.findAll())
    }

    fun showRecipes (recipes: MutableList<RecipeModel>) {
        adapter = RecipeAdapter(recipes, this)
        binding.recyclerView.adapter?.notifyDataSetChanged()
    }


    private fun registerRefreshCallback() {
        refreshIntentLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult())
            { loadRecipes() }
    }
}