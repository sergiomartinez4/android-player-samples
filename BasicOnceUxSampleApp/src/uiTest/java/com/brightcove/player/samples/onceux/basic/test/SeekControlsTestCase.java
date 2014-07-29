package com.brightcove.player.samples.onceux.basic.test;

import java.util.concurrent.TimeUnit;

import android.util.Log;

import com.android.uiautomator.core.UiObject;
import com.android.uiautomator.core.UiObjectNotFoundException;
import com.android.uiautomator.core.UiScrollable;
import com.android.uiautomator.core.UiSelector;
import com.android.uiautomator.testrunner.UiAutomatorTestCase;

public class SeekControlsTestCase extends OnceUxUiAutomatorBase {

    /**
     * The Android logcat tag.
     */
    private final String TAG = this.getClass().getSimpleName();

    /**
     * The UiObject that represents the rewind button.
     */
    UiObject rewButton = new UiObject(new UiSelector().resourceId("android:id/rew"));
    /**
     * The UiObject that represents the fast forward button.
     */
    UiObject ffwdButton = new UiObject(new UiSelector().resourceId("android:id/ffwd"));
    /**
     * The UiObject that represents the total time text view.
     */
    UiObject timeTextView = new UiObject(new UiSelector().resourceId("android:id/time"));
    /**
     * The UiObject that represents the current time text view.
     */
    UiObject currentTimeTextView = new UiObject(new UiSelector().resourceId("android:id/time_current"));
    /**
     * The UiObject that represents the play and pause button.
     */
    UiObject playPauseButton = new UiObject(new UiSelector().resourceId("android:id/pause"));
    /**
     * The String that defines the total content length, in the form that it is presented in the UI.
     */
    String totalContentTime = "01:45";


    // Test Methods

    /**
     * testSeekControlsBreakTime tests that fast forwarding and rewinding once each does not break the video 
     */
    public void testSeekControlsBreakTime() throws UiObjectNotFoundException, InterruptedException {
        Log.v(TAG, "Beginning testSeekControlsBreakTime");
        // Navigate to Content
        playVideo();
        TimeUnit.SECONDS.sleep(5);
        playPauseButton.waitForExists(30000);
        Log.v(TAG, "Ad Break ended.");

        // Test Fast Forward functionality
        ffwdButton.click();
        TimeUnit.SECONDS.sleep(5);
        toggleSeekControlsVisibility();
        String timeString1 = timeTextView.getText();
        assertTrue("Fast Forward caused timeline to break. Total Time: " + timeString1, timeString1.equals(totalContentTime));

        // Test Rewind functionality
        rewButton.click();
        TimeUnit.SECONDS.sleep(5);
        toggleSeekControlsVisibility();
        String timeString2 = timeTextView.getText();
        assertTrue("Rewind caused timeline to break. Total Time: " + timeString2, timeString2.equals(totalContentTime));
    }

    /**
     * testMultipleRewinds navigates to content, then tests that rewinding twice will go backward 10 seconds total.
     */
    public void testMultipleRewinds() throws UiObjectNotFoundException, InterruptedException {
        Log.v(TAG, "Beginning testMultipleRewinds");
        // Navigate to Content.
        playVideo();
        TimeUnit.SECONDS.sleep(5);
        playPauseButton.waitForExists(30000);
        Log.v(TAG, "Ad Break ended.");

        // Pause, Fast Forward, and capture the current time to prepare for rewinding.
        playPauseButton.click();
        TimeUnit.SECONDS.sleep(5);
        toggleSeekControlsVisibility();
        ffwdButton.click();
        String currentTimeString1 = currentTimeTextView.getText();
        TimeUnit.SECONDS.sleep(5);

        // Rewind twice, then capture the current time. Then make the comparison.
        toggleSeekControlsVisibility();
        rewButton.click();
        rewButton.click();
        String currentTimeString2 = currentTimeTextView.getText();
        stringComparison(currentTimeString1, currentTimeString2);
    }

    /**
     * The actual comparison of the test is done in stringComparison. The two strings are both divided into two pairs 
     * of integers, then each pair is compared to the other pair. It is asserted that both pairs of strings are identical.
     */
    private void stringComparison(String currentTimeStringBefore, String currentTimeStringAfter) {
        // The strings are both set to XX:XX, and they must be converted into a suitable format to compare. 
        String divider = ":";
        String[] fragment1 = currentTimeStringBefore.split(divider);
        String[] fragment2 = currentTimeStringAfter.split(divider);

        // We divide the two strings into two pairs of integers.
        int minutes1 = Integer.parseInt(fragment1[0]);
        int seconds1 = Integer.parseInt(fragment1[1]);
        int minutes2 = Integer.parseInt(fragment2[0]);
        int seconds2 = Integer.parseInt(fragment2[1]);

        // Then we assert that the minutes are the same and the seconds are the same.
        int time1 = (minutes1 * 60) + seconds1;
        int time2 = (minutes2 * 60) + seconds2;
        Log.v(TAG, "Before Rewind Time: " + time1);
        Log.v(TAG, "After Rewind Time: " + time2);
        assertTrue("After Rewind Time (" + time2 + ") is not 10 seconds earlier than Before Rewind Time (" + time1 + ").", time2 == (time1 - 10));
    }
}
