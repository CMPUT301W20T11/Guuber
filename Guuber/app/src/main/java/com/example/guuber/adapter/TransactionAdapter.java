package com.example.guuber.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.guuber.R;
import com.example.guuber.model.Transaction;

import java.util.ArrayList;

public class TransactionAdapter extends ArrayAdapter<Transaction> {

	private ArrayList<Transaction> transactions;
	private Context context;

	public TransactionAdapter(Context context, ArrayList<Transaction> transactions){
		super(context, 0 , transactions);
		this.transactions = transactions;
		this.context = context;
	}

	public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
		Transaction transaction = transactions.get(position);
		View view = convertView;

		// Use custom list view content xml to display transactions
		if (view == null){
			view = LayoutInflater.from(context).inflate(R.layout.content_trans, parent, false);
		}

		// Get the textView objects of the content xml
		TextView tDate = view.findViewById(R.id.date_tv);
		TextView tMessage = view.findViewById(R.id.message_tv);
		TextView tAmount = view.findViewById(R.id.amount_tv);

		// Set the values
		tDate.setText(transaction.getDate());
		tMessage.setText(transaction.getMessage());
		tAmount.setText(String.valueOf(transaction.getAmount()));

		return view;
	}
}
