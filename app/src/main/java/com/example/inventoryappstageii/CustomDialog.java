package com.example.inventoryappstageii;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.Window.FEATURE_NO_TITLE;

/**
 * Created by Mayank on 12/2/18 at 2:42 PM
 **/
public class CustomDialog extends Dialog implements View.OnClickListener {

    @BindView(R.id.supplierName)
    TextView name;

    @BindView(R.id.phone)
    TextView phone;


    private Context context;

    private String supplierPhone;

    CustomDialog(@NonNull Context context, String supplierName, String supplierPhone) {
        super(context);
        this.requestWindowFeature(FEATURE_NO_TITLE);

        //set custom dialog layout
        this.setContentView(R.layout.layout_custom_dialog);

        this.context = context;

        this.supplierPhone = supplierPhone;

        ButterKnife.bind(this);

        //display supplier name
        this.name.append("  " + supplierName);

        //verify if supplier phone is available and display/hide corresponding TextView accordingly
        if (supplierPhone.length() > 0) {
            phone.setVisibility(View.VISIBLE);
            phone.setText(String.format(context.getString(R.string.call_at), supplierPhone));
        } else {
            phone.setVisibility(View.GONE);
        }

        //set click listeners to start intents for email/phone

        phone.setOnClickListener(this);
    }

    /**
     * invoked on selecting phone/email from supplier contact dialog on the main screen
     *
     * @param view reference to view that registered the click event
     */
    @Override
    public void onClick(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.phone:
                //set phone dial intent
                intent.setAction(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + supplierPhone));
                break;
        }
        //start intent
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
            //dismiss once the intent was successfully handled
            dismiss();
        } else {
            Toast.makeText(context, "No suitable app to handle the current action", Toast.LENGTH_SHORT).show();
        }
    }
}
