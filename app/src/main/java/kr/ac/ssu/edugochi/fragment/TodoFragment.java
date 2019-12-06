package kr.ac.ssu.edugochi.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;
import kr.ac.ssu.edugochi.R;
import kr.ac.ssu.edugochi.TodoDB.TodoDBHandler;
import kr.ac.ssu.edugochi.activity.AddTodoActivity;
import kr.ac.ssu.edugochi.adapter.TodoAdapter;
import kr.ac.ssu.edugochi.object.TodoItem;


public class TodoFragment extends Fragment {

    private static final String TAG = TodoFragment.class.getSimpleName();

    private TodoDBHandler handler;
    private ImageButton AddBtn;
    private SwipeMenuListView TodoList;
    private TodoAdapter adapter;
    private TextView tv;

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
        tv = view.findViewById(R.id.empty_view);
        AddBtn = view.findViewById(R.id.action_add_task);
        TodoList = view.findViewById(R.id.todo_list);
        tv = view.findViewById(R.id.empty_view);
        listItems();
    }

    private void confirmRemoval(final TodoItem vo) {
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

    private void removeItem(TodoItem vo) {
        long rowsAffected = handler.removeItem(vo.id);
        if (rowsAffected == -1) {
            Toasty.error(getActivity(), getActivity().getString(R.string.remove_item_failed), Toast.LENGTH_SHORT).show();
        } else {
            Toasty.success(getActivity(), getActivity().getString(R.string.remove_item_success), Toast.LENGTH_SHORT).show();
            listItems();// DB에서 삭제후 리스트 갱신
            if (TodoList.getAdapter().getCount() == 0) {
                listItems();
            }
        }
    }


    public void onResume() { // AddTodoActivity 엑티비티에서 복귀 했을때 리스트 갱신
        listItems();
        Log.d(this.getClass().getSimpleName(), "리스트갱신");
        super.onResume();
    }


    private void listItems() { //받아온 데이터를 어뎁터를 통해 리스트뷰에 전달
        final ArrayList<TodoItem> data = getItems();

        if (data == null || data.size() == 0) {
            Log.d(TAG, "아이템 없다");
            Log.d(TAG, "setEmptyView!");
            TodoList.setVisibility(View.GONE);
            tv.setVisibility(View.VISIBLE);
            //TodoList.setEmptyView(tv);
        } else {
            Log.d(this.getClass().getSimpleName(), "리스트갱신되는중");
            TodoList.setVisibility(View.VISIBLE);
            tv.setVisibility(View.GONE);
            adapter = new TodoAdapter(getActivity(), R.layout.item_todo, data);
            TodoList.setAdapter(adapter);
            TodoList.setMenuCreator(creator);
            TodoList.setOnSwipeListener(new SwipeMenuListView.OnSwipeListener() {

                @Override
                public void onSwipeStart(int position) {
                    // swipe start
                    TodoList.smoothOpenMenu(position);
                }

                @Override
                public void onSwipeEnd(int position) {
                    // swipe end
                    TodoList.smoothOpenMenu(position);
                }
            });
            TodoList.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                    TodoItem vo = data.get(position);
                    switch (index) {
                        case 0:
                            // open
                            Log.d("", "--------상세정......");
                            MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(getContext());
                            dialog.setIcon(R.drawable.ic_done_all_black_24dp);
                            dialog.setTitle(vo.title);
                            dialog.setMessage("\n기한 : "+vo.date+"\n\n메모 : "+vo.memo);
                            dialog.setNegativeButton("확인", null);
                            dialog.create();
                            dialog.show();
                            break;
                        case 1:
                            confirmRemoval(vo);
                            break;
                    }
                    // false : close the menu; true : not close the menu
                    return false;
                }
            });
        }


    }


    private ArrayList<TodoItem> getItems() { //arraylist에 데이터를 받아옴
        return handler.getItems();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_task:
                Intent intent = new Intent(
                        getActivity(),
                        AddTodoActivity.class);
                startActivity(intent);
                break;
        }
        return true;
    }

    SwipeMenuCreator creator = new SwipeMenuCreator() {

        @Override
        public void create(SwipeMenu menu) {
            // create "open" item
            SwipeMenuItem openItem = new SwipeMenuItem(
                    getActivity());
            // set item background
            openItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9,
                    0xCE)));
            // set item width
            openItem.setWidth(200);
            // set item title
            openItem.setTitle("상세");
            // set item title fontsize
            openItem.setTitleSize(18);
            // set item title font color
            openItem.setTitleColor(Color.WHITE);
            // add to menu
            menu.addMenuItem(openItem);

            // create "delete" item
            SwipeMenuItem deleteItem = new SwipeMenuItem(
                    getActivity());
            // set item background
            deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                    0x3F, 0x25)));
            // set item width
            deleteItem.setWidth(200);
            // set a icon
            deleteItem.setIcon(R.drawable.ic_delete);
            // add to menu
            menu.addMenuItem(deleteItem);
        }
    };

}


