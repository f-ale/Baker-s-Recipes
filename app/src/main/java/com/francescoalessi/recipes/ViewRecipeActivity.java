package com.francescoalessi.recipes;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.francescoalessi.recipes.data.Ingredient;
import com.francescoalessi.recipes.data.Recipe;
import com.francescoalessi.recipes.editing.EditRecipeActivity;
import com.francescoalessi.recipes.editing.adapters.IngredientListAdapter;
import com.francescoalessi.recipes.editing.model.EditRecipeViewModel;
import com.francescoalessi.recipes.editing.model.EditRecipeViewModelFactory;
import com.francescoalessi.recipes.utils.RecipeUtils;
import com.francescoalessi.recipes.utils.RequestCodes;

import java.io.IOException;
import java.util.List;

public class ViewRecipeActivity extends AppCompatActivity
{

    private EditRecipeViewModel mEditRecipeViewModel;
    private LiveData<Recipe> mRecipe;
    private LiveData<List<Ingredient>> mIngredientList;
    private RecyclerView mIngredientsRecyclerView;
    private EditText mTotalWeightEditText;
    private IngredientListAdapter mAdapter;
    private int mRecipeId;
    private boolean calculateQuantities = false;
    private ImageView mRecipeThumbnailImageView;
    private TextView mNoIngredientsTextView;
    private TextWatcher mTotalWeightTextWatcher;

