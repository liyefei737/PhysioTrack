package welfareSM;

import static welfareSM.WelfareStatus.*;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class WelfareTracker {
    private HashMap<String, WelfareStatus> currentStateMap;
    private HashMap<String, WelfareStatus> prevStateMap;
    private WelfareAlgoParams wap;
    private String OVERALL = "OVERALL";
    private String HR = "HEART";
    private String BR = "BREATH";
    private String ST = "SKIN";
    private String CT = "CORE";

    public WelfareTracker() {
        initStateMaps();
        wap = new WelfareAlgoParams();
    }

    public WelfareTracker(List<Integer> customHRRange, List<Integer> customBRRange,
                          List<Float> customSTRange, List<Float> customCTRange) {
        initStateMaps();
        wap = new WelfareAlgoParams(customHRRange, 0.25, customBRRange, 0.25, customSTRange, 0.25,
                customCTRange, 0.25);
    }

    public WelfareTracker(List<Integer> customHRRange, double heartWeight,
                          List<Integer> customBRRange, double breathWeight,
                          List<Float> customSTRange, double skinWeight, List<Float> customCTRange,
                          double coreWeight) {
        initStateMaps();
        wap = new WelfareAlgoParams(customHRRange, heartWeight, customBRRange, breathWeight,
                customSTRange, skinWeight, customCTRange, coreWeight);
    }

    private void initStateMaps() {
        currentStateMap = new HashMap<String, WelfareStatus>();
        prevStateMap = new HashMap<String, WelfareStatus>();
        currentStateMap.put(OVERALL, GREY);
        currentStateMap.put(HR, GREY);
        currentStateMap.put(BR, GREY);
        currentStateMap.put(ST, GREY);
        currentStateMap.put(CT, GREY);
        prevStateMap.put(OVERALL, GREY);
        prevStateMap.put(HR, GREY);
        prevStateMap.put(BR, GREY);
        prevStateMap.put(ST, GREY);
        prevStateMap.put(CT, GREY);
    }

    public void setPhysioParamThresholds(List<Integer> heartRateRange,
                                         List<Integer> breathRateRange, List<Float> skinTempRange,
                                         List<Float> coreTempRange) {
        wap.setPhysioParamThresholds(heartRateRange, breathRateRange, skinTempRange, coreTempRange);

    }

    public void setPhysioParamThresholds(WelfareAlgoParams welfareAP) {
        wap = welfareAP;
    }

    public Object[] calculateWelfareStatus(JSONArray lastXsecondsData) {
        Object[] resultant = new Object[2];
        HashMap<String, Object> algoValues = processJSONDataArray(lastXsecondsData);
        if (algoValues == null) {
            return GREY;
        }

        getHeartStatus((int) algoValues.get(HR));
        getBreathStatus((int) algoValues.get(BR));
        getSkinStatus((float) algoValues.get(ST));
        getCoreStatus((float) algoValues.get(CT));

        WelfareStatus nextState;
        int numGreenParams = Collections.frequency(currentStateMap.values(), GREEN);
        int numYellowParams = Collections.frequency(currentStateMap.values(), YELLOW);
        int numRedParams = Collections.frequency(currentStateMap.values(), RED);
        int numValidParams = numGreenParams + numYellowParams + numRedParams;
        if (numValidParams == 0) {
            nextState = GREY;
        } else {
            //TODO: Do calc based on previous states of each params.

            float stateAverage =
                    (numGreenParams * GREEN.ordinal() + numYellowParams * YELLOW.ordinal() +
                            numRedParams * RED.ordinal() / numValidParams);

            nextState = stateAverage <= 1 ? RED : (stateAverage <= 2 ? YELLOW : GREEN);
        }
        prevStateMap.put(OVERALL, currentStateMap.get(OVERALL));
        resultant[0] = currentStateMap;
        currentStateMap.put(OVERALL, nextState);
        resultant[1] = nextState;
        return resultant;
    }

    private HashMap<String, Object> processJSONDataArray(JSONArray lastXsecondsData) {
        //TODO: look at actual time length of data, 15 sec intervals for hr and br

        int len = lastXsecondsData.length();
        if (len == 0) {
            return null;
        }

        HashMap<String, Object> algoValues = new HashMap<String, Object>();
        JSONObject lastEntry = null;
        try {
            String entryTemp = lastXsecondsData.getString(len - 1);
            lastEntry = new JSONObject(entryTemp);
        } catch (JSONException e) {
            algoValues.put(HR, 0);
            algoValues.put(BR, 0);
        }

        try {
            algoValues.put(HR, lastEntry.getInt("heartRate"));
        } catch (JSONException e) {
            algoValues.put(HR, 0);
        }

        try {
            algoValues.put(BR, lastEntry.getInt("breathRate"));
        } catch (JSONException e) {
            algoValues.put(BR, 0);
        }

        JSONObject curr = null;
        float skinSum = 0, coreSum = 0;
        int numValidSkinValues = 0, numValidCoreValues = 0;

        for (int i = 0; i < len; i++) {
            try {
                curr = new JSONObject(lastXsecondsData.getString(i));
            } catch (JSONException e) {
                ;
            }
            try {
                skinSum += curr.getInt("skinTemp");
                numValidSkinValues++;
            } catch (JSONException e) {
                //don't count value
            }
            try {
                coreSum += curr.getInt("coreTemp");
                numValidCoreValues++;
            } catch (JSONException e) {
                //don't count value
            }
        }
        if (coreSum == 0 || numValidCoreValues == 0) {
            algoValues.put(CT, 0.0f);
        } else {
            algoValues.put(CT, coreSum / numValidCoreValues);
        }

        if (skinSum == 0 || numValidSkinValues == 0) {
            algoValues.put(ST, 0.0f);
        } else {
            algoValues.put(ST, skinSum / numValidSkinValues);
        }

        return algoValues;
    }

    public WelfareStatus getHeartStatus(int heartRate) {
        prevStateMap.put(HR, currentStateMap.get(HR));
        List<Integer> hrRange = wap.getHrRange();
        WelfareStatus next;
        if (heartRate <= 0) {
            next = GREY;
        } else if (heartRate < hrRange.get(0) || heartRate > hrRange.get(3)) {
            next = RED;
        } else if (heartRate < hrRange.get(1) || heartRate > hrRange.get(2)) {
            next = YELLOW;
        } else {
            next = GREEN;
        }
        currentStateMap.put(HR, next);
        return next;
    }

    public WelfareStatus getHeartStatus() {
        return currentStateMap.get(HR);
    }

    public WelfareStatus getBreathStatus(int breathRate) {
        prevStateMap.put(BR, currentStateMap.get(BR));
        List<Integer> brRange = wap.getBrRange();
        WelfareStatus next;
        if (breathRate <= 0) {
            next = GREY;
        } else if (breathRate < brRange.get(0) || breathRate > brRange.get(1)) {
            next = RED;
        } else {
            next = GREEN;
        }
        currentStateMap.put(BR, next);
        return next;
    }

    public WelfareStatus getBreathStatus() {
        return currentStateMap.get(BR);
    }

    public WelfareStatus getSkinStatus(float skinTemp) {
        prevStateMap.put(ST, currentStateMap.get(ST));
        List<Float> stRange = wap.getStRange();
        WelfareStatus next;
        if (skinTemp <= 0) {
            next = GREY;
        } else if (skinTemp < stRange.get(0) || skinTemp > stRange.get(3)) {
            next = RED;
        } else if (skinTemp < stRange.get(1) || skinTemp > stRange.get(2)) {
            next = YELLOW;
        } else {
            next = GREEN;
        }
        currentStateMap.put(ST, next);
        return next;
    }

    public WelfareStatus getSkinStatus() {
        return currentStateMap.get(ST);
    }

    public WelfareStatus getCoreStatus(float coreTemp) {
        prevStateMap.put(CT, currentStateMap.get(CT));
        List<Float> ctRange = wap.getCtRange();
        WelfareStatus next;
        if (coreTemp <= 15) {
            next = GREY;
        } else if (coreTemp < ctRange.get(0) || coreTemp > ctRange.get(3)) {
            next = RED;
        } else if (coreTemp < ctRange.get(1) || coreTemp > ctRange.get(2)) {
            next = YELLOW;
        } else {
            next = GREEN;
        }
        currentStateMap.put(CT, next);
        return next;
    }

    public WelfareStatus getCoreStatus() {
        return currentStateMap.get(CT);
    }

    public WelfareAlgoParams getWAP() {
        return wap;
    }

}
