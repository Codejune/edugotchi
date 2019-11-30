package kr.ac.ssu.edugochi.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
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
        SharedPreferences prefs;
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        prefs
                .registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
                    public void onSharedPreferenceChanged(SharedPreferences sp, String key) {
                        Log.d("tag","클릭된 Preference의 key는 "+key);
                    }
                });



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

        ListPreference WN = (ListPreference)findPreference("WhiteNoise");
        Log.d(TAG, String.valueOf(WN));
        Preference reset = findPreference("Reset");
        reset.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                AlertDialog dialog = new AlertDialog.Builder(getActivity())
                        .setTitle("초기화 하시겠습니까?")
                        .setPositiveButton("예", null)
                         /*
                         @Override
                         public void onClick(DialogInterface dialogInterface, int i) {
                             removeItem(vo);
                             listItems();
                         }
                          */

                        .setNegativeButton("아니오", null)
                        .create();
                dialog.show();
                return false;
            }
        });
        

    }




    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preference, rootKey);
    }
}
