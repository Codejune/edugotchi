package kr.ac.ssu.edugochi.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import java.io.File;

import es.dmoral.toasty.Toasty;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import kr.ac.ssu.edugochi.R;
import kr.ac.ssu.edugochi.eduPreManger;
import kr.ac.ssu.edugochi.object.Character;
import kr.ac.ssu.edugochi.realm.module.UserModule;
import kr.ac.ssu.edugochi.realm.utils.Migration;


public class SettingFragment extends PreferenceFragmentCompat {
    private static final String TAG = SettingFragment.class.getSimpleName();
    private String id;
    private String nickname;
    private static String USERTABLE = "User.realm";
    private RealmConfiguration UserModuleConfig;
    private Realm userRealm;
    private RealmResults<Character> characterList;


    public SettingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RealmInit();

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getListView().setPadding(120, 0, 120, 0);
        id = eduPreManger.getString(getActivity(),"id");
        userRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                characterList = getCharacterList();
                nickname =characterList.get(0).getName();
            }
        });
        final Preference user_id = (Preference) findPreference("id");
        user_id.setTitle(nickname);
        user_id.setSummary(id);

        final EditText input = new EditText(getActivity());

        Preference ChangeNick = (Preference) findPreference("nickname");
        ChangeNick.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                AlertDialog.Builder nickdialog = new AlertDialog.Builder(getActivity())
                        .setTitle("변경할 닉네임을 입력해주세요.");
                if (input.getParent() != null)
                    ((ViewGroup) input.getParent()).removeView(input);
                nickdialog.setView(input)
                        .setPositiveButton("예", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String nicktxt = input.getText().toString();
                                Log.d(TAG,"@@@@@@@@2"+nicktxt+"@@@@@@@");
                                if(!nicktxt.isEmpty()) {
                                    eduPreManger.setString(getActivity(), "nickname", input.getText().toString());
                                    user_id.setTitle(input.getText().toString());
                                    userRealm.executeTransaction(new Realm.Transaction() {
                                        @Override
                                        public void execute(Realm realm) {

                                            characterList = getCharacterList();
                                            characterList.get(0).setName(input.getText().toString());
                                        }
                                    });
                                }else{
                                    Toasty.error(getActivity(),"닉네임이 비어있습니다.",Toasty.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton("아니오", null)
                        .create();
                nickdialog.show();
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
                Log.d(TAG, String.valueOf(newValue));
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

    private void RealmInit() {
        // User.realm 모듈 설정
        UserModuleConfig = new RealmConfiguration.Builder()
                .modules(new UserModule())
                .migration(new Migration())
                .schemaVersion(0)
                .name(USERTABLE)
                .build();

        userRealm = Realm.getInstance(UserModuleConfig);
        characterList = getCharacterList();
    }

    // 캐릭터 데이터 리스트 반환
    private RealmResults<Character> getCharacterList() {
        return userRealm.where(Character.class).findAll();
    }

}
