package kr.ac.ssu.edugochi.view;

import android.graphics.Typeface;

import kr.ac.ssu.edugochi.listener.CustomNavigationChangeListener;

@SuppressWarnings("unused")
public interface ICustomNavigation {
    void setNavigationChangeListener(CustomNavigationChangeListener navigationChangeListener);

    void setTypeface(Typeface typeface);

    int getCurrentActiveItemPosition();

    void setCurrentActiveItem(int position);

    void setBadgeValue(int position, String value);
}
