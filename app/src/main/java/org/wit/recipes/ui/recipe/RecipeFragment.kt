package org.wit.recipes.ui.recipe

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.*
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import org.wit.recipes.R
import org.wit.recipes.adapters.*
import org.wit.recipes.databinding.FragmentRecipeBinding
import org.wit.recipes.helpers.showImagePicker
import org.wit.recipes.models.RecipeModel
import org.wit.recipes.ui.auth.LoggedInViewModel
import timber.log.Timber
import java.io.File


class RecipeFragment : Fragment(), IngredientListener, StepListener {

    private var _fragBinding: FragmentRecipeBinding? = null
    private val fragBinding get() = _fragBinding!!
    var recipe = RecipeModel()
    private lateinit var imageIntentLauncher : ActivityResultLauncher<Intent>
    private lateinit var cameraIntentLauncher : ActivityResultLauncher<Intent>
    private lateinit var photoFile: File
    private var FILE_NAME = "photo"
    private lateinit var recipeViewModel: RecipeViewModel
    private val loggedInViewModel : LoggedInViewModel by activityViewModels()

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
        _fragBinding = FragmentRecipeBinding.inflate(inflater, container, false)
        val root = fragBinding.root

        recipeViewModel = ViewModelProvider(this).get(RecipeViewModel::class.java)
        recipeViewModel.observableStatus.observe(viewLifecycleOwner, Observer {
                status -> status?.let { render(status) }
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
        fragBinding.recyclerView.adapter = IngredientAdapter(recipe.ingredients, this)
        fragBinding.stepsRecyclerView.adapter = StepsAdapter(recipe.steps, this)
        when (status) {
            true -> {
                view?.let {
                    findNavController().popBackStack()
                }
            }
            false -> Toast.makeText(context,getString(R.string.recipeError),Toast.LENGTH_LONG).show()
        }
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
                recipeViewModel.addRecipe(loggedInViewModel.liveFirebaseUser,recipe)
            }
        }
        layout.btnAddIngredient.setOnClickListener() {
            recipe.ingredients.add(fragBinding.ingredientText.text.toString())
            Timber.i("ingredients ${recipe.ingredients}")
            fragBinding.recyclerView.adapter = IngredientAdapter(recipe.ingredients,this)
            fragBinding.recyclerView.adapter?.notifyDataSetChanged()
        }
        layout.btnAddStep.setOnClickListener() {
            recipe.steps.add(fragBinding.stepsText.text.toString())
            Timber.i("Steps ${recipe.steps}")
            fragBinding.stepsRecyclerView.adapter = StepsAdapter(recipe.steps, this)
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
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            RecipeFragment().apply {
                arguments = Bundle().apply {}
            }
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
                            recipe.image = result.data!!.data.toString()
                            Picasso.get().load(recipe.image).into(fragBinding.recipeImage)
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
                        recipe.image = photoFile.toString()
                        Timber.i("Image ${recipe.image}")
                        Picasso.get().load(recipe.image).into(fragBinding.recipeImage)
                        fragBinding.chooseImage.setText(R.string.change_recipe_image)
                    }
                    AppCompatActivity.RESULT_CANCELED -> { } else -> { }
                }
            }
    }
}