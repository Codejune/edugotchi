package kr.ac.ssu.edugochi.activity;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import io.realm.RealmResults;
import kr.ac.ssu.edugochi.R;
import kr.ac.ssu.edugochi.eduPreManger;
import kr.ac.ssu.edugochi.object.Character;
import kr.ac.ssu.edugochi.object.MeasureData;
import kr.ac.ssu.edugochi.realm.module.UserModule;
import kr.ac.ssu.edugochi.realm.utils.Migration;
import kr.ac.ssu.edugochi.service.ScreenService;
import kr.ac.ssu.edugochi.service.TimerService;

public class MeasureActivity extends AppCompatActivity {

    private static final String TAG = MeasureActivity.class.getSimpleName();

    // Realm
    private static String USERTABLE = "User.realm";
    private Realm userRealm;                        // User.realm 인스턴스
    private RealmConfiguration UserModuleConfig;    // User.realm 모듈 설정
    private RealmResults<Character> characterList;  // 캐릭터 정보 Realm 리스트(0)

    // Measure
    private final static int init = 0;  // 정지
    private final static int run = 1;   // 측정 중
    private final static int pause = 2; // 일시정지
    private int timer_status = init;    // 현재 상태
    private long out_time;              // 측정된 시간
    private String subject;             // 과목 이름

    // White Noise
    private MediaPlayer player;     // 백색소음
    private int WN_status = init;   // 백색소음 재생 상태

    // View
    private TextView timer;
    private TextView title;
    private MaterialButton record_btn;
    private MaterialButton stop_btn;
    private MaterialButton WN_btn;

