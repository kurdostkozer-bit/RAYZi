package com.example.rayzi.pk;

import android.os.Bundle;
import android.view.SurfaceView;

import androidx.annotation.Nullable;

import com.example.rayzi.MainApplication;
import com.example.rayzi.activity.BaseActivity;
import com.example.rayzi.agora.rtc.Constants;
import com.example.rayzi.agora.rtc.EngineConfig;
import com.example.rayzi.agora.stats.StatsManager;

import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;

public class PkBaseActivity extends BaseActivity {


    protected MainApplication application() {
        return (MainApplication) getApplication();
    }

    protected StatsManager statsManager() {
        return application().statsManager();
    }

    protected RtcEngine rtcEngine() {
        return application().rtcEngine();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    protected EngineConfig config() {
        return application().engineConfig();
    }

    protected SurfaceView prepareRtcVideo(int uid, boolean local) {
        // Render local/remote video on a SurfaceView

        SurfaceView surface = RtcEngine.CreateRendererView(getApplicationContext());
        if (local) {
            rtcEngine().setupLocalVideo(
                    new VideoCanvas(
                            surface,
                            VideoCanvas.RENDER_MODE_HIDDEN,
                            0,
                            Constants.VIDEO_MIRROR_MODES[config().getMirrorLocalIndex()]
                    )
            );
        } else {
            rtcEngine().setupRemoteVideo(
                    new VideoCanvas(
                            surface,
                            VideoCanvas.RENDER_MODE_HIDDEN,
                            uid,
                            Constants.VIDEO_MIRROR_MODES[config().getMirrorRemoteIndex()]
                    )
            );
        }
        return surface;
    }

}
