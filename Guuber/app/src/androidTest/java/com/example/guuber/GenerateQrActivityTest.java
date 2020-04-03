package com.example.guuber;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 *Intent testing for the QrActivity Class.
 * Tests are based on current app functionality
 *
 */
public class GenerateQrActivityTest {

    private Solo solo;

    @Rule
    public ActivityTestRule<QrActivity> rule =
            new ActivityTestRule<>(QrActivity.class,true,true);

    /**
     * * runs before all tests and creates solo instance.
     * @throws Exception
     **/
    @Before
    public void setUp() throws Exception{
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
        solo.waitForActivity(QrActivity.class, 2000);
    }

    /**
     * check that generate QR
     * stays in Activity
     */
    @Test
    public void onGenerateButtonClick(){
        solo.waitForActivity(QrActivity.class, 1000);
        solo.clickOnText("Generate");
        solo.assertCurrentActivity("Activity should stay in QrActivity", QrActivity.class);
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