    TimerService timerService = null;
    private long hour;
    private long minute;
    private long second;
    private ProgressBar progressBarCircle;
    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measure);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

        // 넘어온 Intent
        Intent intent = getIntent();
        // Realm 초기 설정
        RealmInit();
        CheckNoise();

        // 뷰 연결
        timer = findViewById(R.id.timer);
        record_btn = findViewById(R.id.record_btn);
        stop_btn = findViewById(R.id.stop_btn);
        WN_btn = findViewById(R.id.play_btn);
        title = findViewById(R.id.measure_title);
        progressBarCircle = findViewById(R.id.progressBarCircle);

        countDownTimer = new CountDownTimer(1000, 1) {
            @Override
            public void onTick(long millisUntilFinished) {
                progressBarCircle.setProgress(1000 - (int) (millisUntilFinished));
            }

            @Override
            public void onFinish() {
            }
        };

        // Intent에 포함된 과목 이름
        subject = intent.getStringExtra("subject");

        if (subject != null)
            title.setText(subject);

        final ServiceConnection connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                timerService = ((TimerService.LocalBinder) iBinder).getTimerService();
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {

            }
        };

        // 측정 시작 버튼 클릭
        record_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (timer_status) {
                    case init:  // 측정시작
                        // 측정 시작 시간 설정
                        Log.d(TAG, "측정시작");
                        Intent counterIntent = new Intent(MeasureActivity.this, TimerService.class);
                        bindService(counterIntent, connection, BIND_AUTO_CREATE);
                        timer_status = run;
                        new Thread(new GetCountThread()).start();
                        //new Thread(new ProgressThread()).start();
                        timer.setText("00 : 00 : 00");
                        record_btn.setIcon(getResources().getDrawable(R.drawable.ic_pause));
                        Intent intent = new Intent(getApplicationContext(), ScreenService.class);
                        startService(intent);
                        break;
                    case run:   // 일시정지
                        Log.d(TAG, "측정 일시정지");
                        timerService.setStatus(pause);
                        timer_status = pause;
                        //countDownTimer.cancel();
                        progressBarCircle.setProgress(1000);
                        record_btn.setText("다시시작");
                        record_btn.setIcon(getResources().getDrawable(R.drawable.ic_play_arrow));
                        // 현재 상태를 일시정지 상태로 변경
                        break;
                    case pause: // 다시시작
                        // 재시작 시간 설정
                        Log.d(TAG, "다시시작");
                        timer_status = run;
                        timerService.setStatus(run);
                        new Thread(new GetCountThread()).start();
                        record_btn.setText("일시정지");
                        record_btn.setIcon(getResources().getDrawable(R.drawable.ic_play_arrow));
                        // 현재 상태를 측정 중 상태로 변경
                        break;
                }
            }
        });

        // 측정 종료 버튼 클릭
        stop_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "측정 종료");
                switch (timer_status) {
                    case init:  // 값 초기화
                        timer.setText("00 : 00 : 00");
                        break;
                    case pause:
                    case run:   // 측정 종료 및 데이터 저장
                        timer_status = init;
                        unbindService(connection);
                        countDownTimer.cancel();
                        progressBarCircle.setProgress(1000);
                        record_btn.setIcon(getResources().getDrawable(R.drawable.ic_play_arrow));
                        record_btn.setText("측정시작");
                        // 만약 Intent로 넘어온 과목 이름이 없을 경우
                        if (subject == null)
                            // 기존에 존재하던 과목에 저장
                            SelectSubject();
                            // Intent에 넘어온 과목에 저장
                        else NoSelectSubject();
                        Intent intent = new Intent(getApplicationContext(), ScreenService.class);
                        stopService(intent);

                        break;
                }
            }
        });

        // 백색소음 버튼 클릭
        WN_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (WN_status) {
                    case init: // 재 상태
                        Log.d(TAG, "재생");
                        player.start();
                        WN_btn.setText("백색소음정지");
                        WN_btn.setIcon(getResources().getDrawable(R.drawable.ic_pause));
                        WN_status = run; //현재상태를 런상태로 변경
                        break;
                    case run: // 정지 상태
                        // player.stop();
                        Log.d(TAG, "정지");
                        player.pause();
                        WN_btn.setText("백색소음재생");
                        WN_btn.setIcon(getResources().getDrawable(R.drawable.ic_play_arrow));
                        WN_status = init;
                        break;
                }

            }
        });
    }

    // Realm 초기 설정
    private void RealmInit() {
        // User.realm 모듈 설정
        UserModuleConfig = new RealmConfiguration.Builder()
                .modules(new UserModule())
                .migration(new Migration())
                .schemaVersion(0)
                .name(USERTABLE)
                .build();
        userRealm = Realm.getInstance(UserModuleConfig);

        // 각 리스트 데이터 로드
        characterList = getCharacterList();
    }

    // 캐릭터 데이터 리스트 반환
    private RealmResults<Character> getCharacterList() {
        return userRealm.where(Character.class).findAllAsync();
    }

    private void CheckNoise() {
        String WN_Check;
        WN_Check = eduPreManger.getString(this, "white_noise");
        Log.d(TAG, WN_Check);

        if (WN_Check.equals("rain")) {
            player = MediaPlayer.create(this, R.raw.rain);
        } else if (WN_Check.equals("fire")) {
            player = MediaPlayer.create(this, R.raw.fire);
        } else {
            player = MediaPlayer.create(this, R.raw.rain);
        }
    }

    private void NoSelectSubject() {
        measureTransaction(subject);   // 측정 데이터 DB 저장
        characterTransaction(subject); // 캐릭터 정보 갱신
    }

    // 과목 미지정 측정시 과목 선택
    private void SelectSubject() {
        final RealmList<String> subjects = new RealmList<>();
        final List SelectedItems = new ArrayList();
        final CharSequence[] items;
        int defaultItem = 0;

        // 모든 과목을 리스트에 추가
        subjects.addAll(characterList.first().getSubject());
        // 과목이름이 없을 경우 항목에서 제외
        subjects.remove("");
        // RealmList를 String 배열로 변환
        items = subjects.toArray(new String[subjects.size()]);
        // 이름 순으로 정렬
        Arrays.sort(items);
        // 선택된 항목으로 보여줄 과목
        SelectedItems.add(defaultItem);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("과목 선택");
        // 항목 클릭
        builder.setSingleChoiceItems(items, defaultItem,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SelectedItems.clear();
                        SelectedItems.add(which);
                    }
                });
        // 과목 지정 후 확인
        builder.setPositiveButton("확인",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (!SelectedItems.isEmpty()) {
                            int index = (int) SelectedItems.get(0);
                            measureTransaction(subjects.get(index));
                            characterTransaction(subjects.get(index));
                        }
                    }
                });
        // 과목 지정 취소
        builder.setNegativeButton("취소",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.show();
    }

    // 측정 데이터 DB 저장
    private void measureTransaction(final String selected) {
        Log.d(TAG, "측정 데이터 추가");
        userRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                //    subject   : 측정된 과목명
                //    date      : 측정 완료된 년/월/일
                //    timeout   : 측정된 시간량
                //    exp       : 측정된 시간의 경험치
                if (out_time > 0) {
                    SimpleDateFormat today_date = new SimpleDateFormat("yyyy/MM/dd", Locale.KOREA);
                    MeasureData MeasureData = realm.createObject(MeasureData.class);
                    MeasureData.setDate(today_date.format(Calendar.getInstance().getTime()));
                    MeasureData.setTimeout(out_time); // 원래 out_time
                    MeasureData.setExp(out_time); // 원래 out_time/1000 >> 테스트 위해 기준 변경
                    MeasureData.setSubject(selected);
                    Log.i(TAG, "date\t\t: " + today_date.format(Calendar.getInstance().getTime()));
                    Log.i(TAG, "timeout\t: " + out_time);
                    Log.i(TAG, "exp\t\t: " + out_time);
                }
            }
        });
    }

    // 캐릭터 정보 갱신
    private void characterTransaction(final String selected) {
        Log.d(TAG, "캐릭터 정보 갱신");
        userRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                long exp = characterList.first().getExp();
                boolean checkSubject = true;
                RealmList<String> subjects = new RealmList<>();
                subjects.addAll(characterList.first().getSubject());

                characterList.first().setExp(exp + out_time);

                Log.d(TAG, "subjects.size: " + subjects.size());
                for (int i = 0; i < subjects.size(); i++) {
                    Log.d(TAG, "subjects[" + i + "]: " + subjects.get(i));
                    if (subjects.get(i).equals(selected)) {
                        checkSubject = false;
                        Log.d(TAG, subject + " is already exists");
                        break;
                    }
                }
                if (checkSubject) {
                    subjects.add(selected);
                    Log.d(TAG, "subjects.size: " + subjects.size());
                    Log.d(TAG, "subjects.first: " + subjects.get(0));
                    characterList.first().setSubject(subjects);
                }
            }
        });
        characterList = getCharacterList();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.release();
            player = null;
        }
    }

    private class GetCountThread implements Runnable {
        @Override
        public void run() {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            while (timer_status == run) {
                try {
                    countDownTimer.start();
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                out_time = timerService.getCount();
                hour = out_time / 60 / 60;
                minute = out_time / 60 % 60;
                second = out_time % 60 % 60;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        timer.setText(String.format("%02d : %02d : %02d", hour, minute, second));
                        progressBarCircle.setProgress(0);
                    }
                });
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (timer_status == init) super.onBackPressed();
        //super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();

        ActivityManager activityManager = (ActivityManager) getApplicationContext()
                .getSystemService(Context.ACTIVITY_SERVICE);

        activityManager.moveTaskToFront(getTaskId(), 0);
    }
}

