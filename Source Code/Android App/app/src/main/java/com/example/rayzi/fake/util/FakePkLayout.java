package com.example.rayzi.fake.util;

import static android.media.metrics.PlaybackStateEvent.STATE_BUFFERING;
import static android.media.metrics.PlaybackStateEvent.STATE_ENDED;
import static com.google.android.exoplayer2.Player.STATE_READY;
import static com.google.android.material.behavior.SwipeDismissBehavior.STATE_IDLE;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.example.rayzi.R;
import com.example.rayzi.databinding.FakePkVideoLayoutBinding;
import com.example.rayzi.pk.PKConstant;
import com.example.rayzi.utils.Filters.FilterUtils;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;


public class FakePkLayout extends LinearLayout {
    private static final int TIMER_TICK_PERIOD = 1000;
    private static final String TAG = "FakePkLayout";

    private LinearLayout mLeftPoint;
    private LinearLayout mRightPoint;
    private RelativeLayout mHostVideoContainer;
    private RelativeLayout mLeftFrameLayout;
    private FrameLayout mRightFrameLayout;
    private RelativeLayout mRightVideoContainer;
    private AppCompatImageView mToOtherRoomBtn;
    private AppCompatTextView mRemainsText;
    private AppCompatTextView mOtherHostName;

    private int mResultIconWidth;

    private AppCompatImageView mPkResultImage;

    private long mTimerStopTimestamp;
    private Handler mTimerHandler;
    private CountDownRunnable mCountDownRunnable = new CountDownRunnable();
    private RelativeLayout lyt_pk_right, lyt_pk_left;
    private LottieAnimationView pkAnim;
    private FakePkVideoLayoutBinding binding;
    private PlayerView playerviewLeft;
    private PlayerView playerviewRight;
    private SimpleExoPlayer player;
    private SimpleExoPlayer player2;
    int leftpoint, rightpoint;

    //  public PkVideoLayoutBinding sheetDilogBinding;

    public FakePkLayout(Context context) {
        super(context);
        init();
    }

