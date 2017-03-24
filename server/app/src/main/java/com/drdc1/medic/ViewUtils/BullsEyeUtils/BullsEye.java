package com.drdc1.medic.ViewUtils.BullsEyeUtils;

import android.content.res.Resources;
import android.graphics.drawable.GradientDrawable;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.drdc1.medic.R;

import java.util.ArrayList;
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

    public static List<ImageView> drawBullsEye(Resources resources, int numSoldiers, List<WelfareStatus> statusArray, boolean small) {

        int red = resources.getColor(R.color.bullsEyeRed);
        int yellow = resources.getColor(R.color.bullsEyeYellow);
        int green = resources.getColor(R.color.bullsEyeGreen);
        int grey = resources.getColor(R.color.grey);
        int ringID = 0;
        List<ImageView> finalDrawables = new ArrayList<>();
        RelativeLayout.LayoutParams imageViewParams;

        int size;
        if (small) {
            ringID = R.drawable.small_ring0;
            size = (int)(2*resources.getDimension(R.dimen.small_ring_radius) + 2*resources.getDimension(R.dimen.small_ring_thickness));
        }
        else {
            ringID = R.drawable.ring0;
            size = (int)(2*resources.getDimension(R.dimen.large_ring_radius) + 2*resources.getDimension(R.dimen.large_ring_thickness));
        }

        imageViewParams = new RelativeLayout.LayoutParams(size, size);
        imageViewParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

        Collections.sort(statusArray);
        Collections.reverse(statusArray);
        for (int i = 0; i < numSoldiers; i++) {
            ImageView ringView = new ImageView(getContext());
            ringView.setLayoutParams(imageViewParams);
            GradientDrawable gd = (GradientDrawable) resources.getDrawable(ringID);
            if (statusArray.get(i) == RED) {
                gd.setColor(red);
            } else if (statusArray.get(i) == YELLOW) {
                gd.setColor(yellow);
            } else if (statusArray.get(i) == GREEN) {
                gd.setColor(green);
            } else {
                gd.setColor(grey);
            }
            ringView.setLayoutParams(imageViewParams);
            ringView.setImageDrawable(gd);

            finalDrawables.add(ringView);
            ringID++;
        }
        return finalDrawables;
    }
}
