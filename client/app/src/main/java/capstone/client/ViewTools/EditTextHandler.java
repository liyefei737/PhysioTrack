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

    public static void enableAndFormat(List<EditText> etList){
        for (EditText et : etList) {
            et.setClickable(true);
            et.setCursorVisible(true);
            et.setFocusable(true);
            et.setFocusableInTouchMode(true);
            et.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            et.setBackgroundResource(R.drawable.edit_et_bg);
            et.setHint("");
        }
    }

    public static void disableAndFormat(List<EditText> etList){
        for (EditText et : etList){
            et.setClickable(false);
            et.setCursorVisible(false);
            et.setFocusable(false);
            et.setFocusableInTouchMode(false);
            et.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            et.setBackground(null);

        }
    }

    public static void setSoldierFields(Soldier s, EditText id, EditText name, EditText age, EditText weight, EditText height, EditText ip) {
        String strID = s.getSoldierID();
        if (!strID.isEmpty()) {
            id.setHint(strID);
            id.setText(strID);
        }

        int iAge = s.getAge();
        if (iAge > 0) {
            age.setHint(String.valueOf(iAge));
            age.setText(String.valueOf(iAge));
        }
        else{
            age.setHint(R.string.age_text_edit);
        }

        int iWeight = s.getWeight();
        if (iWeight > 0) {
            weight.setHint(String.valueOf(iWeight));
            weight.setText(String.valueOf(iWeight));
        }
        else
            weight.setHint(R.string.weight_text_edit);

        int iHeight = s.getHeight();
        if (iHeight > 0) {
            height.setHint(String.valueOf(iHeight));
            height.setText(String.valueOf(iHeight));
        }
        else
            height.setHint(R.string.height_text_edit);

        String strIP = s.getMedicIP();
        if (!strIP.isEmpty()) {
            ip.setHint(strIP);
            ip.setText(strIP);
        }
        else
            ip.setHint(R.string.ip_text_edit);

        String strName = s.getSoldierName();
        if (!strName.isEmpty()) {
            name.setHint(strName);
            name.setText(strName);
        }
        else
            name.setHint(R.string.name_text_edit);



    }
}
