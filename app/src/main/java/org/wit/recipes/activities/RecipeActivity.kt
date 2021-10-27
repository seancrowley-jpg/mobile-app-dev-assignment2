package org.wit.recipes.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import org.wit.recipes.R
import org.wit.recipes.adapters.*
import org.wit.recipes.databinding.ActivityRecipeBinding
import org.wit.recipes.helpers.showImagePicker
import org.wit.recipes.main.MainApp
import org.wit.recipes.models.RecipeModel
import timber.log.Timber.i
import java.io.File

class RecipeActivity : AppCompatActivity(), IngredientListener, StepListener {
    private lateinit var binding: ActivityRecipeBinding
    private lateinit var imageIntentLauncher : ActivityResultLauncher<Intent>
    private lateinit var cameraIntentLauncher : ActivityResultLauncher<Intent>
    private lateinit var photoFile: File
    private var FILE_NAME = "photo"
    var recipe = RecipeModel()
    lateinit var app: MainApp


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecipeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var edit = false

        val layoutManager = LinearLayoutManager(this)
        val stepsLayoutManager = LinearLayoutManager(this)
        binding.recyclerView.layoutManager = layoutManager
        binding.stepsRecyclerView.layoutManager = stepsLayoutManager
        binding.recyclerView.adapter = IngredientAdapter(recipe.ingredients, this)
        binding.stepsRecyclerView.adapter = StepsAdapter(recipe.steps, this )

        binding.toolbarAdd.title = title
        setSupportActionBar(binding.toolbarAdd)
        registerImagePickerCallback()
        registerCameraCallback()
        var meals = resources.getStringArray(R.array.meals)
        binding.mealPicker.minValue = 0
        binding.mealPicker.maxValue= 2
        binding.mealPicker.value = 0
        binding.mealPicker.displayedValues = meals

        binding.mealPicker.setOnValueChangedListener{ _, _, newVal ->
            binding.mealText.setText(meals[newVal])
        }

        app = application as MainApp

        i("Recipe Activity started..")

        if (intent.hasExtra("recipe_edit")) {
            edit = true
            recipe = intent.extras?.getParcelable("recipe_edit")!!
            binding.recipeName.setText(recipe.name)
            binding.recipeDescription.setText(recipe.description)
            binding.mealText.setText(recipe.meal)
            binding.recyclerView.adapter = IngredientAdapter(recipe.ingredients,this)
            binding.stepsRecyclerView.adapter = StepsAdapter(recipe.steps, this)
            Picasso.get().load(recipe.image).into(binding.recipeImage)
            if (recipe.image != Uri.EMPTY) binding.chooseImage.setText(R.string.change_recipe_image)
            binding.btnAdd.setText(R.string.save_recipe)
        }
        binding.btnAdd.setOnClickListener() {
            recipe.name = binding.recipeName.text.toString()
            recipe.description = binding.recipeDescription.text.toString()
            recipe.meal = binding.mealText.text.toString()
            if (recipe.name.isEmpty() or recipe.meal.contentEquals("What type of meal is it?")) {
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
        binding.btnTakePic.setOnClickListener {
            val takePicIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            photoFile = getPhotoFile(FILE_NAME)
            val fileProvider = FileProvider.getUriForFile(this, "org.wit.recipes.fileprovider", photoFile)
            takePicIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)
            if(takePicIntent.resolveActivity(this.packageManager) != null)
                cameraIntentLauncher.launch(takePicIntent)
            else
                Toast.makeText(this, "Cant open camera", Toast.LENGTH_SHORT)

        }
        binding.btnAddIngredient.setOnClickListener() {
            recipe.ingredients.add(binding.ingredientText.text.toString())
            i("ingredients ${recipe.ingredients}")
            binding.recyclerView.adapter = IngredientAdapter(recipe.ingredients,this)
            binding.recyclerView.adapter?.notifyDataSetChanged()
        }
        binding.btnAddStep.setOnClickListener() {
            recipe.steps.add(binding.stepsText.text.toString())
            i("Steps ${recipe.steps}")
            binding.stepsRecyclerView.adapter = StepsAdapter(recipe.steps, this)
            binding.stepsRecyclerView.adapter?.notifyDataSetChanged()
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

    override fun onIngredientBtnClick(ingredient :String?) {
        recipe.ingredients.remove(ingredient)
        binding.recyclerView.adapter?.notifyDataSetChanged()
    }

    override fun onStepBtnClick(step: String?) {
        recipe.steps.remove(step)
        binding.stepsRecyclerView.adapter?.notifyDataSetChanged()
    }


    private fun getPhotoFile(fileName: String): File {
        val storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(fileName, ".jpg", storageDirectory)
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

    private fun registerCameraCallback() {
        cameraIntentLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult())
            { result ->
                when(result.resultCode){
                    RESULT_OK -> {
                            i("Got Result ${result.data!!.data}")
                            recipe.image = photoFile.toUri()
                            i("Image ${recipe.image}")
                            Picasso.get().load(recipe.image).into(binding.recipeImage)
                            binding.chooseImage.setText(R.string.change_recipe_image)
                    }
                    RESULT_CANCELED -> { } else -> { }
                }
            }
    }
}