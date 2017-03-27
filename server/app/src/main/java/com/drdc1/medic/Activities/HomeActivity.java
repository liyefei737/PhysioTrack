package com.drdc1.medic.Activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.drdc1.medic.BackgroundServices.BackgroundServer;
import com.drdc1.medic.BackgroundServices.BackgroundSleepAlgo;
import com.drdc1.medic.BackgroundServices.BackgroundWellnessAlgo;
import com.drdc1.medic.DataManagement.DataObserver;
import com.drdc1.medic.DataManagement.DataSleepObserver;
import com.drdc1.medic.DataManagement.DataStatusObserver;
import com.drdc1.medic.DataManagement.FragmentDataManager;
import com.drdc1.medic.Fragments.IndividualSoldierTab;
import com.drdc1.medic.Fragments.NameList;
import com.drdc1.medic.Fragments.SquadStatus;
import com.drdc1.medic.Fragments.TreatmentScreenTab;
import com.drdc1.medic.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

// This is the home page of the tab application.
public class HomeActivity extends AppCompatActivity implements FragmentDataManager {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private DataReceiver dataReceiver;
    private BullsEyeReceiver beReceiver;
    private OverallWithIDReceiver oIDReceiver;
    private SleepReceiver sleepReceiver;
    private ArrayList<DataObserver> fragmentlist;
    private ArrayList<DataObserver> bullsEyeFragmentlist;
    private ArrayList<DataStatusObserver> statusFragmentList;
    private ArrayList<DataSleepObserver> sleepFragmentList;

    public ArrayList<String> latestFatigueStatuses;

    private String solderId;
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private String individualSolderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startBackgroundServices();
        setContentView(R.layout.activity_home);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        //register intent filter for LocalBroadbast Manager
        IntentFilter PDAMESSAGE = new IntentFilter("PDAMessage");
        dataReceiver = new DataReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(dataReceiver, PDAMESSAGE);

        IntentFilter BULLSEYE = new IntentFilter(getString(R.string.bulls_eye_update));
        beReceiver = new BullsEyeReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(beReceiver, BULLSEYE);

        IntentFilter OID = new IntentFilter("NAMELIST_OVERALL");
        oIDReceiver = new OverallWithIDReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(oIDReceiver, OID);

        IntentFilter SLEEP = new IntentFilter("NAMELIST_SLEEP");
        sleepReceiver = new SleepReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(sleepReceiver, SLEEP);

        fragmentlist = new ArrayList<>();
        bullsEyeFragmentlist = new ArrayList<>();
        statusFragmentList = new ArrayList<>();
        sleepFragmentList = new ArrayList<>();

        latestFatigueStatuses = new ArrayList<>();

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

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
    public void registerBullsEyeFragment(DataObserver o) {
        if (!bullsEyeFragmentlist.contains(o))
            bullsEyeFragmentlist.add(o);
    }

    @Override
    public void unregisterBullsEyeFragment(DataObserver o) {
        if (bullsEyeFragmentlist.contains(o)) {
            int observerIndex = bullsEyeFragmentlist.indexOf(o);
            bullsEyeFragmentlist.remove(observerIndex);
        }
    }

    @Override
    public void  registerStatusWithIDFragment(DataStatusObserver o) {
        if (!statusFragmentList.contains(o))
            statusFragmentList.add(o);
    }

    @Override
    public void  unregisterStatusWithIDFragment(DataStatusObserver o) {
        if (statusFragmentList.contains(o)) {
            int observerIndex = statusFragmentList.indexOf(o);
            statusFragmentList.remove(observerIndex);
        }
    }

    @Override
    public void  registerSleepFragment(DataSleepObserver o) {
        if (!sleepFragmentList.contains(o))
            sleepFragmentList.add(o);
    }

