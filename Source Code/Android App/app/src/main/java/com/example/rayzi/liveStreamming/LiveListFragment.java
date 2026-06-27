package com.example.rayzi.liveStreamming;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.rayzi.MainApplication;
import com.example.rayzi.R;
import com.example.rayzi.activity.BaseFragment;
import com.example.rayzi.activity.FakeWatchLiveActivity;
import com.example.rayzi.activity.ProfileActivity;
import com.example.rayzi.audioLive.WatchAudioLiveActivity;
import com.example.rayzi.databinding.FragmentLiveListBinding;
import com.example.rayzi.databinding.ItemPkInviteHostBinding;
import com.example.rayzi.databinding.ItemVideoGridBinding;
import com.example.rayzi.fake.audio.FakeAudioWatchActivity;
import com.example.rayzi.fake.pk.FakeWatchPKLiveActivity;
import com.example.rayzi.pk.HostPKLiveActivity;
import com.example.rayzi.pkRoot.PkAudioLiveUserRoot;
import com.example.rayzi.popups.PopupBuilder;
import com.example.rayzi.retrofit.Const;
import com.example.rayzi.socket.MySocketManager;
import com.example.rayzi.user.SearchActivity;
import com.example.rayzi.utils.FloatingButtonService;
import com.example.rayzi.viewModel.LiveListViewModel;
import com.example.rayzi.viewModel.ViewModelFactory;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;


public class LiveListFragment extends BaseFragment {
    private static final String TAG = "LiveListFragment";
    FragmentLiveListBinding binding;
    private LiveListViewModel viewModel;
    GridLayoutManager gridLayoutManager;
    private String type = "All";
    private boolean isGone = false;
    private boolean onPause = false, onResume = false;

    public LiveListFragment() {
    }

    public LiveListFragment(String type) {
        // Required empty public constructor
        this.type = type;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_live_list, container, false);
        viewModel = new ViewModelProvider(this, new ViewModelFactory(new LiveListViewModel()).createFor()).get(LiveListViewModel.class);

        if (type.equals("AudioLive")) {
            gridLayoutManager = new GridLayoutManager(requireActivity(), 1);
        } else {
            gridLayoutManager = new GridLayoutManager(requireActivity(), 2);
        }
        binding.rvVideos.setLayoutManager(gridLayoutManager);

