package com.francescoalessi.recipes.editing;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.francescoalessi.recipes.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class NewRecipeDialogFragment extends DialogFragment
{

    public interface NewRecipeDialogListener
    {
        void onDialogPositiveClick(DialogFragment dialog, String recipeName);
    }

    private NewRecipeDialogListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState)
    {
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity());
        builder.setView(inflater.inflate(R.layout.dialog_new_recipe, null))
                .setTitle(R.string.new_recipe)
                .setPositiveButton(R.string.create_recipe, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        EditText recipeNameEditText = NewRecipeDialogFragment.this.getDialog().findViewById(R.id.et_recipe_name);
                        String recipeName = recipeNameEditText.getText().toString();
                        listener.onDialogPositiveClick(NewRecipeDialogFragment.this, recipeName);

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        Dialog dialog = NewRecipeDialogFragment.this.getDialog();

                        if(dialog != null)
                            dialog.cancel();
                    }
                });

        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context)
    {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try
        {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = (NewRecipeDialogListener) context;
        }
        catch (ClassCastException e)
        {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString()
                    + " must implement NewIngredientDialogListener");
        }

    }
}
