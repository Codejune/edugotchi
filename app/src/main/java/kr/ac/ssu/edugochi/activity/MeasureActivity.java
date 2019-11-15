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
import kr.ac.ssu.edugochi.MeasureTimeObject;
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
                        measureTimer.removeMessages(0); //핸들러 메세지 제거
                        pause_time = SystemClock.elapsedRealtime();
                        fab.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                        // DB에 기록된 데이터 저장
                        mRealm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {

                                //    date      : 측정 완료된 년/월/일/시/분/초
                                //    timeout   : 측정된 시간량
                                //    exp       : 측정된 시간의 경험치

                                MeasureTimeObject measureTimeObject = realm.createObject(MeasureTimeObject.class);
                                measureTimeObject.setDate(Calendar.getInstance().getTime());
                                measureTimeObject.setTimeout(out_time);
                                measureTimeObject.setExp(out_time/60);

                                SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.KOREA);
                                Log.d("MeasureActivity", formatter.format(measureTimeObject.getDate()) + " / " + measureTimeObject.getExp());
                            }
                        });
                        timer.setText(getTimeOut());
                        timer_status = init;
                        break;
                    case pause:

                        break;
                }
                MeasureTimeObject test = mRealm.where(MeasureTimeObject.class).findFirst();
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.KOREA);
                Log.d("MeasureActivity", formatter.format(test.getDate()) + " / " + test.getExp());
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
        return String.format("%02d:%02d:%02d", out_time/1000 / 60, (out_time/1000)%60,(out_time%1000)/10);
    }
}
