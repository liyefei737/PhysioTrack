package com.drdc1.medic.DataManagement;

import com.drdc1.medic.DataManagement.DataObserver;

import java.util.Map;

/**
 * This and the DataObserver interface uses to observer pattern
 * to avoid duplicated code in each fragment to register
 * to the broadcast
 */

public interface FragmentDataManager {
    public void registerFragment(DataObserver o);

    public void unregisterFragment(DataObserver o);

    public void notifyObserver(Map data);

}
