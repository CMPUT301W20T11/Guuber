package com.example.guuber;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.journeyapps.barcodescanner.BarcodeEncoder;

/**
 * This class contains the activity the rider
 * will be directed to upon selecting "generate QR"
 * in their menu
 * it enables their ability to generate a QR
 */

public class QrActivity extends AppCompatActivity {
	private ImageView qrImage;
	private Button qrButton;
	private TextView qrEText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_qr);

		qrImage = findViewById(R.id.qr_image);
		qrButton = findViewById(R.id.qr_button);
		qrEText = findViewById(R.id.qr_text);

		// Display the back button
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		// Get the passed qr info
		Intent intent = getIntent();
		String info = intent.getExtras().getString("INFO_TAG");

		// Generate a qr from the intent info
		genQR(info);


		qrButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				genQR();
			}
		});
	}

	/**
	 * TODO: This is just the method for user generated QR codes, just a proof of concept, delete later
	 */
	public void genQR(){
		String qrString = qrEText.getText().toString();
		// If no input is provided, prompt user for input
		if(TextUtils.isEmpty(qrString)) {
			Toast.makeText(QrActivity.this,"Please enter some text thanks man preciate it", Toast.LENGTH_SHORT).show();
		}else {
			MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
			try {
				BitMatrix bitMatrix = multiFormatWriter.encode(qrString, BarcodeFormat.QR_CODE, 1000, 1000);
				BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
				Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
				qrImage.setImageBitmap(bitmap);
			} catch (WriterException e) {
				e.printStackTrace(); }
		}
	}

	/**
	 * Generate a qr code with info, to be called during OnCreate
	 */
	public void genQR(String info){
		// If no input is provided, prompt user for input
		if(TextUtils.isEmpty(info)) {
			Toast.makeText(QrActivity.this,"Please enter some text thanks man preciate it", Toast.LENGTH_SHORT).show();
		}else {
			MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
			try {
				BitMatrix bitMatrix = multiFormatWriter.encode(info, BarcodeFormat.QR_CODE, 1000, 1000);
				BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
				Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
				qrImage.setImageBitmap(bitmap);
			} catch (WriterException e) {
				e.printStackTrace(); }
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

