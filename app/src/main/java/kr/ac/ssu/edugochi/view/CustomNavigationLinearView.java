package kr.ac.ssu.edugochi.view;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import java.util.ArrayList;

import kr.ac.ssu.edugochi.listener.CustomNavigationChangeListener;

public class CustomNavigationLinearView extends LinearLayout implements View.OnClickListener, ICustomNavigation {

    //constants
    private static final String TAG = "BNLView";
    private static final int MIN_ITEMS = 2;
    private static final int MAX_ITEMS = 5;

    private ArrayList<CustomToggleView> customNavItems;
    private CustomNavigationChangeListener navigationChangeListener;

    private int currentActiveItemPosition = 0;
    private boolean loadPreviousState;

    private Typeface currentTypeface;

    private SparseArray<String> pendingBadgeUpdate;

    /**
     * Constructors
     */
    public CustomNavigationLinearView(@NonNull Context context) {
        super(context);
        init(context, null);
    }

    public CustomNavigationLinearView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CustomNavigationLinearView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("superState", super.onSaveInstanceState());
        bundle.putInt("current_item", currentActiveItemPosition);
        bundle.putBoolean("load_prev_state", true);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            currentActiveItemPosition = bundle.getInt("current_item");
            loadPreviousState = bundle.getBoolean("load_prev_state");
            state = bundle.getParcelable("superState");
        }
        super.onRestoreInstanceState(state);
    }

    /////////////////////////////////////////
    // PRIVATE METHODS
    /////////////////////////////////////////

    /**
     * Initialize
     *
     * @param context current context
     * @param attrs   custom attributes
     */
    private void init(Context context, AttributeSet attrs) {

        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER);

        post(new Runnable() {
            @Override
            public void run() {
                updateChildNavItems();
            }
        });
    }

    private void updateChildNavItems() {
        customNavItems = new ArrayList<>();
        for (int index = 0; index < getChildCount(); ++index) {
            View view = getChildAt(index);
            if (view instanceof CustomToggleView)
                customNavItems.add((CustomToggleView) view);
            else {
                Log.w(TAG, "Cannot have child customNavItems other than customToggleView");
                return;
            }
        }

        if (customNavItems.size() < MIN_ITEMS) {
            Log.w(TAG, "The customNavItems list should have at least 2 customNavItems of customToggleView");
        } else if (customNavItems.size() > MAX_ITEMS) {
            Log.w(TAG, "The customNavItems list should not have more than 5 customNavItems of customToggleView");
        }

        setClickListenerForItems();
        setInitialActiveState();
        updateMeasurementForItems();

        //update the typeface
        if (currentTypeface != null)
            setTypeface(currentTypeface);

        //update the badge count
        if (pendingBadgeUpdate != null && customNavItems != null) {
            for (int i = 0; i < pendingBadgeUpdate.size(); i++)
                setBadgeValue(pendingBadgeUpdate.keyAt(i), pendingBadgeUpdate.valueAt(i));
            pendingBadgeUpdate.clear();
        }
    }

    /**
     * Makes sure that ONLY ONE child {@link #customNavItems} is active
     */
    private void setInitialActiveState() {

        if (customNavItems == null) return;

        boolean foundActiveElement = false;

        // find the initial state
        if (!loadPreviousState) {
            for (int i = 0; i < customNavItems.size(); i++) {
                if (customNavItems.get(i).isActive() && !foundActiveElement) {
                    foundActiveElement = true;
                    currentActiveItemPosition = i;
                } else {
                    customNavItems.get(i).setInitialState(false);
                }
            }
        } else {
            for (int i = 0; i < customNavItems.size(); i++) {
                customNavItems.get(i).setInitialState(false);
            }
        }
        //set the active element
        if (!foundActiveElement)
            customNavItems.get(currentActiveItemPosition).setInitialState(true);
    }

    /**
     * Update the measurements of the child components {@link #customNavItems}
     */
    private void updateMeasurementForItems() {
        int numChildElements = customNavItems.size();
        if (numChildElements > 0) {
            int calculatedEachItemWidth = (getMeasuredWidth() - (getPaddingRight() + getPaddingLeft())) / numChildElements;
            for (CustomToggleView btv : customNavItems)
                btv.updateMeasurements(calculatedEachItemWidth);
        }
    }

    /**
     * Sets {@link android.view.View.OnClickListener} for the child views
     */
    private void setClickListenerForItems() {
        for (CustomToggleView btv : customNavItems)
            btv.setOnClickListener(this);
    }

    /**
     * Gets the Position of the Child from {@link #customNavItems} from its id
     *
     * @param id of view to be searched
     * @return position of the Item
     */
    private int getItemPositionById(int id) {
        for (int i = 0; i < customNavItems.size(); i++)
            if (id == customNavItems.get(i).getId())
                return i;
        return -1;
    }

    ///////////////////////////////////////////
    // PUBLIC METHODS
    ///////////////////////////////////////////

    /**
     * Set the navigation change listener {@link CustomNavigationChangeListener}
     *
     * @param navigationChangeListener sets the passed parameters as listener
     */
    @Override
    public void setNavigationChangeListener(CustomNavigationChangeListener navigationChangeListener) {
        this.navigationChangeListener = navigationChangeListener;
    }

    /**
     * Set the {@link Typeface} for the Text Elements of the View
     *
     * @param typeface to be used
     */
    @Override
    public void setTypeface(Typeface typeface) {
        if (customNavItems != null) {
            for (CustomToggleView btv : customNavItems)
                btv.setTitleTypeface(typeface);
        } else {
            currentTypeface = typeface;
        }
    }

    /**
     * Gets the current active position
     *
     * @return active item position
     */
    @Override
    public int getCurrentActiveItemPosition() {
        return currentActiveItemPosition;
    }

    /**
     * Sets the current active item
     *
     * @param position current position change
     */
    @Override
    public void setCurrentActiveItem(int position) {

        if (customNavItems == null) {
            currentActiveItemPosition = position;
            return;
        }

        if (currentActiveItemPosition == position) return;

        if (position < 0 || position >= customNavItems.size())
            return;

        CustomToggleView btv = customNavItems.get(position);
        btv.performClick();
    }

    /**
     * Sets the badge value
     *
     * @param position current position change
     * @param value    value to be set in the badge
     */
    @Override
    public void setBadgeValue(int position, String value) {
        if (customNavItems != null) {
            CustomToggleView btv = customNavItems.get(position);
            if (btv != null)
                btv.setBadgeText(value);
        } else {
            if (pendingBadgeUpdate == null)
                pendingBadgeUpdate = new SparseArray<>();
            pendingBadgeUpdate.put(position, value);
        }
    }

    @Override
    public void onClick(View v) {
        int changedPosition = getItemPositionById(v.getId());
        if (changedPosition >= 0) {
            if (changedPosition == currentActiveItemPosition) {
                return;
            }
            CustomToggleView currentActiveToggleView = customNavItems.get(currentActiveItemPosition);
            CustomToggleView newActiveToggleView = customNavItems.get(changedPosition);
            if (currentActiveToggleView != null)
                currentActiveToggleView.toggle();
            if (newActiveToggleView != null)
                newActiveToggleView.toggle();

            //changed the current active position
            currentActiveItemPosition = changedPosition;

            if (navigationChangeListener != null)
                navigationChangeListener.onNavigationChanged(v, currentActiveItemPosition);
        } else {
            Log.w(TAG, "Selected id not found! Cannot toggle");
        }
    }
}
