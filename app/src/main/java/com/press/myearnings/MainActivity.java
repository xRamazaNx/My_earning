package com.press.myearnings;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.press.myearnings.adapter.AdapterCard;
import com.press.myearnings.database.SchemaDB;
import com.press.myearnings.database.ZarplataHelper;
import com.press.myearnings.dialogs.CompleteAllDialog;
import com.press.myearnings.dialogs.InputNameCardDialog;
import com.press.myearnings.model.Dannie;
import com.press.myearnings.model.DannieCard;
import com.press.myearnings.setting.ColumnName;
import com.press.myearnings.setting.Setting;

import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class MainActivity extends AppCompatActivity {
    private static final String IS_ARHIVE = "is_archive";
    //    private boolean sizeCARDItem;
    private static String ALLMONEY;
    private static String ALLAVANS;
    private static String ALLKVIDACHE;
    private List<DannieCard> cardList;
    private Handler handler;
    //    private SharedPreferences preferences;
//    private SharedPreferences.Editor editor;
    private List<Dannie> listDannie;
    private FragmentManager fragmentManager;
    private int sortIn = -1;
    private int sortmetod = 0;
    private Toolbar toolbar;
    private SharedPreferences preferences;
    private ViewGroup viewGroup;
    private ArrayList<DannieCard> cardListSorted;
    private boolean isArchive;

    @SuppressLint("HandlerLeak")
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        preferences = getSharedPreferences(SchemaDB.Name.PREFERENCES, MODE_MULTI_PROCESS);
        isArchive = getIntent().getBooleanExtra(IS_ARHIVE, false);
        if (isArchive) {
            setTitle(R.string.ARHIVE);
        } else {

            setTitle(R.string.app_zarplata_label);
            fragmentManager = getSupportFragmentManager();

//            setSupportActionBar(toolbar);
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//            toolbar.setNavigationIcon(R.drawable.ic_donat);

            String pathFilesDir = getFilesDir().getPath();
            String pathPackage = pathFilesDir.substring(0, pathFilesDir.lastIndexOf("/") + 1);

//        Log.d("проверка", getCacheDir().getAbsolutePath());

            String pathDB = pathPackage + "databases/";
            File oldNameDb = new File(pathDB + "zarplataDataBase.db");
            // проверка на старое название базы данных, в случае чего замена имени
            if (oldNameDb.exists()) {
                File outNewDb = new File(pathDB + "myDataBase.db");
                Setting.copyFileNoNewFile(oldNameDb, outNewDb);
                oldNameDb.delete();
            }

            new ZarplataHelper(this).getWritableDatabase().close();

            viewGroup = (LinearLayout) getLayoutInflater().inflate(R.layout.sort_ard, null);
            switcher(viewGroup);
        }
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                //удаление
                if (msg.what == 11) {
                    updateMassivRecycler();
                    getRecycler();
                }
                //завершение всех карточек
                if (msg.what == 5) {
                    onSetArhiveAllCard();
                    updateMassivRecycler();
                    getRecycler();
                }
            }
        };
