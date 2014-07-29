package com.brightcove.player.samples.onceux.basic.test;

import android.util.Log;
import java.util.concurrent.TimeUnit;

import com.android.uiautomator.core.UiObject;
import com.android.uiautomator.core.UiObjectNotFoundException;
import com.android.uiautomator.core.UiScrollable;
import com.android.uiautomator.core.UiSelector;
import com.android.uiautomator.testrunner.UiAutomatorTestCase;

/**
 * Provides a class that will check if the Ad Overlay's countdown during 
 * ad breaks matches up the progress bar's counter within a reasonable degree.
 */
public class AdOverlayCountdown extends OnceUxUiAutomatorBase {

    // Class Constants
    /**
     * The android Logcat tag.
     */
    private final String TAG = this.getClass().getSimpleName();

    /**
     * The UiObject that represents both the play and pause button. It is called in this 
     * method to allow expedited pausing and playing, without having to worry about the 
     * waiting that occurs in the super.playVideo() method.
     */
    private UiObject playPauseButton = new UiObject(new UiSelector().resourceId("android:id/pause"));


    // Test Methods
    /**
     * Compare Ad Overlay's counter to the progress bar. The two should add up to at least 
     * 29 and no more than 31 for the entirety of the ad break. This check should occur every 
     * second until the ad break has concluded. 
     */
    public void testAdOverlayCountdown() throws Exception {
        Log.v(TAG, "Beginning testAdOverlayCountdown");
        playVideo();
        Log.v(TAG, "Play clicked. Pressing pause...");
        playPauseButton.click();
        Log.v(TAG, "Pause clicked. Beginning loop.");
        for (int i = 1; i < 28; i++) {
            Log.v(TAG, "Beginning loop. Loop number: " + i);
            assertTrue(timeComparison(currentTimeSeconds(), adTextTimeSeconds()));
            toggleSeekControlsVisibility();
            Log.v(TAG, "Showing Seek Controls.");
            Log.v(TAG, "Pressing play...");
            playPauseButton.click();
            Log.v(TAG, "Pressing pause...");
            playPauseButton.click();
        }
    }


    // Private Methods

    /**
     * currentTimeSeconds finds the current time located in the seek controls menu and returns the 
     * number of seconds (00:XX) in the form of an integer.
     */
    private int currentTimeSeconds() throws UiObjectNotFoundException {
        UiObject currentTime = new UiObject(new UiSelector().resourceId("android:id/time_current"));
        String currentTimeString;
        try {
            currentTimeString = currentTime.getText();
        } catch (UiObjectNotFoundException currentTimeException) {
            toggleSeekControlsVisibility();
            currentTimeString = currentTime.getText();
        }
        String fragment = currentTimeString.replace("00:", "");
        int currentSeconds = Integer.parseInt(fragment);
        return (currentSeconds);
    }
    /**
     * adTextTimeSeconds finds the string containing the number of seconds left in the ad overlay, 
     * (Your video will resume in XX seconds) then removes the extraneous words, and returns the 
     * number of seconds before the ad break will end in the form of an integer.
     */
    private int adTextTimeSeconds() throws UiObjectNotFoundException {
        UiObject adText = new UiObject(new UiSelector().textStartsWith("Your video will resume in"));
        String adTextString;
        try {
            adTextString = adText.getText();
        } catch (UiObjectNotFoundException adTimeException) {
            toggleSeekControlsVisibility();
            adTextString = adText.getText();
        }
        String fragment1 = adTextString.replace("Your video will resume in ", "");
        String fragment2 = fragment1.replace(" seconds", "");
        int adTextSeconds = Integer.parseInt(fragment2);
        return (adTextSeconds);
    }

    /**
     * timeComparison adds two int variables, and returns true if they add up to 29, 30, or 31.
     * Otherwise, it will fail.
     */
    private boolean timeComparison(int CurrentInt, int AdTimeInt) {
        Log.v(TAG, "Current Time: " + CurrentInt + ". Ad Time: " + AdTimeInt + ".");
        if (CurrentInt + AdTimeInt == 29) {
            Log.v(TAG, "Current Time + Ad Time = 29");
            return true;
        } else if (CurrentInt + AdTimeInt == 30) {
            Log.v(TAG, "Current Time + Ad Time = 30");
            return true;
        } else if (CurrentInt + AdTimeInt == 31) {
            Log.v(TAG, "Current Time + Ad Time = 31");
            return true;
        } else {
            Log.v(TAG, "Current Time + Ad Time is not valid.");
            return false;
        }
    }
}
