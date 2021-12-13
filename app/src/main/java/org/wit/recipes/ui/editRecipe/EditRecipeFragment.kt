package org.wit.recipes.ui.editRecipe

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import org.wit.recipes.R
import org.wit.recipes.adapters.IngredientAdapter
import org.wit.recipes.adapters.IngredientListener
import org.wit.recipes.adapters.StepListener
import org.wit.recipes.adapters.StepsAdapter
import org.wit.recipes.databinding.FragmentEditRecipeBinding
import org.wit.recipes.helpers.showImagePicker
import org.wit.recipes.models.RecipeModel
import timber.log.Timber
import java.io.File

class EditRecipeFragment : Fragment(), IngredientListener, StepListener {

    private var _fragBinding: FragmentEditRecipeBinding? = null
    private val fragBinding get() = _fragBinding!!
    var recipe = RecipeModel()
    private lateinit var imageIntentLauncher : ActivityResultLauncher<Intent>
    private lateinit var cameraIntentLauncher : ActivityResultLauncher<Intent>
    private lateinit var photoFile: File
    private var FILE_NAME = "photo"
    private lateinit var editRecipeViewModel: EditRecipeViewModel
    private val args by navArgs<EditRecipeFragmentArgs>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        registerImagePickerCallback()
        registerCameraCallback()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _fragBinding = FragmentEditRecipeBinding.inflate(inflater, container, false)
        val root = fragBinding.root

        editRecipeViewModel = ViewModelProvider(this).get(EditRecipeViewModel::class.java)
        editRecipeViewModel.observableStatus.observe(viewLifecycleOwner, Observer {
                status -> status?.let { render(status) }

        })
        editRecipeViewModel.observableRecipe.observe(viewLifecycleOwner, Observer {
                recipe -> recipe?.let { renderRecipe(recipe)}
        })
        fragBinding.recyclerView.setLayoutManager(LinearLayoutManager(activity))
        fragBinding.stepsRecyclerView.setLayoutManager(LinearLayoutManager(activity))

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

    private fun render(status: Boolean) {
        when (status) {
            true -> {
                view?.let {
                    findNavController().popBackStack()
                }
            }
            false -> Toast.makeText(context,getString(R.string.recipeError), Toast.LENGTH_LONG).show()
        }
    }

    private fun renderRecipe(recipe: RecipeModel) {
        fragBinding.recipevm = editRecipeViewModel
        Picasso.get().load(recipe.image).into(fragBinding.recipeImage)
        fragBinding.recyclerView.adapter = IngredientAdapter(recipe.ingredients, this)
        fragBinding.stepsRecyclerView.adapter = StepsAdapter(recipe.steps, this)
    }

    fun setButtonListener(layout: FragmentEditRecipeBinding) {
        layout.btnAdd.setOnClickListener() {
            recipe.id = args.recipeid
            recipe.name = layout.recipeName.text.toString()
            recipe.description = layout.recipeDescription.text.toString()
            recipe.meal = layout.mealText.text.toString()
            recipe.image = fragBinding.recipevm?.observableRecipe!!.value!!.image
            Timber.i("Steps ${recipe.steps}")
            if (recipe.name.isEmpty()) {
                layout.recipeName.requestFocus();
                layout.recipeName.setError("Please enter a Name for the recipe");
            }
            else if (recipe.meal.contentEquals("What type of meal is it?")){
                Snackbar.make(it,"Please pick a meal type", Snackbar.LENGTH_LONG).show()
            }
            else {
                Timber.i("Recipe: $recipe")
                editRecipeViewModel.updateRecipe(recipe)
            }
        }
        layout.btnAddIngredient.setOnClickListener() {
            recipe.ingredients.add(fragBinding.ingredientText.text.toString())
            recipe.ingredients = fragBinding.recipevm?.observableRecipe!!.value!!.ingredients
            Timber.i("ingredients ${fragBinding.recipevm?.observableRecipe!!.value!!.ingredients}")
            fragBinding.recyclerView.adapter = IngredientAdapter(fragBinding.recipevm?.observableRecipe!!.value!!.ingredients,this)
            fragBinding.recyclerView.adapter?.notifyDataSetChanged()
        }
        layout.btnAddStep.setOnClickListener() {
            fragBinding.recipevm?.observableRecipe!!.value!!.steps.add(fragBinding.stepsText.text.toString())
            recipe.steps = fragBinding.recipevm?.observableRecipe!!.value!!.steps
            Timber.i("Steps View ${fragBinding.recipevm?.observableRecipe!!.value!!.steps}")
            Timber.i("Steps ${recipe.steps}")
            fragBinding.stepsRecyclerView.adapter = StepsAdapter(fragBinding.recipevm?.observableRecipe!!.value!!.steps, this)
            fragBinding.stepsRecyclerView.adapter?.notifyDataSetChanged()
        }
        layout.chooseImage.setOnClickListener {
            showImagePicker(imageIntentLauncher)
        }

        layout.btnTakePic.setOnClickListener {
            val takePicIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val context = requireContext()
            photoFile = getPhotoFile(FILE_NAME)
            val fileProvider = FileProvider.getUriForFile(context, "org.wit.recipes.fileprovider", photoFile)
            takePicIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)
            if(takePicIntent.resolveActivity(context.packageManager) != null)
                cameraIntentLauncher.launch(takePicIntent)
            else
                Toast.makeText(context, "Cant open camera", Toast.LENGTH_SHORT)

        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_recipe, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.onNavDestinationSelected(item,
            requireView().findNavController()) || super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _fragBinding = null
    }

    override fun onResume() {
        super.onResume()
        editRecipeViewModel.getRecipe(args.recipeid)
    }

    override fun onIngredientBtnClick(ingredient: String?) {
        recipe.ingredients.remove(ingredient)
        fragBinding.recyclerView.adapter?.notifyDataSetChanged()
    }

    override fun onStepBtnClick(step: String?) {
        recipe.steps.remove(step)
        fragBinding.stepsRecyclerView.adapter?.notifyDataSetChanged()
    }

    private fun getPhotoFile(fileName: String): File {
        val storageDirectory = context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(fileName, ".jpg", storageDirectory)
    }

    private fun registerImagePickerCallback() {
        imageIntentLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult())
            { result ->
                when(result.resultCode){
                    AppCompatActivity.RESULT_OK -> {
                        if (result.data != null) {
                            Timber.i("Got Result ${result.data!!.data}")
                            fragBinding.recipevm?.observableRecipe!!.value!!.image = result.data!!.data!!
                            Picasso.get().load(fragBinding.recipevm?.observableRecipe!!.value!!.image).into(fragBinding.recipeImage)
                            fragBinding.chooseImage.setText(R.string.change_recipe_image)
                        }
                    }
                    AppCompatActivity.RESULT_CANCELED -> { } else -> { }
                }
            }
    }

    private fun registerCameraCallback() {
        cameraIntentLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult())
            { result ->
                when(result.resultCode){
                    AppCompatActivity.RESULT_OK -> {
                        Timber.i("Got Result ${result.data!!.data}")
                        fragBinding.recipevm?.observableRecipe!!.value!!.image = photoFile.toUri()
                        Timber.i("Image ${recipe.image}")
                        Picasso.get().load(fragBinding.recipevm?.observableRecipe!!.value!!.image).into(fragBinding.recipeImage)
                        fragBinding.chooseImage.setText(R.string.change_recipe_image)
                    }
                    AppCompatActivity.RESULT_CANCELED -> { } else -> { }
                }
            }
    }

}