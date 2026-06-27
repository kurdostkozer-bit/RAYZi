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
import com.example.rayzi.adapter.SvgaListAdapter;
import com.example.rayzi.databinding.FragmentSvgaListBinding;
import com.example.rayzi.databinding.ItemSvgaListBinding;
import com.example.rayzi.modelclass.SvgaListRoot;
import com.example.rayzi.popups.PopupSvgaPreview;
import com.example.rayzi.retrofit.Const;
import com.example.rayzi.viewModel.SvgaViewModel;
import com.example.rayzi.viewModel.ViewModelFactory;

public class SvgaListFragment extends BaseFragment {
    private static final String TAG = "SvgaListFragment";
    FragmentSvgaListBinding binding;
    private SvgaViewModel viewModel;

    public SvgaListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_svga_list, container, false);
        viewModel = ViewModelProviders.of(this, new ViewModelFactory(new SvgaViewModel()).createFor()).get(SvgaViewModel.class);
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        initView();
        initListener();
    }

    private void initListener() {
        viewModel.svgaListAdapter.setOnSvgaClickListener(new SvgaListAdapter.onSvgaClickListener() {
            @Override
            public void onPurchaseClick(SvgaListRoot.DataItem svgaItem, ItemSvgaListBinding binding) {
                Log.d(TAG, "onPurchaseClick: "+svgaItem.toString());
//                if (!svgaItem.isIsPurchase()) {
//                    viewModel.purchaseSvga(svgaItem.getId(), svgaItem.getType(), binding, svgaItem);
//                    customDialogClass.show();
//                } else if (svgaItem.isIsPurchase() && !svgaItem.isIsSelected()) {
//                    viewModel.selectSvga(svgaItem.getId(), svgaItem.getType(), binding, svgaItem);
//                    customDialogClass.show();
//                }

                if (!svgaItem.isIsPurchase()) {
                    if (svgaItem.getDiamond() <= sessionManager.getUser().getDiamond()) {
                        viewModel.purchaseSvga(svgaItem.getId(), svgaItem.getType(), binding, svgaItem);
                        customDialogClass.show();
                    } else {
                        if (!requireActivity().isFinishing()) {
                            Toast.makeText(requireActivity(), getString(R.string.you_don_t_have_required_diamonds), Toast.LENGTH_SHORT).show();
                        }
                    }
                } else if (svgaItem.isIsPurchase() && !svgaItem.isIsSelected()) {
                    viewModel.selectSvga(svgaItem.getId(), svgaItem.getType(), binding, svgaItem,true);
                    customDialogClass.show();
                }else if(svgaItem.isIsPurchase() && svgaItem.isIsSelected()){
                    viewModel.selectSvga(svgaItem.getId(), svgaItem.getType(), binding, svgaItem,false);
                    customDialogClass.show();
                }
            }

            @Override
            public void onSvgaClick(SvgaListRoot.DataItem svgaItem) {
                requireActivity().runOnUiThread(() -> {
                    new PopupSvgaPreview(requireActivity(), svgaItem.getImage(), sessionManager.getUser().getAvatarFrame() != null && !sessionManager.getUser().getAvatarFrame().getImage().isEmpty() ? sessionManager.getUser().getAvatarFrame().getImage() : "", sessionManager.getUser().getImage());
                });
            }
        });
    }

    private void initView() {
        binding.setViewModel(viewModel);
        viewModel.init(requireActivity());
        binding.setLifecycleOwner(this);
        viewModel.getSvgaList(false, Const.SVGA);
        viewModel.isLoadingComplete.observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean) {
                binding.swipeRefresh.finishRefresh();
                binding.swipeRefresh.finishLoadMore();
            }
        });
        binding.swipeRefresh.setOnRefreshListener(refreshLayout -> viewModel.getSvgaList(false, Const.SVGA));
        binding.swipeRefresh.setOnLoadMoreListener(refreshLayout -> viewModel.getSvgaList(true, Const.SVGA));
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