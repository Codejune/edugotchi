package kr.ac.ssu.edugochi.fragment;

import android.content.Intent;
import android.os.Bundle;

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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import kr.ac.ssu.edugochi.R;
import kr.ac.ssu.edugochi.activity.MeasureActivity;
import kr.ac.ssu.edugochi.object.CharacterObject;
import kr.ac.ssu.edugochi.object.ExpTable;
import kr.ac.ssu.edugochi.object.MeasureTimeObject;
import kr.ac.ssu.edugochi.util.RealmUtils;

public class MainFragment extends Fragment {
    private static final String TAG = MainFragment.class.getSimpleName();
    private View view;                  // MainFragment 뷰
    private Realm realm;                // 기본 인스턴스
    private Realm exptable;             // 경험치 테이블 인스턴스
    private RealmResults<MeasureTimeObject> measureTransaction; // 측정 데이터 리스트
    private RealmResults<CharacterObject> characterTransaction; // 캐릭터 정보 리스트(0)
    private RealmResults<ExpTable> expTables;
    private ProgressBar expbar;         // 경험치 막대
    private TextView exptext;           // 경험치 텍스트
    private TextView character_name;    // 캐릭터 이름
    private TextView character_lv;      // 캐릭터 레벨

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        try {
            RealmInit();
        } catch (IOException e) {
            e.printStackTrace();
        }
        view = inflater.inflate(R.layout.fragment_main, container, false);
        character_name = view.findViewById(R.id.name);
        character_lv = view.findViewById(R.id.lv);
        expbar = view.findViewById(R.id.exp_bar);
        exptext = view.findViewById(R.id.exp_text);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ImageView character = view.findViewById(R.id.character);
        Glide.with(this).load(R.drawable.character).into(character);
        MaterialButton record = view.findViewById(R.id.record);
        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MeasureActivity.class);
                startActivity(intent);
            }
        });
        try {
            expbar.setProgress((int) characterTransaction.get(0).getExp());
        } catch (NullPointerException e) {
            initCharacterData();
        }
    }

    private void RealmInit() throws IOException {
        Realm.init(getContext());           // Realm 초기화
        try {
            realm = Realm.getDefaultInstance(); // Realm 기본 인스턴스 호출
            exptable = RealmUtils.getRealm(getContext(), "ExpTable.realm");  // Assets 폴더에 존재하는 ExpTable.realm 호출
        } catch (RuntimeException e) {
            e.printStackTrace();
            InputStream is = null;
            FileOutputStream fos = null;
            File outDir = new File("/data/data/kr.ac.ssu.edugochi/files/");
            is = getActivity().getAssets().open("ExpTalbe.realm");
            int size = is.available();
            byte[] buffer = new byte[size];
            File outfile = new File(outDir + "ExpTable.realm");
            fos = new FileOutputStream(outfile);
            for (int c = is.read(buffer); c != -1; c = is.read(buffer)) {
                fos.write(buffer, 0, c);
            }
            is.close();
            fos.close();
            exptable = RealmUtils.getRealm(getContext(), "ExpTable.realm");  // Assets 폴더에 존재하는 ExpTable.realm 호출
        }
    }

    // 측정 데이터 리스트 반환
    private RealmResults<MeasureTimeObject> getMeasureList() {
        return realm.where(MeasureTimeObject.class).findAllAsync();
    }

    // 캐릭터 데이터 리스트 반환
    private RealmResults<CharacterObject> getCharacterList() {
        return realm.where(CharacterObject.class).findAllAsync();
    }

    // 경험치 테이블 리스트 반환
    private RealmResults<ExpTable> getExpTables() {
        return realm.where(ExpTable.class).findAllAsync();
    }

    // 측정 데이터 DB 저장
    private void initCharacterData() {
        //    date      : 측정 완료된 년/월/일
        //    timeout   : 측정된 시간량
        //    exp       : 측정된 시간의 경험치
        realm.beginTransaction();
        CharacterObject characterObject = realm.createObject(CharacterObject.class);
        characterObject.setName("몰랑잉");
        characterObject.setLv(1);
        characterObject.setExp(0);
        realm.commitTransaction();
    }

    // 메인 프레그먼트 진입 시
    @Override
    public void onStart() {
        measureTransaction = getMeasureList();
        characterTransaction = getCharacterList();
        expTables = getExpTables();

        character_name.setText(characterTransaction.first().getName());
        character_lv.setText(String.format("LV %d.", characterTransaction.first().getLv()));
        measureTransaction.addChangeListener(new RealmChangeListener<RealmResults<MeasureTimeObject>>() {
            @Override
            public void onChange(RealmResults<MeasureTimeObject> measureTimeObjects) {
                Integer exp = (int) (characterTransaction.first().getExp()) / 100;
                expbar.setProgress(exp);
                exptext.setText(exp + " / 100");
                Log.d(TAG, exp.toString());
            }
        });
        super.onStart();

    }

    @Override
    public void onResume() {
        measureTransaction = getMeasureList();
        characterTransaction = getCharacterList();

        measureTransaction.addChangeListener(new RealmChangeListener<RealmResults<MeasureTimeObject>>() {
            @Override
            public void onChange(RealmResults<MeasureTimeObject> measureTimeObjects) {
                Integer exp = (int) (characterTransaction.first().getExp()) / 100;
                expbar.setProgress(exp);
                exptext.setText(exp + " / 100");
                Log.d(TAG, exp.toString());
            }
        });
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
        //realm.close();
    }
}