    public FakePkLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        Resources resources = getResources();
        mResultIconWidth = resources.getDimensionPixelSize(R.dimen.live_pk_result_icon_size);
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.fake_pk_video_layout, null, false);


        mHostVideoContainer = binding.pkHostVideoLayoutContainer;
        mRightVideoContainer = binding.pkHostVideoLayoutRightContainer;
        mLeftPoint = binding.pkProgressLeftText;
        mRightPoint = binding.pkProgressRightText;
        playerviewLeft = binding.playerviewLeft;
        playerviewRight = binding.playerviewRight;
        mRemainsText = binding.pkHostRemainingTimeText;
        mOtherHostName = binding.pkVideoLayoutOtherHostName;
        lyt_pk_right = binding.lytPkRight;
        lyt_pk_left = binding.lytPkLeft;

        Glide.with(getContext()).asGif().load(R.drawable.pking).into(binding.sparkle);
        Glide.with(getContext()).load(R.drawable.girl).circleCrop().into(binding.leftHost);
        Glide.with(getContext()).load(R.drawable.girl).circleCrop().into(binding.rightHost);


        addView(binding.getRoot());

    }

    public void setUserLayVisible() {
        binding.userLayBorder.setVisibility(View.VISIBLE);
    }

    public void setOnClickGotoPeerChannelListener(OnClickListener listener) {
        mToOtherRoomBtn.setOnClickListener(listener);
    }

    public void setOnLeftHostClick(OnClickListener listener) {
        binding.leftHost.setOnClickListener(listener);
    }

    public void setOnRightHostClick(OnClickListener listener) {
        binding.rightHost.setOnClickListener(listener);
    }

    public void setPoints(int localPoint, int remotePoint) {
        if (localPoint < 0 || remotePoint < 0) {
            return;
        }

        int localWeight;
        int remoteWeight;
        if (localPoint == 0 && remotePoint == 0) {
            localWeight = 1;
            remoteWeight = 1;
        } else if (localPoint == 0) {
            localWeight = 10;
            remoteWeight = 90;
        } else if (remotePoint == 0) {
            localWeight = 90;
            remoteWeight = 10;
        } else {
            localWeight = localPoint;
            remoteWeight = remotePoint;
        }

        setWeight(mLeftPoint, localWeight);
        setWeight(binding.pkProgressLeftText1, localWeight);
        setWeight(mRightPoint, remoteWeight);
        setWeight(binding.pkProgressRightText1, remoteWeight);

        leftpoint = localPoint;
        rightpoint = remotePoint;

        binding.localHostRank.setText(String.valueOf(localPoint));
        binding.remoteHostRank.setText(String.valueOf(remotePoint));

//          mLeftPoint.setText(String.valueOf(localPoint));
//          mRightPoint.setText(String.valueOf(remotePoint));

    }

    public int getLeftPoints() {
        return leftpoint;
    }


    public int getRightpoint() {
        return rightpoint;
    }

    public void setWeight(LinearLayout textView, int weight) {
        LayoutParams params =
                (LayoutParams) textView.getLayoutParams();
        params.weight = weight;
        textView.setLayoutParams(params);
    }

    public RelativeLayout getLeftVideoLayout() {
        return mLeftFrameLayout;
    }

    public FrameLayout getRightVideoLayout() {
        return mRightFrameLayout;
    }

    public void setPKHostName(String name) {
        mOtherHostName.setText(name);
    }

    public void startCountDownTimer(long remaining) {
        mTimerStopTimestamp = System.currentTimeMillis() + remaining;
        mTimerHandler = new Handler(getContext().getMainLooper());
        mTimerHandler.post(() -> mRemainsText.setText(timestampToCountdown(remaining)));
        mTimerHandler.postDelayed(this::stopCountDownTimer, remaining);
        mTimerHandler.postDelayed(mCountDownRunnable, TIMER_TICK_PERIOD);
    }

    public void stopCountDownTimer() {
        if (mTimerHandler != null) {
            mTimerHandler.removeCallbacksAndMessages(null);
        }
    }

    public void stopPlayer() {
        if (player != null) {
            player.release();
            player.stop();
        }
        if (player2 != null) {
            player2.release();
            player2.stop();
        }

    }


    public void setPlayer1(String videoLink) {
        player = new SimpleExoPlayer.Builder(getContext()).build();
        binding.playerviewLeft.setPlayer(player);
        //  binding.playerview.setShowBuffering(true);

        Uri uri = Uri.parse(videoLink);


        MediaSource mediaSource = buildMediaSource(uri);
        Log.d(TAG, "initializePlayer: setPlayer1 == " + uri);
        player.setPlayWhenReady(true);
        //player.seekTo(videoTiming);
        player.prepare(mediaSource, false, false);
        player.setRepeatMode(Player.REPEAT_MODE_ALL);
        //player.setVolume(0f);

        player.addListener(new Player.EventListener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                switch (playbackState) {
                    case STATE_BUFFERING:
                        Log.d("TAG", "buffer: " + uri);
                        break;
                    case STATE_ENDED:
                        break;
                    case STATE_IDLE:
                        Log.d("TAG", "idle: " + uri);
                        player.release();
                        Toast.makeText(getContext(), R.string.something_went_wrong_text, Toast.LENGTH_SHORT).show();
                        if (player != null) {
                            player.release();
                        }
                        break;

                    case STATE_READY:
                        //binding.animationView.setVisibility(View.GONE);

                        Log.d("TAG", "ready: " + uri);

                        break;
                    default:
                        break;
                }
            }
        });
    }


    public void setPlayer2(String videoLink) {
        player2 = new SimpleExoPlayer.Builder(getContext()).build();
        binding.playerviewRight.setPlayer(player2);
        //  binding.playerview.setShowBuffering(true);

        Uri uri = Uri.parse(videoLink);


        MediaSource mediaSource = buildMediaSource(uri);
        Log.d(TAG, "initializePlayer: setPlayer2 ==  " + uri);
        player2.setPlayWhenReady(true);
        //player.seekTo(videoTiming);
        player2.prepare(mediaSource, false, false);
        player2.setRepeatMode(Player.REPEAT_MODE_ALL);
        //player2.setVolume(0f);

        player2.addListener(new Player.EventListener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                switch (playbackState) {

                    case STATE_BUFFERING:
                        Log.d("TAG", "buffer: " + uri);
                        break;
                    case STATE_ENDED:

                        break;
                    case STATE_IDLE:
                        Log.d("TAG", "idle: " + uri);
                        player2.release();

                        Toast.makeText(getContext(),R.string.something_went_wrong_text, Toast.LENGTH_SHORT).show();

                        if (player2 != null) {
                            player2.release();
                        }
                        break;

                    case STATE_READY:
                        //binding.animationView.setVisibility(View.GONE);

                        Log.d("TAG", "ready: " + uri);

                        break;
                    default:
                        break;
                }
            }
        });
    }

    private MediaSource buildMediaSource(Uri uri) {
        DataSource.Factory dataSourceFactory =
                new DefaultDataSourceFactory(getContext(), "exoplayer-codelab");
        return new ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(uri);
    }


    private String timestampToCountdown(long remaining) {
        if (remaining <= 0) return "00:00";
        long seconds = remaining / 1000;
        long minute = seconds / 60;
        int remainSecond = (int) seconds % 60;
        String minuteString = minute < 10 ? "0" + minute : "" + minute;
        String secondString = remainSecond < 10 ? "0" + remainSecond : "" + remainSecond;
        return minuteString + ":" + secondString;
    }

    /**
     * Set PK result of current PK session.
     *
     * @param result pk result of current room owner.
     */
    public void setResult(int result) {
        mPkResultImage = new AppCompatImageView(getContext());
        if (result == PKConstant.PK_RESULT_LOSE || result == PKConstant.PK_RESULT_WIN) {
            mPkResultImage.setImageResource(R.drawable.winner);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    mResultIconWidth, mResultIconWidth);
            params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
            RelativeLayout container = result == PKConstant.PK_RESULT_LOSE
                    ? mRightVideoContainer
                    : mLeftFrameLayout;
            container.addView(mPkResultImage, params);
        } else if (result == PKConstant.PK_RESULT_DRAW) {
            mPkResultImage.setImageResource(R.drawable.handshake);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    mResultIconWidth, mResultIconWidth);
            params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
            mHostVideoContainer.addView(mPkResultImage, params);
        }
    }

    public void removeResult() {
        if (mPkResultImage == null) {
            return;
        }

        ViewGroup parent = (ViewGroup) mPkResultImage.getParent();
        if (parent != null) {
            parent.removeView(mPkResultImage);
            mPkResultImage = null;
        }

    }

    public void playAnim() {

//        pkAnim.setProgress(0);
//        pkAnim.playAnimation();

        Animation animRtoL = AnimationUtils.loadAnimation(getContext().getApplicationContext(), R.anim.anim_slide_right_to_left);
        lyt_pk_right.startAnimation(animRtoL);

        Animation animImg = AnimationUtils.loadAnimation(getContext().getApplicationContext(), R.anim.zoom_out_zomm_in);
        binding.vsImg.startAnimation(animImg);

        animImg.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                binding.vsImg.setVisibility(GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        animRtoL.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Animation animRtoLFast = AnimationUtils.loadAnimation(getContext().getApplicationContext(), R.anim.anim_slide_right_to_left_fast);
                lyt_pk_right.startAnimation(animRtoLFast);
                animRtoLFast.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        lyt_pk_right.setVisibility(GONE);
                        // pkAnim.setVisibility(GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


        Animation animLtoR = AnimationUtils.loadAnimation(getContext().getApplicationContext(), R.anim.anim_slide_left_to_right);
        lyt_pk_left.startAnimation(animLtoR);

        animLtoR.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Animation animLtoRFast = AnimationUtils.loadAnimation(getContext().getApplicationContext(), R.anim.anim_slide_left_to_right_fast);
                lyt_pk_left.startAnimation(animLtoRFast);
                animLtoRFast.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        lyt_pk_left.setVisibility(GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }


//    public void setUpHostsDetails(LiveUser.PkConfig pkConfig) {
//        binding.hostNameLeft.setText(pkConfig.getHost1Details().getName());
//        binding.lytHostImageLeft.setUserImage(pkConfig.getHost1Details().getImage(), pkConfig.getHost1Details().isVIP());
//
//        binding.hostNameRight.setText(pkConfig.getHost2Details().getName());
//        binding.lytHostImageRight.setUserImage(pkConfig.getHost2Details().getImage(), pkConfig.getHost2Details().isVIP());
//    }


    public void setLeftFilter1(String filterName) {
        Log.d("TAG", "initLister: filter name " + filterName);
        if (filterName.equalsIgnoreCase("None")) {

            binding.imgFilter2Left.setImageDrawable(null);
        } else {

            Glide.with(binding.imgFilter2Left).load(FilterUtils.getDraw(filterName)).into(binding.imgFilter2Left);
            //  Glide.with(this).asGif().load(selectedFilter.getFilter()).into(binding.imgFilter);
        }
    }

    public void setRightFilter1(String FilterName) {
        Log.d("TAG", "initLister: filter name " + FilterName);
        if (FilterName.equalsIgnoreCase("None")) {

            binding.imgFilter2Right.setImageDrawable(null);
        } else {

            Glide.with(binding.imgFilter2Right).load(FilterUtils.getDraw(FilterName)).into(binding.imgFilter2Right);
            //  Glide.with(this).asGif().load(selectedFilter.getFilter()).into(binding.imgFilter);
        }
    }

    public void setTime(String convertSecondsToHMmSs) {
        binding.pkHostRemainingTimeText.setText(convertSecondsToHMmSs);
    }

    public void setWinner(int winner) {
        if (winner == 1) {
            binding.imgResult1.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.winner));
        } else if (winner == 2) {
            binding.imgResult2.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.winner));
        } else {
            binding.imgResult1.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.handshake));
            binding.imgResult2.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.handshake));
        }
    }

    private class CountDownRunnable implements Runnable {
        @Override
        public void run() {
            long current = System.currentTimeMillis();
            mRemainsText.setText(timestampToCountdown(mTimerStopTimestamp - current));
            mTimerHandler.postDelayed(mCountDownRunnable, TIMER_TICK_PERIOD);
        }
    }

}
