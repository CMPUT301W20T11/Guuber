package com.example.guuber;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class TransactionFragment extends DialogFragment {
	private EditText amountET;


	public TransactionFragment() {
		// Required empty public constructor
	}

	private TransactionFragment.OnFragmentInteractionListener listener;

	// Implemented within Wallet Activity
	public interface OnFragmentInteractionListener {
		void onOkPressed(Double amount);
		void onCancelPressed();
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		if (context instanceof TransactionFragment.OnFragmentInteractionListener){
			listener = (TransactionFragment.OnFragmentInteractionListener) context;
		} else {
			throw new RuntimeException(context.toString()
					+ " must implement OnFragmentInteractionListener");
		}
	}


	// Used to create a fragment
	public static TransactionFragment newInstance() {
		return new TransactionFragment();
	}

	@NonNull
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		View view = LayoutInflater.from(getActivity()).inflate(R.layout.activity_transaction_fragment, null);

		// Get the view objects
		amountET = view.findViewById(R.id.amount_et);



		AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

		return builder
				.setView(view)
				.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						listener.onCancelPressed();
					}
				})
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						try{
							Double amount = Double.valueOf(amountET.getText().toString());
							listener.onOkPressed(amount);
						}catch (Exception e){
							Toast.makeText(getActivity(), "Invalid Input", Toast.LENGTH_SHORT).show();
						}
					}}).create();
	}

}