        return binding.getRoot();
    }

    private void initLister() {

        if (type.equals("AudioLive")) {
            binding.shimmerForParty.setVisibility(View.VISIBLE);
            binding.shimmerForVideo.setVisibility(View.GONE);
        } else {
            binding.shimmerForParty.setVisibility(View.GONE);
            binding.shimmerForVideo.setVisibility(View.VISIBLE);
        }

        binding.swipeRefresh.setOnRefreshListener((refreshLayout) -> {
            viewModel.getData(false);
        });
        binding.swipeRefresh.setOnLoadMoreListener(refreshLayout -> {
            viewModel.getData(true);
        });
        viewModel.isLoadingComplete.observe(requireActivity(), aBoolean -> {
            if (aBoolean) {
                binding.swipeRefresh.finishLoadMore();
                binding.swipeRefresh.finishRefresh();
            }
        });

        binding.ivProfile.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), ProfileActivity.class));
            requireActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });

        binding.ivSearch.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), SearchActivity.class));
            doTransition(Const.BOTTOM_TO_UP);
        });
        viewModel.liveListAdapter.setOnHostClickLister(new LiveListAdapter.OnHostClickLister() {
            @Override
            public void onHostItemClick(PkAudioLiveUserRoot.UsersItem userDummy, ItemVideoGridBinding itemVideoGridBinding, ItemPkInviteHostBinding itemPkInviteHostBinding) {
                Log.d("TAG", "onHostItemClick: userDummy.isAudio()  " + userDummy.isAudio());

                Log.d(TAG, "onHostItemClick: ===" + userDummy.getBlockedUsers());

                if (userDummy.getBlockedUsers() != null) {

                    for (int i = 0; i < userDummy.getBlockedUsers().size(); i++) {
                        if (userDummy.getBlockedUsers().get(i).getBlockedUserId().equals(sessionManager.getUser().getId())) {
                            Toast.makeText(requireActivity(), "You are blocked by Host.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                }


                if (sessionManager.getIsUserBackgroundLive()) {
                    if (userDummy.getLiveUserId() != null) {

                        String currentUserId = userDummy.getLiveUserId();
                        String backgroundLiveChannel = (sessionManager.getUserAudioBgModel() != null) ? sessionManager.getUserAudioBgModel().getLiveUserId() : "";

                        if (!currentUserId.isEmpty() && !backgroundLiveChannel.isEmpty() && !currentUserId.equals(backgroundLiveChannel)) {

                            if (sessionManager.getUser().getImage() != null) {
                                getContext().startService(new Intent(getContext(), FloatingButtonService.class).putExtra("image", sessionManager.getUser().getImage()));
                            }


                            new PopupBuilder(requireActivity()).showSimplePopup(getString(R.string.you_are_currently_in_audioroom_please_exit_room_and_watch_live), getString(R.string.dismiss), new PopupBuilder.OnPopupClickListner() {
                                @Override
                                public void onClickCountinue() {

                                }
                            });
                        }else {
                            if (sessionManager.getIsAudioRoomExit()) {

                                if (userDummy.isAudio() && !userDummy.isIsFake() && !userDummy.isIsPkMode()) {
                                    startActivity(new Intent(requireActivity(), WatchAudioLiveActivity.class).putExtra(Const.DATA, new Gson().toJson(userDummy)));
                                } else if (userDummy.isIsFake() && !userDummy.isAudio() && !userDummy.isIsPkMode()) {
                                    startActivity(new Intent(requireActivity(), FakeWatchLiveActivity.class).putExtra(Const.DATA, new Gson().toJson(userDummy)));
                                } else if (userDummy.isIsFake() && userDummy.isIsPkMode() && !userDummy.isAudio()) {
                                    Log.d(TAG, "onHostItemClick: fake pk");
                                    startActivity(new Intent(requireActivity(), FakeWatchPKLiveActivity.class).putExtra(Const.DATA, new Gson().toJson(userDummy)));
                                } else if (userDummy.isIsFake() && !userDummy.isIsPkMode() && userDummy.isAudio()) {
                                    Log.d(TAG, "onHostItemClick: fake audio");
                                    startActivity(new Intent(requireActivity(), FakeAudioWatchActivity.class).putExtra(Const.DATA, new Gson().toJson(userDummy)));
                                } else {
                                    singleLiveUserEventFire(userDummy);
                                }
                            } else {
                                new PopupBuilder(requireActivity()).showSimplePopup(getString(R.string.you_are_currently_in_audioroom_please_exit_room_and_watch_live), getString(R.string.dismiss), new PopupBuilder.OnPopupClickListner() {
                                    @Override
                                    public void onClickCountinue() {

                                    }
                                });
                            }
                        }
                    }else {


                        if (sessionManager.getUser().getImage() != null) {
                            getContext().startService(new Intent(getContext(), FloatingButtonService.class).putExtra("image", sessionManager.getUser().getImage()));
                        }

                        new PopupBuilder(requireActivity()).showSimplePopup(getString(R.string.you_are_currently_in_audioroom_please_exit_room_and_watch_live), getString(R.string.dismiss), new PopupBuilder.OnPopupClickListner() {
                            @Override
                            public void onClickCountinue() {

                            }
                        });
                    }

                } else {

                    if (sessionManager.getIsAudioRoomExit()) {
                        if (sessionManager.getIsUserBackgroundLive()) {
                            JsonObject jsonObject = new JsonObject();
                            jsonObject.addProperty("userId", sessionManager.getUser().getId());
                            jsonObject.addProperty("liveUserMongoId", sessionManager.getUserAudioBgModel().getId());
                            jsonObject.addProperty("liveStreamingId", sessionManager.getUserAudioBgModel().getLiveStreamingId());

                            MySocketManager.getInstance().getSocket().emit(Const.EVENT_LESS_PARTICIPATED, jsonObject);

                            ((MainApplication) requireActivity().getApplication()).rtcEngine().leaveChannel();
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
                        if (userDummy.isAudio() && !userDummy.isIsFake() && !userDummy.isIsPkMode()) {
                            startActivity(new Intent(requireActivity(), WatchAudioLiveActivity.class).putExtra(Const.DATA, new Gson().toJson(userDummy)));
                        } else if (userDummy.isIsFake() && !userDummy.isAudio() && !userDummy.isIsPkMode()) {
                            startActivity(new Intent(requireActivity(), FakeWatchLiveActivity.class).putExtra(Const.DATA, new Gson().toJson(userDummy)));
                        } else if (userDummy.isIsFake() && userDummy.isIsPkMode() && !userDummy.isAudio()) {
                            Log.d(TAG, "onHostItemClick: fake pk");
                            startActivity(new Intent(requireActivity(), FakeWatchPKLiveActivity.class).putExtra(Const.DATA, new Gson().toJson(userDummy)));
                        } else if (userDummy.isIsFake() && !userDummy.isIsPkMode() && userDummy.isAudio()) {
                            Log.d(TAG, "onHostItemClick: fake audio");
                            startActivity(new Intent(requireActivity(), FakeAudioWatchActivity.class).putExtra(Const.DATA, new Gson().toJson(userDummy)));
                        } else {
                            singleLiveUserEventFire(userDummy);
                        }
                    } else {
                        new PopupBuilder(requireActivity()).showSimplePopup(getString(R.string.you_are_currently_in_audioroom_please_exit_room_and_watch_live), getString(R.string.dismiss), new PopupBuilder.OnPopupClickListner() {
                            @Override
                            public void onClickCountinue() {

                            }
                        });
                    }

                }
            }
        });
    }

    private void singleLiveUserEventFire(PkAudioLiveUserRoot.UsersItem liveUser) {
        customDialogClass.show();
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userId", liveUser.getLiveUserId());
            jsonObject.put("joinUserId", sessionManager.getUser().getId());
            jsonObject.put("liveStreamingId", liveUser.getLiveStreamingId());
            jsonObject.put("type", (liveUser.isAudio()) ? "audio" : "other");
            Log.d(TAG, "singleLiveUserEventFire: =======" + liveUser.getLiveStreamingId());
            MySocketManager.getInstance().getSocket().emit("singleLiveUser", jsonObject);
            Log.d(TAG, "singleLiveUserEventFire: singleLiveUser emitted");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        MySocketManager.getInstance().getSocket().once(Const.DUMMY, args -> {
            requireActivity().runOnUiThread(() -> {
                if (args[0] != null) {
                    // Log.d(TAG, "run: SIngle Live user getted  :" + args[0].toString());
                    Log.d(TAG, "singleLiveUserEventFire: " + args[0].toString());

                    PkAudioLiveUserRoot.UsersItem socketLiveUser = new Gson().fromJson(args[0].toString(), PkAudioLiveUserRoot.UsersItem.class);
                    if (!isGone) {
                        isGone = true;
                        Log.d(TAG, "singleLiveUserEventFire: ====" + socketLiveUser.getLiveStreamingId());
                        LiveListFragment.this.startActivity(new Intent(LiveListFragment.this.getActivity(), HostPKLiveActivity.class).putExtra(Const.ISHOST, false).putExtra(Const.DATA, new Gson().toJson(socketLiveUser)));
                        customDialogClass.dismiss();
                    }
                    Log.d("TAG", "onHostItemClick: userDummy.isPkView() =============== " + socketLiveUser.isIsPkMode());
                }
                customDialogClass.dismiss();
            });
        });
        MySocketManager.getInstance().getSocket().once(Const.IS_LIVE_USER, args -> {
            requireActivity().runOnUiThread(() -> {
                if (args[0] != null) {
                    Log.d(TAG, "singleLiveUserEventFire: else ma jay che ==== ");
                    if (!requireActivity().isFinishing()) {
                        Toast.makeText(requireActivity(), liveUser.getName() + getString(R.string.s_live_has_ended), Toast.LENGTH_SHORT).show();
                        viewModel.getData(false);
                    }
                }
            });
            customDialogClass.dismiss();
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        initView();
        initLister();
    }

    @Override
    public void onPause() {
        super.onPause();
        onPause = true;
    }

    private void initView() {
        isGone = false;
        binding.setViewModel(viewModel);
        viewModel.init(requireContext(), type);
        viewModel.getData(false);
        if (!requireActivity().isFinishing()) {
            binding.ivProfile.setUserImage(sessionManager.getUser().getImage(), sessionManager.getUser().getAvatarFrameImage(), 15);
        }
        int resId = R.anim.layout_anim_scale_in;
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getContext(), resId);
        binding.rvVideos.setLayoutAnimation(animation);
        viewModel.liveListAdapter.notifyDataSetChanged();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}