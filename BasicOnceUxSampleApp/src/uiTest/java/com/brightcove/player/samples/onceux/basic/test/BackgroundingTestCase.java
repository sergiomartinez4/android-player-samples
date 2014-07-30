package com.brightcove.player.samples.onceux.basic.test;

import java.util.concurrent.TimeUnit;

import android.util.Log;
import android.os.RemoteException;

import com.android.uiautomator.core.UiObject;
import com.android.uiautomator.core.UiObjectNotFoundException;
import com.android.uiautomator.core.UiScrollable;
import com.android.uiautomator.core.UiSelector;
import com.android.uiautomator.testrunner.UiAutomatorTestCase;

/**
 * Tests that the Sample App can keep track of the playhead position if the user hits the
 * home button while playing or paused, and that the seek controls remain hidden if the
 * backgrounding is initated during an ad break.
 */
public class BackgroundingTestCase extends OnceUxUiAutomatorBase {

    /**
     * The Android log tag.
     */
    private final String TAG = this.getClass().getSimpleName();

    /**
     * The string that contains the playback time before the Backgrounding process.
     */
    private String currentTimeStringBeforeBackground;

    /**
     * The string that contains the playback time after the Backgrounding process.
     */
    private String currentTimeStringAfterBackground;


    // Test Methods

    //Tests during Ad Breaks
    /**
     * testBackgroundAdBreakPlaying tests the life cycle of the app under the circumstances
     * that the user leaves the app by pressing the home button and does not close it in 
     * any way. It calls upon four of the five utility methods. First, it plays the video.
     * Then, it performs getBackgroundTimes, exiting the video, and returning. The video
     * is quickly paused to keep the current time, at which point the time is documented,
     * and then the comparison occurrs. The test asserts stringComparison will return true.
     */
    public void testBackgroundAdBreakPlaying() throws UiObjectNotFoundException, InterruptedException, RemoteException {
        Log.v(TAG, "Beginning testBackgroundAdBreakPlaying");
        playVideo();
        TimeUnit.MILLISECONDS.sleep(msecToPreroll);
        toggleSeekControlsVisibility();
        getBackgroundTimes();
        stringComparison();
        Log.v(TAG, "Finished testBackgroundAdBreakPlaying");
    }
    /**
     * testBackgroundAdBreakPaused performs much the same as testBackgroundAdBreakPlaying,
     * with one exception: it waits five seconds after play has begun, then pauses the
     * video *before* launching getBackgroundTimes. The rest of the process is identical.
     */
    public void testBackgroundAdBreakPaused() throws UiObjectNotFoundException, InterruptedException, RemoteException {
        Log.v(TAG, "Beginning testBackgroundAdBreakPaused");
        playVideo();
        TimeUnit.MILLISECONDS.sleep(msecToPreroll);
        pauseVideo();
        getBackgroundTimes();
        stringComparison();
        Log.v(TAG, "Finished testBackgroundAdBreakPaused");
    }
    /**
     * testBackgroundAdBreakSeekControls backgrounds the sample app during an ad break, then
     * asserts that the seek bar should not be present. If it is present, the test fails.
     */
    public void testBackgroundAdBreakSeekControls() throws UiObjectNotFoundException, InterruptedException, RemoteException {
        Log.v(TAG, "Beginning testBackgroundAdBreakSeekControls");
        playVideo();
        TimeUnit.MILLISECONDS.sleep(msecToPreroll);
        toggleSeekControlsVisibility();
        getBackgroundTimes();
        TimeUnit.SECONDS.sleep(1);
        UiObject seekBar = new UiObject(new UiSelector().resourceId("android:id/mediacontroller_progress"));
        assertFalse("Failure: Seek Bar found.", seekBar.waitForExists(10000));
        Log.v(TAG, "Finished testBackgroundAdBreakSeekControls");
    }


