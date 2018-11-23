package com.example.inventoryappstage1;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.inventoryappstage1.data.BookStoreContract.BookStoreEntry;
import com.example.inventoryappstage1.data.BookStoreDbHelper;

public class EditorActivity extends AppCompatActivity {
    /**
     * EditText field to enter the Product Name
     */
    private EditText mProductNameEditText;

    /**
     * EditText field to enter the Product Price
     */

    private EditText mProductPriceEditText;

    /**
     * EditText field to enter the Product Quantity
     */

    private EditText mProductQuantityEditText;

    /**
     * EditText field to enter the Product Supplier Name
     */

    private EditText mProductSupplierNameEditText;

    /**
     * EditText field to enter the Product Supplier Phone Number
     */

    private EditText mProductSupplierPhoneNumberEditText;

    private BookStoreDbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);


        // Find all relevant views that we will need to read user input from

        mProductNameEditText = findViewById(R.id.edit_product_name);
        mProductPriceEditText = findViewById(R.id.edit_product_price);
        mProductQuantityEditText = findViewById(R.id.edit_product_quantity);
        mProductSupplierNameEditText = findViewById(R.id.edit_product_supplier_name);
        mProductSupplierPhoneNumberEditText = findViewById(R.id.edit_product_supplier_phone_number);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Do nothing for now
                insertData();
                finish();
                return true;

            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Do nothing for now
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // Navigate back to parent activity (CatalogActivity)
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void insertData() {

        // Gets the database in write mode

        String productNameString = mProductNameEditText.getText().toString().trim();
        String productPriceString = mProductPriceEditText.getText().toString().trim();
        String quantityString = mProductQuantityEditText.getText().toString().trim();
        int price = 0;
        int quantity = 0;
        try {
            price = Integer.parseInt(productPriceString);
            quantity = Integer.parseInt(quantityString);
        } catch (NumberFormatException nfe) {
            Toast.makeText(EditorActivity.this, "You entered a empty row", Toast.LENGTH_SHORT).show();
        }

        String productSupplierNameString = mProductSupplierNameEditText.getText().toString().trim();
        String productSupplierPhoneNumberString = mProductSupplierPhoneNumberEditText.getText().toString().trim();
        mDbHelper = new BookStoreDbHelper(this);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();


        // Create a ContentValues object where column names are the keys,

        ContentValues values = new ContentValues();
        values.put(BookStoreEntry.COLUMN_PRODUCT_NAME, productNameString);
        values.put(BookStoreEntry.COLUMN_PRODUCT_PRICE, price);
        values.put(BookStoreEntry.COLUMN_QUANTITY, quantity);
        values.put(BookStoreEntry.COLUMN_SUPPLIER_NAME, productSupplierNameString);
        values.put(BookStoreEntry.COLUMN_SUPPLIER_PHONE_NUMBER, productSupplierPhoneNumberString);
        // Insert a new row for Book in the database, returning the ID of that new row.
        // The first argument for db.insert() is the BookStore table name.
        // The second argument provides the name of a column in which the framework
        // can insert NULL in the event that the ContentValues is empty (if
        // this is set to "null", then the framework will not insert a row when
        // there are no values).
        // The third argument is the ContentValues object containing the info for Book.
        long newRowId = db.insert(BookStoreEntry.TABLE_NAME, null, values);

        // Show a toast message depending on whether or not the insertion was successful
        if (newRowId == -1) {
            // If the row ID is -1, then there was an error with insertion.
            Toast.makeText(this, "Error with saving book", Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the insertion was successful and we can display a toast with the row ID.
            Toast.makeText(this, "Book saved with row id: " + newRowId, Toast.LENGTH_SHORT).show();
        }


    }
}



