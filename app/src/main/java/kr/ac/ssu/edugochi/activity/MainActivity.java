package kr.ac.ssu.edugochi.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import kr.ac.ssu.edugochi.R;
import kr.ac.ssu.edugochi.eduPreManger;
import kr.ac.ssu.edugochi.fragment.MainFragment;
import kr.ac.ssu.edugochi.fragment.SettingFragment;
import kr.ac.ssu.edugochi.fragment.TimelineFragment;
import kr.ac.ssu.edugochi.fragment.TodoFragment;
import kr.ac.ssu.edugochi.listener.CustomNavigationChangeListener;
import kr.ac.ssu.edugochi.view.CustomNavigationLinearView;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private final Fragment mainFragment = new MainFragment();
    private final Fragment timelineFragment = new TimelineFragment();
    private final Fragment todoFragment = new TodoFragment();
    private final Fragment settingFragment = new SettingFragment();
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private CustomNavigationLinearView customNavigationLinearView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        boolean themeCheck;

        themeCheck = eduPreManger.getBoolean(this, "darkMode");

        if (themeCheck) {
            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_YES);
        }
        else {
            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_NO);
        }

        // 툴바 설정
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Fragment 할당
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

    // 상단 메뉴 아이템 설정
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        switch (customNavigationLinearView.getCurrentActiveItemPosition()) {
            case 0:
                inflater.inflate(R.menu.home_menu, menu);
                break;
            case 1:
                inflater.inflate(R.menu.timeline_menu, menu);
                break;
            case 2:
                inflater.inflate(R.menu.todo_menu, menu);
                break;
            case 3:
                break;
        }
        return true;
    }

    // 각 Bottom Navigation Item 클릭 시 해당 Fragment 이동
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

    @Override
    public void onBackPressed() {
        finish();
        System.exit(0);
        super.onBackPressed();
    }
}
