package com.example.rayzi.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.rayzi.fragments.AvatarListFragment;
import com.example.rayzi.fragments.SvgaListFragment;

public class LiveViewPagerAdapter extends FragmentPagerAdapter {
    public LiveViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new SvgaListFragment();
        } else {
            return new AvatarListFragment();
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
