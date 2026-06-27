package com.example.rayzi.reels.record.workers;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.concurrent.futures.CallbackToFutureAdapter;
import androidx.work.ListenableWorker;
import androidx.work.WorkerParameters;

import com.example.rayzi.utils.VideoUtil;
import com.google.common.util.concurrent.ListenableFuture;
import com.otaliastudios.transcoder.Transcoder;
import com.otaliastudios.transcoder.TranscoderListener;
import com.otaliastudios.transcoder.TranscoderOptions;
import com.otaliastudios.transcoder.common.TrackType;
import com.otaliastudios.transcoder.source.BlankAudioDataSource;
import com.otaliastudios.transcoder.source.ClipDataSource;
import com.otaliastudios.transcoder.source.DataSource;
import com.otaliastudios.transcoder.strategy.DefaultAudioStrategy;
import com.otaliastudios.transcoder.strategy.DefaultVideoStrategies;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;


public class MergeAudioVideoWorker2 extends ListenableWorker {

    public static final String KEY_AUDIO = "audio";
    public static final String KEY_OUTPUT = "output";
    public static final String KEY_VIDEO = "video";
    private static final String TAG = "===MergeAudioVideoWorker2";

    public MergeAudioVideoWorker2(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    @SuppressWarnings("ConstantConditions")
    public ListenableFuture<Result> startWork() {
        String audio = getInputData().getString(KEY_AUDIO);
        File video = new File(getInputData().getString(KEY_VIDEO));
        File output = new File(getInputData().getString(KEY_OUTPUT));
        return CallbackToFutureAdapter.getFuture(completer -> {
            doActualWork(video, audio, output, completer);
            return null;
        });
    }

    private void doActualWork(File video, String audio, File output, CallbackToFutureAdapter.Completer<Result> completer) {
        DataSource audioDS = VideoUtil.createDataSource(getApplicationContext(), audio);
        long audioDurationMs;
        boolean usingFallback = false;
        if (audioDS == null) {
            Log.e(TAG, "DataSource is null for audio: " + audio + ". Falling back to MediaMetadataRetriever.");
            usingFallback = true;
            try {
                audioDurationMs = VideoUtil.getDuration(getApplicationContext(), Uri.parse(audio));
                if (audioDurationMs <= 0) {
                    Log.e(TAG, "Fallback failed to retrieve a valid audio duration: " + audio);
                    completer.set(Result.failure());
                    return;
                }
            } catch (IOException ex) {
                Log.e(TAG, "Fallback to MediaMetadataRetriever also failed.", ex);
                completer.set(Result.failure());
                return;
            }
        } else {
            try {
                audioDurationMs = TimeUnit.MICROSECONDS.toMillis(audioDS.getDurationUs());
                if (audioDurationMs <= 0) {
                    throw new IllegalArgumentException("Invalid duration from DataSource.");
                }
            } catch (Exception e) {
                Log.e(TAG, "Failed to get duration from DataSource, falling back.", e);
                try {
                    audioDurationMs = VideoUtil.getDuration(getApplicationContext(), Uri.parse(audio));
                    if (audioDurationMs <= 0) {
                        Log.e(TAG, "Fallback failed: Invalid audio duration.");
                        completer.set(Result.failure());
                        return;
                    }
                    usingFallback = true;
                } catch (IOException ex) {
                    Log.e(TAG, "Fallback to MediaMetadataRetriever also failed.", ex);
                    completer.set(Result.failure());
                    return;
                }
            }
        }
        Log.d(TAG, "Audio duration (ms): " + audioDurationMs);

        // --- Get video duration ---
        long videoDurationMs;
        try {
            videoDurationMs = VideoUtil.getDuration(getApplicationContext(), Uri.fromFile(video));
            if (videoDurationMs <= 0) {
                Log.e(TAG, "Failed to get valid video duration for: " + video.getAbsolutePath());
                completer.set(Result.failure());
                return;
            }
        } catch (IOException e) {
            Log.e(TAG, "Failed to get video duration for: " + video.getAbsolutePath(), e);
            completer.set(Result.failure());
            return;
        }
        Log.d(TAG, "Video duration (ms): " + videoDurationMs);

        // --- Build the transcoder options ---
        TranscoderOptions.Builder transcoder = Transcoder.into(output.getAbsolutePath());

        // Add the video data source (using file path).
        transcoder.addDataSource(TrackType.VIDEO, video.getAbsolutePath());

        // --- Add the audio data source ---
        if (!usingFallback && audioDS != null) {
            if (audioDurationMs > videoDurationMs) {
                // Clip the audio to match video duration.
                Log.d(TAG, "Clipping audio from " + audioDurationMs + "ms to video duration " + videoDurationMs + "ms.");
                DataSource clippedAudio = new ClipDataSource(audioDS, 0, TimeUnit.MILLISECONDS.toMicros(videoDurationMs));
                transcoder.addDataSource(TrackType.AUDIO, clippedAudio);
            } else {
                // Audio is shorter or equal – add the original audio.
                transcoder.addDataSource(TrackType.AUDIO, audioDS);
                if (audioDurationMs < videoDurationMs) {
                    // Optionally, append blank audio to fill the gap.
                    long blankDurationMicros = TimeUnit.MILLISECONDS.toMicros(videoDurationMs - audioDurationMs);
                    Log.d(TAG, "Audio shorter than video. Appending blank audio for " + (videoDurationMs - audioDurationMs) + "ms.");
                    transcoder.addDataSource(TrackType.AUDIO, new BlankAudioDataSource(blankDurationMicros));
                }
            }
        } else {
            // Fallback: use the file path (this version won’t clip the audio).
            Log.d(TAG, "Using fallback audio DataSource from file path without clipping.");
            transcoder.addDataSource(TrackType.AUDIO, audio);
        }

        // --- Set transcoder track strategies ---
        transcoder.setVideoTrackStrategy(DefaultVideoStrategies.for720x1280());
        transcoder.setAudioTrackStrategy(new DefaultAudioStrategy.Builder()
                .bitRate(128 * 1000)
                .channels(2)
                .sampleRate(44100)
                .build());

        transcoder.setListener(new TranscoderListener() {
            @Override
            public void onTranscodeProgress(double progress) {
                // Optionally report progress.
            }

            @Override
            public void onTranscodeCompleted(int code) {
                Log.d(TAG, "Merging audio/video completed successfully.");
                completer.set(Result.success());
                if (!video.delete()) {
                    Log.w(TAG, "Could not delete video file: " + video);
                }
            }

            @Override
            public void onTranscodeCanceled() {
                Log.d(TAG, "Merging audio/video was cancelled.");
                completer.setCancelled();
                if (!output.delete()) {
                    Log.w(TAG, "Could not delete failed output file: " + output);
                }
            }

            @Override
            public void onTranscodeFailed(@NonNull Throwable e) {
                Log.e(TAG, "Merging audio/video failed.", e);
                completer.setException(e);
                if (!output.delete()) {
                    Log.w(TAG, "Could not delete failed output file: " + output);
                }
            }
        });

        Log.d(TAG, "Starting transcoding process...");
        transcoder.transcode();
    }

  /*  private void doActualWork(File video, String audio, File output, CallbackToFutureAdapter.Completer<Result> completer) {
        DataSource audio2 = VideoUtil.createDataSource(getApplicationContext(), audio);
        TranscoderOptions.Builder transcoder = Transcoder.into(output.getAbsolutePath());

        long duration1;
        try {
            duration1 = TimeUnit.MICROSECONDS.toMillis(audio2.getDurationUs());
            if (duration1 <= 0) {
                throw new IllegalArgumentException("Invalid duration from DataSource.");
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to get duration using DataSource, falling back to MediaMetadataRetriever.", e);
            try {
                duration1 = VideoUtil.getDuration(getApplicationContext(), Uri.parse(audio));
                if (duration1 <= 0) {
                    Log.e(TAG, "Failed to retrieve duration using fallback method: " + audio);
                    completer.set(Result.failure());
                    return;
                }
            } catch (IOException ex) {
                Log.e(TAG, "Fallback to MediaMetadataRetriever also failed.", ex);
                completer.set(Result.failure());
                return;
            }
        }


        long duration2;
        try {
            duration2 = VideoUtil.getDuration(getApplicationContext(), Uri.fromFile(video));
            if (duration2 <= 0) {
                Log.e(TAG, "Failed to get duration for video: " + video.getAbsolutePath());
                completer.set(Result.failure());
                return;
            }
        } catch (IOException e) {
            Log.e(TAG, "Failed to get video duration: " + video.getAbsolutePath(), e);
            completer.set(Result.failure());
            return;
        }


//        long duration1 = TimeUnit.MICROSECONDS.toMillis(audio2.getDurationUs());
//        long duration2 = 0;
//        try {
//            duration2 = VideoUtil.getDuration(getApplicationContext(), Uri.fromFile(video));
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        if (duration1 > duration2) {
//            audio2 = new ClipDataSource(
//                    audio2, 0, TimeUnit.MILLISECONDS.toMicros(duration2));
//            transcoder.addDataSource(TrackType.AUDIO, audio2);
//        } else {
//            transcoder.addDataSource(TrackType.AUDIO, audio2);
//            transcoder.addDataSource(TrackType.AUDIO,
//                    new BlankAudioDataSource(
//                            TimeUnit.MILLISECONDS.toMicros(duration2 - duration1)));
//        }

        transcoder.addDataSource(TrackType.VIDEO, video.getAbsolutePath());
        transcoder.setListener(new TranscoderListener() {

            @Override
            public void onTranscodeProgress(double progress) {
            }

            @Override
            public void onTranscodeCompleted(int code) {
                Log.d(TAG, "Merging audio/video has finished.");
                completer.set(Result.success());
                if (!video.delete()) {
                    Log.w(TAG, "Could not delete video file: " + video);
                }
            }

            @Override
            public void onTranscodeCanceled() {
                Log.d(TAG, "Merging audio/video was cancelled.");
                completer.setCancelled();
                if (!output.delete()) {
                    Log.w(TAG, "Could not delete failed output file: " + output);
                }
            }

            @Override
            public void onTranscodeFailed(@NonNull Throwable e) {
                Log.d(TAG, "Merging audio/video failed with error.", e);
                completer.setException(e);
                if (!output.delete()) {
                    Log.w(TAG, "Could not delete failed output file: " + output);
                }
            }
        });
        transcoder.setVideoTrackStrategy(DefaultVideoStrategies.for720x1280());
        transcoder.transcode();
    }*/
}
