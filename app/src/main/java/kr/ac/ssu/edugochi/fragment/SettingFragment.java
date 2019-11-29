package kr.ac.ssu.edugochi.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import es.dmoral.toasty.Toasty;
import kr.ac.ssu.edugochi.R;


public class SettingFragment extends PreferenceFragmentCompat {
    private static final String TAG = SettingFragment.class.getSimpleName();

    public SettingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SwitchPreferenceCompat darkMode = (SwitchPreferenceCompat) findPreference("darkMode");

        darkMode.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
        {
            @Override
            public boolean onPreferenceChange(Preference preference,
                                              Object newValue) {
                Log.d(TAG, String.valueOf(newValue));
                boolean switched = (boolean) newValue;
                Log.d(TAG, String.valueOf(switched));
                if (switched) {
                    Toasty.success(getActivity(), "darkmode on", Toasty.LENGTH_SHORT).show();
                    AppCompatDelegate.setDefaultNightMode(
                            AppCompatDelegate.MODE_NIGHT_YES);
                    return true;
                }
                else {
                  Toasty.error(getActivity(), "darkmode off", Toasty.LENGTH_SHORT).show();
                    AppCompatDelegate.setDefaultNightMode(
                            AppCompatDelegate.MODE_NIGHT_NO);
                  return true;
                }
            }

        });



    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preference, rootKey);


    }


}
