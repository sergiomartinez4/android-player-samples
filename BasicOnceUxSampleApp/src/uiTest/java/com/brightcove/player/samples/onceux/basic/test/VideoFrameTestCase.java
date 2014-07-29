package com.brightcove.player.samples.onceux.basic.test;

import android.util.Log;
import java.util.concurrent.TimeUnit;
import android.view.View;
import android.graphics.Rect;

import com.android.uiautomator.core.UiObject;
import com.android.uiautomator.core.UiObjectNotFoundException;
import com.android.uiautomator.core.UiScrollable;
import com.android.uiautomator.core.UiSelector;
import com.android.uiautomator.testrunner.UiAutomatorTestCase;

/**
 * This class provides a method that tests the video frame bounds to ensure that the video is the same
 * size/location both before and after play. This is done by waiting to play when the setUp() is
 * completed, gathering the information, then inducing a tearDown() and setUp(), then  playing the
 * new instance of the sample app, gathering the information on the frame boundaries, and  comparing
 * the two sets of boundaries.
 *
 * @author Bryan Gregory Scott -- bscott@brightcove.com
 */
public class VideoFrameTestCase extends OnceUxUiAutomatorBase {

    /**
     * The Android logcat tag.
     */
    private final String TAG = this.getClass().getSimpleName();


    // Test Methods

    /**
     * testViewBounds asserts that the particular view bounds of a video are identical before
     * play begins and after play has concluded. First, the test gathers the untouched frame
     * size before the video plays. It gathers the view bounds into a Rect object, then organizes
     * it into four different ints that can be compared later. Then, the test runs tearDown()
     * and setUp() to get a totally fresh version of the sample app, and hits play. After ten
     * seconds, the test collects the bounds of the video frame as a new Rect object and converts
     * the rect into four different ints. Then, the four int pairs are asserted to be identical, as
     * the video's location should not change after play.
     */
    public void testVideoFrameBounds() throws Exception {
        Log.v(TAG, "Beginning testPlayStartMessViewBounds");
        // The play button is being used to check when seek controls first appear,
        // which is when the video gets its first size and location.
        UiObject playButton = new UiObject(new UiSelector().resourceId("android:id/pause"));
        playButton.waitForExists(3000);
        toggleSeekControlsVisibility();

        UiObject brightcoveVideoView = new UiObject(new UiSelector().resourceId("com.brightcove.player.samples.onceux.basic:id/brightcove_video_view"));
        UiObject videoView = brightcoveVideoView.getChild(new UiSelector().className(android.view.View.class));
        String divider = " ";

        // This chunk of code gathers the first set of video bounds, then converts it into four ints.
        Rect videoBoundsRectangle1 = videoView.getBounds();
        String videoBoundsString1 = videoBoundsRectangle1.flattenToString();
        String[] fragments1 = videoBoundsString1.split(divider);
        int left1 = Integer.parseInt(fragments1[0]);
        int top1 = Integer.parseInt(fragments1[1]);
        int right1 = Integer.parseInt(fragments1[2]);
        int bottom1 = Integer.parseInt(fragments1[3]);
        Log.v(TAG, "Preliminary rectangle bounds... Left bound: " + left1 + ". Top bound: " + top1 + ". Right bound: " + right1 + ". Bottom bound: " + bottom1 + ".");

        tearDown();
        setUp();

        // To get the follow up video bounds, we simply play the video. Waiting ten seconds
        // removes the need to worry about its initialization process or seek controls interfering.
        playVideo();
        TimeUnit.SECONDS.sleep(10);

        // This next chunk of code gathers the second set of video bounds, and convers them into four ints.
        Rect videoBoundsRectangle2 = videoView.getBounds();
        String videoBoundsString2 = videoBoundsRectangle2.flattenToString();
        String[] fragments2 = videoBoundsString2.split(divider);
        int left2 = Integer.parseInt(fragments2[0]);
        int top2 = Integer.parseInt(fragments2[1]);
        int right2 = Integer.parseInt(fragments2[2]);
        int bottom2 = Integer.parseInt(fragments2[3]);
        Log.v(TAG, "Follow up rectangle bounds... Left bound: " + left2 + ". Top bound: " + top2 + ". Right bound: " + right2 + ". Bottom bound: " + bottom2 + ".");

        // The four pairs of bounds are then compared. If any are different, the test will fail.
        assertTrue("Left bound of video not correct.", left1 == left2);
        assertTrue("Top bound of video not correct.", top1 == top2);
        assertTrue("Right bound of video not correct.", right1 == right2);
        assertTrue("Bottom bound of video not correct.", bottom1 == bottom2);
    }

}
