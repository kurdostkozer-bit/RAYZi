package com.example.rayzi.pk.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.os.CountDownTimer;
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

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.example.rayzi.R;
import com.example.rayzi.databinding.PkVideoLayoutBinding;
import com.example.rayzi.pk.PKConstant;
import com.example.rayzi.pkRoot.PkAudioLiveUserRoot;


public class PkLayout extends LinearLayout {
    private static final int TIMER_TICK_PERIOD = 1000;


    private int mResultIconWidth;

    private AppCompatImageView mPkResultImage;

    private long mTimerStopTimestamp;
    private Handler mTimerHandler;
    private final CountDownRunnable mCountDownRunnable = new CountDownRunnable();
    private PkVideoLayoutBinding binding;
    private Context context;
    //  public PkVideoLayoutBinding sheetDilogBinding;

    public PkLayout(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public PkLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        Resources resources = getResources();
        mResultIconWidth = resources.getDimensionPixelSize(R.dimen.live_pk_result_icon_size);
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.pk_video_layout, null, false);


//        Glide.with(getContext()).asGif().load(R.drawable.pking).into(binding.sparkle);
        Glide.with(getContext()).load(R.drawable.girl).circleCrop().into(binding.leftHost);
        Glide.with(getContext()).load(R.drawable.girl).circleCrop().into(binding.rightHost);


