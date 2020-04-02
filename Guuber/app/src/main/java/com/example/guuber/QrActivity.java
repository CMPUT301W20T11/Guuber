package com.example.guuber;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.Arrays;
import java.util.List;

/**
 * This class contains the activity the rider
 * will be directed to upon selecting "generate QR"
 * in their menu
 * it enables their ability to generate a QR
 */

public class QrActivity extends AppCompatActivity {
	private static final String TAG = "QrActivity";

	private ImageView qrImage;
//	private Button qrButton;
	private TextView qrEText;

	private List<String> qrInfo;
	private int changeCount = 0;

	private FirebaseFirestore db = FirebaseFirestore.getInstance();
	private CollectionReference usersCol = db.collection("Users");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_qr);

		qrImage = findViewById(R.id.qr_image);
//		qrButton = findViewById(R.id.qr_button);
		qrEText = findViewById(R.id.qr_text);


		// Get the passed qr info
		Intent intent = getIntent();
		String info = intent.getExtras().getString("INFO_TAG");

		// Get the amount and email into an array for easy access
		qrInfo = Arrays.asList(info.split(","));

		// Parse the payment amount into the textview
		String previewString = qrInfo.get(1) + " QrBucks";
		qrEText.setText(previewString);

		// Generate a qr from the intent info
		genQR(info);


//		qrButton.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				genQR();
//			}
//		});
	}

	@Override
	protected void onStart() {
		super.onStart();
		usersCol.document(qrInfo.get(0)).addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
			@Override
			public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
				if (e != null) {
					Toast.makeText(QrActivity.this, "Error while loading!", Toast.LENGTH_SHORT).show();
					Log.d(TAG, e.toString());
					return;
				}
				if (documentSnapshot.exists()) {
					changeCount++;
					if (changeCount > 1){
						Intent transactionProcessed = new Intent();
						setResult(Activity.RESULT_OK, transactionProcessed);
						finish();
					}
				}
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
			Toast.makeText(QrActivity.this,"Please enter some text thanks man appreciate it", Toast.LENGTH_SHORT).show();
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

}

