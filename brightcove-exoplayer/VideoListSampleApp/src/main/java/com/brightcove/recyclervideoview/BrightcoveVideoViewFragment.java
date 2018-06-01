package com.brightcove.recyclervideoview;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.brightcove.ima.GoogleIMAComponent;
import com.brightcove.ima.GoogleIMAEventType;
import com.brightcove.ima.GoogleIMAVideoAdPlayer;
import com.brightcove.player.edge.Catalog;
import com.brightcove.player.edge.VideoListener;
import com.brightcove.player.event.Event;
import com.brightcove.player.event.EventEmitter;
import com.brightcove.player.event.EventListener;
import com.brightcove.player.event.EventType;
import com.brightcove.player.model.CuePoint;
import com.brightcove.player.model.Video;
import com.brightcove.player.view.BaseVideoView;
import com.brightcove.player.view.BrightcoveExoPlayerVideoView;
import com.brightcove.player.view.BrightcoveVideoView;
import com.google.ads.interactivemedia.v3.api.AdDisplayContainer;
import com.google.ads.interactivemedia.v3.api.AdsRequest;
import com.google.ads.interactivemedia.v3.api.ImaSdkFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * An Activity with basic life cycle and full screen support.
 * The onCreate() should be extended to wire up the activity's layout to the mBrightcoveVideoView instance variable.
 * For example: mBrightcoveVideoView = (BrightcoveVideoView) findViewById(R.id.brightcove_video_view);
 *
 * @author mszkut, updated to fragment from activity sweiss
 */

/* Sample implementations:
 * http://www.codeitive.com/fSimHXPPVV/android-brightcove-video-player.html
 * https://github.com/BrightcoveOS/android-player-samples/blob/master/BasicIMASampleApp/src/main/java/com/brightcove/player/samples/ima/basic/MainActivity.java
 *
 * Sample ad url:
                 String ad = "http://pubads.g.doubleclick.net/gampad/ads?" +
                "sz=400x300&iu=%2F6062%2Fhanna_MA_group%2Fvideo_comp_app&ciu_szs=&impl=s&gdfp_req=1&" +
                "env=vp&output=xml_vast2&unviewed_position_start=1&m_ast=vast&url=[referrer_url]&" +
                "correlator=[timestamp]" +
                "http://ad.doubleclick.net/pfadx/CABdemoSite;kw=acb;sz=728x90;ord=29078349023890482394823;dcmt=text/xml" +
                "http://pubads.g.doubleclick.net/gampad/ads?sz=400x300&iu=%2F6062%2Fhanna_MA_group%2Fwrapper_with_comp&ciu_szs=728x90&impl=s&gdfp_req=1&env=vp&output=xml_vast2&unviewed_position_start=1&m_ast=vast&url=[referrer_url]&correlator=[timestamp]";

 */
public class BrightcoveVideoViewFragment extends com.brightcove.player.appcompat.BrightcovePlayerFragment {

    private final String TAG = this.getClass().getSimpleName();
    private final String CUE_POINT_TYPE = "ad";
    private final String CUE_POINT_AD_URL = "ad_url";

    private MediaPlayer adsPlayer = null;
    private boolean mResume = false;
    private Catalog catalog;
    private EventEmitter mEventEmitter;
    private View mRootView;
    private Fragment mFragment;

    // Google IMA plugin
    private GoogleIMAComponent mGoogleIMAComponent;
    private GoogleIMAVideoAdPlayer mVideoAdPlayer;
    private BrightcoveExoPlayerVideoView mBrightcoveVideoView;

    private AdTimeout mAdTimeout;
    private String mReferenceId;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFragment = this;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.bc_videoview, container, false);
        mBrightcoveVideoView = (BrightcoveExoPlayerVideoView) mRootView.findViewById(R.id.brightcove_video_view);

        mReferenceId = "96256312";
