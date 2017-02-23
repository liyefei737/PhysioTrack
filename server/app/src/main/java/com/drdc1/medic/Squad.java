package com.drdc1.medic;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class Squad {
    private static Squad instance = null;

    //monitoring list has all the soildiers that are currently being monitored,
    //whereas pendding list has the soliders yet to be acknowledged by the medic
    private static List<Soldier> monitoring = null;
    private static List<Soldier> pending = null;

    public static Squad getInstance() {
        if (instance == null) {
            instance = new Squad();
        }
        return instance;
    }

    private Squad() {
        monitoring = new ArrayList<>();
        pending = new ArrayList<>();
    }

    public void addSoldierToMonitoring (Soldier newSoldier) {
        if (newSoldier == null) {
            throw new InvalidParameterException();
        }
        monitoring.add(newSoldier);
    }

    public void addSoldierToPending (Soldier newSoldier) {
        if (newSoldier == null) {
            throw new InvalidParameterException();
        }
        pending.add(newSoldier);
    }

}
