package kr.ac.ssu.edugochi.fragment;

import androidx.appcompat.app.AlertDialog;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import io.realm.RealmResults;
import kr.ac.ssu.edugochi.R;
import kr.ac.ssu.edugochi.activity.MeasureActivity;
import kr.ac.ssu.edugochi.adapter.SubjectListAdapter;
import kr.ac.ssu.edugochi.eduPreManger;
import kr.ac.ssu.edugochi.object.Character;
import kr.ac.ssu.edugochi.object.ExpTable;
import kr.ac.ssu.edugochi.object.MeasureData;
import kr.ac.ssu.edugochi.realm.module.ExpModule;
import kr.ac.ssu.edugochi.realm.module.UserModule;
import kr.ac.ssu.edugochi.realm.utils.Migration;
import kr.ac.ssu.edugochi.view.CustomListView;

public class MainFragment extends Fragment {
    private static final String TAG = MainFragment.class.getSimpleName();
    private static String EXPTABLE = "ExpTable.realm";
    private static String USERTABLE = "User.realm";

    private Realm userRealm;                // 기본 인스턴스
    private Realm expRealm;             // 경험치 테이블 인스턴스
    private RealmConfiguration UserModuleConfig;
    private RealmConfiguration ExpModuleConfig;
    private RealmResults<MeasureData> measureList;     // 측정 데이터 리스트
    private RealmResults<Character> characterList;   // 캐릭터 정보 리스트(0)
    private RealmResults<ExpTable> expList;        // 경험치 테이블
    private SubjectListAdapter listAdapter;

    private ImageView character_img;            // 캐릭터 이미지
    private ProgressBar expbar;         // 경험치 막대
    private TextView exptext;           // 경험치 텍스트
    private TextView character_name;    // 캐릭터 이름
    private TextView character_lv;      // 캐릭터 레벨
    private MaterialButton record_btn;          // 측정하기 버튼
    private MaterialButton addsubject_btn;          // 측정하기 버튼
    private CustomListView subject_listview;

    private int currentLv;     // 현재 레벨
    private long currentExp;    // 현재 경험치
    private int nextLv;        // 다음 레벨
    private long nextExp;       // 다음 레벨 경험치
    private long nextInterval;  // 다음 레벨과의 경험치 차이

    private String ch_check;
    private String evo_ch_check;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UserModuleConfig = new RealmConfiguration.Builder()
                .modules(new UserModule())
                .migration(new Migration())
                .schemaVersion(0)
                .name(USERTABLE)
                .build();
        ExpModuleConfig = new RealmConfiguration.Builder()
                .modules(new ExpModule())
                .name(EXPTABLE)
                .assetFile(EXPTABLE)
                .readOnly()
                .build();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        character_img = view.findViewById(R.id.character);
        character_name = view.findViewById(R.id.name);
        character_lv = view.findViewById(R.id.lv);
        expbar = view.findViewById(R.id.exp_bar);
        exptext = view.findViewById(R.id.exp_text);
        record_btn = view.findViewById(R.id.record);
        subject_listview = view.findViewById(R.id.subject_list);
        addsubject_btn = view.findViewById(R.id.subject_add);

        //Realm 초기 설정
        RealmInit();

        measureList = getMeasureList();
        characterList = getCharacterList();
        expList = getExpList();

