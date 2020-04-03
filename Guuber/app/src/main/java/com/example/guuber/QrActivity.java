package com.example.guuber;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.Arrays;
import java.util.List;


/**
 * Generate and display a QR image used for transactions
 * Citation: https://medium.com/@aanandshekharroy/generate-barcode-in-android-app-using-zxing-64c076a5d83a
 */
public class QrActivity extends AppCompatActivity {
	private static final String TAG = "QrActivity";

	private ImageView qrImage;
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
	}

	@Override
	protected void onStart() {
		super.onStart();
		usersCol.document(qrInfo.get(0)).addSnapshotListener(this, (documentSnapshot, e) -> {
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
		});
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

