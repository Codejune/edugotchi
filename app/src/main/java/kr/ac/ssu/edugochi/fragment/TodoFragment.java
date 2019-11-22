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
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;
import kr.ac.ssu.edugochi.R;
import kr.ac.ssu.edugochi.TodoDB.TodoDBHandler;
import kr.ac.ssu.edugochi.activity.AddTodoActivity;
import kr.ac.ssu.edugochi.adapter.TodoAdapter;
import kr.ac.ssu.edugochi.object.TodoObject;


public class TodoFragment extends Fragment {

    private static final String TAG = TodoFragment.class.getSimpleName();

    private TodoDBHandler handler;
    private ImageButton AddBtn;
    private SwipeMenuListView TodoList;

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

        TodoList = view.findViewById(R.id.todo_list);
        listItems();
    }

    private View.OnClickListener mOnItemDeleteListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Object tag = v.getTag();
            if (tag instanceof TodoObject) {
                TodoObject vo = (TodoObject) tag;
                confirmRemoval(vo);
            } else {
                Log.w(TAG, " Unexpected tag found. Expected " + TodoObject.class.getSimpleName() + " Found: " + tag.getClass());
            }
        }
    };


    private void confirmRemoval(final TodoObject vo) {
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

    private void removeItem(TodoObject vo) {
        long rowsAffected = handler.removeItem(vo.id);
        if (rowsAffected == -1) {
            Toasty.error(getActivity(), getActivity().getString(R.string.remove_item_failed), Toast.LENGTH_LONG).show();
        } else {
            Toasty.success(getActivity(), getActivity().getString(R.string.remove_item_success), Toast.LENGTH_LONG).show();
            ((ArrayAdapter) TodoList.getAdapter()).remove(vo);            // remove the item to the list
            ((ArrayAdapter) TodoList.getAdapter()).notifyDataSetChanged();
            listItems();// update the UI.
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
        ArrayList<TodoObject> data = getItems();

        if (data == null || data.size() == 0) {
            TextView tv = getView().findViewById(R.id.empty_view);
            TodoList.setEmptyView(tv);
        } else {
            Log.d(this.getClass().getSimpleName(), "리스트갱신되는중");
            TodoAdapter adapter = new TodoAdapter(getActivity(), R.layout.item_todo, data, mOnItemDeleteListener);
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
                    switch (index) {
                        case 0:
                            // open
                            Log.d("", "--------close......");
                            break;
                        case 1:
                            // delete
                            Log.d("", "--------delete......");
                            break;
                    }
                    // false : close the menu; true : not close the menu
                    return false;
                }
            });
        }

    }


    private ArrayList<TodoObject> getItems() { //arraylist에 데이터를 받아옴
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
            openItem.setTitle("Open");
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
            deleteItem.setIcon(R.drawable.ic_close_black_24dp);
            // add to menu
            menu.addMenuItem(deleteItem);
        }
    };

}


