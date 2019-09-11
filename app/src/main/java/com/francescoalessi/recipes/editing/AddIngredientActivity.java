package com.francescoalessi.recipes.editing;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.francescoalessi.recipes.MainActivity;
import com.francescoalessi.recipes.R;
import com.francescoalessi.recipes.data.Ingredient;
import com.francescoalessi.recipes.editing.model.EditRecipeViewModel;
import com.francescoalessi.recipes.editing.model.EditRecipeViewModelFactory;

public class AddIngredientActivity extends AppCompatActivity implements View.OnClickListener {

    private int mRecipeId;
    private int mIngredientId;
    private EditRecipeViewModel mEditRecipeViewModel;

    private EditText mIngredientNameEditText;
    private EditText mIngredientPercentEditText;
    private Button mAddIngredientButton;
    private Ingredient mIngredient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ingredient);

        mIngredientNameEditText = findViewById(R.id.et_ingredient_name);
        mIngredientPercentEditText = findViewById(R.id.et_ingredient_percent);
        mAddIngredientButton = findViewById(R.id.btn_add_ingredient);
        mAddIngredientButton.setOnClickListener(this);

        Intent intent = getIntent();

        if(intent != null)
        {
            mRecipeId = intent.getIntExtra(MainActivity.EXTRA_RECIPE_ID, -1);
            mIngredientId = intent.getIntExtra(MainActivity.EXTRA_INGREDIENT_ID, -1);
        }

        if(savedInstanceState != null && !savedInstanceState.isEmpty())
        {
            if(mRecipeId == -1)
                mRecipeId = savedInstanceState.getInt(MainActivity.EXTRA_RECIPE_ID, -1);

            if(mIngredientId == -1)
                mIngredientId = savedInstanceState.getInt(MainActivity.EXTRA_INGREDIENT_ID, -1);
        }

        retrieveViewModel();
        retrieveIngredientData(savedInstanceState);
    }

    private void retrieveViewModel()
    {
        EditRecipeViewModelFactory factory = new EditRecipeViewModelFactory(this.getApplication(), mRecipeId);
        mEditRecipeViewModel = ViewModelProviders.of(this, factory).get(EditRecipeViewModel.class);
    }

    private void retrieveIngredientData(final Bundle savedInstanceState)
    {
        if(mIngredientId != -1)
        {
            LiveData<Ingredient> ingredient = mEditRecipeViewModel.getIngredientById(mIngredientId);
            ingredient.observe(this, new Observer<Ingredient>() {
                @Override
                public void onChanged(Ingredient ingredient) {
                    if(savedInstanceState == null || savedInstanceState.isEmpty())
                        populateUI(ingredient);
                    mIngredient = ingredient;
                }
            });
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);

        outState.putInt(MainActivity.EXTRA_INGREDIENT_ID, mIngredientId);
        outState.putInt(MainActivity.EXTRA_RECIPE_ID, mRecipeId);
    }

    private void populateUI(Ingredient ingredient)
    {
        mIngredientNameEditText.setText(ingredient.getName());
        mIngredientPercentEditText.setText(ingredient.getPercent() + ""); // TODO: change this to use something better than string concat
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.btn_add_ingredient)
        {
            // This needs lots of input sanity checks, especially for percent
            String name = mIngredientNameEditText.getText().toString();
            String percent = mIngredientPercentEditText.getText().toString();

            boolean hasName = !name.equals("");
            boolean hasPercent = !percent.equals("");
            if(hasName && hasPercent && mIngredientId == -1)
            {
                mEditRecipeViewModel.insert(new Ingredient(mRecipeId, name, Float.parseFloat(percent)));
                finish();
            }
            else
                if(hasName && hasPercent && mIngredient != null)
                {
                    mIngredient.setName(name);
                    mIngredient.setPercent(Float.parseFloat(percent));
                    mEditRecipeViewModel.update(mIngredient);
                    finish();
                }

        }
    }
}
