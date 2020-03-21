package com.example.guuber.model;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

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
        users.document(email).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    Log.d("user","found user");
                    setUser(documentSnapshot.get("phoneNumber").toString(),documentSnapshot.get("email").toString(),
                            "P","P","111",documentSnapshot.get("username").toString());
                }
                else{
                    Log.d("user","user does not exist");
                }
            }
        });
    }
    public void setUser(String email,String phone,String first,String last,String uid,String uname){
        this.user.setEmail(email);
        this.user.setPhoneNumber(phone);
        this.user.setFirstName(first);
        this.user.setLastName(last);
        this.user.setUid(uid);
        this.user.setUsername(uname);

    }
    public User getUser(String email ){
        findUser(email);
        return this.user;
    }
    public void createUser(){

    }
}
