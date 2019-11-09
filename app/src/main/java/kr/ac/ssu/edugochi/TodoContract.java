package kr.ac.ssu.edugochi;

import android.provider.BaseColumns;

public class TodoContract {
    public static final String DB_NAME = "kr.ac.ssu.edugochi.todolist.db";
    public static final int DB_VERSION = 1;

    public class TaskEntry implements BaseColumns {
        public static final String TABLE = "tasks";

        public static final String COL_TASK_TITLE = "title";
    }

}
