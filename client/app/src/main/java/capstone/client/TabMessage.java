package capstone.client;

/**
 * Created by mehmetatmaca on 2017-01-30.
 */

public class TabMessage {
    public static String get(int menuItemId, boolean isReselection) {
        String message = "Content for ";

        switch (menuItemId) {
            case R.id.tab_heart:
                message += "Heart";
                break;
            case R.id.tab_lung:
                message += "Lung";
                break;
            case R.id.tab_home:
                message += "Home";
                break;
            case R.id.tab_skin:
                message += "Skin";
                break;
            case R.id.tab_core:
                message += "Core";
                break;
        }

        if (isReselection) {
            message += " RESELECTED!!";
        }

        return message;
    }
}
