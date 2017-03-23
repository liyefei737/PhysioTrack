package capstone.client.ViewTools;

import android.content.res.Resources;

import capstone.client.R;
import welfareSM.WelfareStatus;

/**
 * Created by Grace on 2017-03-14.
 */

public class StateColourUtils {

    public static int StringStateToColour(String state, Resources resources){
        switch (state) {
            case "RED":
                return resources.getColor(R.color.red);
            case "YELLOW":
                return resources.getColor(R.color.yellow);
            case "GREEN":
                return resources.getColor(R.color.green);
            default:
                return resources.getColor(R.color.bb_inActiveBottomBarItemColor);
        }

    }

    public static int WelfareStateToColour(WelfareStatus state, Resources resources){
        if (state == WelfareStatus.RED)
            return resources.getColor(R.color.red);
        else if (state == WelfareStatus.YELLOW)
            return resources.getColor(R.color.yellow);
        else if (state == WelfareStatus.GREEN)
            return resources.getColor(R.color.green);
        else return resources.getColor(R.color.bb_inActiveBottomBarItemColor);

    }
}
