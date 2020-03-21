package com.example.guuber;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.test.espresso.internal.inject.InstrumentationContext;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule ;

import com.example.guuber.model.GuuDbHelper;
import com.example.guuber.model.User;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.robotium.solo.Solo ;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class GuuDbHelperTest {
    private static FirebaseFirestore db;
    private static GuuDbHelper dbHelper;
//    private static CollectionReference users;
    private static User mockUser;
    private static Context instrumentationContext;
    private static Solo solo;
    private User dbUser;


    private static User mockUser(){
        return new User("780", "m@gmail.com", "Matt", "Dziubina", "1", "MattUserName");
    }

    @ClassRule
    public static ActivityTestRule<LoginActivity> rule =
            new ActivityTestRule<>(LoginActivity.class,true,true);

    @BeforeClass
    public static void setUp() throws InterruptedException {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
        solo.waitForActivity(LoginActivity.class, 4000);

        instrumentationContext = InstrumentationRegistry.getInstrumentation().getContext();
        FirebaseApp.initializeApp(instrumentationContext);

        dbHelper = new GuuDbHelper(db);
        mockUser = mockUser();

        // These sleeps are important since the database is async so we need to wait a bit
        Thread.sleep(1000);

    }

    @Test
    public void getUserTest() throws InterruptedException {
        User testUser = new User("473 7373","73@gmail.com","Bob","Bob","1","TheOnlyBob");
        User obtain;
        obtain = dbHelper.getUser("TheOnlyBob");
        String name = obtain.getUsername();

        assertSame(name,"TheOnlyBob");
    }


    @AfterClass
    public static void tearDown() throws InterruptedException {
        // Delete the user we added to the db

        solo.finishOpenedActivities();
    }
}
