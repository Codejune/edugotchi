package kr.ac.ssu.edugochi.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.Sort;
import kr.ac.ssu.edugochi.R;
import kr.ac.ssu.edugochi.adapter.HistoryAdapter;
import kr.ac.ssu.edugochi.object.MeasureData;
import kr.ac.ssu.edugochi.realm.module.UserModule;
import kr.ac.ssu.edugochi.realm.utils.Migration;

public class HistoryActivity extends AppCompatActivity {

    private Realm userRealm;
    private RealmConfiguration UserModuleConfig;
    private static String USERTABLE = "User.realm";
    private RealmResults<MeasureData> measureList;

    private void RealmInit() {
        userRealm = Realm.getInstance(UserModuleConfig);
    }

    private RealmResults<MeasureData> getMeasureList() {
        return userRealm.where(MeasureData.class).findAllAsync();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        UserModuleConfig = new RealmConfiguration.Builder()
                .modules(new UserModule())
                .migration(new Migration())
                .schemaVersion(0)
                .name(USERTABLE)
                .build();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false); // 기존 title 지우기
        actionBar.setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼 만들기
        makeHistoryList();
    }

    private void makeHistoryList(){
        RealmInit();
        measureList = getMeasureList();
        measureList = userRealm.where(MeasureData.class).findAll().sort("date", Sort.DESCENDING);
        RecyclerView recyclerView = findViewById(R.id.history_list) ;
        recyclerView.setLayoutManager(new LinearLayoutManager(this)) ;
        HistoryAdapter adapter = new HistoryAdapter(measureList) ;
        recyclerView.setAdapter(adapter) ;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                onBackPressed();
                return false;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}