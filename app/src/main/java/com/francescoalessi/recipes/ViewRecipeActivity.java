package com.francescoalessi.recipes;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
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

import java.util.List;

public class ViewRecipeActivity extends AppCompatActivity implements View.OnClickListener
{
    private EditRecipeViewModel mEditRecipeViewModel;
    private LiveData<Recipe> mRecipe;
    private EditText mTotalWeightEditText;
    private IngredientListAdapter mAdapter;
    private int mRecipeId;
    private boolean calculateQuantities = false;
    private ImageView mRecipeThumbnailImageView;
    private TextView mNoIngredientsTextView;
    private View mMakeRecipeCardView;

    private Recipe mRecipeData;
    private List<Ingredient> mIngredientsData;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_recipe);

        mTotalWeightEditText = findViewById(R.id.et_total_recipe_weight);

        mRecipeThumbnailImageView = findViewById(R.id.iv_recipe_thumbnail);
        mRecipeThumbnailImageView.setVisibility(View.INVISIBLE);

        RecyclerView mIngredientsRecyclerView = findViewById(R.id.rv_ingredient_list);
        mAdapter = new IngredientListAdapter(this, false);
        mIngredientsRecyclerView.setAdapter(mAdapter);
        mIngredientsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        Log.d("VIEWRECIPE", "onCreate");

        mNoIngredientsTextView = findViewById(R.id.tv_no_ingredients);
        mNoIngredientsTextView.setOnClickListener(this);
        mMakeRecipeCardView = findViewById(R.id.cv_make_recipe);

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

        TextWatcher mTotalWeightTextWatcher = new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

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
            ImageView mColorLip = findViewById(R.id.iv_color_lip);
            mColorLip.setImageDrawable(new ColorDrawable(RecipeUtils.stringToColor(recipe.getRecipeName())));
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
            else
            {
                mRecipeThumbnailImageView.setVisibility(View.GONE);
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

        LiveData<List<Ingredient>> mIngredientList = mEditRecipeViewModel.getRecipeIngredients();
        mIngredientList.observe(this, new Observer<List<Ingredient>>()
        {
            @Override
            public void onChanged(List<Ingredient> ingredients)
            {
                mAdapter.setIngredients(ingredients);
                mIngredientsData = ingredients;
                if (ingredients.size() == 0)
                {
                    mNoIngredientsTextView.setVisibility(View.VISIBLE);
                    mMakeRecipeCardView.setVisibility(View.GONE);
                }

                else
                {
                    mNoIngredientsTextView.setVisibility(View.GONE);
                    mMakeRecipeCardView.setVisibility(View.VISIBLE);
                }
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

    private void startEditActivity()
    {
        Context context = this;

        //start intent
        Intent intent = new Intent(context, EditRecipeActivity.class);
        intent.putExtra(MainActivity.EXTRA_RECIPE_ID, mRecipeId);
        startActivityForResult(intent, RequestCodes.EDIT_RECIPE_REQUEST);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        if (item.getItemId() == R.id.action_edit)
        {
            startEditActivity();
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

                double percentSum = RecipeUtils.getPercentSum(ingredients);
                float totalWeight;

                String totalWeightString = mTotalWeightEditText.getText().toString();

                if (!totalWeightString.equals(""))
                    totalWeight = Float.parseFloat(totalWeightString);
                else
                    totalWeight = 0;

                if (calculateQuantities)
                {
                    recipe = recipe.concat(getString(R.string.makes_weight_grams,Math.round(totalWeight)) + "\n\n");
                }

                for (Ingredient i : ingredients)
                {
                    String ingredientString;

                    if (calculateQuantities)
                    {
                        ingredientString = RecipeUtils.getFormattedIngredientWeight(
                                percentSum, totalWeight, i.getPercent(),
                                RecipeUtils.getLocalizedWeightSuffix(this));
                    }
                    else
                    {
                        ingredientString = RecipeUtils.getFormattedIngredientPercent(i.getPercent(), true);
                        Log.d("SHARE", i.getPercent() + "; " + RecipeUtils.getFormattedIngredientPercent(i.getPercent(), true));
                    }


                    recipe = recipe.concat(i.getName() + " " + ingredientString + "\n");
                }

            }
            else return false;

            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, recipe);
            sendIntent.setType("text/plain");

            Intent shareIntent = Intent.createChooser(sendIntent, getString(R.string.share_recipe));

            if(sendIntent.resolveActivity(getPackageManager()) != null)
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

    @Override
    public void onClick(View view)
    {
        startEditActivity();
    }
}