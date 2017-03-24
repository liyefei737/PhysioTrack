package com.drdc1.medic.ViewUtils.BullsEyeUtils;

import android.content.res.Resources;
import android.os.AsyncTask;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.List;


public class BullsEyeDrawTask extends AsyncTask<BullsEyeInfo, Integer, List<ImageView>> {
    Resources mRes;
    int mNumSoldiers;
    RelativeLayout mRelLayout;
    public BullsEyeDrawTask(Resources res, int numSoldiers){
        mRes = res;
        mNumSoldiers = numSoldiers;

    }

    @Override
    protected List<ImageView> doInBackground(BullsEyeInfo... bullsEyeInfo) {
        BullsEyeInfo bei = bullsEyeInfo[0];
        mRelLayout = bei.getRelLayout();
        return BullsEye.drawBullsEye(mRes, mNumSoldiers, bei.getStatusList(), bei.isSmall());
    }

    @Override
    protected void onPostExecute(List<ImageView> resultDrawables){
        for (int i = 0; i< resultDrawables.size(); i++){
            mRelLayout.addView(resultDrawables.get(i));
        }

    }

}
