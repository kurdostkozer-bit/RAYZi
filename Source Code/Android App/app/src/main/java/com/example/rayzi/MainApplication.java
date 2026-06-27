package com.example.rayzi;

import android.app.ActivityManager;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ProcessLifecycleOwner;
import androidx.multidex.MultiDex;


import com.bumptech.glide.request.RequestOptions;
import com.example.rayzi.agora.rtc.AgoraEventHandler;
import com.example.rayzi.agora.rtc.Constants;
import com.example.rayzi.agora.rtc.EngineConfig;
import com.example.rayzi.agora.rtc.EventHandler;
import com.example.rayzi.agora.stats.StatsManager;
import com.example.rayzi.providers.ExoPlayerProvider;
import com.example.rayzi.providers.JacksonProvider;
import com.example.rayzi.providers.RoomProvider;
import com.example.rayzi.socket.MySocketManager;
import com.example.rayzi.utils.TempUtil;
import com.google.android.exoplayer2.database.ExoDatabaseProvider;
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;
import com.vaibhavpandey.katora.Container;
import com.vaibhavpandey.katora.contracts.ImmutableContainer;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

import io.agora.rtc.RtcEngine;
import io.branch.referral.Branch;
import io.socket.client.Socket;


public class MainApplication extends Application {

    private static final Container CONTAINER = new Container();
    private static final String TAG = "MainApplication";
    public static boolean isAppOpen = false;
    public Socket socket;


    public static ImmutableContainer getContainer() {
        return CONTAINER;
    }

    public static SimpleCache simpleCache = null;
    public static LeastRecentlyUsedCacheEvictor leastRecentlyUsedCacheEvictor = null;
    public static ExoDatabaseProvider exoDatabaseProvider = null;
    public static Long exoPlayerCacheSize = (long) (900 * 1024 * 1024);

    private RtcEngine mRtcEngine;
    private AgoraEventHandler mHandler = new AgoraEventHandler();
    private EngineConfig mGlobalConfig = new EngineConfig();
    private StatsManager mStatsManager = new StatsManager();

    private static Context context;


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressWarnings("SameParameterValue")
    private void createChannel(String id, String name, int visibility, int importance) {
        NotificationChannel channel = new NotificationChannel(id, name, importance);
        channel.enableLights(true);

        Uri ringUri = Settings.System.DEFAULT_NOTIFICATION_URI;
        channel.setSound(ringUri, new AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setLegacyStreamType(AudioManager.STREAM_NOTIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION_COMMUNICATION_INSTANT).build());

        channel.setLightColor(ContextCompat.getColor(this, R.color.pink));
        channel.setLockscreenVisibility(visibility);
        if (importance == NotificationManager.IMPORTANCE_LOW) {
            channel.setShowBadge(false);
        }

        NotificationManager nm =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nm.createNotificationChannel(channel);
    }

    private SessionManager sessionManager;

    public static Context getAppContext() {
        return MainApplication.context;
    }

    public static RequestOptions requestOptionsFeed = new RequestOptions().placeholder(R.drawable.bg_placeholder_feed).error(R.drawable.bg_placeholder_feed);
    public static RequestOptions requestOptionsLive = new RequestOptions().placeholder(R.drawable.placeholder_live).error(R.drawable.placeholder_live);
    public static RequestOptions requestOptions = new RequestOptions().placeholder(R.drawable.bg_placeholder_defult).error(R.drawable.bg_placeholder_defult);

    AppLifecycleObserver appLifecycleObserver = new AppLifecycleObserver();

