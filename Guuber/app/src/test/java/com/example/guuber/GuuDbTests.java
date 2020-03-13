package com.example.guuber;

//import static org.junit.Assert.*;
import android.app.Application;
import android.content.Context;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

import org.junit.Test;

public class GuuDbTests {

    private GuuDb mockDb(){
        FirebaseApp.initializeApp(this);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        return new GuuDb();
    }

    @Test
    public void TestUserSetup() {
        GuuDbTests context = this;
        GuuDb gdb = mockDb();
        gdb.setUpUser("123@123.ca","Luke","Atme","LukeAtme","780 780 7807","rider");
        gdb.findUser("123@123.ca");

    }

}
