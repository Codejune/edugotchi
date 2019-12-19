package kr.ac.ssu.edugochi.activity.user;

import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

import kr.ac.ssu.edugochi.R;


public class SignUpActivity extends AppCompatActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();
    // 비밀번호 정규식
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^[a-zA-Z0-9!@.#$%^&*?_~]{4,16}$");

    // 파이어베이스 인증 객체 생성
    private FirebaseAuth firebaseAuth;

    // 이메일과 비밀번호
    private EditText editTextEmail;
    private EditText editTextPassword;


    private String email = "";
    private String password = "";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // 파이어베이스 인증 객체 선언
        firebaseAuth = FirebaseAuth.getInstance();

        editTextEmail = findViewById(R.id.et_eamil);
        editTextPassword = findViewById(R.id.et_password);

    }

    public void signUp(View view) {
        Log.d(TAG, "1. 회원가입");
        email = editTextEmail.getText().toString();
        password = editTextPassword.getText().toString();


        if (isValidEmail() && isValidPasswd()) {
            createUser(email, password);
        }
    }


    // 이메일 유효성 검사
    private boolean isValidEmail() {
        if (email.isEmpty()) {
            // 이메일 공백
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            // 이메일 형식 불일치
            return false;
        } else {
            return true;
        }
    }

    // 비밀번호 유효성 검사
    private boolean isValidPasswd() {
        if (password.isEmpty() && !PASSWORD_PATTERN.matcher(password).matches()) {
            // 비밀번호 공백
            return false;
        } else {
            return true;
        }
    }

    // 회원가입
    private void createUser(String email, String password) {

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            Toast.makeText(SignUpActivity.this, R.string.success_signup, Toast.LENGTH_SHORT).show();
                            onBackPressed();
                        } else {
                            // 회원가입 실패
                           Toast.makeText(SignUpActivity.this, R.string.failed_signup, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}