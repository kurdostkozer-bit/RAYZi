package com.example.rayzi.user;

import static android.provider.MediaStore.MediaColumns.DATA;

import android.Manifest;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.example.rayzi.BuildConfig;
import com.example.rayzi.MainApplication;
import com.example.rayzi.R;
import com.example.rayzi.activity.BaseActivity;
import com.example.rayzi.activity.MainActivity;
import com.example.rayzi.databinding.ActivityEditProfile1Binding;

import com.example.rayzi.modelclass.RestResponse;
import com.example.rayzi.modelclass.UserRoot;
import com.example.rayzi.retrofit.Const;
import com.example.rayzi.retrofit.RetrofitBuilder;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends BaseActivity {
    private static final int GALLERY_CODE = 1001;
    private static final int GALLERY_CODE_1 = 1002;
    private static final int PERMISSION_REQUEST_CODE = 111;
    private static final String TAG = "Editprofileact";
    ActivityEditProfile1Binding binding;
    boolean isValidUserName = false;
    String nameS, usernameS;
    private String gender = "";
    private String picturePath = "";
    UserRoot.User userDummy;
    private String coverPhotoPath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_profile_1);

        binding.pd1.setVisibility(View.GONE);
        userDummy = sessionManager.getUser();
        Glide.with(this).load(userDummy.getImage())
                .centerCrop().into(binding.imgUser);

        Glide.with(this).load(userDummy.getCoverImage())
                .centerCrop().into(binding.ivCoverPhoto);

        binding.etName.setText(userDummy.getName());
        binding.etBio.setText(userDummy.getBio());
        binding.etUserName.setText(userDummy.getUniqueId());

        binding.lytMale.setOnClickListener(v -> onMaleClick());
        binding.lytFemale.setOnClickListener(v -> onFeMaleClick());

        if (userDummy.getGender().equalsIgnoreCase(Const.MALE)) {
            onMaleClick();
            Log.d(TAG, "onCreate: select gender = male");
        } else if (userDummy.getGender().equalsIgnoreCase(Const.FEMALE)) {
            onFeMaleClick();
            Log.d(TAG, "onCreate: select gender = female");
        } else {
            onMaleClick();
            Log.d(TAG, "onCreate: select gender = other male");
        }
        if (isRTL(this)) {
            binding.back.setScaleX(isRTL(this) ? -1 : 1);
        }

        gender = Const.MALE;
        binding.etUserName.setText(userDummy.getUniqueId());

//        isValidUserName = !userDummy.getUsername().isEmpty();
//        Log.d(TAG, "checkDetails: " + isValidUserName + "  " + gender);
//        if (userDummy != null && userDummy.getUsername() != null && !userDummy.getUsername().isEmpty()) {
//            binding.etUserName.setText(userDummy.getUsername());
//            isValidUserName = true;
//            // binding.etUserName.setEnabled(false);
//        }
//
//        if (userDummy.getUsername() != null && !userDummy.getUsername().isEmpty()) {
//
//            isValidUserName = true;
//            //  binding.etUserName.setEnabled(false);
//        }
        binding.imgUser.setOnClickListener(v -> choosePhoto(GALLERY_CODE));
        binding.btnPencil.setOnClickListener(v -> choosePhoto(GALLERY_CODE));
        binding.ivCoverPhotoEdit.setOnClickListener(v -> choosePhoto(GALLERY_CODE_1));

//        binding.etUserName.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
////                checkValidation(s.toString());
//                usernameS = s.toString();
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
////                if (!usernameS.isEmpty()) {
////                  checkDetails();
////                }
//            }
//        });

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        binding.etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                nameS = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

