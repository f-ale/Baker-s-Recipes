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
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

import com.francescoalessi.recipes.data.Ingredient;
import com.francescoalessi.recipes.data.Recipe;
import com.francescoalessi.recipes.editing.EditRecipeActivity;
import com.francescoalessi.recipes.editing.adapters.IngredientListAdapter;
import com.francescoalessi.recipes.editing.model.EditRecipeViewModel;
import com.francescoalessi.recipes.editing.model.EditRecipeViewModelFactory;

import java.util.List;

public class ViewRecipeActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    private EditRecipeViewModel mEditRecipeViewModel;
    private LiveData<Recipe> mRecipe;
    private LiveData<List<Ingredient>> mIngredientList;
    private RecyclerView mIngredientsRecyclerView;
    private Switch mCalculateWeightsButton;
    private EditText mTotalWeightEditText;
    private IngredientListAdapter mAdapter;
    private int mRecipeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_recipe);

        mTotalWeightEditText = findViewById(R.id.et_total_recipe_weight);
        mCalculateWeightsButton = findViewById(R.id.btn_calculate_quantities);
        mCalculateWeightsButton.setOnCheckedChangeListener(this);

        mIngredientsRecyclerView = findViewById(R.id.rv_ingredient_list);
        mAdapter = new IngredientListAdapter(this, false);
        mIngredientsRecyclerView.setAdapter(mAdapter);
        mIngredientsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        Log.d("VIEWRECIPE", "onCreate");

        Intent intent = getIntent();

        if(intent != null)
        {
            mRecipeId = intent.getIntExtra(MainActivity.EXTRA_RECIPE_ID, -1);
            Log.d("VIEWRECIPE", "onCreate intent ID = " + mRecipeId);
        }

        if(mRecipeId == -1 && savedInstanceState != null && !savedInstanceState.isEmpty())
        {
            mRecipeId = savedInstanceState.getInt(MainActivity.EXTRA_RECIPE_ID, -1);
            Log.d("VIEWRECIPE", "onCreate savedInstanceState ID = " + mRecipeId);
        }

        setupUserInterface();
    }

    private void populateRecipeUI(Recipe recipe)
    {
        if(recipe != null)
        {
            setTitle(recipe.getRecipeName());
        }
    }

    private void setupUserInterface()
    {
        EditRecipeViewModelFactory factory = new EditRecipeViewModelFactory(this.getApplication(), mRecipeId);
        mEditRecipeViewModel = ViewModelProviders.of(this, factory).get(EditRecipeViewModel.class);

        mRecipe = mEditRecipeViewModel.getRecipeById();
        mRecipe.observe(this, new Observer<Recipe>() {
            @Override
            public void onChanged(@Nullable final Recipe recipe) {
                populateRecipeUI(recipe);
            }});

        mIngredientList = mEditRecipeViewModel.getRecipeIngredients();
        mIngredientList.observe(this, new Observer<List<Ingredient>>() {
            @Override
            public void onChanged(List<Ingredient> ingredients) {
                mAdapter.setIngredients(ingredients);
            }
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        outState.putInt(MainActivity.EXTRA_RECIPE_ID, mRecipeId);
        Log.d("VIEWRECIPE", "onSave");
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if(mRecipeId == -1 && !savedInstanceState.isEmpty())
        {
            mRecipeId = savedInstanceState.getInt(MainActivity.EXTRA_RECIPE_ID);
            setupUserInterface();
        }

        Log.d("VIEWRECIPE", "onRestore");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mRecipeId = resultCode;
            Log.d("VIEWRECIPE", "OnActivityResult: mRecipeId = " + mRecipeId);
            setupUserInterface();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.view_recipe_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.action_edit)
        {
            Context context = this;

            //start intent
            Intent intent = new Intent(context, EditRecipeActivity.class);
            intent.putExtra(MainActivity.EXTRA_RECIPE_ID, mRecipeId);
            startActivityForResult(intent, 1); // TODO: Change request code
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        if(compoundButton.getId() == R.id.btn_calculate_quantities)
        {
            if(isChecked)
            {
                if(mTotalWeightEditText.getText() != null)
                {
                    String weightText = mTotalWeightEditText.getText().toString();
                    if(!weightText.equals(""))
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
            }

        }
    }
}
