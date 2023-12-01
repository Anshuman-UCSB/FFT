package com.example.fft_video;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import wseemann.media.FFmpegMediaMetadataRetriever;

public class FrameExtractor {
    private static final String TAG = "FrameExtractor";
    private final FFmpegMediaMetadataRetriever retriever;
    private Context context;
    public FrameExtractor(Context context){
        this.context = context;
        retriever = new FFmpegMediaMetadataRetriever();
    }
    public List<Bitmap> extractFrames(Uri videoUri) {
        List<Bitmap> frames = new ArrayList<>();

        retriever.setDataSource(context, videoUri);
        String duration = retriever.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_DURATION);
        long videoDuration = Long.parseLong(duration) * 1000; // in microseconds
        Log.i(TAG, "Duration is "+videoDuration+"us");
        Log.i(TAG, "KEY FRAMERATE is "+retriever.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_FRAMERATE));

        for (long time = 0; time < videoDuration; time += 100000) { // Extract frame every second
            Bitmap frame = retriever.getFrameAtTime(time, FFmpegMediaMetadataRetriever.OPTION_CLOSEST_SYNC);
            frames.add(frame);
        }

        retriever.release();
        return frames;
    }
}
