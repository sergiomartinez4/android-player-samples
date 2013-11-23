package com.brightcove.player.samples.fragment.basic;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;

import com.brightcove.player.event.EventEmitter;
import com.brightcove.player.media.Catalog;
import com.brightcove.player.media.PlaylistListener;
import com.brightcove.player.model.Playlist;
import com.brightcove.player.view.BrightcovePlayerFragment;
import com.brightcove.player.view.BrightcoveVideoView;

public class MainFragment extends BrightcovePlayerFragment {

    private static final String TAG = MainFragment.class.getSimpleName();

    private EventEmitter eventEmitter;

    public MainFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.bc_fragment_main, container, false);
        brightcoveVideoView = (BrightcoveVideoView) view.findViewById(R.id.brightcove_video_view);
        super.onCreateView(inflater, container, savedInstanceState);

        eventEmitter = brightcoveVideoView.getEventEmitter();

        // Add a test video to the BrightcoveVideoView.
        Catalog catalog = new Catalog("ErQk9zUeDVLIp8Dc7aiHKq8hDMgkv5BFU7WGshTc-hpziB3BuYh28A..");
        catalog.findPlaylistByReferenceID("stitch", new PlaylistListener() {
            public void onPlaylist(Playlist playlist) {
                brightcoveVideoView.addAll(playlist.getVideos());
                //brightcoveVideoView.start();
            }

            public void onError(String error) {
                Log.e(TAG, error);
            }
        });
        return view;
    }
}
