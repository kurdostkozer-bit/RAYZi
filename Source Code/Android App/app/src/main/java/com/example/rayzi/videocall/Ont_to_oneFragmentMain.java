package com.example.rayzi.videocall;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LifecycleOwner;

import com.example.rayzi.R;
import com.example.rayzi.activity.BaseFragment;
import com.example.rayzi.databinding.FragmentOntToOneMainBinding;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;


public class Ont_to_oneFragmentMain extends BaseFragment {

    private static final String TAG = "camarafeag";
    FragmentOntToOneMainBinding binding;
    private ProcessCameraProvider cameraProvider;
    private CameraSelector cameraSelector;
    private Preview preview;
    String type = "Male";


    public Ont_to_oneFragmentMain() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_ont_to_one_main, container, false);
        initView();
        initListener();
        return binding.getRoot();
    }

    private void initListener() {

        binding.rbMale.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                type = "Male";
                binding.rbFemale.setChecked(false);
                binding.rbRandom.setChecked(false);
            }
        });

        binding.rbFemale.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                type = "Female";
                binding.rbMale.setChecked(false);
                binding.rbRandom.setChecked(false);
            }
        });

        binding.rbRandom.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                type = "Random";
                binding.rbMale.setChecked(false);
                binding.rbFemale.setChecked(false);
            }
        });

    }

    @SuppressLint("RestrictedApi")
    private void initView() {
        binding.tvCallNow.setOnClickListener(v -> startActivity(new Intent(getActivity(), RandomMatchActivity.class).putExtra("type", type)));
    }

    @Override
    public void onResume() {
        super.onResume();
        new Handler(Looper.getMainLooper()).postDelayed(this::initCamera, 1000);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @SuppressLint("RestrictedApi")
    private void initCamera() {
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.CAMERA}, 1);
        } else {
            try {
                final ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(requireActivity());
                cameraProviderFuture.addListener(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            cameraProvider = cameraProviderFuture.get();
                            cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA;
                            preview = new Preview.Builder().build();
                            preview.setSurfaceProvider(binding.previewView.getSurfaceProvider());
                            cameraProvider.unbindAll();
                            cameraProvider.bindToLifecycle((LifecycleOwner) requireActivity(), cameraSelector, preview);
                        } catch (ExecutionException | InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }, ContextCompat.getMainExecutor(requireActivity()));

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        initCamera();
    }

}