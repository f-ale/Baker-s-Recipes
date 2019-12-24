package com.francescoalessi.recipes;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.francescoalessi.recipes.data.Recipe;
import com.francescoalessi.recipes.utils.RecipeUtils;

import java.util.List;

public class RecipeListAdapter extends RecyclerView.Adapter<RecipeListAdapter.RecipeViewHolder>
{
    private List<Recipe> mRecipeList;
    private LayoutInflater mInflater;

    public RecipeListAdapter(Context context)
    {
        mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public RecipeListAdapter.RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View mItemView = mInflater.inflate(R.layout.recipe_list_item, parent, false);
        return new RecipeViewHolder(mItemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeListAdapter.RecipeViewHolder holder, int position)
    {
        if (mRecipeList != null)
        {
            Recipe mCurrent = mRecipeList.get(position);
            holder.mRecipeNameTextView.setText(mCurrent.getRecipeName());
            loadThumbImage(mCurrent, holder.mRecipeThumbnailImageView);
        }
        else
        {
            holder.mRecipeNameTextView.setText(R.string.no_recipe);
        }
    }

    private void loadThumbImage(Recipe recipe, ImageView view) // TODO: Code reused with copy\paste, abstract instead
    {
        Uri uri = recipe.getRecipeImageUri();

        if (uri != null)
        {
            Glide.with(view.getContext()).load(uri)
                    .placeholder(R.drawable.ic_action_pick_image)
                    .centerCrop()
                    .apply(RequestOptions.circleCropTransform())
                    .into(view);
        }
        else
        {
            Drawable[] drawables = {new ColorDrawable(RecipeUtils.stringToColor(recipe.getRecipeName())), view.getContext().getResources().getDrawable(R.drawable.ic_chefhat_small)};
            LayerDrawable drawable = new LayerDrawable(drawables);
            Glide.with(view.getContext()).load(drawable)
                    .fitCenter()
                    .apply(RequestOptions.circleCropTransform())
                    .into(view);
        }
    }

    public void setRecipes(List<Recipe> recipes)
    {
        mRecipeList = recipes;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount()
    {
        if (mRecipeList != null)
        {
            return mRecipeList.size();
        }
        else
            return 0;

    }

    class RecipeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private final TextView mRecipeNameTextView;
        private final ImageView mRecipeThumbnailImageView;

        final RecipeListAdapter mAdapter;

        private RecipeViewHolder(@NonNull View itemView, RecipeListAdapter adapter)
        {
            super(itemView);

            mAdapter = adapter;
            mRecipeNameTextView = itemView.findViewById(R.id.tv_recipe_name);
            mRecipeThumbnailImageView = itemView.findViewById(R.id.iv_recipe_thumbnail);

            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View view)
        {
            int position = getLayoutPosition();
            Recipe recipe = mRecipeList.get(position);
            Context context = view.getContext();

            //start intent
            Intent intent = new Intent(context, ViewRecipeActivity.class);
            intent.putExtra(MainActivity.EXTRA_RECIPE_ID, recipe.getId());
            context.startActivity(intent);

        }
    }
}
