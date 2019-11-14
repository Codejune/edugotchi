package kr.ac.ssu.edugochi.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

import kr.ac.ssu.edugochi.R;
import kr.ac.ssu.edugochi.TodoDB.TodoDBManager;


public class TodoFragment extends Fragment {

    private static final String TAG = TodoFragment.class.getSimpleName();

    private TodoDBManager mTodoDBManager;

    private EditText mTodoData;
    private View mAdd;
    private ListView mTodoList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mTodoDBManager = new TodoDBManager(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_todo, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mTodoData = (EditText) view.findViewById(R.id.todo_data);
        mAdd = view.findViewById(R.id.btn_add);
        mTodoList = (ListView) view.findViewById(R.id.todo_list);

        listItems();
        initOnAddClick();
    }

    private View.OnClickListener mOnItemDeleteListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Object tag = v.getTag();

            if (tag instanceof TodoItem) {
                TodoItem todoItem = (TodoItem) tag;
                confirmRemoval(todoItem);
            } else {
                Log.w(TAG, " Unexpected tag found. Expected " + TodoItem.class.getSimpleName() + " Found: " + tag.getClass());
            }

        }
    };

    private void confirmRemoval(final TodoItem todoItem) {
        android.app.AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle("정말 삭제하시겠습니까?")
                .setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        removeItem(todoItem);
                    }
                })
                .setNegativeButton("아니오", null)
                .create();
        dialog.show();
    }

    private void removeItem(@NonNull TodoItem todoItem) {
        long rowsAffected = mTodoDBManager.removeItem(todoItem.getId());
        if (rowsAffected == -1) {
            Toast.makeText(getActivity(), getActivity().getString(R.string.remove_item_failed), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getActivity(), getActivity().getString(R.string.remove_item_success), Toast.LENGTH_LONG).show();
            ((ArrayAdapter) mTodoList.getAdapter()).remove(todoItem);            // remove the item to the list
            ((ArrayAdapter) mTodoList.getAdapter()).notifyDataSetChanged();      // update the UI.

            // show empty listview if there are no more items to be displayed.
            if (mTodoList.getAdapter().getCount() == 0) {
                listItems();
            }
        }
    }

    private void listItems() {
        ArrayList<TodoItem> todoItems = getItems();

        if (todoItems == null || todoItems.size() == 0) {
            TextView tv = (TextView) getView().findViewById(R.id.empty_view);
            mTodoList.setEmptyView(tv);
        } else {
            TodoAdapter adapter = new TodoAdapter(getActivity(), R.layout.item_todo, todoItems, mOnItemDeleteListener);
            mTodoList.setAdapter(adapter);
        }
    }

    private void initOnAddClick() {
        if (mAdd != null) {
            mAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addItem();
                }
            });
        }
    }

    private void addItem() {
        if (mTodoData.getText() == null || TextUtils.isEmpty(mTodoData.getText().toString())) {
            Toast.makeText(getActivity(), getActivity().getString(R.string.message_item_not_empty), Toast.LENGTH_LONG).show();
            return;
        }

        String todoText = mTodoData.getText().toString();
        TodoItem todoItem = new TodoItem(todoText);
        long rowId = mTodoDBManager.insert(todoItem);
        todoItem.setId((int) rowId);

        if (rowId == -1) {
            // some issue in adding item.
            Toast.makeText(getActivity(), getActivity().getString(R.string.add_item_failed), Toast.LENGTH_LONG).show();
        } else {
            // successfully added
            mTodoData.setText(null);
            Toast.makeText(getActivity(), getActivity().getString(R.string.add_item_success), Toast.LENGTH_LONG).show();

            if (mTodoList.getAdapter() == null) {
                // if the list was empty previously
                listItems();
            } else {
                ((ArrayAdapter) mTodoList.getAdapter()).insert(todoItem, 0);        // add the item to the top of the list
                ((ArrayAdapter) mTodoList.getAdapter()).notifyDataSetChanged();      // update the UI.
            }
        }
    }

    private ArrayList<TodoItem> getItems() {
        return mTodoDBManager.getItems();
    }

    private void removeAllItems() {
        int rowsAffected = mTodoDBManager.removeAll();

        if (rowsAffected == -1) {
            Toast.makeText(getActivity(), getActivity().getString(R.string.message_remove_all_failed), Toast.LENGTH_LONG).show();
        } else {
            // removal was successful, update the UI.
            ((ArrayAdapter) mTodoList.getAdapter()).clear();    // clear the list in the UI.
            listItems();
        }
    }
}
