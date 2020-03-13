package com.example.guuber;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;


/**
 * Intent testing for the scanQrActivity.
 *  Tests are based on current app functionality
 */
public class ScanQrActivityTest {

    private Solo solo;

    @Rule
    public ActivityTestRule<scanQrActivity> rule =
            new ActivityTestRule<>(scanQrActivity.class,true,true);

    /**
     * runs before all tests and creates solo instance.
     * @throws Exception
     **/
    @Before
    public void setUp() throws Exception{
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
        solo.waitForActivity(scanQrActivity.class, 2000);
    }

    /**
     * check that scanQR
     * stays in Activity
     */
    @Test
    public void onScanButtonClick(){
        solo.waitForActivity(scanQrActivity.class, 1000);
        solo.clickOnText("Scan");
        solo.assertCurrentActivity("Activity should stay in scanQrActivity", scanQrActivity.class);
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
