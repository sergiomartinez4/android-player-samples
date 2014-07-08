package com.brightcove.player.samples.onceux.basic.test;

import java.util.concurrent.TimeUnit;
import java.util.List;
import java.util.ArrayList;
import android.util.Log;

import com.android.uiautomator.core.UiObject;
import com.android.uiautomator.core.UiObjectNotFoundException;
import com.android.uiautomator.core.UiSelector;
import com.android.uiautomator.core.UiDevice;
import com.android.uiautomator.core.UiWatcher;
import com.android.uiautomator.testrunner.UiAutomatorTestCase;

/**
 * BasicCompanionAdTest would check for companion ads and their links
 * for when the occasion arrises that they are put into the Sample App.
 */
public class BasicCompanionAdTest extends OnceUxUiAutomatorBase {

    /**
     * The Android logcat tag.
     */
    private final String TAG = this.getClass().getSimpleName();

    /**
     * The UiObject that represents the companion ad image.
     */
    private UiObject companionAd;

    /**
     * The UiObject that represents the text that is present at every Ad Break. It reads "Your video will
     * resume in" followed by how many seconds are left in the ad break.
     */
    private UiObject adText = new UiObject(new UiSelector().textStartsWith("Your video will resume in"));

    // Test Methods
    /**
     * Makes the assertion as that there should be a companion ad in the ad breaks.
     */
    public void testCompanionAd() throws Exception {
        Log.v(TAG, "Beginning testCompanionAd");
        super.playVideo();
        assertTrue("Ad Break did not begin within given time.", adText.waitForExists(5000));
        assertTrue("Companion ad not found.", companionCheck());
        Log.v(TAG, "Finished testCompanionAd");
    }

    /**
     * Upon clicking the companion ad, the test assess if browser opens and the correct URL loads.
     * It will wait for the browser's URL text to appear. If it does not appear within 5 seconds,
     * the test will fail. Note that the UiObject representing the browser URL is context-sensitive
     * for the particular ad being used. As a result, the selector should be changed accordingnly
     * if a different ad is used.
     */
    public void testCompanionAdLink() throws Exception {
        Log.v(TAG, "Beginning testCompanionAdLink");
        super.playVideo();
        assertTrue("Ad Break did not begin within given time.", adText.waitForExists(5000));
        UiObject companionAdUrl = new UiObject(new UiSelector().textContains("starbucks.com"));
        if (companionCheck()) {
            companionAd.clickAndWaitForNewWindow();
        } else {
            fail("Companion ad not found.");
        }
        assertTrue("Companion ad did not link to correct url.", companionAdUrl.waitForExists(15000));
        Log.v(TAG, "Finished testCompanionAdLink");
    }

    /**
     * testCompanionAdVanishPrerolls checks if the companion ad accompanying the ad breaks vanish in time.
     * This is done by waiting for the adText UiObject to be present, then performs companionCheck. After 
     * the utility methods are done, it is asserted that the Companion Ad will vanish within the next 5 
     * seconds. Much of the time a false positive arrives as the seek controls appear when the content 
     * block begins, and can hide the companion ad from UiAutomator, so next the seek controls are hidden
     * and it is asserted that the companion ad is gone. Then, the test waits for the next ad break to begin.
     */
    public void testCompanionAdVanish() throws Exception {
        Log.v(TAG, "Beginning testCompanionAdVanish");
        super.playVideo();

        // The following tests that the companion ad vanishes after prerolls.
        assertTrue("Ad Break did not begin within given time.", adText.waitForExists(5000));
        Log.v(TAG, "Prerolls...");
        companionCheck();
        TimeUnit.SECONDS.sleep(5);
        assertTrue("Ad Break did not finish within given time.", companionAd.waitUntilGone(30000));
        super.toggleSeekControlsVisibility();
        assertFalse("Companion ad still present after prerolls.", companionAd.exists());

        // Next, the companion ad that accompanies the Midroll ad break is tested.
        assertTrue("Ad Break did not begin within given time.", adText.waitForExists(35000));
        Log.v(TAG, "Midrolls...");
        companionCheck();
        TimeUnit.SECONDS.sleep(5);
        assertTrue("Ad Break did not finish within given time.", companionAd.waitUntilGone(30000));
        super.toggleSeekControlsVisibility();
        assertFalse("Companion ad still present after midrolls.", companionAd.exists());

        // Next, the companion ad that accompanies the Postroll ad break is tested.
        assertTrue("Ad Break did not begin within given time.", adText.waitForExists(112000));
        Log.v(TAG, "Postrolls...");
        companionCheck();
        TimeUnit.SECONDS.sleep(5);
        assertTrue("Ad Break did not finish within given time.", companionAd.waitUntilGone(30000));
        super.toggleSeekControlsVisibility();
        assertFalse("Companion ad still present after postrolls.", companionAd.exists());
        Log.v(TAG, "Finished testCompanionAdVanish");
    }


    // Utility Methods

    /**
     * companionCheck returns <code>true</code> if it finds the companion and, and <code>false</code> if it does not find
     * it. The method searches for the companion ad frame, using its resource id, <code>android:id/ad_frame</code>
     * Then, it finds the companion ad itself using the frame as a parent Ui Object.
     */
    private boolean companionCheck() throws Exception {
        Log.v(TAG, "Beginning companionCheck");
        UiObject companionAdFrame = new UiObject(new UiSelector().resourceId("com.brightcove.player.samples.onceux.basic:id/ad_frame"));
        if (companionAdFrame.exists()) {
            Log.v(TAG, "Companion ad frame found.");
            try {
                companionAd = companionAdFrame.getChild(new UiSelector().className(android.widget.ImageView.class));
                if (companionAd.exists() && companionAd.isEnabled()) {
                    Log.v(TAG, "Companion ad found.");
                    return true;
                } else {
                    Log.v(TAG, "Companion ad not found.");
                    return false;
                }
            } catch (UiObjectNotFoundException companionAdNotFound) {
                companionAdNotFound.printStackTrace();
                return false;
            }
        } else {
            Log.v(TAG, "Companion ad frame not found.");
            return false;
        }
    }

}
