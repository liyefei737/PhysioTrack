package com.drdc1.medic.ViewUtils.BullsEyeUtils;

import android.widget.LinearLayout;

import java.util.List;

/**
 * Created by Grace on 2017-03-15.
 */

public class BullsEyeInfo {


    private LinearLayout mLinLayout;
    private List<String> mStatusList;

    private List<String> mSkinStatusList;
    private List<String> mCoreStatusList;
    private List<String> mFatigueStatusList;


    public BullsEyeInfo(){

    }
    public BullsEyeInfo(LinearLayout ll,List<String> statuses, List<String>skinStatuses,
                        List<String> coreStatuses,  List<String> fatStatuses){
        mLinLayout = ll;
        mStatusList = statuses;
        mSkinStatusList = skinStatuses;
        mCoreStatusList = coreStatuses;
        mFatigueStatusList = fatStatuses;
    }

    public LinearLayout getLinLayout() {
        return mLinLayout;
    }
    public List<String> getStatusList() {
        return mStatusList;
    }
    public List<String> getCoreStatusList() {
        return mCoreStatusList;
    }
    public List<String> getSkinStatusList() {
        return mSkinStatusList;
    }
    public List<String> getFatigueStatusList() {
        return mFatigueStatusList;
    }


}
