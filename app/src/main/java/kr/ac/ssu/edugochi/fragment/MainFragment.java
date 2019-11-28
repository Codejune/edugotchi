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

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import kr.ac.ssu.edugochi.R;
import kr.ac.ssu.edugochi.activity.MeasureActivity;
import kr.ac.ssu.edugochi.object.CharacterObject;
import kr.ac.ssu.edugochi.object.MeasureTimeObject;

public class MainFragment extends Fragment {
    private static final String TAG = MainFragment.class.getSimpleName();
    private View view;
    private Realm realm;
    private RealmResults<MeasureTimeObject> measureTransaction;
    private RealmResults<CharacterObject> characterTransaction;
    private ProgressBar expbar;
    private TextView exptext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Realm.init(getContext());
        realm = Realm.getDefaultInstance();
        view = inflater.inflate(R.layout.fragment_main, container, false);
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

    // 측정 데이터 리스트 반환
    private RealmResults<MeasureTimeObject> getMeasureList() {
        return realm.where(MeasureTimeObject.class).findAllAsync();
    }

    // 캐릭터 데이터 리스트 반환
    private RealmResults<CharacterObject> getCharacterList() {
        return realm.where(CharacterObject.class).findAllAsync();
    }

    // 측정 데이터 DB 저장
    private void initCharacterData() {
        //    date      : 측정 완료된 년/월/일
        //    timeout   : 측정된 시간량
        //    exp       : 측정된 시간의 경험치
        realm.beginTransaction();
        CharacterObject characterObject = realm.createObject(CharacterObject.class);
        characterObject.setName("몰랑잉");
        characterObject.setLv(0);
        characterObject.setExp(0);
        realm.commitTransaction();
    }

    @Override
    public void onStart() {
        measureTransaction = getMeasureList();
        characterTransaction = getCharacterList();

        measureTransaction.addChangeListener(new RealmChangeListener<RealmResults<MeasureTimeObject>>() {
            @Override
            public void onChange(RealmResults<MeasureTimeObject> measureTimeObjects) {
                Integer exp = (int) (characterTransaction.first().getExp())/100;
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
                Integer exp = (int) (characterTransaction.first().getExp())/100;
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
