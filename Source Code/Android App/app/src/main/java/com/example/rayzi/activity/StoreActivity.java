package com.example.rayzi.activity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.DataBindingUtil;

import com.example.rayzi.R;
import com.example.rayzi.adapter.LiveViewPagerAdapter;
import com.example.rayzi.databinding.ActivityStoreBinding;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class StoreActivity extends BaseActivity {
    private ActivityStoreBinding binding;
    private List<String> categories = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_store);
        initView();
        initListner();
    }

    private void initListner() {
        binding.backBtn.setOnClickListener(view -> finish());
    }

    private void initView() {
        binding.viewPager.setAdapter(new LiveViewPagerAdapter(getSupportFragmentManager()));
        binding.tabLayout.setupWithViewPager(binding.viewPager);
        categories.add(getString(R.string.admission_car));
        categories.add(getString(R.string.avatar_frame));
        settab(categories);
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                View v = tab.getCustomView();
                if (v != null) {
                    TextView tv = (TextView) v.findViewById(R.id.tvPaymentName);
                    tv.setTextColor(ContextCompat.getColor(StoreActivity.this, R.color.tabSelected));
                    Typeface typeface = ResourcesCompat.getFont(StoreActivity.this, R.font.abold);
                    tv.setTypeface(typeface);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                View v = tab.getCustomView();
                if (v != null) {
                    TextView tv = (TextView) v.findViewById(R.id.tvPaymentName);
                    tv.setTextColor(ContextCompat.getColor(StoreActivity.this, R.color.tabDeselected));
                    Typeface typeface = ResourcesCompat.getFont(StoreActivity.this, R.font.aregular);
                    tv.setTypeface(typeface);
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    private void settab(List<String> contry) {
        binding.tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        binding.tabLayout.removeAllTabs();
        for (int i = 0; i < contry.size(); i++) {
            binding.tabLayout.addTab(binding.tabLayout.newTab().setCustomView(createCustomView(i, contry.get(i), contry)));
        }
        TabLayout tabLayout = binding.tabLayout;
        int betweenSpace = 0;
        binding.tabLayout.setScrollPosition(0, 0, true);
        final ViewGroup test = (ViewGroup) (tabLayout.getChildAt(0));//tabs is your Tablayout
        int tabLen = test.getChildCount();

        TabLayout.Tab tab = tabLayout.getTabAt(0); // Count Starts From 0
        tab.select();
        for (int i = 0; i < tabLen; i++) {
            View v = test.getChildAt(i);
            v.setPadding(0, 0, 0, 0);
        }


    }

    private View createCustomView(int i, String s, List<String> contry) {

        View v = LayoutInflater.from(this).inflate(R.layout.item_categories_live_tab, null);

        TextView tv = (TextView) v.findViewById(R.id.tvPaymentName);
        LinearLayout linearLayout = (LinearLayout) v.findViewById(R.id.mainLL);

        tv.setText(s);
        if (i == 0) {
            tv.setTextColor(ContextCompat.getColor(this, R.color.tabSelected));
        } else {
            tv.setTextColor(ContextCompat.getColor(this, R.color.tabDeselected));
        }
        return v;

    }

}