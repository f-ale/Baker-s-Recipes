package com.francescoalessi.recipes.editing.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.RecyclerView;

import com.francescoalessi.recipes.MainActivity;
import com.francescoalessi.recipes.R;
import com.francescoalessi.recipes.data.Ingredient;
import com.francescoalessi.recipes.data.comparators.CompareIngredientPercent;
import com.francescoalessi.recipes.editing.AddIngredientActivity;
import com.francescoalessi.recipes.utils.RecipeUtils;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;

public class IngredientListAdapter extends RecyclerView.Adapter<IngredientListAdapter.IngredientViewHolder> {

    private List<Ingredient> mIngredientList;
    private LayoutInflater mInflater;
    public boolean editMode = true;
    public boolean calculateQuantities = false;
    private Ingredient mMaxIngredient;
    private Float totalWeigth;

    public IngredientListAdapter(Context context, boolean editMode)
    {
        onInstantiate(context);
        this.editMode = editMode;
    }

    private float getPercentSum(List<Ingredient> ingredientList)
    {
        float percentSum = 0;

        for (Ingredient ingredient : ingredientList)
        {
            percentSum += ingredient.getPercent();
        }

        return percentSum;
    }

    private float getIngredientWeight(float totalWeigth, float percentSum, Ingredient ingredient)
    {
        float weight = ((ingredient.getPercent() / percentSum) * totalWeigth);
        return weight;
    }

    public IngredientListAdapter(Context context)
    {
        onInstantiate(context);
    }

    private void onInstantiate(Context context)
    {
        mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public IngredientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = mInflater.inflate(R.layout.ingredient_list_item, parent, false);
        return new IngredientViewHolder(mItemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientViewHolder holder, int position) {
        if(mIngredientList != null)
        {
            Ingredient mCurrent = mIngredientList.get(position);
            holder.mIngredientNameTextView.setText(mCurrent.getName());

            if(!calculateQuantities)
            {
                String percentString = RecipeUtils.getFormattedIngredientPercent(mCurrent.getPercent());
                holder.mIngredientPercentTextView.setText(percentString);
            }
            else
            {
                float percentSum = getPercentSum(mIngredientList);

                String weightString = RecipeUtils.getFormattedIngredientWeight(percentSum, totalWeigth, mCurrent.getPercent());
                holder.mIngredientPercentTextView.setText(weightString);
            }
        }
        else
        {
            holder.mIngredientNameTextView.setText("No ingredient");
        }
    }

    public void setIngredients(List<Ingredient> ingredients)
    {
        mIngredientList = ingredients;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if(mIngredientList != null)
        {
            return mIngredientList.size();
        }
        else
            return 0;

    }

    public void calculateQuantities(float totalWeigth)
    {
        calculateQuantities = true;
        this.totalWeigth = totalWeigth;
        notifyDataSetChanged();
    }

    public void showPercent()
    {
        calculateQuantities = false;
        notifyDataSetChanged();
    }

    class IngredientViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        public final TextView mIngredientNameTextView;
        public final TextView mIngredientPercentTextView;
        public final AppCompatImageButton mEditIngredientButton;

        IngredientListAdapter mAdapter;


        public IngredientViewHolder(@NonNull View itemView, IngredientListAdapter adapter) {
            super(itemView);

            mAdapter = adapter;
            mIngredientNameTextView = itemView.findViewById(R.id.tv_ingredient_list_name);
            mIngredientPercentTextView = itemView.findViewById(R.id.tv_ingredient_list_percent);
            mEditIngredientButton = itemView.findViewById(R.id.btn_edit_ingredient_list);

            if(adapter.editMode)
            {
                mEditIngredientButton.setOnClickListener(this);
            }
            else
            {
                mEditIngredientButton.setVisibility(View.GONE);
            }
        }


        @Override
        public void onClick(View view) {

            int position = getLayoutPosition();
            Ingredient ingredient = mIngredientList.get(position);
            Context context = view.getContext();

            if(view.getId() == R.id.btn_edit_ingredient_list)
            {
                editIngredient(ingredient, context);
            }
        }

        private void editIngredient(Ingredient ingredient, Context context)
        {
            Intent intent = new Intent(context, AddIngredientActivity.class);
            intent.putExtra(MainActivity.EXTRA_INGREDIENT_ID, ingredient.getId());
            context.startActivity(intent);
        }
    }
}
