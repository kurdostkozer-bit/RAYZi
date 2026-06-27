package com.example.rayzi.utils;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.rayzi.HostAPICall;
import com.example.rayzi.MainApplication;
import com.example.rayzi.R;
import com.example.rayzi.SessionManager;
import com.example.rayzi.audioLive.HostLiveAudioActivity;
import com.example.rayzi.audioLive.WatchAudioLiveActivity;
import com.example.rayzi.retrofit.Const;
import com.example.rayzi.socket.MySocketManager;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import io.agora.rtc.Constants;

public class FloatingButtonService extends Service {

    private ViewGroup floatView;
    private ImageView maximizeBtn;
    private ImageView ivUserImage;
    SessionManager sessionManager;
    HostAPICall hostAPICall;
    private WindowManager windowManager;
    private WindowManager.LayoutParams floatWindowLayoutParam;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onCreate() {
        super.onCreate();

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        floatView = (ViewGroup) inflater.inflate(R.layout.floating_button_layout, null);

        int LAYOUT_TYPE;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LAYOUT_TYPE = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            LAYOUT_TYPE = WindowManager.LayoutParams.TYPE_PHONE;
        }

        floatWindowLayoutParam = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                LAYOUT_TYPE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );

        floatWindowLayoutParam.gravity = Gravity.TOP | Gravity.START;
        floatWindowLayoutParam.x = 0;
        floatWindowLayoutParam.y = 0;

        windowManager.addView(floatView, floatWindowLayoutParam);

        maximizeBtn = floatView.findViewById(R.id.ivClose);
        ivUserImage = floatView.findViewById(R.id.ivUserImage);

        maximizeBtn.setOnClickListener(v -> {
            stopSelf();
            if (sessionManager.getIsAudioRoomBackground()) {
//                HostLiveAudioActivity.hostPosition = -1;
//                sessionManager.setIsAudioRoomBackground(false);
                sessionManager.setIsAudioRoomExit(false);
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("userId", sessionManager.getUser().getId());
                jsonObject.addProperty("liveUserMongoId", sessionManager.getLiveUserForBackground().getId());
                jsonObject.addProperty("liveStreamingId", sessionManager.getLiveUserForBackground().getLiveStreamingId());
                ((MainApplication) getApplication()).rtcEngine().setClientRole(Constants.CLIENT_ROLE_AUDIENCE);
                MySocketManager.getInstance().getSocket().emit(Const.EVENT_LESS_PARTICIPATED, jsonObject);
                sessionManager.saveBooleanValue("isHostKeep", false);
                JSONObject jsonObject1 = new JSONObject();
                try {
                    jsonObject1.put("liveUserId", sessionManager.getLiveUserForBackground().getId());
                    jsonObject1.put("liveStreamingId", sessionManager.getLiveUserForBackground().getLiveStreamingId());
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                MySocketManager.getInstance().getSocket().emit("audioLiveHostRemove", jsonObject1);
            }

            ((MainApplication) getApplication()).rtcEngine().leaveChannel();

            if (sessionManager.getIsUserBackgroundLive()) {
                sessionManager.setIsUserBackgroundLive(false);
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("userId", sessionManager.getUser().getId());
                jsonObject.addProperty("liveUserMongoId", sessionManager.getUserAudioBgModel().getId());
                jsonObject.addProperty("liveStreamingId", sessionManager.getUserAudioBgModel().getLiveStreamingId());
                MySocketManager.getInstance().getSocket().emit(Const.EVENT_LESS_PARTICIPATED, jsonObject);
                sessionManager.saveBooleanValue("isUserKeep", false);
                ((MainApplication) getApplication()).rtcEngine().leaveChannel();
                JSONObject jsonObject1 = new JSONObject();
                try {
                    jsonObject1.put("liveStreamingId", sessionManager.getUserAudioBgModel().getLiveStreamingId());
                    jsonObject1.put("liveUserMongoId", sessionManager.getUserAudioBgModel().getId());
                    jsonObject1.put("userId", sessionManager.getUser().getId());
                    jsonObject1.put("isVIP", sessionManager.getUser().isIsVIP());
                    jsonObject1.put("image", sessionManager.getUser().getImage());
                    jsonObject1.put("name", sessionManager.getUser().getName());
                    jsonObject1.put("gender", sessionManager.getUser().getGender());
                    jsonObject1.put("country", sessionManager.getUser().getCountry());
                    jsonObject1.put("userName", sessionManager.getUser().getName());
                    jsonObject1.put("avatarFrame", sessionManager.getUser().getAvatarFrameImage());
                    jsonObject1.put("entrySvga", sessionManager.getUser().getSvgaImage());
                    MySocketManager.getInstance().getSocket().emit(Const.EVENT_LESSVIEW, jsonObject1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            windowManager.removeView(floatView);
//            sessionManager.setIsAudioRoomBackground(false);
            sessionManager.setIsAudioRoomExit(true);

            if (isMyServiceRunning()) {
                stopService(new Intent(FloatingButtonService.this, FloatingButtonService.class));
            }

        });

        ivUserImage.setOnTouchListener(new View.OnTouchListener() {
            private WindowManager.LayoutParams updatedParameters = floatWindowLayoutParam;
            private int x, y;
            private float touchX, touchY;
            private long touchStartTime;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        x = updatedParameters.x;
                        y = updatedParameters.y;
                        touchX = event.getRawX();
                        touchY = event.getRawY();
                        touchStartTime = System.currentTimeMillis();
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        updatedParameters.x = (int) (x + (event.getRawX() - touchX));
                        updatedParameters.y = (int) (y + (event.getRawY() - touchY));
                        windowManager.updateViewLayout(floatView, updatedParameters);
                        return true;
                    case MotionEvent.ACTION_UP:
                        long touchDuration = System.currentTimeMillis() - touchStartTime;
                        if (touchDuration < 200 && Math.abs(event.getRawX() - touchX) < 10 && Math.abs(event.getRawY() - touchY) < 10) {
                            v.performClick();
                        }
                        return true;
                }
                return false;
            }
        });

        ivUserImage.setOnClickListener(view -> {
            if (sessionManager.getIsUserBackgroundLive()) {
                startActivity(new Intent(FloatingButtonService.this, WatchAudioLiveActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        .putExtra(Const.DATA, new Gson().toJson(sessionManager.getUserAudioBgModel())));
            } else {
                Intent intent = new Intent(FloatingButtonService.this, HostLiveAudioActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(Const.DATA, new Gson().toJson(sessionManager.getLiveUserForBackground()));
                intent.putExtra(Const.PRIVACY, "Public");
                startActivity(intent);
            }
        });

    }

    public boolean isMyServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (FloatingButtonService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sessionManager = new SessionManager(FloatingButtonService.this);
        hostAPICall = new HostAPICall(FloatingButtonService.this,"audio");

        if (sessionManager.getUser().isHost() && sessionManager.getIsAudioRoomBackground()){
            hostAPICall.startApiCallLoop();
        }

        Glide.with(FloatingButtonService.this).load(intent.getStringExtra("image")).circleCrop().into(ivUserImage);
        RotateAnimation rotate = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(5000);
        rotate.setInterpolator(new LinearInterpolator());
        rotate.setRepeatMode(Animation.RESTART);
        rotate.setRepeatCount(Animation.INFINITE);
        ivUserImage.startAnimation(rotate);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (floatView != null) windowManager.removeView(floatView);
        if (sessionManager.getUser().isHost() && sessionManager.getIsAudioRoomBackground()){
            hostAPICall.stopApiCallLoop();
        }
    }
}
