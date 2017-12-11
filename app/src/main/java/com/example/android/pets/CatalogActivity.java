/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.pets;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.pets.data.PetContract;
import com.example.android.pets.data.PetContract.PetEntry;
import com.example.android.pets.data.PetCursor;
import com.example.android.pets.data.PetDbHelper;


/**
 * Displays list of pets that were entered and stored in the app.
 */
public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private Uri uri;
    ListView listView;
    PetCursor petCursor;
    String toastmsg;
    //Cursor mcursor;
    String[] projection = {
            PetEntry._id,
            PetEntry.COLUMN_PET_NAME,
            PetEntry.COLUMN_PET_BREED,
            PetEntry.COLUMN_PET_GENDER,
            PetEntry.COLUMN_PET_WEIGHT
    };

    private String deletemsg;
    private final static int Url_loader = 0;
    PetDbHelper petDbHelper ;
    public static final class helperClass{
        public static int mposition;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);
        petDbHelper = new PetDbHelper(this);
        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });
        listView = (ListView) findViewById(R.id.listlayout);
        petCursor = new PetCursor(this,null);
        listView.setAdapter(petCursor);
        listView.setEmptyView(findViewById(R.id.empty_view));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
               Intent intent=new Intent(CatalogActivity.this,EditorActivity.class);
               Uri EditUri = ContentUris.withAppendedId(PetEntry.Content_Uri,id);
               helperClass.mposition=position;
               intent.setData(EditUri);
               startActivity(intent);

            }
        });
       getLoaderManager().initLoader(Url_loader,null,this);
    }
    @Override
    public void onStart()
    {
        super.onStart();


    }

    public void insertPet()
    {
        //SQLiteDatabase sqLiteDatabase = petDbHelper.getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(PetEntry.COLUMN_PET_NAME,"Toto");
        contentValues.put(PetEntry.COLUMN_PET_BREED,"Terrier");
        contentValues.put(PetEntry.COLUMN_PET_GENDER,PetEntry.GENDER_MALE);
        contentValues.put(PetEntry.COLUMN_PET_WEIGHT,7);
        long rowId;
        uri = getContentResolver().insert(PetEntry.Content_Uri,contentValues);
        rowId=Integer.parseInt(String.valueOf(ContentUris.parseId(uri)));
        if(rowId>=0)
            toastmsg = "Dummy pet data Inserted";
        else
            toastmsg="Error saving the Dummy pet info";

        Log.e("CatalogActivity","The row id"+ rowId);


    }
    private void deleteAllPets()
    {        Cursor dCursor = getContentResolver().query(PetEntry.Content_Uri,projection,null,null,null);

        int rowId = getContentResolver().delete(PetEntry.Content_Uri,null,null);
        Log.e("Catalog Activity","number of rows deleted"+rowId);
        if(rowId>=0 && dCursor.getCount()!=0)
            deletemsg = "All Pets Info deleted";
        else
            deletemsg="No data Found";
        dCursor.close();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertPet();
                Toast.makeText(this.getApplicationContext(),toastmsg,Toast.LENGTH_SHORT).show();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                deleteAllPets();
                Toast.makeText(this.getApplicationContext(),deletemsg,Toast.LENGTH_SHORT).show();

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        //Log.e("catalog activity","onCreateLoader");
       // cursor=getContentResolver().query(PetEntry.Content_Uri,projection,null,null,null);
        switch (i)
        {case Url_loader:
        return new CursorLoader(this,PetEntry.Content_Uri,projection,null,null,null);
        default:
            return null;
    }}

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        petCursor.swapCursor(cursor);
        Log.e("catalog activity","onLoadFinished");

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.e("catalog activity","onLoaderReset");

     petCursor.swapCursor(null);
    }
}
