package com.francescoalessi.recipes.data;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.francescoalessi.recipes.data.converters.UriTypeConverter;

@Database(entities = {Recipe.class, Ingredient.class}, version = 5, exportSchema = false)
// TODO: set exportSchema to true later in development
@TypeConverters({UriTypeConverter.class})
public abstract class RecipeRoomDatabase extends RoomDatabase
{
    public abstract RecipeDao recipeDao();

    private static volatile RecipeRoomDatabase INSTANCE;

    static RecipeRoomDatabase getDatabase(final Context context)
    {
        if (INSTANCE == null)
        {
            synchronized (RecipeRoomDatabase.class)
            {
                if (INSTANCE == null)
                {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            RecipeRoomDatabase.class, "recipe_database").
                            addCallback(sRoomDatabaseCallback).
                            fallbackToDestructiveMigration().build(); // TODO: remove destructive migration eventually
                }
            }
        }

        return INSTANCE;
    }

    private static RoomDatabase.Callback sRoomDatabaseCallback =
            new RoomDatabase.Callback()
            {

                @Override
                public void onCreate(@NonNull SupportSQLiteDatabase db)
                {
                    super.onCreate(db);
                    new PopulateDbAsync(INSTANCE).execute();
                }
            };

    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void>
    {

        private final RecipeDao mDao;

        PopulateDbAsync(RecipeRoomDatabase db)
        {
            mDao = db.recipeDao();
        }

        @Override
        protected Void doInBackground(final Void... params)
        {
            //pre-populate here

            return null;
        }
    }
}


