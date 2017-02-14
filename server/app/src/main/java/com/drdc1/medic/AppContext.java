package com.drdc1.medic;

import android.content.Context;

/**
 * Created by ub on 2/14/17.
 */

public class AppContext extends Application {

    private static AppContext instance;

    public AppContext() {
        instance = this;
    }

    public static Context getContext() {
        return instance;
    }

}
