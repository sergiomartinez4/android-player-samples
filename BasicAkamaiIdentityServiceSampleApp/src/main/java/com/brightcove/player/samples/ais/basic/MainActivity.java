package com.brightcove.player.samples.ais.basic;

import android.os.Bundle;
import android.util.Log;

import com.brightcove.player.event.EventEmitter;
import com.brightcove.player.event.EventLogger;
import com.brightcove.player.media.Catalog;
import com.brightcove.player.media.VideoListener;
import com.brightcove.player.model.Video;
import com.brightcove.player.view.BrightcovePlayer;
import com.brightcove.player.view.BrightcoveVideoView;

import com.brightcove.ais.AkamaiIdentityServicesPlugin;
/**
 * This app illustrates how to use the Akamai Identity Service Plugin with the
 * Brightcove Player for Android.
 *
 * @author Billy Hnath
 */

public class MainActivity extends BrightcovePlayer {

    public static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Establish the video object and use it's event emitter to get important notifications
        // and to control logging and media.
        setContentView(R.layout.ais_activity_main);
        brightcoveVideoView = (BrightcoveVideoView) findViewById(R.id.brightcove_video_view);
        super.onCreate(savedInstanceState);
        EventEmitter eventEmitter = brightcoveVideoView.getEventEmitter();
        new EventLogger(eventEmitter, true, getClass().getSimpleName());

        AkamaiIdentityServicesPlugin aisPlugin = new AkamaiIdentityServicesPlugin(brightcoveVideoView, eventEmitter, this);

        // Create the catalog object which will start and play the video.
        Catalog catalog = new Catalog("FqicLlYykdimMML7pj65Gi8IHl8EVReWMJh6rLDcTjTMqdb5ay_xFA..");
        catalog.findVideoByID("2142125168001", new VideoListener() {

            @Override
            public void onError(String error) {
                Log.e(TAG, error);
            }

            @Override
            public void onVideo(Video video) {
                brightcoveVideoView.add(video);
                brightcoveVideoView.start();
            }
        });
    }

}
