package kr.ac.ssu.edugochi.TodoDB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TodoDBHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public TodoDBHelper(Context context) {
        super((Context) context, "tododb", null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String todoSQL = "create table tb_todo (" +
                "_id integer primary key autoincrement, " +
                "title, " +
                "date, " +
                "memo)";
        db.execSQL(todoSQL);
    }
    @Override
   public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { if (newVersion == DATABASE_VERSION) {
        db.execSQL("drop table tb_todo");
        onCreate(db);
    }
    }
}
