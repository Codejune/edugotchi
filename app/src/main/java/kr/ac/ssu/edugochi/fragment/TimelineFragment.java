package kr.ac.ssu.edugochi.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;


import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import kr.ac.ssu.edugochi.R;

public class TimelineFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public TimelineFragment() {
        // Required empty public constructor
    }

    public static TimelineFragment newInstance(String param1, String param2) {
        Log.d("Superoid", "TimelineFragment");
        TimelineFragment fragment = new TimelineFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("Superoid", "onCreate");
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d("Superoid", "onCreateView");
        View myView = inflater.inflate(R.layout.fragment_timeline, container, false);
        pre_Button = myView.findViewById((R.id.pre_button));
        pre_Button.setOnClickListener(this);
        fore_Button = myView.findViewById((R.id.fore_button));
        fore_Button.setOnClickListener(this);
        gridView = myView.findViewById((R.id.gridview));
        gridView.setOnItemClickListener(this);
        return myView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("Superoid", "onViewCreated");
        makeCalendar();
        TabHost tabHost = getView().findViewById(R.id.host);
        tabHost.setup();
        TabHost.TabSpec spec = tabHost.newTabSpec("tab1");
        spec.setIndicator("일간");
        spec.setContent(R.id.tab_content1);
        tabHost.addTab(spec);
        spec = tabHost.newTabSpec("tab2");
        spec.setIndicator("주간");
        spec.setContent(R.id.tab_content2);
        tabHost.addTab(spec);
        spec = tabHost.newTabSpec("tab3");
        spec.setIndicator("월간");
        spec.setContent(R.id.tab_content3);
        tabHost.addTab(spec);
    }

    int month = 0;  // 달력 표시 달 수정용 변수

    View pre_view;
    int pre_position;


    // 달력 관련 클래스 변수
    private TextView tvDate;
    private GridAdapter gridAdapter;
    private ArrayList<String> dayList;
    private GridView gridView;
    private Calendar mCal;

    //연,월,일을 따로 저장
    SimpleDateFormat curYearFormat = new SimpleDateFormat("yyyy", Locale.KOREA);
    SimpleDateFormat curMonthFormat = new SimpleDateFormat("MM", Locale.KOREA);
    SimpleDateFormat curDayFormat = new SimpleDateFormat("dd", Locale.KOREA);

    // 이미지뷰로 대체 중
    ImageView pre_Button;
    ImageView fore_Button;

    // 달력의 년,월,일을 배치해주는 makeCalendar 메소드
    private void makeCalendar() {
        Log.d("Superoid", "makeCalendar");
        tvDate = getView().findViewById(R.id.tv_date);
        gridView =  getView().findViewById(R.id.gridview);

        // 해당 월에 대한 정보를 세팅해준다.
        mCal = Calendar.getInstance();
        mCal.add(Calendar.MONTH, month);


        //현재 연도와 월을 텍스트뷰에 뿌려줌
        tvDate.setText(curYearFormat.format(mCal.getTime()) + "년 " + curMonthFormat.format(mCal.getTime()) + "월");

        dayList = new ArrayList<>();

        //이번달 1일 무슨요일인지 판단 mCal.set(Year,Month,Day)
        mCal.set(Integer.parseInt(curYearFormat.format(mCal.getTime())), Integer.parseInt(curMonthFormat.format(mCal.getTime())) - 1, 1);
        int dayNum = mCal.get(Calendar.DAY_OF_WEEK);
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

            if (month == 0) {
                //해당 날짜 텍스트 컬러,배경 변경
                mCal = Calendar.getInstance();
                //오늘 day 가져옴
                Integer today = mCal.get(Calendar.DAY_OF_MONTH);
                String sToday = String.valueOf(today);

                if (sToday.equals(getItem(position))) { //오늘 day 텍스트 컬러 변경
                    holder.tvItemGridView.setTextColor(getResources().getColor(R.color.color_000000));
                }
            }
            return convertView;
        }
    }

    private class ViewHolder {
        TextView tvItemGridView;
    }

    // onClick메소드 구현
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


    @Override
    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        if (pre_position > 0) {
            pre_view.setBackgroundColor(Color.argb(0, 0, 0, 0));
        }
        v.setBackgroundResource(R.drawable.selected_date);
        pre_position = position;
        pre_view = v;
    }
}