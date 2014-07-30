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
 * of actually playing over from the beginning after play has stopped entirely. 
 *
 * @author Bryan Gregory Scott -- bscott@brightcove.com
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
    public void testReplay() throws UiObjectNotFoundException, InterruptedException {
        Log.v(TAG, "Beginning testReplay");
        setUpReplay();
        TimeUnit.MILLISECONDS.sleep(msecToPreroll);
        String currentTimeString;
        try {
            toggleSeekControlsVisibility();
            Log.v(TAG, "Getting current time.");
            currentTimeString = currentTimeView.getText();
        } catch (UiObjectNotFoundException currentTimeNotFound) {
            Log.v(TAG, "Current time not found. Retrying...");
            toggleSeekControlsVisibility();
            currentTimeString = currentTimeView.getText();
            Log.v(TAG, "Current time is " + currentTimeString);
        }
        assertFalse("Time has not elapsed.", currentTimeString.equals("00:00"));
        Log.v(TAG, "Finished testReplay");

    }

    /**
     * testReplayCheckAdBreaks checks for ad breaks during a Sample app's replay of the video.
     * If any ad breaks are found, the test will fail. The test defines, registers, and runs a 
     * UiWatcher that watches for a UiObject whose text begins with "Your video will resume in"
     * and fails if it is present. This object is present in Ad Breaks and only ad breaks.
     */
    public void testReplayCheckAdBreaks() throws UiObjectNotFoundException, InterruptedException {
        Log.v(TAG, "Beginning testReplayCheckAdBreaks");
        setUpReplay();

        TimeUnit.MILLISECONDS.sleep(msecToPreroll);
        assertTrue("Failure: Ad Break Not Found.", adOverlayTextView.waitForExists(15000));
        assertFalse("Failure: Seek controls found.", seekBar.waitForExists(15000));
        Log.v(TAG, "Finished testReplayCheckAdBreaks");
    }

    public void testReplayPlay() throws UiObjectNotFoundException, InterruptedException {
        Log.v(TAG, "Beginning testReplayCheckAdBreaks");
        setUpReplay();

        TimeUnit.MILLISECONDS.sleep(msecToPreroll);
        toggleSeekControlsVisibility();
        assertFalse("Failure: Play has not begun.", currentTimeView.getText().equals("00:00"));
        Log.v(TAG, "Finished testReplayCheckAdBreaks");
        
    }


    // Utility Methods

    /**
     * setUpReplay programmatically moves through the video to help expedite the process. It calls upon
     * the skipAhead utility method to skip content blocks and waits through ad blocks.
     */
    private void setUpReplay() throws UiObjectNotFoundException, InterruptedException {
        playVideo();
        TimeUnit.MILLISECONDS.sleep(msecToPreroll);
        Log.v(TAG, "Ad type: " + ADTYPE_PREROLL);
        assertTrue("Preroll ad break did not complete in time.", adOverlayTextView.waitUntilGone(msecAdBreakLength));
        skipAhead(msecToMidroll);
        Log.v(TAG, "Ad type: " + ADTYPE_MIDROLL);
        assertTrue("Midroll ad break did not complete in time.", adOverlayTextView.waitUntilGone(msecAdBreakLength));
        skipAhead(msecToPostroll);
        Log.v(TAG, "Ad type: " + ADTYPE_POSTROLL);
        assertTrue("Postroll ad break did not complete in time.", adOverlayTextView.waitUntilGone(msecAdBreakLength));

        // Due to the nature of the sample app, the first play after it has concluded serves as an
        // alert that the video needs to reload. The second play serves to actually play.
        playVideo();
        TimeUnit.SECONDS.sleep(4);
        playVideo();

    }

}