    @Override
    public void  unregisterSleepFragment(DataSleepObserver o) {
        if (sleepFragmentList.contains(o)) {
            int observerIndex = sleepFragmentList.indexOf(o);
            sleepFragmentList.remove(observerIndex);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void notifyObserver(Map data) {
        for (DataObserver observer : fragmentlist) {
            observer.update(data);
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void notifyBullsEyeObserver(Map data) {
        for (DataObserver observer : bullsEyeFragmentlist) {
            observer.update(data);
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void notifyStatusWithIDObserver(Map data) {
        for (DataStatusObserver observer : statusFragmentList) {
            observer.updateStatus(data);
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void notifySleepObserver(Map data) {
        for (DataSleepObserver observer : sleepFragmentList) {
            observer.updateSleep(data);
        }

    }

    public class BullsEyeReceiver extends BroadcastReceiver {

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onReceive(Context context, Intent intent) {
            Map data = new HashMap();
            data.put("overall", intent.getStringArrayExtra("OVERALL"));
            data.put("skin", intent.getStringArrayExtra("SKIN"));
            data.put("core", intent.getStringArrayExtra("CORE"));

            notifyBullsEyeObserver(data);
        }
    }
    public class DataReceiver extends BroadcastReceiver {

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onReceive(Context context, Intent intent) {
            Map data = new HashMap<>();
            data.put("bodypos", intent.getStringExtra("bodypos"));
            data.put("coreTemp", intent.getStringExtra("coreTemp"));
            data.put("skinTemp", intent.getStringExtra("skinTemp"));
            data.put("br", intent.getStringExtra("br"));
            data.put("hr", intent.getStringExtra("hr"));
            data.put("ID", intent.getStringExtra("ID"));
            data.put("name", intent.getStringExtra("name"));

            notifyObserver(data);
        }
    }
    public class OverallWithIDReceiver extends BroadcastReceiver {

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onReceive(Context context, Intent intent) {
            Map data = new HashMap();
            for (String s:intent.getExtras().keySet()){
                data.put(s, intent.getStringExtra(s));
            }

            notifyStatusWithIDObserver(data);
        }
    }
    public class SleepReceiver extends BroadcastReceiver {

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onReceive(Context context, Intent intent) {
            Map data = new HashMap();
            latestFatigueStatuses.clear();
            for (String s:intent.getExtras().keySet()){
                int percent = intent.getIntExtra(s, 0);
                data.put(s, percent);
                if (percent < 33)
                    latestFatigueStatuses.add("GREEN");
                else if (percent > 67)
                    latestFatigueStatuses.add("RED");
                else
                    latestFatigueStatuses.add("YELLOW");
            }

            notifySleepObserver(data);
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_home, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(
                    getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    SquadStatus tab1 = new SquadStatus();
                    return tab1;
                case 1:
                    NameList tab2 = new NameList();
                    return tab2;
                case 2:
                    IndividualSoldierTab tab3 = new IndividualSoldierTab();
                    return tab3;
                case 3:
                    TreatmentScreenTab tab4 = new TreatmentScreenTab();
                    return tab4;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            // Show 4 total pages.
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Squad Status";
                case 1:
                    return "Name List";
                case 2:
                    return "Individual Soldier";
                case 3:
                    return "Treatment Screen";
            }
            return null;
        }
    }

    private void startBackgroundServices() {
        Intent Serviceintent = new Intent();
        Serviceintent.setClass(this, BackgroundServer.class);
        startService(Serviceintent);

        Intent sleepAlgo = new Intent();
        sleepAlgo.setClass(this, BackgroundSleepAlgo.class);
        startService(sleepAlgo);

        Intent wellnessAlgo = new Intent();
        wellnessAlgo.setClass(this, BackgroundWellnessAlgo.class);
        startService(wellnessAlgo);
    }

    public void callHelp(View v) {
        Intent callhelp = new Intent(HomeActivity.this, HelpActivity.class);
        startActivity(callhelp);
    }



    public void onSelectSoldierByName(String id) {
        this.solderId = id;
        mViewPager.setCurrentItem(2, true);
    }

    public String popSoldierId() {
        String result = this.solderId;
//        solderId = null;
        return result;
    }


    public void onSelectIndividualSoldier(String id) {
        this.individualSolderId = id;
        mViewPager.setCurrentItem(3, true);
    }

    public String popIndividualSoldierId() {
        String result = individualSolderId;
        individualSolderId = null;
        return result;
    }
    public String getIndivID() {
        return this.individualSolderId;
    }

}
