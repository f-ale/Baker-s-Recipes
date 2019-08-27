package com.francescoalessi.recipes;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.francescoalessi.recipes.data.Recipe;
import com.francescoalessi.recipes.editing.EditRecipeActivity;

import java.util.List;

public class RecipeListAdapter extends RecyclerView.Adapter<RecipeListAdapter.RecipeViewHolder> {

    private List<Recipe> mRecipeList;
    private LayoutInflater mInflater;

    public RecipeListAdapter(Context context)
    {
        mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public RecipeListAdapter.RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = mInflater.inflate(R.layout.recipe_list_item, parent, false);
        return new RecipeViewHolder(mItemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeListAdapter.RecipeViewHolder holder, int position) {
        if(mRecipeList != null)
        {
            Recipe mCurrent = mRecipeList.get(position);
            holder.mRecipeNameTextView.setText(mCurrent.getRecipeName());
        }
        else
        {
            holder.mRecipeNameTextView.setText("No recipe");
        }

    }

    public void setRecipes(List<Recipe> recipes)
    {
        mRecipeList = recipes;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if(mRecipeList != null)
        {
            return mRecipeList.size();
        }
        else
            return 0;

    }

    class RecipeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        public final TextView mRecipeNameTextView;
        public final ImageView mRecipeThumbnailImageView;

        final RecipeListAdapter mAdapter;


        public RecipeViewHolder(@NonNull View itemView, RecipeListAdapter adapter) {
            super(itemView);

            mAdapter = adapter;
            mRecipeNameTextView = itemView.findViewById(R.id.tv_recipe_name);
            mRecipeThumbnailImageView = itemView.findViewById(R.id.iv_recipe_thumbnail);

            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
            int position = getLayoutPosition();
            Recipe recipe = mRecipeList.get(position);
            Context context = view.getContext();

            //start intent
            Intent intent = new Intent(context, EditRecipeActivity.class);
            intent.putExtra(MainActivity.EXTRA_RECIPE_ID, recipe.getId());
            context.startActivity(intent);

        }
    }
}
