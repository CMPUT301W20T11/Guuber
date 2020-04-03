package com.example.guuber;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.guuber.adapter.TransactionAdapter;
import com.example.guuber.controller.TransactionController;
import com.example.guuber.model.User;
import com.example.guuber.model.Wallet;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;


/**
 * Wallet dashboard that displays user transactions and balance
 * External withdrawals and deposits enabled through the menu
 */
public class WalletActivity extends AppCompatActivity implements TransactionFragment.OnFragmentInteractionListener {
    // Get the user email from the user singleton, used as db key
    private String uEmail;

    // Activity objects
    private Wallet wallet;
    private ListView transactionListView;
    private TextView balanceTV;

    // Db instance of user
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference uRef = db.collection("Users");

    // Menu item selection
    int id;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);

        uEmail = ((UserData) (getApplicationContext())).getUser().getEmail();
        transactionListView = findViewById(R.id.trans_lv);
        balanceTV = findViewById(R.id.balance_tv);

        // Display the back button if action bar is enabled
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        uRef.document(uEmail).addSnapshotListener(this, (documentSnapshot, e) -> {
            wallet = documentSnapshot.toObject(User.class).getWallet();
            TransactionAdapter transactionAdapter = new TransactionAdapter(WalletActivity.this, wallet.getTransactions());
            transactionListView.setAdapter(transactionAdapter);

            String balance = "Balance: " + wallet.getBalance() + " QrB";
            balanceTV.setText(balance);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.walletmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        id = item.getItemId();

        switch (id){
            case R.id.withdraw:
                Toast.makeText(this, "withdraw clicked", Toast.LENGTH_SHORT).show();
                TransactionFragment transactionFragment1 = TransactionFragment.newInstance();
                transactionFragment1.show(getSupportFragmentManager(), "Withdraw");
                break;
            case R.id.deposit:
                Toast.makeText(this, "deposit clicked", Toast.LENGTH_SHORT).show();
                TransactionFragment transactionFragment2 = TransactionFragment.newInstance();
                transactionFragment2.show(getSupportFragmentManager(), "Deposit");
                break;
            case android.R.id.home:
                this.finish();
                break;
        }
        return true;
    }


    /**
     * Transaction fragment cancel button onClick listener implementation
     */
    @Override
    public void onCancelPressed(){
        Toast.makeText(this, "Transaction cancelled", Toast.LENGTH_SHORT).show();
    }

    /**
     * Transaction fragment ok button onClick listener implementation
     * @param amount - Amount to withdraw
     */
    @Override
    public void onOkPressed(Double amount) {
        switch (id) {
            case R.id.withdraw:
                uRef.document(uEmail).get().addOnSuccessListener(documentSnapshot -> {
                    User user = documentSnapshot.toObject(User.class);
                    if(TransactionController.processWithdrawal(user, amount)) {
                        uRef.document(uEmail).set(user);
                    }else{
                        Toast.makeText(WalletActivity.this, "Insufficient funds", Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            case R.id.deposit:
                uRef.document(uEmail).get().addOnSuccessListener(documentSnapshot -> {
                    User user = documentSnapshot.toObject(User.class);
                    if(TransactionController.processDeposit(user, amount)) {
                        uRef.document(uEmail).set(user);
                    }else{
                        Toast.makeText(WalletActivity.this, "Deposit failed", Toast.LENGTH_SHORT).show();
                    }
                });
                break;
        }
    }
}
