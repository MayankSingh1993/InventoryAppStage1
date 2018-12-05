package com.example.inventoryappstageii.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.inventoryappstageii.data.BookStoreContract.BookStoreEntry;

/**
 * Created by Mayank on 11/22/18 at 8:58 PM
 **/
public class BookStoreDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "bookstore.db";
    private static final int DATABASE_VERSION = 1;
    // Create a String that contains the SQL statement to create the bookstore table
    private static final String SQL_CREATE_BOOKSTORE_TABLE =
            "CREATE TABLE " +
                    BookStoreEntry.TABLE_NAME +
                    " (" +
                    BookStoreEntry._ID +
                    " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    BookStoreEntry.COLUMN_PRODUCT_NAME +
                    " TEXT NOT NULL, " +
                    BookStoreEntry.COLUMN_PRODUCT_PRICE +
                    " REAL NOT NULL , " +
                    BookStoreEntry.COLUMN_QUANTITY +
                    " INTEGER NOT NULL DEFAULT 0, " +
                    BookStoreEntry.COLUMN_SUPPLIER_NAME +
                    " TEXT NOT NULL , " +
                    BookStoreEntry.COLUMN_SUPPLIER_PHONE_NUMBER +
                    " TEXT NOT NULL );";

    //define drop table query
    private static final String DROP_BOOKSTORE_TABLE =
            "DROP TABLE IF EXISTS " +
                    BookStoreEntry.TABLE_NAME;

    BookStoreDbHelper(Context context) {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_BOOKSTORE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL(DROP_BOOKSTORE_TABLE);
        onCreate(db);
    }
}
