package com.example.inventoryappstage1.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.inventoryappstage1.data.BookStoreContract.BookStoreEntry;

/**
 * Created by Mayank on 11/22/18 at 8:58 PM
 **/
public class BookStoreDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "bookstore.db";
    private static final int DATABASE_VERSION = 1;

    public BookStoreDbHelper(Context context) {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a String that contains the SQL statement to create the bookstore table
        String SQL_CREATE_BOOKSTORE_TABLE = "CREATE TABLE " + BookStoreEntry.TABLE_NAME + " (" + BookStoreEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + BookStoreEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL, " + BookStoreEntry.COLUMN_PRODUCT_PRICE + " INTEGER NOT NULL , " + BookStoreEntry.COLUMN_QUANTITY + " INTEGER NOT NULL DEFAULT 0, " + BookStoreEntry.COLUMN_SUPPLIER_NAME + " TEXT NOT NULL , " + BookStoreEntry.COLUMN_SUPPLIER_PHONE_NUMBER + " TEXT NOT NULL );";

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_BOOKSTORE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
