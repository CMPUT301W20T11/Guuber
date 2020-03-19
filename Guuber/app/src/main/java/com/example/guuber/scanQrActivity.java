package com.example.guuber;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

/**
 * This class contains the activity the driver
 * will be directed to upon selecting "scan QR"
 * in their menu
 * it enables their ability to scan a QR
 * Source: https://www.youtube.com/watch?v=Fe7F4Jx7rwo
 */
public class scanQrActivity extends AppCompatActivity {


	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qr);
		Button scan_button = findViewById(R.id.scan_button);
        final Activity activity = this;

        scan_button.setOnClickListener(v -> {
			IntentIntegrator integrator = new IntentIntegrator(activity);
			integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
			integrator.setPrompt("Scan");
			integrator.setCameraId(0);
			integrator.setBeepEnabled(false);
			integrator.setBarcodeImageEnabled(false);
			integrator.initiateScan();
		});

    }

	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
		if(result != null){
			if(result.getContents() == null){
				Toast.makeText(this, "You cancelled the scanning", Toast.LENGTH_LONG).show();
			}else{
				Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();
			}
		}
		else{
			super.onActivityResult(requestCode, resultCode, data);
		}
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
