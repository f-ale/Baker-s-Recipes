package com.francescoalessi.recipes;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.francescoalessi.recipes.data.Recipe;
import com.francescoalessi.recipes.editing.EditRecipeActivity;
import com.francescoalessi.recipes.model.RecipeViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView mRecyclerView;
    private RecipeListAdapter mAdapter;
    private RecipeViewModel mRecipeViewModel;

    private FloatingActionButton mFAB;

    public static final String EXTRA_RECIPE_NAME = "extra_recipe_name";
    public static final String EXTRA_RECIPE_ID = "extra_recipe_id";
    public static final int NEW_RECIPE_ID = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = findViewById(R.id.rv_recipe_list);
        mAdapter = new RecipeListAdapter(this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mFAB = findViewById(R.id.fab);
        mFAB.setOnClickListener(this);

        mRecipeViewModel = ViewModelProviders.of(this).get(RecipeViewModel.class);

        mRecipeViewModel.getRecipeList().observe(this, new Observer<List<Recipe>>() {
            @Override
            public void onChanged(@Nullable final List<Recipe> recipes) {
                mAdapter.setRecipes(recipes);
            }
        });

        ItemTouchHelper.SimpleCallback deleteRecipeCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getLayoutPosition();
                Recipe recipe = mRecipeViewModel.getRecipeList().getValue().get(position);
                mRecipeViewModel.delete(recipe);
            }
        };

        new ItemTouchHelper(deleteRecipeCallback).attachToRecyclerView(mRecyclerView);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.fab)
        {
            Context context = view.getContext();

            //start intent
            Intent intent = new Intent(context, EditRecipeActivity.class);
            intent.putExtra(EXTRA_RECIPE_ID, NEW_RECIPE_ID);
            context.startActivity(intent);
        }
    }
}
