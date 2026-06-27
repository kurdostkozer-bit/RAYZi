package com.example.rayzi.videocall;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.example.rayzi.activity.BaseActivity;
import com.example.rayzi.agora.rtc.EventHandler;

import io.agora.rtc.RtcEngine;

public abstract class VideoCallBaseActivity extends BaseActivity implements EventHandler {

    private static final String TAG = "callBaseact";

    @Override
    protected void onStart() {
        super.onStart();
        BaseActivity.STATUS_VIDEO_CALL = true;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BaseActivity.STATUS_VIDEO_CALL = true;
        registerRtcEventHandler(this);
    }

    protected RtcEngine rtcEngine() {
        return application().rtcEngine();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeRtcEventHandler(this);

    }
}
