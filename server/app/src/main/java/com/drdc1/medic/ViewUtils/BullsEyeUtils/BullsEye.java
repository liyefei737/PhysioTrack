package com.drdc1.medic.ViewUtils.BullsEyeUtils;

import android.content.res.Resources;
import android.graphics.drawable.GradientDrawable;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.drdc1.medic.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.drdc1.medic.AppContext.getContext;

/**
 * Created by Grace on 2017-02-24.
 */

public class BullsEye {

    public static RelativeLayout drawBullsEye(Resources resources, int numSoldiers, List<String> statusArray, boolean small) {

        int red = resources.getColor(R.color.bullsEyeRed);
        int yellow = resources.getColor(R.color.bullsEyeYellow);
        int green = resources.getColor(R.color.bullsEyeGreen);
        int grey = resources.getColor(R.color.grey);
        int ringID = 0;

        LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
        RelativeLayout finalLayout = new RelativeLayout(getContext());
        finalLayout.setLayoutParams(lparams);

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

        List<String> goodStatusList = new ArrayList<>();
        for (String s:statusArray){
            if (s!=null && !s.isEmpty()){
                goodStatusList.add(s);
            }
        }
        if (goodStatusList != null) {
            Collections.sort(goodStatusList);
            Collections.reverse(goodStatusList);
            for (int i = 0; i < numSoldiers; i++) {
                ImageView ringView = new ImageView(getContext());
                ringView.setLayoutParams(imageViewParams);
                GradientDrawable gd = (GradientDrawable) resources.getDrawable(ringID);
                if (goodStatusList.get(i).equals("RED")) {
                    gd.setColor(red);
                } else if (goodStatusList.get(i).equals("YELLOW")) {
                    gd.setColor(yellow);
                } else if (goodStatusList.get(i).equals("GREEN")) {
                    gd.setColor(green);
                } else {
                    gd.setColor(grey);
                }
                ringView.setLayoutParams(imageViewParams);
                ringView.setImageDrawable(gd);

                finalLayout.addView(ringView);
                ringID++;
            }
        }
        return finalLayout;
    }
}
