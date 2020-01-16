package com.francescoalessi.recipes.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;

import androidx.preference.PreferenceManager;

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

    public static String getLocalizedWeightSuffix(Context context)
    {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String unit = sharedPreferences.getString("weight_unit", "grams");

        if(unit.equals("ounces"))
            return "oz";
        else
            return "g";
    }

    public static String getFormattedIngredientWeight(double percentSum, double totalWeight, double ingredientPercent, String suffix)
    {
        double weight = getIngredientWeight(percentSum, totalWeight, ingredientPercent);

        DecimalFormat oneDecimal = new DecimalFormat("#.#");

        if (weight == Math.round(weight))
            return Math.round(weight) + suffix; // TODO: change this to use String.format to parse float
        else
            return oneDecimal.format(weight) + suffix; // TODO: change this to use String.format to parse float
    }

    public static String getFormattedIngredientPercent(double percent, boolean addPercent)
    {
        if(addPercent)
            return getFormattedIngredientPercent(percent, "%");
        else
            return getFormattedIngredientPercent(percent);
    }

    public static String getFormattedIngredientPercent(double maxIngredientPercent, double percent, String suffix)
    {
        return getFormattedIngredientPercent(maxIngredientPercent, percent) + suffix;
    }

    public static String getFormattedIngredientPercent(double percent, String suffix)
    {
        return getFormattedIngredientPercent(percent) + suffix;
    }

    private static String getFormattedIngredientPercent(double percent)
    {
        DecimalFormat oneDecimal = new DecimalFormat("#.#");
        DecimalFormat wholeNumber = new DecimalFormat("#");

        if (percent == Math.round(percent))
            return wholeNumber.format(percent); // TODO: change this to use String.format to parse float
        else
            return oneDecimal.format(percent); // TODO: change this to use String.format to parse float
    }

    private static String getFormattedIngredientPercent(double maxIngredientPercent, double percent)
    {
        percent = getIngredientPercent(maxIngredientPercent, percent);
        DecimalFormat oneDecimal = new DecimalFormat("#.#");
        DecimalFormat wholeNumber = new DecimalFormat("#");

        if (percent == Math.round(percent))
            return wholeNumber.format(percent); // TODO: change this to use String.format to parse float
        else
            return oneDecimal.format(percent); // TODO: change this to use String.format to parse float
    }

    public static double getIngredientWeight(double percentSum, double totalWeight, double ingredientPercent)
    {
        return ((ingredientPercent / percentSum) * totalWeight);
    }

    public static double getIngredientPercent(double maxIngredientPercent, double ingredientPercent)
    {
        return ((ingredientPercent / maxIngredientPercent)) * 100;
    }

    public static double getPercentSum(List<Ingredient> ingredientList)
    {
        double percentSum = 0;

        for (Ingredient ingredient : ingredientList)
        {
            percentSum += ingredient.getPercent();
        }

        return percentSum;
    }

    public static double getMaxIngredientPercent(List<Ingredient> ingredientList)
    {
        double maxIngredientPercent = 0;

        for (Ingredient ingredient : ingredientList)
        {
            double percent =  ingredient.getPercent();

            if(maxIngredientPercent < percent)
                maxIngredientPercent = percent;
        }

        return maxIngredientPercent;
    }
}