    private Recipe mRecipeData;
    private List<Ingredient> mIngredientsData;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_recipe);

        mTotalWeightEditText = findViewById(R.id.et_total_recipe_weight); // TODO: Update weights if total weight is changed and calculate weights button is checked

        mRecipeThumbnailImageView = findViewById(R.id.iv_recipe_thumbnail);
        mRecipeThumbnailImageView.setVisibility(View.INVISIBLE);

        mIngredientsRecyclerView = findViewById(R.id.rv_ingredient_list);
        mAdapter = new IngredientListAdapter(this, false);
        mIngredientsRecyclerView.setAdapter(mAdapter);
        mIngredientsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        Log.d("VIEWRECIPE", "onCreate");

        mNoIngredientsTextView = findViewById(R.id.tv_no_ingredients);

        Intent intent = getIntent();

        if (intent != null)
        {
            mRecipeId = intent.getIntExtra(MainActivity.EXTRA_RECIPE_ID, -1);
            Log.d("VIEWRECIPE", "onCreate intent ID = " + mRecipeId);
        }

        if (mRecipeId == -1 && savedInstanceState != null && !savedInstanceState.isEmpty())
        {
            mRecipeId = savedInstanceState.getInt(MainActivity.EXTRA_RECIPE_ID, -1);
            Log.d("VIEWRECIPE", "onCreate savedInstanceState ID = " + mRecipeId);
        }

        setupUserInterface();

        mTotalWeightTextWatcher = new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {

            }

            @Override
            public void afterTextChanged(Editable editable)
            {
                String text = editable.toString();
                if (!text.equals(""))
                {
                    calculateQuantities = true;

                    if (mTotalWeightEditText.getText() != null)
                    {
                        String weightText = mTotalWeightEditText.getText().toString();
                        if (!weightText.equals(""))
                        {
                            mAdapter.calculateQuantities(Float.parseFloat(weightText));
                        }
                        else
                            mAdapter.calculateQuantities(0);
                    }
                    else
                        mAdapter.calculateQuantities(0);
                }
                else
                {
                    mAdapter.showPercent();
                    calculateQuantities = false;
                }
            }
        };

        mTotalWeightEditText.addTextChangedListener(mTotalWeightTextWatcher);
    }

    private void populateRecipeUI(Recipe recipe)
    {
        if (recipe != null)
        {
            setTitle(recipe.getRecipeName());
            loadThumbImage(recipe);
        }
    }

    private void loadThumbImage(Recipe recipe)
    {
        if (recipe != null)
        {
            Uri uri = recipe.getRecipeImageUri();
            if (uri != null)
            {
                Glide.with(mRecipeThumbnailImageView.getContext()).load(uri)
                        .placeholder(R.drawable.ic_action_pick_image)
                        .centerCrop()
                        .into(mRecipeThumbnailImageView);

                mRecipeThumbnailImageView.setVisibility(View.VISIBLE);
            }
        }
    }

    private void setupUserInterface()
    {
        EditRecipeViewModelFactory factory = new EditRecipeViewModelFactory(this.getApplication(), mRecipeId);
        mEditRecipeViewModel = ViewModelProviders.of(this, factory).get(EditRecipeViewModel.class);

        mRecipe = mEditRecipeViewModel.getRecipeById();
        mRecipe.observe(this, new Observer<Recipe>()
        {
            @Override
            public void onChanged(@Nullable final Recipe recipe)
            {
                populateRecipeUI(recipe);
                mRecipeData = recipe;
            }
        });

        mIngredientList = mEditRecipeViewModel.getRecipeIngredients();
        mIngredientList.observe(this, new Observer<List<Ingredient>>()
        {
            @Override
            public void onChanged(List<Ingredient> ingredients)
            {
                mAdapter.setIngredients(ingredients);
                mIngredientsData = ingredients;
                if (ingredients.size() == 0)
                    mNoIngredientsTextView.setVisibility(View.VISIBLE);
                else
                    mNoIngredientsTextView.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState)
    {
        outState.putInt(MainActivity.EXTRA_RECIPE_ID, mRecipeId);
        Log.d("VIEWRECIPE", "onSave");
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);

        if (mRecipeId == -1 && !savedInstanceState.isEmpty())
        {
            mRecipeId = savedInstanceState.getInt(MainActivity.EXTRA_RECIPE_ID);
            setupUserInterface();
        }

        Log.d("VIEWRECIPE", "onRestore");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        mRecipeId = resultCode;
        Log.d("VIEWRECIPE", "OnActivityResult: mRecipeId = " + mRecipeId);
        setupUserInterface();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.view_recipe_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        if (item.getItemId() == R.id.action_edit)
        {
            Context context = this;

            //start intent
            Intent intent = new Intent(context, EditRecipeActivity.class);
            intent.putExtra(MainActivity.EXTRA_RECIPE_ID, mRecipeId);
            startActivityForResult(intent, RequestCodes.EDIT_RECIPE_REQUEST); // TODO: Change request code
            return true;
        }

        if (item.getItemId() == R.id.action_share)
        {
            List<Ingredient> ingredients = mIngredientsData;

            String recipe = "";

            if (ingredients != null)
            {
                if (mRecipeData != null)
                {
                    recipe = recipe.concat(mRecipeData.getRecipeName() + "\n\n");
                }
                else return false;

                float percentSum = RecipeUtils.getPercentSum(ingredients);
                float totalWeight;

                String totalWeightString = mTotalWeightEditText.getText().toString();

                if (totalWeightString != null && !totalWeightString.equals(""))
                    totalWeight = Float.parseFloat(totalWeightString);
                else
                    totalWeight = 0;

                if (calculateQuantities)
                {
                    recipe = recipe.concat("Makes " + Math.round(totalWeight) + "g\n\n");
                }

                for (Ingredient i : ingredients)
                {
                    String ingredientString;

                    if (calculateQuantities)
                    {
                        ingredientString = RecipeUtils.getFormattedIngredientWeight
                                (percentSum, totalWeight, i.getPercent());
                    }
                    else
                        ingredientString = RecipeUtils.getFormattedIngredientPercent(i.getPercent());
                    recipe = recipe.concat(i.getName() + " " + ingredientString + "\n");
                }

            }
            else return false;

            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, recipe);
            sendIntent.setType("text/plain");

            Intent shareIntent = Intent.createChooser(sendIntent, "Share Recipe");
            startActivity(shareIntent);

            return true;
        }

        if (item.getItemId() == R.id.action_delete)
        {
            Recipe recipe;
            if (mRecipeId != -1)
            {
                recipe = mRecipe.getValue();
                setResult(mRecipeId);
                mEditRecipeViewModel.delete(recipe);
            }

            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}