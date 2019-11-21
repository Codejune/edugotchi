package kr.ac.ssu.edugochi.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import kr.ac.ssu.edugochi.R;
import kr.ac.ssu.edugochi.fragment.MainFragment;
import kr.ac.ssu.edugochi.fragment.SettingFragment;
import kr.ac.ssu.edugochi.fragment.TimelineFragment;
import kr.ac.ssu.edugochi.fragment.TodoFragment;
import kr.ac.ssu.edugochi.listener.CustomNavigationChangeListener;
import kr.ac.ssu.edugochi.view.CustomNavigationLinearView;

public class MainActivity extends AppCompatActivity {

    TextView title;
    LinearLayout titlebar;
    final Fragment mainFragment = new MainFragment();
    final Fragment timelineFragment = new TimelineFragment();
    final Fragment todoFragment = new TodoFragment();
    final Fragment settingFragment = new SettingFragment();
    final FragmentManager fragmentManager = getSupportFragmentManager();
    Fragment active = mainFragment; // 현재 활성화된 Fragment

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 화면 타이틀 id 연결
        title = findViewById(R.id.title);
        titlebar = findViewById(R.id.title_bar);

        // Fragment 등록
        fragmentManager.beginTransaction().add(R.id.content_fragment_layout, settingFragment, "setting").hide(settingFragment).commit();
        fragmentManager.beginTransaction().add(R.id.content_fragment_layout, todoFragment, "todo").hide(todoFragment).commit();
        fragmentManager.beginTransaction().add(R.id.content_fragment_layout, timelineFragment, "timeline").hide(timelineFragment).commit();
        fragmentManager.beginTransaction().add(R.id.content_fragment_layout, mainFragment, "main").commit();

        final CustomNavigationLinearView customNavigationLinearView = findViewById(R.id.bottom_navigation_view_linear);

        customNavigationLinearView.setNavigationChangeListener(new CustomNavigationChangeListener() {
            @Override
            public void onNavigationChanged(View view, int position) {
                switch (view.getId()) {
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
                        title.setText("Settings");
                        fragmentManager.beginTransaction().hide(active).show(settingFragment).commit();
                        active = settingFragment;
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
}
