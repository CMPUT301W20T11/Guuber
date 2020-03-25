package com.example.guuber;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.guuber.model.User;

/** TripHistoryAdapter class is a custom
 * adapter class that enables us to populate a
 * list view with more flexibility.
 * class TripAdapter handles the User objects
 * in a  list*/


/******
 * currently this class has no data provided to it. adapter is empty
 *****
 */
public class TripHistoryAdapter extends ArrayAdapter<User> {

    private Context context;

    /**provide adapter with data it will be using**/
    public TripHistoryAdapter(@NonNull Context context){
        super(context, 0);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        View listItem = convertView;
        /**if there is no list item, inflate rest of list**/
        if (listItem == null){
            listItem = LayoutInflater.from(context).inflate(R.layout.trip_history_list_layout,parent,false);
        }


        TextView dateTextView = (TextView) listItem.findViewById(R.id.date_text_view);
        dateTextView.setText("Date:" + "example date");

        /**
         * if User is a driver the first text field should display "Rider: ..."
         * if User is a rider the first text field should display "Driver: ..."
         */
        TextView UserLabelTextView = (TextView) listItem.findViewById(R.id.date_text_view);
        UserLabelTextView.setText("Rider: ..." + "example username");

        TextView pickupLocationTextView = (TextView) listItem.findViewById(R.id.pickup_location_text_view);
        pickupLocationTextView.setText("Pick up Location:" + "example pickup");

        TextView dropoffLocationTextView = (TextView) listItem.findViewById(R.id.dropoff_location_text_view);
        dropoffLocationTextView.setText("Drop off Location:" + "example dropoff");

        /**
         * if User is Driver display "amount received"
         * if User is Rider display "amount paid"
         */
        TextView paymentTextView = (TextView) listItem.findViewById(R.id.payment_text_view);
        paymentTextView.setText("Amount... :" + "example amount");


        return listItem;

    }

}
