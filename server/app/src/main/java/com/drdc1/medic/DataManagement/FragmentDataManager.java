package com.drdc1.medic.DataManagement;

import java.util.Map;

/**
 * This and the DataObserver interface uses to observer pattern
 * to avoid duplicated code in each fragment to register
 * to the broadcast
 */

public interface FragmentDataManager {
    public void registerFragment(DataObserver o);

    public void unregisterFragment(DataObserver o);

    public void registerBullsEyeFragment(DataObserver o);

    public void unregisterBullsEyeFragment(DataObserver o);

    public void notifyObserver(Map data);

    public void notifyBullsEyeObserver(Map data);

    public void registerStatusWithIDFragment(DataStatusObserver o);

    public void unregisterStatusWithIDFragment(DataStatusObserver o);

    public void notifyStatusWithIDObserver(Map data);

    public void registerSleepFragment(DataSleepObserver o);

    public void unregisterSleepFragment(DataSleepObserver o);

    public void notifySleepObserver(Map data);

}
