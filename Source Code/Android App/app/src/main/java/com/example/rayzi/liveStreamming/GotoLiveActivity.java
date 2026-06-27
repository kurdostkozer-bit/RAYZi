package com.example.rayzi.liveStreamming;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.Toast;

import androidx.camera.core.AspectRatio;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.CameraX;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LifecycleOwner;

import com.example.rayzi.R;
import com.example.rayzi.activity.BaseActivity;
import com.example.rayzi.databinding.ActivityGotoLiveBinding;
import com.example.rayzi.modelclass.LiveStreamRoot;
import com.example.rayzi.pk.HostPKLiveActivity;
import com.example.rayzi.retrofit.Const;
import com.example.rayzi.retrofit.RetrofitBuilder;
import com.example.rayzi.utils.AutoFitPreviewBuilder;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.Random;
import java.util.concurrent.ExecutionException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GotoLiveActivity extends BaseActivity {
    ActivityGotoLiveBinding binding;
    int front = 1, back = 2;
    int CAMARA = front;
    boolean isPrivate = false;

    private ProcessCameraProvider cameraProvider;
    private CameraSelector cameraSelector;
    private Preview preview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_goto_live);
        //  initCamera();
        initCamera();
        initListner();


    }

    private void initListner() {
//        binding.btnSwitchCamara.setOnClickListener(v -> {
//            if (CAMARA == front) {
//                CAMARA = back;
//                lensFacing = CameraX.LensFacing.BACK;
//            } else {
//                CAMARA = front;
//                lensFacing = CameraX.LensFacing.FRONT;
//            }
//            CameraX.unbindAll();
//            initCamera();
//        });
        binding.lytPrivacy.setOnClickListener(v -> {
            isPrivate = !isPrivate;

            if (isPrivate) {
                binding.imgLock.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.lock));
                binding.tvPrivacy.setText(R.string.private_text);

            } else {
                binding.imgLock.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.unlock));
                binding.tvPrivacy.setText(getString(R.string.public_text));
            }
        });
        binding.btnClose.setOnClickListener(v -> onBackPressed());
        binding.btnLive.setOnClickListener(v -> {
            binding.btnLive.setEnabled(false);
            try {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("userId", sessionManager.getUser().getId());

                jsonObject.addProperty("isPublic", !isPrivate);
                jsonObject.addProperty("channel", sessionManager.getUser().getId());
                Random random = new Random();
                int agoraUID = random.nextInt(999999 - 111111) + 111111;
                jsonObject.addProperty("agoraUID", 0);  // just for unique host int id

                customDialogClass.show();
                Call<LiveStreamRoot> call = RetrofitBuilder.create().makelivestreamUser(jsonObject);
                call.enqueue(new Callback<LiveStreamRoot>() {
                    @Override
                    public void onResponse(Call<LiveStreamRoot> call, Response<LiveStreamRoot> response) {
                        if (response.code() == 200) {
                            if (response.body().isStatus()) {
                                Intent intent = new Intent(GotoLiveActivity.this, HostLiveActivity.class);
                                intent.putExtra(Const.DATA, new Gson().toJson(response.body().getLiveUser()));
                                intent.putExtra(Const.PRIVACY, isPrivate ? "Private" : "Public");
                                startActivity(intent);
                                finish();
                            }
                        }
                        customDialogClass.dismiss();
                        binding.btnLive.setEnabled(true);
                    }

                    @Override
                    public void onFailure(Call<LiveStreamRoot> call, Throwable t) {
                        Log.d(TAG, "onFailure: >>>>>>>>>>>>>  " + t.getMessage());
                        customDialogClass.dismiss();
                        binding.btnLive.setEnabled(true);
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }

        });

        binding.btnPK.setOnClickListener(v -> {
            try {
                binding.btnPK.setEnabled(false);
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("userId", sessionManager.getUser().getId());

                jsonObject.addProperty("isPublic", !isPrivate);
                jsonObject.addProperty("channel", sessionManager.getUser().getId());

                Random random = new Random();
                int agoraUID = random.nextInt(999999 - 111111) + 111111;
                jsonObject.addProperty("agoraUID", 1);  // just for unique host int id

                customDialogClass.show();

                Call<LiveStreamRoot> call = RetrofitBuilder.create().makelivestreamUser(jsonObject);
                call.enqueue(new Callback<LiveStreamRoot>() {
                    @Override
                    public void onResponse(Call<LiveStreamRoot> call, Response<LiveStreamRoot> response) {
                        if (response.code() == 200) {
                            if (response.body().isStatus()) {
                                binding.btnPK.setEnabled(true);
                                Intent intent = new Intent(GotoLiveActivity.this, HostPKLiveActivity.class);
                                intent.putExtra(Const.ISHOST, true);
                                intent.putExtra(Const.DATA, new Gson().toJson(response.body().getLiveUser()));

                                Log.d("<<<<<<agorauid>>>>>>", "onResponse: on click button " + response.body().getLiveUser().getAgoraUID());


                                intent.putExtra(Const.PRIVACY, isPrivate ? "Private" : "Public");
                                startActivity(intent);
                                finish();
                            }
                        }
                        customDialogClass.dismiss();
                        binding.btnPK.setEnabled(true);
                    }

                    @Override
                    public void onFailure(Call<LiveStreamRoot> call, Throwable t) {
                        Log.d(TAG, "onFailure: " + t.getMessage());
                        customDialogClass.dismiss();
                        binding.btnPK.setEnabled(true);
                    }
                });


            } catch (Exception e) {
                e.printStackTrace();
            }

        });


    }

    @SuppressLint("RestrictedApi")
    private void initCamera() {
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO},
                    1);
        } else {

            try {

                final ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

                cameraProviderFuture.addListener(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            cameraProvider = cameraProviderFuture.get();

                            cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA;

                            preview = new Preview.Builder().build();

                            preview.setSurfaceProvider(binding.previewView.getSurfaceProvider());

                            cameraProvider.unbindAll();

                            cameraProvider.bindToLifecycle((LifecycleOwner) GotoLiveActivity.this, cameraSelector, preview);

                        } catch (ExecutionException | InterruptedException e) {
                        }
                    }
                }, ContextCompat.getMainExecutor(this));

            } catch (Exception e) {
                e.printStackTrace();
            }


            initListner();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_SHORT).show();
            return;
        }
        initCamera();

    }
}
