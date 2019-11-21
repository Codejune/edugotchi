package kr.ac.ssu.edugochi.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

import kr.ac.ssu.edugochi.R;
import kr.ac.ssu.edugochi.TodoDB.TodoAdapter;
import kr.ac.ssu.edugochi.TodoDB.TodoDBHandler;
import kr.ac.ssu.edugochi.TodoDB.TodoVO;


public class TodoFragment extends Fragment {

    private static final String TAG = TodoFragment.class.getSimpleName();

    private TodoDBHandler handler;
    private ImageButton AddBtn;
    private ListView TodoList;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        handler = new TodoDBHandler(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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
            if (tag instanceof TodoVO) {
                TodoVO vo = (TodoVO) tag;
                confirmRemoval(vo);
            } else {
                Log.w(TAG, " Unexpected tag found. Expected " + TodoVO.class.getSimpleName() + " Found: " + tag.getClass());
            }
        }
    };


    private void confirmRemoval(final TodoVO vo) {
        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle("정말 삭제하시겠습니까?")
                .setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        removeItem(vo);
                        listItems();
                    }
                })
                .setNegativeButton("아니오", null)
                .create();
        dialog.show();
    }

    private void removeItem(TodoVO vo) {
        long rowsAffected = handler.removeItem(vo.id);
        if (rowsAffected == -1) {
        Toast.makeText(getActivity(), getActivity().getString(R.string.remove_item_failed), Toast.LENGTH_LONG).show();
        } else {
        Toast.makeText(getActivity(), getActivity().getString(R.string.remove_item_success), Toast.LENGTH_LONG).show();
        ((ArrayAdapter) TodoList.getAdapter()).remove(vo);            // remove the item to the list
        ((ArrayAdapter) TodoList.getAdapter()).notifyDataSetChanged();
        listItems();// update the UI.
        if (TodoList.getAdapter().getCount() == 0) {
            listItems();
        }
        }
    }


    public void onResume() { // AddTodo 엑티비티에서 복귀 했을때 리스트 갱신
        listItems();
        Log.d(this.getClass().getSimpleName(), "리스트갱신");
        super.onResume();
    }


    private void listItems() { //받아온 데이터를 어뎁터를 통해 리스트뷰에 전달
        ArrayList<TodoVO> data = getItems();

        if (data == null || data.size() == 0) {
            TextView tv = getView().findViewById(R.id.empty_view);
            TodoList.setEmptyView(tv);
        } else {
            Log.d(this.getClass().getSimpleName(), "리스트갱신되는중");
            TodoAdapter adapter = new TodoAdapter(getActivity(), R.layout.item_todo, data, mOnItemDeleteListener);
            TodoList.setAdapter(adapter);
        }

    }
    private ArrayList<TodoVO> getItems() { //arraylist에 데이터를 받아옴
        return handler.getItems();
    }
}


