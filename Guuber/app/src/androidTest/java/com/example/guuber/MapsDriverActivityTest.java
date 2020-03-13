package com.example.guuber;

import android.app.Activity;
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
    public ActivityTestRule<MapsDriverActivity> rule =
            new ActivityTestRule<>(MapsDriverActivity.class,true,true);

    /**
     * * runs before all tests and creates solo instance.
     * @throws Exception
     **/
    @Before
    public void setUp() throws Exception{
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
    }

    /**
     * Gets the Activity
     * @throws Exception
     */
    @Test
    public void start() throws Exception{
        Activity activity = rule.getActivity();
    }

    /**
     * Add a city to the listview and check the city name using assertTrue
     * clear all the cities from the listiew and check again with assertFalse
     */
    @Test
    public void checkSearch(){
        //Asserts that the current activity is the MapsActivity. otherwise "Wrong activity"
        solo.assertCurrentActivity("Wrong Activity", MapsDriverActivity.class);
        solo.clickOnButton("Search"); // click on search button

    }

    /*** Check item taken from the listview*/
    @Test
    public void CheckSpinnerListItem() {
        solo.assertCurrentActivity("Wrong Activity", MapsDriverActivity.class);
        View spinnerIndex1 = solo.getView(Spinner.class, 0); // 0 is the first index
        solo.clickOnView(spinnerIndex1);
        solo.scrollToTop(); //scrolls spinner back to default val (position "MENU")
        solo.clickOnView(solo.getView(TextView.class, 2)); //select first item in the spinner
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
