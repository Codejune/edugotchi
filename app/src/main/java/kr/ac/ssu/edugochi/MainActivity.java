package kr.ac.ssu.edugochi;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.LayoutInflater;
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

import org.w3c.dom.Text;

import kr.ac.ssu.edugochi.fragment.MainFragment;
import kr.ac.ssu.edugochi.fragment.TimelineFragment;
import kr.ac.ssu.edugochi.fragment.TodoFragment;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    TextView title;
    TextView timer;
    MainFragment fragment;
    final Fragment mainFragment = new MainFragment();
    final Fragment timelineFragment = new TimelineFragment();
    final Fragment todoFragment = new TodoFragment();
    final FragmentManager fragmentManager = getSupportFragmentManager();
    Fragment active = mainFragment; // 현재 활성화된 Fragment
    final static int init = 0;
    final static int run = 1;
    final static int pause = 2;
    int timer_status = init; //현재의 상태를 저장할변수를 초기화함.
    long base_time;
    long pause_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 화면 타이틀 id 연결
        title = findViewById(R.id.title);

        // 툴바 설정
        Toolbar toolbar = findViewById(R.id.bottom_app_bar);
        setSupportActionBar(toolbar);

        // Fragment 등록
        fragmentManager.beginTransaction().add(R.id.content_fragment_layout, todoFragment, "todo").hide(todoFragment).commit();
        fragmentManager.beginTransaction().add(R.id.content_fragment_layout, timelineFragment, "timeline").hide(timelineFragment).commit();
        fragmentManager.beginTransaction().add(R.id.content_fragment_layout, mainFragment, "main").commit();

        //  Floating Action Button 클릭 이벤트 처리
        final FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (timer_status) {
                    case init: // 정지 상태
                        base_time = SystemClock.elapsedRealtime();
                        //핸들러에 빈 메세지를 보내서 호출
                        measureTimer.sendEmptyMessage(0);
                        fab.setImageResource(R.drawable.ic_pause_black_24dp);
                        timer_status = run; //현재상태를 런상태로 변경
                        break;
                    case run: // 측정 상태
                        measureTimer.removeMessages(0); //핸들러 메세지 제거
                        pause_time = SystemClock.elapsedRealtime();
                        fab.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                        timer.setText(getTimeOut());
                        timer_status = init;
                        break;
                    case pause:
                        break;
                }
            }
        });
    }

    // 타이머 핸들
    @SuppressLint("HandlerLeak")
    Handler measureTimer = new Handler(){
        public void handleMessage(Message msg){
            fragment = (MainFragment) fragmentManager.findFragmentByTag("main");
            timer = fragment.getView().findViewById(R.id.timer);
            timer.setText(getTimeOut());
            //sendEmptyMessage 는 비어있는 메세지를 Handler 에게 전송
            measureTimer.sendEmptyMessage(0);
        }
    };

    //현재시간을 계속 구해서 출력하는 메소드
    @SuppressLint("DefaultLocale")
    String getTimeOut(){
        long now = SystemClock.elapsedRealtime(); //애플리케이션이 실행되고나서 실제로 경과된 시간(??)^^;
        long outTime = now - base_time;
        return String.format("%02d:%02d:%02d", outTime/1000 / 60, (outTime/1000)%60,(outTime%1000)/10);
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
                Toast.makeText(getApplicationContext(), "TODO 리스트",
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
