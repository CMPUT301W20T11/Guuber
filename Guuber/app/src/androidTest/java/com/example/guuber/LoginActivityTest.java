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
 *Intent testing for the LoginActivity Class.
 * Tests are based on current app functionality
 */
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
        solo.waitForActivity(LoginActivity.class, 2000);
    }

    /**
     * checks that the activity launched is log in activity
     */
    @Test
    public void checkLogInActivity(){
        solo.assertCurrentActivity("Wrong Activity", LoginActivity.class);
    }

    /**checks sole the functionality of the sign in button**/
    @Test
    public void checkSignIn(){
        solo.waitForActivity(LoginActivity.class, 1000);
        solo.clickOnText("Sign In");
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
