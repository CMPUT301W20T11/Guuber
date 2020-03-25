package com.example.guuber.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;
import com.google.gson.internal.$Gson$Preconditions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GuuDbHelper {
    private static FirebaseFirestore db;
    private static CollectionReference requests;
    private static CollectionReference users;
    private static DocumentReference profile;
    public static User user;
    public static Map<String,Object> Request = new HashMap<>();
    public static Vehicle car;
    public static ArrayList<Map<String,Object>> reqList = new ArrayList<Map<String,Object>>();

    private static CollectionReference rating;
    public static Map<String,Object> Rating = new HashMap<>();
    public static ArrayList<Map<String, String>> ratingList = new ArrayList<Map<String, String>>();


    private static CollectionReference wallet;
    public static Map<String,Object> Wallet = new HashMap<>();
    public static ArrayList<Map<String, String>> walletList = new ArrayList<Map<String, String>>();


    public GuuDbHelper(FirebaseFirestore db){
        this.db = db.getInstance();
        this.users = this.db.collection("Users");
        this.requests = this.db.collection("requests");
        this.user = new User();
    }

    //helper function
    public void findUser(String email){
        users.document(email).get(Source.SERVER).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    Log.d("user","found user");
                    setUser(documentSnapshot.get("phoneNumber").toString(),documentSnapshot.get("email").toString(),
                            documentSnapshot.get("first").toString(),documentSnapshot.get("last").toString(),
                            documentSnapshot.get("uid").toString(),documentSnapshot.get("username").toString());
                }
                else{
                    Log.d("user","user does not exist");
                }
            }
        });
    }

    //helper function
    public void setUser(String phone,String email,String first,String last,String uid,String uname){
        this.user.setEmail(email);
        this.user.setPhoneNumber(phone);
        this.user.setFirstName(first);
        this.user.setLastName(last);
        this.user.setUid(uid);
        this.user.setUsername(uname);

    }
    //get user information
    public User getUser(String email ){
        findUser(email);
        setProfile(email);
        return user;
    }

    //NOTE: USED TO CREATE USERS
    public void checkEmail(User newUser){
        Map<String,Object> user = new HashMap<>();
        user.put("first",newUser.getFirstName());
        user.put("last",newUser.getLastName());
        user.put("email",newUser.getEmail());
        user.put("username",newUser.getUsername());
        user.put("phoneNumber",newUser.getPhoneNumber());
        user.put("uid",newUser.getUid());
        users.document(newUser.getEmail()).get(Source.SERVER).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if( !documentSnapshot.exists()){
                    Log.d("checking email in db","Email does not exist");
                    createUser(user, newUser);
                }
                else{
                    Log.d("checking email in db","Email exist");
                }
            }
        });
    }

    public void createUser(Map<String,Object> info,User newUser){
        users.document(newUser.getEmail()).set(info);
    }
    public void setProfile(String email){
        this.profile = users.document(email);
    }

    public void deleteUser(String email){
        setProfile(email);
        profile.delete();
    }
    public void updateUsername(String email,String name){
        users.document(email).update("username",name);
    }
    public void updatePhoneNumber(String email,String number){
        users.document(email).update("phoneNumber",number);
    }


    public void makeReq(User rider,int tip, String location,String oriLat,String oriLng,String desLat,String desLng){
        setProfile(rider.getEmail());
        Map<String,Object> details = new HashMap<>();
        details.put("reqTip",tip);
        details.put("reqLocation",location);
        details.put("oriLat",oriLat);
        details.put("oriLng",oriLng);
        details.put("desLat",desLat);
        details.put("desLng",desLng);
        this.profile.update(details);

        this.requests.document(rider.getEmail()).set(details);


    }
    public void cancelRequest(User rider) {
        setProfile(rider.getEmail());
        profile.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()) {
                        Map<String, Object> delete = new HashMap<>();
                        delete.put("reqTip", FieldValue.delete());
                        delete.put("reqLocation", FieldValue.delete());
                        delete.put("oriLat", FieldValue.delete());
                        delete.put("oriLng", FieldValue.delete());
                        delete.put("desLat", FieldValue.delete());
                        delete.put("desLng", FieldValue.delete());
                        if (doc.get("reqDriver") != null) {
                            delete.put("reqDriver", FieldValue.delete());
                            setProfile(doc.get("reqDriver").toString());
                            profile.collection("driveRequest").document(rider.getEmail()).delete();
                            setProfile(rider.getEmail());
                            profile.update(delete);
                        } else {

                            setProfile(rider.getEmail());
                            profile.update(delete);
                            requests.document(rider.getEmail()).delete();
                        }
                    }
                }
            }
        });

    }
    public void setRequest(String email, Object tip ,String location, String oriLat,String oriLng,String desLat,String desLng ){
        this.Request.put("reqTip", tip);
        this.Request.put("reqLocation",location);
        this.Request.put("oriLat",oriLat);
        this.Request.put("oriLng",oriLng);
        this.Request.put("desLat",desLat);
        this.Request.put("desLng",desLng);
        this.Request.put("email",email);


    }
    public Map<String,Object> getRiderRequest(User rider){
        setProfile(rider.getEmail());
        profile.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
               setRequest(documentSnapshot.get("email").toString(), documentSnapshot.get("reqTip"),
                       documentSnapshot.get("reqLocation").toString(),documentSnapshot.get("oriLat").toString(),
                       documentSnapshot.get("oriLng").toString(),documentSnapshot.get("desLat").toString(),
                       documentSnapshot.get("desLng").toString());
            }
        });

        return Request;
    }
    public Map<String,Object> getDriverActiveReq(User driver){
        setProfile(driver.getEmail());
       profile.collection("driveRequest").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
           @Override
           public void onComplete(@NonNull Task<QuerySnapshot> task) {
               if(task.isSuccessful()){
                   for(QueryDocumentSnapshot doc : task.getResult()){
                       setRequest(doc.getId(),doc.get("reqTip").toString(),doc.get("reqLocation").toString(),
                               doc.get("oriLat").toString(),doc.get("oriLng").toString(),
                               doc.get("desLat").toString(),doc.get("desLng").toString());
                   }
               }
           }
       });
        return Request;
    }

    public ArrayList<Map<String,Object>> getReqList(){
        reqList.clear();
        requests.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document : task.getResult()){
                        Log.d("requestList",document.getId());
                        updateReqList(document.getId(),document.getData());
                    }
                }
                else{
                    Log.d("requestList","failed");
                }
            }
        });
        return this.reqList;
    }
    public void updateReqList(String email,Map<String,Object> reqDetails){
        reqDetails.put("email",email);
        this.reqList.add(reqDetails);
    }


    public void reqAccepted(User rider, User driver){
        setProfile(rider.getEmail());
        profile.update("reqDriver",driver.getEmail());
        Map<String,Object> reqDetails = getRiderRequest(rider);
        requests.document(rider.getEmail()).delete();
        setProfile(driver.getEmail());
        profile.collection("driveRequest").document(rider.getEmail()).set(reqDetails);

    }

    public void addVehicle(User user, Vehicle car){
        setProfile(user.getEmail());
        profile.update("vehMake",car.getMake());
        profile.update("vehModel",car.getModel());
        profile.update("vehColor",car.getColor());
    }
    public Vehicle getCarDetail(User driver){
        setProfile(driver.getEmail());
        profile.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    Log.d("doc","found document");
                    if(documentSnapshot.get("vehMake") != null){
                        Log.d("carDetails","car exist");
                        findVehicle(documentSnapshot.get("vehMake").toString(),documentSnapshot.get("vehModel").toString(),
                                documentSnapshot.get("vehColor").toString(),documentSnapshot.getId());
                    }
                    else{
                        Log.d("carDetails","car does not exist");
                    }

                }
                else{
                    Log.d("doc","Cannot find document");
                }
            }
        });
        return car;
    }
    public void findVehicle(String make,String model,String color,String driver){
        this.car = new Vehicle(make,model,color,driver);

    }













    /**
     * Ratings
     * @param email
     * @param AggPositive
     * @param AggNegative
     *
     * Remove everything below, would be easier to have ratings incorporated in user like username or Uid is
     *
     * TODO:
     * Test Cases

    public void setRating(User user, Integer AggPositive, Integer AggNegative){
        setProfile(user.getEmail());
        Map<String,Object> details = new HashMap<>();
        details.put("AggPositive", AggPositive);
        details.put("AggNegative", AggNegative);
        //this.Rating.put("email",email);


    }
    public Map<String, Object> getRatingDetail(User user){
        setProfile(user.getEmail());
        profile.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                setRating(documentSnapshot.get("email").toString(), (int) documentSnapshot.getLong("AggPositive").intValue(),(int) documentSnapshot.getLong("AggNegative").intValue());
            }
        });

        return Rating;
    }
    public void updatePositiveRating(String email, Integer AggPositive)
    {
        setProfile(user.getEmail());
        profile.document(email).update("AggPositive", FieldValue.increment(1));



    }
    public void updateNegativeRating(String email, Integer AggNegative)
    {

    }
*/











    /**
     * Wallet Stuff
     * @param email
     * @param balance (updating balance would include deposits and withdrawals)
     * @param transaction history
     *
     *
     * TODO:
     * Finish redoing Wallet/balance
     * Add other attributes found in the wallet class?
     * Test Cases
     * */




    }


}

