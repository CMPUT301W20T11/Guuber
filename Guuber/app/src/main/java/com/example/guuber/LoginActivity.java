package com.example.guuber;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.guuber.model.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


//Citation: Authenticate Using Google Sign-in On Android
//https://firebase.google.com/docs/auth/android/google-signin

/**
 * Class to implement the Login Activity
 * includes google sign in and ability
 * to log in as rider or driver
 * Source: https://github.com/firebase/quickstart-android
 */
public class LoginActivity extends AppCompatActivity implements RegisterFragment.OnFragmentInteractionListener {
	private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 10;
	public static final int PERMISSIONS_REQUEST_ENABLE_GPS = 12;
	private boolean isLocationPermissionGranted = false;

	private static final int RC_SIGN_IN = 9001;

	private FirebaseAuth mAuth;
	private static final String TAG = "LoginActivity";
	private GoogleSignInClient mGoogleSignInClient;

	private FirebaseFirestore db = FirebaseFirestore.getInstance();
	private DocumentReference uRef;

	private static final int RC_SIGN_OUT = 1000;

	// location client
	private FusedLocationProviderClient fusedLocationClient;
	Location currLocation;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		SignInButton signInButton = findViewById(R.id.sign_in_button);

		// Configure sign-in to request the user's ID, email address, and basic
		// profile. ID and basic profile are included in DEFAULT_SIGN_IN.
		GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
				.requestIdToken(getString(R.string.default_web_client_id))
				.requestEmail()
				.build();

		// Build a GoogleSignInClient with the options specified by gso.
		mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

		mAuth = FirebaseAuth.getInstance();

		signInButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (v.getId() == R.id.sign_in_button) {
					signIn();
				}
			}
		});


		// INITIALIZING LOCATION CLIENT
		fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
		fusedLocationClient.getLastLocation()
				.addOnSuccessListener(this, new OnSuccessListener<Location>() {
					@Override
					public void onSuccess(Location location) {
						android.util.Log.i("Bad", "location is null");
						if (location != null) {
							currLocation = location;
						}
					}
				});
	}

	@Override
	protected void onStart() {
		super.onStart();
		updateUI(null);

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch(requestCode){// Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
			case RC_SIGN_IN: {
				Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
				try {
					// Google Sign In was successful, authenticate with Firebase
					GoogleSignInAccount account = task.getResult(ApiException.class);
					firebaseAuthWithGoogle(account);
				} catch (ApiException e) {
					// Google Sign In failed, update UI appropriately
					Log.w(TAG, "Google sign in failed", e);
					// [START_EXCLUDE]
					updateUI(null);
					// [END_EXCLUDE]
				}
			}
			// This is to handle sign out from the map activities
			case RC_SIGN_OUT: {
				if (resultCode == RC_SIGN_OUT) {
					String confirmSignOut = data.getStringExtra("SignOut");
					if (confirmSignOut.equals("TRUE")){
						//mGoogleSignInClient.signOut();
						signOut();
					}


				}
			}
		}

	}

	// [START auth_with_google]

	/**
	 * Perform the firebase authentication with google sign in
	 * @param acct the google account
	 */
	private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
		Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

		AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
		mAuth.signInWithCredential(credential)
				.addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
					@Override
					public void onComplete(@NonNull Task<AuthResult> task) {
						if (task.isSuccessful()) {
							// Google sign in success
							Log.d(TAG, "signInWithCredential:success");
							final FirebaseUser user = mAuth.getCurrentUser();

							// Check if user has registered
							uRef = db.collection("Users").document(user.getEmail());
							uRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
								@Override
								public void onSuccess(DocumentSnapshot documentSnapshot) {
									if(!documentSnapshot.exists()){
										Log.d(TAG, "User not found in DB: " + documentSnapshot.getData());
										RegisterFragment registerFragment = RegisterFragment.newInstance(user.getUid(), user.getEmail());
										registerFragment.show(getSupportFragmentManager(), "Register");
									}else{
										Log.d(TAG, "User info pulled from DB " + documentSnapshot.getData());
										updateUI(user);
									}
								}
							});
						} else {
							// If sign in fails, display a message to the user.
							Context context = getApplicationContext();
							CharSequence text = "Google sign in failed";
							int duration = Toast.LENGTH_SHORT;

							Toast toast = Toast.makeText(context, text, duration);
							toast.show();

							updateUI(null);
						}
					}
				});
	}
	// [END auth_with_google]

	/**
	 * Sign in to the google sign in client
	 */
	private void signIn() {
		Intent signInIntent = mGoogleSignInClient.getSignInIntent();
		startActivityForResult(signInIntent, RC_SIGN_IN);
	}
	// [END signin]

	/**
	 * Initiate the sign out from both firebase and google sign out
	 */
	private void signOut() {
		// Firebase sign out
		mAuth.signOut();

		// Google sign out
		mGoogleSignInClient.signOut().addOnCompleteListener(this,
				new OnCompleteListener<Void>() {
					@Override
					public void onComplete(@NonNull Task<Void> task) {
						updateUI(null);
					}
				});
	}

	private void revokeAccess() {
		// Firebase sign out
		mAuth.signOut();

		// Google revoke access
		mGoogleSignInClient.revokeAccess().addOnCompleteListener(this,
				new OnCompleteListener<Void>() {
					@Override
					public void onComplete(@NonNull Task<Void> task) {
						updateUI(null);
					}
				});
	}

	/**
	 * Update the interface for the user, if successful opens the proper map activity depending on driver or rider account
	 * @param user The Firebase user
	 */
	private void updateUI(FirebaseUser user) {
		Context context = LoginActivity.this;
		if (user != null) {
			// Populate the singleton
			uRef = db.collection("Users").document(user.getEmail());
			uRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
				@Override
				public void onSuccess(DocumentSnapshot documentSnapshot) {
					User loggedUser = documentSnapshot.toObject(User.class);
					((UserData) (getApplicationContext())).setUser(loggedUser);
					Intent homeScreen;
					int signInType = 0;
					if (loggedUser.getRider() == 1) {
						signInType = 1;
					}

					// Go to home screen depending on user type
					if (signInType == 1) {
						//if user is a Rider
						homeScreen = new Intent(context, MapsRiderActivity.class);
					} else {
						//else user is a driver
						homeScreen = new Intent(context, MapsDriverActivity.class);
					}
					startActivityForResult(homeScreen, RC_SIGN_OUT);
					//startActivity(homeScreen);
				}
			});
		} else {
			// if the user has never registered before we have to get permissions
			checkUserPermissions();
		}

	}

	/**
	 * Register fragment cancel button onClick listener implementation
	 */
	@Override
	public void onCancelPressed(String message){
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
		signOut();
	}

	/**
	 * Register fragment ok button onClick listener implementation
 	 */
	@Override
	public void onOkPressed(){
		final FirebaseUser user = mAuth.getCurrentUser();
		updateUI(user);
	}

	/**
	 * Checks User permissions for location access
	 * @return Boolean of if there location is on or off
	 */
	public boolean checkUserPermissions(){
		if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
				android.Manifest.permission.ACCESS_FINE_LOCATION)
				== PackageManager.PERMISSION_GRANTED) {
			isLocationPermissionGranted = true;
			return true;
		} else {
			ActivityCompat.requestPermissions(this,
					new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
					PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
			return false;
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode,
										   String[] permissions, int[] grantResults) {
		switch (requestCode) {
			case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
				// If request is cancelled, the result arrays are empty.
				if (grantResults.length > 0
						&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					isLocationPermissionGranted = true;
				} else {
					userPermissionsRationale();
				}
				return;
			}

		}
	}

	/**
	 * The permission dialog popup
	 */
	public  void userPermissionsRationale(){
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Hey there Friend. To have the best experience with this application, " +
				"we ask you provide us your location. Don't worry. We are just going to sell your data and " +
				"exploit information that makes you vulnerable. If you have chosen to not be asked again, " +
				"please visit your app setting and grant us your location permissions")
				.setCancelable(false)
				.setPositiveButton("Got It!", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						checkUserPermissions();
						dialog.dismiss();
					}
				});
		final AlertDialog alert = builder.create();
		alert.show();
	}

}






