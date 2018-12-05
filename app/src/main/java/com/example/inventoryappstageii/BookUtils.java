package com.example.inventoryappstageii;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

/**
 * Created by Mayank on 12/2/18 at 1:23 PM
 **/
class BookUtils {

    /**
     * hides soft keyboard
     *
     * @param activity reference to activity to access application resources
     */
    static void hideSoftKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    static void disableButton(Context context, Button saleButton) {

        if (saleButton.isEnabled()) {
            //disable the sale button
            saleButton.setEnabled(false);
            //change the button text color to gray
            saleButton.setTextColor(context.getResources().getColor(android.R.color.darker_gray));
            //change the background color to gray for order button
            if (saleButton.getId() == R.id.order) {
                ((GradientDrawable) saleButton.getBackground()).setColor((context.getResources().getColor(R.color.lighterGray)));
            }
        }
    }

    static void enableButton(Context context, Button saleButton) {
        if (!saleButton.isEnabled()) {
            //enable sale button in case it was previously disabled
            saleButton.setEnabled(true);
            //update the button text color for enabled state
            switch (saleButton.getId()) {
                case R.id.saleButton:
                    saleButton.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                    break;
                case R.id.order:
                    saleButton.setTextColor(context.getResources().getColor(android.R.color.white));
                    //restore the background color for order button
                    ((GradientDrawable) saleButton.getBackground()).setColor(context.getResources().getColor(R.color.colorAccent));
            }
        }

    }
}
