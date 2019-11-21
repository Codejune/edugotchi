package kr.ac.ssu.edugochi.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import kr.ac.ssu.edugochi.R;
import kr.ac.ssu.edugochi.fragment.MainFragment;
import kr.ac.ssu.edugochi.fragment.SettingFragment;
import kr.ac.ssu.edugochi.fragment.TimelineFragment;
import kr.ac.ssu.edugochi.fragment.TodoFragment;
import kr.ac.ssu.edugochi.listener.CustomNavigationChangeListener;
import kr.ac.ssu.edugochi.view.CustomNavigationLinearView;

public class MainActivity extends AppCompatActivity {

    TextView title;
    Toolbar toolbar;
    final Fragment mainFragment = new MainFragment();
    final Fragment timelineFragment = new TimelineFragment();
    final Fragment todoFragment = new TodoFragment();
    final Fragment settingFragment = new SettingFragment();
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    CustomNavigationLinearView customNavigationLinearView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 화면 타이틀 id 연결
        title = findViewById(R.id.title);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        customNavigationLinearView = findViewById(R.id.bottom_navigation_view_linear);

        customNavigationLinearView.setNavigationChangeListener(new CustomNavigationChangeListener() {
            @Override
            public void onNavigationChanged(View view, int position) {
                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentManager.popBackStack();
                switch (position) {
                    case 0:
                        toolbar.setTitle("Home");
                        fragmentTransaction.replace(R.id.content_fragment_layout, mainFragment);
                        Log.d("navigation", "home");
                        break;
                    case 1:
                        toolbar.setTitle("Timeline");
                        fragmentTransaction.replace(R.id.content_fragment_layout, timelineFragment);
                        Log.d("navigation", "timeline");
                        break;
                    case 2:
                        toolbar.setTitle("Todo");
                        fragmentTransaction.replace(R.id.content_fragment_layout, todoFragment);
                        Log.d("navigation", "todo");
                        break;
                    case 3:
                        toolbar.setTitle("Settings");
                        fragmentTransaction.replace(R.id.content_fragment_layout, settingFragment);
                        Log.d("navigation", "setting");
                        break;
                }
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
    }

    // 네비게이션 메뉴 생성
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        switch (customNavigationLinearView.getCurrentActiveItemPosition()) {
            case 0:
                inflater.inflate(R.menu.home_menu, menu);
                break;
            case 1:
                break;
            case 2:
                inflater.inflate(R.menu.todo_menu, menu);
                break;
            case 3:
                break;
        }
        return true;
    }

    @Override
        protected void onResume() {
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.addToBackStack(null);
        Log.d("onResume", String.valueOf(customNavigationLinearView.getCurrentActiveItemPosition()));

        switch (customNavigationLinearView.getCurrentActiveItemPosition()) {
            case 0:
                toolbar.setTitle("Home");
                fragmentTransaction.replace(R.id.content_fragment_layout, mainFragment);
                break;
            case 1:
                toolbar.setTitle("Timeline");
                fragmentTransaction.replace(R.id.content_fragment_layout, timelineFragment);
                break;
            case 2:
                toolbar.setTitle("Todo");
                fragmentTransaction.replace(R.id.content_fragment_layout, todoFragment);
                break;
            case 3:
                toolbar.setTitle("Settings");
                fragmentTransaction.replace(R.id.content_fragment_layout, settingFragment);
                break;
        }
        fragmentTransaction.commit();
        super.onResume();
    }
}
