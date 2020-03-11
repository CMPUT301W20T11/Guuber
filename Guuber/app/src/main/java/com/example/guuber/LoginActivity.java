package com.example.guuber;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
/**CITATION: Authenticate Using Google Sign-In on Android,
 * published on  2020-03-09 Standard License, https://firebase.google.com/docs/auth/android/google-signin#next_steps **/
/**CITATION: Integrating Google Sign-In into Your Android App,
 * published on 2020-03-04 Standard License, https://developers.google.com/identity/sign-in/android/sign-in **/



public class LoginActivity extends AppCompatActivity {
	private static final int RC_SIGN_IN = 1;
	private static final String TAG = "LoginActivity";
	private GoogleSignInClient mGoogleSignInClient;

	private FirebaseAuth mAuth;

	@Override
	public void onStart() {
		super.onStart();
		// Check if user is signed in (non-null) and update UI accordingly.
		FirebaseUser currentUser = mAuth.getCurrentUser();
		updateUI(currentUser);
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		SignInButton signInButton = findViewById(R.id.sign_in_button);

		// ...
		// Initialize Firebase Auth
		mAuth = FirebaseAuth.getInstance();

		// Configure sign-in to request the user's ID, email address, and basic
		// profile. ID and basic profile are included in DEFAULT_SIGN_IN.
		GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
				.requestIdToken(getString(R.string.default_web_client_id))
				.requestEmail()
				.build();

		// Build a GoogleSignInClient with the options specified by gso.
		mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

		signInButton.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				if (v.getId() == R.id.sign_in_button) {
					signIn();
					// ...
				}
			}
		});
	}

	private void signIn() {
		Intent signInIntent = mGoogleSignInClient.getSignInIntent();
		startActivityForResult(signInIntent, RC_SIGN_IN);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		// Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
		if (requestCode == RC_SIGN_IN) {
			Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
			try {
				// Google Sign In was successful, authenticate with Firebase
				GoogleSignInAccount account = task.getResult(ApiException.class);
				firebaseAuthWithGoogle(account);
			} catch (ApiException e) {
				// Google Sign In failed, update UI appropriately
				Log.w(TAG, "Google sign in failed", e);
				// ...
			}
		}
	}


//	private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
//		try {
//			GoogleSignInAccount account = completedTask.getResult(ApiException.class);
//
//			// Signed in successfully, show authenticated UI.
//			updateUI(account);
//		} catch (ApiException e) {
//			// The ApiException status code indicates the detailed failure reason.
//			// Please refer to the GoogleSignInStatusCodes class reference for more information.
//			Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
//			updateUI(null);
//		}
//	}

	private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
		Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

		AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
		mAuth.signInWithCredential(credential)
				.addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
					@Override
					public void onComplete(@NonNull Task<AuthResult> task) {
						if (task.isSuccessful()) {
							// Sign in success, update UI with the signed-in user's information
							Log.d(TAG, "signInWithCredential:success");
							FirebaseUser user = mAuth.getCurrentUser();
							updateUI(user);
						} else {
							// If sign in fails, display a message to the user.
							Log.w(TAG, "signInWithCredential:failure", task.getException());
							//Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
							updateUI(null);
						}

						// ...
					}
				});
	}

	private void updateUI(FirebaseUser user) {
		//TODO implement check to see if user id is in database
		Intent newUser = new Intent(LoginActivity.this, NewUserActivity.class);
		startActivity(newUser);
		/**if user is a Riders**/
		Intent homeScreen = new Intent(this, MapsDriverActivity.class);
		startActivity(homeScreen);
		/**else user is a driver **/
	}
}
