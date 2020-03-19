package com.example.guuber;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;


/**
 * View measurement class creates a dialog fragment in main activity. upon viewing
 * their entry, user may choose to delete, edit, or be done viewing
 *currently this dialog is not being used
 * **/
public class EnableLocationServices extends DialogFragment {

    private final String LOCATION_NOT_ENABLED_ERROR = "ENABLE LOCATION SERVICES";

    private OnFragmentInteractionListener listener;

    /**providing View measurement constructor with object, and position if user wants to edit**/
    public EnableLocationServices(){
        //empty constructor
    }

    /**methods implemented  in main activity**/
    public interface OnFragmentInteractionListener {
    }

    /**
     * throw runtime exception if fragment
     * interaction listener hasn't been implemented
     * in Maps Activity
     **/
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

    /**
     * populate the fragment text field
     * with warning message
     **/
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.location_services_fragment, null);

        /**dialog builder + instructions upon button clicks**/
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        return builder
                .setView(view)
                .setTitle(LOCATION_NOT_ENABLED_ERROR)
                .create();
    }
}


