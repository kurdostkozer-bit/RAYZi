package com.example.rayzi.utils;

import android.content.Context;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.example.rayzi.R;
import com.google.android.material.tabs.TabLayout;

public class CustomTabLayout extends TabLayout {

    public CustomTabLayout(Context context) {
        super(context);
    }

    public CustomTabLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void addTab(Tab tab, boolean setSelected) {
        super.addTab(tab, setSelected);
        updateTabIcons();
    }

    private void updateTabIcons() {
        for (int i = 0; i < getTabCount(); i++) {
            Tab tab = getTabAt(i);
            if (tab != null && tab.getIcon() != null) {
                ImageView iconView = (ImageView) tab.getCustomView();
                iconView.setColorFilter(getContext().getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);
            }
        }
    }
}

