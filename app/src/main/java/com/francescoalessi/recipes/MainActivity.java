package com.francescoalessi.recipes;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.francescoalessi.recipes.data.Recipe;
import com.francescoalessi.recipes.editing.EditRecipeActivity;
import com.francescoalessi.recipes.editing.NewRecipeDialogFragment;
import com.francescoalessi.recipes.model.RecipeViewModel;
import com.francescoalessi.recipes.settings.SettingsActivity;
import com.francescoalessi.recipes.utils.RequestCodes;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, NewRecipeDialogFragment.NewRecipeDialogListener
{

    private RecyclerView mRecyclerView;
    private TextView mNoRecipeTextView;
    private RecipeListAdapter mAdapter;
    private RecipeViewModel mRecipeViewModel;

    private Recipe mLastAddedRecipe;
    public static final String EXTRA_RECIPE_ID = "extra_recipe_id";
    public static final String EXTRA_INGREDIENT_ID = "extra_ingredient_id";
    public static final int NEW_RECIPE_ID = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = findViewById(R.id.rv_recipe_list);
        mAdapter = new RecipeListAdapter(this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mNoRecipeTextView = findViewById(R.id.tv_no_recipes);
        mNoRecipeTextView.setOnClickListener(this);

        FloatingActionButton mFAB = findViewById(R.id.fab);
        mFAB.setOnClickListener(this);

        mRecipeViewModel = ViewModelProviders.of(this).get(RecipeViewModel.class);

        mRecipeViewModel.getRecipeList().observe(this, new Observer<List<Recipe>>()
        {
            @Override
            public void onChanged(@Nullable final List<Recipe> recipes)
            {
                mAdapter.setRecipes(recipes);

                if (recipes != null && recipes.size() > 0)
                {
                    mLastAddedRecipe = recipes.get(recipes.size() - 1);
                    mNoRecipeTextView.setVisibility(View.GONE);
                    mRecyclerView.setVisibility(View.VISIBLE);
                }
                else
                {
                    mNoRecipeTextView.setVisibility(View.VISIBLE);
                    mRecyclerView.setVisibility(View.GONE);
                }

            }
        });

        ItemTouchHelper.SimpleCallback deleteRecipeCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT)
        {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target)
            {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction)
            {
                int position = viewHolder.getLayoutPosition();
                Recipe recipe = mRecipeViewModel.getRecipeList().getValue().get(position);
                mRecipeViewModel.delete(recipe);
            }
        };

        new ItemTouchHelper(deleteRecipeCallback).attachToRecyclerView(mRecyclerView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        if (item.getItemId() == R.id.action_open_settings)
        {
            Context context = this;

            //start intent
            Intent intent = new Intent(context, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return false;
    }

    @Override
    public void onClick(View view)
    {
        if (view.getId() == R.id.fab || view.getId() == R.id.tv_no_recipes)
        {
            DialogFragment newFragment = new NewRecipeDialogFragment();
            newFragment.show(getSupportFragmentManager(), "newRecipe");
        }
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, String recipeName)
    {
        Log.d("ONDIALOG", "Recipename: " + recipeName);
        if (recipeName != null && !recipeName.equals(""))
        {
            List<Recipe> recipeList = mRecipeViewModel.getRecipeList().getValue();
            if (recipeList != null)
            {
                Recipe recipe = new Recipe(recipeName);

                int recipeId = mRecipeViewModel.insert(recipe);

                Intent intent = new Intent(this, EditRecipeActivity.class);
                intent.putExtra(MainActivity.EXTRA_RECIPE_ID, recipeId);
                startActivity(intent);
            }
        }
    }
}
