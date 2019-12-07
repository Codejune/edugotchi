package kr.ac.ssu.edugochi.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import es.dmoral.toasty.Toasty;
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
import kr.ac.ssu.edugochi.util.DpPxConvertor;
import kr.ac.ssu.edugochi.view.CustomListView;

public class MainFragment extends Fragment {
    private static final String TAG = MainFragment.class.getSimpleName();
    // Realm
    private static String EXPTABLE = "ExpTable.realm";
    private static String USERTABLE = "User.realm";
    private Realm userRealm;                            // User.realm 인스턴스
    private Realm expRealm;                             // ExpTable.realm 테이블 인스턴스
    private RealmConfiguration UserModuleConfig;        // User.realm 모듈 설정
    private RealmConfiguration ExpModuleConfig;         // ExpTable.realm 모듈 설정
    private RealmResults<MeasureData> measureList;      // 측정 데이터 Realm 리스트
    private RealmResults<Character> characterList;      // 캐릭터 정보 Realm 리스트(0)
    private RealmResults<ExpTable> expList;             // 경험치 테이블 Realm 리스트
    private RealmList<String> subjects;                 // 과목 목록 리스트
    private SubjectListAdapter listAdapter;             // 과목 목록 리스트뷰 어댑터

    // View
    private SwipeMenuCreator swipeMenuCreator;  // 스와이프 메뉴
    private ImageView character_img;            // 캐릭터 이미지뷰
    private ProgressBar expbar;                 // 경험치 프로그레스바
    private TextView exptext;                   // 경험치 텍스트뷰
    private TextView character_name;            // 캐릭터 이름 텍스트뷰
    private TextView character_lv;              // 캐릭터 레벨 텍스트뷰
    private MaterialButton record_btn;          // 시간 측정 버튼
    private MaterialButton addsubject_btn;      // 과목 추가 버튼
    private CustomListView subject_listview;    // 과목 목록 리스트뷰

    // Character
    private int currentLv;      // 현재 레벨
    private long currentExp;    // 현재 경험치
    private int nextLv;         // 다음 레벨
    private long nextExp;       // 다음 레벨 경험치
    private long nextInterval;  // 다음 레벨과의 경험치 차이
    private String ch_check;
    private String evo_ch_check;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Realm 초기화
        RealmInit();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 뷰 연결
        character_img = view.findViewById(R.id.character);          // 캐릭터 이미지 이미지뷰
        character_name = view.findViewById(R.id.name);              // 캐릭터 이름 텍스트뷰
        character_lv = view.findViewById(R.id.lv);                  // 캐릭터 레벨 텍스트뷰
        expbar = view.findViewById(R.id.exp_bar);                   // 경험치 프로그레스바
        exptext = view.findViewById(R.id.exp_text);                 // 경험치 비교 텍스트뷰
        record_btn = view.findViewById(R.id.record);                // 시간 측정 버튼
        subject_listview = view.findViewById(R.id.subject_list);    // 과목 목록 리스트뷰
        addsubject_btn = view.findViewById(R.id.subject_add);       // 과목 추가 버튼

        // 스와이프 메뉴 생성
        CreateSwipeMenu();

