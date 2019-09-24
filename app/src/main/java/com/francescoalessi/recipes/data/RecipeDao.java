package com.francescoalessi.recipes.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface RecipeDao
{

    @Insert
    long insert(Recipe recipe);

    @Delete
    void delete(Recipe recipe);

    @Update
    int update(Recipe recipe);

    @Insert
    void insert(Ingredient ingredient);

    @Delete
    void delete(Ingredient ingredient);

    @Update
    int update(Ingredient ingredient);

    @Query("SELECT * FROM recipe_table ORDER BY id DESC LIMIT 1;")
    LiveData<Recipe> getLastAddedRecipe();

    @Query("SELECT * from ingredient_table WHERE recipeId = :recipeId ORDER BY percent DESC")
    LiveData<List<Ingredient>> getIngredientsForRecipe(int recipeId);

    @Query("SELECT * from recipe_table WHERE id = :id")
    LiveData<Recipe> getRecipeById(int id);

    @Query("SELECT * from ingredient_table WHERE id = :id")
    LiveData<Ingredient> getIngredientById(int id);

    @Query("DELETE FROM recipe_table")
    void deleteAll();

    @Query("SELECT * from recipe_table ORDER BY name ASC")
    LiveData<List<Recipe>> getAllRecipes();
}
