package com.example.rayzi.activity;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.rayzi.R;
import com.example.rayzi.SessionManager;
import com.example.rayzi.dilog.CustomDialogClass;
import com.example.rayzi.retrofit.Const;
import com.google.android.exoplayer2.SimpleExoPlayer;

import java.util.Objects;

public abstract class BaseFragment extends Fragment {
    public SessionManager sessionManager;
    public SimpleExoPlayer player;
    public CustomDialogClass customDialogClass;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionManager = new SessionManager(requireActivity());
        customDialogClass = new CustomDialogClass(requireContext(), R.style.customStyle);
        customDialogClass.setCancelable(false);
        customDialogClass.setCanceledOnTouchOutside(false);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    public static boolean isRTL(Context context) {

        Configuration config = context.getResources().getConfiguration();
        if (config.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL) {
            return true;
        } else {
            return false;
        }
    }

    public void doTransition(int type) {

        if (getActivity() != null) {
            if (type == Const.BOTTOM_TO_UP) {

                getActivity().overridePendingTransition(R.anim.enter_from_bottom, R.anim.exit_none);
            } else if (type == Const.UP_TO_BOTTOM) {
                getActivity().overridePendingTransition(R.anim.exit_none, R.anim.enter_from_up);

            }

        }
    }


    public void applyGradientToTextView(TextView textView) {
        Paint paint = textView.getPaint();
        float width = paint.measureText(textView.getText().toString());

        Shader textShader = new LinearGradient(
                0f, 0f, width, textView.getTextSize(),
                new int[] {
                        ContextCompat.getColor(requireActivity(), R.color.party_gradient_1),
                        ContextCompat.getColor(requireActivity(), R.color.party_gradient_2)
                },
                null,
                Shader.TileMode.CLAMP
        );
        textView.getPaint().setShader(textShader);
    }

}
