package com.example.rayzi.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.example.rayzi.R;
import com.example.rayzi.databinding.ActivityWebBinding;
import com.example.rayzi.retrofit.Const;

public class WebActivity extends BaseActivity {

    ActivityWebBinding binding;
    String website;
    String title;
    private boolean loadingFinished;
    private boolean redirect;
    boolean isToolbar;

    public static void open(Context context, String title, String url, boolean isToolbar) {
        context.startActivity(new Intent(context, WebActivity.class).putExtra(Const.TITLE, title).putExtra(Const.URL, url).putExtra("isToolbar", isToolbar));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_web);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            getWindow().getInsetsController().setSystemBarsAppearance(WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS, WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS);
        }

        binding.pd.setVisibility(View.VISIBLE);

        Intent intent = getIntent();
        if (intent != null) {
            website = intent.getStringExtra(Const.URL);
            title = intent.getStringExtra(Const.TITLE);
            isToolbar = intent.getBooleanExtra("isToolbar", false);
            binding.tvtitle.setText(title);

            if (isToolbar) {
                binding.rlTop.setVisibility(View.VISIBLE);
            } else {
                binding.rlTop.setVisibility(View.GONE);
            }

            loadUrl(website);
        }

    }

    private void loadUrl(String url) {
        if (url != null) {
            binding.webview.getSettings().setJavaScriptEnabled(true);
            binding.webview.getSettings().setDomStorageEnabled(true);
            binding.webview.loadUrl(url);

            binding.webview.addJavascriptInterface(new WebAppInterface(this), "Android");

            binding.webview.setWebViewClient(new WebViewClient() {

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String urlNewString) {
                    if (!loadingFinished) {
                        redirect = true;
                    }

                    loadingFinished = false;
                    view.loadUrl(urlNewString);
                    return true;
                }

                @Override
                public void onPageStarted(WebView view, String url, Bitmap facIcon) {
                    loadingFinished = false;
                    //SHOW LOADING IF IT ISNT ALREADY VISIBLE
                    binding.pd.setVisibility(View.VISIBLE);
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    if (!redirect) {
                        loadingFinished = true;
                        binding.pd.setVisibility(View.GONE);
                    }

                    if (loadingFinished && !redirect) {
                        //HIDE LOADING IT HAS FINISHED
                        binding.pd.setVisibility(View.GONE);
                    } else {
                        redirect = false;
                        binding.pd.setVisibility(View.GONE);
                    }

                }
            });

        }

    }

    public class WebAppInterface {
        Context mContext;

        WebAppInterface(Context c) {
            mContext = c;
        }

        @JavascriptInterface
        public void showToast(String toast) {
            finish();
        }

        @JavascriptInterface
        public void showAndroidToast(String toast) {
            Toast.makeText(mContext, toast + "asasa", Toast.LENGTH_SHORT).show();
        }

        @JavascriptInterface
        public void ok(String toast) {
            Toast.makeText(mContext, toast + getString(R.string.ok), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}