        addView(binding.getRoot());

    }

    public void setHost(boolean isHost) {
//        mToOtherRoomBtn.setVisibility(isHost ? View.GONE : View.VISIBLE);
        binding.userLayBorder.setVisibility(isHost ? View.GONE : View.VISIBLE);
    }

    public void setUserLayVisible() {
        binding.userLayBorder.setVisibility(View.VISIBLE);
    }

    public void setLeftUserImage(String image) {
        Glide.with(getContext().getApplicationContext()).load(image).circleCrop().into(binding.leftHost);
    }

    public void setRightUserImage(String image) {
        Glide.with(getContext().getApplicationContext()).load(image).circleCrop().into(binding.rightHost);
    }

    public void setOnClickGotoPeerChannelListener(OnClickListener listener) {
        binding.pkVideoLayoutEnterOtherRoomBtn.setOnClickListener(listener);
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

        setWeight(binding.pkProgressLeftText, localWeight);
//        setWeight(binding.pkProgressLeftText1, localWeight);
        setWeight(binding.pkProgressRightText, remoteWeight);
//        setWeight(binding.pkProgressRightText1, remoteWeight);
        binding.localHostRank.setText(String.valueOf(localPoint));
        binding.remoteHostRank.setText(String.valueOf(remotePoint));

        //  mLeftPoint.setText(String.valueOf(localPoint));
        //  mRightPoint.setText(String.valueOf(remotePoint));

    }

    public void normalizePoints() {
        setWeight(binding.pkProgressLeftText, 1);
//        setWeight(binding.pkProgressLeftText1, 1);
        setWeight(binding.pkProgressRightText, 1);
//        setWeight(binding.pkProgressRightText1, 1);
        binding.localHostRank.setText(String.valueOf(0));
        binding.remoteHostRank.setText(String.valueOf(0));
    }

    public void setWeight(LinearLayout textView, int weight) {
        LayoutParams params =
                (LayoutParams) textView.getLayoutParams();
        params.weight = weight;
        textView.setLayoutParams(params);
    }

    public RelativeLayout getLeftVideoLayout() {
        return binding.pkHostVideoLayoutLeftContainer;
    }

    public FrameLayout getRightVideoLayout() {
        return binding.pkHostVideoLayoutRight;
    }

    public void setPKHostName(String name) {
        binding.pkVideoLayoutOtherHostName.setText(name);
    }

    public void startCountDownTimer(long remaining) {
        mTimerStopTimestamp = System.currentTimeMillis() + remaining;
        mTimerHandler = new Handler(getContext().getMainLooper());
        mTimerHandler.post(() -> binding.pkHostRemainingTimeText.setText(timestampToCountdown(remaining)));
        mTimerHandler.postDelayed(this::stopCountDownTimer, remaining);
        mTimerHandler.postDelayed(mCountDownRunnable, TIMER_TICK_PERIOD);
    }

    public void stopCountDownTimer() {
        if (mTimerHandler != null) {
            mTimerHandler.removeCallbacksAndMessages(null);
        }
    }

    private String timestampToCountdown(long remaining) {
        if (remaining <= 0) return "00:00";
        long seconds = remaining / 1000;
        long minute = seconds / 60;
        int remainSecond = (int) seconds % 60;
        String minuteString = minute < 10 ? "0" + minute : String.valueOf(minute);
        String secondString = remainSecond < 10 ? "0" + remainSecond : String.valueOf(remainSecond);
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
            RelativeLayout container = result == PKConstant.PK_RESULT_LOSE ? binding.pkHostVideoLayoutRightContainer : binding.pkHostVideoLayoutLeftContainer;
            container.addView(mPkResultImage, params);
        } else if (result == PKConstant.PK_RESULT_DRAW) {
            mPkResultImage.setImageResource(R.drawable.handshake);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    mResultIconWidth, mResultIconWidth);
            params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
            binding.pkHostVideoLayoutContainer.addView(mPkResultImage, params);
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
        binding.pkAnim.setProgress(0);
        binding.pkAnim.playAnimation();

        Animation animRtoL = AnimationUtils.loadAnimation(getContext().getApplicationContext(), R.anim.anim_slide_right_to_left);
        binding.lytPkRight.startAnimation(animRtoL);
        animRtoL.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Animation animRtoLFast = AnimationUtils.loadAnimation(getContext().getApplicationContext(), R.anim.anim_slide_right_to_left_fast);
                binding.lytPkRight.startAnimation(animRtoLFast);
                animRtoLFast.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        binding.lytPkRight.setVisibility(GONE);
                        binding.pkAnim.setVisibility(GONE);
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
        binding.lytPkLeft.startAnimation(animLtoR);
        animLtoR.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Animation animLtoRFast = AnimationUtils.loadAnimation(getContext().getApplicationContext(), R.anim.anim_slide_left_to_right_fast);
                binding.lytPkLeft.startAnimation(animLtoRFast);
                animLtoRFast.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        binding.lytPkLeft.setVisibility(GONE);
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


    @SuppressLint("SetTextI18n")
    public void setUpHostsDetails(boolean isHost, PkAudioLiveUserRoot.UsersItem.PkConfig pkConfig) {
        binding.setIsHost(isHost);
        binding.hostNameLeft.setText(pkConfig.getHost1Details().getName());
        binding.userNameLeft.setText(pkConfig.getHost1Details().getName());
        binding.uniqueIdLeft.setText("ID: " + pkConfig.getHost1Details().getUniqueId());
        binding.lytHostImageLeft.setUserImage(pkConfig.getHost1Details().getImage(), pkConfig.getHost1Details().getAvatarFrameImage(), 5);

        binding.hostNameRight.setText(pkConfig.getHost2Details().getName());
        binding.userNameRight.setText(pkConfig.getHost2Details().getName());
        binding.uniqueIdRight.setText("ID: " + pkConfig.getHost2Details().getUniqueId());
        binding.lytHostImageRight.setUserImage(pkConfig.getHost2Details().getImage(), pkConfig.getHost2Details().getAvatarFrameImage(), 5);
    }


    public void setTime(String convertSecondsToHMmSs) {
        binding.pkHostRemainingTimeText.setText(convertSecondsToHMmSs);
    }


    public void setWinner(int winner) {

        binding.imgResult1.setVisibility(VISIBLE);
        binding.imgResult2.setVisibility(VISIBLE);

        if (winner == 1) {
            binding.imgResult1.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.winner));
        } else if (winner == 2) {
            binding.imgResult2.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.winner));
        } else {
//            Glide.with(getContext()).load(R.drawable.handshake).into(binding.imgResult1);
//            Glide.with(getContext()).load(R.drawable.handshake).into(binding.imgResult2);

            Log.d("hostpkliveactivity", "setWinner: " + winner);

            binding.imgResult1.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.handshake));

            Log.d("hostpkliveactivity", "setWinner: " + binding.imgResult1);

            binding.imgResult2.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.handshake));
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                binding.imgResult1.setVisibility(GONE);
                binding.imgResult2.setVisibility(GONE);
            }
        }, 3000);
    }

    public void onClickStartPk(OnClickListener listener) {
        binding.startPK.setOnClickListener(listener);
    }

    public void setOnClickSwitchPKRoom(OnClickListener listener) {
        binding.switchPkRoom.setOnClickListener(listener);
    }

    public void setPKbuttonVisible() {
        binding.startPK.setVisibility(VISIBLE);
    }

    public void setPKbuttonGone() {
        binding.startPK.setVisibility(GONE);
    }

    public void setTimeText(String text) {
        binding.pkHostRemainingTimeText.setText(text);
    }

    public void startTimer() {
        binding.startPK.setVisibility(GONE);
        binding.timerText.setVisibility(VISIBLE);

        new CountDownTimer(3000, 1000) {

            public void onTick(long millisUntilFinished) {
                binding.timerText.setVisibility(VISIBLE);
                Animation animImg = AnimationUtils.loadAnimation(getContext().getApplicationContext(), R.anim.zoom_out_zomm_in);
                binding.timerText.startAnimation(animImg);
                binding.timerText.setText(String.valueOf(millisUntilFinished / 1000));
                // logic to set the EditText could go here
            }

            public void onFinish() {
                binding.timerText.setText(R.string.go);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        binding.timerText.setVisibility(GONE);

                    }
                }, 1000);
            }

        }.start();
    }

    private class CountDownRunnable implements Runnable {
        @Override
        public void run() {
            long current = System.currentTimeMillis();
            binding.pkHostRemainingTimeText.setText(timestampToCountdown(mTimerStopTimestamp - current));
            mTimerHandler.postDelayed(mCountDownRunnable, TIMER_TICK_PERIOD);
        }
    }

}
