package com.example.guuber.model;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;

import java.util.HashMap;
import java.util.Map;

public class GuuDbHelper {
    private static FirebaseFirestore db;
    private static CollectionReference users;
    private static DocumentReference profile;
    public static User user;

    public GuuDbHelper(FirebaseFirestore db){
        this.db = db.getInstance();
        this.users = this.db.collection("Users");
        this.user = new User();
    }
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
    public void setUser(String phone,String email,String first,String last,String uid,String uname){
        this.user.setEmail(email);
        this.user.setPhoneNumber(phone);
        this.user.setFirstName(first);
        this.user.setLastName(last);
        this.user.setUid(uid);
        this.user.setUsername(uname);

    }
    public User getUser(String email ){
        findUser(email);
        return user;
    }


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

    public void deleteUser(String email){
        this.profile = users.document(email);
        profile.delete();
    }
    public void updateUsername(String email,String name){
        users.document(email).update("username",name);
    }
    public void updatePhoneNumber(String email,String number){
        users.document(email).update("phoneNumber",number);
    }
}

