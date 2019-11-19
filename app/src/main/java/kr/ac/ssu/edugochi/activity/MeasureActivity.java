package kr.ac.ssu.edugochi.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmResults;
import kr.ac.ssu.edugochi.object.MeasureTimeObject;
import kr.ac.ssu.edugochi.R;

public class MeasureActivity extends AppCompatActivity {

    private final static int init = 0;
    private final static int run = 1;
    private final static int pause = 2;
    private long pause_time;
    private int timer_status = init; //현재의 상태를 저장할변수를 초기화함.
    private long base_time;
    private long current_time;
    private long out_time;
    Realm mRealm;
    TextView timer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measure);

        // 넘어온 Intent 캐치
        Intent intent = getIntent();

        // Realm DB 등록
        Realm.init(this);
        mRealm = Realm.getDefaultInstance();

        final FloatingActionButton fab = findViewById(R.id.record_btn);
        // 측정 중 짧게 누르면 일시 정지
        // 측정 중 길게 누르면 정지
        fab.setOnClickListener(new View.OnClickListener() {
            /*
                init    : 정지
                run     : 측정 중
                pause   : 측정 일시정지
            */
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
                        pause_time = SystemClock.elapsedRealtime();
                        measureTimer.removeMessages(0); //핸들러 메세지 제거
                        fab.setImageResource(R.drawable.ic_pause_black_24dp);
                        timer_status = pause;
                        break;
                    case pause:
                        long now = SystemClock.elapsedRealtime();
                        //잠깐 스톱워치를 멈췄다가 다시 시작하면 기준점이 변하게 되므로..
                        base_time += (now - pause_time);
                        measureTimer.sendEmptyMessage(0);
                        fab.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                        timer_status = run;
                        break;
                }
            }
        });
        fab.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Log.d("MeasureActivity", "onLongClick(" + timer_status + ")");
                switch (timer_status) {
                    case init: // 정지 상태
                        return false;
                    case run: // 측정 상태
                        measureTimer.removeMessages(0); //핸들러 메세지 제거
                        fab.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                        // DB에 기록된 데이터 저장
                        mRealm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {

                                //    date      : 측정 완료된 년/월/일
                                //    timeout   : 측정된 시간량
                                //    exp       : 측정된 시간의 경험치

                                SimpleDateFormat today_date = new SimpleDateFormat("yyyy/MM/dd", Locale.KOREA);
                                MeasureTimeObject measureTimeObject = realm.createObject(MeasureTimeObject.class);
                                measureTimeObject.setDate(today_date.format(Calendar.getInstance().getTime()));
                                measureTimeObject.setTimeout(out_time);
                                measureTimeObject.setExp(out_time / 60);
                            }
                        });
                        timer.setText(getTimeOut());
                        timer_status = init;
                        return true;
                    default:
                        return false;
                }
            }
        });
    }

    // 타이머 핸들
    @SuppressLint("HandlerLeak")
    Handler measureTimer = new Handler(){
        public void handleMessage(Message msg){
            timer = findViewById(R.id.timer);
            timer.setText(getTimeOut());
            //sendEmptyMessage 는 비어있는 메세지를 Handler 에게 전송
            measureTimer.sendEmptyMessage(0);
        }
    };

    //현재시간을 계속 구해서 출력하는 메소드
    @SuppressLint("DefaultLocale")
    String getTimeOut(){
        current_time = SystemClock.elapsedRealtime(); //애플리케이션이 실행되고나서 실제로 경과된 시간(??)^^;
        out_time = current_time - base_time;
        return String.format("%02d:%02d:%02d", out_time / 1000 / 60, (out_time / 1000) % 60,(out_time % 1000) / 10);
    }

    /*
    @Override
    public void onBackPressed() {
        mRealm = Realm.getDefaultInstance();
        RealmResults<MeasureTimeObject> characterTransaction = mRealm.where(MeasureTimeObject.class).findAllSorted("exp");
        characterTransaction.first().setExp(characterTransaction.first().getExp() + out_time / 60);
        mRealm.commitTransaction();
    }
     */
}

