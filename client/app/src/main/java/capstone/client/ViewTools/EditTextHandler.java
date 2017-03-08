package capstone.client.ViewTools;

import android.view.View;
import android.widget.EditText;

import java.util.List;

import capstone.client.DataManagement.Soldier;
import capstone.client.R;

/**
 * Created by Grace on 2017-03-03.
 */

public class EditTextHandler {
    private static int rightAlign = View.TEXT_ALIGNMENT_TEXT_START;
    private static int centerAlign = View.TEXT_ALIGNMENT_CENTER;

    public static void enableAndFormat(List<EditText> etList){
        for (EditText et : etList) {
            et.setClickable(true);
            et.setCursorVisible(true);
            et.setFocusable(true);
            et.setFocusableInTouchMode(true);
            et.setTextAlignment(rightAlign);
            et.setBackgroundResource(R.drawable.edit_bg);
            et.setHint("");

        }
    }

    public static void disableAndFormat(List<EditText> etList){
        for (EditText et : etList){
            et.setClickable(false);
            et.setCursorVisible(false);
            et.setFocusable(false);
            et.setFocusableInTouchMode(false);
            et.setTextAlignment(centerAlign);
            et.setBackground(null);

        }
    }

    public static void setSoldierFields(Soldier s, EditText id, EditText age, EditText weight, EditText height) {
        String strID = s.getSoldierID();
        if (strID != null && !strID.isEmpty()) {
            id.setHint(strID);
            id.setText(strID);
        }

        int iAge = s.getAge();
        if (iAge > 0) {
            age.setHint(String.valueOf(iAge));
            age.setText(String.valueOf(iAge));
        }

        int iWeight = s.getWeight();
        if (iWeight > 0) {
            weight.setHint(String.valueOf(iWeight));
            weight.setText(String.valueOf(iWeight));
        }

        int iHeight = s.getHeight();
        if (iHeight > 0) {
            height.setHint(String.valueOf(iHeight));
            height.setText(String.valueOf(iHeight));
        }

    }
}
