package com.example.rayzi.liveGame.bottomsheet;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.RenderProcessGoneDetail;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.widget.FrameLayout;

import androidx.databinding.DataBindingUtil;

import com.example.rayzi.R;
import com.example.rayzi.SessionManager;
import com.example.rayzi.databinding.BottomSheetGameTeenPattiBinding;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.Objects;


public class BottomSheetGameTeenPatti {
    String TAG = "BottomSheetGame";
    BottomSheetGameTeenPattiBinding binding;
    SessionManager sessionManager;

    @SuppressLint("SetJavaScriptEnabled")
    public BottomSheetGameTeenPatti(Context context, String gameUrl,OnDialogDismissListener onDialogDismissListener) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context, R.style.CustomBottomSheetDialogTheme);
        sessionManager = new SessionManager(context);
        Objects.requireNonNull(bottomSheetDialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        bottomSheetDialog.setOnShowListener(dialog -> {
            BottomSheetDialog d = (BottomSheetDialog) dialog;
            FrameLayout bottomSheet = (FrameLayout) d.findViewById(R.id.design_bottom_sheet);
            assert bottomSheet != null;
            BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
        });
        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.bottom_sheet_game_teen_patti, null, false);
        bottomSheetDialog.setContentView(binding.getRoot());

        binding.loader.setVisibility(View.VISIBLE);
        binding.webViewGame.setWebViewClient(new WebViewClient());
        binding.webViewGame.loadUrl(gameUrl + "?id=" + sessionManager.getUser().getId());
        binding.webViewGame.getSettings().setJavaScriptEnabled(true);
        binding.webViewGame.getSettings().setDomStorageEnabled(true);
        WebView.setWebContentsDebuggingEnabled(true);
        bottomSheetDialog.show();
        binding.closeBtn.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            onDialogDismissListener.onDismiss();
        });
        bottomSheetDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                onDialogDismissListener.onDismiss();
            }
        });
    }

    public class WebViewClient extends android.webkit.WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            binding.loader.setVisibility(View.GONE);
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            Log.d(TAG, "onReceivedError: " + error.getErrorCode());
        }

        @Override
        public boolean onRenderProcessGone(WebView view, RenderProcessGoneDetail detail) {
            Log.d(TAG, "onRenderProcessGone: " + detail.toString());
            return super.onRenderProcessGone(view, detail);
        }
    }

    public interface OnDialogDismissListener {

        void onDismiss();

    }


}
