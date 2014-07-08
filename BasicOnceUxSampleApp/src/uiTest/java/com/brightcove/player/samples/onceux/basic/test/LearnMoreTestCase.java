package com.brightcove.player.samples.onceux.basic.test;

import java.util.concurrent.TimeUnit;
import android.util.Log;

import com.android.uiautomator.core.UiObject;
import com.android.uiautomator.core.UiObjectNotFoundException;
import com.android.uiautomator.core.UiSelector;
import com.android.uiautomator.testrunner.UiAutomatorTestCase;
/**
 * This class tests if the "Learn More" button is present. One method tests 
 * the Preroll adbreak, one tests the Midroll adbreak, where the "Learn
 * More" button should not be present, and one tests the Postroll adbreak.
 * 
 * @author Bryan Gregory Scott -- bscott@brightcove.com
 */
public class LearnMoreTestCase extends OnceUxUiAutomatorBase {

    /**
     * The Android logcat tag.
     */
    private final String TAG = this.getClass().getSimpleName();

    /**
     * Provides a quick way to validate IF the learn more button SHOULD be present, as
     * opposed to its actual presence. Will return <code>true</code> or <code>false</code> as instructed by the test.
     */
    private boolean shouldHaveLearnMore;

    /**
     * The setUp runs the super.setUp(), then defines shouldHaveLearnMore as false by default.
     */
    protected void setUp() throws Exception {
        super.setUp();
        shouldHaveLearnMore = false;
    }


    // Test Methods

    /**
     * The Preroll test checks the preroll ad break for the presence of the Learn More button.
     * If the button is present, the test will pass. This is done by calling upon the playVideo
     * utility method from the superclass to begin then waiting a few seconds for the ad break 
     * to start, then it calls upon adBreakHandler, which performs the test.
     */
    public void testLearnMoreCheckPrerolls() throws Exception {
        //Calls upon utility methods, makes assertions that prerolls should have the "Learn More" UiObject.
        super.playVideo();
        Log.v(TAG, "Beginning testLearnMoreCheckPrerolls.");
        shouldHaveLearnMore = true;
        TimeUnit.SECONDS.sleep(10);
        adBreakHandler();
    }

    /**
     * The Midroll test checks the midroll ad break for the presence of the Learn More button. If 
     * the button is not present, the test will pass. Note that this is only the case for the video
     * being used for testing. Other videos may have a Learn More, in which case this test would
     * need to be altered somewhat. The test is done by calling upon the playVideo utility method 
     * from the superclass to begin, then waiting a for the ad break to start, then it calls upon 
     * the adBreakHandler utility method, which performs the test.
     */
    public void testLearnMoreCheckMidrolls() throws Exception {
        //Calls upon utility methods, makes assertions that midrolls should not have the "Learn More" UiObject.
        super.playVideo();
        Log.v(TAG, "Beginning testLearnMoreCheckMidrolls");
        shouldHaveLearnMore = false;
        TimeUnit.SECONDS.sleep(70);
        adBreakHandler();
        }

    /**
     * The Postroll test checks the postroll ad break for the presence of the Learn More button.
     * If the button is present, the test will pass. This is done by calling upon the playVideo
     * utility method from the superclass to begin, then waiting a few seconds for the ad break
     *  to start, then it calls upon the adBreakHandler utility method, which performs the test.
     */
    public void testLearnMoreCheckPostrolls() throws Exception {
        //Calls upon utility methods, makes assertions that prerolls should have the "Learn More" UiObject.
        super.playVideo();
        Log.v(TAG, "Beginning testLearnMoreCheckPostrolls");
        shouldHaveLearnMore = true;
        TimeUnit.MINUTES.sleep(3);
        adBreakHandler();
    }

    /**
     * testLearnMoreLink does an actual press of the Learn More Button, and asserts that when the
     * browser loads, the url should contain "starbucks.com," a context-sensitive url specification.
     * If the Learn More button is not present when the test tries to click on it, then the test fails.
     */
    public void testLearnMoreLink() throws Exception {
        Log.v(TAG, "Beginning testLearnMoreLink");
        super.playVideo();
        shouldHaveLearnMore = true;
        assertTrue("Ad Break did not begin within given time.", adText.waitForExists(5000));
        UiObject learnMoreAdUrl = new UiObject(new UiSelector().textContains("starbucks.com"));
        if (learnMoreCheck()) {
            companionAd.clickAndWaitForNewWindow();
        } else {
            fail("Learn More Button not found.");
        }
        assertTrue("Learn More Button did not link to correct url.", learnMoreAdUrl.waitForExists(15000));
        Log.v(TAG, "Finished testCompanionAdLink");
    }


    // Utility Methods

    /**
     * learnMoreChecker provides a way to keep track of and call upon the conditional presence of
     * the Learn More button. If the conditions match, the test will return true.
     */
    private boolean learnMoreChecker() {
        // Establishes the Learn More button.
        UiObject learnMoreButton = new UiObject(new UiSelector().textContains("Learn More"));
        if (learnMoreButton.exists()) {
            if (shouldHaveLearnMore == true) {
                Log.v(TAG, "Learn More button found. It should be present.");
                return true;
            } else {
                Log.v(TAG, "Learn More button found. It should not be present.");
                return false;
            }
        } else {
            if (shouldHaveLearnMore == false) {
                Log.v(TAG, "Learn More button not found. It should not be present.");
                return true;
            } else {
                Log.v(TAG, "Learn More button not found. It should be present."); 
                return false;
            }
        }
    }

    /**
     * adBreakHandler checks for the presence of the UiObject that is ubiquitous in every ad break,
     * a text view that reads "Your video will resume in" followed by a number of seconds. Upon seeing
     * that object and verifying if it is enabled, the test for Learn More is done, then the test 
     * waits for the object to disappear, signaling the end of the ad break.
     */
    private void adBreakHandler() throws Exception {
        UiObject adMarkerText = new UiObject(new UiSelector().textStartsWith("Your video will resume in"));
        if (adMarkerText.exists() && adMarkerText.isEnabled()) {
            Log.v(TAG, "Ad Break started.");
            assertTrue("Conditions did not match.", learnMoreChecker());
        }
    }
}
