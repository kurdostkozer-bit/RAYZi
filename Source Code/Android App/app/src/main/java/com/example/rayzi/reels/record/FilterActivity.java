package com.example.rayzi.reels.record;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.daasuu.gpuv.composer.FillMode;
import com.daasuu.gpuv.composer.GPUMp4Composer;
import com.daasuu.gpuv.egl.filter.GlBrightnessFilter;
import com.daasuu.gpuv.egl.filter.GlExposureFilter;
import com.daasuu.gpuv.egl.filter.GlFilter;
import com.daasuu.gpuv.egl.filter.GlGammaFilter;
import com.daasuu.gpuv.egl.filter.GlGrayScaleFilter;
import com.daasuu.gpuv.egl.filter.GlHazeFilter;
import com.daasuu.gpuv.egl.filter.GlInvertFilter;
import com.daasuu.gpuv.egl.filter.GlMonochromeFilter;
import com.daasuu.gpuv.egl.filter.GlPixelationFilter;
import com.daasuu.gpuv.egl.filter.GlPosterizeFilter;
import com.daasuu.gpuv.egl.filter.GlSepiaFilter;
import com.daasuu.gpuv.egl.filter.GlSharpenFilter;
import com.daasuu.gpuv.egl.filter.GlSolarizeFilter;
import com.daasuu.gpuv.egl.filter.GlVignetteFilter;
import com.daasuu.gpuv.player.GPUPlayerView;
import com.daasuu.mp4compose.filter.GlWatermarkFilter;
import com.example.rayzi.R;
import com.example.rayzi.SharedConstants;
import com.example.rayzi.activity.BaseActivity;
import com.example.rayzi.databinding.ActivityFilterBinding;
import com.example.rayzi.dilog.CustomDialogClass;
import com.example.rayzi.reels.record.workers.VideoFilterWorker;
import com.example.rayzi.utils.BitmapUtil;
import com.example.rayzi.utils.TempUtil;
import com.example.rayzi.utils.VideoUtil;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;

import java.io.File;
import java.util.concurrent.TimeUnit;


public class FilterActivity extends BaseActivity {

    public static final String EXTRA_SONG = "song";
    public static final String EXTRA_VIDEO = "video";
    private static final String TAG = "FilterActivity";

