package com.francescoalessi.recipes;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.francescoalessi.recipes.data.Recipe;
import com.francescoalessi.recipes.editing.model.EditRecipeViewModel;
import com.francescoalessi.recipes.editing.model.EditRecipeViewModelFactory;

public class ViewRecipeActivity extends AppCompatActivity {

    public TextView mIngredientsTextView;
    private EditRecipeViewModel mEditRecipeViewModel;
    private LiveData<Recipe> mRecipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_recipe);

        mIngredientsTextView = findViewById(R.id.tv_recipe_ingredients);

        Intent intent = getIntent();

        if(intent != null)
        {
            int recipeId = intent.getIntExtra(MainActivity.EXTRA_RECIPE_ID, -1);

            EditRecipeViewModelFactory factory = new EditRecipeViewModelFactory(this.getApplication(), recipeId);
            mEditRecipeViewModel = ViewModelProviders.of(this, factory).get(EditRecipeViewModel.class);

            mRecipe = mEditRecipeViewModel.getRecipeById();

            mRecipe.observe(this, new Observer<Recipe>() {
            @Override
            public void onChanged(@Nullable final Recipe recipe) {
                populateUI(recipe);
            }});

        }
    }

    private void populateUI(Recipe recipe)
    {
        if(recipe != null)
        {
            mIngredientsTextView.setText(recipe.getRecipeName());
            setTitle(recipe.getRecipeName());
        }
    }
}
