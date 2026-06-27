package com.example.rayzi.demoreels;

import com.example.rayzi.modelclass.ReliteRoot;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;

import java.util.List;

public class PreloadManager implements Player.EventListener {

    private final SimpleExoPlayer player;
    private final List<ReliteRoot.VideoItem> videoUrls;

    public PreloadManager(SimpleExoPlayer player, List<ReliteRoot.VideoItem> videoUrls) {
        this.player = player;
        this.videoUrls = videoUrls;
        player.addListener(this); // Register the listener
    }

    public void preloadNextVideo(int position) {
        // Preload the next video in the list
        int nextPosition = position + 1;
        if (nextPosition < videoUrls.size()) {
            MediaItem mediaItem = MediaItem.fromUri(videoUrls.get(nextPosition).getVideo());
            player.setMediaItem(mediaItem);
            player.prepare();
        }
    }

    // Implement Player.EventListener methods to handle player events
    @Override
    public void onPlaybackStateChanged(int state) {
        // Handle playback state changes
        if (state == Player.STATE_READY && player.getPlayWhenReady()) {
            // Video is ready for playback, trigger preload for the next video
            preloadNextVideo(player.getCurrentWindowIndex());
        }
    }

    // Other event listener methods...

    // Don't forget to unregister the listener when it's no longer needed
    public void release() {
        player.removeListener(this);
    }
}
