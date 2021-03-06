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
import androidx.fragment.app.activityViewModels
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
import org.wit.recipes.ui.auth.LoggedInViewModel
import timber.log.Timber
import java.io.File

class EditRecipeFragment : Fragment(), IngredientListener, StepListener {

    private var _fragBinding: FragmentEditRecipeBinding? = null
    private val fragBinding get() = _fragBinding!!
    private lateinit var imageIntentLauncher : ActivityResultLauncher<Intent>
    private lateinit var cameraIntentLauncher : ActivityResultLauncher<Intent>
    private lateinit var photoFile: File
    private var FILE_NAME = "photo"
    private lateinit var editRecipeViewModel: EditRecipeViewModel
    private val args by navArgs<EditRecipeFragmentArgs>()
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
        _fragBinding = FragmentEditRecipeBinding.inflate(inflater, container, false)
        val root = fragBinding.root

        editRecipeViewModel = ViewModelProvider(this).get(EditRecipeViewModel::class.java)
        editRecipeViewModel.getRecipe(loggedInViewModel.liveFirebaseUser.value?.uid!!,args.recipeid)
        editRecipeViewModel.observableRecipe.observe(viewLifecycleOwner, Observer {
                recipe -> recipe?.let { render()}
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

    private fun render() {
        fragBinding.recipevm = editRecipeViewModel
        fragBinding.recyclerView.adapter = IngredientAdapter(fragBinding.recipevm?.observableRecipe!!.value!!.ingredients, this)
        fragBinding.stepsRecyclerView.adapter = StepsAdapter(fragBinding.recipevm?.observableRecipe!!.value!!.steps, this)
        if (fragBinding.recipevm?.observableRecipe!!.value!!.image != "")
            Picasso.get().load(fragBinding.recipevm?.observableRecipe!!.value!!.image).into(fragBinding.recipeImage)
    }

    /*private fun renderRecipe(recipe: RecipeModel) {
        fragBinding.recipevm = editRecipeViewModel
        fragBinding.recyclerView.adapter = IngredientAdapter(recipe.ingredients, this)
        fragBinding.stepsRecyclerView.adapter = StepsAdapter(recipe.steps, this)
    }*/

    fun setButtonListener(layout: FragmentEditRecipeBinding) {
        layout.btnAdd.setOnClickListener() {
            fragBinding.recipevm?.observableRecipe!!.value!!.uid = args.recipeid
            fragBinding.recipevm?.observableRecipe!!.value!!.name = layout.recipeName.text.toString()
            fragBinding.recipevm?.observableRecipe!!.value!!.description = layout.recipeDescription.text.toString()
            fragBinding.recipevm?.observableRecipe!!.value!!.meal = layout.mealText.text.toString()
            Timber.i("Image ${fragBinding.recipevm?.observableRecipe!!.value!!}")
            if (layout.recipeName.text!!.isEmpty()) {
                layout.recipeName.requestFocus();
                layout.recipeName.setError("Please enter a Name for the recipe");
            }
            else if (layout.mealText.text.contentEquals("What type of meal is it?")){
                Snackbar.make(it,"Please pick a meal type", Snackbar.LENGTH_LONG).show()
            }
            else {
                Timber.i("Recipe: $fragBinding.recipevm?.observableRecipe!!.value!!")
                editRecipeViewModel.updateRecipe(fragBinding.recipevm?.observableRecipe!!.value!!, loggedInViewModel.liveFirebaseUser.value?.uid!!, args.recipeid, context!!)
                findNavController().popBackStack()
            }
        }
        layout.btnAddIngredient.setOnClickListener() {
            fragBinding.recipevm?.observableRecipe!!.value!!.ingredients.add(fragBinding.ingredientText.text.toString())
            Timber.i("ingredients ${fragBinding.recipevm?.observableRecipe!!.value!!.ingredients}")
            fragBinding.recyclerView.adapter = IngredientAdapter(fragBinding.recipevm?.observableRecipe!!.value!!.ingredients,this)
            fragBinding.recyclerView.adapter?.notifyDataSetChanged()
        }
        layout.btnAddStep.setOnClickListener() {
            fragBinding.recipevm?.observableRecipe!!.value!!.steps.add(fragBinding.stepsText.text.toString())
            Timber.i("Steps View ${fragBinding.recipevm?.observableRecipe!!.value!!.steps}")
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
        //editRecipeViewModel.getRecipe(loggedInViewModel.liveFirebaseUser.value?.uid!!,args.recipeid)
    }

    override fun onIngredientBtnClick(ingredient: String?) {
        fragBinding.recipevm?.observableRecipe!!.value!!.ingredients.remove(ingredient)
        fragBinding.recyclerView.adapter?.notifyDataSetChanged()
    }

    override fun onStepBtnClick(step: String?) {
        fragBinding.recipevm?.observableRecipe!!.value!!.steps.remove(step)
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
                            fragBinding.recipevm?.observableRecipe!!.value!!.image = result.data!!.data.toString()
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
                        fragBinding.recipevm?.observableRecipe!!.value!!.image = photoFile.toUri().toString()
                        Picasso.get().load(fragBinding.recipevm?.observableRecipe!!.value!!.image).into(fragBinding.recipeImage)
                        fragBinding.chooseImage.setText(R.string.change_recipe_image)
                    }
                    AppCompatActivity.RESULT_CANCELED -> { } else -> { }
                }
            }
    }

}