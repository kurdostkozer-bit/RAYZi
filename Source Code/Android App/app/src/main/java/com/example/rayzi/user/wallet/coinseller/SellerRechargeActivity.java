package com.example.rayzi.user.wallet.coinseller;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.rayzi.R;
import com.example.rayzi.databinding.ActivitySellerRechargeBinding;

public class SellerRechargeActivity extends AppCompatActivity {
    ActivitySellerRechargeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_seller_recharge);

        initView();

        binding.ivBack.setOnClickListener(view -> {
            finish();
        });

    }

    private void initView() {
        binding.tabLayout.setupWithViewPager(binding.pager);
        binding.pager.setAdapter(new DemoCollectionPagerAdapter(getSupportFragmentManager()));

        binding.tabLayout.getTabAt(0).setIcon(R.drawable.diamond);
        binding.tabLayout.getTabAt(0).setText( getString(R.string.recharge));
        binding.tabLayout.getTabAt(1).setText(R.string.history);
        binding.tabLayout.getTabAt(1).setIcon(R.drawable.history2);

        int selectedColor = getResources().getColor(R.color.white);
        int unselectedColor = getResources().getColor(R.color.offwhite);
        binding.tabLayout.setTabTextColors(unselectedColor, selectedColor);
    }

    public class DemoCollectionPagerAdapter extends FragmentStatePagerAdapter {


        public DemoCollectionPagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return new SellerRechargeFragment();
            } else {
                return new SellerRechargeHistoryFragment();
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

    }
}