    private FilterActivityViewModel mModel;
    private SimpleExoPlayer mPlayer;
    private GPUPlayerView mPlayerView;
    private String mSong;
    private String mVideo;
    ActivityFilterBinding binding;
    private CustomDialogClass progress;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_filter);
        mModel = new ViewModelProvider(this).get(FilterActivityViewModel.class);
        mSong = getIntent().getStringExtra(EXTRA_SONG);
        mVideo = getIntent().getStringExtra(EXTRA_VIDEO);
        Log.d(TAG, "onCreate:songid " + mSong);

        Bitmap frame = VideoUtil.getFrameAtTime(mVideo, TimeUnit.SECONDS.toMicros(3));
        Bitmap square = BitmapUtil.getSquareThumbnail(frame, 250);
        //noinspection ConstantConditions
        frame.recycle();
        Bitmap rounded = BitmapUtil.addRoundCorners(square, 25);
        square.recycle();
        FilterAdapter adapter = new FilterAdapter(this, rounded);
        adapter.setListener(this::applyFilter);
        RecyclerView filters = findViewById(R.id.filters);
        filters.setAdapter(adapter);


        binding.btnDone.setOnClickListener(v -> submitForFilter());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        File video = new File(mVideo);
        if (!video.delete()) {
            Log.w(TAG, "Could not delete input video: " + video);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPlayerView.onPause();
        mPlayer.setPlayWhenReady(false);
        mPlayer.stop(true);
        mPlayer.release();
        mPlayer = null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPlayer = new SimpleExoPlayer.Builder(this).build();
        mPlayer.setRepeatMode(ExoPlayer.REPEAT_MODE_ALL);

        mPlayer.setThrowsWhenUsingWrongThread(false);

        DefaultDataSourceFactory factory =
                new DefaultDataSourceFactory(this, getString(R.string.app_name));
        ProgressiveMediaSource source = new ProgressiveMediaSource.Factory(factory)
                .createMediaSource(Uri.fromFile(new File(mVideo)));
        mPlayer.setPlayWhenReady(true);
        mPlayer.prepare(source);
        mPlayerView = findViewById(R.id.player);
        mPlayerView.setSimpleExoPlayer(mPlayer);
        mPlayerView.onResume();
    }

    public void applyFilter(VideoFilter filter) {
        Log.d(TAG, "User wants to apply " + filter.name() + " filter.");
        GPUPlayerView player = findViewById(R.id.player);
        switch (mModel.filter = filter) {
            case BRIGHTNESS: {
                GlBrightnessFilter glf = new GlBrightnessFilter();
                glf.setBrightness(0.2f);
                player.setGlFilter(glf);
                break;
            }
            case EXPOSURE:
                player.setGlFilter(new GlExposureFilter());
                break;
            case GAMMA: {
                GlGammaFilter glf = new GlGammaFilter();
                glf.setGamma(2f);
                player.setGlFilter(glf);
                break;
            }
            case GRAYSCALE:
                player.setGlFilter(new GlGrayScaleFilter());
                break;
            case HAZE: {
                GlHazeFilter glf = new GlHazeFilter();
                glf.setSlope(-0.5f);
                player.setGlFilter(glf);
                break;
            }
            case INVERT:
                player.setGlFilter(new GlInvertFilter());
                break;
            case MONOCHROME:
                player.setGlFilter(new GlMonochromeFilter());
                break;
            case PIXELATED: {
                GlPixelationFilter glf = new GlPixelationFilter();
                glf.setPixel(5);
                player.setGlFilter(glf);
                break;
            }
            case POSTERIZE:
                player.setGlFilter(new GlPosterizeFilter());
                break;
            case SEPIA:
                player.setGlFilter(new GlSepiaFilter());
                break;
            case SHARP: {
                GlSharpenFilter glf = new GlSharpenFilter();
                glf.setSharpness(1f);
                player.setGlFilter(glf);
                break;
            }
            case SOLARIZE:
                player.setGlFilter(new GlSolarizeFilter());
                break;
            case VIGNETTE:
                player.setGlFilter(new GlVignetteFilter());
                break;
            default:
                player.setGlFilter(new GlFilter());
                break;
        }
    }

    private void closeFinally(File clip) {

        Intent intent = new Intent(FilterActivity.this, UploadActivity.class);
        intent.putExtra(UploadActivity.EXTRA_SONG, mSong);
        intent.putExtra(UploadActivity.EXTRA_VIDEO, clip.getAbsolutePath());
        startActivity(intent);
        finish();

//        Context context = getApplicationContext();
//
//        File fixed = TempUtil.createNewFile(this, ".mp4");
//
//        Size size = VideoUtil.getDimensions(clip.getAbsolutePath());
//        GlWatermarkFilter watermark =
//                createWatermark(context.getResources(), size.getWidth(), "");
//        Mp4Composer composer = new Mp4Composer(clip.getAbsolutePath(), fixed.getAbsolutePath());
//        composer.filter(watermark);
//        composer.listener(new Mp4Composer.Listener() {
//            @Override
//            public void onProgress(double progress) {
//                Log.d(TAG, "onProgress: ");
//            }
//
//            @Override
//            public void onCompleted() {
//                progress.dismiss();
//                Intent intent = new Intent(FilterActivity.this, UploadActivity.class);
//                intent.putExtra(UploadActivity.EXTRA_SONG, mSong);
//                intent.putExtra(UploadActivity.EXTRA_VIDEO, fixed.getAbsolutePath());
//                startActivity(intent);
//                finish();
//
////                onBackPressed();
////                Log.d(TAG, "MP4 composition has finished. " + fixed.getAbsolutePath());
//            }
//
//            @Override
//            public void onCanceled() {
//                Log.d(TAG, "MP4 composition was cancelled.");
//            }
//
//            @Override
//            public void onFailed(Exception e) {
//                Log.d(TAG, "MP4 composition failed with error.", e);
//            }
//        });
//        composer.videoFormatMimeType(VideoFormatMimeType.AVC);
//        composer.start();


    }


    private GlWatermarkFilter createWatermark(Resources resources, int vw, String username) {
        Bitmap watermark =
                BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher);
        int ow = watermark.getWidth();
        int optimal = (int) (vw * .2);
        if (ow > optimal) {
            int oh = watermark.getHeight();
            float ratio = (float) ow / (float) oh;
            int fw = optimal;
            int fh = optimal;
            if (1 > ratio) {
                fw = (int) ((float) optimal * ratio);
            } else {
                fh = (int) ((float) optimal / ratio);
            }

            Bitmap scaled = Bitmap.createScaledBitmap(watermark, fw, fh, true);
            watermark.recycle();
            watermark = scaled;
        }

        int padding = resources.getDimensionPixelSize(R.dimen.watermark_username_padding);
        if (padding > 0) {
            int sw = watermark.getWidth() + (padding * 2);
            int sh = watermark.getHeight() + (padding * 2);
            Bitmap padded = Bitmap.createBitmap(sw, sh, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(padded);
            Paint paint = new Paint();
            paint.setFilterBitmap(true);
            canvas.drawBitmap(watermark, padding, padding, paint);
            watermark.recycle();
            watermark = padded;
        }

        GlWatermarkFilter.Position position;
        switch (resources.getInteger(R.integer.watermark_position)) {
            case 1:
                position = GlWatermarkFilter.Position.LEFT_TOP;
                break;
            case 2:
                position = GlWatermarkFilter.Position.RIGHT_TOP;
                break;
            case 3:
                position = GlWatermarkFilter.Position.RIGHT_BOTTOM;
                break;
            default:
                position = GlWatermarkFilter.Position.LEFT_BOTTOM;
                break;
        }
        if (resources.getBoolean(R.bool.watermark_username_enabled)) {
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setStyle(Paint.Style.FILL);
            paint.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD));
            paint.setTextAlign(Paint.Align.LEFT);
            paint.setTextSize(resources.getDimension(R.dimen.watermark_username_size));
            Rect rect = new Rect();
            paint.getTextBounds(username, 0, username.length(), rect);
            Size size = new Size(
                    rect.width() + (2 * padding),
                    rect.height() + (2 * padding)
            );
            size = new Size(
                    Math.max(size.getWidth(), watermark.getWidth()),
                    watermark.getHeight() + size.getHeight()
            );
            Bitmap combined =
                    Bitmap.createBitmap(size.getWidth(), size.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(combined);
            boolean right = position == GlWatermarkFilter.Position.RIGHT_TOP ||
                    position == GlWatermarkFilter.Position.RIGHT_BOTTOM;
            if (right) {
                canvas.drawBitmap(watermark, size.getWidth() - watermark.getWidth(), 0f, null);
            } else {
                canvas.drawBitmap(watermark, 0f, 0f, null);
            }

            paint.setColor(Color.BLACK);
            canvas.drawText(
                    username,
                    padding + 1,
                    watermark.getHeight() + padding + 1,
                    paint
            );
            paint.setColor(Color.WHITE);
            canvas.drawText(
                    username,
                    padding,
                    watermark.getHeight() + padding,
                    paint
            );
            watermark.recycle();
            watermark = combined;
        }

        return new GlWatermarkFilter(watermark, position);
    }


    private void submitForFilter() {
        Log.d(TAG, "submitForFilter: ");
        mPlayer.setPlayWhenReady(false);
        progress = new CustomDialogClass(this, R.style.customStyle);
        progress.setCancelable(false);
        progress.show();
        File filtered = TempUtil.createNewFile(this, ".mp4");

        new VideoFilterTask(FilterActivity.this,mModel.filter.name(),mVideo,filtered.getAbsolutePath(),filtered).execute();


//        Data data = new Data.Builder()
//                .putString(VideoFilterWorker.KEY_INPUT, mVideo)
//                .putString(VideoFilterWorker.KEY_OUTPUT, filtered.getAbsolutePath())
//                .putString(VideoFilterWorker.KEY_FILTER, mModel.filter.name())
//                .build();
//        Log.d(TAG, "submitForFilter: " + mModel.filter.name());
//        OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(VideoFilterWorker.class)
//                .setInputData(data)
//                .build();
//        WorkManager wm = WorkManager.getInstance(this);
//        wm.enqueue(request);
//        wm.getWorkInfoByIdLiveData(request.getId())
//                .observe(this, info -> {
//                    boolean ended = info.getState() == WorkInfo.State.CANCELLED
//                            || info.getState() == WorkInfo.State.FAILED;
//                    if (info.getState() == WorkInfo.State.SUCCEEDED) {
////                        progress.dismiss();
//                        closeFinally(filtered);
//                    } else if (ended) {
////                        progress.dismiss();
//                    }
//                });
    }

    public static class FilterActivityViewModel extends ViewModel {

        public VideoFilter filter = VideoFilter.NONE;
    }

    public class VideoFilterTask extends AsyncTask<Void, Void, Boolean> {

        private static final String TAG = "VideoFilterTask";

        private Context context;
        private String filter;
        private String inputPath;
        private String outputPath;
        File filtered;


        public VideoFilterTask(Context context, String filter, String inputPath, String outputPath, File filtered) {
            this.context = context;
            this.filter = filter;
            this.inputPath = inputPath;
            this.outputPath = outputPath;
            this.filtered = filtered;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Show any loading UI if necessary
            progress.show();

        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            File input = new File(inputPath);
            File output = new File(outputPath);
            Size size = VideoUtil.getDimensions(input.getAbsolutePath());
            int width = size.getWidth();
            int height = size.getHeight();
            if (width > SharedConstants.MAX_RESOLUTION || height > SharedConstants.MAX_RESOLUTION) {
                if (width > height) {
                    height = SharedConstants.MAX_RESOLUTION * height / width;
                    width = SharedConstants.MAX_RESOLUTION;
                } else {
                    width = SharedConstants.MAX_RESOLUTION * width / height;
                    height = SharedConstants.MAX_RESOLUTION;
                }
            }
            if (width % 2 != 0) {
                width += 1;
            }

            if (height % 2 != 0) {
                height += 1;
            }

            Log.v(TAG, "Original: " + width + "x" + height + "px; scaled: " + width + "x" + height + "px");
            GPUMp4Composer composer = new GPUMp4Composer(input.getAbsolutePath(), output.getAbsolutePath());
            composer.videoBitrate((int) (.07 * 30 * width * height));
            composer.fillMode(FillMode.PRESERVE_ASPECT_FIT);
            composer.size(width, height);
            VideoFilter videoFilter = VideoFilter.valueOf(filter);
            Log.d(TAG, "doActualWork: filtername " + videoFilter.name());
            switch (videoFilter) {
                case BRIGHTNESS: {
                    GlBrightnessFilter glf = new GlBrightnessFilter();
                    glf.setBrightness(0.2f);
                    composer.filter(glf);
                    break;
                }
                case EXPOSURE:
                    composer.filter(new GlExposureFilter());
                    break;
                case GAMMA: {
                    GlGammaFilter glf = new GlGammaFilter();
                    glf.setGamma(2f);
                    composer.filter(glf);
                    break;
                }
                case GRAYSCALE:
                    composer.filter(new GlGrayScaleFilter());
                    break;
                case HAZE: {
                    GlHazeFilter glf = new GlHazeFilter();
                    glf.setSlope(-0.5f);
                    composer.filter(glf);
                    break;
                }
                case INVERT:
                    composer.filter(new GlInvertFilter());
                    break;
                case MONOCHROME:
                    composer.filter(new GlMonochromeFilter());
                    break;
                case PIXELATED:
                    composer.filter(new GlPixelationFilter());
                    break;
                case POSTERIZE:
                    composer.filter(new GlPosterizeFilter());
                    break;
                case SEPIA:
                    composer.filter(new GlSepiaFilter());
                    break;
                case SHARP: {
                    GlSharpenFilter glf = new GlSharpenFilter();
                    glf.setSharpness(1f);
                    composer.filter(glf);
                    break;
                }
                case SOLARIZE:
                    composer.filter(new GlSolarizeFilter());
                    break;
                case VIGNETTE:
                    composer.filter(new GlVignetteFilter());
                    break;
                default:
                    break;
            }

            final boolean[] success = {false};
            composer.listener(new GPUMp4Composer.Listener() {
                @Override
                public void onProgress(double progress) {
                    // You can update progress here if needed
                }

                @Override
                public void onCompleted() {
                    Log.d(TAG, "MP4 composition has finished.");
                    success[0] = true;
                }

                @Override
                public void onCanceled() {
                    Log.d(TAG, "MP4 composition was cancelled.");
                    if (!output.delete()) {
                        Log.w(TAG, "Could not delete failed output file: " + output);
                    }
                }

                @Override
                public void onFailed(Exception e) {
                    Log.d(TAG, "MP4 composition failed with error.", e);
                    if (!output.delete()) {
                        Log.w(TAG, "Could not delete failed output file: " + output);
                    }
                }
            });
            composer.start();

            // Wait for the composition to complete
            while (!success[0]) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Log.e(TAG, "Interrupted while waiting for composition to complete.", e);
                    return false;
                }
            }

            return success[0];
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            if (success) {
                closeFinally(filtered);
                // Handle the success case, e.g., proceed to the next step
            } else {
                // Handle the failure case
                Log.e(TAG, "Merging video files failed.");
            }
            progress.dismiss();
            // Hide any loading UI if necessary
        }
    }

}
