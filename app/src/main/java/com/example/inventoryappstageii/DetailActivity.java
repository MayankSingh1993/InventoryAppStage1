package com.example.inventoryappstageii;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.inventoryappstageii.data.BookStoreContract.BookStoreEntry;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener {

    private static final int BOOKSTORE_LOADER = 0;
    /**
     * Content URI for the existing book (null if it's a new book)
     */
    private Uri mCurrentBookUri;
    /**
     * EditText field to enter the Product Name
     */
    @BindView(R.id.edit_product_name)
    EditText mProductNameEditText;

    /**
     * EditText field to enter the Product Price
     */
    @BindView(R.id.edit_product_price)
    EditText mProductPriceEditText;

    /**
     * EditText field to enter the Product Quantity
     */
    @BindView(R.id.edit_product_quantity)
    EditText mProductQuantityEditText;

    /**
     * EditText field to enter the Product Supplier Name
     */
    @BindView(R.id.edit_product_supplier_name)
    EditText mProductSupplierNameEditText;

    /**
     * EditText field to enter the Product Supplier Phone Number
     */
    @BindView(R.id.edit_product_supplier_phone_number)
    EditText mProductSupplierPhoneNumberEditText;

    @BindView(R.id.increaseQuantity)
    Button mIncreaseQuantity;

    @BindView(R.id.decreaseQuantity)
    Button mDecreaseQuantity;

    @BindView(R.id.order)
    Button mOrder;


    private boolean mBookHasChanged = false;
    private boolean saveSuccess = true;
    private Toast toast;

    @SuppressLint("ClickableViewAccessibility")
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ButterKnife.bind(this);

        Intent intent = getIntent();
        mCurrentBookUri = intent.getData();


        if (mCurrentBookUri == null) {

            setTitle(getString(R.string.editor_activity_title_new_book));

            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            // (It doesn't make sense to delete a book that hasn't been created yet.)
            invalidateOptionsMenu();


        } else {

            setTitle(getString(R.string.editor_activity_title_edit_book));
            getLoaderManager().initLoader(BOOKSTORE_LOADER, null, this);
        }


        // Setup OnTouchListeners on all the input fields, so we can determine if the user
        // has touched or modified them. This will let us know if there are unsaved changes
        // or not, if the user tries to leave the editor without saving.
        mProductNameEditText.setOnTouchListener(mTouchListener);
        mProductPriceEditText.setOnTouchListener(mTouchListener);
        mProductQuantityEditText.setOnTouchListener(mTouchListener);
        mProductSupplierNameEditText.setOnTouchListener(mTouchListener);
        mProductSupplierPhoneNumberEditText.setOnTouchListener(mTouchListener);

        //register listeners for increase and decrease quantity buttons
        mIncreaseQuantity.setOnClickListener(this);
        mDecreaseQuantity.setOnClickListener(this);

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

                saveData();
                //close the edit screen on successfully saving the data to the database
                if (saveSuccess) {
                    finish();
                    return true;
                }
                return false;

            case R.id.action_delete:
                // Pop up confirmation dialog for deletion
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the book hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!mBookHasChanged) {
                    NavUtils.navigateUpFromSameTask(DetailActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, navigate to parent activity.
                        NavUtils.navigateUpFromSameTask(DetailActivity.this);
                    }
                };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveData() {

        // Gets the database in write mode

        String productNameString = mProductNameEditText.getText().toString().trim();
        String productPriceString = mProductPriceEditText.getText().toString().trim();
        String quantityString = mProductQuantityEditText.getText().toString().trim();
        String productSupplierNameString = mProductSupplierNameEditText.getText().toString().trim();
        String productSupplierPhoneNumberString = mProductSupplierPhoneNumberEditText.getText().toString().trim();

        // Check if this is supposed to be a new book
        // and check if all the fields in the editor are blank
        if (mCurrentBookUri == null && TextUtils.isEmpty(productNameString) && TextUtils.isEmpty(productPriceString) && TextUtils.isEmpty(quantityString) && TextUtils.isEmpty(productSupplierNameString) && TextUtils.isEmpty(productSupplierPhoneNumberString)) {
            // Since no fields were modified, we can return early without creating a new book.
            // No need to create ContentValues and no need to do any ContentProvider operations.
            return;
        }
        //validate that the book title isn't an empty string
        if (TextUtils.isEmpty(productNameString)) {
            //set error accordingly
            mProductNameEditText.requestFocus();
            mProductNameEditText.setError(getString(R.string.title_empty_error));
            displayToastAlert(getString(R.string.title_empty_error));
            //indicate save wasn't successful
            saveSuccess = false;
            //hide soft keyboard to indicate to the user that field validation has failed
            BookUtils.hideSoftKeyboard(this);
            return;
        } else {
            //update save success flag if validation succeeds
            saveSuccess = true;
        }
        //validate price field isn't empty
        if (TextUtils.isEmpty(productPriceString)) {
            //set error accordingly
            mProductPriceEditText.setError("");
            displayToastAlert(getString(R.string.price_invalid_error));
            //indicate save wasn't successful
            saveSuccess = false;
            //hide soft keyboard to indicate to the user that field validation has failed
            BookUtils.hideSoftKeyboard(this);
            return;
        } else {
            //update save success flag if validation succeeds
            saveSuccess = true;
        }

        if (TextUtils.isEmpty(quantityString)) {
            //set error accordingly
            mProductQuantityEditText.setError("");
            displayToastAlert(getString(R.string.quantity_missing_error));
            //indicate save wasn't successful
            saveSuccess = false;
            //hide soft keyboard to indicate to the user that field validation has failed
            BookUtils.hideSoftKeyboard(this);
            return;
        } else {
            //update save success flag if validation succeeds
            saveSuccess = true;
        }

        if (TextUtils.isEmpty(productSupplierNameString)) {
            //set error accordingly
            mProductSupplierNameEditText.setError("");
            displayToastAlert(getString(R.string.supplier_name_error));
            //indicate save wasn't successful
            saveSuccess = false;
            //hide soft keyboard to indicate to the user that field validation has failed
            BookUtils.hideSoftKeyboard(this);
            return;
        } else {
            //update save success flag if validation succeeds
            saveSuccess = true;
        }

        if (TextUtils.isEmpty(productSupplierPhoneNumberString)) {
            //display error accordingly
            displayToastAlert(getString(R.string.supplier_details_missing_error));
            //indicate save wasn't successful
            saveSuccess = false;
            //hide soft keyboard to indicate to the user that field validation has failed
            BookUtils.hideSoftKeyboard(this);
            mProductSupplierPhoneNumberEditText.requestFocus();
            return;
        } else {
            //update save success flag if validation succeeds
            saveSuccess = true;
        }


        // Create a ContentValues object where column names are the keys,

        ContentValues values = new ContentValues();
        values.put(BookStoreEntry.COLUMN_PRODUCT_NAME, productNameString);
        values.put(BookStoreEntry.COLUMN_PRODUCT_PRICE, productPriceString);
        values.put(BookStoreEntry.COLUMN_QUANTITY, quantityString);
        values.put(BookStoreEntry.COLUMN_SUPPLIER_NAME, productSupplierNameString);
        values.put(BookStoreEntry.COLUMN_SUPPLIER_PHONE_NUMBER, productSupplierPhoneNumberString);

        if (mCurrentBookUri == null) {
            // Insert a new row for Book in the database, returning the ID of that new row.
            // The first argument for db.insert() is the BookStore table name.
            // The second argument provides the name of a column in which the framework
            // can insert NULL in the event that the ContentValues is empty (if
            // this is set to "null", then the framework will not insert a row when
            // there are no values).
            // The third argument is the ContentValues object containing the info for Book.
            // Insert a new bookstore into the provider, returning the content URI for the new book.
            Uri newUri = getContentResolver().insert(BookStoreEntry.CONTENT_URI, values);
            // Show a toast message depending on whether or not the insertion was successful
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.editor_insert_book_failed), Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_insert_book_successful), Toast.LENGTH_SHORT).show();
            }
        } else {
            int rowsAffected = getContentResolver().update(mCurrentBookUri, values, null, null);
            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(R.string.editor_update_book_failed), Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_update_book_successful), Toast.LENGTH_SHORT).show();
            }
        }


    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Perform this raw SQL query "SELECT * FROM bookstore"
        // to get a Cursor that contains all rows from the bookstore table.

        String[] projection = {BookStoreEntry._ID, BookStoreEntry.COLUMN_PRODUCT_NAME, BookStoreEntry.COLUMN_PRODUCT_PRICE, BookStoreEntry.COLUMN_QUANTITY, BookStoreEntry.COLUMN_SUPPLIER_NAME, BookStoreEntry.COLUMN_SUPPLIER_PHONE_NUMBER};
        return new CursorLoader(this, mCurrentBookUri, projection, null, null, null);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if (data.moveToFirst()) {
            // Find the columns of book attributes that we're interested in
            int nameColumnIndex = data.getColumnIndex(BookStoreEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = data.getColumnIndex(BookStoreEntry.COLUMN_PRODUCT_PRICE);
            int quantityColumnIndex = data.getColumnIndex(BookStoreEntry.COLUMN_QUANTITY);
            int supplierNameColumnIndex = data.getColumnIndex(BookStoreEntry.COLUMN_SUPPLIER_NAME);
            int supplierPhoneNumberColumnIndex = data.getColumnIndex(BookStoreEntry.COLUMN_SUPPLIER_PHONE_NUMBER);
            // Extract out the value from the Cursor for the given column index
            String name = data.getString(nameColumnIndex);
            double price = data.getDouble(priceColumnIndex);
            int quantity = data.getInt(quantityColumnIndex);
            String supplierName = data.getString(supplierNameColumnIndex);
            String supplierPhoneNumber = data.getString(supplierPhoneNumberColumnIndex);

            mProductNameEditText.setText(name);
            mProductPriceEditText.setText(String.valueOf(price));
            mProductQuantityEditText.setText(String.valueOf(quantity));
            mProductSupplierNameEditText.setText(supplierName);
            mProductSupplierPhoneNumberEditText.setText(supplierPhoneNumber);

            mOrder.setOnClickListener(this);
        }


    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        mProductNameEditText.setText("");
        mProductPriceEditText.setText("");
        mProductQuantityEditText.setText("");
        mProductSupplierNameEditText.setText("");
        mProductSupplierPhoneNumberEditText.setText("");

    }

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mBookHasChanged = true;
            return false;
        }
    };

    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the book.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        // If the book hasn't changed, continue with handling back button press
        if (!mBookHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // User clicked "Discard" button, close the current activity.
                finish();
            }
        };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new book, hide the "Delete" menu item.
        if (mCurrentBookUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the book.
                deletebook();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the book.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Perform the deletion of the book in the database.
     */
    private void deletebook() {
        // Only perform the delete if this is an existing book.
        if (mCurrentBookUri != null) {
            // Call the ContentResolver to delete the book at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentBookUri
            // content URI already identifies the book that we want.
            int rowsDeleted = getContentResolver().delete(mCurrentBookUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_book_failed), Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_book_successful), Toast.LENGTH_SHORT).show();
            }
            finish();
        }
    }

    /**
     * displays alert message in the form of a toast
     *
     * @param message message to be displayed to the user
     */
    private void displayToastAlert(String message) {
        //cancel any outstanding toast before displaying a new one
        cancelToast();
        toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    /**
     * cancel any existing toasts
     */
    private void cancelToast() {
        if (toast != null) {
            toast.cancel();
        }
    }

    @Override
    public void onClick(View view) {

        //declare and initialize variable to hold and update quantity
        int quantity = 0;
        if (mProductQuantityEditText.getText().length() > 0) {
            //fetch the quantity from the edit text
            quantity = Integer.parseInt(mProductQuantityEditText.getText().toString());
        }

        switch (view.getId()) {
            //handle incrementing the quantity
            case R.id.increaseQuantity:
                quantity++;
                updateQuantity(quantity);
                break;
            //handle decrementing the quantity
            case R.id.decreaseQuantity:
                if (quantity > 0) {
                    quantity--;
                    updateQuantity(quantity);
                } else {
                    //ensure that the quantity doesn't go negative
                    displayToastAlert(getString(R.string.negative_quantity_error));
                }
                break;

            case R.id.order:
                CustomDialog dialog = new CustomDialog(this, mProductSupplierNameEditText.getText().toString().trim(), mProductSupplierPhoneNumberEditText.getText().toString().trim());
                dialog.show();
                break;
        }
    }

    /**
     * displays the updated quantity value in the edit text field
     *
     * @param quantity new quantity value
     */
    private void updateQuantity(int quantity) {
        //update the edit text with the new value, clear error alert if any
        mProductQuantityEditText.setText(String.valueOf(quantity));
        mProductQuantityEditText.setSelection(mProductQuantityEditText.getText().length());
        mProductQuantityEditText.setError(null);
    }
}



