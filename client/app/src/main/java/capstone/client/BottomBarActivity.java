package capstone.client;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Map;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class BottomBarActivity extends AppCompatActivity implements BaseFragment.FragmentNavigation,
                                                                FragNavController.TransactionListener,
                    FragNavController.RootFragmentListener,FragmentDataManager  {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbManager = new DBManager(this);
        setContentView(R.layout.activity_bottom_bar);

        mBottomBar = (BottomBar) findViewById(R.id.bottomBar);
        mBottomBar.selectTabAtPosition(INDEX_HOME);

        mNavController =
                new FragNavController(savedInstanceState, getSupportFragmentManager(), R.id.container,this,5, INDEX_HOME);
        mNavController.setTransactionListener(this);

        //data for all the graphs from background
        data = new HashMap();
        fragmentlist = new ArrayList<>();

        IntentFilter UPDATEDATA = new IntentFilter("UI_UPDATE");
        dataReceiver = new DataReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(dataReceiver,UPDATEDATA);

        mBottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                if(mNavController.getCurrentStack().peek().getClass() == EditInfoFragment.class)
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
            public void onTabReSelected(@IdRes int tabId){
                mNavController.clearStack();
            }
        });

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
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(!mNavController.isRootFragment());
        }
    }

    @Override
    public void onFragmentTransaction(Fragment fragment) {
        //do fragmentty stuff. Maybe change title, I'm not going to tell you how to live your life
        // If we have a backstack, show the back button
        if(getSupportActionBar() != null){
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
    public void cancel_info(View view){
        Button savebtn = (Button)findViewById(R.id.btSave);
        Button cancelbtn = (Button)findViewById(R.id.btCancel);
        savebtn.setVisibility(INVISIBLE);
        cancelbtn.setVisibility(INVISIBLE);
        EditText id = (EditText) findViewById(R.id.etSoldierId);
        EditText age = (EditText) findViewById(R.id.etAge);
        EditText weight = (EditText) findViewById(R.id.etWeight);
        EditText height = (EditText) findViewById(R.id.etHeight);

        id.setClickable(false);
        id.setCursorVisible(false);
        id.setFocusable(false);
        id.setFocusableInTouchMode(false);

        age.setClickable(false);
        age.setCursorVisible(false);
        age.setFocusable(false);
        age.setFocusableInTouchMode(false);

        weight.setClickable(false);
        weight.setCursorVisible(false);
        weight.setFocusable(false);
        weight.setFocusableInTouchMode(false);

        height.setClickable(false);
        height.setCursorVisible(false);
        height.setFocusable(false);
        height.setFocusableInTouchMode(false);
    }

    public void edit_info_save(View view){
        final Button btnSave = (Button)findViewById(R.id.btSave);
        final Button cancelbtn = (Button)findViewById(R.id.btCancel);
        Database userDB = dbManager.getDatabase(dbManager.USER_DB);
        EditText id = (EditText) findViewById(R.id.etSoldierId);
        final String newId = id.getText().toString();

        EditText age = (EditText) findViewById(R.id.etAge);
        final String newAge = age.getText().toString();

        EditText weight = (EditText) findViewById(R.id.etWeight);
        final String newWeight = weight.getText().toString();

        EditText height = (EditText) findViewById(R.id.etHeight);
        final String newHeight = height.getText().toString();
        Document doc = userDB.getDocument("1");
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
        }catch (CouchbaseLiteException e){


        }
    }
    public void edit_fields(View view){
        Button savebtn = (Button)findViewById(R.id.btSave);
        Button cancelbtn = (Button)findViewById(R.id.btCancel);
        savebtn.setVisibility(VISIBLE);
        cancelbtn.setVisibility(VISIBLE);
        EditText id = (EditText) findViewById(R.id.etSoldierId);
        EditText age = (EditText) findViewById(R.id.etAge);
        EditText weight = (EditText) findViewById(R.id.etWeight);
        EditText height = (EditText) findViewById(R.id.etHeight);

        id.setClickable(true);
        id.setCursorVisible(true);
        id.setFocusable(true);
        id.setFocusableInTouchMode(true);

        age.setClickable(true);
        age.setCursorVisible(true);
        age.setFocusable(true);
        age.setFocusableInTouchMode(true);

        weight.setClickable(true);
        weight.setCursorVisible(true);
        weight.setFocusable(true);
        weight.setFocusableInTouchMode(true);

        height.setClickable(true);
        height.setCursorVisible(true);
        height.setFocusable(true);
        height.setFocusableInTouchMode(true);

    }
    public void updateHomeFragment(String state){
        if(mNavController.getCurrentFrag().getClass() == HomeFragment.class){
            HomeFragment homeFrag = (HomeFragment) mNavController.getCurrentFrag();
            homeFrag.updateWellnessStatus(state, (ImageView) homeFrag.getView().findViewById(R.id.wellness_status));
        }
    }

    @Override
    public void registerFragment(DataObserver o) {
        fragmentlist.add(o);
    }

    @Override
    public void unregisterFragment(DataObserver o) {
        int observerIndex = fragmentlist.indexOf(o);
        fragmentlist.remove(observerIndex);
    }

    @Override
    public void notifyObserver(Map data) {
        for(DataObserver observer : fragmentlist){
            observer.update(data);
        }


    }

    class DataReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            data.put("skinTemp",intent.getFloatArrayExtra("skinTemp"));
            data.put("coreTemp",intent.getFloatArrayExtra("coreTemp"));
            data.put("br",intent.getIntArrayExtra("br"));
            data.put("hr",intent.getIntArrayExtra("hr"));
            System.out.println("receiving...");
            System.out.println(intent.getAction());
            notifyObserver(data);
        }
    }

    public void updateHeartFragment(String param){
        if(mNavController.getCurrentFrag().getClass() == HeartFragment.class){
            HeartFragment heartFrag = (HeartFragment) mNavController.getCurrentFrag();
            heartFrag.updateParam(param, (TextView) heartFrag.getView().findViewById(R.id.currentHeartRate));
        }
    }

    public void updateBreathFragment(String param){
        if(mNavController.getCurrentFrag().getClass() == BreathFragment.class){
            BreathFragment breathFrag= (BreathFragment) mNavController.getCurrentFrag();
            breathFrag.updateParam(param, (TextView) breathFrag.getView().findViewById(R.id.currentBreathRate));
        }
    }

    public void updateSkinFragment(String param){
        if(mNavController.getCurrentFrag().getClass() == SkinTempFragment.class){
            SkinTempFragment skinFrag = (SkinTempFragment) mNavController.getCurrentFrag();
            skinFrag.updateParam(param, (TextView) skinFrag.getView().findViewById(R.id.currentSkinTemp));
        }
    }

    public void updateCoreFragment(String param){
        if(mNavController.getCurrentFrag().getClass() == CoreTempFragment.class){
            CoreTempFragment coreFrag = (CoreTempFragment) mNavController.getCurrentFrag();
            coreFrag.updateParam(param, (TextView) coreFrag.getView().findViewById(R.id.currentCoreTemp));
        }
    }


}


