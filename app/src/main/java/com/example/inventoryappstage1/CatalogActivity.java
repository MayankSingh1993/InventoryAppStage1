package com.example.inventoryappstage1;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.example.inventoryappstage1.data.BookStoreContract.BookStoreEntry;
import com.example.inventoryappstage1.data.BookStoreDbHelper;

public class CatalogActivity extends AppCompatActivity {

    private BookStoreDbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        mDbHelper = new BookStoreDbHelper(this);
        displayDatabaseInfo();

    }


    private void displayDatabaseInfo() {

        // Create and/or open a database to read from it
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Perform this raw SQL query "SELECT * FROM bookstore"
        // to get a Cursor that contains all rows from the bookstore table.

        String[] projection = {BookStoreEntry._ID, BookStoreEntry.COLUMN_PRODUCT_NAME, BookStoreEntry.COLUMN_PRODUCT_PRICE, BookStoreEntry.COLUMN_QUANTITY, BookStoreEntry.COLUMN_SUPPLIER_NAME, BookStoreEntry.COLUMN_SUPPLIER_PHONE_NUMBER};
        Cursor cursor = db.query(BookStoreEntry.TABLE_NAME, projection, null, null, null, null, null);

        try {
            // Display the number of rows in the Cursor (which reflects the number of rows in the
            // Bookstore table in the database).
            TextView displayView = (TextView) findViewById(R.id.text_view_bookstore);
            displayView.setText("The Bookstore table contains " + cursor.getCount() + " book.\n\n");

            displayView.append(BookStoreEntry._ID + " - " + BookStoreEntry.COLUMN_PRODUCT_NAME + " - " + BookStoreEntry.COLUMN_PRODUCT_PRICE + " - " + BookStoreEntry.COLUMN_QUANTITY + " - " + BookStoreEntry.COLUMN_SUPPLIER_NAME + " - " + BookStoreEntry.COLUMN_SUPPLIER_PHONE_NUMBER + "\n");
            // Figure out the index of each column
            int idColumnIndex = cursor.getColumnIndex(BookStoreEntry._ID);
            int nameColumnIndex = cursor.getColumnIndex(BookStoreEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(BookStoreEntry.COLUMN_PRODUCT_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(BookStoreEntry.COLUMN_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(BookStoreEntry.COLUMN_SUPPLIER_NAME);
            int supplierPhoneNumberColumnIndex = cursor.getColumnIndex(BookStoreEntry.COLUMN_SUPPLIER_PHONE_NUMBER);


            // Iterate through all the returned rows in the cursor
            while (cursor.moveToNext()) {
                // Use that index to extract the String or Int value of the word
                // at the current row the cursor is on.
                int currentID = cursor.getInt(idColumnIndex);
                String currentName = cursor.getString(nameColumnIndex);
                int price = cursor.getInt(priceColumnIndex);
                int quantity = cursor.getInt(quantityColumnIndex);
                String supplierName = cursor.getString(supplierNameColumnIndex);
                String supplierPhoneNumber = cursor.getString(supplierPhoneNumberColumnIndex);

                // Display the values from each column of the current row in the cursor in the TextView
                displayView.append(("\n" + currentID + " - " + currentName + " - " + price + " - " + quantity + " - " + supplierName + " - " + supplierPhoneNumber));
            }
        } finally {
            // Always close the cursor when you're done reading from it. This releases all its
            // resources and makes it invalid.
            cursor.close();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        displayDatabaseInfo();
    }


}
