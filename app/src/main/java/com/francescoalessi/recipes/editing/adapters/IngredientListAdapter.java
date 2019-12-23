package com.francescoalessi.recipes.editing.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.RecyclerView;

import com.francescoalessi.recipes.R;
import com.francescoalessi.recipes.data.Ingredient;
import com.francescoalessi.recipes.editing.EditRecipeActivity;
import com.francescoalessi.recipes.utils.RecipeUtils;

import java.util.List;

public class IngredientListAdapter extends RecyclerView.Adapter<IngredientListAdapter.IngredientViewHolder>
{

    private List<Ingredient> mIngredientList;
    private LayoutInflater mInflater;
    private boolean editMode = true;
    private boolean calculateQuantities = false;
    private boolean byWeight = false;
    private Float totalWeight;
    private Context context;

    public IngredientListAdapter(Context context, boolean editMode)
    {
        onInstantiate(context);
        this.context = context;
        this.editMode = editMode;
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
    public IngredientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View mItemView = mInflater.inflate(R.layout.ingredient_list_item, parent, false);
        return new IngredientViewHolder(mItemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientViewHolder holder, int position)
    {
        if (mIngredientList != null)
        {
            Ingredient mCurrent = mIngredientList.get(position);
            holder.mIngredientNameTextView.setText(mCurrent.getName());

            String suffix;
            if(byWeight)
                suffix = "g";
            else
                suffix = "%";

            if (!calculateQuantities)
            {
                float maxIngredientPercent = RecipeUtils.getMaxIngredientPercent(mIngredientList);
                String percentString;

                if(!editMode)
                    percentString = RecipeUtils.getFormattedIngredientPercent(maxIngredientPercent, mCurrent.getPercent(), suffix);
                else
                    percentString = RecipeUtils.getFormattedIngredientPercent(mCurrent.getPercent(), suffix);

                holder.mIngredientPercentTextView.setText(percentString);
            }
            else
            {
                float percentSum = RecipeUtils.getPercentSum(mIngredientList);
                String weightString = RecipeUtils.getFormattedIngredientWeight(percentSum, totalWeight, mCurrent.getPercent());
                holder.mIngredientPercentTextView.setText(weightString);
            }
        }
        else
        {
            holder.mIngredientNameTextView.setText(context.getString(R.string.no_ingredient));
        }
    }

    public void setIngredients(List<Ingredient> ingredients)
    {
        mIngredientList = ingredients;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount()
    {
        if (mIngredientList != null)
        {
            return mIngredientList.size();
        }
        else
            return 0;

    }

    public void calculateQuantities(float totalWeight)
    {
        calculateQuantities = true;
        this.totalWeight = totalWeight;
        notifyDataSetChanged();
    }

    public void showPercent()
    {
        calculateQuantities = false;
        notifyDataSetChanged();
    }

    public void switchRepresentation()
    {
        byWeight = !byWeight;
        notifyDataSetChanged();
    }

    class IngredientViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private final TextView mIngredientNameTextView;
        private final TextView mIngredientPercentTextView;
        private final AppCompatImageButton mEditIngredientButton;

        IngredientListAdapter mAdapter;


        private IngredientViewHolder(@NonNull View itemView, IngredientListAdapter adapter)
        {
            super(itemView);

            mAdapter = adapter;
            mIngredientNameTextView = itemView.findViewById(R.id.tv_ingredient_list_name);
            mIngredientPercentTextView = itemView.findViewById(R.id.tv_ingredient_list_percent);
            mEditIngredientButton = itemView.findViewById(R.id.btn_edit_ingredient_list);

            if (adapter.editMode)
            {
                mEditIngredientButton.setOnClickListener(this);
            }
            else
            {
                mEditIngredientButton.setVisibility(View.GONE);
            }
        }


        @Override
        public void onClick(View view)
        {

            int position = getLayoutPosition();
            Ingredient ingredient = mIngredientList.get(position);
            Context context = view.getContext();

            if (view.getId() == R.id.btn_edit_ingredient_list)
            {
                EditRecipeActivity activity = (EditRecipeActivity) context;
                activity.launchEditDialogForId(ingredient.getId(), ingredient.getName(), ingredient.getPercent());
            }
        }
    }
}
