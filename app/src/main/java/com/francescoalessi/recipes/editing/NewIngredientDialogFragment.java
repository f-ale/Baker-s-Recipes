package com.francescoalessi.recipes.editing;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.francescoalessi.recipes.R;
import com.francescoalessi.recipes.utils.RecipeUtils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class NewIngredientDialogFragment extends DialogFragment
{

    public interface NewIngredientDialogListener
    {
        void onDialogPositiveClick(DialogFragment dialog, String ingredientName, Float percent, int ingredientId);
    }

    private NewIngredientDialogListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState)
    {
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_new_ingredient, null);
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity());
        builder.setView(dialogView)
                .setTitle(R.string.new_ingredient)
                .setPositiveButton(R.string.add_ingredient, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        Dialog dialog = NewIngredientDialogFragment.this.getDialog();
                        if(dialog != null)
                        {
                            Bundle arguments = getArguments();
                            int ingredientId = -2;
                            if(arguments != null)
                            {
                                ingredientId = getArguments().getInt("INGREDIENT_ID");
                            }

                            EditText ingredientNameEditText = dialog.findViewById(R.id.et_ingredient_name);
                            EditText ingredientPercentEditText = dialog.findViewById(R.id.et_ingredient_percent);
                            String ingredientPercentString = ingredientPercentEditText.getText().toString();
                            String ingredientName = ingredientNameEditText.getText().toString();
                            if(!ingredientPercentString.equals("") && !ingredientName.equals(""))
                            {
                                Float ingredientPercent = Float.parseFloat(ingredientPercentString);
                                listener.onDialogPositiveClick(NewIngredientDialogFragment.this, ingredientName, ingredientPercent, ingredientId);
                            }
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        Dialog dialog = NewIngredientDialogFragment.this.getDialog();
                        if(dialog != null)
                            dialog.cancel();
                    }
                });

        setIngredientUI(dialogView);

        Dialog dialog = builder.create();


        return builder.create();
    }

    private void setIngredientUI(View view)
    {
        if(view != null)
        {
            EditText ingredientNameEditText = view.findViewById(R.id.et_ingredient_name);
            EditText ingredientPercentEditText = view.findViewById(R.id.et_ingredient_percent);

            Bundle arguments = getArguments();
            Log.d("FRAGMENT", (arguments != null) + "");
            if(arguments != null)
            {
                int ingredientId = arguments.getInt("INGREDIENT_ID");
                if(ingredientId != -1)
                {
                    ingredientNameEditText.setText(arguments.getString("INGREDIENT_NAME"));
                    float percent = arguments.getFloat("INGREDIENT_PERCENT");
                    ingredientPercentEditText.setText(RecipeUtils.getFormattedIngredientPercent(percent,null));
                }
            }
        }
    }

    @Override
    public void onAttach(@NonNull Context context)
    {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try
        {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = (NewIngredientDialogListener) context;
        }
        catch (ClassCastException e)
        {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString()
                    + " must implement NewIngredientDialogListener");
        }

    }
}
