package com.brightcove.player.samples.hls.basic;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

import com.brightcove.player.media.Catalog;
import com.brightcove.player.media.PlaylistListener;
import com.brightcove.player.model.Playlist;
import com.brightcove.player.view.BrightcovePlayer;
import com.brightcove.player.view.BrightcoveVideoView;


/**
 * This app illustrates how to use the Brightcove HLS player for
 * Android.
 *
 * @author Paul Matthew Reilly
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
        brightcoveVideoView = (BrightcoveVideoView) findViewById(R.id.brightcove_video_view);
        super.onCreate(savedInstanceState);
//
        Catalog catalog = new Catalog("ErQk9zUeDVLIp8Dc7aiHKq8hDMgkv5BFU7WGshTc-hpziB3BuYh28A..");
        catalog.findPlaylistByReferenceID("stitch", new PlaylistListener() {
                public void onPlaylist(Playlist playlist) {
                    brightcoveVideoView.addAll(playlist.getVideos());
                }

                public void onError(String error) {
                    Log.e(TAG, error);
                }
            });

//        String hlsURL = "http://www.gaiamtv.com/api/brightcove/proxy/30920/master.m3u8?expiration=1400256000&token=fba67d661c868c2e9e52e1788b0249254a629709bc9f3f730bf1f875ab8ef3fe";
//        String hlsURL = "http://bcoveliveios-i.akamaihd.net/hls/live/206506/1959877078001/cuepoints_2/ios_300.m3u8";
//        Source src = new Source(hlsURL, DeliveryType.HLS);
//        SourceCollection col = new SourceCollection(src, DeliveryType.HLS);
//        HashSet<SourceCollection> sourceSet = new HashSet<SourceCollection>(1);
//        sourceSet.add(col);
//        HashMap<String, Object> vidData = new HashMap<String, Object>();
//        vidData.put(Video.Fields.PUBLISHER_ID,"1324209225001");
//        Video video = new Video(vidData,sourceSet);
//        brightcoveVideoView.add(0, video);

        // Log whether or not instance state in non-null.
        if (savedInstanceState != null) {
            Log.v(TAG, "Restoring saved position");
        } else {
            Log.v(TAG, "No saved state");
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent keyEvent) {
        switch(keyCode) {
            case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                if(brightcoveVideoView.isPlaying()) {
                    brightcoveVideoView.pause();
                } else {
                    brightcoveVideoView.start();
                }
                break;
        }
        return false;
    }
}