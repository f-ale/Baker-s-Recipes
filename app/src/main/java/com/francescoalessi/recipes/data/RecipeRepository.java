package com.francescoalessi.recipes.data;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;

import java.util.List;

public class RecipeRepository {

    private RecipeDao mRecipeDao;
    private LiveData<List<Recipe>> mRecipeList;

    public RecipeRepository(Application application) {
        RecipeRoomDatabase db = RecipeRoomDatabase.getDatabase(application);
        mRecipeDao = db.recipeDao();
        mRecipeList = mRecipeDao.getAllRecipes();
    }

    public LiveData<List<Recipe>> getRecipeList() {
        return mRecipeList;
    }

    public LiveData<List<Ingredient>> getIngredientsForRecipe(int recipeId) {
        return mRecipeDao.getIngredientsForRecipe(recipeId);
    }

    public LiveData<Recipe> getRecipeById(int id) { return mRecipeDao.getRecipeById(id); }

    public void insert (Recipe recipe) {
        new insertAsyncTask(mRecipeDao).execute(recipe);
    }

    public void update (Recipe recipe) {
        new updateAsyncTask(mRecipeDao).execute(recipe);
    }

    public void delete (Recipe recipe)  {
        new deleteAsyncTask(mRecipeDao).execute(recipe);
    }

    public void insert (Ingredient ingredient) {
        new insertIngredientAsyncTask(mRecipeDao).execute(ingredient);
    }

    private static class insertIngredientAsyncTask
            extends AsyncTask<Ingredient, Void, Void>
    {
        private RecipeDao mAsyncTaskDao;

        insertIngredientAsyncTask(RecipeDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(Ingredient... ingredients) {
            mAsyncTaskDao.insert(ingredients[0]);
            return null;
        }
    }

    private static class insertAsyncTask
            extends AsyncTask<Recipe, Void, Void>
    {
        private RecipeDao mAsyncTaskDao;

        insertAsyncTask(RecipeDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(Recipe... recipes) {
            mAsyncTaskDao.insert(recipes[0]);
            return null;
        }
    }

    private static class updateAsyncTask
            extends AsyncTask<Recipe, Void, Void>
    {
        private RecipeDao mAsyncTaskDao;

        updateAsyncTask(RecipeDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(Recipe... recipes) {
            int rows = mAsyncTaskDao.update(recipes[0]);
            Log.d("UPDATE", rows + "");
            return null;
        }
    }

    private static class deleteAsyncTask
            extends AsyncTask<Recipe, Void, Void>
    {
        private RecipeDao mAsyncTaskDao;

        deleteAsyncTask(RecipeDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(Recipe... recipes) {
            mAsyncTaskDao.delete(recipes[0]);
            return null;
        }
    }
}
