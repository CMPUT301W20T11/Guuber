package com.example.guuber;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.example.guuber.model.User;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;


public class RegisterFragment extends DialogFragment {
	private FirebaseFirestore db = FirebaseFirestore.getInstance();
	private CollectionReference uRef = db.collection("Users");


	// Keys for the fragment inputs
	private static final String ARG_UID = "uid";
	private static final String ARG_EMAIL = "email";

	// Fragment input parameters
	private String uid;
	private String emailS;

	// View items
	private EditText userName;
	private EditText email;
	private EditText fname;
	private EditText lname;
	private EditText phoneN;
	private Integer isRider = 1;


	public RegisterFragment() {
		// Required empty public constructor
	}

	private OnFragmentInteractionListener listener;

	// Implemented within Login
	public interface OnFragmentInteractionListener {
		void onOkPressed();
		void onCancelPressed();
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		if (context instanceof OnFragmentInteractionListener){
			listener = (OnFragmentInteractionListener) context;
		} else {
			throw new RuntimeException(context.toString()
					+ " must implement OnFragmentInteractionListener");
		}
	}


	// Used to create a fragment
	public static RegisterFragment newInstance(String uid, String email) {
		RegisterFragment fragment = new RegisterFragment();
		Bundle args = new Bundle();
		args.putString(ARG_UID, uid);
		args.putString(ARG_EMAIL, email);
		fragment.setArguments(args);
		return fragment;
	}

	@NonNull
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_register, null);

		// Get the view objects
		userName = view.findViewById(R.id.username_tv);
		email = view.findViewById(R.id.email_et);
		fname = view.findViewById(R.id.fname_et);
		lname = view.findViewById(R.id.lname_et);
		phoneN = view.findViewById(R.id.phone_et);
		Switch isRiderSwitch = view.findViewById(R.id.driver_switch);
		isRiderSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					// The toggle is enabled
					isRider = 0;
				} else {
					// The toggle is disabled
					isRider = 1;
				}
			}
		});




		AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

		if (getArguments() != null) {
			uid = getArguments().getString(ARG_UID);
			emailS = getArguments().getString(ARG_EMAIL);
			email.setText(emailS);
		}

		return builder
				.setView(view)
				.setTitle("Register")
				.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						listener.onCancelPressed();
					}
				})
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						// Try to parse user input, send toast if failed
						try {
							emailS = email.getText().toString();
							String fnameS = fname.getText().toString();
							String lnameS = lname.getText().toString();
							String phoneNS = phoneN.getText().toString();
							String userNameS = userName.getText().toString();
							User user = new User(phoneNS, emailS,fnameS,lnameS,uid, userNameS, 0, 0);
							// Check if user is rider or driver
							if (isRider == 0) {
								user.setRider(0);
							}
							// Query the username to ensure its unique before adding to database TODO

							uRef.document(user.getEmail()).set(user);
							listener.onOkPressed();


						}catch (Exception e){
							Log.d("Database", "Failed to register user", e);
							Toast toast = Toast.makeText(getActivity(), "Registration failed", Toast.LENGTH_SHORT);
							toast.show();
						}
					}}).create();
	}


}

