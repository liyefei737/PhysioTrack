package com.pengona.sqtest;

import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.io.InputStream;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private ItemArrayAdapter itemArrayAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.list_view);
        itemArrayAdapter = new ItemArrayAdapter(getApplicationContext(), R.layout.single_list_item);

        Parcelable state = listView.onSaveInstanceState();
        listView.setAdapter(itemArrayAdapter);
        listView.onRestoreInstanceState(state);

        InputStream inputStream = getResources().openRawResource(R.raw.csv);
        CSVReader csv = new CSVReader(inputStream);
        List<String[]> scoreList = csv.read();

        for(String [] scoreData : scoreList) {
            itemArrayAdapter.add(scoreData);
        }
        //MyDBHandler mydb = new MyDBHandler(this, null, null,1);
        //mydb.addData();
    }


}
