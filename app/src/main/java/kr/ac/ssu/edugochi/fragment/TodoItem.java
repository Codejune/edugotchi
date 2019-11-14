package kr.ac.ssu.edugochi.fragment;

/**
 * Created by jay on 01/01/15.
 */

public class TodoItem {


    private int mId;
    private String mTodoText;

    public TodoItem(String todoText) {
        mTodoText = todoText;
    }

    public String getTodoText() {
        return mTodoText;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }
}