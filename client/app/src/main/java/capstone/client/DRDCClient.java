package capstone.client;

import android.app.Application;

import welfareSM.WelfareStatus;
import welfareSM.WelfareTracker;

/**
 * Created by Grace on 2017-01-21.
 */

public class DRDCClient extends Application {

    //global vars for app
    //access in any activity by String s = ((MyApplication) this.getApplication()).getSomeVariable(); or
    //    ((MyApplication) this.getApplication()).setSomeVariable("foo");

    private WelfareStatus lastState;

    public WelfareStatus getLastState() {
        return lastState;
    }

    public void setLastState(WelfareStatus state) {
        lastState = state;
    }

    private WelfareTracker welfareTracker = new WelfareTracker();

    public WelfareTracker getWelfareTracker(){
        return welfareTracker;
    }

}