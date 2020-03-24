package com.example.guuber.model;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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

import java.util.ArrayList;
import java.util.HashMap;
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


    public void makeReq(User user,int tip, String location){
        setProfile(user.getEmail());
        this.profile.update("reqTip",tip);
        this.profile.update("reqLocation",location);
        Map<String,Object> details = new HashMap<>();
        details.put("reqTip",tip);
        details.put("reqLocation",location);
        this.requests.document(user.getEmail()).set(details);


    }
    public void cancelRequest(User user){
        Map<String,Object> delete = new HashMap<>();
        delete.put("reqTip", FieldValue.delete());
        delete.put("reqLocation",FieldValue.delete());
        this.profile.update(delete);
        this.requests.document(user.getEmail()).delete();

    }
    public void setRequest(String email, Object tip , String location ){
        this.Request.put("reqTip", tip);
        this.Request.put("reqLocation",location);
        this.Request.put("email",email);


    }
    public Map<String,Object> getRequestDetail(User user){
        setProfile(user.getEmail());
        profile.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
               setRequest(documentSnapshot.get("email").toString(), documentSnapshot.get("reqTip"),documentSnapshot.get("reqLocation").toString());
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
        return this.getReqList();
    }
    public void updateReqList(String email,Map<String,Object> reqDetails){
        reqDetails.put("email",email);
        this.reqList.add(reqDetails);
    }

    public void acceptReq(Map<String,Object> reqDetails,User driver){
        setProfile(driver.getEmail());

    }

    public void regVehicle(User user, Vehicle car){
        setProfile(user.getEmail());
        profile.update("vehMake",car.getMake());
        profile.update("vehModel",car.getModel());
        profile.update("vehColor",car.getColor());
    }
//    public Vehicle getVehDetail(User user){
//
//    }
}

