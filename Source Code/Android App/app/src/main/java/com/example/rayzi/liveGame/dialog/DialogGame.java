package com.example.rayzi.liveGame.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.webkit.RenderProcessGoneDetail;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;

import androidx.databinding.DataBindingUtil;

import com.example.rayzi.R;
import com.example.rayzi.SessionManager;
import com.example.rayzi.databinding.DialogGameBinding;

public class DialogGame {
    private static final String TAG = "DialogGame";
    DialogGameBinding binding;

    @SuppressLint("SetJavaScriptEnabled")
    public DialogGame(Context context, String gameUrl,OnDialogDismissListener onDialogDismissListener) {
        if (context != null) {
            SessionManager sessionManager = new SessionManager(context);
            Dialog mBuilder = new Dialog(context);
            mBuilder.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mBuilder.setCancelable(true);
            mBuilder.setCanceledOnTouchOutside(true);
            if (mBuilder.getWindow() != null) {
                mBuilder.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }

            binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.dialog_game, null, false);
            mBuilder.setContentView(binding.getRoot());
            binding.loader.setVisibility(View.VISIBLE);
            binding.webViewGame.setWebViewClient(new WebViewClient());
            binding.webViewGame.loadUrl(gameUrl + "?id=" + sessionManager.getUser().getId());
            binding.webViewGame.getSettings().setJavaScriptEnabled(true);
            binding.webViewGame.getSettings().setDomStorageEnabled(true);
            WebView.setWebContentsDebuggingEnabled(true);
            binding.closeBtn.setOnClickListener(v -> {
                mBuilder.dismiss();
                onDialogDismissListener.onDismiss();
            });
            mBuilder.setOnDismissListener(dialog -> onDialogDismissListener.onDismiss());
            mBuilder.show();
        }

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
