package capstone.client;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.UnsavedRevision;
import com.ncapdevi.fragnav.FragNavController;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabReselectListener;
import com.roughike.bottombar.OnTabSelectListener;

import java.util.Map;

public class BottomBarActivity extends AppCompatActivity implements BaseFragment.FragmentNavigation, FragNavController.TransactionListener, FragNavController.RootFragmentListener {
    private BottomBar mBottomBar;
    private FragNavController mNavController;
    private DBManager dbManager;

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

    public void edit_info_save(View view){
        Database userDB = dbManager.getDatabase(dbManager.USER_DB);
        EditText id = (EditText) findViewById(R.id.etSoldierId);
        final String newId = id.getText().toString();

        EditText age = (EditText) findViewById(R.id.etAge);
        final String newAge = age.getText().toString();

        EditText weight = (EditText) findViewById(R.id.etWeight);
        final String newWeight = weight.getText().toString();

        EditText height = (EditText) findViewById(R.id.etHeight);
        final String newHeight = age.getText().toString();
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
                    return true;
                }
            });
        }catch (CouchbaseLiteException e){


        }

    }

    public void updateHomeFragment(String state){
        if(mNavController.getCurrentFrag().getClass() == HomeFragment.class){
            HomeFragment homeFrag = (HomeFragment) mNavController.getCurrentFrag();
            homeFrag.updateWellnessStatus(state);
        }
    }

}


