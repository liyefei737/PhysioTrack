package com.drdc1.medic;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.drdc1.medic.uitls.Trie;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class IndividualSoldierTab extends Fragment {
    private FloatingSearchView seachView;
    private Squad squad;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_individual_soldier_tab, container, false);
        seachView = (FloatingSearchView)rootView.findViewById(R.id.searchBar);
        squad = Squad.getInstance();
        setupSearchBar();
        return rootView;
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
                    seachView.showProgress();
                     List<NameSuggestion> nameSearchSuggestions = getNameSearchSuggestions(newQuery);
                    seachView.swapSuggestions(nameSearchSuggestions);
                    seachView.hideProgress();
                }
            }
        });
    }

    /***
     *
     * @param searchString is the user input in the search box
     * @return a List of Suggestions for the searchString
     */
    private List<NameSuggestion> getNameSearchSuggestions (String searchString) {

        Trie trie = new Trie();
        for (Soldier s : squad.getMonitoringSoildiersSoildiers()) {
            trie.insert(s.getName());
        }
        List<NameSuggestion> nameSuggestions = new ArrayList<NameSuggestion>();
        for(String name : trie.autoComplete(searchString)) {
            nameSuggestions.add(new NameSuggestion(name));
        }
        return nameSuggestions;
    }

}
