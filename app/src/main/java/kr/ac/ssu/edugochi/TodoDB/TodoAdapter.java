package kr.ac.ssu.edugochi.TodoDB;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class TodoAdapter extends ArrayAdapter<TodoVO> {
    Context context;
    int resId;
    ArrayList<TodoVO> data;

    public TodoAdapter(Context context, int resId, ArrayList<TodoVO> data) {
        super(context, resId);
        this.context = context;
        this.resId = resId;
        this.data = data;
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
        TextView dateView = holder.dateView;
        ImageView deleteBtn = holder.deleteBtn;

        final TodoVO vo = data.get(position);

        titleView.setText(vo.title);
        dateView.setText(vo.date);



        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast toast = Toast.makeText(context, vo.title+" menu click", Toast.LENGTH_SHORT); toast.show();
            }
        });
        return convertView;
    }

}

