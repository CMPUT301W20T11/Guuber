package com.example.guuber;

import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class LoginActivityTest {

    private Solo solo;

    @Rule
    public ActivityTestRule<LoginActivity> rule =
            new ActivityTestRule<>(LoginActivity.class,true,true);

    /**
     * * runs before all tests and creates solo instance.
     * @throws Exception
     **/
    @Before
    public void setUp() throws Exception{
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
        solo.waitForActivity(LoginActivity.class, 1000);
    }

    /**
     * checks that the activity launched is log in activity
     */
    @Test
    public void checkLogInActivity(){
        solo.assertCurrentActivity("Wrong Activity", LoginActivity.class);
    }

    /**checks soley the functionality of the sign in button**/
    @Test
    public void checkSignIn(){
        solo.waitForActivity(LoginActivity.class, 1000);
        solo.clickOnText("Sign in");
        solo.assertCurrentActivity("Wrong Activity", MapsRiderActivity.class);
    }


    /**
     * This test checks the functionality of Rider sign in.
     * because rider sign in is the default selection, it also checks the functionality
     * of the sign in button
     */
    @Test
    public void checkRiderSignIn(){
        solo.assertCurrentActivity("Wrong Activity", LoginActivity.class);
        solo.clickOnText("As Rider");
        solo.clickOnText("Sign in");
        solo.waitForActivity(MapsRiderActivity.class, 1000);
        solo.assertCurrentActivity("Wrong Activity (supposed to launch Rider activity",MapsRiderActivity.class);
    }

    /**
     * This test checks the functionality of Driver sign in.
     */
    @Test
    public void checkDriverSignIn(){
        solo.assertCurrentActivity("Wrong Activity", LoginActivity.class);
        solo.clickOnText("As Driver");
        solo.clickOnText("Sign in");
        solo.waitForActivity(MapsRiderActivity.class, 1000);
        solo.assertCurrentActivity("Wrong Activity (supposed to launch Driver activity",MapsDriverActivity.class);

    }


    /**
     * closes the activity after each test
     * @throws Exception
     * */
    @After
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }

}
