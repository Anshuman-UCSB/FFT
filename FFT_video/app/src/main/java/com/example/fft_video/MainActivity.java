package com.example.fft_video;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import com.example.fft_video.gles.GlPlayerView;
import com.example.fft_video.gles.GlPlayerRenderer;

import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.mlkit.vision.pose.Pose;
import com.google.mlkit.vision.pose.PoseLandmark;

import java.io.IOException;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements GlPlayerRenderer.FrameListener{
    private static final String TAG = "FFT";
    private SimpleExoPlayer player;
    private PlayerView playerView;
    private ActivityResultLauncher<PickVisualMediaRequest> pickMedia;
    private GraphicOverlay graphicOverlay;

    private int frameWidth, frameHeight;

    private boolean processing;
    private boolean pending;
    private Bitmap lastFrame;
    private CustomPoseDetector imageProcessor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageProcessor = new CustomPoseDetector(this);

        player = new SimpleExoPlayer.Builder(this).build();

        playerView = findViewById(R.id.player_view);
        playerView.setPlayer(player);
        FrameLayout contentFrame = playerView.findViewById(com.google.android.exoplayer2.ui.R.id.exo_content_frame);
        View videoFrameView = createVideoFrameView();
        if(videoFrameView != null) contentFrame.addView(videoFrameView);

        graphicOverlay = new GraphicOverlay(this, null);
        contentFrame.addView(graphicOverlay);

        pickMedia = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                    // Callback is invoked after the user selects a media item or closes the
                    // photo picker.
                    if (uri != null) {
                        Log.d("PhotoPicker", "Selected URI: " + uri);
                        setupPlayer(uri);
                        preprocessVideo(uri);
                    } else {
                        Log.d("PhotoPicker", "No media selected");
                    }
                });

        findViewById(R.id.selectVidBtn).setOnClickListener( v -> {
            startSelectVideo();
        });
    }

    private void preprocessVideo(Uri uri) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(this, uri); // Set your media item path or Uri

        // Get the duration of the video in microseconds
        String duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        long videoDuration = Long.parseLong(duration) * 1000;

        // Set the time interval for frame extraction
        long interval = 10000; // Set your desired interval in microseconds
        for (long time = 0; time < videoDuration; time += interval) {
            Bitmap frame = retriever.getFrameAtTime(time, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);

            // Process the 'frame' bitmap using your custom function
            imageProcessor.queue(frame);
        }
        Log.i(TAG, "Set all frames to be preprocessed");
        imageProcessor.updateStatus();

        // Release the MediaMetadataRetriever
        try {
            retriever.release();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        imageProcessor.destroy();
    }

    private void startSelectVideo() {
        pickMedia.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.VideoOnly.INSTANCE)
                .build());
    }

    private View createVideoFrameView() {
        GlPlayerView glPlayerView = new GlPlayerView(this);
        glPlayerView.setSimpleExoPlayer(player);
        glPlayerView.setFrameListener(this);
        return glPlayerView;
    }

    private void setupPlayer(Uri uri){
        MediaItem mediaItem = MediaItem.fromUri(uri);
        player.stop();
        player.setMediaItem(mediaItem);
        player.prepare();
        player.play();
    }

    private void processFrame(Bitmap frame){
        lastFrame = frame;
        if(imageProcessor != null){
            pending = processing;
            if(!processing){
                processing = true;
                if(frameWidth != frame.getWidth() || frameHeight != frame.getHeight()){
                    frameWidth = frame.getWidth();
                    frameHeight = frame.getHeight();
                    graphicOverlay.setImageSourceInfo(frameWidth, frameHeight, false);
                }
//                Log.i(TAG, "procesed frame: "+imageProcessor.detect(frame));
//                Pose p = imageProcessor.detect(frame);
//                if(p!= null){
//                    Log.i(TAG, "Landmarks: "+p.getPoseLandmark(PoseLandmark.NOSE));
//                    drawPose(graphicOverlay, p);
//                }
                processing = false;
            }
        }
    }

    private void drawPose(GraphicOverlay graphicOverlay, Pose pose) {
        graphicOverlay.clear();
        graphicOverlay.add(new PoseGraphic(graphicOverlay, pose));
        graphicOverlay.postInvalidate();
    }


    @Override
    public void onFrame(Bitmap bitmap) {
        processFrame(bitmap);
        imageProcessor.updateStatus();
    }
}