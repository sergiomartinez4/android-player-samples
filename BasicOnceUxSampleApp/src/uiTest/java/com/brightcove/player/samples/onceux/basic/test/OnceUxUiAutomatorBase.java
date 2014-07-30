package com.brightcove.player.samples.onceux.basic.test;

import android.util.Log;

import java.util.concurrent.TimeUnit;

import android.os.RemoteException;
import com.android.uiautomator.core.UiObject;
import com.android.uiautomator.core.UiObjectNotFoundException;
import com.android.uiautomator.core.UiScrollable;
import com.android.uiautomator.core.UiSelector;
import com.android.uiautomator.testrunner.UiAutomatorTestCase;

/**
 * Provides the setUp and tearDown methods for the other tests.
 *
 * It also serves as an in-depth explanation of the universal setUp and tearDown methods.

 * Due to the varied user interfaces that android supports, it may be that this 
 * method will not work exactly as intended on every device. The following devices
 * do not work for testing with the setUp as it is currently designed. The 
 * following is a list of known incompatible devices:
 *
 * Samsung Galaxy Tab Pro
 *
 * @author Bryan Gregory Scott -- bscott@brightcove.com
 */
public abstract class OnceUxUiAutomatorBase extends UiAutomatorTestCase {

    // Class constants
    
    /**
     * The Android logcat tag.
     */
    private final String TAG = this.getClass().getSimpleName();

    /**
     * The UiObject that represents both the play and pause button. It is defined separately to allow for expedited
     * pausing and playing, without having to worry about the waiting that occurs in the playVideo() method.
     */
    protected final UiObject playPauseButton = new UiObject(new UiSelector().resourceId("android:id/pause"));

    /**
     * The UiObject that represents the text that is present at every Ad Break. It reads "Your video will
     * resume in" followed by how many seconds are left in the ad break.
     */
    protected final UiObject adOverlayTextView = new UiObject(new UiSelector().textStartsWith("Your video will resume in"));

    /**
     * The UiObject that contains the current time of the current segment of the video.
     */
    protected final UiObject currentTimeView = new UiObject(new UiSelector().resourceId("android:id/time_current"));

    /**
     * The UiObject that contains the total time of the current segment of the video.
     */
    protected final UiObject totalTimeView = new UiObject(new UiSelector().resourceId("android:id/time"));

    /**
     * The UiObject that represents the rewind button in the media controls.
     */
    protected final UiObject rewButton = new UiObject(new UiSelector().resourceId("android:id/rew"));

    /**
     * The UiObject that represents the fast forward button in the media controls.
     */
    protected final UiObject ffwdButton = new UiObject(new UiSelector().resourceId("android:id/ffwd"));

    /**
     * The UiObject that represents the seek bar in the media controls.
     */
    protected final UiObject seekBar = new UiObject(new UiSelector().resourceId("android:id/mediacontroller_progress"));



    // Class Constants for skipAhead

    /**
     * The timeout to the first ad break in milliseconds.
     */
    protected final int msecToPreroll = 8000;

    /**
     * The timeout to the second ad break in milliseconds
     */
    protected final int msecToMidroll = 35000;

    /**
     * The timeout to the third ad break in milliseconds
     */
    protected final int msecToPostroll = 112000;

    /**
     * The length of the ad breaks in milliseconds 
     */
    protected final int msecAdBreakLength = 30000;

    /**
     * The string that identifies a preroll ad type
     */
    protected final String ADTYPE_PREROLL = "preroll";

    /**
     * The string that identifies a midroll ad type
     */
    protected final String ADTYPE_MIDROLL = "midroll";

    /**
     * The string that identifies a postroll ad type
     */
    protected final String ADTYPE_POSTROLL = "postroll";


    // Class Variables for the setUp and tearDown
    /**
     * The UiObject that represents the apps button.
     */
    protected UiObject allAppsButton;
    /**
     * The UiObject that represents the Basic ONCE UX Sample App.
     */
    protected UiObject basicOnceUxSampleApp;
    /**
     * The UiObject that represents the Settings app.
     */
    protected UiObject settingsApp;
    /**
     * The UiObject that represents the Basic Once UX Sample App within the Apps section of the Settings app.
     */
    protected UiObject basicOnceUxSampleAppSettings;
    /**
     * The UiObject that represents the Basic Once UX Sample App in the recent apps screen.
     */
    protected UiObject basicOnceUxSampleAppRecentActivity;
    /**
     * The UiObject that represents the force stop buton within settings.
     */
    protected UiObject forceStopButton;

    // Universal setUp and tearDown methods.

