package com.example.grace.snappytest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.snappydb.DB;
import com.snappydb.DBFactory;
import com.snappydb.SnappydbException;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.opencsv.CSVReader;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoSerializable;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.MapSerializer;

import static java.lang.Float.parseFloat;

public class ReadData extends AppCompatActivity {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    private static final String DATA_FILE = "EquivitalData.csv";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_data);

        // Example of a call to a native method
        TextView tv = (TextView) findViewById(R.id.sample_text);
        tv.setText(stringFromJNI());
        try {
            InputStream data = getAssets().open(DATA_FILE);
            CSVReader reader = new CSVReader(new InputStreamReader(data));
            DB snappydb = DBFactory.open( this, "equivitalData");

            String[] nextLine;
            DataSnap ds;
            Boolean grabData = true;
            String keyToRetrieve = "";
            reader.readNext();
            while ((nextLine = reader.readNext()) != null) {

                ds = new DataSnap(parseFloat(!nextLine[34].isEmpty() ? nextLine[34] : "-1.0"),
                                  parseFloat(!nextLine[35].isEmpty() ? nextLine[35] : "-1.0"),
                                  nextLine[9],
                                  parseFloat(!nextLine[2].isEmpty() ? nextLine[2] : "-1.0"),
                                  parseFloat(!nextLine[4].isEmpty() ? nextLine[4] : "-1.0"),
                                  parseFloat(!nextLine[8].isEmpty() ? nextLine[8] : "-1.0"),
                                  parseFloat(!nextLine[60].isEmpty() ? nextLine[60] : "-1.0"),
                                  parseFloat(!nextLine[61].isEmpty() ? nextLine[61] : "-1.0"),
                                  parseFloat(!nextLine[62].isEmpty() ? nextLine[62] : "-1.0"),
                                  nextLine[10]);

                snappydb.put(nextLine[0], ds);
                if(grabData && !nextLine[10].isEmpty()) {//("04/03/2014 13:33:07.855"))
                    keyToRetrieve = nextLine[0];
                    grabData = false;
                }
            }

            DataSnap found = snappydb.get(keyToRetrieve, DataSnap.class);
            String whatIFound = "what I found: " + found.print();
            tv.setText(whatIFound);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
    public void main(String[] args) throws IOException{
    }
}

