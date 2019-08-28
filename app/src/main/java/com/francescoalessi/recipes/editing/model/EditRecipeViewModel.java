package com.francescoalessi.recipes.editing.model;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.francescoalessi.recipes.data.Ingredient;
import com.francescoalessi.recipes.data.Recipe;
import com.francescoalessi.recipes.data.RecipeRepository;

import java.util.List;

public class EditRecipeViewModel extends AndroidViewModel {

    private RecipeRepository mRepository;
    private LiveData<Recipe> mRecipe;
    private LiveData<Ingredient> mIngredient;
    private LiveData<List<Ingredient>> mRecipeIngredients;

    public EditRecipeViewModel(Application application, int recipeId)
    {
        super(application);
        mRepository = new RecipeRepository(application);
        mRecipe = mRepository.getRecipeById(recipeId);
        mRecipeIngredients = mRepository.getIngredientsForRecipe(recipeId);
    }

    public LiveData<Recipe> getRecipeById() { return mRecipe; }

    public LiveData<Ingredient> getIngredientById(int id) { return mRepository.getIngredientById(id); }

    public LiveData<List<Ingredient>> getRecipeIngredients() { return mRecipeIngredients; }

    public void insert(Recipe recipe)
    {
        mRepository.insert(recipe);
    }

    public void insert(Ingredient ingredient)
    {
        mRepository.insert(ingredient);
    }

    public void delete(Ingredient ingredient)
    {
        mRepository.delete(ingredient);
    }

    public void update(Recipe recipe)
    {
        mRepository.update(recipe);
    }

    public void update(Ingredient ingredient)
    {
        mRepository.update(ingredient);
    }
}
