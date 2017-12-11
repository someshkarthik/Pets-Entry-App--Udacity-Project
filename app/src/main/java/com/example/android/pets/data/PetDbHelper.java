package com.example.android.pets.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.android.pets.data.PetContract.PetEntry;


/**
 * Created by Robo warrior on 03-12-2017.
 */

public class PetDbHelper extends SQLiteOpenHelper {
     private static final String DATABASE_NAME = "shelter.db";
     private static final int DATABASE_VERSION = 1;
    public PetDbHelper(Context context)
    {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String Create_Dp_Table = "CREATE TABLE " + PetEntry.TABLE_NAME + "("
                + PetEntry._id+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + PetEntry.COLUMN_PET_NAME   + " TEXT NOT NULL,"
                + PetEntry.COLUMN_PET_BREED  + " TEXT NOT NULL,"
                + PetEntry.COLUMN_PET_GENDER + " INTEGER NOT NULL,"
                + PetEntry.COLUMN_PET_WEIGHT + " INTEGER NOT NULL DEFAULT 0);";
        sqLiteDatabase.execSQL(Create_Dp_Table);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
