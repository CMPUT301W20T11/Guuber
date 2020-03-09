package com.example.guuber;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

import static androidx.constraintlayout.widget.Constraints.TAG;

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
    //find the doc and uses it as a reference
    public void findDoc(final String username){
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
    // gets user's information
    // --------NOTE: That the user/Rider/Driver class constructor must have no aruguments---------------------
    //-------- Nebye can you try and see if there is another way to do this?-------------------------------
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



}
