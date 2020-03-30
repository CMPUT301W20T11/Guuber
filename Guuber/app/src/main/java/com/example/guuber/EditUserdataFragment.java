package com.example.guuber;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class EditUserdataFragment extends DialogFragment {
    private EditText oldText;
    private EditText confirmText;
    private EditText newText;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState){
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.change_user_data_fragment, null);
        String activit = getArguments().getString("activity");
        String fields = getArguments().getString("field");
        String value = getArguments().getString("old");
        oldText = view.findViewById(R.id.oldText);
        newText = view.findViewById(R.id.newText);
        confirmText = view.findViewById(R.id.confirmText);

        confirmText.setHint("Confirm new " + fields);
        oldText.setHint("Enter old " +fields +" here");
        newText.setHint("Enter new  " +fields +"  here");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        return builder
                .setView(view)
                .setTitle("Edit")
                .setPositiveButton("CANCEL", null)//add click listener that closes the dialogue
                .setNegativeButton("SAVE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Boolean boolOld = value.equals(oldText.getText().toString());
                        Boolean boolUnmatch = newText.getText().toString().equals(confirmText.getText().toString());
                        if (boolOld == false){//oldText.setTextColor(Color.RED);
                            Toast.makeText(getActivity(), "Not changed!!!", Toast.LENGTH_SHORT).show();
                        }
                        else if (boolUnmatch = false){
                            //newText.setTextColor(Color.RED);
                            //confirmText.setTextColor(Color.RED);
                            Toast.makeText(getActivity(), "Not changed!!!", Toast.LENGTH_SHORT).show();
                            }
                        else{
                            Toast.makeText(getActivity(), "Changes saved!", Toast.LENGTH_LONG).show();
                            if (activit.equals("RiderProfileActivity")){
                            ((RiderProfileActivity) getActivity()).updateData(fields, confirmText.getText().toString());}
                            else{
                                ((DriverProfilActivity) getActivity()).updateData(fields, confirmText.getText().toString());
                            }


                        }
                    }
                })
                .create();
    }

}
