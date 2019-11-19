package kr.ac.ssu.edugochi.fragment;

/**
 * Created by jay on 01/01/15.
 */

public class TodoItem {


    private int mId;
    private String mTodoTitle;

    public TodoItem(String todoText) {
        mTodoTitle = todoText;
    }

    public String getTodoText() {

        return mTodoTitle;
    }

    public int getId() {

        return mId;
    }

    public void setId(int id) {
        mId = id;
    }
}