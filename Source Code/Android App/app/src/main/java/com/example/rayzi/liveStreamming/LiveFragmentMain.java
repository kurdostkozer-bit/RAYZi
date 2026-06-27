package com.example.rayzi.liveStreamming;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableBoolean;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rayzi.R;
import com.example.rayzi.activity.BaseFragment;
import com.example.rayzi.activity.ProfileActivity;
import com.example.rayzi.adapter.DotAdaptr;
import com.example.rayzi.audioLive.HostLiveAudioActivity;
import com.example.rayzi.databinding.FragmentLiveBinding;
import com.example.rayzi.home.adapter.BannerAdapter;
import com.example.rayzi.popups.PopupBuilder;
import com.example.rayzi.retrofit.Const;
import com.example.rayzi.user.SearchActivity;
import com.example.rayzi.viewModel.ViewModelFactory;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;

public class LiveFragmentMain extends BaseFragment {

    FragmentLiveBinding binding;
    BannerAdapter bannerAdapter = new BannerAdapter();
    private LiveFragmentViewModel viewModel;
    String[] liveUserType = new String[]{"All", "NormalLive", "AudioLive", "PkLive"};
    String[] liveUserTypeHeading = new String[]{"Explore", "Live Streming", "Party", "PK Battle"};

    public LiveFragmentMain() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_live, container, false);
        viewModel = ViewModelProviders.of(this, new ViewModelFactory(new LiveFragmentViewModel()).createFor()).get(LiveFragmentViewModel.class);
        binding.setViewModel(viewModel);

        initView();

        return binding.getRoot();
    }

    private void initView() {

        applyGradientToTextView(binding.tvPartyRoom);

        binding.tablayout1.setupWithViewPager(binding.viewPager);
        binding.tablayout1.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                View v = tab.getCustomView();
                if (v != null) {
                    TextView tv = v.findViewById(R.id.tvTab);
                    tv.setTextColor(ContextCompat.getColor(requireActivity(), R.color.white));
                    tv.setTextSize(18);
                    LinearLayout layIndicator = v.findViewById(R.id.layIndicator);
                    layIndicator.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                View v = tab.getCustomView();
                if (v != null) {
                    TextView tv = v.findViewById(R.id.tvTab);
                    tv.setTextColor(ContextCompat.getColor(requireActivity(), R.color.white_50));
                    tv.setTextSize(16);
                    LinearLayout layIndicator = v.findViewById(R.id.layIndicator);
                    layIndicator.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        binding.viewPager.setAdapter(new HomeViewPagerAdapter(getChildFragmentManager(), liveUserType));
        bannerAdapter.addData(sessionManager.getBannerList());
        binding.rvBanner.setAdapter(bannerAdapter);
        new PagerSnapHelper().attachToRecyclerView(binding.rvBanner);
        if (bannerAdapter.getItemCount() >= 2) {
            setupLogicAutoSlider();
        }

        binding.ivProfile.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), ProfileActivity.class));
            getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });

        binding.ivSearch.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), SearchActivity.class));
            doTransition(Const.BOTTOM_TO_UP);
        });

        setTab(liveUserTypeHeading);

        if (!sessionManager.getIsAudioRoomBackground()) {
            binding.layBackgroundAudio.setVisibility(View.GONE);
        } else {
            binding.layBackgroundAudio.setVisibility(View.VISIBLE);
            binding.layBackgroundAudio.setOnClickListener(view -> {
                if (sessionManager.getIsUserBackgroundLive()) {
                    new PopupBuilder(requireActivity()).showSimplePopup(getString(R.string.you_are_currently_in_audioroom_please_exit_room_and_watch_live), getString(R.string.dismiss), new PopupBuilder.OnPopupClickListner() {
                        @Override
                        public void onClickCountinue() {

                        }
                    });
                } else {
                    Intent intent = new Intent(requireActivity(), HostLiveAudioActivity.class);
                    intent.putExtra(Const.DATA, new Gson().toJson(sessionManager.getLiveUserForBackground()));
                    intent.putExtra(Const.PRIVACY, "Public");
                    intent.putExtra("backgroundroom",true);
                    startActivity(intent);
                }
            });
        }

    }

    private void setupLogicAutoSlider() {
        DotAdaptr dotAdapter = new DotAdaptr(bannerAdapter.getItemCount(), R.color.white);
        binding.rvDots.setAdapter(dotAdapter);
        binding.rvBanner.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager myLayoutManager = (LinearLayoutManager) binding.rvBanner.getLayoutManager();
                int scrollPosition = myLayoutManager.findFirstVisibleItemPosition();
                dotAdapter.changeDot(scrollPosition);
            }
        });
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            int pos = 0;
            boolean flag = true;

            @Override
            public void run() {
                if (pos == bannerAdapter.getItemCount() - 1) {
                    flag = false;
                } else if (pos == 0) {
                    flag = true;
                }
                if (flag) {
                    pos++;
                } else {
                    pos--;
                }
                binding.rvBanner.smoothScrollToPosition(pos);
                handler.postDelayed(this, 2000);
            }
        };
        handler.postDelayed(runnable, 2000);
    }

    private void setTab(String[] tab) {
        binding.tablayout1.setTabGravity(TabLayout.GRAVITY_FILL);
        binding.tablayout1.removeAllTabs();
        for (int i = 0; i < tab.length; i++) {
            binding.tablayout1.addTab(binding.tablayout1.newTab().setCustomView(createCustomView(i, tab[i])));
        }
        TabLayout tablayout1 = binding.tablayout1;
        final ViewGroup test = (ViewGroup) (tablayout1.getChildAt(0));//tabs is your Tablayout
        int tabLen = test.getChildCount();

        for (int i = 0; i < tabLen; i++) {
            View v = test.getChildAt(i);
            v.setPadding(10, 0, 10, 0);
        }

    }

    private View createCustomView(int i, String s) {
        View v = LayoutInflater.from(requireActivity()).inflate(R.layout.custom_tabhorizontol, null);
        TextView tv = v.findViewById(R.id.tvTab);
        LinearLayout layIndicator = v.findViewById(R.id.layIndicator);
        tv.setText(s);
        if (i == 0) {
            tv.setTextColor(ContextCompat.getColor(requireActivity(), R.color.white));
            tv.setTextSize(18);
            layIndicator.setVisibility(View.VISIBLE);
        } else {
            tv.setTextColor(ContextCompat.getColor(requireActivity(), R.color.white_50));
        }
        return v;
    }

    public class LiveFragmentViewModel extends ViewModel {
        public ObservableBoolean isLoading = new ObservableBoolean(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!requireActivity().isFinishing()) {
            binding.ivProfile.setUserImage(sessionManager.getUser().getImage(), sessionManager.getUser().getAvatarFrameImage(), 15);
        }
        if (sessionManager.getIsAudioRoomBackground()) {
            binding.layBackgroundAudio.setVisibility(View.VISIBLE);
            binding.layBackgroundAudio.setOnClickListener(view -> {
                if (sessionManager.getIsUserBackgroundLive()) {
                    new PopupBuilder(requireActivity()).showSimplePopup(getString(R.string.you_are_currently_in_audioroom_please_exit_room_and_watch_live), getString(R.string.dismiss), new PopupBuilder.OnPopupClickListner() {
                        @Override
                        public void onClickCountinue() {

                        }
                    });
                } else {
                    Intent intent = new Intent(requireActivity(), HostLiveAudioActivity.class);
                    intent.putExtra(Const.DATA, new Gson().toJson(sessionManager.getLiveUserForBackground()));
                    intent.putExtra(Const.PRIVACY, "Public");
                    startActivity(intent);
                }
            });
        } else {
            binding.layBackgroundAudio.setVisibility(View.GONE);
        }
    }

}