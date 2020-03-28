package com.example.guuber;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.guuber.adapter.TransactionAdapter;
import com.example.guuber.model.Transaction;
import com.example.guuber.model.Wallet;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;

/**
 * This class contains the activity the rider and/or driver
 * will be directed to upon selecting "Wallet"
 * in their menu
 * it enables their ability to view their wallet and balance details
 */
public class WalletActivity extends AppCompatActivity {
    private static final String TAG = "Query error";

    // Get the user email from the user singleton, used as db key
    private String uEmail;

    // Activity objects
    Wallet wallet;
    ListView transactionListView;
    ArrayList<Transaction> transactionArrayList;
    ArrayAdapter<Transaction> transactionArrayAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);

        uEmail = ((UserData) (getApplicationContext())).getUser().getEmail();

        transactionListView = findViewById(R.id.trans_lv);

        // TODO: This is just a test
        Wallet tWallet = new Wallet();
        Transaction tTransaction = new Transaction(20, "1", "Message");
        ArrayList<Transaction> aLT = new ArrayList<Transaction>();
        aLT.add(tTransaction);
        tWallet.setTransactions(aLT);
        wallet = tWallet;

        TransactionAdapter transactionAdapter = new TransactionAdapter(this, wallet.getTransactions());
        transactionListView.setAdapter(transactionAdapter);

        // Display the back button if action bar is enabled
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.walletmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.withdraw:
                Toast.makeText(this, "withdraw clicked", Toast.LENGTH_LONG).show();
                break;
            case R.id.deposit:
                Toast.makeText(this, "deposit clicked", Toast.LENGTH_LONG).show();
                break;
        }
        return true;
    }
}
