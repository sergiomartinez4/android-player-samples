package com.brightcove.player.samples.castonceux.basic;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.brightcove.cast.GoogleCastComponent;
import com.brightcove.cast.GoogleCastEventType;
import com.brightcove.player.event.Event;
import com.brightcove.player.event.EventEmitter;
import com.brightcove.player.event.EventListener;
import com.brightcove.player.view.BrightcovePlayerFragment;
import com.brightcove.player.view.BrightcoveVideoView;
import com.brightcove.player.view.BrightcovePlayer;
import com.brightcove.player.view.BrightcoveVideoView;
import com.brightcove.plugin.onceux.OnceUxPlugin;
import com.brightcove.plugin.onceux.event.OnceUxEventType;

import com.google.sample.castcompanionlibrary.widgets.MiniController;

import java.util.HashMap;
import java.util.Map;

/**
 * ...
 *
 * Created by bhnath on 3/10/14.
 * Modified by Paul Michael Reilly (combined basic Cast and OnceUx sample apps)
 */
public class GoogleCastSampleFragment extends BrightcovePlayerFragment {

    // Public class constants

    public static final String TAG = GoogleCastSampleFragment.class.getSimpleName();

    // Private class variables

    private static EventEmitter eventEmitter;
    private static Context context;

    // Private instance variables

    private GoogleCastComponent googleCastComponent;
    private MiniController miniController;

    //Provide a pair of URLs, one for the VMAP data that will tell the plugin when to send
    //tracking beacons, when to hide the player controls and what the click through URL for the
    //ads shoud be.  The VMAP data will also identify what the componion ad should be and what
    //it's click through URL is.

    // The OnceUX plugin VMAP data URL.
    private String onceUxVMAPDataUrl = "http://onceux.unicornmedia.com/now/ads/vmap/od/auto/95ea75e1-dd2a-4aea-851a-28f46f8e8195/43f54cc0-aa6b-4b2c-b4de-63d707167bf9/9b118b95-38df-4b99-bb50-8f53d62f6ef8??umtp=0";

    // The OnceUX plugin content URL.
    private String onceUxContentUrl = "http://cdn5.unicornmedia.com/now/stitched/mp4/95ea75e1-dd2a-4aea-851a-28f46f8e8195/00000000-0000-0000-0000-000000000000/3a41c6e4-93a3-4108-8995-64ffca7b9106/9b118b95-38df-4b99-bb50-8f53d62f6ef8/0/0/105/1438852996/content.mp4";

    /**
     * Static initializer method for the fragment to get easy access to the EventEmitter
     * and Context from the top level Activity.
     */
    public static GoogleCastSampleFragment newInstance(EventEmitter emitter, Context theContext) {
        GoogleCastSampleFragment googleCastSampleFragment = new GoogleCastSampleFragment();
        eventEmitter = emitter;
        context = theContext;
        return googleCastSampleFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Perform the internal wiring to be able to make use of the BrightcovePlayerFragment.
        View view = inflater.inflate(R.layout.cast_onceux_fragment, container, false);
        brightcoveVideoView = (BrightcoveVideoView) view.findViewById(R.id.brightcove_video_view);
        brightcoveVideoView.setEventEmitter(eventEmitter);
        super.onCreateView(inflater, container, savedInstanceState);

        // Initialize the android_cast_plugin which requires the application id of your Cast
        // receiver application.
        String applicationId = getResources().getString(R.string.application_id);
        googleCastComponent = new GoogleCastComponent(eventEmitter, applicationId, context);

        // Initialize the MiniController widget which will allow control of remote media playback.
        miniController = (MiniController) view.findViewById(R.id.miniController1);
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(GoogleCastComponent.CAST_MINICONTROLLER, miniController);
        eventEmitter.emit(GoogleCastEventType.SET_MINI_CONTROLLER, properties);

        // Send the location of the media (url) and its metadata information for remote playback.
        String url = onceUxContentUrl; //getResources().getString(R.string.media_url);
        String imageUrl = getResources().getString(R.string.media_image);
        eventEmitter.emit(GoogleCastEventType.SET_MEDIA_METADATA, buildMetadataProperties("subTitle", "title", "studio", imageUrl, imageUrl, url));

        //brightcoveVideoView.setVideoPath(url);

        // Setup the event handlers for the OnceUX plugin, register the VMAP data URL inside the
        // plugin and start the video.  The URL must be registered prior to starting the video,
        // otherwise the video will not play.  The plugin will detect that the video has been
        // started and pause it until the ad data is ready or an error condition is detected.
        // On either event the plugin will continue playing the video.
        registerEventHandlers();
        OnceUxPlugin plugin = new OnceUxPlugin(context, brightcoveVideoView);
        plugin.processServerData(onceUxVMAPDataUrl);

        return view;
    }

    private Map<String, Object> buildMetadataProperties(String subTitle, String title, String studio,
                                                        String imageUrl, String bigImageUrl, String url) {
        Log.v(TAG, "buildMetadataProperties: subTitle " + subTitle + ", title: " + title
                + ", studio: " + studio + ", imageUrl: " + imageUrl + ", bigImageUrl: " + bigImageUrl
                + ", url: " + url);
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(GoogleCastComponent.CAST_MEDIA_METADATA_SUBTITLE, subTitle);
        properties.put(GoogleCastComponent.CAST_MEDIA_METADATA_TITLE, title);
        properties.put(GoogleCastComponent.CAST_MEDIA_METADATA_STUDIO, studio);
        properties.put(GoogleCastComponent.CAST_MEDIA_METADATA_IMAGE_URL, imageUrl);
        properties.put(GoogleCastComponent.CAST_MEDIA_METADATA_BIG_IMAGE_URL, bigImageUrl);
        properties.put(GoogleCastComponent.CAST_MEDIA_METADATA_URL, url);
        return properties;
    }

    /**
     * Handle resuming Chromecast notifications on a resume lifecycle event.
     */
    @Override
    public void onResume() {
        super.onResume();
        eventEmitter.emit(GoogleCastEventType.SET_NOTIFICATIONS);
    }

    /**
     * Handle pausing Chromecast nofications on a pause lifecycle event.
     */
    @Override
    public void onPause() {
        super.onPause();
        eventEmitter.emit(GoogleCastEventType.UNSET_NOTIFICATIONS);
    }


    // Private instance methods

    /**
     * Procedural abstraction used to setup event handlers for the OnceUX plugin.
     */
    private void registerEventHandlers() {
        // Handle the case where the ad data URL has not been supplied to the plugin.
        EventEmitter eventEmitter = brightcoveVideoView.getEventEmitter();
        eventEmitter.on(OnceUxEventType.NO_AD_DATA_URL, new EventListener() {
            @Override
            public void processEvent(Event event) {
                // Log the event and display a warning message (later)
                Log.e(TAG, event.getType());
                // TODO: throw up a stock Android warning widget.
            }
        });

        eventEmitter.on(OnceUxEventType.AD_DATA_READY, new EventListener() {
            @Override
            public void processEvent(Event event) {
                Log.i(TAG, "Ad data processing complete.  Starting video...");
                brightcoveVideoView.setVideoPath(onceUxContentUrl);
                brightcoveVideoView.start();
            }
        });
    }

}
