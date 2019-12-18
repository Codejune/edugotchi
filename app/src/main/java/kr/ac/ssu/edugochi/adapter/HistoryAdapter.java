package kr.ac.ssu.edugochi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import io.realm.RealmResults;
import kr.ac.ssu.edugochi.R;
import kr.ac.ssu.edugochi.fragment.TimelineFragment;
import kr.ac.ssu.edugochi.object.MeasureData;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    private RealmResults<MeasureData> measureList;
    int hour, min, sec;

    public HistoryAdapter(RealmResults<MeasureData> data) {
        measureList = data;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView historyText1, historyText2;

        ViewHolder(View itemView) {
            super(itemView);
            historyText1 = itemView.findViewById(R.id.info_history1);
            historyText2 = itemView.findViewById(R.id.info_history2);
        }
    }

    @NonNull
    @Override
    public HistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.item_history, parent, false);
        HistoryAdapter.ViewHolder holder = new HistoryAdapter.ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryAdapter.ViewHolder holder, int position) {
        holder.historyText1.setText(measureList.get(position).getDate().substring(0, 4) + "년 " + measureList.get(position).getDate().substring(5, 7) + "월 " + measureList.get(position).getDate().substring(8, 10) + "일에 " + measureList.get(position).getSubject() + " 공부를");
        String time = TimelineFragment.makeTimeForm(measureList.get(position).getTimeout());
        hour = Integer.parseInt(time.substring(0, 2));
        min = Integer.parseInt(time.substring(3, 5));
        sec = Integer.parseInt(time.substring(6, 8));

        if (hour > 0)
            if (min > 0)
                if (sec > 0)
                    holder.historyText2.setText(hour + "시간 " + min + "분 " + sec + "초 동안 했고 " + measureList.get(position).getExp() + "exp를 획득했다.");
                else
                    holder.historyText2.setText(hour + "시간 " + min + "분 동안 했고 " + measureList.get(position).getExp() + "exp를 획득했다.");
            else if (sec > 0)
                holder.historyText2.setText(hour + "시간" + sec + "초 동안 했고 " + measureList.get(position).getExp() + "exp를 획득했다.");
            else
                holder.historyText2.setText(hour + "시간 동안 했고 " + measureList.get(position).getExp() + "exp를 획득했다.");
        else if (min > 0)
            if (sec > 0)
                holder.historyText2.setText(min + "분 " + sec + "초 동안 했고 " + measureList.get(position).getExp() + "exp를 획득했다.");
            else
                holder.historyText2.setText(min + "분 동안 했고 " + measureList.get(position).getExp() + "exp를 획득했다.");
        else if (sec > 0)
            holder.historyText2.setText(sec + "초 동안 했고 " + measureList.get(position).getExp() + "exp를 획득했다.");

    }

    @Override
    public int getItemCount() {
        return measureList.size();
    }
}
