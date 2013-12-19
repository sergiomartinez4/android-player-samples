package com.brightcove.player.samples.adobepass.basic;

import android.os.Bundle;

import com.brightcove.adobe.adobepass.AdobePassPlugin;
import com.brightcove.adobe.adobepass.event.AdobePassEvent;
import com.brightcove.adobe.adobepass.event.AdobePassEventType;
import com.brightcove.player.event.EventEmitter;
import com.brightcove.player.view.BrightcovePlayer;
import com.brightcove.player.view.BrightcoveVideoView;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * This app illustrates how to use the AdobePass plugin with the Brightcove Player for Android.
 *
 * @author Billy Hnath
 */
public class MainActivity extends BrightcovePlayer {

    private final String TAG = this.getClass().getSimpleName();

    private EventEmitter eventEmitter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // When extending the BrightcovePlayer, we must assign the BrightcoveVideoView
        // before entering the superclass. This allows for some stock video player lifecycle
        // management.
        setContentView(R.layout.adobepass_activity_main);
        brightcoveVideoView = (BrightcoveVideoView) findViewById(R.id.brightcove_video_view);
        eventEmitter = brightcoveVideoView.getEventEmitter();
        super.onCreate(savedInstanceState);

        //Initialize the Adobe Pass plugin
        AdobePassPlugin adobePlugin = new AdobePassPlugin(brightcoveVideoView, eventEmitter);

        InputStream cert = getResources().openRawResource(R.raw.adobepass);
        String cert_passwd = "adobepass";
        String staging_url = "sp.auth-staging.adobe.com/adobe-services";
        String requestorId = "AdobeBEAST";

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(AdobePassEvent.CREDENTIAL_STORE_PASSWORD, cert_passwd);
        properties.put(AdobePassEvent.CREDENTIAL_STORE, cert);
        properties.put(AdobePassEvent.STAGING_URL, staging_url);
        properties.put(AdobePassEvent.REQUESTOR_ID, requestorId);
        eventEmitter.emit(AdobePassEventType.SET_PARAMETERS, properties);
    }
}
