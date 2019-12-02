package kr.ac.ssu.edugochi.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import kr.ac.ssu.edugochi.R;
import kr.ac.ssu.edugochi.activity.MeasureActivity;


public class SubjectListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<String> subjects = new ArrayList<>();
    private MaterialButton measure_subject;
    private TextView subject_title;
    String subject;

    public SubjectListAdapter (Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return subjects.size();
    }

    @Override
    public String getItem(int position) {
        return subjects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {

        /* 'listview_custom' Layout을 inflate하여 convertView 참조 획득 */
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.item_subject, viewGroup, false);
        };

        measure_subject = view.findViewById(R.id.measure_subject);
        subject_title = view.findViewById(R.id.subject_string);

        /* MyItem에 아이템을 setting한다. */
        subject_title.setText(subjects.get(position));
        measure_subject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, MeasureActivity.class);
                intent.putExtra("subject", subjects.get(position));
                context.startActivity(intent);
            }
        });

        return view;
    }

    /* 아이템 데이터 추가를 위한 함수. 자신이 원하는대로 작성 */
    public void addItem(String subject) {

        this.subject = subject;
        subjects.add(subject);

    }
}
