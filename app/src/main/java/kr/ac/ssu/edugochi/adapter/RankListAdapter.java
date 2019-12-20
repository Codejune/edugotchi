package kr.ac.ssu.edugochi.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import kr.ac.ssu.edugochi.R;
import kr.ac.ssu.edugochi.fragment.TimelineFragment;
import kr.ac.ssu.edugochi.object.RankListItem;

public class RankListAdapter extends BaseAdapter {
    TimelineFragment timeline = new TimelineFragment();
    private LayoutInflater inflater;
    private ArrayList<RankListItem> data;
    private int layout;

    public RankListAdapter(Context context, int layout, ArrayList<RankListItem> data) {
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.data = data;
        this.layout = layout;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public String getItem(int position) {
        return data.get(position).getSubject();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView textview;

        if (convertView == null) {
            convertView = inflater.inflate(layout, parent, false);
        }

        RankListItem rank_items = data.get(position);
        textview = convertView.findViewById(R.id.rank);

        switch (position + 1) {
            case 1:
                textview.setText(position + 1 + "st");
                textview.setTextColor(Color.parseColor("#f1be02"));
                break;
            case 2:
                textview.setText(position + 1 + "nd");
                textview.setTextColor(Color.parseColor("#d6d6d8"));
                break;
            case 3:
                textview.setText(position + 1 + "rd");
                textview.setTextColor(Color.parseColor("#a06216"));
                break;
            default:
                textview.setText(position + 1 + "th");
        }

        textview = convertView.findViewById(R.id.subject_name);
        textview.setText(rank_items.getSubject());
        textview = convertView.findViewById(R.id.subject_time);
        textview.setText(timeline.makeTimeForm(rank_items.getTime()));
        textview = convertView.findViewById(R.id.subject_exp);
        textview.setText(rank_items.getExp()+"exp");

        return convertView;

    }
}


