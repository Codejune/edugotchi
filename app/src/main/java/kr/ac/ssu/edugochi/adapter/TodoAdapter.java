package kr.ac.ssu.edugochi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import kr.ac.ssu.edugochi.TodoDB.TodoHolder;
import kr.ac.ssu.edugochi.object.TodoItem;

public class TodoAdapter extends ArrayAdapter<TodoItem> {

    private Context context;
    private int resId;
    private ArrayList<TodoItem> data;

    public TodoAdapter(Context context, int resId, ArrayList<TodoItem> data) {
        super(context, resId);
        this.context = context;
        this.resId = resId;
        this.data = data;;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(resId, null);
            TodoHolder holder = new TodoHolder(convertView);
            convertView.setTag(holder);
        }
        TodoHolder holder = (TodoHolder) convertView.getTag();

        TextView titleView = holder.titleView;
        final TextView dateView = holder.dateView;
        TextView memoView = holder.memoView;

        final TodoItem vo = data.get(position);

        titleView.setText(vo.title);
        dateView.setText(vo.date);
        memoView.setText(vo.memo);



        return convertView;
    }
}

