package kr.ac.ssu.edugochi;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.bottom_app_bar);
        setSupportActionBar(toolbar);

        final FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            boolean status = false;
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "버튼 눌림",
                        Toast.LENGTH_SHORT).show();
                if(!status) {
                    fab.setImageResource(R.drawable.ic_pause_black_24dp);
                    status = true;
                } else {
                    fab.setImageResource(R.drawable.ic_play_arrow_white_24dp);
                    status = false;
                }
            }
        });
    }


    @Override
     public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.bottomappbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                final BottomNavigationDrawerFragment bottomNavigationDrawerFragment = new BottomNavigationDrawerFragment();
                bottomNavigationDrawerFragment.show(getSupportFragmentManager(), bottomNavigationDrawerFragment.getTag());
                return true;
            case R.id.app_bar_home:
                findViewById(R.id.content_main).setVisibility(View.VISIBLE);
                findViewById(R.id.content_timeline).setVisibility(View.GONE);
                findViewById(R.id.content_todo).setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), "메인",
                        Toast.LENGTH_SHORT).show();
                return true;
            case R.id.app_bar_timeline:
                findViewById(R.id.content_main).setVisibility(View.GONE);
                findViewById(R.id.content_timeline).setVisibility(View.VISIBLE);
                findViewById(R.id.content_todo).setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), "타임라인",
                        Toast.LENGTH_SHORT).show();
                return true;
            case R.id.app_bar_todo:
                findViewById(R.id.content_main).setVisibility(View.GONE);
                findViewById(R.id.content_timeline).setVisibility(View.GONE);
                findViewById(R.id.content_todo).setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(), "TODO리스트",
                        Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }
}
