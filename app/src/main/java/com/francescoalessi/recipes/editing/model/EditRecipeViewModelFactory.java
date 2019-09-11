package com.francescoalessi.recipes.editing.model;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class EditRecipeViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final Application application;
    private final int recipeId;

    public EditRecipeViewModelFactory(Application application, int recipeId)
    {
        this.application = application;
        this.recipeId = recipeId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new EditRecipeViewModel(application, recipeId);
    }
}
