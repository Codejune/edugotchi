package kr.ac.ssu.edugochi.activity.user;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import kr.ac.ssu.edugochi.R;
import kr.ac.ssu.edugochi.activity.MainActivity;
import kr.ac.ssu.edugochi.eduPreManger;
import kr.ac.ssu.edugochi.object.Character;

public class CharacterActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener {
    private static final String TAG = CharacterActivity.class.getSimpleName();
    private EditText editNickName;
    private ImageView character_img;
    private String nickName;

    private static String USERTABLE = "User.realm";
    private RealmConfiguration UserModuleConfig;
    private Realm userRealm;
    private RealmResults<Character> characterList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character);
      //  RealmInit();

        editNickName = findViewById(R.id.et_nickname);
        character_img = findViewById(R.id.character);
        Glide.with(this).load(R.drawable.character).into(character_img);
        RadioGroup chGroup = findViewById(R.id.radio_ch);
        chGroup.setOnCheckedChangeListener(this);
    }

    public void CompBtn(View view) {
        nickName = editNickName.getText().toString();
        Log.d(TAG,nickName);
        if (!nickName.isEmpty()) {
            eduPreManger.setString(this,"nickname", nickName);
            Intent intent = new Intent(
                    this,
                    MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
           // initCharacterData(nickName);
            startActivity(intent);
        }

    }
    /*
    private void RealmInit() {
        // User.realm 모듈 설정
        UserModuleConfig = new RealmConfiguration.Builder()
                .modules(new UserModule())
                .migration(new Migration())
                .schemaVersion(0)
                .name(USERTABLE)
                .build();

        userRealm = Realm.getInstance(UserModuleConfig);
        characterList = getCharacterList();
    }
    // 캐릭터 데이터 리스트 반환
    private RealmResults<Character> getCharacterList() {
        return userRealm.where(Character.class).findAll();
    }
    private void initCharacterData(final String ch_name) {
        Log.d(TAG, getCharacterList().size()+"");
        Log.d(TAG, "캐릭터 초기 설정 등록");
        userRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Character Character = userRealm.createObject(Character.class);
                RealmList<String> subject = new RealmList<>();
                Character.setName(ch_name);
                Character.setLv(1);
                Character.setExp(0);
                Character.setSubject(subject);
            }
        });
    }

     */

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if(group.getId()==R.id.radio_ch){
            switch (checkedId){
                case R.id.basic_ch:
                    Log.d(TAG, String.valueOf(checkedId));
                    eduPreManger.setString(this,"selectCharacter", "basic_ch");
                    Glide.with(this).load(R.drawable.character).into(character_img);
                    break;
                case R.id.mozzi_ch:
                    Log.d(TAG, String.valueOf(checkedId));
                    eduPreManger.setString(this,"selectCharacter", "mozzi_ch");
                    Glide.with(this).load(R.drawable.mozzi1).into(character_img);
                    break;
                case R.id.water_ch:
                    Log.d(TAG, String.valueOf(checkedId));
                    eduPreManger.setString(this,"selectCharacter", "water_ch");
                    Glide.with(this).load(R.drawable.water1).into(character_img);
                    break;
                case R.id.fire_ch:
                    Log.d(TAG, String.valueOf(checkedId));
                    eduPreManger.setString(this,"selectCharacter", "fire_ch");
                    Glide.with(this).load(R.drawable.fire1).into(character_img);
                    break;
                case R.id.grass_ch:
                    Log.d(TAG, String.valueOf(checkedId));
                    eduPreManger.setString(this,"selectCharacter", "grass_ch");
                    Glide.with(this).load(R.drawable.grass1).into(character_img);
                    break;
                case R.id.fish_ch:
                    Log.d(TAG, String.valueOf(checkedId));
                    eduPreManger.setString(this,"selectCharacter", "fish_ch");
                    Glide.with(this).load(R.drawable.fish).into(character_img);
                    break;

            }
        }

    }
}
