package com.example.guuber;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
/**
 * Intent testing for the MapsDriverActivity
 * tests are based on current app functionality
 * USER MUST HAVE SIGNED IN  ONCE BEFORE RUNNING THESE TESTS
 * Robotium does not want to click the google dialog yet
 */
public class MapsDriverActivityTest {

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
        solo.clickOnText("As Driver");
        solo.clickOnText("Sign in");
    }

    /**
     * check the driver search Button Function
     */
    @Test
    public void driverSearchButton(){
        solo.waitForActivity(MapsDriverActivity.class, 1000);
        solo.clickOnText("Search");
        solo.assertCurrentActivity("Activity should stay the same", MapsDriverActivity.class);
    }

    /**
     * check the Offer Request Button Function
     */
    @Test
    public void driverOfferRequestButton(){
        solo.waitForActivity(MapsDriverActivity.class, 1000);
        solo.clickOnText("Offer Request");
        solo.assertCurrentActivity("Should remain on Driver Activity",MapsDriverActivity.class);
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
