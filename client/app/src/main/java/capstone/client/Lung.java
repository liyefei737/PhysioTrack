package capstone.client;

import android.content.Intent;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

import static capstone.client.R.id.tab_core;
import static capstone.client.R.id.tab_heart;
import static capstone.client.R.id.tab_home;
import static capstone.client.R.id.tab_lung;
import static capstone.client.R.id.tab_skin;

public class Lung extends AppCompatActivity {
    private TextView messageView;
    private ImageButton imageButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lung);


        // Bottom navigation menu
        messageView =(TextView) findViewById(R.id.messageLung);
        BottomBar bottomBar=(BottomBar) findViewById(R.id.bottomBar);

        bottomBar.setDefaultTab(R.id.tab_lung);
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                messageView.setText(TabMessage.get(tabId, false));
                if(tabId==tab_home){
                    //Toast.makeText(getApplicationContext(), "Heart Rate Page",Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(Lung.this, MainActivity.class);
                    startActivity(intent);
                }
                if(tabId == tab_heart){
                    //Toast.makeText(getApplicationContext(), "Skin Temp page",Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(Lung.this, Heart.class);
                    startActivity(intent);
                }
                if(tabId == tab_skin){
                    //Toast.makeText(getApplicationContext(), "Skin Temp page",Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(Lung.this, Skin.class);
                    startActivity(intent);
                }
                if(tabId == tab_core){
                    //Toast.makeText(getApplicationContext(), "Skin Temp page",Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(Lung.this, Core.class);
                    startActivity(intent);
                }
            }
        });
        imageButton = (ImageButton)findViewById(R.id.imageButtonLung);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentLoader = new Intent(Lung.this, Soldier.class);
                startActivity(intentLoader);
            }
        });

    }
}
