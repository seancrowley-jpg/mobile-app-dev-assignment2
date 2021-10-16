package org.wit.recipes.activities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import org.wit.recipes.R
import org.wit.recipes.databinding.ActivityRecipeBinding
import org.wit.recipes.helpers.showImagePicker
import org.wit.recipes.main.MainApp
import org.wit.recipes.models.RecipeModel
import timber.log.Timber.i

class RecipeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRecipeBinding
    private lateinit var imageIntentLauncher : ActivityResultLauncher<Intent>
    var recipe = RecipeModel()
    lateinit var app: MainApp


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecipeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        var edit = false
        binding.toolbarAdd.title = title
        setSupportActionBar(binding.toolbarAdd)
        registerImagePickerCallback()

        app = application as MainApp

        i("Recipe Activity started..")

        if (intent.hasExtra("recipe_edit")) {
            edit = true
            recipe = intent.extras?.getParcelable("recipe_edit")!!
            binding.recipeName.setText(recipe.name)
            binding.recipeDescription.setText(recipe.description)
            Picasso.get().load(recipe.image).into(binding.recipeImage)
            if (recipe.image != Uri.EMPTY) binding.chooseImage.setText(R.string.change_recipe_image)
            binding.btnAdd.setText(R.string.save_recipe)
        }
        binding.btnAdd.setOnClickListener() {
            recipe.name = binding.recipeName.text.toString()
            recipe.description = binding.recipeDescription.text.toString()
            if (recipe.name.isEmpty()) {
                Snackbar.make(it,R.string.enter_recipe_name, Snackbar.LENGTH_LONG)
                    .show()
            }
            else {
                if (edit) {
                    app.recipes.update(recipe.copy())
                } else {
                    app.recipes.create(recipe.copy())
                }
            }
            setResult(RESULT_OK)
            finish()
        }
        binding.chooseImage.setOnClickListener {
            showImagePicker(imageIntentLauncher)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_recipe, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_cancel -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun registerImagePickerCallback() {
        imageIntentLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult())
            { result ->
                when(result.resultCode){
                    RESULT_OK -> {
                        if (result.data != null) {
                            i("Got Result ${result.data!!.data}")
                            recipe.image = result.data!!.data!!
                            Picasso.get().load(recipe.image).into(binding.recipeImage)
                            binding.chooseImage.setText(R.string.change_recipe_image)
                        }
                    }
                    RESULT_CANCELED -> { } else -> { }
                }
            }
    }
}