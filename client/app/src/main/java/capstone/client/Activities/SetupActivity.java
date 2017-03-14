package capstone.client.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import capstone.client.DataManagement.DBManager;
import capstone.client.Fragments.EditInfoFragment;
import capstone.client.R;

public class SetupActivity extends AppCompatActivity {
    private DBManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        dbManager = new DBManager(this);
        FragmentManager fm = getSupportFragmentManager();

        Fragment fragment = fm.findFragmentById(R.id.initInfo);
        findViewById(R.id.welcome_Message).setVisibility(View.VISIBLE);
        if (fragment == null) {
            fragment =  new EditInfoFragment();
            edit_fields(findViewById(R.id.initInfo));
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(R.id.initInfo, fragment, "initEditInfo");
            ft.commit();
        }
        edit_fields(findViewById(R.id.initInfo));
    }

    public void edit_fields(View view){
        EditInfoFragment.edit_fields(this);
        findViewById(R.id.btCancel).setVisibility(View.GONE);
        findViewById(R.id.back_arrow).setVisibility(View.INVISIBLE);
        findViewById(R.id.helpButton).setVisibility(View.INVISIBLE);
        findViewById(R.id.edit_data).setVisibility(View.INVISIBLE);
        findViewById(R.id.editInfoTitle).setVisibility(View.INVISIBLE);
        findViewById(R.id.edit_user_image).setVisibility(View.INVISIBLE);
        ((EditText)findViewById(R.id.etSoldierId)).setHint(R.string.id_text_edit);
        ((EditText)findViewById(R.id.etAge)).setHint(R.string.age_text_edit);
        ((EditText)findViewById(R.id.etWeight)).setHint(R.string.weight_text_edit);
        ((EditText)findViewById(R.id.etHeight)).setHint(R.string.height_text_edit);
        ((EditText)findViewById(R.id.etIP)).setHint(R.string.ip_text_edit);
        ((EditText)findViewById(R.id.etSoldierName)).setHint(R.string.name_text_edit);
    }

    public void edit_info_save(View view){
        EditInfoFragment.edit_info_save(this, dbManager);
        Intent startApp = new Intent(SetupActivity.this, BottomBarActivity.class);
        startActivity(startApp);
        finish();
    }

}
