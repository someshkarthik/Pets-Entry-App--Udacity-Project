package com.example.android.pets.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Robo warrior on 03-12-2017.
 */

public final class PetContract {
    private PetContract()
    {

    }
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://com.example.android.pets/");
    private static final String Path_Pets = "pets";
    public static final class PetEntry implements BaseColumns{
        public static final Uri Content_Uri = Uri.withAppendedPath(BASE_CONTENT_URI,Path_Pets);
        public static final String TABLE_NAME = "pets";

        public final static String _id = BaseColumns._ID;
        public final static String COLUMN_PET_NAME = "name";
        public final static String  COLUMN_PET_BREED = "breed";
        public final static String COLUMN_PET_GENDER = "gender";
        public final static String COLUMN_PET_WEIGHT = "weight";

        public static final int GENDER_UNKNOWN = 0;
        public static final int GENDER_MALE = 1;
        public static final int GENDER_FEMALE = 2;
    }
}
