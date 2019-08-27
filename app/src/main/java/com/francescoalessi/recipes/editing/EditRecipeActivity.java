package com.francescoalessi.recipes.editing;

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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.francescoalessi.recipes.MainActivity;
import com.francescoalessi.recipes.R;
import com.francescoalessi.recipes.data.Ingredient;
import com.francescoalessi.recipes.data.Recipe;
import com.francescoalessi.recipes.editing.adapters.IngredientListAdapter;
import com.francescoalessi.recipes.editing.model.EditRecipeViewModel;
import com.francescoalessi.recipes.editing.model.EditRecipeViewModelFactory;

import java.util.List;

public class EditRecipeActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mRecipeNameEditText;
    private Button mSaveRecipeButton;
    private EditRecipeViewModel mEditRecipeViewModel;
    private LiveData<Recipe> mRecipe;
    private LiveData<List<Ingredient>> mIngredientList;
    private Button mNewIngredientButton;
    private RecyclerView mIngredientsRecyclerView;
    private IngredientListAdapter mAdapter;
    private int mRecipeId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_recipe);

        mRecipeNameEditText = findViewById(R.id.et_recipe);
        mSaveRecipeButton = findViewById(R.id.btn_save_recipe);
        mSaveRecipeButton.setOnClickListener(this);

        mNewIngredientButton = findViewById(R.id.btn_new_ingredient);
        mNewIngredientButton.setOnClickListener(this);

        mIngredientsRecyclerView = findViewById(R.id.rv_ingredient_list);
        mAdapter = new IngredientListAdapter(this);
        mIngredientsRecyclerView.setAdapter(mAdapter);
        mIngredientsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        Intent intent = getIntent();

        if(intent != null)
        {
            mRecipeId = intent.getIntExtra(MainActivity.EXTRA_RECIPE_ID, -1);

            EditRecipeViewModelFactory factory = new EditRecipeViewModelFactory(this.getApplication(), mRecipeId);
            mEditRecipeViewModel = ViewModelProviders.of(this, factory).get(EditRecipeViewModel.class);

            if (mRecipeId == -1)
            {
                setTitle("New Recipe");
                mRecipeNameEditText.requestFocus();
            }
            else
            {
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
        }
    }

    private void populateRecipeUI(Recipe recipe)
    {
        if(recipe != null)
        {
            mRecipeNameEditText.setText(recipe.getRecipeName());
            mRecipeNameEditText.setSelection(mRecipeNameEditText.getText().length());
            mRecipeNameEditText.requestFocus();
            setTitle(recipe.getRecipeName());
        }
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.btn_save_recipe)
        {
            Recipe recipe;
            if(mRecipeId != -1)
                recipe = mRecipe.getValue();
            else
                recipe = new Recipe("New Recipe");

            if(!mRecipeNameEditText.getText().toString().equals(""))
                recipe.setRecipeName(mRecipeNameEditText.getText().toString());

            if(mRecipeId != -1)
                mEditRecipeViewModel.update(recipe);
            else
                mEditRecipeViewModel.insert(recipe);
            finish();
        }

        if(view.getId() == R.id.btn_new_ingredient)
        {
            //Launch add Ingredient activity;
            Context context = view.getContext();

            //start intent
            Intent intent = new Intent(context, AddIngredientActivity.class);
            intent.putExtra(MainActivity.EXTRA_RECIPE_ID, mRecipeId);
            context.startActivity(intent);
        }
    }
}
