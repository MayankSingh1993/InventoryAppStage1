package com.example.inventoryappstageii.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.example.inventoryappstageii.data.BookStoreContract.BookStoreEntry;

/**
 * Created by Mayank on 11/23/18 at 6:27 AM
 **/
public class BookStoreProvider extends ContentProvider {

    /**
     * URI matcher code for the content URI for the bookstore table
     */
    private static final int BOOKSTORE = 100;

    /**
     * URI matcher code for the content URI for a single book in the bookstore table
     */
    private static final int BOOKSTORE_ID = 101;

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = BookStoreProvider.class.getSimpleName();

    // Static initializer. This is run the first time anything is called from this class.
    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.

        sUriMatcher.addURI(BookStoreContract.CONTENT_AUTHORITY, BookStoreContract.PATH_BOOKSTORE, BOOKSTORE);
        sUriMatcher.addURI(BookStoreContract.CONTENT_AUTHORITY, BookStoreContract.PATH_BOOKSTORE + "/#", BOOKSTORE_ID);

    }

    private BookStoreDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new BookStoreDbHelper(getContext());
        return true;
    }


    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKSTORE:
                // For the bookstore code, query the bookstore table directly with the given
                // projection, selection, selection arguments, and sort order. The cursor
                // could contain multiple rows of the bookstore table.
                cursor = database.query(BookStoreEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case BOOKSTORE_ID:
                // For the BOOKSTORE_ID code, extract out the ID from the URI.
                // For an example URI such as "content://com.example.inventoryappstageii/bookstore/4",
                // the selection will be "_id=?" and the selection argument will be a
                // String array containing the actual ID of 4 in this case.
                //
                // For every "?" in the selection, we need to have an element in the selection
                // arguments that will fill in the "?". Since we have 1 question mark in the
                // selection, we have 1 String in the selection arguments' String array.
                selection = BookStoreEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                // This will perform a query on the bookstore table where the _id equals 4 to return a
                // Cursor containing that row of the table.
                cursor = database.query(BookStoreEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        // Set notification URI on the Cursor,
        // so we know what content URI the Cursor was created for.
        // If the data at this URI changes, then we know we need to update the Cursor.
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }


    @Override
    public Uri insert(@NonNull Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKSTORE:
                if (contentValues != null) {
                    validateInsertData(contentValues);
                    return insertBook(uri, contentValues);
                }
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private void validateInsertData(ContentValues values) {
        // Check that the name is not null
        String name = values.getAsString(BookStoreEntry.COLUMN_PRODUCT_NAME);
        if (name == null || TextUtils.isEmpty(name)) {
            throw new IllegalArgumentException("Book requires a name");
        }

        // Check that the price  cannot be -ve.
        String priceInString = values.getAsString(BookStoreEntry.COLUMN_PRODUCT_PRICE);
        double price = priceInString != null && !TextUtils.isEmpty(priceInString) ? Double.parseDouble(priceInString) : 0;
        if (price < 0) {
            throw new IllegalArgumentException("Price cannot be negative or zero");
        }
        // Check that the quantity cannot be -ve.
        String quantityInString = values.getAsString(BookStoreEntry.COLUMN_QUANTITY);
        int quantity = quantityInString != null && !TextUtils.isEmpty(quantityInString) ? Integer.parseInt(quantityInString) : 0;
        if (quantity < 0) {
            throw new IllegalArgumentException("Book requires a quantity");
        }

        // Check that the name is not null
        String supplierName = values.getAsString(BookStoreEntry.COLUMN_SUPPLIER_NAME);
        if (supplierName == null || TextUtils.isEmpty(supplierName)) {
            throw new IllegalArgumentException("Book requires a supplier name");
        }
        // Check that the name is not null
        String supplierPhoneNumber = values.getAsString(BookStoreEntry.COLUMN_SUPPLIER_PHONE_NUMBER);
        if (supplierPhoneNumber == null) {
            throw new IllegalArgumentException("Book requires a supplierPhoneNumber");
        }
    }

    /**
     * Insert a pet into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertBook(Uri uri, ContentValues values) {

        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Insert the new book data with the given values
        long id = database.insert(BookStoreEntry.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        // Notify all listeners that the data has changed for the book content URI
        getContext().getContentResolver().notifyChange(uri, null);

        // Once we know the ID of the new row in the table,
        // return the new URI with the ID appended to the end of it
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Track the number of rows that were deleted
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKSTORE:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(BookStoreEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case BOOKSTORE_ID:
                // Delete a single row given by the ID in the URI
                selection = BookStoreEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(BookStoreEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        // If 1 or more rows were deleted, then notify all listeners that the data at the
        // given URI has changed
        if (rowsDeleted != 0 && getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        // Return the number of rows deleted
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        validateUpdateData(values);
        switch (match) {
            case BOOKSTORE:
                return updateBook(uri, values, selection, selectionArgs);
            case BOOKSTORE_ID:
                // For the BOOKSTORE_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = BookStoreEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateBook(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private void validateUpdateData(ContentValues values) {
        // If the {@link BookStoreEntry#COLUMN_PRODUCT_NAME} key is present,
        // check that the name value is not null.
        if (values.containsKey(BookStoreEntry.COLUMN_PRODUCT_NAME)) {
            String name = values.getAsString(BookStoreEntry.COLUMN_PRODUCT_NAME);
            if (name == null || TextUtils.isEmpty(name) || TextUtils.isEmpty(name)) {
                throw new IllegalArgumentException("Book requires a name");
            }
        }

        // If the {@link BookStoreEntry#COLUMN_PRODUCT_PRICE} key is present,
        // check that the price value is valid.
        if (values.containsKey(BookStoreEntry.COLUMN_PRODUCT_PRICE) && values.getAsString(BookStoreEntry.COLUMN_PRODUCT_PRICE).length() > 0) {
            double price = values.getAsDouble(BookStoreEntry.COLUMN_PRODUCT_PRICE);
            if (price < 0) {
                throw new IllegalArgumentException("Price cannot be zero or negative");
            }
        }

        // If the {@link BookStoreEntry#COLUMN_QUANTITY} key is present,
        // check that the quantity value is valid.
        if (values.containsKey(BookStoreEntry.COLUMN_QUANTITY)) {
            // Check that the quantity cannot be -ve.
            int quantity = Integer.parseInt(values.getAsString(BookStoreEntry.COLUMN_QUANTITY));
            if (quantity < 0) {
                throw new IllegalArgumentException("Book requires valid quantity");
            }
        }

        // If the {@link BookStoreEntry#COLUMN_SUPPLIER_NAME} key is present,
        // check that the supplier name value is not null.
        if (values.containsKey(BookStoreEntry.COLUMN_SUPPLIER_NAME)) {
            String supplierName = values.getAsString(BookStoreEntry.COLUMN_SUPPLIER_NAME);
            if (supplierName == null || TextUtils.isEmpty(supplierName)) {
                throw new IllegalArgumentException("Book requires a supplier name");
            }
        }
        // If the {@link BookStoreEntry#COLUMN_SUPPLIER_PHONE_NUMBER} key is present,
        // check that the supplier phone number value is not null.
        if (values.containsKey(BookStoreEntry.COLUMN_SUPPLIER_PHONE_NUMBER)) {
            String supplierPhoneNumber = values.getAsString(BookStoreEntry.COLUMN_SUPPLIER_PHONE_NUMBER);
            if (TextUtils.isEmpty(supplierPhoneNumber)) {
                throw new IllegalArgumentException("Book requires a supplier's valid phone Number");
            }
        }
    }

    /**
     * Update bookstore in the database with the given content values. Apply the changes to the rows
     * specified in the selection and selection arguments (which could be 0 or 1 or more books).
     * Return the number of rows that were successfully updated.
     */
    private int updateBook(Uri uri, ContentValues values, String selection, String[] selectionArgs) {


        // If there are no values to update, then don't try to update the database
        if (values == null || values.size() == 0) {
            return 0;
        }

        // Otherwise, get writeable database to update the data
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Perform the update on the database and get the number of rows affected
        int rowsUpdated = database.update(BookStoreEntry.TABLE_NAME, values, selection, selectionArgs);

        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        // Return the number of rows updated
        return rowsUpdated;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKSTORE:
                return BookStoreEntry.CONTENT_LIST_TYPE;
            case BOOKSTORE_ID:
                return BookStoreEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

}
