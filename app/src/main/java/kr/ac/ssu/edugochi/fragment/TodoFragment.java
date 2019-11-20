package kr.ac.ssu.edugochi.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

import kr.ac.ssu.edugochi.R;
import kr.ac.ssu.edugochi.TodoDB.TodoAdapter;
import kr.ac.ssu.edugochi.TodoDB.TodoDBHelper;
import kr.ac.ssu.edugochi.TodoDB.TodoVO;


public class TodoFragment extends Fragment {

    private static final String TAG = TodoFragment.class.getSimpleName();
    private TodoDBHelper helper;
    private ImageButton AddBtn;
    private ListView TodoList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        helper = new TodoDBHelper(getActivity());
        return inflater.inflate(R.layout.fragment_todo, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        AddBtn = view.findViewById(R.id.btn_add);
        AddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(
                        getActivity(),
                        AddTodo.class);
                startActivity(intent);
            }
        });
        TodoList = view.findViewById(R.id.todo_list);
        listItems();
    }


    private View.OnClickListener mOnItemDeleteListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Object tag = v.getTag();
            TodoVO title = (TodoVO)tag;

                confirmRemoval(title);

        }
    };



        private void confirmRemoval(final TodoVO title) {
            AlertDialog dialog = new AlertDialog.Builder(getActivity())
                    .setTitle("정말 삭제하시겠습니까?")
                    .setPositiveButton("예", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            removeItem(title);
                        }
                    })
                    .setNegativeButton("아니오", null)
                    .create();
            dialog.show();
        }

        private void removeItem(TodoVO title) {


            //long rowsAffected = mTodoDBManager.removeItem(todoItem.getId());
            //if (rowsAffected == -1) {
               // Toast.makeText(getActivity(), getActivity().getString(R.string.remove_item_failed), Toast.LENGTH_LONG).show();
           // } else {

                Toast.makeText(getActivity(), getActivity().getString(R.string.remove_item_success), Toast.LENGTH_LONG).show();
                ((ArrayAdapter) TodoList.getAdapter()).remove(title);            // remove the item to the list
                ((ArrayAdapter) TodoList.getAdapter()).notifyDataSetChanged();      // update the UI.
                if (TodoList.getAdapter().getCount() == 0) {
                    listItems();
             //   }

            }
        }


    public void onResume(){
        listItems();
        Log.d(this.getClass().getSimpleName(), "리스트갱신");
        super.onResume();
    }


    public void listItems(){
        helper = new TodoDBHelper(getActivity());
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from tb_todo", null);

        ArrayList<TodoVO> data = new ArrayList<>();
        while (cursor.moveToNext()) {
            TodoVO vo = new TodoVO();
            vo.title = cursor.getString(1);
            vo.date = cursor.getString(2);
            data.add(vo);
        }
        db.close();
        TodoAdapter adapter = new TodoAdapter(getActivity(), R.layout.item_todo, data);
        TodoList.setAdapter(adapter);
/*
        if (TodoAdapter.getCount() == 0) {
            TextView tv = getView().findViewById(R.id.empty_view);
            TodoList.setEmptyView(tv);

        } else {

*/


        }
}


