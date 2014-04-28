package com.brightcove.player.samples.adobepass.webview.basic;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.adobe.adobepass.accessenabler.api.AccessEnabler;
import com.adobe.adobepass.accessenabler.api.AccessEnablerException;
import com.adobe.adobepass.accessenabler.api.IAccessEnablerDelegate;
import com.adobe.adobepass.accessenabler.models.MetadataKey;
import com.adobe.adobepass.accessenabler.models.MetadataStatus;
import com.adobe.adobepass.accessenabler.models.Mvpd;
import com.brightcove.player.event.Event;
import com.brightcove.player.event.EventEmitter;
import com.brightcove.player.event.EventListener;
import com.brightcove.player.event.EventType;
import com.brightcove.player.media.Catalog;
import com.brightcove.player.media.MediaService;
import com.brightcove.player.media.PlaylistFields;
import com.brightcove.player.media.PlaylistListener;
import com.brightcove.player.model.Playlist;
import com.brightcove.player.model.Video;
import com.brightcove.player.view.BrightcovePlayer;
import com.brightcove.player.view.BrightcoveVideoView;

import java.io.InputStream;
import java.net.URLDecoder;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Signature;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This app illustrates how to integrate AdobePass within a webview.
 *
 * @author Billy Hnath (bhnath)
 */
public class MainActivity extends BrightcovePlayer implements IAccessEnablerDelegate {

    private final String TAG = this.getClass().getSimpleName();

    private static final String SP_URL = "sp.auth-staging.adobe.com/adobe-services";
    private static final int WEBVIEW_ACTIVITY = 1;
    private static final String TVE_TOKEN = "tve_token";
    private static final String ID = "1293799933001";

    private String resourceId;
    private ArrayList<String> serviceProvidersUrls;
    private PrivateKey privateKey;

    private EventEmitter eventEmitter;
    private AccessEnabler accessEnabler;
    private Catalog catalog;
    private boolean shouldLogout;

