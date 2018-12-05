package com.example.inventoryappstageii;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.inventoryappstageii.data.BookStoreContract.BookStoreEntry;

/**
 * Created by Mayank on 11/27/18 at 12:49 PM
 **/
public class BookStoreCursorAdapter extends CursorAdapter {

    //listener for sale button
    private OnChangeInQuantityListener listener;
    private Toast toast;

    BookStoreCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
        //initialize the listener
        listener = (OnChangeInQuantityListener) context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a list item view using the layout specified in list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {

        // Find individual views that we want to modify in the list item layout
        TextView nameTextView = view.findViewById(R.id.name);
        TextView quantityTextView = view.findViewById(R.id.quantity);
        TextView priceTextView = view.findViewById(R.id.price);
        final Button saleButton = view.findViewById(R.id.saleButton);

        //set the title TextView to not extend to more than 1 line
        nameTextView.setSingleLine();
        // Find the columns of book attributes that we're interested in
        int rowIndex = cursor.getColumnIndex(BookStoreEntry._ID);
        int nameColumnIndex = cursor.getColumnIndex(BookStoreEntry.COLUMN_PRODUCT_NAME);
        int quantityColumnIndex = cursor.getColumnIndex(BookStoreEntry.COLUMN_QUANTITY);
        int priceColumnIndex = cursor.getColumnIndex(BookStoreEntry.COLUMN_PRODUCT_PRICE);
        // Read the book attributes from the Cursor for the current book
        final long rowId = cursor.getLong(rowIndex);
        String productName = cursor.getString(nameColumnIndex);
        double price = cursor.getDouble(priceColumnIndex);
        final int[] quantity = {cursor.getInt(quantityColumnIndex)};

        // Update the TextViews with the attributes for the current book
        nameTextView.setText(productName);
        priceTextView.setText(String.valueOf(price));
        quantityTextView.setText(String.valueOf(quantity[0]));

        //enable/disable sale button depending on stock availability
        if (quantity[0] == 0) {
            BookUtils.disableButton(context, saleButton);
        } else {
            BookUtils.enableButton(context, saleButton);
        }

        //set on click listener for the sale button
        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    //decrease the product quantity, ensuring it doesn't go below 0
                    if (quantity[0] > 0) {
                        quantity[0]--;
                        //call update quantity to update the value in the database
                        listener.updateQuantity(rowId, quantity[0]);
                    }
                    if (quantity[0] == 0) {
                        if (toast != null) {
                            //cancel any outstanding toasts
                            toast.cancel();
                        }
                        //
                        //display out of stock error when the quantity reduces to 0
                        toast = Toast.makeText(context, R.string.out_of_stock, Toast.LENGTH_SHORT);
                        toast.show();
                        //disable sale button
                        BookUtils.disableButton(context, saleButton);
                    } else {
                        //if product is available in stock, enable sale button
                        BookUtils.enableButton(context, saleButton);
                    }
                }
            }
        });

    }
}
