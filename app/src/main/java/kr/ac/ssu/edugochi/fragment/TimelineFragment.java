package kr.ac.ssu.edugochi.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;


import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;

import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;


import com.google.android.material.tabs.TabLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmResults;
import kr.ac.ssu.edugochi.object.MeasureTimeObject;
import kr.ac.ssu.edugochi.R;
import kr.ac.ssu.edugochi.view.CustomGridView;

public class TimelineFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener {

    int month = 0;  // 달력 표시 달 수정용 변수
    int dayNum; // 매 달 공백 생성용 변수
    int pre_position;
    View pre_view;

    // 달력 관련 클래스 변수
    private TextView tvDate;
    private GridAdapter gridAdapter;
    private ArrayList<String> dayList;
    private CustomGridView gridView;
    private Calendar mCal;

    // 연,월,일을 따로 저장
    SimpleDateFormat curYearFormat = new SimpleDateFormat("yyyy", Locale.KOREA);
    SimpleDateFormat curMonthFormat = new SimpleDateFormat("MM", Locale.KOREA);
    SimpleDateFormat curDayFormat = new SimpleDateFormat("dd", Locale.KOREA);
    SimpleDateFormat curTotalFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.KOREA);

    // 달을 변경하는 버튼 이미지
    ImageView pre_Button;
    ImageView fore_Button;

    // 탭레이아웃 변수
    TabLayout tabLayout;

    // Realm DB 등록
    Realm mRealm;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("Superoid", "onCreateView");
        View myView = inflater.inflate(R.layout.fragment_timeline, container, false);
        // 클릭리스너 부착 선언
        pre_Button = myView.findViewById((R.id.pre_button));
        pre_Button.setOnClickListener(this);
        fore_Button = myView.findViewById((R.id.fore_button));
        fore_Button.setOnClickListener(this);
        gridView = myView.findViewById((R.id.gridview));
        gridView.setOnItemClickListener(this);

        tabLayout = myView.findViewById(R.id.tabs) ;
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // TODO : process tab selection event.
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // do nothing
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // do nothing
            }
        }) ;
        return myView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("Superoid", "onViewCreated");
        makeCalendar(); // 달력 생성 함수
    }

    // 달력의 년,월,일을 배치해주는 makeCalendar 메소드
    private void makeCalendar() {
        Log.d("Superoid", "makeCalendar");
        tvDate = getView().findViewById(R.id.tv_date);
        gridView = getView().findViewById(R.id.gridview);

        // 해당 월에 대한 정보를 세팅해준다.
        mCal = Calendar.getInstance();
        mCal.add(Calendar.MONTH, month);

        //현재 연도와 월을 텍스트뷰에 뿌려줌
        tvDate.setText(curYearFormat.format(mCal.getTime()) + "년 " + curMonthFormat.format(mCal.getTime()) + "월");

        dayList = new ArrayList<>(); // 날짜를 넣어줄 콜렉터

        //이번달 1일 무슨요일인지 판단 mCal.set(Year,Month,Day)
        mCal.set(Integer.parseInt(curYearFormat.format(mCal.getTime())), Integer.parseInt(curMonthFormat.format(mCal.getTime())) - 1, 1);
        dayNum = mCal.get(Calendar.DAY_OF_WEEK);
        //1일 - 요일 매칭 시키기 위해 공백 add
        for (int i = 1; i < dayNum; i++) {
            dayList.add("");
        }
        setCalendarDate(mCal.get(Calendar.MONTH) + 1);

        gridAdapter = new GridAdapter(getActivity(), dayList);
        gridView.setAdapter(gridAdapter);
    }

    // 해당 월에 표시할 일 수 구함
    private void setCalendarDate(int month) {
        Log.d("Superoid", "setCalendarDate");
        mCal.set(Calendar.MONTH, month - 1);

        for (int i = 0; i < mCal.getActualMaximum(Calendar.DAY_OF_MONTH); i++) {
            dayList.add("" + (i + 1));
        }
    }

    // 그리드뷰 어댑터
    private class GridAdapter extends BaseAdapter {
        private final List<String> list;
        private final LayoutInflater inflater;

        public GridAdapter(Context context, List<String> list) {
            this.list = list;
            this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public String getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Log.d("Superoid", "getView");
            ViewHolder holder = null;

            if (convertView == null) {
                Log.d("Superoid", "getView null");
                convertView = inflater.inflate(R.layout.item_calendar_gridview, parent, false);
                holder = new ViewHolder();
                holder.tvItemGridView = convertView.findViewById(R.id.tv_item_gridview);
                convertView.setTag(holder);
            } else {
                Log.d("Superoid", "getView else");
                holder = (ViewHolder) convertView.getTag();
            }
            holder.tvItemGridView.setText("" + getItem(position));

            // 토요일과 일요일에 색깔 지정
            if ((position + 1) % 7 == 1)
                holder.tvItemGridView.setTextColor(getResources().getColor(R.color.red));
            else if ((position + 1) % 7 == 0)
                holder.tvItemGridView.setTextColor(getResources().getColor(R.color.blue));

            // 공부량에 따른 시각화 태그 부여
            Realm.init(getActivity());
            mRealm = Realm.getDefaultInstance();
            RealmResults<MeasureTimeObject> allTransactions = mRealm.where(MeasureTimeObject.class).findAllSorted("date");

            long total_time = 0;
            mCal.add(Calendar.DATE, position - dayNum + 1);
            // DB의 모든 데이터 검사 하는 for문
            for (int i = 0; !allTransactions.get(i+1).equals(allTransactions.last()); i++) {

                // 날짜 값이 일치할 경우
                if (allTransactions.get(i).getDate().equals(curTotalFormat.format(mCal.getTime()))) {
                    total_time += allTransactions.get(i).getTimeout();
                }
            }
            // 마지막 값 검사하는 if문
            if (allTransactions.last().getDate().equals(curTotalFormat.format(mCal.getTime()))) {
                total_time += allTransactions.last().getTimeout();
            }

            if (total_time >= (6 * 60 * 60 * 1000))
                holder.tvItemGridView.setBackground(getResources().getDrawable(R.drawable.green_circle));

            else if (total_time >= (3 * 60 * 60 * 1000))
                holder.tvItemGridView.setBackground(getResources().getDrawable(R.drawable.yellow_circle));

            else if ((position >= dayNum - 1) && total_time > 0)
                holder.tvItemGridView.setBackground(getResources().getDrawable(R.drawable.red_circle));


            mCal.add(Calendar.DATE, -(position - dayNum + 1));
            return convertView;
        }
    }

    private class ViewHolder {
        TextView tvItemGridView;
    }

    // onClick 메소드 구현
    @Override
    public void onClick(View v) {
        // implements your things
        if (v == pre_Button) {
            month--;
            makeCalendar();
        } else if (v == fore_Button) {
            month++;
            makeCalendar();
        }
    }

    // 날짜 클릭시 호출되는 onItemClick 메소드
    @Override
    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        // 선택 날짜에 시각 효과 표시
        if (pre_position > 0)   // 선택된게 있었으면 효과 삭제
            pre_view.setBackgroundColor(Color.argb(0, 0, 0, 0));

        v.setBackgroundResource(R.drawable.selected_date);
        pre_position = position;
        pre_view = v;

        Realm.init(getActivity());
        mRealm = Realm.getDefaultInstance();
        RealmResults<MeasureTimeObject> allTransactions = mRealm.where(MeasureTimeObject.class).findAllSorted("date");

        long total_time = 0, total_exp = 0, avg_time, pre_total_time = 0;
        long total_week_time = 0, total_week_exp = 0, avg_week_time, pre_week_total_time = 0;
        long total_month_time = 0, total_month_exp = 0, avg_month_time, pre_month_total_time = 0;
        Calendar[] week = new Calendar[7];
        Calendar[] pre_week = new Calendar[7];
        Calendar week_day, pre_day,month_day;
        int rest = -1,pre_rest=-1,month_rest=-1;

        mCal.add(Calendar.DATE, position - dayNum + 1); // 클릭 된 날짜로 세팅

        // 클릭된 날짜의 어제 날짜 세팅
        pre_day = (Calendar) mCal.clone();
        pre_day.add(Calendar.DATE, -1);

        // 클릭된 날짜의 주에서 일요일로 세팅
       week_day = (Calendar)mCal.clone();
        week_day.add(Calendar.DATE,-(position%7));
        for(int i=0;i<7;i++){
            week[i]=(Calendar)week_day.clone();
            week_day.add(Calendar.DATE,1);
        }
        week_day.add(Calendar.DATE,-14); // 전 주 일요일 세팅
        for(int i=0;i<7;i++){
            pre_week[i]=(Calendar)week_day.clone();
            week_day.add(Calendar.DATE,1);
        }

        month_day = (Calendar) mCal.clone();
        month_day.add(Calendar.MONTH, -1);

        /** 탭호스트 레이아웃의 데이터 세팅 **/
        for (int i = 0; !allTransactions.get(i).equals(allTransactions.last()); i++) {
            if (allTransactions.get(i).getDate().equals(curTotalFormat.format(mCal.getTime()))) { // 날짜 값이 일치할 경우
                total_time += allTransactions.get(i).getTimeout();
                total_exp += allTransactions.get(i).getExp();
                rest++;
            } else if (allTransactions.get(i).getDate().equals(curTotalFormat.format(pre_day.getTime()))) // 어제 날짜 값이 일치할 경우
                pre_total_time += allTransactions.get(i).getTimeout();

            // 주간 단위 비교
            for(int j=0;j<7;j++){
                if(allTransactions.get(i).getDate().equals(curTotalFormat.format(week[j].getTime()))){
                    total_week_time+=allTransactions.get(i).getTimeout();
                    total_week_exp+=allTransactions.get(i).getExp();
                    pre_rest++;
                } else if(allTransactions.get(i).getDate().equals(curTotalFormat.format(pre_week[j].getTime())))
                    pre_week_total_time+=allTransactions.get(i).getTimeout();
            }
            // 월간 단위 비교
            if(allTransactions.get(i).getDate().substring(5,7).equals(curMonthFormat.format(mCal.getTime()))){
                total_month_time+=allTransactions.get(i).getTimeout();
                total_month_exp+=allTransactions.get(i).getExp();
                month_rest++;
            }
            else if(allTransactions.get(i).getDate().substring(5,7).equals(curMonthFormat.format(month_day.getTime())))
                pre_month_total_time+=allTransactions.get(i).getTimeout();
        }

        // 마지막 값 검사하는 if문
        if (allTransactions.last().getDate().equals(curTotalFormat.format(mCal.getTime()))) {
            total_time += allTransactions.last().getTimeout();
            total_exp += allTransactions.last().getExp();
            rest++;
        } else if (allTransactions.last().getDate().equals(curTotalFormat.format(pre_day.getTime())))
            pre_total_time += allTransactions.last().getTimeout();
        for(int j=0;j<7;j++){
            if(allTransactions.last().getDate().equals(curTotalFormat.format(week[j].getTime()))){
                total_week_time+=allTransactions.last().getTimeout();
                total_week_exp+=allTransactions.last().getExp();
                pre_rest++;
            }else if(allTransactions.last().getDate().equals(curTotalFormat.format(pre_week[j].getTime())))
                pre_week_total_time+=allTransactions.last().getTimeout();
        }
        if(allTransactions.last().getDate().substring(5,7).equals(curMonthFormat.format(mCal.getTime()))){
            total_month_time+=allTransactions.last().getTimeout();
            total_month_exp+=allTransactions.last().getExp();
            month_rest++;
        }
        else if(allTransactions.last().getDate().substring(5,7).equals(curMonthFormat.format(month_day.getTime())))
            pre_month_total_time+=allTransactions.last().getTimeout();


        /** 일간 탭호스트 레이아웃 세팅 **/
        TextView textview = getView().findViewById(R.id.total_time);
        textview.setText(makeTimeForm(total_time));

        textview = getView().findViewById(R.id.pre_day_time);
        textview.setText(makeTimeForm(pre_total_time));

        textview = getView().findViewById(R.id.differ_days);
        textview.setText(makeTimeForm(total_time - pre_total_time));
        if (total_time - pre_total_time < 0)
            textview.setTextColor(getResources().getColor(R.color.colorAccent));
        else
            textview.setTextColor(getResources().getColor(R.color.soongsilPrimary));

        textview = getView().findViewById(R.id.total_exp);
        textview.setText("+" + total_exp);

        textview = getView().findViewById(R.id.avg_time);
        textview.setText(makeTimeForm((total_time)));

        if (rest < 0) rest = 0;
        textview = getView().findViewById(R.id.rest_time);
        textview.setText(rest + "회");

        /** 주간 탭호스트 레이아웃 세팅 **/
        textview = getView().findViewById(R.id.total_week_time);
        textview.setText(makeTimeForm(total_week_time));

        textview = getView().findViewById(R.id.pre_week_time);
        textview.setText(makeTimeForm(pre_week_total_time));

        textview = getView().findViewById(R.id.differ_week_days);
        textview.setText(makeTimeForm(total_week_time-pre_week_total_time));
        if (total_week_time - pre_week_total_time < 0)
            textview.setTextColor(getResources().getColor(R.color.colorAccent));
        else
            textview.setTextColor(getResources().getColor(R.color.soongsilPrimary));

        textview = getView().findViewById(R.id.total_week_exp);
        textview.setText("+" + total_week_exp);

        textview = getView().findViewById(R.id.avg_week_time);
        textview.setText(makeTimeForm((total_week_time/7)));

        if (pre_rest < 0) pre_rest = 0;
        textview = getView().findViewById(R.id.rest_week_time);
        textview.setText(pre_rest + "회");

        /** 월간 탭호스트 레이아웃 세팅 **/
        textview = getView().findViewById(R.id.total_month_time);
        textview.setText(makeTimeForm(total_month_time));

        textview = getView().findViewById(R.id.pre_month_time);
        textview.setText(makeTimeForm(pre_month_total_time));

        textview = getView().findViewById(R.id.differ_month_days);
        textview.setText(makeTimeForm(total_month_time-pre_month_total_time));
        if (total_month_time - pre_month_total_time < 0)
            textview.setTextColor(getResources().getColor(R.color.colorAccent));
        else
            textview.setTextColor(getResources().getColor(R.color.soongsilPrimary));

        textview = getView().findViewById(R.id.total_month_exp);
        textview.setText("+" + total_month_exp);

        textview = getView().findViewById(R.id.avg_month_time);
        textview.setText(makeTimeForm((total_week_time/mCal.getActualMaximum(Calendar.DAY_OF_MONTH))));

        if (month_rest < 0) month_rest = 0;
        textview = getView().findViewById(R.id.rest_month_time);
        textview.setText(month_rest + "회");
        mCal.add(Calendar.DATE, -(position - dayNum + 1));
    }

    // long타입 인수를 시간형식에 맞춰 String값을 반환해주는 함수
    public String makeTimeForm(long Time) {
        String hour, minute, seconds;
        int nhour, nminute, nseconds;
        boolean isMinus = false;

        if (Time < 0) {
            Time *= -1;
            isMinus = true;
        }
        nhour = (int) Time / 3600000;
        hour = Integer.toString(nhour);
        Time %= 3600000;
        nminute = (int) Time / 60000;
        minute = Integer.toString(nminute);
        Time %= 60000;
        nseconds = (int) Time / 1000;
        seconds = Integer.toString(nseconds);

        // 각 파트별로 10이하면 0을 추가
        if (nhour < 10)
            hour = "0" + hour;
        if (nminute < 10)
            minute = "0" + minute;
        if (nseconds < 10)
            seconds = "0" + seconds;

        if (isMinus)
            return "-" + hour + ":" + minute + ":" + seconds;
        else
            return hour + ":" + minute + ":" + seconds;

    }
}