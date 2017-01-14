/**
 * Created by ut on 1/12/17.
 */

import java.lang.*;

public class algor {
    public static void main(String[] args) {

    }

    public static void check(int n, int thres) {
        int val0 = 0.04 * Math.sqrt(Math.pow(DatabaseGetValue(n - 4).x, 2) + Math.pow(DatabaseGetValue(n - 4).y, 2) + Math.pow(DatabaseGetValue(n - 4).z, 2))
                + 0.04 * Math.sqrt(Math.pow(DatabaseGetValue(n - 3).x, 2) + Math.pow(DatabaseGetValue(n - 3).y, 2) + Math.pow(DatabaseGetValue(n - 3).z, 2))
                + 0.2 * Math.sqrt(Math.pow(DatabaseGetValue(n - 2).x, 2) + Math.pow(DatabaseGetValue(n - 2).y, 2) + Math.pow(DatabaseGetValue(n - 2).z, 2))
                + 0.2 * Math.sqrt(Math.pow(DatabaseGetValue(n - 1).x, 2) + Math.pow(DatabaseGetValue(n - 1).y, 2) + Math.pow(DatabaseGetValue(n - 1).z, 2))
                + 0.2 * Math.sqrt(Math.pow(DatabaseGetValue(n).x, 2) + Math.pow(DatabaseGetValue(n).y, 2) + Math.pow(DatabaseGetValue(n).z, 2))
                + 0.2 * Math.sqrt(Math.pow(DatabaseGetValue(n + 1).x, 2) + Math.pow(DatabaseGetValue(n + 1).y, 2) + Math.pow(DatabaseGetValue(n + 1).z, 2))
                + 0.04 * Math.sqrt(Math.pow(DatabaseGetValue(n + 2).x, 2) + Math.pow(DatabaseGetValue(n + 2).y, 2) + Math.pow(DatabaseGetValue(n + 2).z, 2)
                + 0.04 * Math.sqrt(Math.pow(DatabaseGetValue(n + 3).x, 2) + Math.pow(DatabaseGetValue(n + 3).y, 2) + Math.pow(DatabaseGetValue(n + 3).z, 2));

        if (val0 > thres) {
            System.err.println("awake");
        } else {
            System.err.println("sleeping");

        }
    }


}