//        BrightcoveMediaController mediaController = new BrightcoveMediaController(mBrightcoveVideoView, R.layout.custom_media_controller);
//        mBrightcoveVideoView.setMediaController(mediaController);
        baseVideoView = (BaseVideoView) mRootView.findViewById(R.id.brightcove_video_view);
        baseVideoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isPlaying())
                    resumeVideo();
            }
        });
        super.onCreateView(inflater, container, savedInstanceState);
        mRootView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {
                setupBrightcoveVideoPlayer();
            }

            @Override
            public void onViewDetachedFromWindow(View v) {

            }
        });
        return mRootView;
    }

    public BaseVideoView getVideoView() {
        return baseVideoView;
    }

    private void initAdsPlayer() {

        mVideoAdPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mVideoAdPlayer.onPrepared(mp);//DON'T FORGET to call this
                adsPlayer = mp;
            }
        });

        mVideoAdPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mVideoAdPlayer.onCompletion(mp);//DON'T FORGET to call this
                adsPlayer = null;
            }
        });

        mVideoAdPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                mVideoAdPlayer.onError(mp, what, extra);//DON'T FORGET to call this
                adsPlayer = null;
                return true;
            }
        });

    }

    public boolean isFeatureVideoPlaying() {
        try {
            if (baseVideoView != null && baseVideoView.isPlaying())
                return true;
        } catch (IllegalStateException e) {
        }
        return false;
    }

    public boolean isAdVideoPlaying() {
        try {
            if (mVideoAdPlayer != null && mVideoAdPlayer.isPlaying())
                return true;
        } catch (IllegalStateException e) {
        }
        return false;
    }

    public boolean isPlaying() {
        if (isFeatureVideoPlaying() || isAdVideoPlaying())
            return true;

        return false;
    }


    public void setupBrightcoveVideoPlayer() {
        Log.i("zl9", "setupBrightcoveVideoPlayer");
        mEventEmitter = baseVideoView.getEventEmitter();
        setupEventListeners();

        initAdsPlayer();

        catalog = new Catalog(mEventEmitter, getString(R.string.account), getString(R.string.policy));
        // get the video from Video Cloud server and play it
        catalog.findVideoByID("5790788419001", new VideoListener() {
//        catalog.findVideoByReferenceID(mReferenceId, new VideoListener() {
            @Override
            public void onVideo(Video video) {
                Log.i("zl9", "onVideo");
//                video.getProperties().get("FLVFullLength"));
                baseVideoView.add(video);
                // Auto play: the GoogleIMAComponent will postpone
                // playback until the Ad Rules are loaded.
                //baseVideoView.start();
            }

            @Override
            public void onError(String error) {
                Log.i("zl9", "onError "+ error);
                Log.e(TAG, error);
            }
        });
    }

    public void resumeVideo() {
        resumeVideo(-1);
    }

    public void resumeVideo(int seekTime) {
        if (baseVideoView == null)
            return;
        Map<String, Object> properties = new HashMap<>();
        baseVideoView.getEventEmitter().emit(EventType.PLAY, properties);
        mVideoAdPlayer.resumeAd();
    }

    public void pauseVideo() {
        if (baseVideoView == null)
            return;
        if (mVideoAdPlayer != null) {
            mVideoAdPlayer.pauseAd();
            mResume = true;
        }
        if (baseVideoView.isPlaying()) {
            mEventEmitter.emit(EventType.PAUSE);
            mResume = true;
        }
    }

    /**
     * Specify where the ad should interrupt the main video.
     */
    private void setupCuePoints() {

        Log.d(TAG, "setupCuePoints: " );
        String cuePointType = CUE_POINT_TYPE; // either ad or code

        // Manage the metadata
        List<CuePoint> points = new ArrayList<CuePoint>();
        com.brightcove.player.model.CuePoint point = null;
        Map<String, Object> properties = new HashMap<String, Object>();

        // pre-roll
            properties.put(CUE_POINT_AD_URL, buildAdUrl());
            point = new com.brightcove.player.model.CuePoint(com.brightcove.player.model.CuePoint.PositionType.BEFORE, cuePointType, properties);
            points.add(point);


//        // mid-roll at 10 seconds.
//        properties.put(CUE_POINT_AD_URL, ad);
//        point = new CuePoint(10 * (int) DateUtils.SECOND_IN_MILLIS, cuePointType, properties);
//        points.add(point);
//
//        // post-roll
//        properties.put(CUE_POINT_AD_URL, ad);
//        point = new CuePoint(CuePoint.PositionType.AFTER, cuePointType, properties);
//        points.add(point);

        //Create details for the cue points
        Map<String, Object> details = new HashMap<String, Object>();
        details.put(Event.CUE_POINTS, points);

        // Dispatch a SET_CUE_POINTS event
        mEventEmitter.emit(EventType.SET_CUE_POINTS, details);
    }


    /**
     * Setup video listeners and the Brightcove IMA Plugin:
     * - add some cue points;
     * - establish a factory object to obtain the Google IMA SDK instance.
     */
    private void setupEventListeners() {

        // Video listeners
        // Defer adding cue points until the set video event is triggered.
        mEventEmitter.on(EventType.DID_SET_SOURCE, new EventListener() {

            public void processEvent(Event event) {
                Log.d(TAG, "DID_SET_SOURCE: " );
                mAdTimeout = new AdTimeout(15000, 1000);    // 15 seconds
                mAdTimeout.start();
                setupCuePoints();
            }
        });

        mEventEmitter.on(EventType.COMPLETED, new EventListener() {
            @Override
            public void processEvent(Event event) {
            }
        });

        mEventEmitter.on(EventType.DID_PLAY, new EventListener() {
            @Override
            public void processEvent(Event event) {
                Log.d(TAG, "DID_PLAY: " );
                if(!mFragment.isVisible()) {
                    pauseVideo();
                    return;
                }
            }
        });

        // Ads listeners
        // This event signaling that cue points are being set on the video
        mEventEmitter.on(EventType.SET_CUE_POINTS, new EventListener() {

            public void processEvent(Event event) {
                Log.d(TAG, "SET_CUE_POINTS: " );
                //         mGoogleIMAComponent.reloadAdsRequest();
            }
        });

        // Establish the Google IMA SDK factory instance.
        final ImaSdkFactory sdkFactory = ImaSdkFactory.getInstance();


       /* Set up a listener for initializing AdsRequests.
        * The Google IMA plugin emits an ad request event in response to each cue point event.
        * The Google IMA plugin emits an ad request event as a result of initializeAdsRequests() being called.
        */
        mEventEmitter.on(GoogleIMAEventType.ADS_REQUEST_FOR_VIDEO, new EventListener() {

            @Override
            public void processEvent(Event event) {
                Log.d(TAG, "ADS_REQUEST_FOR_VIDEO: " );
//                Log.i("zl2", "ADS_REQUEST_FOR_VIDEO " + mReferenceId);
                Log.d(TAG, event.getType());

                List<CuePoint> cuePoints = (List<CuePoint>) event.properties.get(Event.CUE_POINTS);
                if (cuePoints == null) {
                    return;
                }

                // Build the ads and the list of ad request objects, one per ad, and point each to the ad;
                // display container created above.
                ArrayList<AdsRequest> adsRequests = new ArrayList<AdsRequest>(cuePoints.size());

                for (CuePoint point : cuePoints) {

                    // Create a container object for the ads to be presented.
                    AdDisplayContainer container = sdkFactory.createAdDisplayContainer();
                    container.setPlayer(mVideoAdPlayer);
                    container.setAdContainer(baseVideoView);

                    AdsRequest adsRequest = sdkFactory.createAdsRequest();

                    // URL and Type
                    adsRequest.setAdTagUrl(point.getStringProperty(CUE_POINT_AD_URL));
                    adsRequest.setAdWillAutoPlay(true);
                    adsRequest.setAdDisplayContainer(container);
                    adsRequests.add(adsRequest);
                }

                // Respond to the event with the new ad requests.
                event.properties.put(GoogleIMAComponent.ADS_REQUESTS, adsRequests);
                mEventEmitter.respond(event);
            }
        });

        mEventEmitter.on(EventType.AD_STARTED, new EventListener() {
            @Override
            public void processEvent(Event event) {
                Log.d(TAG, "AD_STARTED: " + event.getType());
            }
        });

        mEventEmitter.on(GoogleIMAEventType.DID_FAIL_TO_PLAY_AD, new EventListener() {
            @Override
            public void processEvent(Event event) {
                Log.d(TAG, "DID_FAIL_TO_PLAY_AD: " );
            }
        });

        mEventEmitter.on(EventType.AD_COMPLETED, new EventListener() {
            @Override
            public void processEvent(Event event) {
                Log.d(TAG, "AD_COMPLETED: " );
            }
        });

        mEventEmitter.on(EventType.AD_PAUSED, new EventListener() {
            @Override
            public void processEvent(Event event) {
                Log.d(TAG, "AD_PAUSED: " );
            }
        });


        mEventEmitter.on(EventType.PAUSE, new EventListener() {
            @Override
            public void processEvent(Event event) {
                Log.d(TAG, "PAUSE: " );

            }
        });

        // Create the Brightcove IMA Plugin and pass in the event
        // emitter so that the plugin can integrate with the SDK.
        mGoogleIMAComponent = new GoogleIMAComponent(baseVideoView, mEventEmitter);
        mVideoAdPlayer = mGoogleIMAComponent.getVideoAdPlayer();
    }

    private String buildAdUrl() {


        String fullAdUrl = "http://pubads.g.doubleclick.net/gampad/ads?sz=400x300&iu=%2F6062%2Fhanna_MA_group%2Fvideo_comp_app&ciu_szs=&impl=s&gdfp_req=1&env=vp&output=xml_vast2&unviewed_position_start=1&m_ast=vast&url=[referrer_url]&correlator=[timestamp]";
//        String fullAdUrl = "http://pubads.g.doubleclick.net/gampad/ads?iu=/4011/trb.app/baltimoresun/4011/trb.app/baltimoresun&&sz=3x3&cust_params=pos%3Dpre&impl=s&gdfp_req=1&env=vp&output=xml_vast2&unviewed_position_start=1&clip=96296503";

        Log.d("zl9", "Pre-roll ad url: " + fullAdUrl);
        return fullAdUrl;
    }

    @Override
    public void onResume() {
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        if (mResume) {
            resumeVideo();
        }
        super.onResume();
    }

    @Override
    public void onPause() {
        pauseVideo();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        if (mEventEmitter != null)
            mEventEmitter.off();
        if (mAdTimeout != null)
            mAdTimeout.cancel();
        super.onDestroy();
    }

    private class AdTimeout extends CountDownTimer {
        public AdTimeout(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            if (baseVideoView.isPlaying()) {
                cancel();
            }
        }

        @Override
        public void onFinish() {
            Log.d(TAG, "Time out!");
        }
    }
}