    //Tests during Content Dlocks
    /**
     * testBackgroundContentBlockPlaying performs similar to testBackgroundAdBreakPlaying, but
     * it waits 40 seconds (for the preroll ad to finish), then begins getBackgroundTimes.
     * It follows the same pattern as testBackgroundAdBreakPlaying for the rest of the check.
     */
    public void testBackgroundContentBlockPlaying() throws UiObjectNotFoundException, InterruptedException, RemoteException {
        Log.v(TAG, "Beginning testBackgroundContentBlockPlaying");
        playVideo();
        TimeUnit.MILLISECONDS.sleep(msecToPreroll + msecAdBreakLength);
        toggleSeekControlsVisibility();
        getBackgroundTimes();
        stringComparison();
        Log.v(TAG, "Finished testBackgroundContentBlockPlaying");
    }
    /**
     * testBackgroundContentBlockPaused performs exactly like testBackgroundContentBlockPlaying,
     * but pauses the video before beginning getBackgroundTimes.
     */
    public void testBackgroundContentBlockPaused() throws UiObjectNotFoundException, InterruptedException, RemoteException {
        Log.v(TAG, "Beginning testBackgroundContentBlockPaused");
        playVideo();
        TimeUnit.MILLISECONDS.sleep(msecToPreroll + msecAdBreakLength);
        pauseVideo();
        getBackgroundTimes();
        stringComparison();
        Log.v(TAG, "Finished testBackgroundContentBlockPaused");
    }
    /**
     * testBackgroundContentBlockTotalTime performs a similar test to the other content block tests,
     * but instead of assessing the current time, it assesses the total time.
     */
    public void testBackgroundContentBlockTotalTime() throws UiObjectNotFoundException, InterruptedException, RemoteException {
        Log.v(TAG, "Beginning testBackgroundContentBlockTotalTime");
        // Navigate to the content, and gather the total time information, and assert that it's correct before backgrounding.
        playVideo();
        TimeUnit.MILLISECONDS.sleep(msecToPreroll + msecAdBreakLength);
        toggleSeekControlsVisibility();
        String beforeTime = getTextFromUiObject(totalTimeView);
        assertTrue("Timeline changed, content is now " + beforeTime + ".", beforeTime.equals("01:45"));
        // Then perform getBackgroundTimes, gathering the information again, and assert that it's still correct.
        getBackgroundTimes();
        TimeUnit.SECONDS.sleep(1);
        toggleSeekControlsVisibility();
        String afterTime = getTextFromUiObject(totalTimeView);
        assertTrue("Timeline changed, content is now " + afterTime + ".", afterTime.equals("01:45"));
        Log.v(TAG, "Finished testBackgroundContentBlockTotalTime");
    }


    // Utility Methods

    /**
     * getBackgroundTimes documents the current time, then exits the app, and reopens
     * it, and documents it. The current time is documented by taking hold of the UiObject,
     * and converting its text into a string.
     */
    private void getBackgroundTimes() throws UiObjectNotFoundException, InterruptedException, RemoteException {
        Log.v(TAG, "Beginning getBackgroundTimes.");
        // Obtain the relevant text, then leave the app.
        currentTimeStringBeforeBackground = getTextFromUiObject(currentTimeView);
        getUiDevice().pressHome();
        Log.v(TAG, "Pressing the home button.");
        // Return to the App from the Recent Apps button.
        getUiDevice().pressRecentApps();
        Log.v(TAG, "Pressing the recent apps button.");
        UiObject basicOnceUxSampleAppRecentActivity = new UiObject(new UiSelector().description("Basic ONCE UX Sample App"));
        Log.v(TAG, "Reopening the Basic ONCE UX Sample App.");
        basicOnceUxSampleAppRecentActivity.clickAndWaitForNewWindow();
        // Get the new text
        Log.v(TAG, "Getting text from UiObject");
        TimeUnit.MILLISECONDS.sleep(500);
        toggleSeekControlsVisibility();
        currentTimeStringAfterBackground = getTextFromUiObject(currentTimeView);

    }

    /**
     * Does the actual getting of the text from the user interface.
     */
    private String getTextFromUiObject(UiObject textViewUiObject) throws UiObjectNotFoundException {
        try {
            return textViewUiObject.getText();
        } catch (UiObjectNotFoundException uiPlayheadPositionMissing) {
            Log.v(TAG, "Text object not found. Trying again.");
            // This is often as a result of the seek controls (and consequently the playhead location) being hidden, so we will show them and retry.
            toggleSeekControlsVisibility();
            return textViewUiObject.getText();
        }        
    }

    /**
     * The actual comparison of the test is done in stringComparison. The two strings are
     * both divided into two pairs of integers, then each pair is compared to the other
     * pair. It is asserted that both pairs of strings are identical.
     */
    private void stringComparison() {
        // The strings are both set to XX:XX, and they must be converted into a suitable format to compare. 
        String divider = ":";
        String[] fragment1 = currentTimeStringBeforeBackground.split(divider);
        String[] fragment2 = currentTimeStringAfterBackground.split(divider);

        // We divide the two strings into two pairs of integers.
        int minutes1 = Integer.parseInt(fragment1[0]);
        int seconds1 = Integer.parseInt(fragment1[1]);
        int minutes2 = Integer.parseInt(fragment2[0]);
        int seconds2 = Integer.parseInt(fragment2[1]);

        // Then we assert that the minutes are the same and the seconds are the same.
        Log.v(TAG, "Before Background Time: " + minutes1 + divider + seconds1);
        Log.v(TAG, "After Background Time: " + minutes2 + divider + seconds2);
        assertTrue("Strings not identical. Minutes are different.", minutes1 == minutes2);
        assertTrue("Strings not identical. Seconds are different.", seconds1 == seconds2);
    }

}
