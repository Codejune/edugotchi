package kr.ac.ssu.edugochi.TodoDB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TodoDBHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    private static TodoDBHelper _instance;
    private SQLiteDatabase sqLiteDatabase;

    public TodoDBHelper(Context context) {
        super(context, "tododb", null, DATABASE_VERSION);
        sqLiteDatabase = this.getWritableDatabase();
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
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion == DATABASE_VERSION) {
            db.execSQL("drop table tb_todo");
            onCreate(db);
        }
    }

    public static TodoDBHelper getInstance(Context context) { //db 열어주기
        if (_instance == null) {
            _instance = new TodoDBHelper(context);
        }
        if (!_instance.sqLiteDatabase.isOpen()) {               //오류로 닫히면 다시 열어준다.
            _instance.sqLiteDatabase = _instance.getWritableDatabase();
        }
        return _instance;
    }

    public long insert(String tableName, ContentValues contentValues) { //데이터 삽입
        return sqLiteDatabase.insert(tableName, null, contentValues);
    }

    public Cursor select(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
        return sqLiteDatabase.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
    }

    public int delete(String table, String whereClause, String[] whereArgs) {
        return sqLiteDatabase.delete(table, whereClause, whereArgs);
    }

}
