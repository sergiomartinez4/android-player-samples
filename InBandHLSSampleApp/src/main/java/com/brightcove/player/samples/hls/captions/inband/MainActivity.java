package com.brightcove.player.samples.hls.captions.inband;

import android.os.Bundle;
import android.util.Log;

import com.brightcove.player.display.SeamlessVideoDisplayComponent;
import com.brightcove.player.model.Video;
import com.brightcove.player.samples.hls.basic.R;
import com.brightcove.player.view.BrightcovePlayer;
import com.brightcove.player.view.SeamlessVideoView;

/**
 * A sample application for handling in-band closed captions from an HLS stream
 * using the SeamlessVideoView.
 *
 * @author Billy Hnath (bhnath@brightcove.com)
 */
public class MainActivity extends BrightcovePlayer {

    private final String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // When extending the BrightcovePlayer, we must assign the brightcoveVideoView before
        // entering the superclass. This allows for some stock video player lifecycle
        // management.  Establish the video object and use it's event emitter to get important
        // notifications and to control logging.
        setContentView(R.layout.activity_main);
        brightcoveVideoView = (SeamlessVideoView) findViewById(R.id.brightcove_video_view);
        super.onCreate(savedInstanceState);

        Video video = Video.createVideo("http://cdnbakmi.kaltura.com/p/243342/sp/24334200/playManifest/entryId/0_uka1msg4/flavorIds/1_vqhfu6uy,1_80sohj7p/format/applehttp/protocol/http/a.m3u8");
        video.getProperties().put(Video.Fields.PUBLISHER_ID, "12312423523");
        brightcoveVideoView.add(video);
        brightcoveVideoView.start();

        ((SeamlessVideoDisplayComponent) brightcoveVideoView.getVideoDisplay()).setClosedCaptionsEnabled(true);

        // Log whether or not instance state in non-null.
        if (savedInstanceState != null) {
            Log.v(TAG, "Restoring saved position");
        } else {
            Log.v(TAG, "No saved state");
        }
    }
}