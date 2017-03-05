package capstone.client.Activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.UnsavedRevision;
import com.ncapdevi.fragnav.FragNavController;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabReselectListener;
import com.roughike.bottombar.OnTabSelectListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import capstone.client.DataManagement.DBManager;
import capstone.client.DataManagement.DataObserver;
import capstone.client.DataManagement.FragmentDataManager;
import capstone.client.DataManagement.Soldier;
import capstone.client.Fragments.BaseFragment;
import capstone.client.Fragments.BreathFragment;
import capstone.client.Fragments.CoreTempFragment;
import capstone.client.Fragments.EditInfoFragment;
import capstone.client.Fragments.HeartFragment;
import capstone.client.Fragments.HelpPageFragment;
import capstone.client.Fragments.HomeFragment;
import capstone.client.Fragments.SkinTempFragment;
import capstone.client.R;
import capstone.client.ViewTools.EditTextHandler;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;


public class BottomBarActivity extends AppCompatActivity implements BaseFragment.FragmentNavigation,
        FragNavController.TransactionListener,
        FragNavController.RootFragmentListener, FragmentDataManager {
    private BottomBar mBottomBar;
    private FragNavController mNavController;
    private DBManager dbManager;

    static private HashMap data; //one data shared by all fragments
    private DataReceiver dataReceiver;
    //this list is all the fragments that needs to know when data updates
    private ArrayList<DataObserver> fragmentlist;

    //Better convention to properly name the indices what they are in your app
    private final int INDEX_HEART = FragNavController.TAB1;
    private final int INDEX_BREATH = FragNavController.TAB2;
    private final int INDEX_HOME = FragNavController.TAB3;
    private final int INDEX_SKINTEMP = FragNavController.TAB4;
    private final int INDEX_CORETEMP = FragNavController.TAB5;

    private int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        dbManager = new DBManager(this);
        setContentView(R.layout.activity_bottom_bar);
        final View view = getWindow().getDecorView();;
        view.setSystemUiVisibility(uiOptions);

        view.setOnSystemUiVisibilityChangeListener
                (new View.OnSystemUiVisibilityChangeListener() {
                    @Override
                    public void onSystemUiVisibilityChange(int visibility) {
                        // Note that system bars will only be "visible" if none of the
                        // LOW_PROFILE, HIDE_NAVIGATION, or FULLSCREEN flags are set.
                        if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                            view.setSystemUiVisibility(uiOptions);
                        }
                    }
                });
        mBottomBar = (BottomBar) findViewById(R.id.bottomBar);
        mBottomBar.selectTabAtPosition(INDEX_HOME);

        mNavController =
                new FragNavController(savedInstanceState, getSupportFragmentManager(), R.id.container, this, 5, INDEX_HOME);
        mNavController.setTransactionListener(this);

        //data for all the graphs from background
        data = new HashMap();
        fragmentlist = new ArrayList<>();

        IntentFilter UPDATEDATA = new IntentFilter("UI_UPDATE");
        dataReceiver = new DataReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(dataReceiver, UPDATEDATA);

        mBottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                if (mNavController.getCurrentStack().peek().getClass() == EditInfoFragment.class)
                    mNavController.popFragment();
                switch (tabId) {
                    case R.id.bb_menu_heart:
                        mNavController.switchTab(INDEX_HEART);
                        break;
                    case R.id.bb_menu_breath:
                        mNavController.switchTab(INDEX_BREATH);
                        break;
                    case R.id.bb_menu_home:
                        mNavController.switchTab(INDEX_HOME);
                        break;
                    case R.id.bb_menu_skin_temp:
                        mNavController.switchTab(INDEX_SKINTEMP);
                        break;
                    case R.id.bb_menu_core_temp:
                        mNavController.switchTab(INDEX_CORETEMP);
                        break;
                }
            }
        });

        mBottomBar.setOnTabReselectListener(new OnTabReselectListener() {
            @Override
            public void onTabReSelected(@IdRes int tabId) {
                mNavController.clearStack();
            }
        });

    }

    @Override
    protected void onResume(){
        super.onResume();
        View view = getWindow().getDecorView();
        view.setSystemUiVisibility(uiOptions);
    }
    @Override
    public void onBackPressed() {
        if (!mNavController.isRootFragment()) {
            mNavController.popFragment();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mNavController != null) {
            mNavController.onSaveInstanceState(outState);
        }
    }

    @Override
    public void pushFragment(Fragment fragment) {
        if (mNavController != null) {
            mNavController.pushFragment(fragment);
        }
    }

    @Override
    public void onTabTransaction(Fragment fragment, int index) {
        // If we have a backstack, show the back button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(!mNavController.isRootFragment());
        }
    }

    @Override
    public void onFragmentTransaction(Fragment fragment) {
        //do fragmentty stuff. Maybe change title, I'm not going to tell you how to live your life
        // If we have a backstack, show the back button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(!mNavController.isRootFragment());
        }
    }

    @Override
    public Fragment getRootFragment(int index) {
        switch (index) {
            case INDEX_HEART:
                return HeartFragment.newInstance(0);
            case INDEX_BREATH:
                return BreathFragment.newInstance(0);
            case INDEX_HOME:
                return HomeFragment.newInstance(0);
            case INDEX_SKINTEMP:
                return SkinTempFragment.newInstance(0);
            case INDEX_CORETEMP:
                return CoreTempFragment.newInstance(0);
        }
        throw new IllegalStateException("Need to send an index that we know");
    }

    public void edit_info(View view) {
        pushFragment(new EditInfoFragment());
    }

    public void callHelpPage(View view) {
        pushFragment(new HelpPageFragment());
    }

    public void cancel_info(View view) {
        Button savebtn = (Button) findViewById(R.id.btSave);
        Button cancelbtn = (Button) findViewById(R.id.btCancel);
        savebtn.setVisibility(INVISIBLE);
        cancelbtn.setVisibility(INVISIBLE);

        List<EditText> etList = new ArrayList<>();
        etList.add((EditText) findViewById(R.id.etSoldierId));
        etList.add((EditText) findViewById(R.id.etAge));
        etList.add((EditText) findViewById(R.id.etWeight));
        etList.add((EditText) findViewById(R.id.etHeight));

        EditTextHandler.disableAndFormat(etList);
        EditTextHandler.setSoldierFields(dbManager.getSoldierDetails(), etList.get(0), etList.get(1), etList.get(2), etList.get(3));
    }

    public void edit_info_save(View view) {
        final Button btnSave = (Button) findViewById(R.id.btSave);
        final Button cancelbtn = (Button) findViewById(R.id.btCancel);
        List<EditText> etList = new ArrayList<>();

        Soldier soldier = dbManager.getSoldierDetails();
        Database userDB = dbManager.getDatabase(dbManager.USER_DB);

        EditText id = (EditText) findViewById(R.id.etSoldierId);
        final String newId = id.getText().toString();
        etList.add(id);

        EditText age = (EditText) findViewById(R.id.etAge);
        final String newAge = age.getText().toString();
        etList.add(age);

        EditText weight = (EditText) findViewById(R.id.etWeight);
        final String newWeight = weight.getText().toString();
        etList.add(weight);

        EditText height = (EditText) findViewById(R.id.etHeight);
        final String newHeight = height.getText().toString();
        etList.add(height);

        if (soldier != null && newId != soldier.getSoldierID()){
            //delete old doc
            Document doc = userDB.getDocument(soldier.getSoldierID());
            try {
                doc.delete();
            }catch (CouchbaseLiteException e){

            }
        }
        Document doc = userDB.getDocument(newId);
        try {
            doc.update(new Document.DocumentUpdater() {
                @Override
                public boolean update(UnsavedRevision newRevision) {
                    Map<String, Object> properties = newRevision.getUserProperties();
                    properties.put(dbManager.ID_KEY, newId);
                    properties.put(dbManager.AGE_KEY, newAge);
                    properties.put(dbManager.WEIGHT_KEY, newWeight);
                    properties.put(dbManager.HEIGHT_KEY, newHeight);
                    newRevision.setUserProperties(properties);
                    btnSave.setVisibility(View.INVISIBLE);
                    cancelbtn.setVisibility(View.INVISIBLE);
                    return true;
                }
            });
        } catch (CouchbaseLiteException e) {

        }

        EditTextHandler.disableAndFormat(etList);
    }

    public void edit_fields(View view) {
        Button savebtn = (Button) findViewById(R.id.btSave);
        Button cancelbtn = (Button) findViewById(R.id.btCancel);
        savebtn.setVisibility(VISIBLE);
        cancelbtn.setVisibility(VISIBLE);

        List<EditText> etList = new ArrayList<>();
        etList.add((EditText) findViewById(R.id.etSoldierId));
        etList.add((EditText) findViewById(R.id.etAge));
        etList.add((EditText) findViewById(R.id.etWeight));
        etList.add((EditText) findViewById(R.id.etHeight));

        EditTextHandler.enableAndFormat(etList);
    }


    @Override
    public void registerFragment(DataObserver o) {
        if (!fragmentlist.contains(o))
            fragmentlist.add(o);
    }

    @Override
    public void unregisterFragment(DataObserver o) {
        if (fragmentlist.contains(o)) {
            int observerIndex = fragmentlist.indexOf(o);
            fragmentlist.remove(observerIndex);
        }
    }

    @Override
    public void notifyObserver(Map data) {
        for (DataObserver observer : fragmentlist) {
            observer.update(data);
        }

    }

    class DataReceiver extends BroadcastReceiver {

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onReceive(Context context, Intent intent) {
            mBottomBar.setBackgroundColor(context.getColor(R.color.colorAccent));
            data.put("skinTemp", intent.getFloatArrayExtra("skinTemp"));
            data.put("coreTemp", intent.getFloatArrayExtra("coreTemp"));
            data.put("br", intent.getIntArrayExtra("br"));
            data.put("hr", intent.getIntArrayExtra("hr"));
            System.out.println("receiving...");
            System.out.println(intent.getAction());
            notifyObserver(data);
        }
    }
}


