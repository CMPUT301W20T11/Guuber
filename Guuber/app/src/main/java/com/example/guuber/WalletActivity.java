package com.example.guuber;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;

/**
 * This class contains the activity the rider and/or driver
 * will be directed to upon selecting "Wallet"
 * in their menu
 * it enables their ability to view their wallet and balance details
 */
public class WalletActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);

        /**display the back button**/
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /**implement logic here for what you want to
     * happen upon back button press**/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
