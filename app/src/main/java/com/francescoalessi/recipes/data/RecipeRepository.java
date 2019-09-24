package com.francescoalessi.recipes.data;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.francescoalessi.recipes.concurrency.AppExecutors;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class RecipeRepository
{

    private RecipeDao mRecipeDao;
    private LiveData<List<Recipe>> mRecipeList;

    public RecipeRepository(Application application)
    {
        RecipeRoomDatabase db = RecipeRoomDatabase.getDatabase(application);
        mRecipeDao = db.recipeDao();
        mRecipeList = mRecipeDao.getAllRecipes();
    }

    public LiveData<List<Recipe>> getRecipeList()
    {
        return mRecipeList;
    }

    public LiveData<List<Ingredient>> getIngredientsForRecipe(int recipeId)
    {
        return mRecipeDao.getIngredientsForRecipe(recipeId);
    }

    public LiveData<Recipe> getRecipeById(int id) { return mRecipeDao.getRecipeById(id); }

    public LiveData<Ingredient> getIngredientById(int id) { return mRecipeDao.getIngredientById(id); }

    public LiveData<Recipe> getLastAddedRecipe() { return mRecipeDao.getLastAddedRecipe(); }

    public long insert(final Recipe recipe)
    {
        Future<Long> future = AppExecutors.getInstance().diskIO().submit(new Callable<Long>()
        {
            @Override
            public Long call()
            {
                return mRecipeDao.insert(recipe);
            }
        });

        Long rowId = (long) -1;

        try
        {
            rowId = future.get();
        }
        catch (InterruptedException e1)
        {
            e1.printStackTrace();
        }
        catch (ExecutionException e)
        {
            e.printStackTrace();
        }

        return rowId;
    }

    public void insert(final Ingredient ingredient)
    {
        AppExecutors.getInstance().diskIO().execute(new Runnable()
        {
            @Override
            public void run()
            {
                mRecipeDao.insert(ingredient);
            }
        });
    }

    public void update(final Recipe recipe)
    {
        AppExecutors.getInstance().diskIO().execute(new Runnable()
        {
            @Override
            public void run()
            {
                mRecipeDao.update(recipe);
            }
        });
    }

    public void update(final Ingredient ingredient)
    {
        AppExecutors.getInstance().diskIO().execute(new Runnable()
        {
            @Override
            public void run()
            {
                mRecipeDao.update(ingredient);
            }
        });
    }

    public void delete(final Recipe recipe)
    {
        AppExecutors.getInstance().diskIO().execute(new Runnable()
        {
            @Override
            public void run()
            {
                mRecipeDao.delete(recipe);
            }
        });
    }

    public void delete(final Ingredient ingredient)
    {
        AppExecutors.getInstance().diskIO().execute(new Runnable()
        {
            @Override
            public void run()
            {
                mRecipeDao.delete(ingredient);
            }
        });
    }
}
