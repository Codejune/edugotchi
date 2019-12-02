package kr.ac.ssu.edugochi.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import es.dmoral.toasty.Toasty;
import kr.ac.ssu.edugochi.R;
import kr.ac.ssu.edugochi.activity.LoginActivity;
import kr.ac.ssu.edugochi.eduPreManger;


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

        Preference user = findPreference("login");
        user.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                                Intent intent = new Intent(
                                        getActivity(),
                                        LoginActivity.class);
                                startActivity(intent);
                return false;
            }
        });
    
        final SwitchPreferenceCompat darkMode = (SwitchPreferenceCompat) findPreference("darkMode");
        //다크모드 온오프
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
                    eduPreManger.setBoolean(getActivity(),"darkMode", switched);
                    AppCompatDelegate.setDefaultNightMode(
                            AppCompatDelegate.MODE_NIGHT_YES);
                    return true;
                }
                else {
                  Toasty.error(getActivity(), "darkmode off", Toasty.LENGTH_SHORT).show();
                    eduPreManger.setBoolean(getActivity(),"darkMode", switched);
                    AppCompatDelegate.setDefaultNightMode(
                            AppCompatDelegate.MODE_NIGHT_NO);
                  return true;
                }
            }

        });
        //캐릭터 선택
        ListPreference character = (ListPreference) findPreference("selectCharacter");
        Log.d(TAG, String.valueOf(character));
        character.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String selected= (String) newValue;
                eduPreManger.setString(getActivity(),"selectCharacter", selected);
                return true;
            }
        });
        //백색소음 선택
        ListPreference Wn = (ListPreference) findPreference("white_noise");
        Wn.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue){
                Log.d(TAG, String.valueOf(newValue));
                String selected= (String) newValue;
                eduPreManger.setString(getActivity(),"white_noise", selected);
                return true;
            }
        });

        //초기화
        Preference reset = findPreference("Reset");
        reset.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                AlertDialog dialog = new AlertDialog.Builder(getActivity())
                        .setTitle("초기화 하시겠습니까?")
                        .setPositiveButton("예", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
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
