package com.francescoalessi.recipes.data.converters;

import android.net.Uri;

import androidx.room.TypeConverter;

public class UriTypeConverter
{

    @TypeConverter
    public static String toString(Uri uri)
    {
        return uri == null ? null : uri.toString();
    }

    @TypeConverter
    public static Uri toUri(String string)
    {
        return string == null ? null : Uri.parse(string);
    }
}

