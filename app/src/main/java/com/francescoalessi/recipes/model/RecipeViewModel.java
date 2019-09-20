package com.francescoalessi.recipes.model;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.francescoalessi.recipes.data.Recipe;
import com.francescoalessi.recipes.data.RecipeRepository;

import java.util.List;

public class RecipeViewModel extends AndroidViewModel {

    private RecipeRepository mRepository;
    private LiveData<List<Recipe>> mRecipeList;

    public RecipeViewModel (Application application)
    {
        super(application);
        mRepository = new RecipeRepository(application);
        mRecipeList = mRepository.getRecipeList();
    }

    public LiveData<List<Recipe>> getRecipeList()
    {
        return mRecipeList;
    }

    public void delete(Recipe recipe)
    {
        mRepository.delete(recipe);
    }
    public int insert(Recipe recipe) { return (int) mRepository.insert(recipe); }

}