    /**
     * Test represents a setUp method for the other tests. Using the UiAutomator API, it
     * goes to the home menu, opens up applications, sifts through and opens up the Basic
     * OnceUx Sample App. It opens the application with the name that matches "Basic ONCE 
     * UX Sample App."
     */
    protected void setUp() throws UiObjectNotFoundException {   
        // Simulate a short press on the HOME button and navigate to apps screen.
        getUiDevice().pressHome();
        Log.v(TAG, "Pressing the home button.");

        allAppsButton = new UiObject(new UiSelector().description("Apps"));
        allAppsButton.clickAndWaitForNewWindow();
        Log.v(TAG, "Pressing the All Apps button.");
        
        // Next is the task of navigating to the sample app in the apps menu and scrolling through until it is found.
        UiObject appsTab = new UiObject(new UiSelector().text("Apps"));
        appsTab.click();
        Log.v(TAG, "Pressing the Apps tab.");

        UiScrollable appViews = new UiScrollable(new UiSelector().scrollable(true));
        appViews.setAsHorizontalList();

        // If it exists, we want to press on the app's icon, launching it.
        basicOnceUxSampleApp = appViews.getChildByText(new UiSelector().className(android.widget.TextView.class.getName()), "Basic ONCE UX Sample App");
        Log.v(TAG, "Pressing the Basic Once Ux Sample App.");
        basicOnceUxSampleApp.clickAndWaitForNewWindow();
    
    }
    
    /**
     * In the tearDown, using UiAutomator API, it goes back to home, reopens the 
     * applications menu, sifts through and finds the settings app, and then force 
     * closes the Sample App. Then, it opens up the recent apps screen, and swipes
     * away the Sample App from that screen. By doing so we entirely remove it 
     * from the device's cached memory. This allows us to have a totally clean 
     * environment when beginning a new test.
     */
    protected void tearDown() throws UiObjectNotFoundException, InterruptedException, RemoteException {
        // We now want to leave the app and close it entirely. The first step is to go to the all apps menu and navigate through it.
        getUiDevice().pressHome();
        Log.v(TAG, "Pressing the Home button.");

        TimeUnit.SECONDS.sleep(1);
        allAppsButton = new UiObject(new UiSelector().description("Apps"));
        allAppsButton.clickAndWaitForNewWindow();
        Log.v(TAG, "Pressing the All Apps button.");

        // Next, we have to navigate through the apps menu.
        UiObject appsTab = new UiObject(new UiSelector().text("Apps"));      
        appsTab.click();
        Log.v(TAG, "Pressing the Apps tab.");

        UiScrollable appViews = new UiScrollable(new UiSelector().scrollable(true));
        appViews.setAsHorizontalList();

        // Next, we open the settings app, and open the particular section that specifies settings for Apps.
        settingsApp = appViews.getChildByText(new UiSelector().className(android.widget.TextView.class.getName()), "Settings");
        settingsApp.click();
        Log.v(TAG, "Pressing the Settings app.");

        UiObject settingsAppsTab = new UiObject (new UiSelector().text("Apps"));
        settingsAppsTab.click();
        Log.v(TAG, "Pressing the Apps tab in the Settings App.");

        // Next, we must choose the "Basic ONCE UX Sample App".
        basicOnceUxSampleAppSettings = new UiObject (new UiSelector().text("Basic ONCE UX Sample App"));
        basicOnceUxSampleAppSettings.click();
        Log.v(TAG, "Pressing the Basic ONCE UX Sample App in the Apps Settings field.");

        // And we Force stop the sample app, pressing OK when the clarification prompt appears, and leave settings.
        forceStopButton = new UiObject(new UiSelector().text("Force stop"));
        forceStopButton.clickAndWaitForNewWindow();

        UiObject okButton = new UiObject(new UiSelector().text("OK").className(android.widget.Button.class));
        okButton.click();

        getUiDevice().pressHome();
        Log.v(TAG, "Pressing the Home button.");

        // The following could serve as an alternative to the first method, or can be used in conjunction 
        // with it. It only works for android devices that have a recent apps button.

        // First, we open the recent apps screen.
        getUiDevice().pressHome();
        Log.v(TAG, "Pressing the Home button.");

        getUiDevice().pressRecentApps();
        Log.v(TAG, "Pressing the Recent Apps button.");

        // Then we register the UiObject and swipe it down in order to remove it from the recent activity screen, and return to home.
        basicOnceUxSampleAppRecentActivity = new UiObject(new UiSelector().description("Basic ONCE UX Sample App"));
        basicOnceUxSampleAppRecentActivity.swipeDown(20);
        Log.v(TAG, "Swiping away the Basic Once Ux Sample App activity Ui Object.");

        getUiDevice().pressHome();
        Log.v(TAG, "Pressing the Home button.");
    }

    // Other Universal Utility Methods

