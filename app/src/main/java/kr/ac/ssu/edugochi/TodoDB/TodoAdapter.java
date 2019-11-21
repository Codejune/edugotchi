package kr.ac.ssu.edugochi.TodoDB;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;

import kr.ac.ssu.edugochi.R;

public class TodoAdapter extends ArrayAdapter<TodoVO> {

    private View.OnClickListener mOnItemDeleteListener;
    Context context;
    int resId;
    ArrayList<TodoVO> data;

    public TodoAdapter(Context context, int resId, ArrayList<TodoVO> data, View.OnClickListener onItemDeleteListener) {
        super(context, resId);
        this.context = context;
        this.resId = resId;
        this.data = data;
        mOnItemDeleteListener = onItemDeleteListener;
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
        ImageView deleteBtn = holder.deleteBtn;
        View view = convertView;

        final TodoVO vo = data.get(position);

        titleView.setText(vo.title);
        dateView.setText(vo.date);
        memoView.setText(vo.memo);



        holder.deleteBtn.setOnClickListener(mOnItemDeleteListener);
        holder.deleteBtn.setTag(vo);



        final String Date = dateView.getText().toString();
        final String Memo = memoView.getText().toString();
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(getContext());

                        dialog.setIcon(R.drawable.ic_done_all_black_24dp);
                        dialog.setTitle(vo.title);
                        dialog.setMessage(Date+"\n"+Memo);
                        dialog.setNegativeButton("확인", null);
                        dialog.create();
                    dialog.show();

            }
        });
        return convertView;
    }
}

