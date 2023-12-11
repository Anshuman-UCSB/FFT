package com.fft.fft;

import static com.fft.fft.utils.getSizeForDesiredSize;

import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.FrameLayout;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;

public class PoseFragment extends Fragment implements TextureView.SurfaceTextureListener{
    private static final String TAG = "FFT_PoseFragment";
    private SimpleExoPlayer player;
    private PlayerView playerView;

    private TextureView textureView;
    private ActivityResultLauncher<PickVisualMediaRequest> pickMedia;
    private Surface playerSurface;
    private SurfaceTexture surfaceTexture;
    private GraphicOverlay graphicOverlay;

    private int frameWidth, frameHeight;

    private CustomPoseDetector imageProcessor;

    public PoseFragment(){
        super(R.layout.pose_fragment);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        imageProcessor = new CustomPoseDetector();

        player = new SimpleExoPlayer.Builder(getContext()).build();
        player.setVolume(0);

        playerView = view.findViewById(R.id.player_view);
        playerView.setPlayer(player);
        FrameLayout contentFrame = playerView.findViewById(com.google.android.exoplayer2.ui.R.id.exo_content_frame);
        View videoFrameView = createVideoFrameView();
        if(videoFrameView != null) contentFrame.addView(videoFrameView);

        graphicOverlay = new GraphicOverlay(getContext(), null);
        contentFrame.addView(graphicOverlay);

        pickMedia = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
            if (uri != null) {
                Log.d(TAG, "Selected URI: " + uri);
                setupPlayer(uri);
            } else {
                Log.d(TAG,"No media selected");
            }
        });

        view.findViewById(R.id.selectVidBtn).setOnClickListener( v -> {
            startSelectVideo();
        });
    }

    private View createVideoFrameView() {
        textureView = new TextureView(getContext());
        textureView.setSurfaceTextureListener(this);
        return textureView;
    }

    private void setupPlayer(Uri uri){
        MediaItem mediaItem = MediaItem.fromUri(uri);
        player.stop();
        player.setMediaItem(mediaItem);
        player.prepare();
        player.play();
    }

    private void startSelectVideo() {
        pickMedia.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.VideoOnly.INSTANCE)
                .build());
    }

    @Override
    public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surface, int width, int height) {
        surfaceTexture = surface;
        playerSurface = new Surface(surface);
        player.setVideoSurface(playerSurface);
    }

    @Override
    public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surface) {
        player.setVideoSurface(null);
        playerSurface.release();
        playerSurface = null;
        surfaceTexture.release();
        surfaceTexture = null;
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surface) {
        Size size = getSizeForDesiredSize(textureView.getWidth(), textureView.getHeight(), 500);
        processFrame(textureView.getBitmap(size.getWidth(), size.getHeight()));
    }

    private void processFrame(Bitmap bitmap) {
        if(frameWidth != bitmap.getWidth() || frameHeight != bitmap.getHeight()){
            frameWidth = bitmap.getWidth();
            frameHeight = bitmap.getHeight();
            graphicOverlay.setImageSourceInfo(frameWidth, frameHeight, false);
        }
        imageProcessor.requestPose(bitmap, pose->{
            graphicOverlay.clear();
            graphicOverlay.add(new PoseGraphic(
                    graphicOverlay,
                    pose,
                    true,
                    false
            ));
            graphicOverlay.postInvalidate();
        });
    }
}
