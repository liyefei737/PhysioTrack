package capstone.client;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent Serviceintent = new Intent();
        Serviceintent.setClass(this, BackgroundDataSim.class);
        startService(Serviceintent);
    }
}