//        binding.etAge.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if (s.toString().isEmpty()) {
//                    binding.etAge.setError("Enter Correct Age");
//                    return;
//                }
//                int age = Integer.parseInt(s.toString());
//                if (age < 18 || age > 105) {
//                    binding.etAge.setError("Minimum age must be 18 years.");
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });

        binding.tvSubmit.setOnClickListener(v -> {
            String name = binding.etName.getText().toString().trim().toString();
           /* if (!binding.etName.getText().toString().trim().isEmpty()) {

                 name = name.substring(0, 1).toUpperCase() + name.substring(1);

              //  name = binding.etName.getText().toString().trim().substring(0, 1).toUpperCase(Locale.ROOT) + binding.etName.getText().toString().trim().substring(1, binding.etName.getText().length());
            } else {
                name = binding.etName.getText().toString();
            }*/

            String userName1 = binding.etUserName.getText().toString().trim();
            String bio = binding.etBio.getText().toString().trim();

            if (name.trim().isEmpty()) {
                Toast.makeText(this, R.string.enter_your_name_first , Toast.LENGTH_SHORT).show();
                return;
            } else {
                name = name.substring(0, 1).toUpperCase() + name.substring(1);
            }

            if (userName1.isEmpty()) {
                Toast.makeText(this, R.string.enter_username_first , Toast.LENGTH_SHORT).show();
                return;
            }
            if (gender.isEmpty()) {
                Toast.makeText(this, R.string.select_your_gender , Toast.LENGTH_SHORT).show();
                return;
            }

            int age = binding.etAge.getValue();
            if (age < 18 || age > 105) {
                Toast.makeText(this, R.string.minimum_age_must_be_18_years , Toast.LENGTH_SHORT).show();
                return;
            }

            customDialogClass.show();
            HashMap<String, RequestBody> map = new HashMap<>();

            MultipartBody.Part body = null;
            if (picturePath != null && !picturePath.isEmpty()) {
                File file = new File(picturePath);
                RequestBody requestFile =
                        RequestBody.create(MediaType.parse("multipart/form-data"), file);
                body = MultipartBody.Part.createFormData("image", file.getName(), requestFile);
            }

            MultipartBody.Part body1 = null;
            if (coverPhotoPath != null && !coverPhotoPath.isEmpty()) {
                File file = new File(coverPhotoPath);
                RequestBody requestFile =
                        RequestBody.create(MediaType.parse("multipart/form-data"), file);
                body1 = MultipartBody.Part.createFormData("coverImage", file.getName(), requestFile);
            }

            RequestBody bodyUserid = RequestBody.create(MediaType.parse("text/plain"), sessionManager.getUser().getId());
            RequestBody bodyName = RequestBody.create(MediaType.parse("text/plain"), name);
            RequestBody bodyGender = RequestBody.create(MediaType.parse("text/plain"), gender);
            // RequestBody bodyEmail = RequestBody.create(MediaType.parse("text/plain"), userDummy.getEmail());
            RequestBody bodyUserName = RequestBody.create(MediaType.parse("text/plain"), userName1);
            RequestBody bodyBio = RequestBody.create(MediaType.parse("text/plain"), bio);

            RequestBody bodyAge = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(age));

            map.put("name", bodyName);
            map.put("username", bodyUserName);
            map.put("bio", bodyBio);
            map.put("userId", bodyUserid);
            map.put("gender", bodyGender);
            map.put("age", bodyAge);

            Call<UserRoot> call = RetrofitBuilder.create().updateUser(map, body, body1);
            call.enqueue(new Callback<>() {
                @Override
                public void onResponse(Call<UserRoot> call, Response<UserRoot> response) {
                    if (response.code() == 200) {
                        if (response.body().isStatus()) {
                            sessionManager.saveUser(response.body().getUser());
                            sessionManager.saveBooleanValue(Const.ISLOGIN, true);
                            startActivity(new Intent(EditProfileActivity.this, MainActivity.class));
                            finishAffinity();
                        } else {
                            Toast.makeText(EditProfileActivity.this, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                    customDialogClass.dismiss();
                }

                @Override
                public void onFailure(Call<UserRoot> call, Throwable t) {
                    t.printStackTrace();
                }
            });

        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void choosePhoto(int galleryCode) {
        List<String> strings;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            strings = Arrays.asList(Manifest.permission.READ_MEDIA_IMAGES);
        } else {
            strings = Arrays.asList(Manifest.permission.READ_EXTERNAL_STORAGE);
        }

        requestPermissionIfNeeded(strings, (allGranted, grantedList, deniedList) -> {
            if (allGranted) {
                Intent i = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, galleryCode);
            } else finishAffinity();
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_CODE && resultCode == RESULT_OK && null != data) {

            Uri selectedImage = data.getData();
            Log.d(TAG, "onActivityResult: " + selectedImage);

            Glide.with(this)
                    .load(selectedImage)
                    .into(binding.imgUser);
            String[] filePathColumn = {DATA};

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            picturePath = cursor.getString(columnIndex);
            cursor.close();

        } else if (requestCode == GALLERY_CODE_1 && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            Log.d(TAG, "onActivityResult: " + selectedImage);

            Glide.with(this)
                    .load(selectedImage)
                    .centerCrop()
                    .into(binding.ivCoverPhoto);
            String[] filePathColumn = {DATA};

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            coverPhotoPath = cursor.getString(columnIndex);
            cursor.close();
        }
    }

    private void onFeMaleClick() {
        gender = Const.FEMALE;
        binding.tvFemale.setTextColor(ContextCompat.getColor(this, R.color.white));
        binding.tvMale.setTextColor(ContextCompat.getColor(this, R.color.white_60));
        binding.lytFemale.setBackground(ContextCompat.getDrawable(this, R.drawable.male_selected));
        binding.lytMale.setBackground(ContextCompat.getDrawable(this, R.drawable.male_unselected));
        binding.imgfemale.setColorFilter(ContextCompat.getColor(this, R.color.white));
        binding.imgmale.setColorFilter(ContextCompat.getColor(this, R.color.white_60));
    }

    private void onMaleClick() {
        gender = Const.MALE;
        binding.tvMale.setTextColor(ContextCompat.getColor(this, R.color.white));
        binding.tvFemale.setTextColor(ContextCompat.getColor(this, R.color.white_60));
        binding.lytMale.setBackground(ContextCompat.getDrawable(this, R.drawable.male_selected));
        binding.lytFemale.setBackground(ContextCompat.getDrawable(this, R.drawable.male_unselected));
        binding.imgmale.setColorFilter(ContextCompat.getColor(this, R.color.white));
        binding.imgfemale.setColorFilter(ContextCompat.getColor(this, R.color.white_60));
    }
}