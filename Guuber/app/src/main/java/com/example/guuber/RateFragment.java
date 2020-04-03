package com.example.guuber;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

public class RateFragment extends DialogFragment {
	private String email;

	// Instantiate the database
	private FirebaseFirestore db = FirebaseFirestore.getInstance();
	private CollectionReference uRef = db.collection("Users");


	public static RateFragment newInstance(String email) {
		// Pass the email of user to be rated to the fragment
		Bundle args = new Bundle();
		args.putString("EMAIL", email);

		// Generate instance of the fragment with args and return
		RateFragment fragment = new RateFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// Set title for this dialog
		getDialog().setTitle("Rate your experience");

		View view = inflater.inflate(R.layout.fragment_rate, container, false);

		// Get passed arg
		if (getArguments() != null) {
			email = getArguments().getString("EMAIL");
		}

		// Get thumb images
		ImageView posThumb = view.findViewById(R.id.pos_thumb);
		ImageView negThumb = view.findViewById(R.id.neg_thumb);

		// On thumbs up
		posThumb.setOnClickListener(v -> {
			rateUser(true);
			Toast.makeText(getActivity(), "Positive experience!", Toast.LENGTH_SHORT).show();
			dismiss();
		});

		// On thumbs down
		negThumb.setOnClickListener(v -> {
			rateUser(false);
			Toast.makeText(getActivity(), "Negative experience!", Toast.LENGTH_SHORT).show();
			dismiss();
		});

		return view;
	}

	// Rate the user
	private void rateUser(Boolean rating){
		// Update the db object's rating
		if(!rating){
			uRef.document(email).update("negRating", FieldValue.increment(1));
		}else{
			uRef.document(email).update("posRating", FieldValue.increment(1));
		}
	}
}