    public MainActivity() {
        serviceProvidersUrls = new ArrayList<String>();
        // The production URL is the default when no URL is passed. If
        // we are using a staging requestorID, we need to pass the
        // staging URL.
        serviceProvidersUrls.add(SP_URL);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // When extending the BrightcovePlayer, we must assign the BrightcoveVideoView
        // before entering the superclass. This allows for some stock video player lifecycle
        // management.
        setContentView(R.layout.adobepass_activity_main);
        brightcoveVideoView = (BrightcoveVideoView) findViewById(R.id.brightcove_video_view);
        eventEmitter = brightcoveVideoView.getEventEmitter();
        super.onCreate(savedInstanceState);

        shouldLogout = true;

        // configure the AdobePass AccessEnabler library

        try {
            accessEnabler = AccessEnabler.Factory.getInstance(this);
            if (accessEnabler != null) {
                accessEnabler.setDelegate(this);
                accessEnabler.useHttps(true);
            }
        } catch (AccessEnablerException e) {
            Log.e(TAG, "Failed to initialize the AccessEnabler library: " + e.getMessage());
            return;
        }

        String credentialStorePassword = getResources().getString(R.string.credential_store_password);
        InputStream credentialStore = getResources().openRawResource(R.raw.adobepass);

        // A signature must be passed along with the requestor id from a private key and a password.
        privateKey = extractPrivateKey(credentialStore, credentialStorePassword);

        catalog = new Catalog("bTO2i--iw3cPl7YYxQPMw3Kw7GW4BHgzVwy1uG4gu4L4ZyYViUaZOg..",
                              "http://bravoapp19.qanet.local:18080/services/library");

        // Media API call will return result with nulled out URL fields if the media
        // is protected. We need to make the adobepass calls to get the token for the media,
        // then make another Media API call with the adobepass token included (in the header or
        // a cookie) which will return a result with non-nulled URL fields.

        catalog.findPlaylistByID(ID, new PlaylistListener() {
            public void onPlaylist(Playlist playlist) {
                brightcoveVideoView.addAll(playlist.getVideos());
            }

            public void onError(String error) {
                Log.e(TAG, error);
            }
        });

        eventEmitter.once(EventType.SELECT_SOURCE, new EventListener() {
            @Override
            public void processEvent(Event event) {
                Video video = (Video) event.properties.get(Event.VIDEO);

                if (video.getSourceCollections().isEmpty()) {
                    event.stopPropagation();
                    event.preventDefault();

                    Map<String, String> customFields = (Map<String, String>) video.getProperties().get(Video.Fields.CUSTOM_FIELDS);
                    String requestorId = null;

                    if (customFields != null) {
                        requestorId = customFields.get("bcadobepassrequestorid");
                        resourceId = customFields.get("bcadobepassresourceid");
                    }

                    if (requestorId == null) {
                        requestorId = "BRIGHTCOVE";
                    }

                    if (resourceId == null) {
                        resourceId = "Brightcove";
                    }

                    Log.v(TAG, "requestorId = " + requestorId + ", resourceId = " + resourceId);

                    try {
                        String signedRequestorId = generateSignature(privateKey, requestorId);

                        // Set the requestor ID; this triggers the authentication process
                        accessEnabler.setRequestor(requestorId, signedRequestorId, serviceProvidersUrls);
                    } catch (AccessEnablerException e) {
                        Log.e(TAG, "Failed to generate signature.");
                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.v(TAG, "onActivityResult: " + requestCode + ", " + resultCode + ", " + data);
        super.onActivityResult(requestCode, resultCode, data);

        shouldLogout = true;

        if (resultCode == RESULT_CANCELED) {
            accessEnabler.setSelectedProvider(null);
        } else if (resultCode == RESULT_OK) {
            accessEnabler.getAuthenticationToken();
        }
    }

    // Make sure we log out once the application is stopped
    @Override
    protected void onStop() {
        Log.v(TAG, "onStop");

        if (shouldLogout) {
            accessEnabler.logout();
        }

        super.onStop();
    }

    private String generateSignature(PrivateKey privateKey, String data) throws AccessEnablerException {
        try {
            Signature rsaSigner = Signature.getInstance("SHA256WithRSA");
            rsaSigner.initSign(privateKey);
            rsaSigner.update(data.getBytes());

            byte[] signature = rsaSigner.sign();
            return new String(Base64.encode(signature, Base64.DEFAULT));
        } catch (Exception e) {
            Log.e(TAG, "Failed to generator signature.", e);
            throw new AccessEnablerException();
        }
    }

    private PrivateKey extractPrivateKey(InputStream PKCSFile, String password) {
        if (PKCSFile == null)
            return null;

        try {
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            keyStore.load(PKCSFile, password.toCharArray());

            String keyAlias = null;
            Enumeration<String> aliases = keyStore.aliases();
            while(aliases.hasMoreElements()) {
                keyAlias = aliases.nextElement();
                if (keyStore.isKeyEntry(keyAlias))
                    break;
            }

            if (keyAlias != null) {
                KeyStore.PrivateKeyEntry keyEntry =
                    (KeyStore.PrivateKeyEntry) keyStore.getEntry(keyAlias,
                                                                 new KeyStore.PasswordProtection(password.toCharArray()));
                return keyEntry.getPrivateKey();
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        return null;
    }

    @Override
    public void setRequestorComplete(int status) {
        Log.v(TAG, "setRequestorComplete: " + status);
        switch (status) {
            case AccessEnabler.ACCESS_ENABLER_STATUS_SUCCESS:
                accessEnabler.getAuthentication();
                break;
            case AccessEnabler.ACCESS_ENABLER_STATUS_ERROR:
                Log.e(TAG, "Config phase failed.");
                break;
            default:
                throw new RuntimeException("Unknown setRequestor() status code.");
        }
    }

    @Override
    public void setAuthenticationStatus(int status, String errorCode) {
        Log.v(TAG, "setAuthenticationStatus: " + status + " , " + errorCode);
        if (status == AccessEnabler.ACCESS_ENABLER_STATUS_SUCCESS) {
            accessEnabler.getAuthorization(resourceId);
        } else if (status == AccessEnabler.ACCESS_ENABLER_STATUS_ERROR) {
            Log.v(TAG, "setAuthenticationStatus: authentication failed.");
        }
    }

    @Override
    public void setToken(String token, String resourceId) {
        Log.v(TAG, "setToken: " + token + " ," + resourceId);

        // Remove the original Video object, because we're going to
        // get another one now that we have a TVE token.
        brightcoveVideoView.clear();

        Log.v(TAG, "removed previous video");

        Map<String, String> options = new HashMap<String, String>();
        options.put(TVE_TOKEN, token);
        catalog.findPlaylistByID(ID, options, new PlaylistListener() {
            public void onPlaylist(Playlist playlist) {
                brightcoveVideoView.addAll(playlist.getVideos());
            }

            public void onError(String error) {
                Log.e(TAG, error);
            }
        });
    }

    @Override
    public void tokenRequestFailed(String resourceId, String errorCode, String errorDescription) {
        Log.v(TAG, "tokenRequestFailed: " + resourceId + ", " + errorCode + ", " + errorDescription);
    }

    @Override
    public void selectedProvider(Mvpd mvpd) {
        Log.v(TAG, "selectedProvider: " + mvpd);
    }

    @Override
    public void displayProviderDialog(ArrayList<Mvpd> mvpds) {
        Log.v(TAG, "displayProviderDialog:" + mvpds);
        accessEnabler.setSelectedProvider(mvpds.get(0).getId());
    }

    // Open the webview activity here to do the URL redirection for both
    // logging in and logging out.
    @Override
    public void navigateToUrl(String url) {
        Log.v(TAG, "navigateToUrl: " + url);

        // if we detect a redirect to our application URL, this is an indication
        // that the authN workflow was completed successfully
        if (!url.equals(URLDecoder.decode(AccessEnabler.ADOBEPASS_REDIRECT_URL))) {
            shouldLogout = false;
            Intent intent = new Intent(MainActivity.this, WebViewActivity.class);
            intent.putExtra("url", url);
            startActivityForResult(intent, WEBVIEW_ACTIVITY);
        }
    }

    @Override
    public void sendTrackingData(com.adobe.adobepass.accessenabler.models.Event event,
                                 ArrayList<String> data) {
        Log.v(TAG, "sendTrackingData: " + event + ", " + data);
    }

    @Override
    public void setMetadataStatus(MetadataKey metadataKey, MetadataStatus metadataStatus) {
        Log.v(TAG, "setMetadataStatus: " + metadataKey + ", " + metadataStatus);
    }

    @Override
    public void preauthorizedResources(ArrayList<String> resources) {
        Log.v(TAG, "preauthorizedResources:" + resources);
    }
}
