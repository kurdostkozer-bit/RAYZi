package com.example.rayzi.worker.UploadingProgres;

import android.util.Log;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;
import okio.Okio;

public class ProgressRequestBody extends RequestBody {
    private static final String TAG = "ProgressRequestBody";
    private File mFile;
    private String mPath;
    private UploadCallbacks mListener;
    private String content_type;

    private static final int DEFAULT_BUFFER_SIZE = 2048;

    public interface UploadCallbacks {
        void onProgressUpdate(int percentage);

        void onError();

        void onFinish();
    }

    public ProgressRequestBody(final File file, String content_type, final UploadCallbacks listener) {
        this.content_type = content_type;
        mFile = file;
        mListener = listener;
    }

    @Override
    public MediaType contentType() {
        return MediaType.parse(content_type + "/*");
    }

    @Override
    public long contentLength() throws IOException {
        return mFile.length();
    }

    /*    @Override
        public void writeTo(BufferedSink sink) throws IOException {
            long fileLength = mFile.length();
            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
            FileInputStream in = new FileInputStream(mFile);
            long uploaded = 0;
    
            try {
                int read;
                Handler handler = new Handler(Looper.getMainLooper());
                while ((read = in.read(buffer)) != -1) {
    
                    // update progress on UI thread
                    handler.post(new ProgressUpdater(uploaded, fileLength));
    
                    uploaded += read;
                    sink.write(buffer, 0, read);
                }
            } finally {
                in.close();
            }
        }*/
    public void writeTo(BufferedSink sink) throws IOException {
        try (okio.Source source = Okio.source(mFile)) {
            long totalBytesRead = 0;
            long fileSize = mFile.length();

            BufferedSink bufferedSink = Okio.buffer(sink);

            for (long bytesRead; (bytesRead = source.read(bufferedSink.buffer(), 2048)) != -1; ) {
                totalBytesRead += bytesRead;
                bufferedSink.flush();
                int percentage = (int) ((totalBytesRead * 100) / fileSize);

                // Ensure that the percentage is not greater than 100
                percentage = Math.min(100, percentage);

                Log.d(TAG, "onWrite: totalBytesRead=" + totalBytesRead + ", fileSize=" + fileSize + ", percentage=" + percentage);
                // Notify progress update
//                mListener.onProgressUpdate(percentage);
            }

            bufferedSink.close();
            // Notify completion
//            mListener.onFinish();
        } catch (Exception e) {
            e.printStackTrace();
            // Notify error
//            mListener.onError();
        }
    }

    private class ProgressUpdater implements Runnable {
        private long mUploaded;
        private long mTotal;

        public ProgressUpdater(long uploaded, long total) {
            mUploaded = uploaded;
            mTotal = total;
        }

        @Override
        public void run() {
            mListener.onProgressUpdate((int) (100 * mUploaded / mTotal));
        }
    }
}