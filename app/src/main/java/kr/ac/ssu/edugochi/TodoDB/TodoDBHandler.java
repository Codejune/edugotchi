package kr.ac.ssu.edugochi.TodoDB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;

import java.util.ArrayList;

import kr.ac.ssu.edugochi.object.TodoItem;


public class TodoDBHandler{
    private Context mContext;
    private ArrayList<TodoItem> data;



    public TodoDBHandler(Context context) {
        mContext = context;
    }

    public static TodoDBHandler open(Context context) throws SQLException {

        return new TodoDBHandler(context);

    }
    public long insert(String title, String date, String memo) {
        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("date", date);
        values.put("memo", memo);
        return TodoDBHelper.getInstance(mContext).insert("tb_todo", values);
    }
    public ArrayList<TodoItem> getItems() {

        Cursor cursor = TodoDBHelper.getInstance(mContext).select("tb_todo", null, null, null, null, null, null);

        if (cursor == null || cursor.getCount() == 0) {
            return null;
        }
        ArrayList<TodoItem> data = new ArrayList<TodoItem>();

        while (cursor.moveToNext()) {
            TodoItem vo = new TodoItem();
            vo.id = cursor.getInt(0);
            vo.title = cursor.getString(1);
            vo.date = cursor.getString(2);
            vo.memo = cursor.getString(3);
            data.add(vo);
        }
        return data;
    }

    public long removeItem(int id) {
        String whereClause = "_id" + "=?";
        String[] whereArgs = new String[]{Integer.toString(id)};
        return TodoDBHelper.getInstance(mContext).delete("tb_todo", whereClause, whereArgs);
    }
    public void close() {
        //mHelper.close();
    }
}


