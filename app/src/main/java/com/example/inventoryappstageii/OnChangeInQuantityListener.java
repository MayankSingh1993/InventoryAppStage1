package com.example.inventoryappstageii;

/**
 * Created by Mayank on 12/5/18 at 12:10 AM
 **/
interface OnChangeInQuantityListener {
    void updateQuantity(long rowId, int newQuantity);
}
