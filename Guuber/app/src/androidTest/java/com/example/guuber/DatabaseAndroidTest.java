package com.example.guuber;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.test.espresso.internal.inject.InstrumentationContext;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule ;

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


public class DatabaseAndroidTest {
	private static CollectionReference db;
	private static User mockUser;
	private static Context instrumentationContext;
	private static Solo solo;
	private User dbUser;


	private static User mockUser(){
		return new User("780", "m@gmail.com", "Matt", "Dziubina", "MattUserName",0,0);
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

		db = FirebaseFirestore.getInstance().collection("UsersTest");
		mockUser = mockUser();

		// These sleeps are important since the database is async so we need to wait a bit
		Thread.sleep(1000);

	}

	@Test
	public void addTest() throws InterruptedException {
		// Add a user to the db
		db.document("User1").set(mockUser);
		Thread.sleep(1000);

		// Attempt to query the same user we just added
		db.document("User1").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
			@Override
			public void onComplete(@NonNull Task<DocumentSnapshot> task) {
				dbUser = task.getResult().toObject(User.class);
			}
		});

		Thread.sleep(1000);

		// Ensure the db user and the real user are the same
//		assertEquals(mockUser.getUid(), dbUser.getUid());
	}


	@AfterClass
	public static void tearDown() throws InterruptedException {
		// Delete the user we added to the db
		db.whereEqualTo("uid", "1").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
			@Override
			public void onComplete(@NonNull Task<QuerySnapshot> task) {
				for (QueryDocumentSnapshot document : task.getResult()) {
					document.getReference().delete();
				}
			}
		});
		Thread.sleep(1000);
		solo.finishOpenedActivities();
	}
}
