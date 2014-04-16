package com.brightcove.player.samples.castonceux.basic;

import java.util.Map;
import java.util.HashMap;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;

import com.brightcove.cast.GoogleCastComponent;
import com.brightcove.cast.GoogleCastEventType;
import com.brightcove.player.event.EventEmitter;
import com.brightcove.player.event.EventEmitterImpl;
import com.brightcove.player.event.EventListener;
import com.brightcove.player.event.EventType;
import com.brightcove.player.event.Event;

/**
 * This app illustrates how to use the Once UX plugin to ensure that:
 *
 * - player controls are hidden during ad playback,
 *
 * - tracking beacons are fired from the client side,
 *
 * - videos are clickable during ad playback and visit the appropriate website,
 *
 * - the companion banner is shown on page switched appropriately as new ads are played 
 *
 * It also covers ensuring that an ad server URL accompanies the content URL.
 *
 * @author Paul Michael Reilly
 */
public class MainActivity extends ActionBarActivity {

    // Private class constants

    private final String TAG = this.getClass().getSimpleName();

    // Private instance variables

    /**
     * A top level event emitter providing communication between the action bar and this main
     * activity.
     */
    private EventEmitter eventEmitter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cast_onceux);
        eventEmitter = new EventEmitterImpl();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        GoogleCastSampleFragment googleCastSampleFragment = GoogleCastSampleFragment.newInstance(eventEmitter, this);
        fragmentTransaction.add(R.id.brightcove_video_view_fragment, googleCastSampleFragment);
        fragmentTransaction.commit();
    }

    /**
     * When the actionbar menu is created, send an event off to the android_cast_plugin
     * to set up the Cast button.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
            Log.v(TAG, "onCreateOptionsMenu:");
            super.onCreateOptionsMenu(menu);
            getMenuInflater().inflate(R.menu.main, menu);

            Map<String, Object> properties = new HashMap<String, Object>();
            properties.put(GoogleCastComponent.CAST_MENU, menu);
            properties.put(GoogleCastComponent.CAST_MENU_RESOURCE_ID, R.id.media_router_menu_item);
            eventEmitter.emit(GoogleCastEventType.SET_CAST_BUTTON, properties);
            return true;
    }

}
