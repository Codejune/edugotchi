package kr.ac.ssu.edugochi.activity;


import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
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

public class MeasureActivity extends AppCompatActivity {

    private static final String TAG = MeasureActivity.class.getSimpleName();
    private static String USERTABLE = "User.realm";
    private final static int init = 0;
    private final static int run = 1;
    private final static int pause = 2;
    private long pause_time;
    private int timer_status = init; //현재의 상태를 저장할변수를 초기화함.
    private int WN_status = init;
    private long base_time;
    private long out_time;

    private MediaPlayer player;
    private String subject;
    private TextView timer;
    private TextView title;

    private Realm userRealm;                // 기본 인스턴스
    private RealmConfiguration UserModuleConfig;
    private RealmResults<MeasureData> measureList;     // 측정 데이터 리스트
    private RealmResults<Character> characterList;   // 캐릭터 정보 리스트(0)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measure);

        Intent intent = getIntent();
        subject = intent.getStringExtra("subject");

        UserModuleConfig = new RealmConfiguration.Builder()
                .modules(new UserModule())
                .migration(new Migration())
                .schemaVersion(0)
                .name(USERTABLE)
                .build();

        userRealm = Realm.getInstance(UserModuleConfig);
        measureList = getMeasureList();
        Log.i(TAG, "measureList.size\t: " + measureList.size());
        characterList = getCharacterList();
        Log.i(TAG, "characterList.size\t: " + characterList.size());

        final MaterialButton record_btn = findViewById(R.id.record_btn);
        final MaterialButton stop_btn = findViewById(R.id.stop_btn);
        final MaterialButton WN_btn = findViewById(R.id.play_btn);
        timer = findViewById(R.id.timer);
        title = findViewById(R.id.measure_title);
        if(subject != null) title.setText(subject);

        String WN_Check = eduPreManger.getString(this, "white_noise");
        Log.d(TAG, WN_Check);
        if (WN_Check.equals("rain")) {
            player = MediaPlayer.create(this, R.raw.rain);
        } else if (WN_Check.equals("fire")) {
            player = MediaPlayer.create(this, R.raw.fire);
        } else {
            player = MediaPlayer.create(this, R.raw.rain);
        }

        WN_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (WN_status) {
                    case init: // 정지 상태
                        player.start();
                        WN_btn.setText("백색소음정지");
                        WN_btn.setIcon(getResources().getDrawable(R.drawable.ic_pause));
                        WN_status = run; //현재상태를 런상태로 변경
                        break;
                    case run: // 측정 상태
                        //player.stop();
                        player.pause();
                        WN_btn.setText("백색소음재생");
                        WN_btn.setIcon(getResources().getDrawable(R.drawable.ic_play_arrow));
                        WN_status = init;
                        break;
                }

            }
        });

        record_btn.setOnClickListener(new View.OnClickListener() {
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
                        record_btn.setText("일시정지");
                        record_btn.setIcon(getResources().getDrawable(R.drawable.ic_pause));
                        timer_status = run; //현재상태를 런상태로 변경
                        break;
                    case run: // 측정 상태
                        pause_time = SystemClock.elapsedRealtime();
                        measureTimer.removeMessages(0); //핸들러 메세지 제거
                        record_btn.setText("다시시작");
                        record_btn.setIcon(getResources().getDrawable(R.drawable.ic_play_arrow));
                        timer_status = pause;
                        break;
                    case pause:
                        long now = SystemClock.elapsedRealtime();
                        //잠깐 스톱워치를 멈췄다가 다시 시작하면 기준점이 변하게 되므로..
                        base_time += (now - pause_time);
                        measureTimer.sendEmptyMessage(0);
                        record_btn.setText("일시정지");
                        record_btn.setIcon(getResources().getDrawable(R.drawable.ic_play_arrow));
                        timer_status = run;
                        break;
                }
            }
        });

        stop_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("MeasureActivity", "onLongClick(" + timer_status + ")");
                switch (timer_status) {
                    case init:
                        timer.setText("00 : 00 : 00");
                        break;
                    case pause:
                    case run: // 측정 상태

                        measureTimer.removeMessages(0); //핸들러 메세지 제거
                        timer.setText(getTimeOut());
                        timer_status = init;
                        record_btn.setIcon(getResources().getDrawable(R.drawable.ic_play_arrow));
                        record_btn.setText("측정시작");
                        if (subject == null)
                            SelectSubject();
                        else NoSelectSubject();
                        break;
                    default:
                        break;
                }
            }
        });
    }

    // 측정 데이터 리스트 반환
    private RealmResults<MeasureData> getMeasureList() {
        return userRealm.where(MeasureData.class).findAllAsync();
    }

    // 캐릭터 데이터 리스트 반환
    private RealmResults<Character> getCharacterList() {
        return userRealm.where(Character.class).findAllAsync();
    }

    private void NoSelectSubject() {
        measureTransaction(subject);   // 측정 데이터 DB 저장
        characterTransaction(subject); // 캐릭터 정보 갱신
    }

    // 과목 미지정 측정시 과목 선택
    private void SelectSubject() {
        final RealmList<String> subjects = new RealmList<>();
        subjects.addAll(characterList.first().getSubject());
        subjects.remove("");

        final CharSequence[] items = subjects.toArray(new String[subjects.size()]);
        Arrays.sort(items);

        final List SelectedItems = new ArrayList();
        int defaultItem = 0;
        SelectedItems.add(defaultItem);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("과목 선택");
        builder.setSingleChoiceItems(items, defaultItem,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SelectedItems.clear();
                        SelectedItems.add(which);
                    }
                });
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
                SimpleDateFormat today_date = new SimpleDateFormat("yyyy/MM/dd", Locale.KOREA);
                MeasureData MeasureData = realm.createObject(MeasureData.class);
                MeasureData.setDate(today_date.format(Calendar.getInstance().getTime()));
                MeasureData.setTimeout(out_time*1000); // 원래 out_time
                MeasureData.setExp(out_time); // 원래 out_time/1000 >> 테스트 위해 기준 변경
                MeasureData.setSubject(selected);
                Log.i(TAG, "date\t\t: " + today_date.format(Calendar.getInstance().getTime()));
                Log.i(TAG, "timeout\t: " + out_time);
                Log.i(TAG, "exp\t\t: " + out_time / 1000);
            }
        });
        measureList = getMeasureList();
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

                characterList.first().setExp(exp + out_time / 1000);

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


    // 타이머 핸들러
    @SuppressLint("HandlerLeak")
    Handler measureTimer = new Handler() {
        public void handleMessage(Message msg) {
            timer.setText(getTimeOut());
            //sendEmptyMessage 는 비어있는 메세지를 Handler 에게 전송
            measureTimer.sendEmptyMessage(0);
        }
    };

    // 타이머 갱신
    @SuppressLint("DefaultLocale")
    private String getTimeOut() {
        long current_time = SystemClock.elapsedRealtime(); //애플리케이션이 실행되고나서 실제로 경과된 시간(??)^^;
        out_time = current_time - base_time;
        return String.format("%02d : %02d : %02d", out_time / 1 / 60 / 60, (out_time / 1) / 60 % 60, (out_time / 1) % 60 % 60); // 원래 1이아니라 1000 >>테스트 위해 변경
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.release();
            player = null;
        }
        userRealm.close();
    }
}

