package com.example.inventoryappstageii.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Mayank on 11/22/18 at 8:37 PM
 **/
public class BookStoreContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public BookStoreContract() {
    }

    /**
     * The "Content authority" is a name for the entire content provider, similar to the
     * relationship between a domain name and its website.  A convenient string to use for the
     * content authority is the package name for the app, which is guaranteed to be unique on the
     * device.
     */
    static final String CONTENT_AUTHORITY = "com.example.inventoryappstageii";
    /**
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider.
     */
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    /**
     * Possible path (appended to base content URI for possible URI's)
     * For instance, content://com.example.inventoryappstageii/bookstore/ is a valid path for
     * looking at bookstore data.
     */
    static final String PATH_BOOKSTORE = "bookstore";

    /**
     * Inner class that defines constant values for the bookstore database table.
     * Each entry in the table represents a single Book Info.
     */
    public static final class BookStoreEntry implements BaseColumns {


        /**
         * The content URI to access the bookstore data in the provider
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_BOOKSTORE);
        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of books.
         */
        static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKSTORE;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single book.
         */
        static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKSTORE;

        /**
         * Name of database table for bookstore
         */
        static final String TABLE_NAME = "bookstore";

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_PRODUCT_NAME = "productname";
        public static final String COLUMN_PRODUCT_PRICE = "price";
        public static final String COLUMN_QUANTITY = "quantity";
        public static final String COLUMN_SUPPLIER_NAME = "suppliername";
        public static final String COLUMN_SUPPLIER_PHONE_NUMBER = "supplierphonenumber";

    }
}
