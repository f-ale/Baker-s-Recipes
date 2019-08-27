package com.francescoalessi.recipes.editing.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.francescoalessi.recipes.MainActivity;
import com.francescoalessi.recipes.R;
import com.francescoalessi.recipes.data.Ingredient;
import com.francescoalessi.recipes.editing.EditRecipeActivity;

import java.util.List;

public class IngredientListAdapter extends RecyclerView.Adapter<IngredientListAdapter.IngredientViewHolder> {

    private List<Ingredient> mIngredientList;
    private LayoutInflater mInflater;

    public IngredientListAdapter(Context context)
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
            holder.mIngredientPercentTextView.setText(mCurrent.getPercent() + "%"); // change this to use String.format to parse float
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

    class IngredientViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        public final TextView mIngredientNameTextView;
        public final TextView mIngredientPercentTextView;

            IngredientListAdapter mAdapter;


        public IngredientViewHolder(@NonNull View itemView, IngredientListAdapter adapter) {
            super(itemView);

            mAdapter = adapter;
            mIngredientNameTextView = itemView.findViewById(R.id.tv_ingredient_list_name);
            mIngredientPercentTextView = itemView.findViewById(R.id.tv_ingredient_list_percent);

            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {

            // Edit ingredient

            /*int position = getLayoutPosition();
            Ingredient ingredient = mIngredientList.get(position);
            Context context = view.getContext();

            //start intent
            Intent intent = new Intent(context, EditRecipeActivity.class);
            intent.putExtra(MainActivity.EXTRA_RECIPE_ID, ingredient.getId());
            context.startActivity(intent);*/

        }
    }
}
