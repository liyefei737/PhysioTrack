package com.drdc1.medic.DataManagement;

import java.util.Map;

/**
 * Created by Grace on 2017-03-25.
 */

public interface DataStatusObserver {

    public void updateStatus(Map<String, String> data);
}