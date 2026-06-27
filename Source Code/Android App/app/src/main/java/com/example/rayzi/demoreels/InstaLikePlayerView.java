package com.example.rayzi.demoreels;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.example.rayzi.MainApplication;
import com.example.rayzi.R;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.cache.CacheDataSource;
import com.google.android.exoplayer2.util.Util;

public class InstaLikePlayerView extends FrameLayout {
    private static final String TAG = "InstaLikePlayerView";
    public static MutableLiveData<Boolean> isMuted = new MutableLiveData<>(false);
    private View videoSurfaceView;
    private ExoPlayer player;
    private boolean isTouching = false;
    private Long lastPos = 0L;
    private Uri videoUri;

    public InstaLikePlayerView(Context context) {
        this(context, null);
    }

    public InstaLikePlayerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public InstaLikePlayerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (isInEditMode()) {
            videoSurfaceView = null;
        } else {
            int playerLayoutId = R.layout.exosimpleview;
            LayoutInflater.from(context).inflate(playerLayoutId, this);
            setDescendantFocusability(FOCUS_AFTER_DESCENDANTS);
            videoSurfaceView = findViewById(R.id.surfaceView);
            initPlayer();
        }
    }

    public ExoPlayer getPlayer() {
        return player;
    }

    private void setPlayer(ExoPlayer player) {
        if (this.player == player) {
            return;
        }
        ExoPlayer oldPlayer = this.player;

        if (oldPlayer != null) {
            oldPlayer.clearVideoSurfaceView((SurfaceView) videoSurfaceView);
        }
        this.player = player;
        if (player != null) {
            Log.d(TAG, "setPlayer: " + videoSurfaceView);
            player.setVideoSurfaceView((SurfaceView) videoSurfaceView);
        }
    }


    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        Log.d(TAG, "setVisibility: " + visibility);
        videoSurfaceView.setVisibility(visibility);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (player != null && player.isPlayingAd()) {
            return super.dispatchKeyEvent(event);
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (player == null) {
            return false;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isTouching = true;
                return true;
            case MotionEvent.ACTION_UP:
                if (isTouching) {
                    isTouching = false;
                    performClick();
                    return true;
                }
                return false;
            default:
                return false;
        }
    }

    @Override
    public boolean performClick() {
        super.performClick();
        return false;
    }

    @Override
    public boolean onTrackballEvent(MotionEvent ev) {
        return false;
    }

    private void initPlayer() {
        reset();

        // Setup player + Adding Cache Directory
//        player = new ExoPlayer.Builder(getContext()).build();


        RenderersFactory renderersFactory = new DefaultRenderersFactory(getContext(), DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER);
        player = new SimpleExoPlayer.Builder(getContext(), renderersFactory).build();

        player.setRepeatMode(Player.REPEAT_MODE_ALL);
        player.setVolume(1f);

        player.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int state) {
                Player.Listener.super.onPlaybackStateChanged(state);
                System.out.println("playbackState== " + state);
                if (state == Player.STATE_READY) {
                    setAlpha(1f);
                }
            }
        });

        setPlayer(player);
    }

    public void startPlaying() {
        if (videoUri == null) return;

        MediaItem mediaItem = MediaItem.fromUri(videoUri);
        DataSource.Factory cacheDataSourceFactory = new CacheDataSource.Factory()
                .setCache(MainApplication.simpleCache)
                .setUpstreamDataSourceFactory(new DefaultHttpDataSource.Factory()
                        .setUserAgent(Util.getUserAgent(getContext(), "ExoPlayer")));

        ProgressiveMediaSource mediaSource = new ProgressiveMediaSource.Factory(cacheDataSourceFactory)
                .createMediaSource(mediaItem);
        player.setMediaSource(mediaSource);
        player.seekTo(lastPos);
        player.prepare();
        player.play();
    }

    public void removePlayer() {
        if (getPlayer() != null) {
            getPlayer().setPlayWhenReady(false);
            lastPos = getPlayer().getCurrentPosition();
            reset();
            getPlayer().stop();
        }
    }

    public void reset() {
        setAlpha(0f);
    }

    public void setVideoUri(Uri uri) {
        this.videoUri = uri;
    }
}
