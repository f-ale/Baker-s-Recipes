package com.francescoalessi.recipes.editing;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.francescoalessi.recipes.MainActivity;
import com.francescoalessi.recipes.R;
import com.francescoalessi.recipes.data.Ingredient;
import com.francescoalessi.recipes.data.Recipe;
import com.francescoalessi.recipes.editing.adapters.IngredientListAdapter;
import com.francescoalessi.recipes.editing.model.EditRecipeViewModel;
import com.francescoalessi.recipes.editing.model.EditRecipeViewModelFactory;
import com.francescoalessi.recipes.utils.RequestCodes;

import java.io.IOException;
import java.util.List;

public class EditRecipeActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mRecipeNameEditText;
    private Button mSaveRecipeButton;
    private EditRecipeViewModel mEditRecipeViewModel;
    private LiveData<Recipe> mRecipe;
    private LiveData<List<Ingredient>> mIngredientList;
    private Button mNewIngredientButton;
    private RecyclerView mIngredientsRecyclerView;
    private ImageButton mPickImageButton;
    private IngredientListAdapter mAdapter;
    private int mRecipeId;
    private Recipe recipeData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_recipe);

        ActionBar actionBar = getSupportActionBar();

        if(actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mRecipeNameEditText = findViewById(R.id.et_recipe);
        mSaveRecipeButton = findViewById(R.id.btn_save_recipe);
        mSaveRecipeButton.setOnClickListener(this);

        mNewIngredientButton = findViewById(R.id.btn_new_ingredient);
        mNewIngredientButton.setOnClickListener(this);

        mPickImageButton = findViewById(R.id.btn_pick_image);
        mPickImageButton.setOnClickListener(this);

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
                mNewIngredientButton.setVisibility(View.GONE);
                mPickImageButton.setVisibility(View.GONE);
                setTitle("New Recipe");
            }
            else
            {
                mRecipe = mEditRecipeViewModel.getRecipeById();
                mRecipe.observe(this, new Observer<Recipe>() {
                    @Override
                    public void onChanged(@Nullable final Recipe recipe) {
                        recipeData = recipe;
                        populateRecipeUI();
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

        ItemTouchHelper.SimpleCallback deleteRecipeCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getLayoutPosition();
                Ingredient ingredient = mIngredientList.getValue().get(position);
                mEditRecipeViewModel.delete(ingredient);
            }
        };

        new ItemTouchHelper(deleteRecipeCallback).attachToRecyclerView(mIngredientsRecyclerView);
    }

    private void populateRecipeUI()
    {
        Recipe recipe = recipeData;

        if(recipe != null)
        {
            mRecipeNameEditText.setText(recipe.getRecipeName());
            mRecipeNameEditText.setSelection(mRecipeNameEditText.getText().length());
            loadThumbImage(recipe);
            setTitle(recipe.getRecipeName());
        }
    }

    private void loadThumbImage(Recipe recipe)
    {
        if(recipe != null)
        {
            Uri uri = recipe.getRecipeImageUri();
            if(uri != null)
            {
                Glide.with(mPickImageButton.getContext()).load(uri)
                        .placeholder(R.drawable.ic_action_pick_image)
                        .centerCrop()
                        .into(mPickImageButton);
            }
        }
    }
    @Override
    public boolean onSupportNavigateUp() {
        setResult(mRecipeId);
        finish();
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RequestCodes.PICK_IMAGE_REQUEST && data != null && data.getData() != null)
        {
            Uri imageUri = data.getData();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                getContentResolver().takePersistableUriPermission(imageUri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            }
            Recipe recipe = mRecipe.getValue();

            if(recipe != null)
            {
                recipe.setRecipeImageUri(imageUri);
                mEditRecipeViewModel.update(recipe);
            }

        }
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.btn_save_recipe)
        {
            Recipe recipe;
            if(mRecipeId != -1)
            {
                recipe = mRecipe.getValue();
                setResult(mRecipeId);
            }
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

        if(view.getId() == R.id.btn_pick_image)
        {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivityForResult(intent, RequestCodes.PICK_IMAGE_REQUEST);
        }
    }
}
