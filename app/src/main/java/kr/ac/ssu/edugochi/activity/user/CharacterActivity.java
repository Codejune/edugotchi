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

import kr.ac.ssu.edugochi.R;
import kr.ac.ssu.edugochi.activity.MainActivity;
import kr.ac.ssu.edugochi.eduPreManger;

public class CharacterActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener {
    private static final String TAG = CharacterActivity.class.getSimpleName();
    private EditText editNickName;
    private ImageView character_img;
    private String nickName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character);

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
