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

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.pets.data.PetContract;
import com.example.android.pets.data.PetContract.PetEntry;
import com.example.android.pets.data.PetDbHelper;

/**
 * Allows user to create a new pet or edit an existing one.
 */
public class EditorActivity extends AppCompatActivity {
    private PetDbHelper petDbHelper;

    /** EditText field to enter the pet's name */
    private EditText mNameEditText;

    /** EditText field to enter the pet's breed */
    private EditText mBreedEditText;

    /** EditText field to enter the pet's weight */
    private EditText mWeightEditText;

    /** EditText field to enter the pet's gender */
    private Spinner mGenderSpinner;
    private String toastmsg;
    private String deletemsg;
    private Uri uri;
    private Uri uri1;
    private Cursor cursor;

    /**
     * Gender of the pet. The possible values are:
     * 0 for unknown gender, 1 for male, 2 for female.
     */
    private int mGender = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        mNameEditText = (EditText) findViewById(R.id.edit_pet_name);
        mBreedEditText = (EditText) findViewById(R.id.edit_pet_breed);
        mWeightEditText = (EditText) findViewById(R.id.edit_pet_weight);
        mGenderSpinner = (Spinner) findViewById(R.id.spinner_gender);

        setupSpinner();
        petDbHelper = new PetDbHelper(this);
        Intent intent =getIntent();
        uri1 = intent.getData();
        String[] projection = {
                PetEntry._id,
                PetEntry.COLUMN_PET_NAME,
                PetEntry.COLUMN_PET_BREED,
                PetEntry.COLUMN_PET_GENDER,
                PetEntry.COLUMN_PET_WEIGHT
        };
        if(uri1 == null)
        setTitle("Add a Pet");
        else {
            setTitle("Edit pet Info");
           int position;
           position=CatalogActivity.helperClass.mposition;
            cursor = getContentResolver().query(PetEntry.Content_Uri,projection,null,null,null);
           if(cursor.moveToPosition(position))
            {mNameEditText.setText(cursor.getString(cursor.getColumnIndexOrThrow(PetEntry.COLUMN_PET_NAME)));
             mBreedEditText.setText(cursor.getString(cursor.getColumnIndexOrThrow(PetEntry.COLUMN_PET_BREED)));
            mWeightEditText.setText(cursor.getString(cursor.getColumnIndexOrThrow(PetEntry.COLUMN_PET_WEIGHT)));
            mGenderSpinner.setSelection(cursor.getInt(cursor.getColumnIndexOrThrow(PetEntry.COLUMN_PET_GENDER)));
            }
            cursor.close();
        }
        // Find all relevant views that we will need to read user input from

    }


    private void setupSpinner() {

        ArrayAdapter genderSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_gender_options, android.R.layout.simple_spinner_item);

        genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        mGenderSpinner.setAdapter(genderSpinnerAdapter);

        mGenderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.gender_male))) {
                        mGender = PetEntry.GENDER_MALE; // Male
                    } else if (selection.equals(getString(R.string.gender_female))) {
                        mGender = PetEntry.GENDER_FEMALE; // Female
                    } else {
                        mGender = PetEntry.GENDER_UNKNOWN; // Unknown
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mGender = PetEntry.GENDER_UNKNOWN; // Unknown
            }
        });
    }

    private void insertPet()
    {   boolean flag =true;
        String PETNAME = mNameEditText.getText().toString().trim();
        if(PETNAME.equals(""))
            flag =false;
        String BREEDNAME = mBreedEditText.getText().toString().trim();
        if(BREEDNAME.equals(""))
            flag=false;
        int WEIGHTQUANTITY ;
         if(mWeightEditText.getText().toString().trim().equals(""))
         {flag=false;
           WEIGHTQUANTITY=0;}
           else
              WEIGHTQUANTITY = Integer.parseInt(mWeightEditText.getText().toString().trim());
        ContentValues contentValues = new ContentValues();
        contentValues.put(PetEntry.COLUMN_PET_NAME,PETNAME);
        contentValues.put(PetEntry.COLUMN_PET_BREED,BREEDNAME);
        contentValues.put(PetEntry.COLUMN_PET_GENDER,mGender);
        contentValues.put(PetEntry.COLUMN_PET_WEIGHT,WEIGHTQUANTITY);
        long rowId=-1;
        int updatedRowId=-1;
        if(uri1==null) {
            if(flag){
            uri = getContentResolver().insert(PetEntry.Content_Uri, contentValues);
            rowId = Integer.parseInt(String.valueOf(ContentUris.parseId(uri)));}
            if(rowId>=0)
                toastmsg = "Pet info saved";
            else
                toastmsg="Error saving the pet info";
        }
        else
        {   if(flag)
        {
            String selection = PetEntry._id + "=?";
            String[] selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri1))};
            updatedRowId = getContentResolver().update(PetEntry.Content_Uri,contentValues,selection,selectionArgs);}
            if(updatedRowId>=0)
                toastmsg = "Pet info updated";
            else
                toastmsg="Error updating the pet info";
        }

    }
    private void deletePet()
    {   String selection = PetEntry._id + "=?";
            String[] selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri1))};
        int rowId = getContentResolver().delete(PetEntry.Content_Uri,selection,selectionArgs);
        Log.e("Editor Activity","the row number deleted :"+rowId);
        if(rowId>=0)
            deletemsg = "Pet info deleted";
        else
            deletemsg="Error deleting the pet info";
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                 insertPet();
                finish();
                Toast.makeText(this.getApplicationContext(),toastmsg,Toast.LENGTH_SHORT).show();

                return true;

            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                if(uri1!=null) {
                    deletePet();
                    finish();
                    Toast.makeText(this.getApplicationContext(), deletemsg, Toast.LENGTH_SHORT).show();
                }
                else
                    Toast.makeText(this.getApplicationContext(),"No data found",Toast.LENGTH_SHORT).show();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // Navigate back to parent activity (CatalogActivity)
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}