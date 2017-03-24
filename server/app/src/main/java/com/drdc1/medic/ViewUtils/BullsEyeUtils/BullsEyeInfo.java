package com.drdc1.medic.ViewUtils.BullsEyeUtils;

import android.widget.RelativeLayout;

import java.util.List;

import welfareSM.WelfareStatus;

/**
 * Created by Grace on 2017-03-15.
 */

public class BullsEyeInfo {


    private RelativeLayout mRelLayout;
    private boolean mIsSmall;
    private List<WelfareStatus> mStatusList;

    public BullsEyeInfo(){

    }
    public BullsEyeInfo(RelativeLayout rl, boolean small, List<WelfareStatus> statuses){
        mRelLayout = rl;
        mIsSmall = small;
        mStatusList = statuses;
    }

    public RelativeLayout getRelLayout() {
        return mRelLayout;
    }

    public boolean isSmall() {
        return mIsSmall;
    }

    public List<WelfareStatus> getStatusList() {
        return mStatusList;
    }


}
