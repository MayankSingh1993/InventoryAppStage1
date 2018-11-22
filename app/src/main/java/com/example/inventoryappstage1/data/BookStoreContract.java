package com.example.inventoryappstage1.data;

import android.provider.BaseColumns;

/**
 * Created by Mayank on 11/22/18 at 8:37 PM
 **/
public class BookStoreContract {
    public BookStoreContract() {
    }

    public static final class BookStoreEntry implements BaseColumns {

        public static final String TABLE_NAME = "bookstore";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_PRODUCT_NAME = "productname";
        public static final String COLUMN_PRODUCT_PRICE = "price";
        public static final String COLUMN_QUANTITY = "quantity";
        public static final String COLUMN_SUPPLIER_NAME = "suppliername";
        public static final String COLUMN_SUPPLIER_PHONE_NUMBER = "supplierphonenumber";

    }
}
