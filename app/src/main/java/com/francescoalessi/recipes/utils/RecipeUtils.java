package com.francescoalessi.recipes.utils;

import android.graphics.Color;

import com.francescoalessi.recipes.data.Ingredient;

import java.text.DecimalFormat;
import java.util.List;

public class RecipeUtils
{

    public static int stringToColor(String string)
    {
        int hash = string.hashCode();
        int r = (hash & 0xFF0000) >> 16;
        int g = (hash & 0x00FF00) >> 8;
        int b = hash & 0x0000FF;
        float[] hsv = new float[3];

        Color.RGBToHSV(r,g,b,hsv);
        //hsv[0] = hash%359;
        hsv[2] = (float) (0.8);
        hsv[1] = (float) (0.8);
        return Color.HSVToColor(hsv);
    }

    public static String getFormattedIngredientWeight(float percentSum, float totalWeight, float ingredientPercent)
    {
        float weight = getIngredientWeight(percentSum, totalWeight, ingredientPercent);

        DecimalFormat oneDecimal = new DecimalFormat("#.#");

        if (weight == Math.round(weight))
            return Math.round(weight) + "g"; // TODO: change this to use String.format to parse float
        else
            return oneDecimal.format(weight) + "g"; // TODO: change this to use String.format to parse float
    }

    public static String getFormattedIngredientPercent(float percent, boolean addPercent)
    {
        if(addPercent)
            return getFormattedIngredientPercent(percent, "%");
        else
            return getFormattedIngredientPercent(percent);
    }

    public static String getFormattedIngredientPercent(float maxIngredientPercent, float percent, String suffix)
    {
        return getFormattedIngredientPercent(maxIngredientPercent, percent) + suffix;
    }

    public static String getFormattedIngredientPercent(float percent, String suffix)
    {
        return getFormattedIngredientPercent(percent) + suffix;
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

    private static String getFormattedIngredientPercent(float maxIngredientPercent, float percent)
    {
        percent = getIngredientPercent(maxIngredientPercent, percent);
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

    public static float getIngredientPercent(float maxIngredientPercent, float ingredientPercent)
    {
        return ((ingredientPercent / maxIngredientPercent)) * 100;
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

    public static float getMaxIngredientPercent(List<Ingredient> ingredientList)
    {
        float maxIngredientPercent = 0;

        for (Ingredient ingredient : ingredientList)
        {
            float percent =  ingredient.getPercent();

            if(maxIngredientPercent < percent)
                maxIngredientPercent = percent;
        }

        return maxIngredientPercent;
    }
}
