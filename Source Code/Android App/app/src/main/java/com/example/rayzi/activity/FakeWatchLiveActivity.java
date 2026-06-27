package com.example.rayzi.activity;

import static com.google.android.exoplayer2.Player.STATE_BUFFERING;
import static com.google.android.exoplayer2.Player.STATE_ENDED;
import static com.google.android.exoplayer2.Player.STATE_IDLE;
import static com.google.android.exoplayer2.Player.STATE_READY;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.rayzi.BuildConfig;
import com.example.rayzi.R;
import com.example.rayzi.SessionManager;
import com.example.rayzi.bottomsheets.BottomSheetReport_g;
import com.example.rayzi.bottomsheets.BottomSheetReport_option;
import com.example.rayzi.databinding.ActivityFakeWatchLiveBinding;
import com.example.rayzi.emoji.EmojiBottomsheetFragment;
import com.example.rayzi.fake.audio.FakeAudioWatchActivity;
import com.example.rayzi.liveGame.bottomsheet.BottomSheetGameAviator;
import com.example.rayzi.liveGame.bottomsheet.BottomSheetGameCasino;
import com.example.rayzi.liveGame.bottomsheet.BottomSheetGameList;
import com.example.rayzi.liveGame.bottomsheet.BottomSheetGameRocketCrash;
import com.example.rayzi.liveGame.bottomsheet.BottomSheetGameTeenPatti;
import com.example.rayzi.liveGame.dialog.DialogGame;
import com.example.rayzi.modelclass.GiftRoot;
import com.example.rayzi.modelclass.LiveStramComment;
import com.example.rayzi.modelclass.LiveUserRoot;
import com.example.rayzi.modelclass.UserRoot;
import com.example.rayzi.popups.PopupBuilder;
import com.example.rayzi.retrofit.Const;
import com.example.rayzi.retrofit.RetrofitBuilder;
import com.example.rayzi.socket.MySocketManager;
import com.example.rayzi.user.guestUser.GuestActivity;
import com.example.rayzi.viewModel.EmojiSheetViewModel;
import com.example.rayzi.viewModel.ViewModelFactory;
import com.example.rayzi.viewModel.WatchLiveViewModel;
import com.example.rayzi.z_demo.Demo_contents;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.gson.Gson;
import com.opensource.svgaplayer.SVGADrawable;
import com.opensource.svgaplayer.SVGADynamicEntity;
import com.opensource.svgaplayer.SVGAImageView;
import com.opensource.svgaplayer.SVGAParser;
import com.opensource.svgaplayer.SVGAVideoEntity;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import io.branch.indexing.BranchUniversalObject;
import io.branch.referral.util.ContentMetadata;
import io.branch.referral.util.LinkProperties;
import jp.wasabeef.glide.transformations.BlurTransformation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FakeWatchLiveActivity extends BaseActivity {

    private static final String TAG = "fakewatch";
    ActivityFakeWatchLiveBinding binding;
    LiveUserRoot.UsersItem fakeHost;
    String videoURL;
    long animationDurationMillis;
    SessionManager sessionManager;

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            viewModel.liveStramCommentAdapter.addSingleComment(Demo_contents.getLiveStreamComment().get(0));
            binding.rvComments.scrollToPosition(0);
            handler.postDelayed(this, 4000);
        }
    };
    Handler handler = new Handler();
    private EmojiSheetViewModel giftViewModel;
    private SimpleExoPlayer player;
    EmojiBottomsheetFragment emojiBottomsheetFragment;
    private WatchLiveViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_fake_watch_live);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        viewModel = ViewModelProviders.of(this, new ViewModelFactory(new WatchLiveViewModel()).createFor()).get(WatchLiveViewModel.class);
        giftViewModel = ViewModelProviders.of(this, new ViewModelFactory(new EmojiSheetViewModel()).createFor()).get(EmojiSheetViewModel.class);
        sessionManager = new SessionManager(this);
        binding.setViewModel(viewModel);
        viewModel.initLister();
        giftViewModel.initEmojiSheet(this);
        giftViewModel.getGiftCategory();

        emojiBottomsheetFragment = new EmojiBottomsheetFragment();
        Intent intent = getIntent();
        String userStr = intent.getStringExtra(Const.DATA);
        sessionManager = new SessionManager(this);
        if (userStr != null && !userStr.isEmpty()) {
            fakeHost = new Gson().fromJson(userStr, LiveUserRoot.UsersItem.class);
            Glide.with(this).load(fakeHost.getImage()).circleCrop().diskCacheStrategy(DiskCacheStrategy.ALL).into(binding.imgProfile);

            Log.d(TAG, "onCreate: fakeho==========" + fakeHost);
            videoURL = fakeHost.getLink();
            Log.d(TAG, "onCreate: link==================== " + fakeHost.getLink());
            Log.e(TAG, "onCreate: " + fakeHost.getLiveStreamingId());
            initView();
        }
        viewModel.liveStramCommentAdapter.addSingleComment(new LiveStramComment("", Demo_contents.getUsers(true).get(0), false, null, "", "comment", ""));
        binding.rvComments.scrollToPosition(0);
        handler.postDelayed(runnable, 4000);
        initListener();
        entryEffectShow();
    }

    public void entryEffectShow() {
        if (sessionManager.getUser().getLiveJoinSvga() != null) {
            binding.layEntry.setVisibility(View.VISIBLE);

            SVGAImageView imageView = binding.svgImage;
            SVGAParser parser = new SVGAParser(FakeWatchLiveActivity.this);
            try {
                parser.decodeFromURL(new URL(sessionManager.getUser().getLiveJoinSvga() != null && !sessionManager.getUser().getLiveJoinSvga().getImage().isEmpty() ? BuildConfig.BASE_URL + sessionManager.getUser().getLiveJoinSvga().getImage() : ""), new SVGAParser.ParseCompletion() {
                    @Override
                    public void onComplete(@NonNull SVGAVideoEntity svgaVideoEntity) {
                        SVGADynamicEntity dynamicEntity = new SVGADynamicEntity();
                        dynamicEntity.setDynamicImage(BuildConfig.BASE_URL + sessionManager.getUser().getLiveJoinSvga().getImage(), "99");
                        SVGADrawable drawable = new SVGADrawable(svgaVideoEntity, dynamicEntity);
                        imageView.setImageDrawable(drawable);
                        imageView.startAnimation();
                        animationDurationMillis = svgaVideoEntity.getFrames() / svgaVideoEntity.getFPS() * 1000L;
                        new Handler(Objects.requireNonNull(Looper.myLooper())).postDelayed(() -> {
                            binding.svgaImage.setVisibility(View.GONE);
                            binding.layEntry.setVisibility(View.GONE);
                            binding.svgaImage.clear();
                        }, animationDurationMillis);
                    }

                    @Override
                    public void onError() {

                    }
                }, null);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            binding.userName.setText(sessionManager.getUser().getName());
            Glide.with(FakeWatchLiveActivity.this).load(sessionManager.getUser().getImage()).circleCrop().into(binding.userImage);
            Glide.with(FakeWatchLiveActivity.this).load(sessionManager.getUser().getAvatarFrameImage() != null && !sessionManager.getUser().getAvatarFrameImage().isEmpty() ? BuildConfig.BASE_URL + sessionManager.getUser().getAvatarFrameImage() : "").into(binding.avatarFrameImage);

            Animation animation = AnimationUtils.loadAnimation(FakeWatchLiveActivity.this, R.anim.slide_in_right);
            animation.setFillAfter(true);
            binding.nameLyt.startAnimation(animation);
        }
    }

    private void initListener() {
        binding.imgshare.setOnClickListener(v -> {
            binding.imgshare.setEnabled(false);
            BranchUniversalObject buo = new BranchUniversalObject()
                    .setCanonicalIdentifier("content/12345")
                    .setTitle("Watch Live Video")
                    .setContentDescription("By : " + fakeHost.getName())
                    .setContentImageUrl(fakeHost.getImage())
                    .setContentMetadata(new ContentMetadata().addCustomMetadata("type", "LIVE").addCustomMetadata(Const.DATA, new Gson().toJson(fakeHost)));

            LinkProperties lp = new LinkProperties()
                    .setChannel("facebook")
                    .setFeature("sharing")
                    .setCampaign("content 123 launch")
                    .setStage("new user")

                    .addControlParameter("", "")
                    .addControlParameter("", Long.toString(Calendar.getInstance().getTimeInMillis()));

            buo.generateShortUrl(this, lp, (url, error) -> {
                try {

                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    String shareMessage = url;
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                    startActivity(Intent.createChooser(shareIntent, "choose one"));
                    binding.imgshare.setEnabled(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        });

        binding.imgGame.setOnClickListener(v -> {
            new BottomSheetGameList(FakeWatchLiveActivity.this, gameItem -> {
                if (gameItem.getName().contains("Roulette")) {
                    new BottomSheetGameCasino(this, gameItem.getLink(), new BottomSheetGameCasino.OnDialogDismissListener() {
                        @Override
                        public void onDismiss() {
                            MySocketManager.getInstance().getSocket().emit(Const.USER_COIN_UPDATE, sessionManager.getUser().getId());
                        }
                    });
                } else if (gameItem.getName().contains("Ferry")) {
                    new DialogGame(this, gameItem.getLink(), new DialogGame.OnDialogDismissListener() {
                        @Override
                        public void onDismiss() {
                            MySocketManager.getInstance().getSocket().emit(Const.USER_COIN_UPDATE, sessionManager.getUser().getId());
                        }
                    });
                }  else {
                    new BottomSheetGameTeenPatti(this, gameItem.getLink(), new BottomSheetGameTeenPatti.OnDialogDismissListener() {
                        @Override
                        public void onDismiss() {
                            MySocketManager.getInstance().getSocket().emit(Const.USER_COIN_UPDATE, sessionManager.getUser().getId());
                        }
                    });
                }
            });
        });

        binding.imggift2.setOnClickListener(v -> {
            if (!emojiBottomsheetFragment.isAdded()) {
                emojiBottomsheetFragment.show(getSupportFragmentManager(), "emojifragfmetn");
            }
        });

        binding.lytHost.setOnClickListener(view -> startActivity(new Intent(FakeWatchLiveActivity.this, GuestActivity.class).putExtra(Const.USERID, fakeHost.getId())));

        giftViewModel.finelGift.observe(this, giftItem -> {
            if (giftItem != null) {
                int totalCoin = giftItem.getCoin() * giftItem.getCount();
                if (sessionManager.getUser().getDiamond() < totalCoin) {
                    Toast.makeText(FakeWatchLiveActivity.this, "You not have enough diamonds to send gift", Toast.LENGTH_SHORT).show();
                    return;
                }

                getCoin(giftItem);

                String finalGiftLink = null;
                List<GiftRoot.GiftItem> giftItemList = sessionManager.getGiftsList(giftItem.getCategory());
                for (int i = 0; i < giftItemList.size(); i++) {
                    if (giftItem.getId().equals(giftItemList.get(i).getId())) {
                        finalGiftLink = BuildConfig.BASE_URL + giftItemList.get(i).getImage();
                    }
                }

                if (giftItem.getType() == 2) {
                    binding.svgaImage.setVisibility(View.VISIBLE);
                    SVGAImageView imageView = binding.svgaImage;
                    SVGAParser parser = new SVGAParser(FakeWatchLiveActivity.this);
                    try {
                        parser.decodeFromURL(new URL(finalGiftLink), new SVGAParser.ParseCompletion() {
                            @Override
                            public void onComplete(@NonNull SVGAVideoEntity svgaVideoEntity) {
                                SVGADrawable drawable = new SVGADrawable(svgaVideoEntity);
                                imageView.setImageDrawable(drawable);
                                imageView.startAnimation();
                                Log.d("TAG", "setData: " + giftItem.getImage());
                                new Handler(Looper.myLooper()).postDelayed(() -> {
                                    binding.svgaImage.setVisibility(View.GONE);
                                    binding.svgaImage.clear();
                                }, 5000);
                            }

                            @Override
                            public void onError() {

                            }
                        }, new SVGAParser.PlayCallback() {
                            @Override
                            public void onPlay(@NonNull List<? extends File> list) {
                            }
                        });

                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }

                } else {
                    binding.imgGift.setVisibility(View.VISIBLE);
                    Glide.with(this).load(finalGiftLink).diskCacheStrategy(DiskCacheStrategy.ALL).into(binding.imgGift);
                    new Handler(Looper.getMainLooper()).postDelayed(() -> binding.imgGift.setImageDrawable(null), 4000);
                }

                emojiBottomsheetFragment.dismiss();

            }
        });

    }

    private void getCoin(GiftRoot.GiftItem selectedGift) {
        Call<UserRoot> call = RetrofitBuilder.create().getCoin(sessionManager.getUser().getId(), selectedGift.getCoin(), "", Const.LIVE);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<UserRoot> call, Response<UserRoot> response) {
                if (response.code() == 200) {
                    if (response.body().isStatus() && response.body().getUser() != null) {
                        sessionManager.saveUser(response.body().getUser());
                        Log.d(TAG, "onResponse: getCoin == sessionManager.getUser().getDiamond() ==  " + sessionManager.getUser().getDiamond());
                    }
                }
            }

            @Override
            public void onFailure(Call<UserRoot> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }


    /*private void showProfileSheet() {

        userProfileBottomSheet = new UserProfileBottomSheet(this);
        userProfileBottomSheet.show(false, fakeHost, "");

    }*/

    @SuppressLint("SetTextI18n")
    private void initView() {
        Log.e(TAG, "onCreate: " + fakeHost.getRCoin());
        setPlayer();

        viewModel.liveStramCommentAdapter.addSingleComment(null);

        binding.tvName.setText(fakeHost.getName());
        binding.tvCoins.setText(String.valueOf(fakeHost.getRCoin()));
        String uniqueId;
        if (fakeHost.getUniqueId() != null && !fakeHost.getUniqueId().isEmpty()) {
            uniqueId = fakeHost.getUniqueId();
        } else {
            Random random = new Random();
            int randomNumber = 100000 + random.nextInt(900000);
            uniqueId = String.valueOf(randomNumber);
        }
        binding.tvUniqueId.setText(getString(R.string.id) + uniqueId);
        binding.tvGifts.setText(String.valueOf(Demo_contents.getRandomPostCoint()));

        LiveStramComment liveStramComment = new LiveStramComment("", sessionManager.getUser(), true, "", "", "comment", "");
        viewModel.liveStramCommentAdapter.addSingleComment(liveStramComment);
        binding.rvComments.scrollToPosition(0);

        MultiTransformation<Bitmap> transformations = new MultiTransformation<>(
                new BlurTransformation(30),
                new CenterCrop()
        );

        MultiTransformation<Bitmap> transformations1 = new MultiTransformation<>(
                new BlurTransformation(30),
                new CenterCrop(),
                new CircleCrop()
        );
        Glide.with(FakeWatchLiveActivity.this)
                .load(fakeHost.getImage())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .transform(transformations)
                .into(binding.backImageBlur);

        Glide.with(FakeWatchLiveActivity.this)
                .load(fakeHost.getImage())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .transform(transformations1)
                .into(binding.imgUser);

    }

    private void setPlayer() {
        player = new SimpleExoPlayer.Builder(this).build();
        binding.playerview.setPlayer(player);
        //  binding.playerview.setShowBuffering(true);
        Log.d(TAG, "setvideoURL: " + videoURL);
        Uri uri = Uri.parse(videoURL);
        MediaSource mediaSource = buildMediaSource(uri);
        Log.d(TAG, "initializePlayer: " + uri);
        player.setPlayWhenReady(true);
        player.seekTo(0, 0);
        player.prepare(mediaSource, false, false);
        player.setRepeatMode(Player.REPEAT_MODE_ALL);

        player.addListener(new Player.EventListener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                switch (playbackState) {
                    case STATE_BUFFERING -> {
                        binding.backImageBlur.setVisibility(View.VISIBLE);
                        binding.pd.setVisibility(View.VISIBLE);
                        Log.d(TAG, "buffer: " + uri);
                    }
                    case STATE_ENDED -> {
                        Toast.makeText(FakeWatchLiveActivity.this, R.string.live_ended, Toast.LENGTH_SHORT).show();
                        new Handler().postDelayed(() -> finish(), 2000);
                        Log.d(TAG, "end: " + uri);
                    }
                    case STATE_IDLE -> Log.d(TAG, "idle: " + uri);
                    case STATE_READY -> {
                        binding.backImageBlur.setVisibility(View.GONE);
                        binding.pd.setVisibility(View.GONE);
                        Log.d(TAG, "ready: " + uri);
                    }
                    default -> {
                    }
                }
            }
        });
    }

    private MediaSource buildMediaSource(Uri uri) {
        DataSource.Factory dataSourceFactory =
                new DefaultDataSourceFactory(this, "exoplayer-codelab");
        return new ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(uri);
    }


    public void onClickBack(View view) {
        onBackPressed();
    }

    public void onClickSendComment(View view) {
        String comment = binding.etComment.getText().toString();
        if (!comment.isEmpty()) {
            LiveStramComment liveStramComment = new LiveStramComment(comment, sessionManager.getUser(), false, fakeHost.getLiveStreamingId(), "", "comment", "");
            viewModel.liveStramCommentAdapter.addSingleComment(liveStramComment);
            binding.rvComments.scrollToPosition(0);
            binding.etComment.setText("");
        }
    }

    @Override
    public void onBackPressed() {
        new PopupBuilder(this).showLiveEndPopup(new PopupBuilder.OnMultButtonPopupLister() {
            @Override
            public void onClickCountinue() {
                finish();
            }

            @Override
            public void onClickCancel() {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
        if (player != null) {
            player.release();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        player.setPlayWhenReady(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        player.setPlayWhenReady(false);
    }

    public void onClickReport(View view) {
        if (fakeHost == null) return;
        new BottomSheetReport_option(FakeWatchLiveActivity.this, new BottomSheetReport_option.OnReportedListener() {
            @Override
            public void onReported() {
                new BottomSheetReport_g(FakeWatchLiveActivity.this, fakeHost.getId(), () -> {
                    LayoutInflater inflater = getLayoutInflater();
                    View layout = inflater.inflate(R.layout.toast_layout, (ViewGroup) findViewById(R.id.customtoastlyt));
                    Toast toast = new Toast(getApplicationContext());
                    toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                    toast.setDuration(Toast.LENGTH_LONG);
                    toast.setView(layout);
                    toast.show();

                });
            }

            @Override
            public void onBlocked() {
                finish();
            }
        });

    }

}