package com.drdc1.medic;

import android.content.res.Resources;
import android.graphics.drawable.GradientDrawable;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.Collections;
import java.util.List;

import welfareSM.WelfareStatus;

import static com.drdc1.medic.AppContext.getContext;
import static welfareSM.WelfareStatus.GREEN;
import static welfareSM.WelfareStatus.RED;
import static welfareSM.WelfareStatus.YELLOW;

/**
 * Created by Grace on 2017-02-24.
 */

public class BullsEye {
    private static RelativeLayout.LayoutParams imageViewParams = new RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.MATCH_PARENT);


    public static void drawBullsEye(Resources resources, RelativeLayout relLayoutBullsEye, int numSoldiers, List<WelfareStatus> statusArray){

        int red = resources.getColor(R.color.bullsEyeRed);
        int yellow = resources.getColor(R.color.bullsEyeYellow);
        int green = resources.getColor(R.color.bullsEyeGreen);
        int grey = resources.getColor(R.color.grey);
        int ringID = R.drawable.ring0;
        Collections.sort(statusArray);
        Collections.reverse(statusArray);
        for (int i = 0; i < numSoldiers; i++){
            ImageView ringView = new ImageView(getContext());
            ringView.setLayoutParams(imageViewParams);
            GradientDrawable gd = (GradientDrawable)resources.getDrawable(ringID);
            if (statusArray.get(i) == RED)
                gd.setColor(red);
            else if (statusArray.get(i) == YELLOW)
                gd.setColor(yellow);
            else if (statusArray.get(i) == GREEN)
                gd.setColor(green);
            else
                gd.setColor(grey);
            ringView.setImageDrawable(gd);
            relLayoutBullsEye.addView(ringView);
            ringID++;
        }
    }
}
