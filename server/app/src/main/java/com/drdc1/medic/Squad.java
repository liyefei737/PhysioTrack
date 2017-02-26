package com.drdc1.medic;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class Squad {
    private static Squad instance = null;

    //monitoringSoildiers list has all the soildiers that are currently being monitored,
    //whereas pendding list has the soliders yet to be acknowledged by the medic
    private static List<Soldier> monitoringSoildiers = null;
    private static List<Soldier> pendingSoildiers = null;

    public static Squad getInstance() {
        if (instance == null) {
            instance = new Squad();
        }
        return instance;
    }

    private Squad() {
        monitoringSoildiers = new ArrayList<>();
        pendingSoildiers = new ArrayList<>();
        addSoldierTomonitoringSoildiers(new Soldier("James John", "6sad6df8hsdf", "Male"));
        addSoldierTomonitoringSoildiers(new Soldier("Kevin Scott", "fdsifjlk3343", "Male"));
    }

    public void addSoldierTomonitoringSoildiers(Soldier newSoldier) {
        if (newSoldier == null) {
            throw new InvalidParameterException();
        }
        monitoringSoildiers.add(newSoldier);
    }

    public void addSoldierTopendingSoildiers(Soldier newSoldier) {
        if (newSoldier == null) {
            throw new InvalidParameterException();
        }
        pendingSoildiers.add(newSoldier);
    }

    public List<Soldier> getMonitoringSoildiersSoildiers() {
        return monitoringSoildiers;
    }

    public static List<Soldier> getPendingSoildiers() {
        return pendingSoildiers;
    }
}
