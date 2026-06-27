package com.example.rayzi.utils.layout;

import android.content.Context;
import android.graphics.Outline;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.FrameLayout;

public class RoundedCornerFrameLayout extends FrameLayout {
    public RoundedCornerFrameLayout(Context context) {
        super(context);
        setClipToOutline(true);
        setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                float radius = 30.0f; // Adjust this value based on your preference
                outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), radius);

            }
        });
    }
}