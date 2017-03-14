package capstone.client.ViewTools;

import android.content.res.Resources;

import capstone.client.R;
import welfareSM.WelfareStatus;

/**
 * Created by Grace on 2017-03-14.
 */

public class StateColourUtils {

    public static int StringStateToColour(String state, Resources resources){
        if (state.equals("RED"))
            return resources.getColor(R.color.red);
        else if (state.equals("YELLOW"))
            return resources.getColor(R.color.yellow);
        else if (state.equals("GREEN"))
            return resources.getColor(R.color.green);
        else return resources.getColor(R.color.bb_inActiveBottomBarItemColor);

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
