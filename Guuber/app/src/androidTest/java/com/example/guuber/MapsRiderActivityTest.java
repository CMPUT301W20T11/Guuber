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

/**
 * Intent testing for the MapsRiderActivity.
 * Tests are based on current app functionality
 * >>>USER MUST HAVE SIGNED IN  ONCE BEFORE RUNNING THESE TESTS.
 *  Robotium does not want to click the google dialog yet
 */

public class MapsRiderActivityTest {

    private Solo solo;

    @Rule
    public ActivityTestRule<LoginActivity> rule =
            new ActivityTestRule<>(LoginActivity.class,true,true);

    /**
     * * runs before all tests and creates solo instance.
     * YOU MUST HAVE SIGNED IN ONCE TO RUN THIS TEST
     * @throws Exception
     **/
    @Before
    public void setUp() throws Exception{
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
        solo.waitForActivity(LoginActivity.class, 2000);
        solo.clickOnText("As Rider");
        solo.clickOnText("Sign in");
        //solo.clickOnView(solo.getView(android.R.id.gso));
    }

    /**
     * check the rider make request button
     * before providing coordinates
     */
    @Test
    public void makeRequestButton(){
        solo.waitForActivity(MapsRiderActivity.class, 1000);
        solo.clickOnText("Make Request");
        solo.assertCurrentActivity("full coordinates not provided: Should remain on MapsRiderActivity", MapsRiderActivity.class);
    }

    /**
     * check the rider make request button
     * before providing coordinates
     */
    @Test
    public void viewTripsActivity(){
        solo.waitForActivity(MapsRiderActivity.class, 1000);
        solo.pressSpinnerItem(0,2);
        solo.assertCurrentActivity("Activity should change to view trips activity", ViewTripsActivity.class);
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
