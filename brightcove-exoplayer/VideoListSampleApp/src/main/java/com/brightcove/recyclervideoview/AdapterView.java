package com.brightcove.recyclervideoview;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.brightcove.ima.GoogleIMAComponent;
import com.brightcove.ima.GoogleIMAEventType;
import com.brightcove.player.event.Event;
import com.brightcove.player.event.EventEmitter;
import com.brightcove.player.event.EventListener;
import com.brightcove.player.event.EventType;
import com.brightcove.player.model.CuePoint;
import com.brightcove.player.model.DeliveryType;
import com.brightcove.player.model.Source;
import com.brightcove.player.model.Video;
import com.brightcove.player.view.BrightcoveExoPlayerVideoView;
import com.brightcove.player.view.BrightcoveVideoView;
import com.google.ads.interactivemedia.v3.api.AdDisplayContainer;
import com.google.ads.interactivemedia.v3.api.AdsRequest;
import com.google.ads.interactivemedia.v3.api.CompanionAdSlot;
import com.google.ads.interactivemedia.v3.api.ImaSdkFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AdapterView extends RecyclerView.Adapter<AdapterView.ViewHolder> {

    private final List<Video> videoList = new ArrayList<>();

    private Context mContext;
    public AdapterView(Context activityContext) {
        mContext = activityContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //Get Video information
        Video video = videoList == null ? null : videoList.get(position);
        if (video != null) {
            holder.videoTitleText.setText(video.getStringProperty(Video.Fields.NAME));
            /*BrightcoveVideoView videoView = (BrightcoveVideoView) holder.fragment.getVideoView();
            videoView.clear();
            videoView.add(video);
            setupGoogleIMA(videoView);*/
        }
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onViewAttachedToWindow(ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        //holder.videoView.start();
    }

    @Override
    public void onViewDetachedFromWindow(ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        //holder.videoView.stopPlayback();
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        int childCount = recyclerView.getChildCount();
        //We need to stop the player to avoid a potential memory leak.
        for (int i = 0; i < childCount; i++) {
            ViewHolder holder = (ViewHolder) recyclerView.findViewHolderForAdapterPosition(i);
            if (holder != null && holder.videoView != null) {
                holder.videoView.stopPlayback();
            }
        }
    }

    public void setVideoList(@Nullable List<Video> videoList) {
        this.videoList.clear();
        this.videoList.addAll(videoList);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {


        public final Context context;
        public final TextView videoTitleText;
        public final FrameLayout videoFrame;
        public final BrightcoveVideoView videoView;
        public final BrightcoveVideoViewFragment fragment;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            context = itemView.getContext();
            videoFrame = (FrameLayout) itemView.findViewById(R.id.video_frame);
            BrightcoveVideoViewFragment mBrightcoveVideoViewFragment = new BrightcoveVideoViewFragment();
            fragment = mBrightcoveVideoViewFragment;

            android.support.v4.app.FragmentManager manager = ((AppCompatActivity) context).getSupportFragmentManager();
            manager.beginTransaction().add(R.id.video_frame, mBrightcoveVideoViewFragment).commit();

            videoTitleText = (TextView) itemView.findViewById(R.id.video_title_text);
            videoView = (BrightcoveVideoView) mBrightcoveVideoViewFragment.getVideoView();
//            videoView = new BrightcoveExoPlayerVideoView(context);
//            videoFrame.addView(videoView);
//            videoView.finishInitialization();

            /*EventEmitter eventEmitter = videoView.getEventEmitter();
            eventEmitter.on(EventType.ENTER_FULL_SCREEN, new EventListener() {
                @Override
                public void processEvent(Event event) {
                    //You can set listeners on each Video View
                }
            });*/
        }
    }

    /*private static final String TAG = "AdapterView";
    private GoogleIMAComponent googleIMAComponent;
    private String[] googleAds = {
            // Honda Pilot
            "http://pubads.g.doubleclick.net/gampad/ads?sz=400x300&iu=%2F6062%2Fhanna_MA_group%2Fvideo_comp_app&ciu_szs=&impl=s&gdfp_req=1&env=vp&output=xml_vast2&unviewed_position_start=1&m_ast=vast&url=[referrer_url]&correlator=[timestamp]"
    };

    private void setupGoogleIMA(final BrightcoveVideoView brightcoveVideoView) {
        final EventEmitter eventEmitter = brightcoveVideoView.getEventEmitter();

        // Defer adding cue points until the set video event is triggered.
        eventEmitter.on(EventType.DID_SET_SOURCE, new EventListener() {
            @Override
            public void processEvent(Event event) {
                setupCuePoints((Source) event.properties.get(Event.SOURCE), eventEmitter);
                Map<CuePoint, List<String>> cuepointMap = new HashMap<>();
            }
        });

        // Establish the Google IMA SDK factory instance.
        final ImaSdkFactory sdkFactory = ImaSdkFactory.getInstance();

        // Enable logging of ad starts
        eventEmitter.on(EventType.AD_STARTED, new EventListener() {
            @Override
            public void processEvent(Event event) {
                Log.v(TAG, event.getType());
            }
        });

        // Enable logging of any failed attempts to play an ad.
        eventEmitter.on(GoogleIMAEventType.DID_FAIL_TO_PLAY_AD, new EventListener() {
            @Override
            public void processEvent(Event event) {
                Log.v(TAG, event.getType());
            }
        });

        // Enable logging of ad completions.
        eventEmitter.on(EventType.AD_COMPLETED, new EventListener() {
            @Override
            public void processEvent(Event event) {
                Log.v(TAG, event.getType());
            }
        });

        // Set up a listener for initializing AdsRequests. The Google IMA plugin emits an ad
        // request event in response to each cue point event.  The event processor (handler)
        // illustrates how to play ads back to back.
        eventEmitter.on(GoogleIMAEventType.ADS_REQUEST_FOR_VIDEO, new EventListener() {
            @Override
            public void processEvent(Event event) {

                List<CuePoint> cuePoints = (List<CuePoint>) event.properties.get(Event.CUE_POINTS);
                // Create a container object for the ads to be presented.
                AdDisplayContainer container = sdkFactory.createAdDisplayContainer();
                container.setPlayer(googleIMAComponent.getVideoAdPlayer());
                container.setAdContainer(brightcoveVideoView);

                // Build the list of ads request objects, one per ad
                // URL, and point each to the ad display container
                // created above.
                ArrayList<AdsRequest> adsRequests = new ArrayList<AdsRequest>(googleAds.length);
                for (String adURL : googleAds) {
                    AdsRequest adsRequest = sdkFactory.createAdsRequest();
                    adsRequest.setAdTagUrl(adURL);
                    adsRequest.setAdDisplayContainer(container);
                    adsRequests.add(adsRequest);
                }

                // Respond to the event with the new ad requests.
                event.properties.put(GoogleIMAComponent.ADS_REQUESTS, adsRequests);
                eventEmitter.respond(event);
            }
        });

        // Create the Brightcove IMA Plugin and register the event emitter so that the plugin
        // can deal with video events.
        googleIMAComponent = new GoogleIMAComponent(brightcoveVideoView, eventEmitter);
    }

    private void setupCuePoints(Source source, EventEmitter eventEmitter) {
        String cuePointType = "ad";
        Map<String, Object> properties = new HashMap<String, Object>();
        Map<String, Object> details = new HashMap<String, Object>();

        // preroll
        CuePoint cuePoint = new CuePoint(CuePoint.PositionType.BEFORE, cuePointType, properties);
        details.put(Event.CUE_POINT, cuePoint);
        eventEmitter.emit(EventType.SET_CUE_POINT, details);

    }*/
}
