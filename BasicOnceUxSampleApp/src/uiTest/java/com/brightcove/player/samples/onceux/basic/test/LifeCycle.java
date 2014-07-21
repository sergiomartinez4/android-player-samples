package com.brightcove.player.samples.onceux.basic.test;

import java.util.concurrent.TimeUnit;

import android.util.Log;

import com.android.uiautomator.core.UiObject;
import com.android.uiautomator.core.UiObjectNotFoundException;
import com.android.uiautomator.core.UiScrollable;
import com.android.uiautomator.core.UiSelector;
import com.android.uiautomator.testrunner.UiAutomatorTestCase;

/**
 * Provides a test that will check to see if the Sample App can keep track
 * of the playhead position if the user hits the home button while playing
 * or paused, or during specific events in the timeline.
 */
public class LifeCycle extends OnceUxUiAutomatorBase {

    /**
     * The Android log tag.
     */
    private final String TAG = this.getClass().getSimpleName();

    /**
     * The string that contains the playback time before the Life Cycle process.
     */
    private String currentTimeStringBeforeLifeCycle;

    /**
     * The string that contains the playback time after the Life Cycle process.
     */
    private String currentTimeStringAfterLifeCycle;


    // Test Methods (makes assertions and specifies the location of testing)

    //Tests during Ad Breaks
    /**
     * testLifeCycleAdBreakPlaying tests the life cycle of the app under the circumstances
     * that the user leaves the app by pressing the home button and does not close it in 
     * any way. It calls upon four of the five utility methods. First, it plays the video.
     * Then, it performs lifeCycleInitialCheck, exiting the video, and returning. The video
     * is quickly paused to keep the current time, at which point the time is documented,
     * and then the comparison occurrs. The test asserts stringComparison will return true.
     */
    public void testLifeCycleAdBreakPlaying() throws Exception {
        Log.v(TAG, "Beginning testLifeCycleAdBreakPlaying");
        super.playVideo();
        TimeUnit.SECONDS.sleep(5);
        lifeCycleTime();
        stringComparison();
        // Leaving the sample app quickly after re-entering it can cause the sample app to crash.
        // However, if given a moment to compose itself, this can be avoided. Hence the following delay.
        TimeUnit.SECONDS.sleep(5);
        Log.v(TAG, "Finished testLifeCycleAdBreakPlaying");
    }
    /**
     * testLifeCycleAdBreakPaused performs much the same as testLifeCycleAdBreakPlaying,
     * with one exception: it waits five seconds after play has begun, then pauses the
     * video *before* launching lifeCycleInitialCheck. The rest of the process is identical.
     */
    public void testLifeCycleAdBreakPaused() throws Exception {
        Log.v(TAG, "Beginning testLifeCycleAdBreakPaused");
        super.playVideo();
        TimeUnit.SECONDS.sleep(5);
        pauseVideo();
        lifeCycleTime();
        stringComparison();
        Log.v(TAG, "Finished testLifeCycleAdBreakPaused");
    }

    //Tests during content blocks
    /**
     * testLifeCycleContentBlockPlaying performs similar to testLifeCycleAdBreakPlaying, but
     * it waits 40 seconds (for the preroll ad to finish), then begins lifeCycleInitialCheck.
     * It follows the same pattern as testLifeCycleAdBreakPlaying for the rest of the check.
     */
    public void testLifeCycleContentBlockPlaying() throws Exception {
        Log.v(TAG, "Beginning testLifeCycleContentBlockPlaying");
        super.playVideo();
        TimeUnit.SECONDS.sleep(45);
        lifeCycleTime();
        stringComparison();
        // Leaving the sample app quickly after re-entering it can cause the sample app to crash.
        // However, if given a moment to compose itself, this can be avoided. Hence the following delay.
        TimeUnit.SECONDS.sleep(5);
        Log.v(TAG, "Finished testLifeCycleContentBlockPlaying");
    }
    /**
     * testLifeCycleContentBlockPaused performs exactly like testLifeCycleContentBlockPlaying,
     * but pauses the video before beginning lifeCycleInitialCheck.
     */
    public void testLifeCycleContentBlockPaused() throws Exception {
        Log.v(TAG, "Beginning testLifeCycleContentBlockPaused");
        super.playVideo();
        TimeUnit.SECONDS.sleep(45);
        pauseVideo();
        lifeCycleTime();
        stringComparison();
        Log.v(TAG, "Finished testLifeCycleContentBlockPaused");
    }

    public void testLifeCycleAdBreakSeekControls() throws Exception {
        Log.v(TAG, "Beginning testLifeCycleAdBreakSeekControls");
        super.playVideo();
        TimeUnit.SECONDS.sleep(5);
        lifeCycleTime();
        UiObject seekBar = new UiObject(new UiSelector().resourceId("android:id/mediacontroller_progress"));
        assertFalse("Failure: Seek Bar found.", seekBar.waitForExists(10000));
        Log.v(TAG, "Finished testLifeCycleContentBlockPaused");
    }

