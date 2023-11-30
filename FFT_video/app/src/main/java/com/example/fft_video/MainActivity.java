package com.example.fft_video;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
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

public class MainActivity extends AppCompatActivity implements GlPlayerRenderer.FrameListener{
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
                    } else {
                        Log.d("PhotoPicker", "No media selected");
                    }
                });

        findViewById(R.id.selectVidBtn).setOnClickListener( v -> {
            startSelectVideo();
        });
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
//                imageProcessor.setOnProcessingCompleteListener(new VisionProcessorBase.OnProcessingCompleteListener() {
//                    @Override
//                    public void onProcessingComplete() {
//                        processing = false;
////                        onProcessComplete(frame);
//                        if(pending) processFrame(lastFrame);
//                    }
//                });
//                imageProcessor.processBitmap(frame, graphicOverlay);
            }
        }
    }

    @Override
    public void onFrame(Bitmap bitmap) {
        processFrame(bitmap);
    }
}