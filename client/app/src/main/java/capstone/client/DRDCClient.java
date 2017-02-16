package capstone.client;

import android.app.Application;

import welfareSM.WelfareStatus;

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

    private String lastHeartRate = "--";

    public String getLastHeartRate() {
        return lastHeartRate;
    }

    public void setLastHeartRate(String hr) {
        if (hr != null)
            if (!hr.isEmpty())
                lastHeartRate = hr;
    }

    private String lastBreathingRate = "--";

    public String getLastBreathingRate() {
        return lastBreathingRate;
    }

    public void setLastBreathingRate(String br) {
        if (br != null)
            if (!br.isEmpty())
                lastBreathingRate = br;
    }

    private String lastSkinTemp = "--";

    public String getLastSkinTemp() {
        return lastSkinTemp;
    }

    public void setLastSkinTemp(String st) {
        if (st != null)
            if (!st.isEmpty())
                lastSkinTemp = st;
    }

    private String lastCoreTemp = "--";

    public String getLastCoreTemp() {
        return lastCoreTemp;
    }

    public void setLastCoreTemp(String ct) {
        if (ct != null)
            if (!ct.isEmpty())
                lastCoreTemp = ct;
    }
}