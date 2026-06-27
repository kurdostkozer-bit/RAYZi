package com.example.rayzi.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;

import com.example.rayzi.BuildConfig;
import com.example.rayzi.R;
import com.example.rayzi.databinding.ActivitySettingBinding;
import com.example.rayzi.modelclass.RestResponse;
import com.example.rayzi.popups.PopupBuilder;
import com.example.rayzi.retrofit.Const;
import com.example.rayzi.retrofit.RetrofitBuilder;
import com.example.rayzi.socket.MySocketManager;
import com.example.rayzi.user.complain.ComplainListActivity;
import com.example.rayzi.user.complain.CreateComplainActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingActivity extends BaseActivity {
    ActivitySettingBinding binding;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_setting);
        getWindow().setStatusBarColor(Color.parseColor("#39273A"));

        binding.tvVerson.setText("Version : "+ BuildConfig.VERSION_CODE);

        initView();
        initLister();

    }

    private void initView() {

        binding.switchNotification.setChecked(sessionManager.isNotificationOn());

        binding.lytSupport.setOnClickListener(v -> startActivity(new Intent(SettingActivity.this, CreateComplainActivity.class)));
        binding.lytComplains.setOnClickListener(v -> startActivity(new Intent(SettingActivity.this, ComplainListActivity.class)));

        if (isRTL(this)) {
            binding.notification.setGravity(Gravity.END);
            binding.termsofservice.setGravity(Gravity.END);
            binding.privacypolicy.setGravity(Gravity.END);
            binding.aboutus.setGravity(Gravity.END);
            binding.logout.setGravity(Gravity.END);

            binding.backbtn.setScaleX(isRTL(this) ? -1 : 1);
        }
    }

    private void initLister() {

        binding.switchNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                sessionManager.notificationOnOff(b);
            }
        });

        binding.btnLogout.setOnClickListener(v -> {
            new PopupBuilder(this).showReliteDiscardPopup(getString(R.string.are_you_sure_you_want_logout), "", getString(R.string.continue_text), getString(R.string.cancel), () -> {
                GoogleSignInOptions gso = new GoogleSignInOptions.
                        Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).
                        build();

                GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, gso);
                googleSignInClient.signOut();
                sessionManager.saveBooleanValue(Const.ISLOGIN, false);
                sessionManager.ClearAllData();
                MySocketManager.getInstance().getSocket().disconnect();
                Intent intent = new Intent(this, SplashActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                finishAffinity();
                startActivity(intent);
            });


        });
    }

    public void onClickPrivacy(View view) {
        WebActivity.open(this, getString(R.string.privacy_policytext), sessionManager.getSetting().getPrivacyPolicyLink(), true);
    }

    public void onClickAbout(View view) {
        WebActivity.open(this, getString(R.string.about_us), sessionManager.getSetting().getPrivacyPolicyLink(), true);
    }

    public void onClickTerms(View view) {
        WebActivity.open(this, getString(R.string.terms_of_service), sessionManager.getSetting().getPrivacyPolicyLink(), true);
    }
}