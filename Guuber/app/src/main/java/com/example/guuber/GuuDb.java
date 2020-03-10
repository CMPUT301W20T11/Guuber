package com.example.guuber;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static androidx.constraintlayout.widget.Constraints.TAG;


///TODO:
//    -creating a collection of request history and being adding to it
//    -other update methods most likely
//    -creating a collection of ratings and able to add to that (maybe)
//    -an average rating of the user (maybe)
//          - will add once ratings has been established in user?



public class GuuDb {
    private FirebaseFirestore db;

    //Root is the Users collection
    private CollectionReference root;
    // doc to be used to get user information
    private DocumentReference doc;

    public GuuDb(){
        db = FirebaseFirestore.getInstance();
        root = db.collection("Users");

    }

    /**
     * find the doc of user and uses it as a reference
     * @param username - the username to find in the database
     */

    public void findUser(final String username){
        doc = root.document(username);
        doc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    Log.d(username,"Document exist");
                }
                else{
                    Log.d(username, "Document does not exist");
                }
            }
        });
    }
    /**
     * gets user's information
    * --------NOTE: That the user/Rider/Driver class constructor must have no aruguments---------------------
    *-------- Nebye can you try and see if there is another way to do this?-------------------------------
     */
    public User getUserInfo(){
        final User[] user = new User[1];
        doc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    user[0] = documentSnapshot.toObject(User.class);
                }
                else{

                }
            }
        });
        return user[0];
    }
    /**
     * Creates and add the minimal info of the user into the database
     * @param username - the document id, as it has to be unique like a username
     * @param first - first name of the user
     * @param last - Last name of the user
     * @param email - the user's email
     * */
    public void setUpUser(String username, String first,String last,String email,String phone){
        Map<String,Object> user = new HashMap<>();
        user.put("first",first);
        user.put("last",last);
        user.put("email",email);
        user.put("phone",phone);

        root.document(username).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("Document", "DocumentSnapshot successfully written");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("Document","Error writing document");
            }
        });
    }
    /**
     * Adds the ratings field if it doesn't exist or updates it in the database
     * The newest rating received by the user (newRating) should be used to update the AggRating which will then update the ratings counter (RatingCounter) and the new average rating (AvgRating)
     * @param NewRating - the new rating for the user; amount to add to AggRating
     * @param AggRating - Aggregation of all the ratings of the user
     * @param RatingCounter - The amount of ratings for each user
     * @param AvgRating - The average rating for each user; AggRating/RatingCounter
     * */
    public void Rating(int NewRating, int AggRating, final int RatingCounter, final double AvgRating)
    {
        doc.update("AggRating", AggRating).addOnSuccessListener(new OnSuccessListener<Void>()
        {
            @Override
            public void onSuccess(Void aVoid)
            {
                Log.d("AggRating","DocumentSnapshot successfully updated");


                // depends on if we want to handle the ratingCounter/AvgRating updating in the database
                // probably preffered since the former is a varaible just used here and for the avgRating system
                // then the user class can pull the avgrating from the database if the user wants to view their current avg
                
                //user RatingCounter++ update 

                //update AvgRating 
                //doc.update("AvgRating", AvgRating).addOnSuccessListener(new OnSuccessListener<Void>() {
                //    @Override
                //    public void onSuccess(Void aVoid) {
                //        Log.d("AvgRating","Documentsnapshot updated");
                //    }
                //});

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("AggRating","DocumentSnapshot successfully updated",e);
            }
        });
    }








    /**
     * Adds the balance field if it doesn't exist or updates it in the database
     * @param balance - amount to put in Database
     * */
    public void addWallet(double balance){
        doc.update("balance", balance).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("Balance","Documentsnapshot updated");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("Balance"," Error updating document",e);
            }
        });
    }

    /**
     * Adds the registered car into the database
     * @param make - the maker of the car
     * @param model - the car model
     * @param colour - the colour of the car
     * */
    public void regCar(String make,String model, String colour){
        Map<String,Object> car = new HashMap<>();
        car.put("make",make);
        car.put("model", model);
        car.put("colour", colour);
        doc.collection("car").document("userCar").set(car).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("carDoc","successfully created");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("carDoc","Error writing document");
            }
        });
    }
    /**
     * updates the registered car into the database
     * @param make - the maker of the car
     * @param model - the car model
     * @param colour - the colour of the car
     * */
    public void updateCar(String make,String model, String colour){
        Map<String,Object> car = new HashMap<>();
        car.put("make",make);
        car.put("model", model);
        car.put("colour", colour);
        doc.collection("car").document("userCar").update(car).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("updateCar","Car updated");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("updateCar","Update failed");
            }
        });
    }
    /**
     * Get the details of the vehicle the user has
     * @return
     *      DocumentSnapshot of the vehicle which contains info about it
     * */
    public DocumentSnapshot getVehicle(){
        return doc.collection("car").document("usercar").get().getResult();
    }

    /**
     * Put the completed request in the user history
     * if the collection of request history does not exist it will create it
     * @param cost - the cost of the request
     * @param driver - There driver that completed their request
     * */
    public void addToRequestHist(int cost, String driver){
        Calendar date = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = dateFormat.format(date.getTime());

        Map<String,Object> requestDetail = new HashMap<>();
        requestDetail.put("cost",cost);
        requestDetail.put("driver",driver);
        requestDetail.put("date",time);
        doc.collection("reqHistory").add(requestDetail);
    }
    /**
     * Gets the request history of the user
     * @return
     *      Returns a list of documentssnapshots
     * */
    public List<DocumentSnapshot> getRequestHist(){
        return doc.collection("reqHistory").get().getResult().getDocuments();
    }


}

