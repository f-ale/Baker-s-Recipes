package com.francescoalessi.recipes.utils;

import com.francescoalessi.recipes.data.Ingredient;

import java.text.DecimalFormat;
import java.util.List;

public class RecipeUtils {

    public static String getFormattedIngredientWeight(float percentSum, float totalWeight, float ingredientPercent)
    {
        float weight = getIngredientWeight(percentSum, totalWeight, ingredientPercent);

        DecimalFormat oneDecimal = new DecimalFormat("#.#");

        if(weight == Math.round(weight))
            return Math.round(weight) + "g"; // TODO: change this to use String.format to parse float
        else
            return oneDecimal.format(weight) + "g"; // TODO: change this to use String.format to parse float
    }

    public static String getFormattedIngredientPercent(float percent)
    {
        DecimalFormat oneDecimal = new DecimalFormat("#.#");

        if(percent == Math.round(percent))
            return Math.round(percent) + "%"; // TODO: change this to use String.format to parse float
        else
            return oneDecimal.format(percent) + "%"; // TODO: change this to use String.format to parse float
    }

    public static float getIngredientWeight(float percentSum, float totalWeight, float ingredientPercent)
    {
        float weight = ((ingredientPercent / percentSum) * totalWeight);
        return weight;
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
