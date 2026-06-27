package com.example.rayzi.videocall;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.example.rayzi.R;
import com.example.rayzi.activity.BaseActivity;
import com.example.rayzi.databinding.ActivityVideoCallBinding;
import com.example.rayzi.modelclass.UserRoot;
import com.example.rayzi.retrofit.Const;
import com.example.rayzi.socket.CallHandler;
import com.example.rayzi.socket.MySocketManager;
import com.example.rayzi.viewModel.VideoCallViewModel;
import com.example.rayzi.viewModel.ViewModelFactory;
import com.google.gson.Gson;

import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;
import io.agora.rtc.video.VideoEncoderConfiguration;

public class VideoCallActivity extends VideoCallBaseActivity {

    private static final String TAG = "videocallact";
    private static final int PERMISSION_REQ_ID = 22;
    private static final String[] REQUESTED_PERMISSIONS = {Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA};
    private static final org.apache.commons.logging.Log log = LogFactory.getLog(VideoCallActivity.class);
    ActivityVideoCallBinding binding;
    VideoCallViewModel viewModel;
    private boolean mCallEnd;
    private boolean mMuted;

    private FrameLayout mLocalContainer;
    private RelativeLayout mRemoteContainer;
    private VideoCanvas mLocalVideo;
    private VideoCanvas mRemoteVideo;
    private boolean isVideoDecoded = false;
    private boolean isCallEnded = false;
    int coin;
    String otherUserId = "";
    private String token;
    private String channel;
    private boolean callByMe;
    private boolean random;
    private String callRoomId;
    Handler timerHandler = new Handler(Objects.requireNonNull(Looper.myLooper()));
    private int seconds = 0;
    Runnable timerRunnable = new Runnable() {
        @SuppressLint("SetTextI18n")
        @Override
        public void run() {
            seconds++;
            if (callByMe && seconds % 10 == 0) {
                reduceCoin();
            }
            int p1 = seconds % 60;
            int p2 = seconds / 60;
            int p3 = p2 % 60;
            p2 = p2 / 60;

            String sec;
            String hour;
            String min;
            if (p1 < 10) {
                sec = "0" + p1;
            } else {
                sec = String.valueOf(p1);
            }
            if (p2 < 10) {
                hour = "0" + p2;
            } else {
                hour = String.valueOf(p2);
            }
            if (p3 < 10) {
                min = "0" + p3;
            } else {
                min = String.valueOf(p3);
            }
            binding.tvtimer.setText(hour + ":" + min + ":" + sec);

            timerHandler.postDelayed(this, 1000);
        }
    };
    CallHandler callHandler = new CallHandler() {
        @Override
        public void onCallRequest(Object[] args) {

        }

        @Override
        public void onCallConform(Object[] args) {

        }

        @Override
        public void onCallAnswer(Object[] args) {

        }

        @Override
        public void onCallRecieve(Object[] args) {
            runOnUiThread(() -> {
                if (args != null) {
                    Log.d(TAG, "call: callReceive " + args[0].toString());
                    try {
                        JSONObject jsonObject = new JSONObject(args[0].toString());
                        UserRoot.User user = new Gson().fromJson(jsonObject.toString(), UserRoot.User.class);
                        sessionManager.saveUser(user);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

        }

        @Override
        public void onCallCancel(Object[] args) {

        }

    };

    private void reduceCoin() {
        if (!sessionManager.getUser().isIsVIP()) {
            if (sessionManager.getUser().getLevel().getAccessibleFunction().isFreeCall()) {
                Log.d(TAG, "reduceCoin: free call");
                return;
            }
        }

        UserRoot.User user = sessionManager.getUser();
        long usercoin = user.getDiamond();
        if (usercoin >= coin) {
            long finalCoin = usercoin - coin;
            if (finalCoin >= 0 ) {
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("callId", callRoomId);
                    jsonObject.put("coin", coin);
                    Log.d(TAG, "reduceCoin: callReceive event " + jsonObject);
                    Log.d(TAG, "reduceCoin: callReceive event " + coin);
                    MySocketManager.getInstance().getSocket().emit(Const.EVENT_CALL_RECIVE, jsonObject);
                    user.setDiamond(finalCoin);
                    sessionManager.saveUser(user);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else {
                Toast.makeText(this,  getString(R.string.insufficient_coins), Toast.LENGTH_SHORT).show();
                endCall();
            }
        } else {
            Toast.makeText(this,  getString(R.string.insufficient_coins), Toast.LENGTH_SHORT).show();
            endCall();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_video_call);

        MySocketManager.getInstance().addCallHandler(callHandler);

        viewModel = ViewModelProviders.of(this, new ViewModelFactory(new VideoCallViewModel()).createFor()).get(VideoCallViewModel.class);

        BaseActivity.STATUS_VIDEO_CALL = true;

        Intent intent = getIntent();
        otherUserId = intent.getStringExtra(Const.USERID);
        token = intent.getStringExtra(Const.TOKEN);
        callRoomId = intent.getStringExtra(Const.CALL_ROOM_ID);
        channel = intent.getStringExtra(Const.CHANNEL);
        callByMe = intent.getBooleanExtra(Const.CALL_BY_ME, false);
        random = intent.getBooleanExtra("random",false);
        Log.d(TAG, "onCreate: random=" + random);
        Log.d(TAG, "onCreate: type=" + getIntent().getStringExtra("type"));

        if (random){
            if (getIntent().getStringExtra("type") != null) {
                if (getIntent().getStringExtra("type").equalsIgnoreCase("Male")) {
                    coin = sessionManager.getSetting().getMaleRandomCallRate();
                    Log.d(TAG, "onCreate: male=====" + coin);
                } else if (getIntent().getStringExtra("type").equalsIgnoreCase("Female")) {
                    coin = sessionManager.getSetting().getFemaleRandomCallRate();
                    Log.d(TAG, "onCreate: female=====" + coin);
                } else if (getIntent().getStringExtra("type").equalsIgnoreCase("Both")) {
                    coin = sessionManager.getSetting().getBothRandomCallRate();
                    Log.d(TAG, "onCreate: both=====" + coin);
                } else {
                    coin = 0;
                }
            }
        }else {
            if (getIntent().getStringExtra("type") != null) {
                if (getIntent().getStringExtra("type").equalsIgnoreCase("Male")) {
                    coin = sessionManager.getSetting().getMaleCallCharge();
                    Log.d(TAG, "onCreate: male=====" + coin);
                } else if (getIntent().getStringExtra("type").equalsIgnoreCase("Female")) {
                    coin = sessionManager.getSetting().getFemaleCallCharge();
                    Log.d(TAG, "onCreate: female=====" + coin);
                } else {
                    coin = 0;
                }
            }
        }

        if (otherUserId != null && !otherUserId.isEmpty()) {
            if (token != null && !token.isEmpty()) {
                initUI();
                initListener();
                if (checkSelfPermission(REQUESTED_PERMISSIONS[0], PERMISSION_REQ_ID) && checkSelfPermission(REQUESTED_PERMISSIONS[1], PERMISSION_REQ_ID)) {
                    initEngineAndJoinChannel();
                }

                timerHandler.postDelayed(timerRunnable, 1000);
                if (callByMe) {
                    reduceCoin();
                }
            }
        }

    }

    private void initListener() {
        binding.btnDecline.setOnClickListener(v -> onBackPressed());
        binding.localVideoViewContainer.setOnClickListener(v -> {
            switchView(mLocalVideo);
            switchView(mRemoteVideo);
        });
        binding.btnMute.setOnClickListener(v -> {
            mMuted = !mMuted;
            rtcEngine().muteLocalAudioStream(mMuted);
            int res = mMuted ? R.drawable.btn_mute_pressed : R.drawable.btnunmute;
            binding.btnMute.setImageResource(res);
        });
        binding.btnSwitchCamera.setOnClickListener(v -> rtcEngine().switchCamera());
        binding.btnDecline.setOnClickListener(v -> {
            endCall();
            v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_PRESS);
        });
    }

    private void setupRemoteVideo(int uid) {
        ViewGroup parent = mRemoteContainer;
        if (parent.indexOfChild(mLocalVideo.view) > -1) {
            parent = mLocalContainer;
        }
        if (mRemoteVideo != null) {
            return;
        }
        SurfaceView view = rtcEngine().CreateRendererView(getBaseContext());
        view.setZOrderMediaOverlay(parent == mLocalContainer);
        parent.removeAllViews();
        parent.addView(view);
        mRemoteVideo = new VideoCanvas(view, VideoCanvas.RENDER_MODE_HIDDEN, uid);
        rtcEngine().setupRemoteVideo(mRemoteVideo);
    }

    private void onRemoteUserLeft(int uid) {
        Log.d(TAG, "onRemoteUserLeft: ");
        if (mRemoteVideo != null && mRemoteVideo.uid == uid) {
            removeFromParent(mRemoteVideo);
            mRemoteVideo = null;
            endCall();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        endCall();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initUI() {
        mLocalContainer = binding.localVideoViewContainer;
        mRemoteContainer = binding.remoteVideoViewContainer;
    }


    private boolean checkSelfPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, REQUESTED_PERMISSIONS, requestCode);
            return false;
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQ_ID) {
            if (grantResults.length < 2 || grantResults[0] != PackageManager.PERMISSION_GRANTED || grantResults[1] != PackageManager.PERMISSION_GRANTED) {
                showLongToast( getString(R.string.need_permissions) + Manifest.permission.RECORD_AUDIO + "/" + Manifest.permission.CAMERA);
                finish();
                return;
            }
            initEngineAndJoinChannel();
        }
    }

    private void showLongToast(String msg) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void initEngineAndJoinChannel() {
        joinChannel();
        setupVideoConfig();
        setupLocalVideo();
    }


    private void setupVideoConfig() {
        rtcEngine().enableVideo();
        rtcEngine().setVideoEncoderConfiguration(new VideoEncoderConfiguration(VideoEncoderConfiguration.VD_360x360, VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15, VideoEncoderConfiguration.STANDARD_BITRATE, VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT));
    }

    private void setupLocalVideo() {
        SurfaceView view = RtcEngine.CreateRendererView(getBaseContext());
        view.setZOrderMediaOverlay(true);
        mLocalContainer.addView(view);
        mLocalVideo = new VideoCanvas(view, VideoCanvas.RENDER_MODE_HIDDEN, 0);
        rtcEngine().setupLocalVideo(mLocalVideo);
    }

    private void joinChannel() {
        rtcEngine().joinChannel(token, channel, "Extra Optional Data", 0);
    }

    @Override
    protected void onDestroy() {
        if (!isCallEnded) {
            leaveChannel();
        }
        BaseActivity.STATUS_VIDEO_CALL = false;
        Log.d(TAG, "reduseCoin: calldisconnect event ");
        MySocketManager.getInstance().getSocket().emit(Const.EVENT_CALL_DISCONNECT, callRoomId);
        MySocketManager.getInstance().removeCallHandler(callHandler);

        timerHandler.removeCallbacks(timerRunnable);
        super.onDestroy();
    }

    private void leaveChannel() {
        if (rtcEngine() != null) {
            rtcEngine().leaveChannel();
            Log.d(TAG, "leaveChannel: =========");
        }
    }


    private void endCall() {
        if (isCallEnded) return;

        isCallEnded = true;
        mCallEnd = true;
        removeFromParent(mLocalVideo);
        mLocalVideo = null;
        removeFromParent(mRemoteVideo);
        mRemoteVideo = null;
        leaveChannel();
        finish();
    }

    private ViewGroup removeFromParent(VideoCanvas canvas) {
        if (canvas != null) {
            ViewParent parent = canvas.view.getParent();
            if (parent != null) {
                ViewGroup group = (ViewGroup) parent;
                group.removeView(canvas.view);
                return group;
            }
        }
        return null;
    }

    private void switchView(VideoCanvas canvas) {
        ViewGroup parent = removeFromParent(canvas);
        if (parent == mLocalContainer) {
            if (canvas.view instanceof SurfaceView) {
                ((SurfaceView) canvas.view).setZOrderMediaOverlay(false);
            }
            mRemoteContainer.addView(canvas.view);
        } else if (parent == mRemoteContainer) {
            if (canvas.view instanceof SurfaceView) {
                ((SurfaceView) canvas.view).setZOrderMediaOverlay(true);
            }
            mLocalContainer.addView(canvas.view);
        }
    }


    @Override
    public void onFirstRemoteVideoDecoded(int uid, int width, int height, int elapsed) {
        runOnUiThread(() -> {
            isVideoDecoded = true;
            Log.d(TAG, "sssss=- run: vide decode");
            setupRemoteVideo(uid);
        });
    }

    @Override
    public void onLeaveChannel(IRtcEngineEventHandler.RtcStats stats) {

    }

    @Override
    public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
        runOnUiThread(() -> new Handler().postDelayed(() -> {
            if (isVideoDecoded) {
                Log.d(TAG, "sssss=- run: yreeeeeeehhhhh  video decoded");
            } else {
                Toast.makeText(VideoCallActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                endCall();
            }
        }, 5000)); //todo
    }

    @Override
    public void onUserOffline(int uid, int reason) {
        runOnUiThread(() -> {
            onRemoteUserLeft(uid);
            endCall();
        });
    }

    @Override
    public void onUserJoined(int uid, int elapsed) {
        runOnUiThread(() -> setupRemoteVideo(uid));
    }

    @Override
    public void onLastmileQuality(int quality) {

    }

    @Override
    public void onErr(int err) {
        Log.d(TAG, "onError: " + err);
    }

    @Override
    public void onConnectionLost() {
        runOnUiThread(() -> {
            Toast.makeText(this, "Connection lost. Reconnecting...", Toast.LENGTH_SHORT).show();
            joinChannel();
        });
    }

    @Override
    public void onVideoStopped() {

    }

    @Override
    public void onLastmileProbeResult(IRtcEngineEventHandler.LastmileProbeResult result) {

    }

    @Override
    public void onLocalVideoStats(IRtcEngineEventHandler.LocalVideoStats stats) {

    }

    @Override
    public void onRtcStats(IRtcEngineEventHandler.RtcStats stats) {

    }

    @Override
    public void onNetworkQuality(int uid, int txQuality, int rxQuality) {
        if (txQuality > 3 || rxQuality > 3) { // Poor network
            rtcEngine().setVideoEncoderConfiguration(new VideoEncoderConfiguration(
                    VideoEncoderConfiguration.VD_180x180, // Lower resolution
                    VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_10, // Lower frame rate
                    VideoEncoderConfiguration.STANDARD_BITRATE / 2, // Lower bitrate
                    VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_ADAPTIVE
            ));
        } else { // Good network
            rtcEngine().setVideoEncoderConfiguration(new VideoEncoderConfiguration(
                    VideoEncoderConfiguration.VD_360x360,
                    VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15,
                    VideoEncoderConfiguration.STANDARD_BITRATE,
                    VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_ADAPTIVE
            ));
        }
    }

    @Override
    public void onRemoteVideoStats(IRtcEngineEventHandler.RemoteVideoStats stats) {

    }

    @Override
    public void onRemoteAudioStats(IRtcEngineEventHandler.RemoteAudioStats stats) {

    }

    @Override
    public void onChannelMediaRelayStateChanged(int state, int code) {

    }

    @Override
    public void onChannelMediaRelayEvent(int code) {

    }

    @Override
    public void onFirstLocalAudioFramePublished(int elapsed) {

    }

    @Override
    public void onFirstRemoteAudioFrame(int uid, int elapsed) {

    }

    @Override
    public void onUserMuteAudio(int uid, boolean muted) {

    }

    @Override
    public void onAudioVolumeIndication(IRtcEngineEventHandler.AudioVolumeInfo[] speakers, int totalVolume) {

    }

    @Override
    public void onActiveSpeaker(int uid) {

    }

    @Override
    public void onAudioMixingStateChanged(int state, int reason) {

    }

    @Override
    public void onTokenPrivilegeWillExpire(String token) {

    }

    @Override
    public void onRequestToken() {

    }

    @Override
    public void onAudioRouteChanged(int routing) {

    }
}