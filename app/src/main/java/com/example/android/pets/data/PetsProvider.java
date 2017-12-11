package com.example.android.pets.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by Robo warrior on 04-12-2017.
 */

public class PetsProvider extends ContentProvider {
    private static final int PETS = 100;
    private static final int PETS_ID =101;
    private static final UriMatcher sUrimatcher= new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sUrimatcher.addURI("com.example.android.pets", PetContract.PetEntry.TABLE_NAME,PETS);
        sUrimatcher.addURI("com.example.android.pets", PetContract.PetEntry.TABLE_NAME+"/#",PETS_ID);
    }
    private PetDbHelper petDbHelper;
    @Override
    public boolean onCreate() {
        petDbHelper= new PetDbHelper(getContext());
        return true;

    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase sqLiteDatabase = petDbHelper.getReadableDatabase();
        Cursor cursor;
        int result = sUrimatcher.match(uri);
        switch(result)
        {
            case PETS:
                cursor = sqLiteDatabase.query(PetContract.PetEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            case PETS_ID:
                selection = PetContract.PetEntry._id + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = sqLiteDatabase.query(PetContract.PetEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown uri"+uri);

        }
        cursor.setNotificationUri(getContext().getContentResolver(),uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
          int result = sUrimatcher.match(uri);
          switch(result)
          {
              case PETS:
                  return insertPets(uri, contentValues);

                  default:
                      throw new IllegalArgumentException("Connot insert unknown uri"+uri);
          }
            }
    private Uri insertPets(Uri uri,ContentValues contentValues)
    {           SQLiteDatabase sqLiteDatabase = petDbHelper.getReadableDatabase();
               Long rowID= sqLiteDatabase.insert(PetContract.PetEntry.TABLE_NAME,null,contentValues);
               getContext().getContentResolver().notifyChange(uri,null);
               return Uri.withAppendedPath(uri,rowID.toString());

    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
     SQLiteDatabase sqLiteDatabase  = petDbHelper.getWritableDatabase();
       int rowId = sqLiteDatabase.delete(PetContract.PetEntry.TABLE_NAME,s,strings);
        getContext().getContentResolver().notifyChange(uri,null);
        return rowId;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        SQLiteDatabase sqLiteDatabase = petDbHelper.getWritableDatabase();

        int rowId=sqLiteDatabase.update(PetContract.PetEntry.TABLE_NAME,contentValues,s,strings);
        getContext().getContentResolver().notifyChange(uri,null);
        return rowId;
    }
}
