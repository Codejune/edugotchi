package kr.ac.ssu.edugochi.TodoDB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;

import kr.ac.ssu.edugochi.fragment.TodoItem;


public class TodoDBManager {

    private Context mContext;
    private ArrayList<TodoItem> items;

    public TodoDBManager(Context context) {
        mContext = context;
    }

    public long insert(TodoItem todoItem) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(TodoDBConfig.COL_DATA, todoItem.getTodoText());
        return TodoDBHelper.getInstance(mContext).insert(TodoDBConfig.TABLE_TODO, contentValues);
    }

    public ArrayList<TodoItem> getItems() {
        // example of a raw query.
        // Cursor cursor = TodoDBHelper.getInstance(mContext).execSQL("select * from " + TodoDBConfig.TABLE_TODO, null);

        // alternatively you are also query as below.
        Cursor cursor = TodoDBHelper.getInstance(mContext).select(TodoDBConfig.TABLE_TODO, null, null, null, null, null, null);

        if (cursor == null || cursor.getCount() == 0) {
            return null;
        }

        ArrayList<TodoItem> items = new ArrayList<TodoItem>();

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(TodoDBConfig.COL_ID));
            String todoText = cursor.getString(cursor.getColumnIndex(TodoDBConfig.COL_DATA));
            TodoItem todoItem = new TodoItem(todoText);
            todoItem.setId(id);
            items.add(todoItem);
        }   // end while

        return items;
    }

    /**
     * Removes all the items from the table
     *
     * @return Number of rows affected.
     */
    public int removeAll() {
        return TodoDBHelper.getInstance(mContext).delete(TodoDBConfig.TABLE_TODO, null, null);
    }

    public long removeItem(int id) {
        String whereClause = TodoDBConfig.COL_ID + "=?";
        String[] whereArgs = new String[]{Integer.toString(id)};
        return TodoDBHelper.getInstance(mContext).delete(TodoDBConfig.TABLE_TODO, whereClause, whereArgs);
    }
}
