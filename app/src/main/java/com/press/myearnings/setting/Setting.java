package com.press.myearnings.setting;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.press.myearnings.MainActivity;
import com.press.myearnings.R;
import com.press.myearnings.StaticUtil;
import com.press.myearnings.database.SchemaDB;
import com.press.myearnings.database.ZarplataHelper;
import com.press.myearnings.dialogs.WarningForRestoredDialog;
import com.press.myearnings.model.Dannie;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

/**
 * Created by PRESS on 30.11.2017.
 */

public class Setting extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    private AppCompatDelegate appCompatDelegate;
    private String pathAppFolder;
    private String pathMyEarning;
    private boolean isImportSuccessfully = false;
    private int changeDelete = -1;

    //    private PreferenceScreen prefSizeZapisi;
//    private PreferenceScreen prefSizeObshee;
//    private PreferenceScreen prefStyleZapisi;
//    private PreferenceScreen prefStyleObshee;
//
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        getDelegate().installViewFactory();
        getDelegate().onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.setting);

        pathMyEarning = Environment.getExternalStoragePublicDirectory("MyEarning").getPath();
        String s = getFilesDir().getPath();
        pathAppFolder = s.substring(0, s.lastIndexOf("/") + 1);
//        Log.d("fileaaaa",pathAppFolder);

//        Preference info = findPreference(getString(R.string.info_programm));
//        info.setOnPreferenceClickListener(prefListenerInfo());

        Preference font = findPreference(getString(R.string.font_setting));
        font.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(Setting.this, SettingAppearance.class);
                startActivity(intent);
                return true;
            }
        });
        Preference.OnPreferenceClickListener onClickBackup = new Preference.OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference preference) {
                if (!StaticUtil.permissionCheck(Setting.this)) {
                    return false;
                }
                int titleRes = preference.getTitleRes();
                if (titleRes == R.string.export_data) {
                    exportBackup();
                } else if (titleRes == R.string.import_data) {
                    importBackup();
                    //                    case R.string.delete_all_data:
//                        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//                            @Override
//                            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                                switch (checkedId) {
//                                    case R.id.radioButton_default_setting:
//                                        changeDelete = 0;
//                                        break;
//                                    case R.id.radioButton_delete_all_database:
//                                        changeDelete = 1;
//                                        break;
//                                    case R.id.radioButton_clear_all:
//                                        changeDelete = 2;
//                                        break;
//                                }
//                            }
//                        });
//
//                        WarningForRestoredDialog warDialog = new WarningForRestoredDialog();
//                        warDialog.setTitle(getString(R.string.warning));
////                        builder.setTitle(R.string.warning);
//                        warDialog.setView(rg);
//                        warDialog.setTitle(getString(R.string.warning_messege_delete_all_data));
//                        warDialog.setPositiveMes(getString(R.string.YES));
//                        WarningForRestoredDialog.OnClickPositiveButton onClickPositiveButton = new WarningForRestoredDialog.OnClickPositiveButton() {
//                            @Override
//                            public void isPositiveButtonClick() {
//
//                                deleteFilePref(new File(pathAppFolder), changeDelete);
//                                if (changeDelete == 1 || changeDelete == 2)
//                                    isImportSuccessfully = true;
//                            }
//                        };
//
//                        warDialog.setNegativeMes(getString(R.string.cancel));
//                        warDialog.show(getFragmentManager(), "changeDelete");
                }
                return true;
            }
        };

        Preference exportBackupPref = findPreference(getString(R.string.export_data));
        exportBackupPref.setOnPreferenceClickListener(onClickBackup);

        Preference importBackupPref = findPreference(getString(R.string.import_data));
        importBackupPref.setOnPreferenceClickListener(onClickBackup);


    }

    private void importBackup() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/zip");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {

//                rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//                    @Override
//                    public void onCheckedChanged(RadioGroup group, int checkedId) {
//                        switch (checkedId) {
//                            case R.id.radioButton_default_setting:
//                                changeDelete = 0;
//                                break;
//                            case R.id.radioButton_delete_all_database:
//                                changeDelete = 1;
//                                break;
//                            case R.id.radioButton_clear_all:
//                                changeDelete = 2;
//                                break;
//                        }
//                    }
//                });
                WarningForRestoredDialog warDialog = new WarningForRestoredDialog();
                warDialog.setTitle(getString(R.string.warning));
