//package com.example.guuber;
//
//import android.app.Activity;
//import android.content.Intent;
//
//import androidx.test.platform.app.InstrumentationRegistry;
//import androidx.test.rule.ActivityTestRule;
//
//import com.example.guuber.model.User;
//import com.robotium.solo.Solo;
//
//import org.junit.Before;
//import org.junit.Rule;
//import org.junit.Test;
//
//public class DriverProfileActivityTest {
//    private Solo solo;
//    private User mockUser;
//
////    private static User mockUser(){
////        return new User("780", "m@gmail.com", "Matt", "Dziubina", "MattUserName",0,0);
////    }
//    @Rule
//    public ActivityTestRule<DriverProfileActivity> rule =
//            new ActivityTestRule<>(DriverProfileActivity.class, true, true);
//
//    @Before
//    public void setup() throws Exception{
//        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
//        solo.waitForActivity(DriverProfileActivity.class, 2000);
//        //mockUser = mockUser();
//        Activity activity = solo.getCurrentActivity();
//        Intent i = new Intent(activity.getApplicationContext(), DriverProfileActivity.class);
//        i.putExtra("caller", "internal");
//        i.putExtra("EMAIL", "osiemusariri@gmail.com");
//        activity.startActivity(i);
//    }
//
//    @Test
//    public void checkActivityStart(){
//        solo.waitForActivity(LoginActivity.class, 1000);
//        solo.assertCurrentActivity("Wrong Activity", DriverProfileActivity.class);
//    }
//
//}
