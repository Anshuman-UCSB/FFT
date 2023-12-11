package com.fft.fft.coaches;

import static com.fft.fft.poseDetection.utils.avg;
import static com.fft.fft.poseDetection.utils.avgY;
import static com.fft.fft.poseDetection.utils.diffX;
import static com.fft.fft.poseDetection.utils.diffY;
import static com.fft.fft.poseDetection.utils.dist;

import static java.lang.Math.abs;

import android.content.Context;
import android.util.Log;

import com.google.mlkit.vision.pose.Pose;
import com.google.mlkit.vision.pose.PoseLandmark;

public class BenchCoach extends Coach{
    public final String TAG = "FFT_BenchCoach";

    private int imbalanced;
    private int elbow;
    private int reps;

    private float lowestRatio;
    private float lowestDiff;

    enum State {
        UP,
        DOWN
    }

    private State state;
    public BenchCoach(Context ctx){
        super(ctx);
        Log.i(TAG, "Initialized bench coach");
    }
    @Override
    public String getAdvice() {
        str.setLength(0);
        note("Currently on rep "+reps+" and in state "+(state==State.UP?"UP":"DOWN"));
        switch(elbow) {
            case 1:
                note("Your elbows are too flared out.");
                break;
            case -1:
                note("Your elbows are tucked in too much.");
                break;
            case 0:
                note("Good job keeping around a 45° angle!");
                break;
        }
//        switch(state) {
//            case UP:
//                note("Currently in state UP");
//                break;
//            case DOWN:
//                note("Currently in state DOWN");
//                break;
//        }
        if(imbalanced>0){
            note("Your hands were offset, try to make sure they're staying even");
        }
        return super.getAdvice();
    }

    @Override
    public void reset() {
        elbow = -2; // do not show text
        state = State.UP;
        str.setLength(0);
        reps = 0;
        lowestDiff = 0;
        imbalanced = 0;
    }

    @Override
    public void process(Pose pose) {
        PoseLandmark leftShoulder = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER);
        if(leftShoulder == null) return;
        PoseLandmark rightShoulder = pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER);
        PoseLandmark leftElbow = pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW);
        PoseLandmark rightElbow = pose.getPoseLandmark(PoseLandmark.RIGHT_ELBOW);
        PoseLandmark leftWrist = pose.getPoseLandmark(PoseLandmark.LEFT_WRIST);
        PoseLandmark rightWrist = pose.getPoseLandmark(PoseLandmark.RIGHT_WRIST);

        float shoulderWidth = dist(leftShoulder, rightShoulder);
        float margin = shoulderWidth / 5f;

        float elbowToShoulderDiff = avgY(leftShoulder, rightShoulder)-avgY(leftElbow, rightElbow);
        debug = "Ratio: "+lowestRatio+"\nelbowToShoulderDiff: "+elbowToShoulderDiff+"\nLowestDiff: "+lowestDiff+"\nElbow Flag: "+elbow;
        if(state == State.UP){
            if(lowestDiff != 0){
                lowestDiff = 0;
                if(lowestRatio < .8){
                    elbow = 1;
                }else if(lowestRatio > 2){
                    elbow = -1;
                }else{
                    elbow = 0;
                }
                if(imbalanced > 0)
                    imbalanced--; // reset flag
            }
            if(elbowToShoulderDiff < margin){
                state = State.DOWN;
            }
        } else { // Down state
            if (elbowToShoulderDiff < lowestDiff){
                lowestDiff = elbowToShoulderDiff;
                lowestRatio = abs(diffY(leftShoulder, leftElbow)/diffX(leftShoulder, leftElbow));
            }
            if(diffY(leftWrist, rightWrist) > margin){
                imbalanced = 3;
            }
            if(elbowToShoulderDiff > margin){
                state = State.UP;
                reps++;
            }
        }
    }
}
