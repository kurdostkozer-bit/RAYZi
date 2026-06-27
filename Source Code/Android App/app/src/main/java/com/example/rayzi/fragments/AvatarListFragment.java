package com.example.rayzi.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.example.rayzi.R;
import com.example.rayzi.activity.BaseFragment;
import com.example.rayzi.databinding.FragmentAvatarListBinding;
import com.example.rayzi.viewModel.AvatarViewModel;
import com.example.rayzi.viewModel.ViewModelFactory;

public class AvatarListFragment extends BaseFragment {
    private static final String TAG = "AvatarListFragment";
    FragmentAvatarListBinding binding;
    private AvatarViewModel viewModel;

    public AvatarListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_avatar_list, container, false);
        viewModel = ViewModelProviders.of(this, new ViewModelFactory(new AvatarViewModel()).createFor()).get(AvatarViewModel.class);
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        initView();
        initListener();
    }

    private void initListener() {
        viewModel.avatarListAdapter.setOnAvatarClickListener((svgaItem, binding1) -> {
            if (!svgaItem.isIsPurchase()) {
                if (svgaItem.getDiamond() <= sessionManager.getUser().getDiamond()) {
                    viewModel.purchaseSvga(svgaItem.getId(), svgaItem.getType(), binding1, svgaItem);
                    customDialogClass.show();
                } else {
                    Toast.makeText(requireActivity(), R.string.you_don_t_have_required_diamonds, Toast.LENGTH_SHORT).show();
                }
            } else if (svgaItem.isIsPurchase() && !svgaItem.isIsSelected()) {
                viewModel.selectSvga(svgaItem.getId(), svgaItem.getType(), binding1, svgaItem,true);
                customDialogClass.show();
            }else if(svgaItem.isIsPurchase() && svgaItem.isIsSelected()){
                viewModel.selectSvga(svgaItem.getId(), svgaItem.getType(), binding1, svgaItem,false);
                customDialogClass.show();
            }
        });
    }

    private void initView() {
        binding.setViewModel(viewModel);
        viewModel.init(requireActivity());
        binding.setLifecycleOwner(this);
        viewModel.getAvatarList(false);
        viewModel.isLoadingComplete.observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean) {
                binding.swipeRefresh.finishRefresh();
                binding.swipeRefresh.finishLoadMore();
            }
        });
        binding.swipeRefresh.setOnRefreshListener(refreshLayout -> viewModel.getAvatarList(false));
        binding.swipeRefresh.setOnLoadMoreListener(refreshLayout -> viewModel.getAvatarList(true));
        viewModel.isPurchased.observe(getViewLifecycleOwner(), aBoolean -> {
            Log.d(TAG, "initListener: " + aBoolean);
            if (aBoolean) {
                if (customDialogClass != null) {
                    customDialogClass.dismiss();
                }
            }
        });
    }
}