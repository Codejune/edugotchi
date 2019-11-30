package kr.ac.ssu.edugochi.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import kr.ac.ssu.edugochi.R;
import kr.ac.ssu.edugochi.activity.MeasureActivity;
import kr.ac.ssu.edugochi.eduPreManger;
import kr.ac.ssu.edugochi.object.Character;
import kr.ac.ssu.edugochi.object.ExpTable;
import kr.ac.ssu.edugochi.object.MeasureData;
import kr.ac.ssu.edugochi.realm.module.ExpModule;
import kr.ac.ssu.edugochi.realm.module.UserModule;
import kr.ac.ssu.edugochi.realm.utils.Migration;

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

    ImageView character_img;            // 캐릭터 이미지
    private ProgressBar expbar;         // 경험치 막대
    private TextView exptext;           // 경험치 텍스트
    private TextView character_name;    // 캐릭터 이름
    private TextView character_lv;      // 캐릭터 레벨
    MaterialButton record_btn;          // 측정하기 버튼

    private int currentLv;     // 현재 레벨
    private long currentExp;    // 현재 경험치
    private int nextLv;        // 다음 레벨
    private long nextExp;       // 다음 레벨 경험치
    private long nextInterval;  // 다음 레벨과의 경험치 차이

    private String ch_check;


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

        ch_check = eduPreManger.getString(getActivity(), "selectCharacter");
        if (ch_check.equals("basic_ch")) {
            Glide.with(this).load(R.drawable.character).into(character_img);
        } else if (ch_check.equals("water_ch")) {
            Glide.with(this).load(R.drawable.water1).into(character_img);
        } else if (ch_check.equals("fire_ch")) {
            Glide.with(this).load(R.drawable.fire1).into(character_img);
        } else if (ch_check.equals("grass_ch")) {
            Glide.with(this).load(R.drawable.grass1).into(character_img);
        } else if (ch_check.equals("fish_ch")) {
            Glide.with(this).load(R.drawable.fish).into(character_img);
        } else Glide.with(this).load(R.drawable.fish).into(character_img);


        // Gif 이미지를 ImageView에 할당
        Glide.with(this).load(R.drawable.character).into(character_img);

        // 측정 버튼 리스너
        record_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MeasureActivity.class);
                startActivity(intent);
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
                Character.setName("몰랑잉");
                Character.setLv(1);
                Character.setExp(0);
            }
        });
    }

    private void SyncCharacterInfo() {
        Log.d(TAG, "캐릭터 데이터 설정");
        currentLv = (int) characterList.first().getLv();
        currentExp = characterList.first().getExp();
        nextLv = currentLv + 1;
        nextExp = expList.get(currentLv - 1).getExp();
        nextInterval = expList.get(currentLv - 1).getInterval();
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

        // 레벨 조정
        while (!isSuit) {
            Log.d(TAG, "레벨업 : " + currentLv + "->" + nextLv);
            currentLv++;
            nextLv++;

            currentExp -= nextInterval;
            nextExp = expList.get(currentLv - 1).getExp();
            nextInterval = expList.get(currentLv - 1).getInterval();
            isSuit = currentExp < nextExp;
        }

        // 캐릭터 레벨 변경
        userRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                characterList.first().setLv(currentLv);
                characterList.first().setExp(currentExp);
            }
        });
    }
    //  private selectCharacter()


    @Override
    public void onResume() {
        Log.d(TAG, "접근");
        super.onResume();
        // 측정 데이터 변화 동기화
        SyncCharacterInfo();
    }
}