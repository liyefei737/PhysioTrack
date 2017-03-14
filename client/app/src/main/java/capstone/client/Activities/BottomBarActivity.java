package capstone.client.Activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.ncapdevi.fragnav.FragNavController;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.BottomBarTab;
import com.roughike.bottombar.OnTabReselectListener;
import com.roughike.bottombar.OnTabSelectListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import capstone.client.DataManagement.DBManager;
import capstone.client.DataManagement.DataObserver;
import capstone.client.DataManagement.FragmentDataManager;
import capstone.client.Fragments.BaseFragment;
import capstone.client.Fragments.BreathFragment;
import capstone.client.Fragments.CoreTempFragment;
import capstone.client.Fragments.EditInfoFragment;
import capstone.client.Fragments.HeartFragment;
import capstone.client.Fragments.HelpPageFragment;
import capstone.client.Fragments.HomeFragment;
import capstone.client.Fragments.SkinTempFragment;
import capstone.client.R;
import capstone.client.ViewTools.StateColourUtils;

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

    private int uiOptions =
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        dbManager = new DBManager(this);
        setContentView(R.layout.activity_bottom_bar);
        final View view = getWindow().getDecorView();
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
                new FragNavController(savedInstanceState, getSupportFragmentManager(),
                        R.id.container, this, 5, INDEX_HOME);
        mNavController.setTransactionListener(this);

        //data for all the graphs from background
        data = new HashMap();
        fragmentlist = new ArrayList<>();

        IntentFilter UPDATEDATA = new IntentFilter(getString(R.string.update_action));
        dataReceiver = new DataReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(dataReceiver, UPDATEDATA);

        mBottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                Class current = mNavController.getCurrentStack().peek().getClass();
                if (current == HelpPageFragment.class) {
                    mNavController.popFragment();
                    current = mNavController.getCurrentStack().peek().getClass();
                }
                if (current == EditInfoFragment.class) {
                    mNavController.popFragment();
                }
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

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent

            String hr = intent.getStringExtra("HEART");
            String br = intent.getStringExtra("BREATH");
            String ct = intent.getStringExtra("CORE");
            String st = intent.getStringExtra("SKIN");
            Resources res = getResources();
            if (hr != null){
                BottomBarTab hearttab = mBottomBar.getTabWithId(R.id.bb_menu_heart);
                hearttab.setBadgeBackgroundColor(StateColourUtils.StringStateToColour(hr, res));
                hearttab.setBadgeText("!");
            }
            if (br != null) {
                BottomBarTab breathtab = mBottomBar.getTabWithId(R.id.bb_menu_breath);
                breathtab.setBadgeText("!");
                breathtab.setBadgeBackgroundColor(StateColourUtils.StringStateToColour(br, res));
            }
            if (st != null) {
                BottomBarTab skintab = mBottomBar.getTabWithId(R.id.bb_menu_skin_temp);
                skintab.setBadgeText("!");
                skintab.setBadgeBackgroundColor(StateColourUtils.StringStateToColour(st, res));

            }
            if (ct != null) {
                BottomBarTab coretab = mBottomBar.getTabWithId(R.id.bb_menu_core_temp);
                coretab.setBadgeText("!");
                coretab.setBadgeBackgroundColor(StateColourUtils.StringStateToColour(ct, res));
            }

        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        View view = getWindow().getDecorView();
        view.setSystemUiVisibility(uiOptions);

        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver, new IntentFilter("@string/tab_colour_update"));

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

    public void edit_info_cancel(View view) {
        EditInfoFragment.edit_info_cancel(this, dbManager);
    }

    public void edit_info_save(View view) {
        EditInfoFragment.edit_info_save(this, dbManager);
    }

    public void edit_fields(View view) {
        EditInfoFragment.edit_fields(this);
    }

    @Override
    public void registerFragment(DataObserver o) {
        if (!fragmentlist.contains(o)) {
            fragmentlist.add(o);
        }
    }

    @Override
    public void unregisterFragment(DataObserver o) {
        if (fragmentlist.contains(o)) {
            int observerIndex = fragmentlist.indexOf(o);
            fragmentlist.remove(observerIndex);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void notifyObserver(Map data) {
//        mBottomBar.setBackgroundColor(getApplicationContext().getColor(R.color.colorAccent));
//        final Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                mBottomBar.setBackgroundColor(getApplicationContext().getColor(R.color.colorPrimary));
//            }
//        }, 100);

        for (DataObserver observer : fragmentlist) {

            observer.update(data);
        }

    }

    class DataReceiver extends BroadcastReceiver {

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onReceive(Context context, Intent intent) {

            data.put("skinTemp", intent.getFloatArrayExtra("skinTemp"));
            data.put("coreTemp", intent.getFloatArrayExtra("coreTemp"));
            data.put("br", intent.getIntArrayExtra("br"));
            data.put("hr", intent.getIntArrayExtra("hr"));
            data.put("state", intent.getStringExtra("state"));
            System.out.println(intent.getAction());
            notifyObserver(data);
        }
    }

    public void onHelpOrSettingsBackPressed(View view) {
        if (mNavController.getCurrentFrag().getClass() == EditInfoFragment.class && ((EditText) findViewById(R.id.etSoldierId)).isCursorVisible() ) {
            EditInfoFragment.edit_info_cancel(this, dbManager);
        }
        else {
            onBackPressed();
        }
    }
}