    // Utility Methods

    /**
     * The pauseVideo utility method provides a way to pause the sample app using the UiAutomator APIs.
     * This is done much the same as playVideo, without the initial waiting for the video to load. It
     * is assumed that this will only be used in conjunction with playVideo, as if otherwise used, this
     * utility method will execute a play function. This is because pause and play functions are both
     * mapped to a single resource id, "android:id/pause".
     */
    private void pauseVideo() {
        // First, we bring up the play/seek control menu, then press pause.
        UiObject pauseButton = new UiObject(new UiSelector().resourceId("android:id/pause"));
        super.toggleSeekControlsVisibility();
        Log.v(TAG, "Pressing Pause...");
        try {
            pauseButton.click();
        } catch (UiObjectNotFoundException pauseButtonNotFound1) {
            try {
                Log.v(TAG, "Pause button not found. Trying again...");
                pauseButtonNotFound1.printStackTrace();
                super.toggleSeekControlsVisibility();
                pauseButton.click();
            } catch (UiObjectNotFoundException pauseButtonNotFound2) {
                pauseButtonNotFound2.printStackTrace();
                fail("Pause Unsuccessful.");
            }
        }
    }

    /**
     * lifeCycleInitialCheck documents playback location, then exits the app, and reopens
     * it, and documents . It heavily uses the UiAutomator API. The playback location is documented by
     * taking hold of the UiObject, and converting its text into a string. It is the first
     * step of the testing process.
     */
    private void lifeCycleTime() throws Exception {
        Log.v(TAG, "Beginning Life Cycle Check.");
        // First, to make note of the playhead position, we reveal seek controls and examine the text view that has the time elapsed. 
        super.toggleSeekControlsVisibility();
        // Because of the slight inconsistency of the Sample App, we set up try-catch blocks that will be prepared for an exception.
        currentTimeStringBeforeLifeCycle = getCurrentTimeFromUiObject();
        // Then we leave the app, beginning the check, and return to the app.
        getUiDevice().pressHome();
        Log.v(TAG, "Pressing the home button.");
        getUiDevice().pressRecentApps();
        Log.v(TAG, "Pressing the recent apps button.");
        UiObject basicOnceUxSampleAppRecentActivity = new UiObject(new UiSelector().description("Basic ONCE UX Sample App"));
        Log.v(TAG, "Reopening the Basic ONCE UX Sample App.");
        basicOnceUxSampleAppRecentActivity.clickAndWaitForNewWindow();
        Log.v(TAG, "Getting text from UiObject");
        TimeUnit.MILLISECONDS.sleep(500);
        super.toggleSeekControlsVisibility();
        currentTimeStringAfterLifeCycle = getCurrentTimeFromUiObject();

    }

    /**
     * Gets the text 
     */
    private String getCurrentTimeFromUiObject() throws UiObjectNotFoundException {
        UiObject currentTimeView = new UiObject(new UiSelector().resourceId("android:id/time_current"));
        try {
            return currentTimeView.getText();
        } catch (UiObjectNotFoundException uiPlayheadPositionMissing) {
            Log.v(TAG, "Current time not found. Trying again.");
            // This is often as a result of the seek controls (and consequently the playhead location) being hidden, so we will show them and retry.
            super.toggleSeekControlsVisibility();
            return currentTimeView.getText();
        }        
    }

    /**
     * The actual comparison of the test is done in stringComparison. The two strings are
     * both divided into two pairs of integers, then each pair is compared to the other
     * pair. It is asserted that both pairs of strings are identical.
     */
    private void stringComparison() {
        // The strings are both set to XX:XX, and they must be converted into a suitable format.
        // First, we specify a divider string, which will be used as a means of dividing the two pairs of numbers.
        String divider = ":";
        String[] fragment1 = currentTimeStringBeforeLifeCycle.split(divider);
        String[] fragment2 = currentTimeStringAfterLifeCycle.split(divider);
        // Now we parse all the integers out of each string, and assign them new int names.
        int minutes1 = Integer.parseInt(fragment1[0]);
        int seconds1 = Integer.parseInt(fragment1[1]);
        int minutes2 = Integer.parseInt(fragment2[0]);
        int seconds2 = Integer.parseInt(fragment2[1]);
        // and we document them, in the event that things go wrong.
        Log.v(TAG, "Before LifeCycle Time: " + minutes1 + divider + seconds1);
        Log.v(TAG, "After LifeCycle Time: " + minutes2 + divider + seconds2);
        // Then, the actual comparison takes place and the boolean returns are specified.
        assertTrue("Strings not identical. Minutes are different.", minutes1 == minutes2);
        assertTrue("Strings not identical. Seconds are different.", seconds1 == seconds2);
    }

}
