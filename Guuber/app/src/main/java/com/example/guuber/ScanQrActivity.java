package com.example.guuber;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.guuber.controller.TransactionController;
import com.example.guuber.model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.Arrays;
import java.util.List;

// Citation: QR Code Scanner - Android Application using ZXing Library
// https://www.youtube.com/watch?v=Fe7F4Jx7rwo

/**
 * This class contains the activity the driver
 * will be directed to upon selecting "scan QR"
 * in their menu, allows for QR scanning and processes
 * transaction encoded in the QR
 */
public class ScanQrActivity extends AppCompatActivity {
	private List<String> qrInfo;
	private FirebaseFirestore db = FirebaseFirestore.getInstance();
	private CollectionReference usersCol = db.collection("Users");
	private User driver;
	private User rider;
	private String dEmail;
	private String rEmail;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qr);
		Button scan_button = findViewById(R.id.scan_button);
        final Activity activity = this;

        // Result encodes the transaction status, failed by default
		setResult(RESULT_CANCELED);

		driver = ((UserData)(getApplicationContext())).getUser();
		dEmail = driver.getEmail();

		// Display the back button
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
				qrInfo = Arrays.asList(result.getContents().split(","));

				usersCol.document(qrInfo.get(0)).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
					@Override
					public void onSuccess(DocumentSnapshot documentSnapshot) {
						if(documentSnapshot.exists()) {
							rider = documentSnapshot.toObject(User.class);
							rEmail = rider.getEmail();
							if(TransactionController.processTrans(driver, rider, Double.parseDouble(qrInfo.get(1)))){
								usersCol.document(rEmail).set(rider);
								usersCol.document(dEmail).set(driver);
								setResult(RESULT_OK);
								finish();
							}
						}
					}
				});
			}
		}else{
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