    /**
     * playVideo provides a method that allows for universal access to the play function. It was
     * created as a separate entity to the tests and setUp to help prevent subtle changes from
     * breaking the sample app before function has begun. A universal method helps in this case,
     * and in order to keep the setUp method universal across all test cases, play was kept separate.
     */
    protected void playVideo() throws UiObjectNotFoundException, InterruptedException {

        // Dismiss the first iteration of the Seek Controls
        playPauseButton.waitForExists(5000);
        toggleSeekControlsVisibility();
        // Wait for them to return, then press play.
        if (playPauseButton.waitForExists(5000)) {
            Log.v(TAG, "Pressing Play...");
            TimeUnit.SECONDS.sleep(2);
            try {
                playPauseButton.click();
            } catch (UiObjectNotFoundException playButtonNotFound1) {
                Log.v(TAG, "Play button not found. Trying again.");
                playButtonNotFound1.printStackTrace();
                toggleSeekControlsVisibility();
                TimeUnit.MILLISECONDS.sleep(500);
                playPauseButton.click();
            }
        } else {
            Log.v(TAG, "Play button not found. Trying Seek Control inversion.");
            toggleSeekControlsVisibility();
            TimeUnit.MILLISECONDS.sleep(500);
            try {
                playPauseButton.click();
            } catch (UiObjectNotFoundException playButtonNotFound2) {
                playButtonNotFound2.printStackTrace();
                fail("Play button not found.");
            }
        }
    }

    /**
     * The pauseVideo utility method provides a way to pause the sample app using the UiAutomator APIs.
     * This is done much the same as playVideo, without the initial waiting for the video to load. It
     * is assumed that this will only be used in conjunction with playVideo, as if otherwise used, this
     * utility method will execute a play function. This is because pause and play functions are both
     * mapped to a single resource id, "android:id/pause".
     */
    protected void pauseVideo() throws InterruptedException {
        // First, we bring up the play/seek control menu, then press pause.
        toggleSeekControlsVisibility();
        TimeUnit.MILLISECONDS.sleep(500);
        Log.v(TAG, "Pressing Pause...");
        try {
            playPauseButton.click();
            // If pause isn't found, reveal seek controls and try again.
        } catch (UiObjectNotFoundException pauseButtonNotFound1) {
            try {
                Log.v(TAG, "Pause button not found. Trying again...");
                pauseButtonNotFound1.printStackTrace();
                toggleSeekControlsVisibility();
                TimeUnit.MILLISECONDS.sleep(500);
                playPauseButton.click();
                // If pause still isn't found, bigger problems are occurring than what these tests should handle.
            } catch (UiObjectNotFoundException pauseButtonNotFound2) {
                pauseButtonNotFound2.printStackTrace();
                fail("Pause Unsuccessful.");
            }
        }
    }

  /**
     * skipAhead uses the UiAutomator API to press the fast forward button a number of times,
     * based on the input number. It takes the number of seconds, divided by how many seconds
     * a single press of the fast forward button moves, then (after rounding down that number)
     * presses the fast forward button the number of times calculated.
     *
     * @throws UiObjecNotFoundException if called within an ad block, where the fast forward
     * button does not exist.
     */
    protected void skipAhead(int millisecondsValue) throws InterruptedException {
        Log.v(TAG, "Fast forwarding " + millisecondsValue + " seconds.");
        int finalMilliseconds = (millisecondsValue - 4000);
        int ffwdMillisecondsValue = 15000;
        // Cast to a double for the floating point divison, then recast to an int to round it 
        // down to a whole number and so the for parameter is comparing two numbers of the same type.
        double result = (double) finalMilliseconds / ffwdMillisecondsValue;
        int r = (int) result;
        TimeUnit.SECONDS.sleep(4);
        Log.v(TAG, "Fast forwarding " + millisecondsValue + " seconds requires " + result + " presses of the fast forward button. The button should be pressed " + r + " times.");
        // Fast Forward loop opens and closes seek controls before and after each press to avoid the controls timing out.
        for (int i = 0; i < r; i++) {
            try {
                toggleSeekControlsVisibility();
                ffwdButton.click();
                Log.v(TAG, "Fast forwarding. Number of fast forwards: " + i);
                toggleSeekControlsVisibility();
            } catch (UiObjectNotFoundException ffwdNotFound) {
                // In the event that the sample app's timing is slightly off, skipAhead prepares for a UiObjectNotFoundException.
                Log.v(TAG, "Fast Forward button not found.");
                if(adOverlayTextView.exists() ){
                    Log.v(TAG, "Currently an ad break. No longer fast forwarding.");
                    break;
                } else {
                    Log.v(TAG, "Not currently an ad break. Fast forwarding should still be possible. Trying again.");
                }
            }
        }
        assertTrue("Not an ad when fast forward finished.", adOverlayTextView.waitForExists(8000));
    }

    /**
     * seekControls provides a method that toggles the accessibility of the seek controls menu,
     * which contains the rewind, fast forward, and pause/play buttons, as well as the seek bar
     * and the Ui Objects that contain the current time elapsed and total time.
     */
    protected void toggleSeekControlsVisibility() {
        Log.v(TAG, "Pressing 500, 500 to toggle the seek controls menu.");
        getUiDevice().click(500, 500);
    }

}
