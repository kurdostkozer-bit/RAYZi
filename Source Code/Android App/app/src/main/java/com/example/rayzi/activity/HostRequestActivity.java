package com.example.rayzi.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.rayzi.R;
import com.example.rayzi.SessionManager;
import com.example.rayzi.databinding.ActivityHostRequestBinding;
import com.example.rayzi.modelclass.RestResponse;
import com.example.rayzi.retrofit.Const;
import com.example.rayzi.retrofit.RetrofitBuilder;
import com.permissionx.guolindev.callback.RequestCallback;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HostRequestActivity extends BaseActivity {

    ActivityHostRequestBinding binding;
    SessionManager sessionManager;

    String name, bio, agency = "", mobileNumber , bankDetails;

    String frontImagePath = "", backImagePath = "";


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_host_request);
        sessionManager = new SessionManager(this);


        binding.etName.setText(sessionManager.getUser().getName());
        binding.etBio.setText(sessionManager.getUser().getBio());

        if (getIntent() != null) {
            if (getIntent().getStringExtra(Const.DATA) != null) {
                binding.etAgencyCode.setText(getIntent().getStringExtra(Const.DATA) + "");
            }
        }

        binding.back.setOnClickListener(v -> onBackPressed());


        binding.layFrontPhoto.setOnClickListener(view -> {
            List<String> permisionList;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                permisionList = List.of(Manifest.permission.READ_MEDIA_IMAGES);
            } else {
                permisionList = List.of(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
            requestPermissionIfNeeded(permisionList, new RequestCallback() {
                @Override
                public void onResult(boolean allGranted, @NonNull List<String> grantedList, @NonNull List<String> deniedList) {
                    if (allGranted) {
                        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                        photoPickerIntent.setType("image/*");
                        startActivityForResult(photoPickerIntent, 100);
                    } else {
                        Toast.makeText(HostRequestActivity.this, R.string.please_allow_all_permission_text, Toast.LENGTH_SHORT).show();
                    }
                }
            });

        });

        binding.layBackPhoto.setOnClickListener(view -> {
            List<String> permisionList;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                permisionList = List.of(Manifest.permission.READ_MEDIA_IMAGES);
            } else {
                permisionList = List.of(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
            requestPermissionIfNeeded(permisionList, new RequestCallback() {
                @Override
                public void onResult(boolean allGranted, @NonNull List<String> grantedList, @NonNull List<String> deniedList) {
                    if (allGranted) {
                        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                        photoPickerIntent.setType("image/*");
                        startActivityForResult(photoPickerIntent, 110);
                    } else {
                        Toast.makeText(HostRequestActivity.this,  R.string.please_allow_all_permission_text, Toast.LENGTH_SHORT).show();
                    }
                }
            });

        });


        binding.txtNext.setOnClickListener(v -> {

            name = binding.etName.getText().toString();
            bio = binding.etBio.getText().toString();
            agency = binding.etAgencyCode.getText().toString();
            mobileNumber = binding.etMobileNumber.getText().toString();
            bankDetails = binding.etBankDetails.getText().toString();

            if (name == null || name.isEmpty()) {
                Toast.makeText(this, getString(R.string.enter_name), Toast.LENGTH_SHORT).show();
            } else if (mobileNumber == null || mobileNumber.isEmpty()) {
                Toast.makeText(this, R.string.enter_mobile_number, Toast.LENGTH_SHORT).show();
            } else if (bio == null || bio.isEmpty()) {
                Toast.makeText(this, R.string.enter_bio_text, Toast.LENGTH_SHORT).show();
            } else if (bankDetails == null || bankDetails.isEmpty()) {
                Toast.makeText(this, R.string.enter_your_bank_details, Toast.LENGTH_SHORT).show();
            } else if (frontImagePath.isEmpty()) {
                Toast.makeText(this, R.string.select_personal_photo, Toast.LENGTH_SHORT).show();
            } else {
                addHostRequest();
            }

        });

    }


    private void addHostRequest() {

        binding.pd.setVisibility(View.VISIBLE);

        HashMap<String, RequestBody> map = new HashMap<>();
        MultipartBody.Part body = null;

        File file = new File(frontImagePath);
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        body = MultipartBody.Part.createFormData("profileImage", file.getName(), requestFile);

//        MultipartBody.Part body1 = null;
//        if (backImagePath != null && !backImagePath.isEmpty()) {
//            File file1 = new File(backImagePath);
//            RequestBody requestFile1 = RequestBody.create(MediaType.parse("multipart/form-data"), file1);
//            body1 = MultipartBody.Part.createFormData("document", file1.getName(), requestFile1);
//        }

        RequestBody bodyUserid = RequestBody.create(MediaType.parse("text/plain"), sessionManager.getUser().getId());
        RequestBody bodyName = RequestBody.create(MediaType.parse("text/plain"), name);
        RequestBody bodyMobileNumber = RequestBody.create(MediaType.parse("text/plain"), mobileNumber);
        RequestBody bodyBio = RequestBody.create(MediaType.parse("text/plain"), bio);
        RequestBody bodyBankDetails = RequestBody.create(MediaType.parse("text/plain"), bankDetails);
        RequestBody bodyAgencyCode = RequestBody.create(MediaType.parse("text/plain"), agency);
        map.put("userId", bodyUserid);
        map.put("name", bodyName);
        map.put("mobileNumber", bodyMobileNumber);
        map.put("bio", bodyBio);
        map.put("bankDetails", bodyBankDetails);
        map.put("agencyCode", bodyAgencyCode);

        Call<RestResponse> call = RetrofitBuilder.create().addHostRequest(map, body);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<RestResponse> call, Response<RestResponse> response) {

                if (response.code() == 200) {
                    if (response.body().isStatus()) {
                        Toast.makeText(HostRequestActivity.this, R.string.host_request_send, Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(HostRequestActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }
                binding.pd.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<RestResponse> call, Throwable t) {
                Toast.makeText(HostRequestActivity.this, R.string.try_after_some_time, Toast.LENGTH_SHORT).show();
                binding.pd.setVisibility(View.GONE);
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.transforms(new CenterCrop(), new RoundedCorners(25));
        if (requestCode == 100 && resultCode == RESULT_OK && null != data) {
            frontImagePath = getRealPathFromURI(data.getData());
            binding.ivSelectedImage.setVisibility(View.VISIBLE);
            binding.layUploadFront.setVisibility(View.GONE);
            Glide.with(this).load(frontImagePath).apply(requestOptions).into(binding.ivSelectedImage);
        } else if (requestCode == 110 && resultCode == RESULT_OK && null != data) {
            backImagePath = getRealPathFromURI(data.getData());
            Glide.with(this).load(backImagePath).apply(requestOptions).into(binding.ivSelectedImageBack);
            binding.ivSelectedImageBack.setVisibility(View.VISIBLE);
            binding.layUploadBack.setVisibility(View.GONE);
        }
    }


}