package com.francescoalessi.recipes.data;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "recipe_table")
public class Recipe
{

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @NonNull
    @ColumnInfo(name = "name")
    private String mRecipeName;

    public Recipe(@NonNull String recipeName)
    {
        this.mRecipeName = recipeName;
    }

    @Ignore
    public Recipe(int id, @NonNull String recipeName)
    {
        this.id = id;
        this.mRecipeName = recipeName;
    }

    private Uri recipeImageUri;

    public Uri getRecipeImageUri() { return this.recipeImageUri; }

    public void setRecipeImageUri(Uri newUri) { this.recipeImageUri = newUri; }

    public String getRecipeName()
    {
        return this.mRecipeName;
    }

    public void setRecipeName(String name) { this.mRecipeName = name; }

    public int getId() { return this.id; }

    public void setId(int id) { this.id = id; }
}
