package com.brightcove.player.samples.onceux.basic.test;

import android.util.Log;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import android.os.RemoteException;
import com.android.uiautomator.core.UiObject;
import com.android.uiautomator.core.UiObjectNotFoundException;
import com.android.uiautomator.core.UiScrollable;
import com.android.uiautomator.core.UiSelector;
import com.android.uiautomator.testrunner.UiAutomatorTestCase;

/**
 * Provide a test case that tests the setUp and tearDown method for the other tests. 
 * 
 * @author Bryan Gregory Scott -- bscott@brightcove.com
 */
public class UiAutomatorTest extends OnceUxUiAutomatorBase {

    final CountDownLatch latch = new CountDownLatch(1);

    /**
     * The Android logcat tag.
     */
    private final String TAG = this.getClass().getSimpleName();

    // Test Methods
    /**
     * testOnceUxUiAutomatorBaseSetUp ensures that the setUp method being used for the other tests is
     * functioning correctly and finding the Apps menu, and the Sample App.
     */
    public void testOnceUxUiAutomatorBaseSetUp() throws InterruptedException {
        try {
            super.setUp();
        } catch (UiObjectNotFoundException setUpException) {
            setUpException.printStackTrace();
            fail("UiObject Not Found in setUp.");
        }
        latch.await(5, TimeUnit.SECONDS);
        assertTrue("Unable to detect the Apps button.", allAppsButton != null);
        assertTrue("Unable to detect Basic Once Ux Sample app.", basicOnceUxSampleApp != null);
    }

    /**
     * testOnceUxUiAutomatorBaseTearDown ensures that the tearDown method being used for the other tests
     * is functioning correctly and is finding the Settings App, the Sample App within the settings app,
     * the Force Stop button for the Sample App's activity, and the Sample App on the recent activity screen.
     */
    public void testOnceUxUiAutomatorBaseTearDown() throws InterruptedException, RemoteException {
        try {
            super.tearDown();
        } catch (UiObjectNotFoundException tearDownException) {
            tearDownException.printStackTrace();
            fail("UiObject Not Found in tearDown.");
        }
        latch.await(5, TimeUnit.SECONDS);
        assertTrue("Unable to detect settings app.", settingsApp != null);
        assertTrue("Unable to detect Sample App within settings menu.", basicOnceUxSampleAppSettings != null);
        assertTrue("Unable to detect Force Stop button in settings app.", forceStopButton != null);
        assertTrue("Unable to detect Sample App in Recent Apps screen.", basicOnceUxSampleAppRecentActivity != null);
    }

    /**
     * A dummy setUp method was created to prevent the super.setUp from acting out of turn.
     */
    @Override protected void setUp() {
        Log.v(TAG, "SetUp is Empty to avoid the super.setUp acting in error.");
    }

    /**
     * A dummy tearDown method was created to prevent the super.tearDown from acting out of turn.
     */
    @Override protected void tearDown() {
        Log.v(TAG, "TearDown is Empty to avoid the super.tearDown acting in error.");
    }

}
