package kr.ac.ssu.edugochi.fragment;

import android.content.Context;
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

import java.io.File;

import es.dmoral.toasty.Toasty;
import kr.ac.ssu.edugochi.R;
import kr.ac.ssu.edugochi.activity.user.LoginActivity;
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
                                AlertDialog dialog = new AlertDialog.Builder(getActivity())
                                        .setTitle("초기화 하면 앱이 종료됩니다.")
                                        .setMessage("초기화 하시겠습니까?")
                                        .setPositiveButton("예", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                Context ctxt = getContext();
                                                clearApplicationData(ctxt);
                                                System.exit(0);
                                            }
                                        })
                                        .setNegativeButton("아니오", null)
                                        .create();
                                dialog.show();

                            }
                        })
                        .setNegativeButton("아니오", null)
                        .create();
                dialog.show();
                return false;
            }
        });
    }
    private static void clearApplicationData(Context context) {
        File cache = context.getCacheDir();
        File appDir = new File(cache.getParent());
        if (appDir.exists()) {
            String[] children = appDir.list();
            for (String s : children) {
                //다운로드 파일은 지우지 않도록 설정
                //if(s.equals("lib") || s.equals("files")) continue;
                deleteDir(new File(appDir, s));
                Log.d("test", "File /data/data/" + context.getPackageName() + "/" + s + " DELETED");
            }
        }
    }
    private static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }


    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preference, rootKey);
    }
}
