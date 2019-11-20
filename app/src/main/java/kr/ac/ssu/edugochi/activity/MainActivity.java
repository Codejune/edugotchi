package kr.ac.ssu.edugochi.activity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import kr.ac.ssu.edugochi.R;
import kr.ac.ssu.edugochi.fragment.MainFragment;
import kr.ac.ssu.edugochi.fragment.TimelineFragment;
import kr.ac.ssu.edugochi.fragment.TodoFragment;
import kr.ac.ssu.edugochi.listener.CustomNavigationChangeListener;
import kr.ac.ssu.edugochi.view.CustomNavigationLinearView;
import kr.ac.ssu.edugochi.view.CustomToggleView;

public class MainActivity extends AppCompatActivity {

    TextView title;
    LinearLayout titlebar;
    final Fragment mainFragment = new MainFragment();
    final Fragment timelineFragment = new TimelineFragment();
    final Fragment todoFragment = new TodoFragment();
    final FragmentManager fragmentManager = getSupportFragmentManager();
    Fragment active = mainFragment; // 현재 활성화된 Fragment

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 화면 타이틀 id 연결
        title = findViewById(R.id.title);
        titlebar = findViewById(R.id.title_bar);
        //titlebar.bringToFront();

        // Fragment 등록
        fragmentManager.beginTransaction().add(R.id.content_fragment_layout, todoFragment, "todo").hide(todoFragment).commit();
        fragmentManager.beginTransaction().add(R.id.content_fragment_layout, timelineFragment, "timeline").hide(timelineFragment).commit();
        fragmentManager.beginTransaction().add(R.id.content_fragment_layout, mainFragment, "main").commit();

        final CustomNavigationLinearView customNavigationLinearView = findViewById(R.id.bottom_navigation_view_linear);

        customNavigationLinearView.setNavigationChangeListener(new CustomNavigationChangeListener() {
            @Override
            public void onNavigationChanged(View view, int position) {
                switch(view.getId()) {
                    case R.id.app_bar_home:
                        title.setText("Home");
                        fragmentManager.beginTransaction().hide(active).show(mainFragment).commit();
                        active = mainFragment;
                        Log.d("navigation", "home");
                        return;
                    case R.id.app_bar_timeline:
                        title.setText("Timeline");
                        fragmentManager.beginTransaction().hide(active).show(timelineFragment).commit();
                        active = timelineFragment;
                        Log.d("navigation", "timeline");
                        return;
                    case R.id.app_bar_todo:
                        title.setText("Todo");
                        fragmentManager.beginTransaction().hide(active).show(todoFragment).commit();
                        active = todoFragment;
                        Log.d("navigation", "todo");
                        return;
                    case R.id.app_bar_setting:
                        Log.d("navigation", "setting");
                        return;
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

    /*
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
                Toast.makeText(getApplicationContext(), "TODO 리스트",
                        Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    */
}
