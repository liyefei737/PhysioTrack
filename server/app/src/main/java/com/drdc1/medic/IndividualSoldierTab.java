package com.drdc1.medic;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.arlib.floatingsearchview.FloatingSearchView;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class IndividualSoldierTab extends Fragment {
    private FloatingSearchView seachView;
    private Squad squad;

    public IndividualSoldierTab() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        squad = Squad.getInstance();
        setupSearchBar();
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_individual_soldier_tab, container, false);
    }

    private void setupSearchBar() {
        seachView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {

            @Override
            public void onSearchTextChanged(String oldQuery, final String newQuery) {

                if (!oldQuery.equals("") && newQuery.equals("")) {
                    seachView.clearSuggestions();
                } else {

                    //this shows the top left circular progress
                    //you can call it where ever you want, but
                    //it makes sense to do it when loading something in
                    //the background.
                    //seachView.showProgress();
                    seachView.swapSuggestions(getNameSearchSuggestions(newQuery));
                    seachView.hideProgress();
                }
            }
        });
    }

    private List<Suggestion> getNameSearchSuggestions (String name) {
        List<Suggestion> suggestions = new ArrayList<Suggestion>();
        return suggestions;
    }

}
