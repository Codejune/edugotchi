package kr.ac.ssu.edugochi.activity;


import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Calendar;

import es.dmoral.toasty.Toasty;
import kr.ac.ssu.edugochi.R;
import kr.ac.ssu.edugochi.TodoDB.TodoDBHandler;

public class AddTodoActivity extends AppCompatActivity {

    TodoDBHandler mHandler = null;

    private EditText title;
    private EditText memo;
    private ImageButton DateBtn;
    private TextView mTxtDate;
    private String emptyTxt = null;

    Calendar c = Calendar.getInstance();
    int mYear = c.get(Calendar.YEAR);
    int mMonth = c.get(Calendar.MONTH);
    int mDay = c.get(Calendar.DAY_OF_MONTH);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_todo);

        mHandler = new TodoDBHandler(this);
        mTxtDate = findViewById(R.id.txtdate);


        title = findViewById(R.id.todo_title);
        memo = findViewById(R.id.todo_memo);
        DateBtn = findViewById(R.id.data_btn);
        DateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChooseDate();
            }
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false); // 기존 title 지우기
        actionBar.setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼 만들기
        //actionBar.setHomeAsUpIndicator(R.id.back_btn); //뒤로가기 버튼 이미지 지정


    }

    void ChooseDate() {
        new DatePickerDialog(this, mDateSetListener, mYear,
                mMonth, mDay).show();

    }

    DatePickerDialog.OnDateSetListener mDateSetListener =

            new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    mYear = year;
                    mMonth = monthOfYear;
                    mDay = dayOfMonth;
                    UpdateNow();
                }
            };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                onBackPressed();
                return false;
            }
            case R.id.action_add_task: { // 오른쪽 상단 버튼 눌렀을 때
                AddClick();

            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.todo_menu, menu);
        return true;
    }

    void UpdateNow() {
        mTxtDate.setText(String.format("%d년 %d월 %d일", mYear, mMonth + 1, mDay));
    }

    private void AddClick() {

        if (title.getText() == null || TextUtils.isEmpty(title.getText().toString())) {
            Toasty.error(this, "Todo item cannot be empty!", Toast.LENGTH_SHORT).show();
            return;
        }
        final String todoTitle = title.getText().toString();
        String todoDate = mTxtDate.getText().toString();
        final String todoMemo = memo.getText().toString();
        if( mHandler == null ) {
            mHandler = TodoDBHandler.open(this);
        }
        if(todoDate.equals("기한 설정")) {
            todoDate = " ";
        }
        mHandler.insert(todoTitle, todoDate, todoMemo);

        title.setText(null);
        Toasty.success(this, "Item added", Toast.LENGTH_SHORT).show();
        onBackPressed();
        }
    }

