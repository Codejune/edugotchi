package kr.ac.ssu.edugochi.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.bumptech.glide.Glide;

import kr.ac.ssu.edugochi.R;
import kr.ac.ssu.edugochi.activity.user.LoginActivity;
import kr.ac.ssu.edugochi.eduPreManger;

public class SplashActivity extends AppCompatActivity {
    private static final String TAG = SplashActivity.class.getSimpleName();

    /* 스플래시 화면이 표시되는 시간을 설정(ms) */
    private final int SPLASH_DISPLAY_TIME = 2500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        boolean themeCheck;

        themeCheck = eduPreManger.getBoolean(this, "darkMode");

        if (themeCheck) {
            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_YES);
        }
        else {
            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_NO);
        }

        ImageView character = findViewById(R.id.character);
        Glide.with(this).load(R.drawable.character).into(character);


        Log.d(TAG, "액티비티 안들어옴");
        final boolean login_check;
        login_check = eduPreManger.getBoolean(this, "login");


        Log.d(TAG, "이게 문제");
        Handler handler = new Handler();
        handler.postDelayed(new Runnable(){
            @Override
            public void run(){
                boolean l_c = login_check;
                if(l_c) startActivity(new Intent(getApplication(), MainActivity.class));
                else {
                    Log.d(TAG, "액티비티 들어옴");
                    Intent intent = new Intent(getApplication(), LoginActivity.class);
                    startActivity(intent);
                }
                /* 스플래시 액티비티를 스택에서 제거. */
                SplashActivity.this.finish();
            }
        }, SPLASH_DISPLAY_TIME);

    }

    @Override
    public void onBackPressed() {
        /* 스플래시 화면에서 뒤로가기 기능 제거. */
    }
}
