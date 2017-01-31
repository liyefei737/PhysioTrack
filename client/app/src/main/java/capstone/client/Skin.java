package capstone.client;

import android.content.Intent;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

import static capstone.client.R.id.tab_core;
import static capstone.client.R.id.tab_heart;
import static capstone.client.R.id.tab_home;
import static capstone.client.R.id.tab_lung;
import static capstone.client.R.id.tab_skin;

public class Skin extends AppCompatActivity {

    private TextView messageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skin);
        // Bottom navigation menu
        messageView =(TextView) findViewById(R.id.messageSkin);
        BottomBar bottomBar=(BottomBar) findViewById(R.id.bottomBar);

        bottomBar.setDefaultTab(R.id.tab_home);
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                messageView.setText(TabMessage.get(tabId, false));
                if(tabId==tab_home){
                    //Toast.makeText(getApplicationContext(), "Heart Rate Page",Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(Skin.this, MainActivity.class);
                    startActivity(intent);
                }
                if(tabId == tab_lung){
                    //Toast.makeText(getApplicationContext(), "Skin Temp page",Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(Skin.this, Lung.class);
                    startActivity(intent);
                }
                if(tabId == tab_heart){
                    //Toast.makeText(getApplicationContext(), "Skin Temp page",Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(Skin.this, Heart.class);
                    startActivity(intent);
                }
                if(tabId == tab_core){
                    //Toast.makeText(getApplicationContext(), "Skin Temp page",Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(Skin.this, Core.class);
                    startActivity(intent);
                }
            }
        });

    }



}
