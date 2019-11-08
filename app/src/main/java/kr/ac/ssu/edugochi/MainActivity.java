package kr.ac.ssu.edugochi;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import kr.ac.ssu.edugochi.fragment.MainFragment;
import kr.ac.ssu.edugochi.fragment.TimelineFragment;
import kr.ac.ssu.edugochi.fragment.TodoFragment;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    TextView title;
    final Fragment mainFragment = new MainFragment();
    final Fragment timelineFragment = new TimelineFragment();
    final Fragment todoFragment = new TodoFragment();
    final FragmentManager fragmentManager = getSupportFragmentManager();
    Fragment active = mainFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 툴바 설정
        Toolbar toolbar = findViewById(R.id.bottom_app_bar);
        setSupportActionBar(toolbar);

        fragmentManager.beginTransaction().add(R.id.content_fragment_layout, todoFragment, "3").hide(todoFragment).commit();
        fragmentManager.beginTransaction().add(R.id.content_fragment_layout, timelineFragment, "2").hide(timelineFragment).commit();
        fragmentManager.beginTransaction().add(R.id.content_fragment_layout, mainFragment, "1").commit();

        title = findViewById(R.id.title);

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

    // 네비게이션 메뉴 생성
    @Override
     public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.bottomappbar_menu, menu);
        return true;
    }

    // 네비게이션 메뉴 아이콘 클릭 이벤트 처리
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                final BottomNavigationDrawerFragment bottomNavigationDrawerFragment = new BottomNavigationDrawerFragment();
                bottomNavigationDrawerFragment.show(getSupportFragmentManager(), bottomNavigationDrawerFragment.getTag());
                return true;
            case R.id.app_bar_home:
                title.setText("Home");
                fragmentManager.beginTransaction().hide(active).show(mainFragment).commit();
                active = mainFragment;
                Toast.makeText(getApplicationContext(), "메인",
                        Toast.LENGTH_SHORT).show();
                return true;
            case R.id.app_bar_timeline:
                title.setText("Timeline");
                fragmentManager.beginTransaction().hide(active).show(timelineFragment).commit();
                active = timelineFragment;
                Toast.makeText(getApplicationContext(), "타임라인",
                        Toast.LENGTH_SHORT).show();
                return true;
            case R.id.app_bar_todo:
                title.setText("Todo");
                fragmentManager.beginTransaction().hide(active).show(todoFragment).commit();
                active = todoFragment;
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