        try {
            currentLv = (int) characterList.first().getLv();
        } catch (Exception e) {
            // 캐릭터 데이터베이스 생성
            initCharacterData();
            currentLv = (int) characterList.first().getLv();
            // 캐릭터 정보 동기화
            SyncCharacterInfo();
        }
        UpdateCharacter(character_img);
        // 측정 버튼 리스너
        record_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MeasureActivity.class);
                startActivity(intent);
            }
        });

        addsubject_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder ad = new AlertDialog.Builder(view.getContext());
                ad.setTitle("과목 추가");
                final EditText et = new EditText(view.getContext());
                ad.setView(et);
                ad.setPositiveButton("저장",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                userRealm.executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        final RealmList<String> subjects = new RealmList<>();
                                        subjects.addAll(characterList.first().getSubject());
                                        subjects.add(et.getText().toString());
                                        characterList.first().setSubject(subjects);
                                    }
                                });
                                dialog.dismiss();
                                SyncSubject();
                            }
                        });
                ad.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                ad.show();
            }
        });
    }

    // 각 Realm 객체 획득
    private void RealmInit() {
        userRealm = Realm.getInstance(UserModuleConfig);
        expRealm = Realm.getInstance(ExpModuleConfig);
    }

    // 측정 데이터 리스트 반환
    private RealmResults<MeasureData> getMeasureList() {
        return userRealm.where(MeasureData.class).findAllAsync();
    }

    // 캐릭터 데이터 리스트 반환
    private RealmResults<Character> getCharacterList() {
        return userRealm.where(Character.class).findAllAsync();
    }

    // 경험치 테이블 리스트 반환
    private RealmResults<ExpTable> getExpList() {
        return expRealm.where(ExpTable.class).findAll();
    }

    // 측정 데이터 DB 저장
    private void initCharacterData() {
        Log.d(TAG, "캐릭터 초기 설정 등록");
        //    date      : 측정 완료된 년/월/일
        //    timeout   : 측정된 시간량
        //    exp       : 측정된 시간의 경험치
        userRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Character Character = userRealm.createObject(Character.class);
                RealmList<String> subject = new RealmList<>();
                Character.setName("몰랑잉");
                Character.setLv(1);
                Character.setExp(0);
                Character.setSubject(subject);
            }
        });
    }

    private void SyncCharacterInfo() {
        Log.d(TAG, "캐릭터 데이터 설정");
        currentLv = (int) characterList.first().getLv();
        currentExp = characterList.first().getExp();
        nextLv = currentLv + 1;
        nextExp = expList.get(currentLv).getExp();
        nextInterval = expList.get(currentLv).getInterval();
        if (currentExp > nextInterval) UpdateLv();


        Log.d("TAG", String.format("%d %d %d %d %d", currentLv, currentExp, nextLv, nextExp, nextInterval));


        // 캐릭터 이름 출력
        character_name.setText(characterList.first().getName());

        // 경험치 바 최대값 출력
        expbar.setMax((int) nextInterval);
        // 캐릭터 레벨 출력
        character_lv.setText(String.format("LV %d.", currentLv));
        expbar.setProgress((int) currentExp);
        exptext.setText(String.format("%d / %d", currentExp, nextInterval));

    }

    private void UpdateLv() {
        Log.d(TAG, "레벨 업데이트");
        boolean isSuit = currentExp < nextInterval;
        int before = currentLv;


        // 레벨 조정
        while (!isSuit) {
            Log.d(TAG, "레벨업 : " + currentLv + "->" + nextLv);
            currentLv++;
            nextLv++;

            currentExp -= nextInterval;
            nextExp = expList.get(currentLv).getExp();
            nextInterval = expList.get(currentLv).getInterval();
            isSuit = currentExp < nextInterval;
        }


        // 캐릭터 레벨 변경
        userRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                characterList.first().setLv(currentLv);
                characterList.first().setExp(currentExp);
            }
        });

        boolean evo_check = false;
        for (int i = before; i <= currentLv; i++) {
            evo_ch_check = eduPreManger.getString(getActivity(), "selectCharacter");
            if (evo_ch_check.equals("basic_ch")) {
                break;
            } else if (evo_ch_check.equals("fish_ch")) {
                if ((i != before) && (i == 8)) {
                    evo_check = true;
                    break;
                }
            } else {
                if ((i != before) && (i == 3 || i == 5 || i == 8)) {
                    evo_check = true;
                    break;
                }
            }
        }
        if (evo_check) {
            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.evolution_dialog, null);
            ImageView evo_img = view.findViewById(R.id.evo_character);
            UpdateCharacter(evo_img);
            AlertDialog dialog = new AlertDialog.Builder(getActivity())
                    .setView(view)
                    .setPositiveButton("확인", null)
                    .create();
            dialog.show();
        } else {
            AlertDialog dialog = new AlertDialog.Builder(getActivity())
                    .setTitle("레벨업!")
                    .setMessage("축하합니다 " + currentLv + " 레벨이 되었습니다!")
                    .setPositiveButton("확인", null)
                    .create();
            dialog.show();

        }

    }
    //  private selectCharacter()


    private void UpdateCharacter(ImageView ch) {
        ch_check = eduPreManger.getString(getActivity(), "selectCharacter");
        //기본 몰랑이
        if (ch_check.equals("basic_ch")) {
            Glide.with(this).load(R.drawable.character).into(ch);
        }
        //모찌
        else if (ch_check.equals("mozzi_ch") && currentLv <= 2) {
            Glide.with(this).load(R.drawable.mozzi1).into(ch);
        } else if (ch_check.equals("mozzi_ch") && currentLv <= 4) {
            Glide.with(this).load(R.drawable.mozzi2).into(ch);
        } else if (ch_check.equals("mozzi_ch") && currentLv <= 7) {
            Glide.with(this).load(R.drawable.mozzi3).into(ch);
        } else if (ch_check.equals("mozzi_ch") && 8 <= currentLv) {
            Glide.with(this).load(R.drawable.mozzi4).into(ch);
        } else if (ch_check.equals("mozzi_ch") && 10 <= currentLv) {
            Glide.with(this).load(R.drawable.mozzi5).into(ch);
        }
        //꼬부기~ 메가 거북왕
        else if (ch_check.equals("water_ch") && currentLv <= 2) {
            Glide.with(this).load(R.drawable.water1).into(ch);
        } else if (ch_check.equals("water_ch") && currentLv <= 4) {
            Glide.with(this).load(R.drawable.water2).into(ch);
        } else if (ch_check.equals("water_ch") && currentLv <= 7) {
            Glide.with(this).load(R.drawable.water3).into(ch);
        } else if (ch_check.equals("water_ch") && 8 <= currentLv) {
            Glide.with(this).load(R.drawable.water4).into(ch);
        }
        //파이리~ 메가 리자몽
        else if (ch_check.equals("fire_ch") && currentLv <= 2) {
            Glide.with(this).load(R.drawable.fire1).into(ch);
        } else if (ch_check.equals("fire_ch") && currentLv <= 4) {
            Glide.with(this).load(R.drawable.fire2).into(ch);
        } else if (ch_check.equals("fire_ch") && currentLv <= 7) {
            Glide.with(this).load(R.drawable.fire3).into(ch);
        } else if (ch_check.equals("fire_ch") && 8 <= currentLv) {
            Glide.with(this).load(R.drawable.fire4).into(ch);
        }
        //이상해씨 ~ 메가 이상해꽃
        else if (ch_check.equals("grass_ch") && currentLv <= 2) {
            Glide.with(this).load(R.drawable.grass1).into(ch);
        } else if (ch_check.equals("grass_ch") && currentLv <= 4) {
            Glide.with(this).load(R.drawable.grass2).into(ch);
        } else if (ch_check.equals("grass_ch") && currentLv <= 7) {
            Glide.with(this).load(R.drawable.grass3).into(ch);
        } else if (ch_check.equals("grass_ch") && 8 <= currentLv) {
            Glide.with(this).load(R.drawable.grass4).into(ch);
        }
        //잉어킹 ~ 갸라도스
        else if (ch_check.equals("fish_ch") && currentLv <= 7) {
            Glide.with(this).load(R.drawable.fish).into(ch);
        } else if (ch_check.equals("fish_ch") && currentLv >= 8) {
            Glide.with(this).load(R.drawable.dragon).into(ch);
        }
        //디폴트 초기설정으로 캐릭터 미설정시 몰랑이 출력
        else {
            Glide.with(this).load(R.drawable.character).into(ch);
        }
    }


    private void SyncSubject() { //받아온 데이터를 어뎁터를 통해 리스트뷰에 전달
        if (characterList.first().getSubject().size() == 0) {
            Log.d(TAG, "아이템 없다");
            Log.d(TAG, "setEmptyView!");
            subject_listview.setVisibility(View.GONE);
        } else {
            listAdapter = new SubjectListAdapter(getContext());
            for (int i = 0; i < characterList.first().getSubject().size(); i++) {
                if (characterList.first().getSubject().get(i).equals("")) continue;
                getSumofTime(characterList.first().getSubject().get(i));
                listAdapter.addItem(characterList.first().getSubject().get(i), getSumofTime(characterList.first().getSubject().get(i)));
            }
            Log.d(this.getClass().getSimpleName(), "리스트갱신되는중");
            subject_listview.setVisibility(View.VISIBLE);
            subject_listview.setAdapter(listAdapter);
        }
    }

    // 오늘의 모든 과목별 시간 합 리턴
    private long getSumofTime(String subject) {
        SimpleDateFormat today_date = new SimpleDateFormat("yyyy/MM/dd", Locale.KOREA);
        String date = today_date.format(Calendar.getInstance().getTime());
        long sum_of_time = 0;
        RealmResults<MeasureData> allData = userRealm.where(MeasureData.class).equalTo("subject", subject).equalTo("date", date).findAll();
        for (int i = 0; i < allData.size(); i++)
            sum_of_time += allData.get(i).getTimeout();
        return sum_of_time;
    }

    @Override
    public void onResume() {
        Log.d(TAG, "접근");
        super.onResume();
        // 측정 데이터 변화 동기화
        SyncCharacterInfo();
        UpdateCharacter(character_img);
        SyncSubject();
    }
}