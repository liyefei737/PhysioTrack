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

}
