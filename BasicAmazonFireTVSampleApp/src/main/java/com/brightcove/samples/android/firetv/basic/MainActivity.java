package com.brightcove.samples.android.firetv.basic;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

import com.brightcove.player.model.Video;
import com.brightcove.player.view.BrightcovePlayer;
import com.brightcove.player.view.BrightcoveVideoView;

/**
 * This app illustrates how to load and play a bundled video with the Brightcove Player for Android.
 *
 * @author Billy Hnath
 */
public class MainActivity extends BrightcovePlayer {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // When extending the BrightcovePlayer, we must assign the BrightcoveVideoView
        // before entering the superclass. This allows for some stock video player lifecycle
        // management.
        setContentView(R.layout.firetv_activity_main);
        brightcoveVideoView = (BrightcoveVideoView) findViewById(R.id.brightcove_video_view);
        super.onCreate(savedInstanceState);

        // Add a test video from the res/raw directory to the BrightcoveVideoView.
        String PACKAGE_NAME = getApplicationContext().getPackageName();
        Uri video = Uri.parse("android.resource://" + PACKAGE_NAME + "/" + R.raw.shark);
        brightcoveVideoView.add(Video.createVideo(video.toString()));
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        Log.v(TAG, "onKeyUp: " + keyCode + ", " + event);

        switch(keyCode) {
            case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                if(brightcoveVideoView.isPlaying()) {
                    brightcoveVideoView.pause();
                } else {
                    brightcoveVideoView.start();
                }
                break;
            case KeyEvent.KEYCODE_MEDIA_REWIND:
                break;
            case KeyEvent.KEYCODE_MEDIA_FAST_FORWARD:
                break;
        }
        return super.onKeyUp(keyCode, event);
    }
}