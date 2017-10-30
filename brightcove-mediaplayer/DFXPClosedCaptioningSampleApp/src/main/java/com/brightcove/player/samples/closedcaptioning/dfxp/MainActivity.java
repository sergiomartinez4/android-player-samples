package com.brightcove.player.samples.closedcaptioning.dfxp;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.brightcove.player.edge.Catalog;
import com.brightcove.player.edge.VideoListener;
import com.brightcove.player.model.Video;
import com.brightcove.player.view.BrightcovePlayer;
import com.brightcove.player.view.BrightcoveVideoView;

/**
 * A sample application for demonstrating playback and control of DFXP/TTML captioned media.
 *
 * @author Billy Hnath (bhnath@brightcove.com)
 */
public class MainActivity extends BrightcovePlayer {
    private final String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // When extending the BrightcovePlayer, we must assign the BrightcoveVideoView
        // before entering the superclass. This allows for some stock video player lifecycle
        // management.
        setContentView(R.layout.activity_main);
        brightcoveVideoView = (BrightcoveVideoView) findViewById(R.id.brightcove_video_view);
        super.onCreate(savedInstanceState);

        // Add a test video to the BrightcoveVideoView.
        Catalog catalog = new Catalog(brightcoveVideoView.getEventEmitter(),
                "3636334163001",
                "BCpkADawqM178_wgE5FkRtooqJ80UcfbASC-39Qy_k2MUy8VZV7d21Q42KntguD_DWph8r5vNdFAlfZqt4En97rshhh4pWa4Psk5SxnH6kmzTsrqZZBD6h-M5eUQ58GyWl6uUTLvj8EwL3si");
        catalog.findVideoByID("3637288623001", new VideoListener() {
            @Override
            public void onVideo(Video video) {
                brightcoveVideoView.add(video);
            }

            @Override
            public void onError(String s) {
                Log.e(TAG, "Could not load video: " + s);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_cc_settings:
                showClosedCaptioningDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}