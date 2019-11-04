package com.francescoalessi.recipes.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.francescoalessi.recipes.R;
import com.francescoalessi.recipes.concurrency.AppExecutors;
import com.francescoalessi.recipes.data.Ingredient;
import com.francescoalessi.recipes.data.Recipe;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

public class RecipeUtils
{

    public static String getFormattedIngredientWeight(float percentSum, float totalWeight, float ingredientPercent)
    {
        float weight = getIngredientWeight(percentSum, totalWeight, ingredientPercent);

        DecimalFormat oneDecimal = new DecimalFormat("#.#");

        if (weight == Math.round(weight))
            return Math.round(weight) + "g"; // TODO: change this to use String.format to parse float
        else
            return oneDecimal.format(weight) + "g"; // TODO: change this to use String.format to parse float
    }

    public static String getFormattedIngredientPercent(float percent, boolean addPercentSign)
    {
        if(addPercentSign)
            return getFormattedIngredientPercent(percent) + "%";
        else
            return getFormattedIngredientPercent(percent);
    }

    private static String getFormattedIngredientPercent(float percent)
    {
        DecimalFormat oneDecimal = new DecimalFormat("#.#");
        DecimalFormat wholeNumber = new DecimalFormat("#");

        if (percent == Math.round(percent))
            return wholeNumber.format(percent); // TODO: change this to use String.format to parse float
        else
            return oneDecimal.format(percent); // TODO: change this to use String.format to parse float
    }

    public static float getIngredientWeight(float percentSum, float totalWeight, float ingredientPercent)
    {
        return ((ingredientPercent / percentSum) * totalWeight);
    }

    public static float getPercentSum(List<Ingredient> ingredientList)
    {
        float percentSum = 0;

        for (Ingredient ingredient : ingredientList)
        {
            percentSum += ingredient.getPercent();
        }

        return percentSum;
    }
}
