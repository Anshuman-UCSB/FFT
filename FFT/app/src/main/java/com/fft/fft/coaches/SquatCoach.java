package com.fft.fft.coaches;

import static com.fft.fft.poseDetection.utils.diffX;
import static com.fft.fft.poseDetection.utils.diffY;
import static java.lang.Math.abs;

import android.content.Context;
import android.util.Log;

import com.google.mlkit.vision.pose.Pose;
import com.google.mlkit.vision.pose.PoseLandmark;

public class SquatCoach extends Coach{
    public final String TAG = "FFT_SquatCoach";

    private int imbalanced;
    private int notEnoughROM;
    private int reps;
    private int ankleFlag;

    private float lowestHip;
    private float lowestRatio;

    enum State {
        UP,
        DOWN
    }

    private State state;
    public SquatCoach(Context ctx){
        super(ctx);
        Log.i(TAG, "Initialized squat coach");
    }
    @Override
    public String getAdvice() {
        str.setLength(0);
        note("Currently on rep "+reps+" and in state "+(state== State.UP?"UP":"DOWN"));

        if(ankleFlag==1){
            note("Try to keep your feet pointed 45° out");
        }else if(ankleFlag == -1){
            note("Good job keeping your feet pointed out!");
        }

        if(notEnoughROM>0){
            note("Make sure you're going down low enough");
        }
        if(imbalanced>0){
            note("Your hands were offset, try to make sure they're staying even");
        }
        return super.getAdvice();
    }

    @Override
    public void reset() {
        state = State.UP;
        str.setLength(0);
        reps = 0;
        lowestHip = 0;
        imbalanced = 0;
        notEnoughROM = 0;
        ankleFlag = 0;
    }

    @Override
    public void process(Pose pose) {
        PoseLandmark leftShoulder = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER);
        if(leftShoulder == null) return;
        PoseLandmark rightShoulder = pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER);
        PoseLandmark leftHip = pose.getPoseLandmark(PoseLandmark.LEFT_HIP);
        PoseLandmark rightHip = pose.getPoseLandmark(PoseLandmark.RIGHT_HIP);

        PoseLandmark leftHeel = pose.getPoseLandmark(PoseLandmark.LEFT_HEEL);
        PoseLandmark rightHeel = pose.getPoseLandmark(PoseLandmark.RIGHT_HEEL);
        PoseLandmark leftToe = pose.getPoseLandmark(PoseLandmark.LEFT_FOOT_INDEX);
        PoseLandmark rightToe = pose.getPoseLandmark(PoseLandmark.RIGHT_FOOT_INDEX);

        PoseLandmark leftWrist = pose.getPoseLandmark(PoseLandmark.LEFT_WRIST);
        PoseLandmark rightWrist = pose.getPoseLandmark(PoseLandmark.RIGHT_WRIST);

        float hipWidth = diffX(leftHip, rightHip);
        float margin = hipWidth;

        float heightRatio = diffY(leftHeel, leftHip)/margin;

        float minAnkleWidth = Math.min(abs(diffX(leftToe, leftHeel)),abs(diffX(rightToe, rightHeel)))/hipWidth;
        if (minAnkleWidth < .35)
            ankleFlag = 1;
        else
            ankleFlag = -1;

        debug = "Ratio: "+heightRatio+"\nLowest Ratio: "+lowestRatio+"\nAnkleWidth: "+minAnkleWidth+"\nAnkleFlag: "+ankleFlag;
        if(state == State.UP){
            if(lowestHip != 0){
                lowestHip = 0;
                if(notEnoughROM>0)
                    notEnoughROM--;
                if(lowestRatio > 2.4)
                    notEnoughROM = 2;
//                debug = "Ratio: "+lowestRatio;
                if(imbalanced > 0)
                    imbalanced--; // reset flag
            }
            if(heightRatio < 3.3){
                state = State.DOWN;
            }
        } else { // Down state
            if (leftHip.getPosition().y > lowestHip){
                lowestHip = leftHip.getPosition().y;
                lowestRatio = heightRatio;
            }
            if(diffY(leftWrist, rightWrist) > margin/2){
                imbalanced = 3;
            }
            if(heightRatio > 3.4){
                state = State.UP;
                reps++;
            }
        }
    }
}