//        toolbar.setTitleTextColor(SettingColorSet.getToolbarTitleColor(
//                PreferenceManager.getDefaultSharedPreferences(this), false));

    }


    private void sortBy(final int sortIn, int reverse) {

        cardListSorted = new ArrayList<>(cardList);
        Collections.sort(cardListSorted, new Comparator<DannieCard>() {
            @Override
            public int compare(DannieCard o1, DannieCard o2) {
                switch (sortIn) {
                    case -1:
                        break;
                    case 0:
                        return o1.getDateForCreated().compareTo(o2.getDateForCreated());
                    case 1:
                        return o1.getDateForModify().compareTo(o2.getDateForModify());
                }
                return 0;
            }
        });
        if (reverse == 1) {
            Collections.reverse(cardListSorted);
        }

    }

    private void createNewTable(String name) {
        Dannie dannieForTable = new Dannie();
        String IDforNameDB = String.valueOf(dannieForTable.getDateMl());

        SQLiteDatabase database = new ZarplataHelper(MainActivity.this).getWritableDatabase();
        database.execSQL(SchemaDB.createTableArhive(IDforNameDB));
//                    database.execSQL(SchemaDB.createTableArhive(String.valueOf(size)));
        ContentValues values = new ContentValues();

        String nameTable = SchemaDB.Table.NAME_TABLE_DATA + IDforNameDB;
        values.put(SchemaDB.Table.Cols.NAME_FOR_TABLE_CARD, name);
        values.put(SchemaDB.Table.NAME_TABLE_DATA, nameTable);
        values.put(SchemaDB.Table.Cols.DATE_CREATE_TABLE, IDforNameDB);
        values.put(SchemaDB.Table.Cols.DATE_MODIFY_TABLE, IDforNameDB);// миллисикунды в лонг будут использоваться для определения даты
        values.put(SchemaDB.Table.IS_ARCHIVE, 0);

        database.insert(SchemaDB.Table.NAME_CARD, null, values);

        ColumnName columnName = new ColumnName();
        columnName.inflateFromPref(PreferenceManager.getDefaultSharedPreferences(this));
        columnName.setNamesToDB(database, nameTable);

        database.close();
    }

    private void getRecycler() {
        RecyclerView recyclerView = findViewById(R.id.id_recycler_main);
        LinearLayout infoLayout = findViewById(R.id.isNullItemInfo);

        if (!isArchive) {
            if (cardList.size() == 0) {
                infoLayout.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
//            TextView addButtonIsNotCard = findViewById(R.id.add_new_is_not_card);
                infoLayout.getChildAt(0).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        newCard();
                    }
                });
                return;
            } else {
                infoLayout.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
        } else
            sortIn = -1;
        sortBy(sortIn, sortmetod);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        AdapterCard adapterCard = new AdapterCard(cardListSorted, handler, isArchive);
        recyclerView.setAdapter(adapterCard);
    }

    private void updateMassivRecycler() {

        SQLiteDatabase database = new ZarplataHelper(this).getWritableDatabase();
        cardList = new ArrayList<>();

        Cursor cursorCard = database.query(SchemaDB.Table.NAME_CARD, null, null, null, null, null, null);

        if (cursorCard.moveToFirst()) {
            do {
                int archive = cursorCard.getInt(cursorCard.getColumnIndexOrThrow(SchemaDB.Table.IS_ARCHIVE));
                boolean isArchive = archive == 1;
                if (this.isArchive != isArchive)
                    continue;

                String nameCard = cursorCard.getString(cursorCard.getColumnIndexOrThrow(SchemaDB.Table.Cols.NAME_FOR_TABLE_CARD));
                String nameTable = cursorCard.getString(cursorCard.getColumnIndexOrThrow(SchemaDB.Table.NAME_TABLE_DATA));
                String dateCreated = cursorCard.getString(cursorCard.getColumnIndexOrThrow(SchemaDB.Table.Cols.DATE_CREATE_TABLE));
                String dateModify = cursorCard.getString(cursorCard.getColumnIndexOrThrow(SchemaDB.Table.Cols.DATE_MODIFY_TABLE));

                Cursor cursor = database.query(nameTable, null, null, null, null, null, null);

                listDannie = new ArrayList<>();
                if (cursor.moveToFirst()) { // пересчет внутреннего содержания
                    do {

                        int id = cursor.getInt(cursor.getColumnIndexOrThrow(SchemaDB.Table.Cols.ID));
                        String rul = cursor.getString(cursor.getColumnIndexOrThrow(SchemaDB.Table.Cols.SUMMA));
                        String av = cursor.getString(cursor.getColumnIndexOrThrow(SchemaDB.Table.Cols.AVANS));
                        String dateMl = cursor.getString(cursor.getColumnIndexOrThrow(SchemaDB.Table.Cols.DATE_LONG));
                        String coment = cursor.getString(cursor.getColumnIndexOrThrow(SchemaDB.Table.Cols.COMENT));

                        float rul1 = 0;
                        float avans = 0;

                        if (!rul.equals("")) {
                            rul1 = Float.parseFloat(rul);
                        }
                        if (!av.equals("")) {
                            avans = Float.parseFloat(av);
                        }

                        // заполнение массива

                        Dannie dannie = new Dannie();
                        dannie.setID(id);

                        if (rul1 > 0) {
                            dannie.setSumma(String.valueOf(rul1));
                        } else dannie.setSumma("");

                        if (avans > 0) {
                            dannie.setAvans(String.valueOf(avans));
                        } else dannie.setAvans("");


                        // вытаскиваем дату
//
//                        int year = cursor.getInt(cursor.getColumnIndexOrThrow(SchemaDB.Table.Cols.DATE_YEAR));
//                        int month = cursor.getInt(cursor.getColumnIndexOrThrow(SchemaDB.Table.Cols.DATE_MONTH));
//                        int day = cursor.getInt(cursor.getColumnIndexOrThrow(SchemaDB.Table.Cols.DATE_DAY));
//
//                        dannie.setDateInt(day, month, year);
                        dannie.setDateMl(Long.parseLong(dateMl));
                        dannie.setComent(coment);

                        listDannie.add(dannie);
                    } while (cursor.moveToNext());
                }
                cursor.close();

                updateSummaAll();
                DannieCard d = new DannieCard();
                d.isArchive = isArchive;
                d.columnName.inflateFromDB(database, nameTable);
                if (d.columnName.isAllNull()) {
                    d.columnName.inflateFromPref(getSharedPreferences(getPackageName() + "_preferences", MODE_MULTI_PROCESS));
                    d.columnName.setNamesToDB(database, nameTable);
                }
                d.setNameCard(nameCard);
                d.setNameTable(nameTable);
                d.setDateForCreated(dateCreated);
                d.setDateForModify(dateModify);

                d.setSumma(ALLMONEY);
                d.setAvans(ALLAVANS);
                d.setOstatok(ALLKVIDACHE);
                d.setDateForArchive("-");

                if (listDannie.size() > 0) {

                    SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    int varVisDate = Integer.parseInt(pref.getString(getString(R.string.date_pref_for_card), "1"));
                    String oldDate = listDannie.get(0).getDate(varVisDate);
                    String newDate = listDannie.get(listDannie.size() - 1).getDate(varVisDate);
                    d.setDateForArchive(oldDate + "  -  " + newDate);

                }
                cardList.add(d);


            } while (cursorCard.moveToNext());
        }
        cursorCard.close();
        database.close();

    }

    private void updateSummaAll() {

        float isummaAvans = 0;
        float isummaAllmoney = 0;
        float isummaMoneyKVidache = 0;

        for (Dannie d : listDannie) {
            float a = 0;
            float c = 0;
            if (!d.getSumma().equals("")) a = Float.parseFloat(d.getSumma());
            if (!d.getAvans().equals("")) c = Float.parseFloat(d.getAvans());

            isummaAvans += c;
            isummaAllmoney += a;
            isummaMoneyKVidache = isummaAllmoney - isummaAvans;
        }
        NumberFormat numberFormat = new DecimalFormat();
        numberFormat.setMaximumFractionDigits(2);

        ALLMONEY = numberFormat.format(isummaAllmoney);
        ALLAVANS = numberFormat.format(isummaAvans);
        ALLKVIDACHE = numberFormat.format(isummaMoneyKVidache);
    }

    private void onSetArhiveAllCard() {
        SQLiteDatabase database = new ZarplataHelper(this).getWritableDatabase();
        for (DannieCard dc : cardList) {
            dc.isArchive = true;
            ContentValues value = new ContentValues();
            value.put(SchemaDB.Table.IS_ARCHIVE, 1);
            database.update(SchemaDB.Table.NAME_CARD, value, SchemaDB.Table.NAME_TABLE_DATA + " = ?", new String[]{dc.getNameTable()});
        }
//        Cursor cursor = database.query(SchemaDB.Table.NAME_CARD, null, null, null, null, null, null);
//        if (cursor.moveToFirst()) {
//            do {
//                String nameTable = cursor.getString(cursor.getColumnIndexOrThrow(SchemaDB.Table.NAME_TABLE_DATA));
//                String nameCard = cursor.getString(cursor.getColumnIndexOrThrow(SchemaDB.Table.Cols.NAME_FOR_TABLE_CARD));
//                String dateCreated = cursor.getString(cursor.getColumnIndexOrThrow(SchemaDB.Table.Cols.DATE_CREATE_TABLE));
//                String dateModify = cursor.getString(cursor.getColumnIndexOrThrow(SchemaDB.Table.Cols.DATE_MODIFY_TABLE));
//
//
//                ContentValues value = new ContentValues();
//                value.put(SchemaDB.Table.NAME_TABLE_DATA, nameTable);
//                value.put(SchemaDB.Table.Cols.NAME_FOR_TABLE_CARD, nameCard);
//                value.put(SchemaDB.Table.Cols.DATE_CREATE_TABLE, dateCreated);
//                value.put(SchemaDB.Table.Cols.DATE_MODIFY_TABLE, dateModify);
//
//                database.insert(SchemaDB.Table.NAME_ARHIVE, null, value);
//                //
//            } while (cursor.moveToNext());
//
//            database.delete(SchemaDB.Table.NAME_CARD, null, null);
//            database.close();
//            cursor.close();
//        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        if (isArchive)
            getMenuInflater().inflate(R.menu.menu_actity_arhive, menu);
        else {
            getMenuInflater().inflate(R.menu.card_main_menu, menu);
//            menu.findItem(android.R.id.home).setIcon(R.drawable.ic_donat);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();
        if (itemId == R.id.id_delete_all_cardArchive) {
            if (cardList.size() == 0) {
                Toast.makeText(this, R.string.arhive_null, Toast.LENGTH_SHORT).show();
                return true;
            }
            new AlertDialog.Builder(this)
                    .setMessage(R.string.is_kill_all_arhive)
                    .setPositiveButton(R.string.YES, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            SQLiteDatabase database = new ZarplataHelper(MainActivity.this).getWritableDatabase();
                            for (DannieCard dc : cardList) {
                                database.delete(SchemaDB.Table.NAME_CARD, SchemaDB.Table.NAME_TABLE_DATA + " = ?", new String[]{dc.getNameTable()});
                            }
                            database.close();

                            updateMassivRecycler();
                            getRecycler();
                        }
                    })
                    .setNegativeButton(R.string.NO, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    })
                    .show();
        } else if (itemId == R.id.id_card_menu_add) {
            newCard();
        } else if (itemId == R.id.id_sort_card) {
            showSortWindow(radioButtonLayout());
        } else if (itemId == R.id.id_get_arhive) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(IS_ARHIVE, true);
            startActivity(intent);
        } else if (itemId == R.id.id_card_menu_complete_all) {
            if (cardList.size() == 0) {
                Toast.makeText(this, R.string.is_no_record_main, Toast.LENGTH_SHORT).show();
            } else {
                CompleteAllDialog dialogFragment = new CompleteAllDialog();
                dialogFragment.setHandler(handler);
                dialogFragment.show(fragmentManager, "completteAll");
            }
        } else if (itemId == R.id.id_setting_item_menu) {
            Intent intent1 = new Intent(this, Setting.class);
            startActivityForResult(intent1, 33);
        }
        return true;
    }

    private void newCard() {
        InputNameCardDialog nameCardDialog = InputNameCardDialog.get("", new InputNameCardDialog.OkClickListener() {
            @Override
            public void click(String name) {
                createNewTable(name);
                updateMassivRecycler();
                getRecycler();
            }
        });
        nameCardDialog.show(fragmentManager, "nameCard");
    }

    private void showSortWindow(ViewGroup viewGroup) {

        PopupWindow window = new PopupWindow(this);
        window.setContentView(viewGroup);
        window.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        window.setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new BitmapDrawable());
        window.setFocusable(true);
        window.setOutsideTouchable(true);

        int statusBarHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics());
        window.showAtLocation(toolbar, Gravity.RIGHT | Gravity.TOP, 0, toolbar.getHeight() + statusBarHeight);
    }

    private ViewGroup radioButtonLayout() {

        RadioGroup radioGroup1 = viewGroup.findViewById(R.id.id_radiogroup_sort_sort);
        switch (sortIn) {
            case -1:
                radioGroup1.check(R.id.radioButton_default_card);
                break;
            case 0:
                radioGroup1.check(R.id.radioButton_date_reated);
                break;
            case 1:
                radioGroup1.check(R.id.radioButton_date_modify);
                break;

        }

        radioGroup1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.radioButton_default_card) {
                    sortIn = -1;
                }
                if (i == R.id.radioButton_date_reated) {
                    sortIn = 0;
                }
                if (i == R.id.radioButton_date_modify) {
                    sortIn = 1;
                }

                setPreferenceForSort(sortIn, sortmetod);
                getRecycler();

            }
        });
        //////////////////////////
        RadioGroup radioGroup2 = viewGroup.findViewById(R.id.id_radiogroup_sort2_metod);
        switch (sortmetod) {
            case 0:
                radioGroup2.check(R.id.radioButtonUp_card);
                break;
            case 1:
                radioGroup2.check(R.id.radioButtonDown_card);
                break;
        }
        radioGroup2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.radioButtonUp_card:
                        sortmetod = 0;
                        break;
                    case R.id.radioButtonDown_card:
                        sortmetod = 1;
                        break;
                }
                setPreferenceForSort(sortIn, sortmetod);
                getRecycler();
            }
        });

        return viewGroup;
    }

    private void setPreferenceForSort(int valueSort, int valueMetod) {

        SharedPreferences.Editor editor = preferences.edit();

        editor.putInt(SchemaDB.Name.Item.RADIO_GROUP_CARD, valueSort);
        editor.putInt(SchemaDB.Name.Item.RADIO_METOD_CARD, valueMetod);

        editor.apply();

    }

    private void getPreferenceForSort() {

        boolean radiogroup = preferences.contains(SchemaDB.Name.Item.RADIO_GROUP_CARD);
        boolean radiometod = preferences.contains(SchemaDB.Name.Item.RADIO_METOD_CARD);

        if (radiogroup && radiometod) {
            sortIn = preferences.getInt(SchemaDB.Name.Item.RADIO_GROUP_CARD, -1);
            sortmetod = preferences.getInt(SchemaDB.Name.Item.RADIO_METOD_CARD, 0);
        }
    }

    private void switcher(ViewGroup viewGroup) {

        Switch sortSaveSwitch = viewGroup.findViewById(R.id.id_switch_save_sort_card);

        if (preferences.contains(SchemaDB.Name.Item.SAVESWITCHSORT_CARD)) {
            //визуальная часть вкл или выкл
            boolean checked = preferences.getBoolean(SchemaDB.Name.Item.SAVESWITCHSORT_CARD, false);
            sortSaveSwitch.setChecked(checked);
            if (checked) {
                getPreferenceForSort();
            }

        }
        sortSaveSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean(SchemaDB.Name.Item.SAVESWITCHSORT_CARD, b);
                editor.apply();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    protected void onResume() {
        super.onResume();
        updateMassivRecycler();
        getRecycler();
    }

//    private int refractDateOldVersion(String date) {
//        String d =
//                date.substring(10)
//                        + date.substring(7, 9)
//                        + date.substring(4, 6);
//
//        Log.d("life", d);
//        return Integer.parseInt(d);
//
//    }


}
