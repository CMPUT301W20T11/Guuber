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
 *  Test currently fails due to requiring the User singleton to be populated
 */
public class ScanQrActivityTest {

    private Solo solo;

    @Rule
    public ActivityTestRule<ScanQrActivity> rule =
            new ActivityTestRule<>(ScanQrActivity.class,true,true);

    /**
     * runs before all tests and creates solo instance.
     * @throws Exception
     **/
    @Before
    public void setUp() throws Exception{
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
        solo.waitForActivity(ScanQrActivity.class, 2000);
    }

    /**
     * check that scanQR
     * stays in Activity
     */
    @Test
    public void onScanButtonClick(){
        solo.waitForActivity(ScanQrActivity.class, 1000);
        solo.clickOnText("Scan");
        solo.assertCurrentActivity("Activity should stay in scanQrActivity", ScanQrActivity.class);
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
