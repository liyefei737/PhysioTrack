package com.drdc1.medic.DataManagement;

import java.util.ArrayList;

/**
 * Created by Grace on 2017-03-25.
 */

public class SoldierList {
    private ArrayList<Soldier> soldiers = new ArrayList<>();

    public void setSoldiers(ArrayList<Soldier> solList){
        soldiers = solList;
    }

    public ArrayList<Soldier> getSoldiers(){
        return soldiers;
    }
    public void addSoldier(Soldier s){
        soldiers.add(s);
    }

    public Soldier getSoldierByID(String id){
        for (Soldier s:soldiers){
            if (s.getId().equals(id))
                return s;
        }
        return null;
    }
}
