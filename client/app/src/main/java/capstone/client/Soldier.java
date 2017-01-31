package capstone.client;

import android.content.Intent;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabReselectListener;
import com.roughike.bottombar.OnTabSelectListener;

import static capstone.client.R.id.tab_core;
import static capstone.client.R.id.tab_heart;
import static capstone.client.R.id.tab_home;
import static capstone.client.R.id.tab_lung;
import static capstone.client.R.id.tab_skin;

public class Soldier extends AppCompatActivity {
    private TextView messageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_soldier);
    }
}
