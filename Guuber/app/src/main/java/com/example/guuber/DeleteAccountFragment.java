package com.example.guuber;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

/** requests for permission to delete the user's account
 * returns to profile page if permission denied
 * deletes the user's account from database and application records and
 * returns user to the sign in activity
*/
public class DeleteAccountFragment extends DialogFragment {
    private TextView dialogueMessage;
    private TextView dialogueTitle;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.delete_account_ragment, null);
        String activit = getArguments().getString("callingActivity");
        dialogueMessage = view.findViewById(R.id.deleteAccTextView);
        dialogueTitle = view.findViewById(R.id.deleteAccTitle);
        dialogueMessage.setText("WARNING: Continuing will permanently delete your account and all transaction history" +
                "\nPlease note any positive balance left in the wallet upon deleting account will be forfeited to us");
        dialogueTitle.setText("WARNING");

         AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
         return builder

         .setView(view)
         .setPositiveButton("CANCEL", null)//add click listener that closes the dialogue
         .setNegativeButton("DELETE ACCOUNT", new DialogInterface.OnClickListener() {
        @Override public void onClick(DialogInterface dialog, int which) {
        if ("rider".equals(activit)){
        ((RiderProfileActivity) getActivity()).deleteSelf();}
        else if ("driver".equals(activit)){
        ((DriverProfilActivity) getActivity()).deleteSelf();}
        }
        })//add click listener that calls Main activity method
         .create();
    }
}