        // 측정 버튼 클릭 시
        record_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MeasureActivity.class);
                startActivity(intent);
            }
        });

        // 과목 추가 버튼 클릭 시
        addsubject_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText editText = new EditText(view.getContext());
                // 다이얼로그 출력
                AlertDialog.Builder dialog = new AlertDialog.Builder(view.getContext());
                // 다이얼로그 제목
                dialog.setTitle("과목 추가");
                // 다이얼로그 뷰 할당
                dialog.setView(editText);
                // 저장 버튼 클릭 시
                dialog.setPositiveButton("저장",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                String subject = editText.getText().toString();
                                Log.d(TAG, "과목 저장 시도: " + subject);
                                boolean checkSubject = true;

                                if(subject.equals("") || subject.equals(" ")) {
                                    Toasty.error(getContext(), "해당 이름은 지정할 수 없습니다.", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                }
                                // 입력한 과목 존재 유무 체크
                                for (int i = 0; i < subjects.size(); i++) {
                                    Log.d(TAG, "subjects[" + i + "]: " + subjects.get(i));
                                    if (subjects.get(i).equals(subject)) {
                                        checkSubject = false;
                                        Log.d(TAG, subject + "가 이미 목록에 존재함");
                                        break;
                                    }
                                }

                                if (checkSubject) {
                                    AddSubject(subject);
                                    dialog.dismiss();
                                } else {
                                    Log.d(TAG, "이미 존재하는 과목");
                                }
                            }
                        });
                dialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "과목 저장 취소: ");
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

        // 과목 목록 스와이프 삭제
        subject_listview.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    // 삭제 버튼 클릭
                    case 0:
                        String subject = subjects.get(position);
                        if(subject.equals("")) {
                            position++;
                            subject = subjects.get(position);
                        }
                        RemoveSubject(subject);
                        break;
                }
                // false : close the menu; true : not close the menu
                return false;
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
        // ExpTable.realm 모듈 설정
        ExpModuleConfig = new RealmConfiguration.Builder()
                .modules(new ExpModule())
                .name(EXPTABLE)
                .assetFile(EXPTABLE)
                .readOnly()
                .build();

        userRealm = Realm.getInstance(UserModuleConfig);
        expRealm = Realm.getInstance(ExpModuleConfig);

        measureList = getMeasureList();
        Log.d(TAG, "measureList.size: " + measureList.size());
        characterList = getCharacterList();
        expList = getExpList();
    }

    // 측정 데이터 리스트 반환
    private RealmResults<MeasureData> getMeasureList() {
        return userRealm.where(MeasureData.class).findAll();
    }

    // 캐릭터 데이터 리스트 반환
    private RealmResults<Character> getCharacterList() {
        return userRealm.where(Character.class).findAll();
    }

    // 경험치 테이블 리스트 반환
    private RealmResults<ExpTable> getExpList() {
        return expRealm.where(ExpTable.class).findAll();
    }

    // 측정 데이터 DB 저장
    private void initCharacterData() {
        Log.d(TAG, "캐릭터 초기 설정 등록");
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

    private void UpdateCharacterInfo() {
        Log.d(TAG, "캐릭터 데이터 설정");
        try {
            // 캐릭터 설정이 있을 때
            currentLv = (int) characterList.first().getLv();        // 현재 레벨 획득
            currentExp = characterList.first().getExp();            // 현재 경험치 획득
            nextLv = currentLv + 1;                                 // 다음 레벨은 현재 레벨 + 1
            nextExp = expList.get(currentLv).getExp();              // 다음 레벨 경험치 획득
            nextInterval = expList.get(currentLv).getInterval();    // 다음 레벨까지 필요한 수치 차이 획득
            subjects = characterList.first().getSubject();

            // 축적된 경험치가 다음 레벨을 초과했을 경우
            if (currentExp > nextInterval)
                UpdateLv();

            Log.d(TAG, "currentLv: " + currentLv);
            Log.d(TAG, "currentExp: " + currentExp);
            Log.d(TAG, "nextLv: " + nextLv);
            Log.d(TAG, "nextExp: " + nextExp);
            Log.d(TAG, "nextInterval: " + nextInterval);

            // 캐릭터 이미지 설정
            UpdateCharacter(character_img);
            // 캐릭터 이름 설정
            character_name.setText(characterList.first().getName());
            // 경험치 프로그레스바 최대값 설정
            expbar.setMax((int) nextInterval);
            // 캐릭터 레벨 설정
            character_lv.setText(String.format("LV %d.", currentLv));
            // 경험치 프로그레스바 현재 경험치 비례 설정
            expbar.setProgress((int) currentExp);
            // 경험치 텍스트 설정
            exptext.setText(String.format("%d / %d", currentExp, nextInterval));

        } catch (Exception e) {
            // Character 설정이 없을 때
            e.printStackTrace();
            // 캐릭터 데이터베이스 생성
            initCharacterData();
            // 캐릭터 정보를 다시 동기화
            UpdateCharacterInfo();
        }

    }

    private void UpdateLv() {
        Log.d(TAG, "UpdateLv()");
        boolean isSuit = currentExp < nextInterval;
        boolean evo_check = false;
        int before = currentLv;

        // 캐릭터 레벨 조정
        // 현재 경험치가 다음 레벨까지 필요한 경험치 차이보다 작아질 때 까지 레벨 + 1
        while (!isSuit) {
            Log.d(TAG, "레벨업 : " + currentLv + "->" + nextLv);
            currentLv++;
            nextLv++;
            // 현재 경험치에서 다음 레벨까지 필요한 경험치 차이를 뺌
            currentExp -= nextInterval;
            // 다음 레벨에 필요한 경험치 업데이트
            nextExp = expList.get(currentLv).getExp();
            // 다음 레벨까지의 경험치 차이 업데이트
            nextInterval = expList.get(currentLv).getInterval();
            // 조건 재비교
            isSuit = currentExp < nextInterval;
        }

        // 조정된 값을 DB에 업데이트
        userRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                // 캐릭터 레벨 설정
                characterList.first().setLv(currentLv);
                // 캐릭터 경험치 설정
                characterList.first().setExp(currentExp);
            }
        });

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


    // 캐릭터 이미지 업데이트
    private void UpdateCharacter(ImageView ch) {
        ch_check = eduPreManger.getString(getActivity(), "selectCharacter");
        //기본 몰랑이
        switch (ch_check) {
            case "basic_ch":
                Glide.with(this).load(R.drawable.character).into(ch);
                break;
            //모찌
            case "mozzi_ch":
                if (currentLv <= 5)
                    Glide.with(this).load(R.drawable.mozzi1).into(ch);
                else if (currentLv <= 10)
                    Glide.with(this).load(R.drawable.mozzi2).into(ch);
                else if (currentLv <= 15)
                    Glide.with(this).load(R.drawable.mozzi3).into(ch);
                else if (currentLv <= 25)
                    Glide.with(this).load(R.drawable.mozzi4).into(ch);
                else
                    Glide.with(this).load(R.drawable.mozzi5).into(ch);
                break;
            //꼬부기~ 메가 거북왕
            case "water_ch":
                if (currentLv <= 5)
                    Glide.with(this).load(R.drawable.water1).into(ch);
                else if (currentLv <= 15)
                    Glide.with(this).load(R.drawable.water2).into(ch);
                else if (currentLv <= 25)
                    Glide.with(this).load(R.drawable.water3).into(ch);
                else
                    Glide.with(this).load(R.drawable.water4).into(ch);
                break;
            //파이리~ 메가 리자몽
            case "fire_ch":
                if (currentLv <= 5)
                    Glide.with(this).load(R.drawable.fire1).into(ch);
                else if (currentLv <= 15)
                    Glide.with(this).load(R.drawable.fire2).into(ch);
                else if (currentLv <= 25)
                    Glide.with(this).load(R.drawable.fire3).into(ch);
                else
                    Glide.with(this).load(R.drawable.fire4).into(ch);
                break;
            //이상해씨 ~ 메가 이상해꽃
            case "grass_ch":
                if (currentLv <= 5)
                    Glide.with(this).load(R.drawable.grass1).into(ch);
                else if (currentLv <= 15)
                    Glide.with(this).load(R.drawable.grass2).into(ch);
                else if (currentLv <= 25)
                    Glide.with(this).load(R.drawable.grass3).into(ch);
                else
                    Glide.with(this).load(R.drawable.grass4).into(ch);
                break;
            //잉어킹 ~ 갸라도스
            case "fish_ch":
                if (currentLv <= 25)
                    Glide.with(this).load(R.drawable.fish).into(ch);
                else
                    Glide.with(this).load(R.drawable.dragon).into(ch);
                break;
            //디폴트 초기설정으로 캐릭터 미설정시 몰랑이 출력
            default:
                Glide.with(this).load(R.drawable.character).into(ch);
                break;
        }
    }


    // 과목 목록 동기화
    private void UpdateSubject() {
        Log.d(TAG, "UpdateSubject()");
        subjects = characterList.first().getSubject();

        if (subjects.size() == 0) {
            Log.d(TAG, "데이터베이스에 과목이 존재하지 않음");
            subject_listview.setVisibility(View.GONE);
        } else {
            Log.d(TAG, "데이터베이스에 과목이 존재함");
            listAdapter = new SubjectListAdapter(getContext());
            for (int i = 0; i < subjects.size(); i++) {
                Log.d(TAG, "subject: " + subjects.get(i));
                // 만약 과목 이름이 null 또는 빈칸이면 해당 항목을 리스트에 출력하지 않음
                if (subjects.get(i).equals("")) continue;
                // 해당 과목의 측정한 총 시간을 계산
                getSumofTime(subjects.get(i));
                listAdapter.addItem(subjects.get(i), getSumofTime(subjects.get(i)));
            }
            subject_listview.setVisibility(View.VISIBLE);
            subject_listview.setAdapter(listAdapter);
        }
    }

    // 오늘의 모든 과목별 시간 합 리턴
    private long getSumofTime(String subject) {
        SimpleDateFormat today_date = new SimpleDateFormat("yyyy/MM/dd", Locale.KOREA);
        String date = today_date.format(Calendar.getInstance().getTime());
        RealmResults<MeasureData> todayList = userRealm.where(MeasureData.class).equalTo("subject", subject).equalTo("date", date).findAll();
        long sum_of_time = 0;

        // 오늘 측정한 해당 과목 총 시간을 가져옴
        // 데이터를 합산
        for (int i = 0; i < todayList.size(); i++)
            sum_of_time += todayList.get(i).getTimeout();
        return sum_of_time;
    }

    // 과목 추가
    private void AddSubject(final String subject) {
        Log.d(TAG, "과목 저장: " + subject);
        userRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmList<String> newSubjectList = new RealmList<>();
                newSubjectList.addAll(characterList.first().getSubject());
                newSubjectList.add(subject);
                characterList.first().setSubject(newSubjectList);
            }
        });
        // 과목 목록 동기화
        UpdateSubject();
    }

    // 과목 삭제
    private void RemoveSubject(final String subject) {
        Log.d(TAG, "과목 삭제: " + subject);
        userRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmList<String> newSubjectList = new RealmList<>();
                newSubjectList.addAll(characterList.first().getSubject());
                newSubjectList.remove(subject);

                Log.d(TAG, "measureList.size: " + measureList.size());
                for(int i = 0; i < measureList.size(); i++) {
                    if(measureList.get(i).getSubject().equals(subject))
                        measureList.get(i).setSubject("");
                }
                characterList.first().setSubject(newSubjectList);
            }
        });
        // 변경된 측정 데이터 리스트 업데이트
        measureList = getMeasureList();
        Log.d(TAG, "measureList.size: " + measureList.size());
        // 과목 목록 동기화
        UpdateSubject();
    }

    // 스와이프 메뉴 생성
    private void CreateSwipeMenu() {
        // 과목 목록 스와이프 메뉴
        swipeMenuCreator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                // 삭제하기 항목 생성
                SwipeMenuItem deleteItem = new SwipeMenuItem(getContext());
                // 삭제하기 항목 배경색상 설정
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                // 삭제하기 항목 너비 설정
                deleteItem.setWidth(DpPxConvertor.dp2px(getContext(), 60));
                // 삭제하기 항목 아이콘 설정
                deleteItem.setIcon(R.drawable.ic_delete);
                // 스와이프 메뉴에 추가
                menu.addMenuItem(deleteItem);
            }
        };
        // 과목 목록 리스트뷰에 할당
        subject_listview.setMenuCreator(swipeMenuCreator);
        //스와이프 방향 지정
        subject_listview.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT);
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume()");
        super.onResume();
        // 측정 데이터 변화 동기화
        UpdateCharacterInfo();
        // 과목 목록 동기화
        UpdateSubject();
    }
}