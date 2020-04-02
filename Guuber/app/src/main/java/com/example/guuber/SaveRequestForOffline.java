package com.example.guuber;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * This class saves and loads data for a request and its status
 * if you are offline
 */
public class SaveRequestForOffline {

    private static String PREFERENCES = "REQUEST_LIST";
    private SharedPreferences sharedPreferences;
    private ArrayList requestsList;


    //CITATION: StackOverflow post by Scott https://stackoverflow.com/users/3576831/scott, Answer,  https://stackoverflow.com/questions/23351904/getting-cannot-resolve-method-error-when-trying-to-implement-getsharedpreferen**/
    //CITATION: Youtube, Coding in Flow, How to Save an ArrayList of Custom Objects to SharedPreferences with Gson - Android Studio Tutorial, published on November 6,2017, Standard License, https://www.youtube.com/watch?v=jcliHGR3CHo **/

    public SaveRequestForOffline(){
        //empty constructor method. Here's a comment to keep it company
    }

    /**
     * save array list containing the request object
     **/
    public void saveData(ArrayList requestsList, Context context){
            sharedPreferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit() ;
            Gson gson = new Gson();
            String json = gson.toJson(requestsList);
            editor.putString(PREFERENCES,json);
            editor.apply();
    }

    /**load arrayList from shared
     * preferences**/
    public ArrayList loadData(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        Gson gson = new Gson();

        //default value is null, so if we didn't save an array list yet we wont get anything
        // back, and we will instantiate a new one
        String json = sharedPreferences.getString(PREFERENCES, null);
        Type type = new TypeToken<ArrayList<Requests>>() {}.getType();
        requestsList = gson.fromJson(json, type);

        if (requestsList == null) {
            requestsList = new ArrayList<>();
        }
            return requestsList;
    }
}


