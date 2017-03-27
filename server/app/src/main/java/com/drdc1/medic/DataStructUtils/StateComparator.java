package com.drdc1.medic.DataStructUtils;

import java.util.Comparator;

/**
 * Created by Grace on 2017-03-27.
 */

public class StateComparator implements Comparator<String> {
    public int compare(String lhs, String rhs) {
        if (lhs.equals(rhs))
            return 0;

        if (lhs.equals("GREY"))
            return -1;

        if (lhs.equals("RED"))
            return 1;

        if (lhs.equals("GREEN"))
            if (rhs.equals("GREY"))
                return 1;
            else
                return -1;

        if (lhs.equals("YELLOW"))
            if (rhs.equals("RED"))
                return -1;
            else
                return 1;

        return 0;
    }
}