//                        builder.setTitle(R.string.warning);
                final LinearLayout checkView = (LinearLayout) getLayoutInflater().inflate(R.layout.change_import, null);
                final CheckBox set = checkView.findViewById(R.id.checkBox_import_setting);
                final CheckBox db = checkView.findViewById(R.id.checkBox_import_db);
                final CheckBox sav = checkView.findViewById(R.id.checkBox_import_save_cyrent);

                db.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            sav.setEnabled(true);
                        } else {
                            sav.setChecked(false);
                            sav.setEnabled(false);
                        }
                    }
                });
                warDialog.setView(checkView);
                warDialog.setPositiveMes(getString(R.string.OK));
                warDialog.setNegativeMes(getString(R.string.cancel));
//                Log.d("fileaaaa", pathUri);
                WarningForRestoredDialog.OnClickPositiveButton onClick = () -> {

                    String uriForIntent = data.getData().getPath();
                    String pathUri = uriForIntent.substring(uriForIntent.lastIndexOf(":") + 1);
                    File file = new File(correctPath(pathUri));

                    if (set.isChecked()) {
                        changeDelete = 0;
//                            File setFile = new File(pathAppFolder+"shared_prefs/"+getPackageName() + "_preferences.xml");
                    }
                    if (db.isChecked()) {
                        changeDelete = 1;
                        if (sav.isChecked())
                            changeDelete = 3;
                    }
                    if (db.isChecked() && set.isChecked()) {

                        changeDelete = 2;
                        if (sav.isChecked())
                            changeDelete = 4;

                    }

                    if (changeDelete > -1) {
                        if (unzipBackupFile(file, changeDelete)) {
                            isImportSuccessfully = true;
                            Toast.makeText(Setting.this, "Резервная копия восстановлена.", Toast.LENGTH_SHORT).show();
//                                final Intent intent = new Intent(Setting.this, Setting.class);
//                                Handler h = new Handler();
//                                h.postAtTime(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        startActivity(intent);
//
//                                    }
//                                }, 5000);
                        }
                    } else
                        Toast.makeText(Setting.this, "Ничего не выбрано", Toast.LENGTH_SHORT).show();
                };
                warDialog.setOnClickPositiveButton(onClick);
                warDialog.show(getFragmentManager(), "warning");


            }
        }


    }

    @Override
    public void onBackPressed() {
        if (isImportSuccessfully) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } else super.onBackPressed();
    }

    private String correctPath(String path) {
        int index = path.indexOf("/");
        if (index != 0) {
            return pathMyEarning.substring(0, pathMyEarning.lastIndexOf("/") + 1) + path;
        }
        return path;
    }

    public void deleteFilePref(File file, int deletedb) {

        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File file1 : files) {
                    deleteFilePref(file1, deletedb);
                }
            }
        } else {
            if (deletedb > 2 || deletedb == 0) {
                if (file.getName().equals("myDataBase.db")) {
                    return;
                }
            }
            if (deletedb == 1 || deletedb == 3) {
                if (file.getName().equals(getPackageName() + "_preferences.xml")) {
                    return;
                }
            }
            file.delete();
        }
    }

    private boolean unzipBackupFile(File file, int deletedb) {
        try {
            FileInputStream inputStream = new FileInputStream(file);
            deleteFilePref(new File(pathAppFolder), deletedb);
            ZipInputStream zipInputStream = new ZipInputStream(inputStream);
            ZipEntry zipEntry;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                unzipFile(zipEntry, zipInputStream);
                zipInputStream.closeEntry();
            }

            File dbFile = new File(pathAppFolder + "cache/myDataBase.db");
            if (deletedb > 0) {
                SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(dbFile, null);
                ZarplataHelper.changeStructureDb(this, db, SchemaDB.Table.NAME_CARD);
            }
            dbFile.delete();
            return true;

        } catch (IOException e) {
            Toast.makeText(this, "Во время восстановления произошла ошибка!", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        return false;
    }

    private void unzipFile(ZipEntry zipEntry, InputStream inputStream) throws IOException {
        String nameFile = zipEntry.getName();
        String name = nameFile.substring(nameFile.lastIndexOf('/') + 1);
        File file;
//        Log.d("nameFile ", name);
        switch (name) {
            case "myDataBase.db":
                file = new File(pathAppFolder + "cache/" + name);
                break;
            case "com.press.earnings_preferences.xml":
                file = new File(pathAppFolder + nameFile.substring(0, nameFile.lastIndexOf('/') + 1),
                        getPackageName() + "_preferences.xml");
                break;
            default:
                file = new File(pathAppFolder + nameFile);
                break;
        }
        if (zipEntry.isDirectory()) {
            file.mkdirs();
        } else {
            if (!file.exists()) {

                FileOutputStream out = new FileOutputStream(file);
                byte[] buffer = new byte[1024];
                int len;
                while ((len = inputStream.read(buffer)) > 0) {
                    out.write(buffer, 0, len);
                }
                out.close();
            }
        }
    }

    private void exportBackup() {
        File fileDb = new File(pathAppFolder + "databases/myDataBase.db");
        File filePrefFolder = new File(pathAppFolder + "shared_prefs/");
        File[] prefFolder = filePrefFolder.listFiles();


        String pathOutDb = "databases/";
        String pathOutPref = "shared_prefs/";

        File myDirect = new File(pathMyEarning);
        myDirect.mkdirs();

        FileOutputStream stream;
        ZipOutputStream outzip;
        FileInputStream dbInput;
        try {
            //имя ахрива
            String nameZipBackup = "/earningPref_" + new Dannie().getDate(-1) + ".zip";
            //поток создания файла
            stream = new FileOutputStream(pathMyEarning + nameZipBackup);
            //создание архиива на основе потока
            outzip = new ZipOutputStream(stream);
            //создание принимающего потока файла текущей базы данных
            dbInput = new FileInputStream(fileDb);
            //создание новой записи для базы данных в ахив
            ZipEntry dDb = new ZipEntry(pathOutDb + "myDataBase.db");
            //добавление этой записи в архив
            outzip.putNextEntry(dDb);

            //копирование информации из источника в архив
            byte[] bufer = new byte[1024];
            int len;
            // запись в массив байтов инфы сколько влезет
            while ((len = dbInput.read(bufer)) > 0) {
                //запись в архив данные из массива байтов
                outzip.write(bufer, 0, len);
            }
            //закрытие записи в архив
            outzip.closeEntry();
            //запись настроек в архив
            for (File f : prefFolder) {
                dbInput = new FileInputStream(f);
                ZipEntry z = new ZipEntry(pathOutPref + f.getName());
                outzip.putNextEntry(z);
                while ((len = dbInput.read(bufer)) > 0) {
                    outzip.write(bufer, 0, len);
                }
                outzip.closeEntry();
            }

            dbInput.close();
            outzip.close();
        } catch (IOException e) {
//            Log.d("исключение", " 2");
            e.printStackTrace();
        } finally {
            Toast.makeText(getApplicationContext(), "Резервная копия сохранена в папке \"sdCard/myEarning\"", Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 777) {
            if (grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "Отказано в доступе!", Toast.LENGTH_SHORT).show();
            } else {
                for (int grantResult : grantResults) {
                    if (grantResult == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(getApplicationContext(), "Доступ разрешен!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }
        }
    }

    public static void copyFileNoNewFile(File inFile, File outFile) {
        try {

            FileInputStream inputStream = new FileInputStream(inFile);
            FileOutputStream outputStream = new FileOutputStream(outFile);

            FileChannel in = inputStream.getChannel();
            FileChannel out = outputStream.getChannel();

            in.transferTo(0, in.size(), out);

            inputStream.close();
            outputStream.close();
            in.close();
            out.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
//            Toast.makeText(getApplicationContext(), "Ошибка при сохранении", Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            e.printStackTrace();
//            Toast.makeText(getApplicationContext(), "Неизвестная ошибка", Toast.LENGTH_SHORT).show();
        }
    }

//    private void copyFile(File inFile, File outFile) {
//        try {
//
//            FileInputStream inputStream = new FileInputStream(inFile);
//            FileOutputStream outputStream = new FileOutputStream(outFile);
//
//            FileChannel in = inputStream.getChannel();
//            FileChannel out = outputStream.getChannel();
//
//            in.transferTo(0, in.size(), out);
//
//            inputStream.close();
//            outputStream.close();
//            in.close();
//            out.close();
//
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//            Toast.makeText(getApplicationContext(), "Ошибка при сохранении", Toast.LENGTH_SHORT).show();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//            Toast.makeText(getApplicationContext(), "Неизвестная ошибка", Toast.LENGTH_SHORT).show();
//        }
//    }

//    private Preference.OnPreferenceClickListener prefListenerInfo() {
//        return new Preference.OnPreferenceClickListener() {
//            @Override
//            public boolean onPreferenceClick(Preference preference) {
//                ViewGroup infoView = (ViewGroup) getLayoutInflater().inflate(R.layout.info, null);
//                TextView sber = infoView.findViewById(R.id.sberbank_tap_copy);
//                TextView webM = infoView.findViewById(R.id.webmoney_tap_copy);
//
//
//                View.OnClickListener listener = new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        Animation animation = AnimationUtils.loadAnimation(Setting.this, R.anim.anim_for_infodonat);
//                        view.startAnimation(animation);
//                        ClipboardManager clipboardManager = (ClipboardManager) Setting.this.getSystemService(Context.CLIPBOARD_SERVICE);
//                        ClipData clipData = ClipData.newPlainText("", ((TextView) view).getText().toString());
//                        if (clipboardManager != null) {
//                            clipboardManager.setPrimaryClip(clipData);
//                        }
//                        Toast.makeText(Setting.this, R.string.toast_copy_donat, Toast.LENGTH_SHORT).show();
//                    }
//                };
//
//                sber.setOnClickListener(listener);
//                webM.setOnClickListener(listener);
//
//                AlertDialog.Builder infoDialog = new AlertDialog.Builder(Setting.this);
//                infoDialog.setView(infoView);
//                infoDialog.show();
//
//                return true;
//            }
//        };
//    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getDelegate().onPostCreate(savedInstanceState);
    }

    public ActionBar getSupportActionBar() {
        return getDelegate().getSupportActionBar();
    }

    public void setSupportActionBar(@Nullable Toolbar toolbar) {
        getDelegate().setSupportActionBar(toolbar);
    }

    @NonNull
    @Override
    public MenuInflater getMenuInflater() {
        return getDelegate().getMenuInflater();
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        getDelegate().setContentView(layoutResID);
    }

    @Override
    public void setContentView(View view) {
        getDelegate().setContentView(view);
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        getDelegate().setContentView(view, params);
    }

    @Override
    public void addContentView(View view, ViewGroup.LayoutParams params) {
        getDelegate().addContentView(view, params);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        getDelegate().onPostResume();
    }

    @Override
    protected void onTitleChanged(CharSequence title, int color) {
        super.onTitleChanged(title, color);
        getDelegate().setTitle(title);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        getDelegate().onConfigurationChanged(newConfig);
    }

    @Override
    protected void onStop() {
        super.onStop();
        getDelegate().onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getDelegate().onDestroy();
    }

    public void invalidateOptionsMenu() {
        getDelegate().invalidateOptionsMenu();
    }


    private AppCompatDelegate getDelegate() {
        if (appCompatDelegate == null) {
            appCompatDelegate = AppCompatDelegate.create(this, null);
        }
        return appCompatDelegate;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Preference checkSimvol = findPreference(getString(R.string.chek_simvol));
        boolean isChecked = getPreferenceScreen().getSharedPreferences().getBoolean(getString(R.string.simvol_rub_string), false);
        checkSimvol.setEnabled(isChecked);

        ListPreference listDatePrefZapisi = (ListPreference) findPreference(getString(R.string.date_pref_for_zapisi));
        ListPreference listDatePrefCard = (ListPreference) findPreference(getString(R.string.date_pref_for_card));
        Dannie d = new Dannie();
        String dateSetEntries[] = {
                d.getDate(0),
                d.getDate(1),
                d.getDate(2),
                d.getDate(3),
                d.getDate(4),
                d.getDate(5),
                d.getDate(6),
                d.getDate(7),
                d.getDate(8),
                d.getDate(9)
        };
        listDatePrefZapisi.setEntries(dateSetEntries);
        listDatePrefCard.setEntries(dateSetEntries);

        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if (s.equals(getString(R.string.simvol_rub_string))) {
            Preference simvolCheck = findPreference(getString(R.string.chek_simvol));
            if (sharedPreferences.getBoolean(s, false)) {
                simvolCheck.setEnabled(true);
            } else simvolCheck.setEnabled(false);
        }
    }

    // на стадии
}
