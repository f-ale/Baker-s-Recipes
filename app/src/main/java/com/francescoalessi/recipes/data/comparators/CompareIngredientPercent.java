package com.francescoalessi.recipes.data.comparators;

import com.francescoalessi.recipes.data.Ingredient;

import java.util.Comparator;

public class CompareIngredientPercent implements Comparator<Ingredient> {
    public int compare(Ingredient a, Ingredient b) {
        if (a.getPercent() > b.getPercent())
            return -1; // highest value first
        if (a.getPercent() == b.getPercent())
            return 0;
        return 1;
    }
}