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

public class MapsDriverActivityTest {

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
    }

    /**
     * check the Offer Request Button Function
     */
    @Test
    public void driverOfferRequestButton(){
        solo.waitForActivity(MapsDriverActivity.class, 1000);
        solo.clickOnText("Offer Request");
    }
}
