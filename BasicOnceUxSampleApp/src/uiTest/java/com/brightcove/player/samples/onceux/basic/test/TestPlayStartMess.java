package com.brightcove.player.samples.onceux.basic.test;

import android.util.Log;

import com.android.uiautomator.core.UiObject;
import com.android.uiautomator.core.UiObjectNotFoundException;
import com.android.uiautomator.core.UiScrollable;
import com.android.uiautomator.core.UiSelector;
import com.android.uiautomator.testrunner.UiAutomatorTestCase;

/**
 * TestPlayStartMess was created in response to a buggy scenario that results when the
 * play button is pressed the instant the seek controls automatically open. The scenario
 * consisted of the following: play not being launched despite the play button being 
 * pressed, the fast forward, rewind, and seek bar being revealed during the ad break,
 * and the first frame of the video appearing instead of the typical black space that 
 * normally occurred before play had begun.
 * 
 * @author Bryan Gregory Scott -- bscott@brightcove.com
 */
public class TestPlayStartMess extends OnceUxUiAutomatorBase {

    /**
     * The Android logcat tag.
     */
    private final String TAG = this.getClass().getSimpleName();

    /**
     * testPlayStartMessFFWDCheck creates the scenario described in the class level comment,
     * then checks for the presence of the fast forward button. If it is present, the test
     * will fail.
     */
    public void testPlayStartMessFFWDCheck() throws UiObjectNotFoundException {
        Log.v(TAG, "Beginning testPlayStartMessFFWDCheck");
        playVideoSpecialized();
        assertFalse("Failure: Fast Forward button found.", ffwdButton.waitForExists(msecToPreroll));
    }

    /**
     * testPlayStartMessFFWDCheck creates the scenario described in the class level comment,
     * then checks for the presence of the rewind button. If it is present, the test will
     * fail.
     */
    public void testPlayStartMessREWCheck() throws UiObjectNotFoundException {
        Log.v(TAG, "Beginning testPlayStartMessREWCheck");
        playVideoSpecialized();
        assertFalse("Failure: Rewind button found.", rewButton.waitForExists(msecToPreroll));
    }

    /**
     * testPlayStartMessFFWDCheck creates the scenario described in the class level comment,
     * then checks for the presence of the seek bar. If it is present, the test will fail.
     */
    public void testPlayStartMessSeekBarCheck() throws UiObjectNotFoundException {
        Log.v(TAG, "Beginning testPlayStartMessSeekBarCheck");
        playVideoSpecialized();
        assertFalse("Failure: Seek Bar found.", seekBar.waitForExists(msecToPreroll));
    }

    /**
     * The scenario as described in the class level comment occurs based on an outdated 
     * version of the playVideo method. As a result, that version needed to be 
     * preserved here for testing.
     */
    private void playVideoSpecialized() throws UiObjectNotFoundException {
        playPauseButton.waitForExists(6000);
        Log.v(TAG, "Pressing Play...");
        try {
            playPauseButton.click();
        } catch (UiObjectNotFoundException playButtonMissing) {
            Log.v(TAG, "Play button not found. Trying again.");
            toggleSeekControlsVisibility();
            playPauseButton.click();
        }
    }

}
