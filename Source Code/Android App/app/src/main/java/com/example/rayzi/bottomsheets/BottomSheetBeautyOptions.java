package com.example.rayzi.bottomsheets;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.Spinner;

import com.example.rayzi.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.Objects;

import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.BeautyOptions;

public class BottomSheetBeautyOptions {

    private final BottomSheetDialog bottomSheetDialog;
    private final Context context;
    private final RtcEngine rtcEngine;
    private int lighteningContrast;

    public BottomSheetBeautyOptions(Context context, RtcEngine rtcEngine) {
        this.context = context;
        this.rtcEngine = rtcEngine;

        bottomSheetDialog = new BottomSheetDialog(context, R.style.CustomBottomSheetDialogTheme);
        Objects.requireNonNull(bottomSheetDialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);

        bottomSheetDialog.setOnShowListener(dialog -> {
            BottomSheetDialog d = (BottomSheetDialog) dialog;
            FrameLayout bottomSheet = d.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            BottomSheetBehavior.from(bottomSheet)
                    .setState(BottomSheetBehavior.STATE_EXPANDED);
        });
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_beautyoptions);


        setupSeekBars();
    }

    private void setupSeekBars() {
        SeekBar lighteningLevelSeekBar = bottomSheetDialog.findViewById(R.id.seekBarLighteningLevel);
        SeekBar smoothnessLevelSeekBar = bottomSheetDialog.findViewById(R.id.seekBarSmoothnessLevel);
        SeekBar rednessLevelSeekBar = bottomSheetDialog.findViewById(R.id.seekBarRednessLevel);
        SeekBar sharpnessLevelSeekBar = bottomSheetDialog.findViewById(R.id.seekBarSharpnessLevel);

        SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateBeautyOptions(lighteningLevelSeekBar.getProgress(), smoothnessLevelSeekBar.getProgress(), rednessLevelSeekBar.getProgress(), sharpnessLevelSeekBar.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        };

        if (lighteningLevelSeekBar != null)
            lighteningLevelSeekBar.setOnSeekBarChangeListener(seekBarChangeListener);
        if (smoothnessLevelSeekBar != null)
            smoothnessLevelSeekBar.setOnSeekBarChangeListener(seekBarChangeListener);
        if (rednessLevelSeekBar != null)
            rednessLevelSeekBar.setOnSeekBarChangeListener(seekBarChangeListener);
        if (sharpnessLevelSeekBar != null)
            sharpnessLevelSeekBar.setOnSeekBarChangeListener(seekBarChangeListener);


        Spinner spinner = bottomSheetDialog.findViewById(R.id.spinnerLighteningContrast);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context, R.array.lightening_contrast_options, R.layout.spinner_item_1);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                switch (position) {
                    case 0:
                        lighteningContrast = BeautyOptions.LIGHTENING_CONTRAST_LOW;
                        break;
                    case 1:
                        lighteningContrast = BeautyOptions.LIGHTENING_CONTRAST_NORMAL;
                        break;
                    case 2:
                        lighteningContrast = BeautyOptions.LIGHTENING_CONTRAST_HIGH;
                        break;
                }
                updateBeautyOptions(
                        lighteningLevelSeekBar.getProgress(),
                        smoothnessLevelSeekBar.getProgress(),
                        rednessLevelSeekBar.getProgress(),
                        sharpnessLevelSeekBar.getProgress()
                );
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Default to NORMAL if nothing is selected
                lighteningContrast = BeautyOptions.LIGHTENING_CONTRAST_NORMAL;
            }
        });
    }

    private void updateBeautyOptions(int lightening, int smoothness, int redness, int sharpness) {
        float lighteningLevel = lightening / 100.0f;
        float smoothnessLevel = smoothness / 100.0f;
        float rednessLevel = redness / 100.0f;
        float sharpnessLevel = sharpness / 100.0f;
        BeautyOptions beautyOptions = new BeautyOptions(lighteningContrast, lighteningLevel, smoothnessLevel, rednessLevel, sharpnessLevel);
        rtcEngine.setBeautyEffectOptions(true, beautyOptions);
    }

    public void show() {
        bottomSheetDialog.show();
    }

    public void dismiss() {
        bottomSheetDialog.dismiss();
    }
}