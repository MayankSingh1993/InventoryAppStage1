package com.example.inventoryappstageii;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
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
import android.widget.ListView;

import com.example.inventoryappstageii.data.BookStoreContract.BookStoreEntry;

public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, OnChangeInQuantityListener {


    private static final int BOOK_LOADER = 0;

    BookStoreCursorAdapter mCursorAdaptor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, DetailActivity.class);
                startActivity(intent);
            }
        });

        // Find the ListView which will be populated with the book data
        ListView bookListView = findViewById(R.id.list);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        bookListView.setEmptyView(emptyView);


        mCursorAdaptor = new BookStoreCursorAdapter(this, null);
        bookListView.setAdapter(mCursorAdaptor);

        //setup item click listener
        bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(CatalogActivity.this, DetailActivity.class);

                Uri currentBookUri = ContentUris.withAppendedId(BookStoreEntry.CONTENT_URI, id);

                intent.setData(currentBookUri);

                startActivity(intent);
            }
        });
        //kick of the loader
        getLoaderManager().initLoader(BOOK_LOADER, null, this);


    }

    @Override
    public android.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Perform this raw SQL query "SELECT * FROM bookstore"
        // to get a Cursor that contains all rows from the bookstore table.

        String[] projection = {BookStoreEntry._ID, BookStoreEntry.COLUMN_PRODUCT_NAME, BookStoreEntry.COLUMN_PRODUCT_PRICE, BookStoreEntry.COLUMN_QUANTITY, BookStoreEntry.COLUMN_SUPPLIER_NAME, BookStoreEntry.COLUMN_SUPPLIER_PHONE_NUMBER};
        return new CursorLoader(this, BookStoreEntry.CONTENT_URI, projection, null, null, null);


    }

    @Override
    public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor data) {
        mCursorAdaptor.swapCursor(data);
    }

    @Override
    public void onLoaderReset(android.content.Loader<Cursor> loader) {
        mCursorAdaptor.swapCursor(null);
    }

    /* Helper method to delete all bookstore in the database.
     */
    private void deleteAllBooks() {
        int rowsDeleted = getContentResolver().delete(BookStoreEntry.CONTENT_URI, null, null);
        Log.v("CatalogActivity", rowsDeleted + " rows deleted from book database");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {

            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                deleteAllBooks();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }


    @Override
    public void updateQuantity(long rowId, int newQuantity) {
        //set up a content values object and set the quantity value
        ContentValues contentValues = new ContentValues();
        contentValues.put(BookStoreEntry.COLUMN_QUANTITY, newQuantity);
        //define the specific product uri based on the row id
        Uri updateUri = ContentUris.withAppendedId(BookStoreEntry.CONTENT_URI, rowId);
        //invoke the update action via the content resolver
        getContentResolver().update(updateUri, contentValues, null, null);
    }
}
