package com.example.rayzi.demoreels;

import android.content.Context;
import android.widget.MediaController;

public class CustomMediaController extends MediaController {

    public CustomMediaController(Context context) {
        super(context);
        // Override the hide method to do nothing, preventing the MediaController from hiding
    }

    @Override
    public void hide() {
        // Override to do nothing, preventing the MediaController from hiding
    }
}
