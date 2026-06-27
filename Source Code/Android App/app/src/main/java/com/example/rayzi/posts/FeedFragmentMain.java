package com.example.rayzi.posts;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.viewpager2.widget.ViewPager2;

import com.example.rayzi.R;
import com.example.rayzi.activity.BaseFragment;
import com.example.rayzi.activity.ProfileActivity;
import com.example.rayzi.databinding.FragmentFeedMainBinding;
import com.example.rayzi.utils.MyExoPlayer;

public class FeedFragmentMain extends BaseFragment {
    private static final String TAG = "feedFregmentMain";
    FragmentFeedMainBinding binding;

    public FeedFragmentMain() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_feed_main, container, false);
        initTabLayout();
        initListener();


        Log.d(TAG, "oncreate: call fragment");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "oncreate: player nullll");

                if (MyExoPlayer.getInstance().getPlayer() != null) {
                    Log.d(TAG, "oncreate: player not null 1111");
                    MyExoPlayer.getInstance().getPlayer().setPlayWhenReady(false);
//                    MyExoPlayer.getInstance().getPlayer().stop();
                }

            }
        }, 1000);


        return binding.getRoot();
    }

    private void initListener() {

        binding.ivProfile.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), ProfileActivity.class));
            getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });

        binding.layFeed.setOnClickListener(v -> {
            binding.viewPager.setCurrentItem(0);
            binding.ivFeed.setImageResource(R.drawable.feeds_select);
            binding.ivFeedLine.setVisibility(View.VISIBLE);
            binding.ivVideo.setImageResource(R.drawable.video_unselect);
            binding.ivVideoLine.setVisibility(View.GONE);
        });

        binding.layVideo.setOnClickListener(v -> {
            binding.viewPager.setCurrentItem(1);
            binding.ivFeed.setImageResource(R.drawable.feeds_unselect);
            binding.ivFeedLine.setVisibility(View.GONE);
            binding.ivVideo.setImageResource(R.drawable.video_select);
            binding.ivVideoLine.setVisibility(View.VISIBLE);

            if (MyExoPlayer.getInstance().getPlayer() != null) {
                Log.d(TAG, "oncreate: player not null 2222");
                MyExoPlayer.getInstance().getPlayer().setPlayWhenReady(true);

                binding.viewPager.setCurrentItem(1);
            }

        });

    }

    private void initTabLayout() {

        binding.viewPager.setAdapter(new FeedViewPagerAdapter(getActivity()));
        binding.viewPager.setUserInputEnabled(false);
        binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (position == 0) {
                    binding.ivFeed.setImageResource(R.drawable.feeds_select);
                    binding.ivFeed.setElevation(0f);
                    binding.ivFeedLine.setVisibility(View.VISIBLE);
                    binding.ivVideo.setImageResource(R.drawable.video_unselect);
                    binding.ivVideo.setBackgroundTintList(ColorStateList.valueOf(requireActivity().getColor(R.color.white)));
                    binding.ivVideo.setElevation(1f);
                    binding.ivVideoLine.setVisibility(View.GONE);
                    binding.ivProfile.setVisibility(View.VISIBLE);
                } else if (position == 1) {
                    binding.ivFeed.setImageResource(R.drawable.feeds_unselect);
                    binding.ivFeed.setBackgroundTintList(ColorStateList.valueOf(requireActivity().getColor(R.color.white)));
                    binding.ivFeed.setElevation(1f);
                    binding.ivFeedLine.setVisibility(View.GONE);
                    binding.ivVideo.setImageResource(R.drawable.video_select);
                    binding.ivVideo.setElevation(0f);
                    binding.ivVideoLine.setVisibility(View.VISIBLE);
                    binding.ivProfile.setVisibility(View.GONE);

                    if (MyExoPlayer.getInstance().getPlayer() != null) {
                        Log.d(TAG, "oncreate: player not null 2222");
                        MyExoPlayer.getInstance().getPlayer().setPlayWhenReady(true);

                        binding.viewPager.setCurrentItem(1);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });

        binding.viewPager.setOffscreenPageLimit(ViewPager2.OFFSCREEN_PAGE_LIMIT_DEFAULT);
        binding.viewPager.setCurrentItem(0);
        binding.ivFeed.setImageResource(R.drawable.feeds_select);
        binding.ivFeedLine.setVisibility(View.VISIBLE);
        binding.ivVideo.setImageResource(R.drawable.video_unselect);
        binding.ivVideoLine.setVisibility(View.GONE);

    }

    @Override
    public void onResume() {
        super.onResume();
        binding.ivProfile.setUserImage(sessionManager.getUser().getImage(), sessionManager.getUser().getAvatarFrameImage(), 10);
//        binding.viewPager.setCurrentItem(0);
        if (MyExoPlayer.getInstance().getPlayer() != null) {
            MyExoPlayer.getInstance().getPlayer().setPlayWhenReady(true);
            MyExoPlayer.getInstance().getPlayer().play();
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
        if (MyExoPlayer.getInstance().getPlayer() != null) {
            MyExoPlayer.getInstance().getPlayer().setPlayWhenReady(false);
            MyExoPlayer.getInstance().getPlayer().stop();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }
}