    @Override
    public void onCreate() {
        super.onCreate();
        MainApplication.context = getApplicationContext();

        sessionManager = new SessionManager(getAppContext());

        Lifecycle lifecycle = ProcessLifecycleOwner.get().getLifecycle();
        lifecycle.removeObserver(appLifecycleObserver);
        lifecycle.addObserver(appLifecycleObserver);

        Branch.getAutoInstance(this);

//        new Prefs.Builder()
//                .setContext(this)
//                .setUseDefaultSharedPreference(true)
//                .build();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel(
                    getString(R.string.notification_channel_id),
                    getString(R.string.notification_channel_name),
                    Notification.VISIBILITY_PUBLIC,
                    NotificationManager.IMPORTANCE_HIGH
            );
        }

        CONTAINER.install(new ExoPlayerProvider(this));
        CONTAINER.install(new JacksonProvider());
        //  CONTAINER.install(new RetrofitProvider(this));
        CONTAINER.install(new RoomProvider(this));
        TempUtil.cleanupStaleFiles(getApplicationContext());

        if (leastRecentlyUsedCacheEvictor == null) {
            leastRecentlyUsedCacheEvictor = new LeastRecentlyUsedCacheEvictor(exoPlayerCacheSize);
        }

        if (exoDatabaseProvider != null) {
            exoDatabaseProvider = new ExoDatabaseProvider(this);
        }

        if (simpleCache == null) {
            simpleCache = new SimpleCache(getCacheDir(), leastRecentlyUsedCacheEvictor, exoDatabaseProvider);
            if (simpleCache.getCacheSpace() >= 400207768) {
                Log.d(TAG, "onCreate: cache is above 400207768");
//                freeMemory();
            }
            Log.i(TAG, "onCreate: " + simpleCache.getCacheSpace());
        }

    }


    private boolean checkForground() {
        ActivityManager activityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null) {
            return false;
        }
        final String packageName = getApplicationContext().getPackageName();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.processName.equals(packageName)) {
                return true;
            }
        }
        return false;
    }


    public void initAgora(Context context) {
        SessionManager sessionManager = new SessionManager(context);
        try {
            mRtcEngine = RtcEngine.create(getApplicationContext(), sessionManager.getSetting().getAgoraKey(), mHandler);
            mRtcEngine.setLogFile(TempUtil.initializeLogFile(this));
            mRtcEngine.setDefaultAudioRoutetoSpeakerphone(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d(TAG, "initAgora: initializing thay che ===== ");
        SharedPreferences pref = getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE);
        mGlobalConfig.setVideoDimenIndex(pref.getInt(
                Constants.PREF_RESOLUTION_IDX, Constants.DEFAULT_PROFILE_IDX));

        boolean showStats = pref.getBoolean(Constants.PREF_ENABLE_STATS, false);
        mGlobalConfig.setIfShowVideoStats(showStats);
        mStatsManager.enableStats(showStats);

        mGlobalConfig.setMirrorLocalIndex(pref.getInt(Constants.PREF_MIRROR_LOCAL, 0));
        mGlobalConfig.setMirrorRemoteIndex(pref.getInt(Constants.PREF_MIRROR_REMOTE, 1));
        mGlobalConfig.setMirrorEncodeIndex(pref.getInt(Constants.PREF_MIRROR_ENCODE, 0));
    }

    public void freeMemory() {

        try {
            File dir = getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.runFinalization();
        Runtime.getRuntime().gc();
        System.gc();
    }

    public boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if (dir != null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }

    public EngineConfig engineConfig() {
        return mGlobalConfig;
    }

    public RtcEngine rtcEngine() {
        return mRtcEngine;
    }

    public StatsManager statsManager() {
        return mStatsManager;
    }

    public void registerEventHandler(EventHandler handler) {
        mHandler.addHandler(handler);
    }

    public void removeEventHandler(EventHandler handler) {
        mHandler.removeHandler(handler);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        RtcEngine.destroy();
    }

    public Socket getGlobalSoket() {
        return socket;
    }

    public void initGlobalSocket() {
        Log.d(TAG, "initGlobalSocket: 106");

        if (MySocketManager.getInstance().globalConnecting) {
            Log.d(TAG, "initGlobalSocket: already connecting... global socket .........");
            return;
        }
        MySocketManager.getInstance().createGlobal(getApplicationContext());
    }

    public class AppLifecycleObserver implements DefaultLifecycleObserver {
        @Override
        public void onResume(@NonNull LifecycleOwner owner) {
            //Log.e(TAG, "onResume: GLOBAL SOCKET "+socket.isActive());
            try {
                if (sessionManager.getUser() == null) {
                    Log.d(TAG, "onResume: not logged yet");
                    return;
                }

                if (MySocketManager.getInstance().getSocket() == null || !MySocketManager.getInstance().getSocket().connected()) {
                    MySocketManager.getInstance().createGlobal(getApplicationContext());
                    MySocketManager.getInstance().globalConnecting = true;

                    Intent intent = new Intent();
                    intent.setAction("com.ttyo.ONLINE");
                    intent.putExtra("from", "mainapp");
                    sendBroadcast(intent);
                }
                Log.d(TAG, "onResume: ");
            } catch (Exception e) {
                Log.d(TAG, "onResume: eerr " + e.toString());
            }

        }

        @Override
        public void onPause(@NonNull LifecycleOwner owner) {
            Log.e(TAG, "onPause: ");
        }

        @Override
        public void onDestroy(@NonNull LifecycleOwner owner) {
            DefaultLifecycleObserver.super.onDestroy(owner);
            if (MySocketManager.getInstance().getSocket() != null) {
                Log.d(TAG, "onDestroy: ");

                String userId = "";

                if (sessionManager.getUser().getId() != null) {
                    userId = sessionManager.getUser().getId();
                }

                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("userId", userId);
                    Log.d(TAG, "onPause: mannual1");
                    MySocketManager.getInstance().getSocket().emit("manualDisconnect", jsonObject);

                    Intent intent1 = new Intent();
                    intent1.setAction("com.ttyo.OFFLINE");
                    intent1.putExtra("from", "mainapp");
                    sendBroadcast(intent1);

                    Log.d(TAG, "onPause: mannual ");
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                MySocketManager.getInstance().getSocket().disconnect();
            }

        }
    }

}





