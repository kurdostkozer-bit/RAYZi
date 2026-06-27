package com.example.rayzi.videocall;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.example.rayzi.R;
import com.example.rayzi.activity.BaseActivity;
import com.example.rayzi.databinding.ActivityCallRequestBinding;
import com.example.rayzi.modelclass.CallRequestRoot;
import com.example.rayzi.modelclass.GuestProfileRoot;
import com.example.rayzi.retrofit.Const;
import com.example.rayzi.retrofit.RetrofitBuilder;
import com.example.rayzi.socket.CallHandler;
import com.example.rayzi.socket.MySocketManager;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import io.agora.rtc.IRtcEngineEventHandler;
import jp.wasabeef.glide.transformations.BlurTransformation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CallRequestActivity extends VideoCallBaseActivity {

    private static final String TAG = "callReqAct";
    ActivityCallRequestBinding binding;

    private GuestProfileRoot.User guestUser;

    Handler handler = new Handler();
    Runnable runnable = () -> {
        Toast.makeText(CallRequestActivity.this, guestUser.getName() + getString(R.string.is_busy_with_someone_else) , Toast.LENGTH_SHORT).show();
        finish();
    };

    private boolean isGone = false;
    CallHandler callHandler = new CallHandler() {
        @Override
        public void onCallRequest(Object[] args) {

        }

        @Override
        public void onCallConform(Object[] args) {
            runOnUiThread(() -> {
                if (args != null) {
                    Log.d(TAG, "callConfirmLister: " + args[0].toString());
                    try {
                        JSONObject jsonObject = new JSONObject(args[0].toString());
                        String userId1 = jsonObject.getString(Const.USERID1);
                        String userId2 = jsonObject.getString(Const.USERID2);
                        boolean isConfirm = jsonObject.getBoolean(Const.ISCONFIRM);
                        if (userId1.equals(guestUser.getUserId())) {
                            if (userId2.equals(sessionManager.getUser().getId())) {
                                if (isConfirm) {
                                    binding.tvStatus.setText( getString(R.string.ringing));
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        @Override
        public void onCallAnswer(Object[] args) {
            runOnUiThread(() -> {
                if (args != null) {
                    Log.d(TAG, "callAnswerLister: " + args[0].toString());
                    try {
                        JSONObject jsonObject = new JSONObject(args[0].toString());  // required feild  token channel
                        String userId1 = jsonObject.getString(Const.USERID1);
                        String userId2 = jsonObject.getString(Const.USERID2);
                        String token = jsonObject.getString(Const.TOKEN);
                        String callRoomId = jsonObject.getString(Const.CALL_ROOM_ID);
                        String channel = jsonObject.getString(Const.CHANNEL);
                        Log.d(TAG, "guest id : " + guestUser.getUserId());
                        Log.d(TAG, "local  id : " + sessionManager.getUser().getId());
                        boolean isAccept = jsonObject.getBoolean(Const.ISACCEPT);
                        if (userId1.equals(guestUser.getUserId())) {
                            if (userId2.equals(sessionManager.getUser().getId())) {
                                if (isAccept) {
                                    if (!isGone) {
                                        isGone = true;
                                        Intent intent = new Intent(CallRequestActivity.this, VideoCallActivity.class);
                                        intent.putExtra(Const.USERID, userId1);
                                        intent.putExtra(Const.TOKEN, token);
                                        intent.putExtra(Const.CHANNEL, channel);
                                        intent.putExtra(Const.CALL_ROOM_ID, callRoomId);
                                        intent.putExtra(Const.CALL_BY_ME, true);
                                        intent.putExtra("type", getIntent().getStringExtra("type"));
                                        intent.putExtra("random",getIntent().getBooleanExtra("random",false));
                                        startActivity(intent);
                                    }
                                } else {
                                    Toast.makeText(CallRequestActivity.this, R.string.call_declined , Toast.LENGTH_SHORT).show();
                                }
                                finish();
                                BaseActivity.STATUS_VIDEO_CALL = true;
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });
        }

        @Override
        public void onCallRecieve(Object[] args) {

        }

        @Override
        public void onCallCancel(Object[] args) {

        }

    };
    private String callRoomId;
    //    private Socket callRoomSocket;
    private String agoraToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_call_request);
        BaseActivity.STATUS_VIDEO_CALL = true;

        MySocketManager.getInstance().addCallHandler(callHandler);

        Intent intent = getIntent();
        String userData = intent.getStringExtra(Const.USER);
        if (userData != null && !userData.isEmpty()) {
            guestUser = new Gson().fromJson(userData, GuestProfileRoot.User.class);
            Log.d(TAG, "onCreate: guest user  " + guestUser.toString());

            if (!isFinishing()) {
                binding.imgUser.setUserImage(guestUser.getImage(), guestUser.getAvatarFrameImage(), 30);
                MultiTransformation<Bitmap> transformations = new MultiTransformation<>(
                        new BlurTransformation(50),
                        new CenterCrop()
                );
                Glide.with(this).load(guestUser.getImage())
                        .transform(transformations).into(binding.backBlurImage);
            }
            binding.tvName.setText(guestUser.getName());

            makeCallRequest();
            handler.postDelayed(runnable, 30000);
        }
        binding.btnDecline.setOnClickListener(v -> onBackPressed());
    }

    private void makeCallRequest() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("callerUserId", sessionManager.getUser().getId());
        jsonObject.addProperty("receiverUserId", guestUser.getUserId());
        jsonObject.addProperty("channel", guestUser.getUserId());
        jsonObject.addProperty("callType", getIntent().getStringExtra("type"));
        Call<CallRequestRoot> call = RetrofitBuilder.create().makeCallRequest(jsonObject);
        call.enqueue(new Callback<CallRequestRoot>() {
            @Override
            public void onResponse(Call<CallRequestRoot> call, Response<CallRequestRoot> response) {
                if (response.code() == 200) {
                    if (response.body().isStatus()) {
                        callRoomId = response.body().getCallId();
                        agoraToken = response.body().getToken();
                        Log.d(TAG, "onResponse: " + callRoomId);
                        initMain();
                    }
                }
            }

            @Override
            public void onFailure(Call<CallRequestRoot> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void initMain() {
        try {
            JSONObject callReqObject = new JSONObject();
            callReqObject.put(Const.USERID1, guestUser.getUserId());
            callReqObject.put(Const.USERID2, sessionManager.getUser().getId());
            callReqObject.put(Const.USER2_NAME, sessionManager.getUser().getName());
            callReqObject.put(Const.USER2_IMAGE, sessionManager.getUser().getImage());
            callReqObject.put(Const.USER2_IMAGE_FRAME_IMAGE, sessionManager.getUser().getAvatarFrameImage());
            callReqObject.put(Const.CALL_ROOM_ID, callRoomId);
            callReqObject.put(Const.TOKEN, agoraToken);

            Log.d(TAG, "initMain:call req send  " + callReqObject);

            MySocketManager.getInstance().getSocket().emit(Const.EVENT_CALL_REQUEST, callReqObject);
            binding.tvStatus.setText("Calling...");

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        MySocketManager.getInstance().removeCallHandler(callHandler);
        handler.removeCallbacks(runnable);
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        try {
            JSONObject callReqObject = new JSONObject();
            callReqObject.put(Const.USERID1, guestUser.getUserId());
            callReqObject.put(Const.USERID2, sessionManager.getUser().getId());
            callReqObject.put(Const.USER2_NAME, sessionManager.getUser().getName());
            callReqObject.put(Const.USER2_IMAGE, sessionManager.getUser().getImage());
            callReqObject.put(Const.CALL_ROOM_ID, callRoomId);

            Log.d(TAG, "initMain:call req send  " + callReqObject);

            MySocketManager.getInstance().getSocket().emit(Const.EVENT_CALL_CANCEL, callReqObject);
            handler.removeCallbacks(runnable);
            handler.removeCallbacksAndMessages(null);

            //  todo ios ma pending

        } catch (JSONException e) {
            e.printStackTrace();
        }

        BaseActivity.STATUS_VIDEO_CALL = false;
        finish();
    }

    @Override
    public void onFirstRemoteVideoDecoded(int uid, int width, int height, int elapsed) {

    }

    @Override
    public void onLeaveChannel(IRtcEngineEventHandler.RtcStats stats) {

    }

    @Override
    public void onJoinChannelSuccess(String channel, int uid, int elapsed) {

    }

    @Override
    public void onUserOffline(int uid, int reason) {

    }

    @Override
    public void onUserJoined(int uid, int elapsed) {

    }

    @Override
    public void onLastmileQuality(int quality) {

    }

    @Override
    public void onErr(int err) {

    }

    @Override
    public void onConnectionLost() {

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