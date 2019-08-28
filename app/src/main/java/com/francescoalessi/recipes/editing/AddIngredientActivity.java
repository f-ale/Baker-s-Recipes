package com.francescoalessi.recipes.editing;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.francescoalessi.recipes.MainActivity;
import com.francescoalessi.recipes.R;
import com.francescoalessi.recipes.data.Ingredient;
import com.francescoalessi.recipes.editing.model.EditRecipeViewModel;
import com.francescoalessi.recipes.editing.model.EditRecipeViewModelFactory;

import java.util.List;

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

            EditRecipeViewModelFactory factory = new EditRecipeViewModelFactory(this.getApplication(), mRecipeId);
            mEditRecipeViewModel = ViewModelProviders.of(this, factory).get(EditRecipeViewModel.class);

            mIngredientId = intent.getIntExtra(MainActivity.EXTRA_INGREDIENT_ID, -1);
            if(mIngredientId != -1)
            {
                LiveData<Ingredient> ingredient = mEditRecipeViewModel.getIngredientById(mIngredientId);
                ingredient.observe(this, new Observer<Ingredient>() {
                    @Override
                    public void onChanged(Ingredient ingredient) {
                        populateUI(ingredient);
                        mIngredient = ingredient;
                    }
                });
            }
        }

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

            Boolean hasName = !name.equals("") ;
            Boolean hasPercent = !percent.equals("");
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
