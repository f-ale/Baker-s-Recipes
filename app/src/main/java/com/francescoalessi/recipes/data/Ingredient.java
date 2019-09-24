package com.francescoalessi.recipes.data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.util.Comparator;

import static androidx.room.ForeignKey.CASCADE;

@Entity(tableName = "ingredient_table", foreignKeys = @ForeignKey(entity = Recipe.class,
        parentColumns = "id",
        childColumns = "recipeId",
        onDelete = CASCADE))

public class Ingredient
{

    @PrimaryKey(autoGenerate = true)
    int id;

    int recipeId;

    @NonNull
    String name;

    float percent;

    public Ingredient(int recipeId, String name, float percent)
    {
        this.recipeId = recipeId;
        this.name = name;
        this.percent = percent;
    }

    public float getPercent()
    {
        return percent;
    }

    public void setPercent(float percent)
    {
        this.percent = percent;
    }

    @NonNull
    public String getName()
    {
        return name;
    }

    public int getId() { return id; }

    public void setName(@NonNull String name)
    {
        this.name = name;
    }
}
