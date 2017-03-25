package com.drdc1.medic.ViewUtils.BullsEyeUtils;

import android.content.res.Resources;
import android.os.AsyncTask;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.drdc1.medic.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class BullsEyeDrawTask extends AsyncTask<BullsEyeInfo, Integer, List<RelativeLayout>> {
    Resources mRes;
    int mNumSoldiers;
    LinearLayout mLinLayout;
    public BullsEyeDrawTask(Resources res, int numSoldiers){
        mRes = res;
        mNumSoldiers = numSoldiers;

    }

    @Override
    protected void onPostExecute(List<RelativeLayout> resultDrawables){
        mLinLayout.removeViewAt(1);
        mLinLayout.addView(resultDrawables.get(0), 1);    //overall bulls eye goes @ 1
        LinearLayout smallWrapper =  (LinearLayout) mLinLayout.findViewById(R.id.smallBullsEyeWrapper);
        smallWrapper.removeAllViews();
        //add core fatigue skin
        smallWrapper.addView(resultDrawables.get(1));
        smallWrapper.addView(resultDrawables.get(2));
        smallWrapper.addView(resultDrawables.get(3));
        mLinLayout.invalidate();
    }

    @Override
    protected List<RelativeLayout> doInBackground(BullsEyeInfo... bullsEyeInfo) {
        BullsEyeInfo bei = bullsEyeInfo[0];
        mLinLayout = bei.getLinLayout();
        RelativeLayout relOverall =  BullsEye.drawBullsEye(mRes, mNumSoldiers, bei.getStatusList(), false);
        RelativeLayout relSkin =  BullsEye.drawBullsEye(mRes, mNumSoldiers, bei.getSkinStatusList(), true);
        RelativeLayout relCore =  BullsEye.drawBullsEye(mRes, mNumSoldiers, bei.getCoreStatusList(), true);
        RelativeLayout relFat =  BullsEye.drawBullsEye(mRes, mNumSoldiers, bei.getFatigueStatusList(), true);

        return new ArrayList<RelativeLayout>(Arrays.asList(relOverall, relCore, relFat, relSkin));
    }

}
