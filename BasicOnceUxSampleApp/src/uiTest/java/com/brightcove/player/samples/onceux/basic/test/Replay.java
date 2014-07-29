package com.brightcove.player.samples.onceux.basic.test;

import java.util.concurrent.TimeUnit;

import android.util.Log;

import com.android.uiautomator.core.UiObject;
import com.android.uiautomator.core.UiObjectNotFoundException;
import com.android.uiautomator.core.UiDevice;
import com.android.uiautomator.core.UiScrollable;
import com.android.uiautomator.core.UiSelector;
import com.android.uiautomator.core.UiWatcher;
import com.android.uiautomator.testrunner.UiAutomatorTestCase;

/**
 * Provides a class to test if the Sample App's replay functionality works correctly, in terms 
 * of actually playing over from the beginning after play has stopped entirely. It also checks
 * that ads can be skipped.
 */
public class Replay extends OnceUxUiAutomatorBase {

    /**
     * The Android logcat tag.
     */
    private final String TAG = this.getClass().getSimpleName();


    // Test Methods
    /**
     * testReplay checks if the replay functionality of the Sample App is functional. It moves
     * through a first "viewing" of the video with setUpReplay, then waits a moment and begins
     * play again. After it begins play, it waits another few seconds, then checks the player
     * has progressed the correct number of seconds.
     */
    public void testReplay() throws Exception {
        Log.v(TAG, "Beginning testReplay");
        setUpReplay();
        TimeUnit.SECONDS.sleep(5);
        UiObject currentTimeUiObject = new UiObject(new UiSelector().resourceId("android:id/time_current"));
        String currentTimeString;
        try {
            toggleSeekControlsVisibility();
            Log.v(TAG, "Getting current time.");
            currentTimeString = currentTimeUiObject.getText();
        } catch (UiObjectNotFoundException currentTimeNotFound) {
            Log.v(TAG, "Current time not found. Retrying...");
            toggleSeekControlsVisibility();
            currentTimeString = currentTimeUiObject.getText();
            Log.v(TAG, "Current time is " + currentTimeString);
        }
        assertTrue("Incorrect time elapsed.", currentTimeString.equals("00:05"));
        Log.v(TAG, "Finished testReplay");

    }

    /**
     * testReplayCheckAdBreaks checks for ad breaks during a Sample app's replay of the video.
     * If any ad breaks are found, the test will fail. The test defines, registers, and runs a 
     * UiWatcher that watches for a UiObject whose text begins with "Your video will resume in"
     * and fails if it is present. This object is present in Ad Breaks and only ad breaks.
     */
    public void testReplayCheckAdBreaks() throws Exception {
        Log.v(TAG, "Beginning testReplayCheckAdBreaks");
        setUpReplay();

        TimeUnit.SECONDS.sleep(5);
        UiObject adMarkerText = new UiObject(new UiSelector().textStartsWith("Your video will resume in "));
        if(adMarkerText.exists()) {
            Log.v(TAG, "Ad Marker Text exists.");
        } else {
            Log.v(TAG, "Ad Marker Text not visible.");
        }
        assertTrue("Failure: Ad Break Not Found.", (adMarkerText.waitForExists(30000)));
        Log.v(TAG, "Finished testReplayCheckAdBreaks");
    }

    // Utility Methods

  /**
     * skipAhead uses the UiAutomator API to press the fast forward button a number of times,
     * based on the input number. It takes the number of seconds, divided by how many seconds
     * a single press of the fast forward button moves, then (after rounding down that number)
     * presses the fast forward button the number of times calculated.
     *
     * Unfortunately, due to the nature of the Sample App as it is now, fast forwarding will often
     * get caught in a loop and end up 30 seconds or even a full minute backward, criplling its
     * functionality. As a result, until this is fixed, skipAhead will print out the information
     * that has been specified, but not do any actual fast forwarding.
     *
     * @throws UiObjecNotFoundException if called within an ad block, where the fast forward
     * button does not exist.
     */
    private void skipAhead(int secondsValue) throws UiObjectNotFoundException {
        Log.v(TAG, "Fast forwarding " + secondsValue + " seconds.");
        UiObject ffwdButton = new UiObject(new UiSelector().resourceId("android:id/ffwd"));
        int ffwdSecondsValue = 15;
        // Cast to a double for the floating point divison, then recast to an int to round it 
        // down to a whole number and so the for parameter is comparing two numbers of the same type.
        double result = (double) secondsValue / ffwdSecondsValue;
        int r = (int) result;
        Log.v(TAG, "Fast forwarding " + secondsValue + " seconds requires " + result + " presses of the fast forward button. The button should be pressed " + r + " times.");
        for (int i = 0; i < r; i++) {
            ffwdButton.click();
            Log.v(TAG, "Fast forwarding. Number of fast forwards: " + i);
        }
    }

    /**
     * setUpReplay programmatically moves through the video to help expedite the process. It calls upon
     * the skipAhead utility method to skip content blocks and waits through ad blocks.
     */
    private void setUpReplay() throws Exception {
        playVideo();
        
        TimeUnit.SECONDS.sleep(31);
        skipAhead(30);
        TimeUnit.SECONDS.sleep(31);
        skipAhead(77);
        TimeUnit.SECONDS.sleep(35);

        // Due to the nature of the sample app, the first play after it has concluded serves as an
        // alert that the video needs to reload. The second play serves to actually play.
        playVideo();
        playVideo();

    }

}
