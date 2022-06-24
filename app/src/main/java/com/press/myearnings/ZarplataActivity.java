package com.press.myearnings;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
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
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.press.myearnings.adapter.AdapterStroke;
import com.press.myearnings.database.SchemaDB;
import com.press.myearnings.database.ZarplataHelper;
import com.press.myearnings.dialogs.InputNameCardDialog;
import com.press.myearnings.dialogs.NewZapis;
import com.press.myearnings.dialogs.ScreenDialog;
import com.press.myearnings.model.CopyData;
import com.press.myearnings.model.Dannie;
import com.press.myearnings.setting.ColumnName;
import com.press.myearnings.setting.Setting;
import com.press.myearnings.setting.SettingColorSet;

import java.io.File;
import java.io.FileOutputStream;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static android.view.View.inflate;
import static com.press.myearnings.setting.SettingAppearance.KEY_AVANS_CARD_TITLE_NAME;
import static com.press.myearnings.setting.SettingAppearance.KEY_AVANS_TITLE_NAME;
import static com.press.myearnings.setting.SettingAppearance.KEY_DATE_TITLE_NAME;
import static com.press.myearnings.setting.SettingAppearance.KEY_ID_TITLE_NAME;
import static com.press.myearnings.setting.SettingAppearance.KEY_OSTATOK_TITLE_NAME;
import static com.press.myearnings.setting.SettingAppearance.KEY_SUMMA_CARD_TITLE_NAME;
import static com.press.myearnings.setting.SettingAppearance.KEY_SUMMA_TITLE_NAME;
import static com.press.myearnings.setting.SettingAppearance.KEY_ZAMETKA_TITLE_NAME;

public class ZarplataActivity extends AppCompatActivity {
    public static final int MESSEG_TO_EDIT_ITEM = 50;
    public static final int MESSEG_TO_PASTE_ITEM = 30;
    public static final int MESSEG_TO_DUBLICATE_ITEM = 40;
    public static final int MESSEG_TO_UPDATE = 25;
    public final static int REQ_EDIT = 4;
    public static final int REQ_PASTE = 5;
    public static final String NAME_TABLE = "name_table";
    public static final int REQUEST_NEW_ZAPIS = 2;
    private LinearLayout sortLayout;
    private LinearLayout periodLayout;
    private Toolbar toolbar;
    private Handler handler;
    private TextView idTitle;
    private TextView summaTitle;
    private TextView avansTitle;
    private TextView noteTitle;
    private TextView dateTitle;
    private TextView summaAllmoney;
    private TextView summaAvans;
    private TextView ostatok;
    private TextView allSummaTitle;
    private TextView allAvansTitle;
    private TextView ostatotTitle;
    private String ALLMONEY;
    private String ALLAVANS;
    private String ALLKVIDACHE;
    private SQLiteDatabase database;
    private PopupWindow window;
    private SharedPreferences preferences;
    private int sort = -1;
    private int sortmetod = 0;
    private String nameTable;
    private String nameCard;
    private String dateModifyTable;
    private boolean isArchive;
    private int KEY_ID = 0;
    private SharedPreferences pref;
    private Menu mMenu;
    private boolean isBack = true;
    private RecyclerView recyclerView;
    private Animation anim;
    private Animation animToAll;
    private List<Dannie> listDanniePeriod;
    private List<Dannie> listDannie;
    private boolean isPeriodShow = false;
    private Spinner firstDateOfPeriodSpinner;
    private Spinner lastDateOfPeriodSpinner;
    private LinearLayout monthLayout;
    private final String[] monthNames = {"Январь", "Февраль", "Март", "Апрель", "Май", "Июнь", "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"};
    private int allSummaOfSortFromDate = 0;
    private CheckBox dayCheckBox;
    private CheckBox periodCheckBox;
    private long first = 0;
    private long last = 0;
    private DecimalFormat numberFormat;

    private void findAll() {
        periodLayout = findViewById(R.id.period);
        firstDateOfPeriodSpinner = findViewById(R.id.period_from);
        lastDateOfPeriodSpinner = findViewById(R.id.period_to);
        monthLayout = findViewById(R.id.months_layout);

        recyclerView = findViewById(R.id.id_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(ZarplataActivity.this));

        idTitle = findViewById(R.id.zarplata_title_number);
        summaTitle = findViewById(R.id.zarplata_title_summa);
        avansTitle = findViewById(R.id.zarplata_title_avans);
        noteTitle = findViewById(R.id.zarplata_title_zametka);
        dateTitle = findViewById(R.id.zarplata_title_date);

        summaAvans = findViewById(R.id.id_summ_avans);
        summaAllmoney = findViewById(R.id.id_summ_all_money);
        ostatok = findViewById(R.id.id_summ_money_k_vidache);

        allSummaTitle = findViewById(R.id.card_title_vsego_z);
        allAvansTitle = findViewById(R.id.card_title_avans_z);
        ostatotTitle = findViewById(R.id.card_title_ostatok_z);

        recyclerView.post(new Runnable() {
            @Override
            public void run() {

                anim = new ScaleAnimation(1, 1.1F, 1, 1.1F, summaTitle.getWidth() / 2F, summaTitle.getHeight() / 2F);
                anim.setRepeatMode(Animation.REVERSE);
                anim.setDuration(150);
                anim.setRepeatCount(Animation.INFINITE);

                animToAll = new ScaleAnimation(1, 1.1F, 1, 1.1F, allAvansTitle.getWidth() / 2f, allAvansTitle.getHeight() / 2f);
                animToAll.setRepeatMode(Animation.REVERSE);
                animToAll.setDuration(150);
                animToAll.setRepeatCount(Animation.INFINITE);
            }
        });
    }

    private void setColorAll() {
        SettingColorSet.getTextColor(pref, idTitle, SettingColorSet.ViewName.idTitle);
        SettingColorSet.getTextColor(pref, summaTitle, SettingColorSet.ViewName.summaTitle);
        SettingColorSet.getTextColor(pref, avansTitle, SettingColorSet.ViewName.avansTitle);
        SettingColorSet.getTextColor(pref, noteTitle, SettingColorSet.ViewName.noteTitle);
        SettingColorSet.getTextColor(pref, dateTitle, SettingColorSet.ViewName.dateTitle);

        SettingColorSet.getTextColor(pref, summaAllmoney, SettingColorSet.ViewName.summaCard);
        SettingColorSet.getTextColor(pref, summaAvans, SettingColorSet.ViewName.avansCard);
        SettingColorSet.getTextColor(pref, ostatok, SettingColorSet.ViewName.ostatok);

        SettingColorSet.getTextColor(pref, allSummaTitle, SettingColorSet.ViewName.summaCardTitle);
        SettingColorSet.getTextColor(pref, allAvansTitle, SettingColorSet.ViewName.avansCardTitle);
        SettingColorSet.getTextColor(pref, ostatotTitle, SettingColorSet.ViewName.ostatokTitle);

    }

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zarplata);
        pref = getSharedPreferences(getPackageName() + "_preferences", Context.MODE_MULTI_PROCESS);
        preferences = getSharedPreferences(SchemaDB.Name.PREFERENCES, MODE_MULTI_PROCESS);

        numberFormat = new DecimalFormat();
        numberFormat.setMaximumFractionDigits(2);
        numberFormat.setRoundingMode(RoundingMode.HALF_EVEN);

        findAll();
        setColorAll();
        settingFont();

        handler = new Handler();
        isArchive = getIntent().getBooleanExtra("toolbar", false);

        nameTable = getIntent().getStringExtra("nametable");
        nameCard = getIntent().getStringExtra("namecard");
        String dateCreatedTable = getIntent().getStringExtra("namedatecreatedtable");
        dateCreatedTable = getIntent().getStringExtra("namedatemodifydtable");

        toolbar = findViewById(R.id.toolbar);
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == MESSEG_TO_UPDATE) {
                    updateAll(-1);
                    updateDateModify();
                    return;
                }
                int id = msg.arg1;
                int position = getPosition(id);
                if (position == -1) return;
                Dannie dannie = listDanniePeriod.get(position);
                Intent intent;

                switch (msg.what) {
                    // если вставили
                    case MESSEG_TO_PASTE_ITEM:
                        Dannie copyDannie = CopyData.getCopy().copy;
                        getChangePasteWin(copyDannie, position);
                        break;
                    case MESSEG_TO_DUBLICATE_ITEM:
                        intent = StaticUtil.getIntentOfShowEditWin(ZarplataActivity.this, dannie, 1);
                        add(intent, true, id);
                        updateAll(id);
                        break;
                    case MESSEG_TO_EDIT_ITEM:
                        intent = StaticUtil.getIntentOfShowEditWin(ZarplataActivity.this, dannie, msg.arg2);
                        intent.putExtra(NAME_TABLE, nameTable);
                        startActivityForResult(intent, REQ_EDIT);
                        break;

                }
            }
        };
        if (!isArchive) {
            toolbar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    InputNameCardDialog nameCardDialog = InputNameCardDialog.get(nameCard, new InputNameCardDialog.OkClickListener() {
                        @Override
                        public void click(String name) {
                            nameCard = name;
                            SQLiteDatabase database = new ZarplataHelper(ZarplataActivity.this).getWritableDatabase();
                            ContentValues value = new ContentValues();
                            value.put(SchemaDB.Table.Cols.NAME_FOR_TABLE_CARD, name);
                            database.update(SchemaDB.Table.NAME_CARD, value,
                                    SchemaDB.Table.NAME_TABLE_DATA + " = ?",
                                    new String[]{nameTable});
                            toolbar.setTitle(nameCard);
                            updateDateModify();
                        }
                    });
                    nameCardDialog.show(getSupportFragmentManager(), "nameEdit");
                }
            });
        }
        toolbar.setTitle(nameCard);
        toolbar.setTitleTextColor(SettingColorSet.getToolbarTitleColor(pref));
        setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        // окошко для сортировки
        sortLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.sort, null);
        window = new PopupWindow(this);
        //сортировка
        switcher();
    }

    private void getChangePasteWin(final Dannie copyDannie, final int position) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final RadioGroup radioGroup = (RadioGroup) inflate(this, R.layout.choose_one_or_last, null);
        final RadioButton up = (RadioButton) radioGroup.getChildAt(0);
        final RadioButton down = (RadioButton) radioGroup.getChildAt(1);

        up.setText(R.string.up_of_stroke);
        down.setText(R.string.down_of_stroke);

        builder.setView(radioGroup);
        builder.setMessage(R.string.PASTE);
        builder.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int addPosition = position;
                if (down.isChecked())
                    addPosition++;
                listDanniePeriod.add(addPosition, copyDannie);
                // заносим массив в базу
                ZarplataHelper.updateDBTable(new ZarplataHelper(ZarplataActivity.this).getWritableDatabase(), nameTable, listDanniePeriod);
                // нужно обновить их иды
                updateMassivRecycler();

                Intent intent = StaticUtil.getIntentOfShowEditWin(ZarplataActivity.this, listDanniePeriod.get(addPosition), 1);
                intent.putExtra(NAME_TABLE, nameTable);
                startActivityForResult(intent, REQ_PASTE);
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private int getPosition(long id) {
        updateMassivRecycler();
        int position = -1;
        for (int i = 0; i < listDanniePeriod.size(); i++) {
            Dannie d = listDanniePeriod.get(i);
            if (d.getID() == id) {
                position = i;
                break;
            }
        }
        return position;
    }

    private void updateSummaAll() {

        float isummaAvans = 0;
        float isummaAllmoney = 0;
        float isummaMoneyKVidache = 0;

        for (Dannie d : listDanniePeriod) {
            if (d.type == 1) continue;
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

        setSimvolValuti();

//        summaAllmoney.setText(String.format("%s р", ALLMONEY));
//        summaAvans.setText(String.format("%s р", ALLAVANS));
//        ostatok.setText(String.format("%s р", ALLKVIDACHE));
        // изменение размер шрифта
//        if (summaAllmoney.getText().length() > 12) {
//            summaAllmoney.setTextSize(16);
//            if (summaAllmoney.getText().length() > 14) {
//                summaAllmoney.setTextSize(15);
//            }
//        } else summaAllmoney.setTextSize(18);
//
//        if (summaAvans.getText().toString().length() > 12) {
//            summaAvans.setTextSize(16);
//        } else summaAvans.setTextSize(18);
//
//        if (ostatok.getText().length() > 12) {
//            ostatok.setTextSize(16);
//            if (ostatok.getText().length() > 14) {
//                ostatok.setTextSize(15);
//            }
//        } else ostatok.setTextSize(18);
        ////////
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        Log.d("life", "onActivityResult");

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_NEW_ZAPIS) {
                add(data, true, -1);
            }
            if (requestCode == REQ_EDIT || requestCode == REQ_PASTE) {
                int key = data.getIntExtra(NewZapis.KEY_ID, 0);
                add(data, false, key);
                CopyData.getCopy().removeCopy();
            }
        }
    }

    private void add(Intent data, boolean trueAdd_falseSet, int KEY_ID) {

        SharedPreferences sharedPreferences = getSharedPreferences(SchemaDB.Table.OLD_SIZE_ITEM, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        //добавление данных одной записи в бд
        database = new ZarplataHelper(this).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SchemaDB.Table.Cols.SUMMA, data.getStringExtra(NewZapis.KEY_SUMMA));
        values.put(SchemaDB.Table.Cols.AVANS, data.getStringExtra(NewZapis.KEY_AVANS));
        values.put(SchemaDB.Table.Cols.DATE_LONG, data.getLongExtra(NewZapis.KEY_DATE, 0));
        values.put(SchemaDB.Table.Cols.COMENT, data.getStringExtra(NewZapis.KEY_COMENT));


        if (trueAdd_falseSet) {

            database.insert(nameTable, null, values);

            Cursor cursor = database.query(nameTable, null, null, null, null, null, null);
            if (cursor.moveToFirst()) {
                cursor.moveToLast();
                KEY_ID = cursor.getInt(cursor.getColumnIndex(SchemaDB.Table.Cols.ID));
                this.KEY_ID = KEY_ID;
            }
            cursor.close();


            editor.putInt(SchemaDB.Table.COUNTER, KEY_ID);
            editor.apply();
//            updateMassivRecycler();
//            sort(sort, sortmetod);
//            getRecycler(KEY_ID);
//            updateSummaAll();

        } else {

            database.update(nameTable, values, SchemaDB.Table.Cols.ID + " = ?", new String[]{Integer.toString(KEY_ID)});
            //            handler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
            this.KEY_ID = KEY_ID;
            editor.putInt(SchemaDB.Table.COUNTER, KEY_ID);
            editor.apply();

//                    updateMassivRecycler();
//                    sort(sort, sortmetod);
//                    getRecycler(finalKEY_ID);
//                    updateSummaAll();
//                }
//            }, 100);
        }
        database.close();
        updateDateModify();
    }

    private void updateMassivRecycler() {
        database = new ZarplataHelper(this).getWritableDatabase();
        Cursor cursor = database.query(nameTable,
                null, null, null, null, null, null);

        listDannie = new ArrayList<>();
        if (cursor.moveToFirst()) {
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

                dannie.setDateMl(Long.parseLong(dateMl));
                dannie.setComent(coment);

                listDannie.add(dannie);
            } while (cursor.moveToNext());
        }
        listDanniePeriod = new ArrayList<>(listDannie);
        cursor.close();
        database.close();

        if (mMenu != null) {
            MenuItem item = mMenu.findItem(R.id.id_period);
            if (listDanniePeriod.size() == 0) {
                item.setEnabled(false);
            } else item.setEnabled(true);
        }
    } //отдельно заполняет массив

    private void getRecycler(final int positionRecyclerItem) {
        LinearLayout infoLayout = findViewById(R.id.isNullItemInfoZ);

        if (listDanniePeriod.size() == 0 && !isArchive) {
            infoLayout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            infoLayout.getChildAt(0).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    newZapis();
                }
            });
            return;
        } else {
            infoLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
        AdapterStroke adapter = new AdapterStroke(this, listDanniePeriod, handler, nameTable, isArchive);
        adapter.setTitleSettinMode(!isBack);
        recyclerView.setAdapter(adapter);

        for (int i = 0; i < listDanniePeriod.size(); i++) {
            if (listDanniePeriod.get(i).getID() == positionRecyclerItem) {
                recyclerView.scrollToPosition(i);
                break;
            }
        }
    } // отдельно создает рекуклер

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_zarplata, menu);
        if (isArchive) {
            menu.removeItem(R.id.id_dobavit);
            menu.removeItem(R.id.id_set_arhive);
            menu.removeItem(R.id.id_delete_all);
            menu.removeItem(R.id.id_setting_item_menu_zarplataactivity);
            menu.removeItem(R.id.id_set_name_titles);
        } else {
            mMenu = menu;
        }
        if (listDanniePeriod.size() == 0) {
            menu.findItem(R.id.id_period).setEnabled(false);
        }
        return true;
    }

//    private int convertDpToPixels(float dp, Context context) {
//        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
//    }

    private void sortRadioButtonListener(ViewGroup viewGroup) {

        RadioGroup radioGroup1 = viewGroup.findViewById(R.id.id_radiogroup_sort1);
        //включаем нужную кнопку
        switch (sort) {
            case 0:
                radioGroup1.check(R.id.radioButton_date);
                break;
            case 1:
                radioGroup1.check(R.id.radioButton_summa);
                break;
            case 2:
                radioGroup1.check(R.id.radioButton_rashod);
                break;
            default:
                radioGroup1.check(R.id.radioButton_default);
        }
        if (sort == 0) {
            dayCheckBox.setEnabled(true);
            periodCheckBox.setEnabled(true);
            if (allSummaOfSortFromDate == 1) {
                dayCheckBox.setChecked(true);
            } else if (allSummaOfSortFromDate == 2) {
                periodCheckBox.setChecked(true);
            } else if (allSummaOfSortFromDate == 3) {
                dayCheckBox.setChecked(true);
                periodCheckBox.setChecked(true);
            }
        } else {
            dayCheckBox.setEnabled(false);
            periodCheckBox.setEnabled(false);
        }
        radioGroup1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.radioButton_default) {
                    sort = -1;
                }
                if (i == R.id.radioButton_date) {
                    sort = 0;
                }
                if (i == R.id.radioButton_summa) {
                    sort = 1;
                }
                if (i == R.id.radioButton_rashod) {
                    sort = 2;
                }
                if (sort == 0) {
                    dayCheckBox.setEnabled(true);
                    periodCheckBox.setEnabled(true);
                } else {
                    dayCheckBox.setEnabled(false);
                    periodCheckBox.setEnabled(false);
                    allSummaOfSortFromDate = 0;
                }
                setPreferenceForSort(sort, sortmetod);
                sort(sort, sortmetod);
                getRecycler(0);

            }
        });
        //////////////////////////
        RadioGroup radioGroup2 = viewGroup.findViewById(R.id.id_radiogroup_sort2);
        switch (sortmetod) {
            case 0:
                radioGroup2.check(R.id.radioButtonUp);
                break;
            case 1:
                radioGroup2.check(R.id.radioButtonDown);
                break;
        }
        radioGroup2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.radioButtonUp:
                        sortmetod = 0;
                        setPreferenceForSort(sort, sortmetod);
                        break;
                    case R.id.radioButtonDown:
                        sortmetod = 1;
                        setPreferenceForSort(sort, sortmetod);
                        break;
                }
                sort(sort, sortmetod);
                getRecycler(0);
            }
        });
    }

    private void sort(int sortBy, int sortMethod) {
        for (int i = 0; i < listDanniePeriod.size(); i++) {
            Dannie d = listDanniePeriod.get(i);
            if (d.type == 1) {
                listDanniePeriod.remove(i);
                i--;
            }
        }
        if (sortMethod == 1) {
            if (sortBy == 0) {
                for (int i = 0; i < listDanniePeriod.size(); i++) {
                    for (int j = i; j < listDanniePeriod.size(); j++) {
                        if (listDanniePeriod.get(j).getDateMl() > listDanniePeriod.get(i).getDateMl()) {
                            Dannie temp = listDanniePeriod.get(i);
                            listDanniePeriod.set(i, listDanniePeriod.get(j));
                            listDanniePeriod.set(j, temp);
                        }
                    }
                }
            } else if (sortBy == 1) {
                for (int i = 0; i < listDanniePeriod.size(); i++) {
                    for (int j = i; j < listDanniePeriod.size(); j++) {
                        float summaJ = 0;
                        float summaI = 0;
                        if (listDanniePeriod.get(j).getSumma().length() > 0)
                            summaJ = Float.parseFloat(listDanniePeriod.get(j).getSumma());
                        if (listDanniePeriod.get(i).getSumma().length() > 0)
                            summaI = Float.parseFloat(listDanniePeriod.get(i).getSumma());

                        if (summaJ > summaI) {
                            Dannie temp = listDanniePeriod.get(i);
                            listDanniePeriod.set(i, listDanniePeriod.get(j));
                            listDanniePeriod.set(j, temp);
                        }
                    }
                }
            } else if (sortBy == 2) {
                for (int i = 0; i < listDanniePeriod.size(); i++) {
                    for (int j = i; j < listDanniePeriod.size(); j++) {
                        float rashodJ = 0;
                        float rashodI = 0;
                        if (listDanniePeriod.get(j).getAvans().length() > 0)
                            rashodJ = Float.parseFloat(listDanniePeriod.get(j).getAvans());
                        if (listDanniePeriod.get(i).getAvans().length() > 0)
                            rashodI = Float.parseFloat(listDanniePeriod.get(i).getAvans());

                        if (rashodJ > rashodI) {
                            Dannie temp = listDanniePeriod.get(i);
                            listDanniePeriod.set(i, listDanniePeriod.get(j));
                            listDanniePeriod.set(j, temp);
                        }

                    }
                }
            } else {
//                updateMassivRecycler(); // можетБытьБаг
                for (int i = 0; i < listDanniePeriod.size(); i++) {
                    for (int j = i; j < listDanniePeriod.size(); j++) {
                        Dannie dannie = listDanniePeriod.get(i);
                        Dannie temp = listDanniePeriod.get(j);
                        if (temp.getID() > dannie.getID()) {
                            listDanniePeriod.set(j, dannie);
                            listDanniePeriod.set(i, temp);
                        }
                    }
                }
            }
        } else {
            if (sortBy == 0) {
                for (int i = 0; i < listDanniePeriod.size(); i++) {
                    for (int j = i; j < listDanniePeriod.size(); j++) {
                        if (listDanniePeriod.get(j).getDateMl() < listDanniePeriod.get(i).getDateMl()) {
                            Dannie temp = listDanniePeriod.get(i);
                            listDanniePeriod.set(i, listDanniePeriod.get(j));
                            listDanniePeriod.set(j, temp);
                        }
                    }
                }
            } else if (sortBy == 1) {
                for (int i = 0; i < listDanniePeriod.size(); i++) {
                    for (int j = i; j < listDanniePeriod.size(); j++) {
                        float summaJ = 0;
                        float summaI = 0;
                        if (listDanniePeriod.get(j).getSumma().length() > 0)
                            summaJ = Float.parseFloat(listDanniePeriod.get(j).getSumma());
                        if (listDanniePeriod.get(i).getSumma().length() > 0)
                            summaI = Float.parseFloat(listDanniePeriod.get(i).getSumma());

                        if (summaJ < summaI) {
                            Dannie temp = listDanniePeriod.get(i);
                            listDanniePeriod.set(i, listDanniePeriod.get(j));
                            listDanniePeriod.set(j, temp);
                        }
                    }
                }
            } else if (sortBy == 2) {
                for (int i = 0; i < listDanniePeriod.size(); i++) {
                    for (int j = i; j < listDanniePeriod.size(); j++) {
                        float rashodJ = 0;
                        float rashodI = 0;
                        if (listDanniePeriod.get(j).getAvans().length() > 0)
                            rashodJ = Float.parseFloat(listDanniePeriod.get(j).getAvans());
                        if (listDanniePeriod.get(i).getAvans().length() > 0)
                            rashodI = Float.parseFloat(listDanniePeriod.get(i).getAvans());

                        if (rashodJ < rashodI) {
                            Dannie temp = listDanniePeriod.get(i);
                            listDanniePeriod.set(i, listDanniePeriod.get(j));
                            listDanniePeriod.set(j, temp);
                        }

                    }
                }
            } else {
//                updateMassivRecycler();
                for (int i = 0; i < listDanniePeriod.size(); i++) {
                    for (int j = i; j < listDanniePeriod.size(); j++) {
                        Dannie dannie = listDanniePeriod.get(i);
                        Dannie temp = listDanniePeriod.get(j);
                        if (temp.getID() < dannie.getID()) {
                            listDanniePeriod.set(j, dannie);
                            listDanniePeriod.set(i, temp);
                        }
                    }
                }
            }
        }
//        if (!isPeriodShow) // не надо обновлять нумерацию если показано окно периода, надо знать под каким номером та или иная запись)
        for (int i = 0; i < listDanniePeriod.size(); i++) {
            listDanniePeriod.get(i).position = i + 1;
        }

        if (sortBy == 0 && allSummaOfSortFromDate > 0) {
            int date = 0;
            float summaDay = 0;
            float avansDay = 0;
            float summaPeriod = 0;
            float avansPeriod = 0;
            if (listDanniePeriod.size() > 1)
                for (int i = 0; i < listDanniePeriod.size(); i++) {
                    Dannie dannie = listDanniePeriod.get(i);
                    if (dannie.type == 1)
                        continue;

                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(dannie.getDateMl());

                    if (i == 0) {
                        date = calendar.get(Calendar.DAY_OF_MONTH);
                    }
                    int tempDate = calendar.get(Calendar.DAY_OF_MONTH);
                    if (date == tempDate) {
                        if (dannie.getSumma().length() > 0) {
                            float summa = Float.parseFloat(dannie.getSumma());
                            summaDay += summa;
                            summaPeriod += summa;
                        }
                        if (dannie.getAvans().length() > 0) {
                            float avans = Float.parseFloat(dannie.getAvans());
                            avansDay += avans;
                            avansPeriod += avans;
                        }
                        if (i == listDanniePeriod.size() - 1) {
                            Dannie dannie1 = new Dannie();
                            dannie1.type = 1;
                            setPeriodAllValues(summaDay, avansDay, summaPeriod, avansPeriod, dannie1);
                            listDanniePeriod.add(dannie1);
                            return;
                        }
                    } else {
                        Dannie dannie1 = new Dannie();
                        dannie1.type = 1;

                        setPeriodAllValues(summaDay, avansDay, summaPeriod, avansPeriod, dannie1);

                        listDanniePeriod.add(i, dannie1);
                        date = calendar.get(Calendar.DAY_OF_MONTH);

                        if (allSummaOfSortFromDate == 1 || allSummaOfSortFromDate == 3) {
                            summaDay = 0;
                            avansDay = 0;
                        }

                    }

                }
        }
    }

    private void setPeriodAllValues(float summaDay, float avansDay, float summaPeriod, float avansPeriod, Dannie dannie1) {
        String summa = "";
        String avans = "";
        String ostatokPeriod = "";
        if (allSummaOfSortFromDate == 1) {
            summa = numberFormat.format(summaDay);
            avans = numberFormat.format(avansDay);
            ostatokPeriod = numberFormat.format(summaDay - avansDay);
        } else if (allSummaOfSortFromDate == 2) {
            summa = numberFormat.format(summaPeriod);
            avans = numberFormat.format(avansPeriod);
            ostatokPeriod = numberFormat.format(summaPeriod - avansPeriod);
        } else if (allSummaOfSortFromDate == 3) {
            summa = numberFormat.format(summaDay) + " / " + numberFormat.format(summaPeriod);
            avans = numberFormat.format(avansDay) + " / " + numberFormat.format(avansPeriod);
            ostatokPeriod = numberFormat.format(summaDay - avansDay) + " / " + numberFormat.format(summaPeriod - avansPeriod);
        }

        dannie1.setSumma(summa);
        dannie1.setAvans(avans);
        dannie1.setOstatok(ostatokPeriod);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.id_send) {
            if (listDanniePeriod.size() == 0) {
                Toast.makeText(this, R.string.is_list_empty, Toast.LENGTH_SHORT).show();
                return true;
            }
            int padding = StaticUtil.convertDpToPixels(3, this);
            File pic = new File(getCacheDir() + "/" + new Date().getTime() + ".png");

            Bitmap bitmapRecycler = SendHelper.getScreenshotFromRecyclerView(recyclerView);
            Bitmap bitmapTitles = SendHelper.getBitmapFromView(findViewById(R.id.id_info_tablo_for_zarplata_layout));
            Bitmap bitmapAllInfo = SendHelper.getBitmapFromView(findViewById(R.id.id_card_for_all_info));


            int width = bitmapTitles.getWidth();
            Bitmap bitmapNameCard = SendHelper.getNameCardBitmap(width, StaticUtil.convertDpToPixels(40, this), nameCard, this);
            int height = bitmapNameCard.getHeight()
                    + (int) (bitmapRecycler.getHeight() + (padding * 2.5)
                    + bitmapAllInfo.getHeight()
                    + bitmapTitles.getHeight());
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmap.eraseColor(Color.WHITE);
            Canvas canvas = new Canvas(bitmap);
            canvas.drawBitmap(bitmapNameCard, 0, 0, new Paint());
            canvas.drawBitmap(bitmapTitles, 0, bitmapNameCard.getHeight(), new Paint());
            canvas.drawBitmap(bitmapRecycler, padding, bitmapTitles.getHeight()
                    + bitmapNameCard.getHeight()
                    + padding, new Paint());
            canvas.drawBitmap(bitmapAllInfo, 0, (float) (bitmapTitles.getHeight()
                    + bitmapRecycler.getHeight()
                    + bitmapNameCard.getHeight()
                    + (padding * 2.5)), new Paint());
            try {
                FileOutputStream fos = new FileOutputStream(pic);

//                Bitmap bitmapRecycler = SendHelper.getBitmapFromView(findViewById(R.id.id_zarplata_general_linear_layout));
                bitmap.compress(Bitmap.CompressFormat.JPEG, 97, fos);


            } catch (Exception e) {
                e.printStackTrace();
            }
            // открываем диалог
//            Intent intent = new Intent(Intent.ACTION_SEND);
//            intent.setType("image/*");
//            Uri uri = FileProvider.getUriForFile(this, "com.a4165816.press.earning.provider", pic);
//            intent.putExtra(Intent.EXTRA_STREAM, uri);
//            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//            startActivity(intent);
//
            ScreenDialog screenDialog = ScreenDialog.createScreenDialog(pic.getPath());
            screenDialog.show(getSupportFragmentManager(), "screenDialog");


        }

        if (item.getItemId() == R.id.id_dobavit) {

            newZapis();

        } else if (item.getItemId() == R.id.id_sort_item) {
            sortRadioButtonListener(sortLayout);
            window.setContentView(sortLayout);
            window.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
            window.setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawable(new BitmapDrawable());
            window.setFocusable(true);
            window.setOutsideTouchable(true);
            int statusBarHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics());
            window.showAtLocation(toolbar, Gravity.END | Gravity.TOP, 0, toolbar.getHeight() + statusBarHeight);
        } else if (item.getItemId() == R.id.id_set_arhive) {
//            if (listDanniePeriod.size() != 0) {
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setMessage(R.string.want_to_set_arhive)
                    .setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            onSetArchive();
                        }
                    })
                    .setNegativeButton(R.string.NO, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    })
                    .show();
//            } else
//                Toast.makeText(this, R.string.is_no_record, Toast.LENGTH_SHORT).show();
        } else if (item.getItemId() == R.id.id_delete_all) {
            if (listDannie.size() == 0) {
                Toast.makeText(this, R.string.is_list_empty, Toast.LENGTH_SHORT).show();
                return true;
            }
            new AlertDialog.Builder(this)
                    .setMessage(R.string.want_to_delete_all_records)
                    .setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {


                            database = new ZarplataHelper(getApplication()).getWritableDatabase();
                            database.delete(nameTable, null, null);
                            database.close();

                            updateMassivRecycler();
                            getRecycler(0);
                            updateSummaAll();

                            updateDateModify();
                        }
                    })
                    .setNegativeButton(R.string.NO, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    })
                    .show();
            return true;
        } else if (item.getItemId() == R.id.id_setting_item_menu_zarplataactivity) {
            Intent intent1 = new Intent(this, Setting.class);
            startActivity(intent1);
        } else if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        } else if (item.getItemId() == R.id.id_set_name_titles) {
            isBack = false;
            showTitleSettingMenu();
            animTitles(true);
            clickTitles(true);
            getRecycler(-1);
//            listDanniePeriod.clear();
//            updateSummaAll();
        } else if (item.getItemId() == R.id.id_galochka) {
            onBackPressed();
        } else if (item.getItemId() == R.id.id_period) {
            showPeriodWindow();
        }
        return true;
    }

    private void showPeriodWindow() {

        if (isPeriodShow) {
            first = 0;
            last = 0;
            updateAll(-1);
            periodLayout.setVisibility(View.GONE);
            isPeriodShow = false;
        } else {
            updatePeriod();
            isPeriodShow = true;
            periodLayout.setVisibility(View.VISIBLE);
        }

    }

    private void updatePeriod() {
        // метод показа даты чтоб потом выбрать как ее показать

        // список всех дат в записях зы.
        final ArrayList<Long> dateArrayTemp = new ArrayList<>();

        // записываем все даты
        for (int i = 0; i < listDannie.size(); i++) { // нам нужны все даты поэтому тут лист данные
            Dannie dannie = listDannie.get(i);
            dateArrayTemp.add(dannie.getDateMl());
        }
        Collections.sort(dateArrayTemp); //  сортируем потому что надо показать от начала до конца а не так как добавил пользователь

        // исключая повторяющиеся
        // тут мы выводим все даты в том виде в котором настроил пользователь
        String oldDate = "";// для сравнения предыдущей записи
        Dannie dannie = new Dannie(); // для присвоения даты и записи ее в массив
        ArrayList<String> dateString = new ArrayList<>(); // список конечных дат
        ArrayList<MonthOfPeriod> months = new ArrayList<>();
        final ArrayList<Long> dateArray = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        for (int i = 0; i < dateArrayTemp.size(); i++) {

            Long dateMl = dateArrayTemp.get(i);
            // присваиваем дату
            dannie.setDateMl(dateMl);
            String date = dannie.getDate(0);
            // сравнение предыдущей записи и настоящей
            if (date.equals(oldDate)) {
                continue;
            }
            oldDate = date;
            // заносим дату в массив
            String dateStr = dannie.getDate(4);
            dateString.add(dateStr);
            dateArray.add(dateMl);
            // суета с месяцами
            calendar.setTimeInMillis(dateMl);
            // добавляем сначала все а пготом будем отсекать дубликаты
            MonthOfPeriod monthOfPeriod = new MonthOfPeriod(calendar.get(Calendar.MONTH));
            monthOfPeriod.year = calendar.get(Calendar.YEAR);
            monthOfPeriod.firstDate = dateMl;
            monthOfPeriod.lastDate = dateMl;
            months.add(monthOfPeriod);
        }

        // удаляем дубликаты месяцев
        for (int i = 1; i < months.size(); i++) {
            MonthOfPeriod monthOfPeriod = months.get(i - 1);
            MonthOfPeriod monthOfPeriod1 = months.get(i);

            int month = monthOfPeriod.month;
            int month1 = monthOfPeriod1.month;

            int year = monthOfPeriod.year;
            int year1 = monthOfPeriod1.year;

            if (month1 == month && year == year1) {
                MonthOfPeriod remove = months.remove(i);
                monthOfPeriod.lastDate = remove.firstDate;
                i--;
            }
        }
        // добавление месяцев
        monthLayout.removeAllViews();
        int oldYear = 0;
        for (final MonthOfPeriod month : months) {
            TextView textView = getTextViewToPeriod(monthNames[month.month], 36, 16);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    setPeriod(month.firstDate, month.lastDate);
                    int f = 0;
                    int l = 0;

                    for (int i = 0; i < dateArray.size(); i++) {
                        if (month.firstDate == dateArray.get(i)) {
                            f = i;
                        }
                        if (month.lastDate == dateArray.get(i)) {
                            l = i;
                        }
                    }
                    firstDateOfPeriodSpinner.setSelection(f);
                    lastDateOfPeriodSpinner.setSelection(l);
                    getRecycler(-1);//
                    updateSummaAll();

                    view.startAnimation(AnimationUtils.loadAnimation(ZarplataActivity.this, android.R.anim.fade_in));
                }
            });
            // добавление года для месяцев
            int year = month.year;
            if (oldYear != year) {
                TextView yearText = getTextViewToPeriod(year + ":", 4, 14);
                yearText.setTextColor(ContextCompat.getColor(this, R.color.color_normal));
                monthLayout.addView(yearText);
            }
            oldYear = year;
            monthLayout.addView(textView);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.item_day_period_spinner, R.id.day);
        adapter.addAll(dateString);

        firstDateOfPeriodSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int la = lastDateOfPeriodSpinner.getSelectedItemPosition();

                if (i > la) {
                    firstDateOfPeriodSpinner.setSelection(la);
                    return;
                }
                first = dateArray.get(i);
                last = dateArray.get(la);
                setPeriod();
                sort(sort, sortmetod);
                getRecycler(-1);
                updateSummaAll();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        lastDateOfPeriodSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int fi = firstDateOfPeriodSpinner.getSelectedItemPosition();

                if (i < fi) {
                    lastDateOfPeriodSpinner.setSelection(fi);
                    return;
                }
                first = dateArray.get(fi);
                last = dateArray.get(i);
                setPeriod();
                sort(sort, sortmetod);
                getRecycler(-1);
                updateSummaAll();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        firstDateOfPeriodSpinner.setAdapter(adapter);
//        firstDateOfPeriodSpinner.setSelection(0, true);
        lastDateOfPeriodSpinner.setAdapter(adapter);
        lastDateOfPeriodSpinner.setSelection(dateArray.size() - 1);
    }

    private TextView getTextViewToPeriod(String monthName, int paddingR, int textSize) {
        TextView textView = new TextView(this);
        textView.setTextSize(textSize);
        textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
        textView.setGravity(Gravity.BOTTOM);
        textView.setPadding(16, 0, paddingR, 0);
//                textView.setTextColor(Color.WHITE);
        textView.setText(monthName);
        return textView;
    }

    private void setPeriod() {
        listDanniePeriod.clear();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(first);
        calendar.set(calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH),
                0,
                0,
                0);
        first = calendar.getTimeInMillis();

        calendar.setTimeInMillis(last);
        calendar.set(calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH),
                23,
                59,
                59);
        last = calendar.getTimeInMillis();

        for (int i = 0; i < listDannie.size(); i++) {
            Dannie d = listDannie.get(i);
            if (d.getDateMl() <= last && d.getDateMl() >= first) {
                listDanniePeriod.add(d);
            }
        }

        for (int i = 0; i < monthLayout.getChildCount(); i++) {
            TextView monthTextView = (TextView) monthLayout.getChildAt(i);
            boolean isMonth = false;
            for (String month : monthNames) {
                if (month.equals(monthTextView.getText().toString())) {
                    isMonth = true;
                    break;
                }
            }
            if (!isMonth)
                continue;
            monthTextView.setTextColor(ContextCompat.getColor(this, R.color.dark_text));

            String name = monthTextView.getText().toString();
            for (Dannie d : listDanniePeriod) {
                calendar.setTimeInMillis(d.getDateMl());
                int monthNumber = calendar.get(Calendar.MONTH);
                if (name.equals(monthNames[monthNumber])) {
                    monthTextView.setTextColor(ContextCompat.getColor(this, R.color.greenButtonLight));
                    break;
                }
            }

        }

    }

    private void clickTitles(boolean b) {
        View.OnClickListener click = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = "";
                int vId = v.getId();
                if (vId == R.id.zarplata_title_number) {
                    id = KEY_ID_TITLE_NAME;
                } else if (vId == R.id.zarplata_title_summa) {
                    id = KEY_SUMMA_TITLE_NAME;
                } else if (vId == R.id.zarplata_title_avans) {
                    id = KEY_AVANS_TITLE_NAME;
                } else if (vId == R.id.zarplata_title_date) {
                    id = KEY_DATE_TITLE_NAME;
                } else if (vId == R.id.zarplata_title_zametka) {
                    id = KEY_ZAMETKA_TITLE_NAME;
                } else if (vId == R.id.card_title_vsego_z) {
                    id = KEY_SUMMA_CARD_TITLE_NAME;
                } else if (vId == R.id.card_title_avans_z) {
                    id = KEY_AVANS_CARD_TITLE_NAME;
                } else if (vId == R.id.card_title_ostatok_z) {
                    id = KEY_OSTATOK_TITLE_NAME;
                }
                ColumnName.TitleNameChange titleNameChange = new ColumnName.TitleNameChange((TextView) v, id);
                titleNameChange.setNameTable(nameTable);
                titleNameChange.invoke(ZarplataActivity.this);
            }
        };
        if (b) {
            idTitle.setOnClickListener(click);
            summaTitle.setOnClickListener(click);
            avansTitle.setOnClickListener(click);
            noteTitle.setOnClickListener(click);
            dateTitle.setOnClickListener(click);

            allSummaTitle.setOnClickListener(click);
            allAvansTitle.setOnClickListener(click);
            ostatotTitle.setOnClickListener(click);
        } else {
            idTitle.setOnClickListener(null);
            summaTitle.setOnClickListener(null);
            avansTitle.setOnClickListener(null);
            noteTitle.setOnClickListener(null);
            dateTitle.setOnClickListener(null);

            allSummaTitle.setOnClickListener(null);
            allAvansTitle.setOnClickListener(null);
            ostatotTitle.setOnClickListener(null);
        }
    }

    private void showTitleSettingMenu() {
        mMenu.clear();
        getMenuInflater().inflate(R.menu.title_setting_menu, mMenu);
    }

    private void animTitles(boolean isAnimate) {

        if (isAnimate) {
            idTitle.startAnimation(anim);
            summaTitle.startAnimation(anim);
            avansTitle.startAnimation(anim);
            noteTitle.startAnimation(anim);
            dateTitle.startAnimation(anim);
//
            allSummaTitle.startAnimation(animToAll);
            allAvansTitle.startAnimation(animToAll);
            ostatotTitle.startAnimation(animToAll);
        } else {
            anim.cancel();
            animToAll.cancel();
        }
    }

    private void updateDateModify() {
        dateModifyTable = String.valueOf(Calendar.getInstance().getTimeInMillis());
        ZarplataHelper.updateDateModify(ZarplataActivity.this, nameTable, dateModifyTable);
    }

    private void newZapis() {
        Intent intent = new Intent(this, NewZapis.class);
        intent.putExtra(NAME_TABLE, nameTable);
//            intent.putExtra("KEY_ID", listDanniePeriod.size());
        intent.putExtra("edit", false);
        startActivityForResult(intent, REQUEST_NEW_ZAPIS);
    }

    private void onSetArchive() {
        dateModifyTable = String.valueOf(Calendar.getInstance().getTimeInMillis());
        database = new ZarplataHelper(this).getWritableDatabase();
        ContentValues value = new ContentValues();
        value.put(SchemaDB.Table.IS_ARCHIVE, 1);
        value.put(SchemaDB.Table.Cols.DATE_MODIFY_TABLE, dateModifyTable);
        database.update(SchemaDB.Table.NAME_CARD, value, SchemaDB.Table.NAME_TABLE_DATA + " = ?", new String[]{nameTable});
        database.close();

        finish();
    }

    private void setPreferenceForSort(int valueSort, int valueMetod) {

        SharedPreferences.Editor editor = preferences.edit();
        boolean radiogroup = preferences.contains(SchemaDB.Name.Item.RADIO_GROUP);
        boolean radiometod = preferences.contains(SchemaDB.Name.Item.RADIO_METOD);
        if (!radiogroup && !radiometod) {
            editor.putInt(SchemaDB.Name.Item.RADIO_GROUP, -1); // значит по умолчанию
            editor.putInt(SchemaDB.Name.Item.RADIO_METOD, 0); // значит по возрастанию
            editor.putInt(SchemaDB.Name.Item.DATE_SORT_SWITCH, 0); // значит по возрастанию
        } else {
            editor.putInt(SchemaDB.Name.Item.RADIO_GROUP, valueSort);
            editor.putInt(SchemaDB.Name.Item.RADIO_METOD, valueMetod);
            editor.putInt(SchemaDB.Name.Item.DATE_SORT_SWITCH, allSummaOfSortFromDate); // значит по возрастанию
        }
        editor.apply();

    }

    private void getPreferenceForSort() {

        boolean radiogroup = preferences.contains(SchemaDB.Name.Item.RADIO_GROUP);
        boolean radiometod = preferences.contains(SchemaDB.Name.Item.RADIO_METOD);
        if (radiogroup && radiometod) {
            sort = preferences.getInt(SchemaDB.Name.Item.RADIO_GROUP, -1);
            sortmetod = preferences.getInt(SchemaDB.Name.Item.RADIO_METOD, 0);
            allSummaOfSortFromDate = preferences.getInt(SchemaDB.Name.Item.DATE_SORT_SWITCH, 0);
        }
    }

    private void switcher() {
        dayCheckBox = sortLayout.findViewById(R.id.day_checkBox);
        periodCheckBox = sortLayout.findViewById(R.id.period_checkBox);
        CompoundButton.OnCheckedChangeListener changeListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                allSummaOfSortFromDate = 0;
                if (dayCheckBox.isChecked())
                    allSummaOfSortFromDate = 1;
                if (periodCheckBox.isChecked())
                    allSummaOfSortFromDate = 2;
                if (dayCheckBox.isChecked() && periodCheckBox.isChecked())
                    allSummaOfSortFromDate = 3;

                setPreferenceForSort(sort, sortmetod);
                sort(sort, sortmetod);
                getRecycler(0);
            }
        };
        dayCheckBox.setOnCheckedChangeListener(changeListener);
        periodCheckBox.setOnCheckedChangeListener(changeListener);

        //флажок для сохранения сортировки
        Switch sortSaveSwitch = sortLayout.findViewById(R.id.id_switch_save_sort);

        if (preferences.contains(SchemaDB.Name.Item.SAVESWITCHSORT)) {
            //визуальная часть вкл или выкл
            boolean checked = preferences.getBoolean(SchemaDB.Name.Item.SAVESWITCHSORT, false);
            sortSaveSwitch.setChecked(checked);
            if (checked) {
                getPreferenceForSort();//
            }

        }
        sortSaveSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean(SchemaDB.Name.Item.SAVESWITCHSORT, b);
                editor.apply();
            }
        });
    }

    private void setSimvolValuti() {
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        String rub = getString(R.string.simvol_rub_string);

        if (pref.getBoolean(rub, false)) {
            String simvolKey = getString(R.string.chek_simvol);
            String simvol = pref.getString(simvolKey, "ք Рубль");

            summaAllmoney.setText(String.format("%s " + simvol.substring(0, 1), ALLMONEY));
            summaAvans.setText(String.format("%s " + simvol.substring(0, 1), ALLAVANS));
            ostatok.setText(String.format("%s " + simvol.substring(0, 1), ALLKVIDACHE));
        } else {

            summaAllmoney.setText(ALLMONEY);
            summaAvans.setText(ALLAVANS);
            ostatok.setText(ALLKVIDACHE);

        }

    }

    private int setFontSize(SharedPreferences pref, int textView) {
        int size = 16;
        switch (textView) {
            case 0:
                String keyZarplata = getString(R.string.zarplata);
                size = Integer.parseInt(pref.getString(keyZarplata, "16"));
                break;
            case 1:
                String keyAvans = getString(R.string.avans_key_obshee);
                size = Integer.parseInt(pref.getString(keyAvans, "16"));
                break;
            case 2:
                String keyOstatok = getString(R.string.Ostatok);
                size = Integer.parseInt(pref.getString(keyOstatok, "16"));
                break;
        }
        return size;
    }

    private int setFontStyle(SharedPreferences pref, int textView) {
        int style = 0;
        switch (textView) {
            case 0:
                String keyZarplata = getString(R.string.zarplataKeyStyle);
                style = Integer.parseInt(pref.getString(keyZarplata, "0"));
                break;
            case 1:
                String keyAvans = getString(R.string.avans_key_obsheeKeyStyle);
                style = Integer.parseInt(pref.getString(keyAvans, "0"));
                break;
            case 2:
                String keyOstatok = getString(R.string.OstatokKeyStyle);
                style = Integer.parseInt(pref.getString(keyOstatok, "0"));
                break;
        }
        return style;
    }

    private void settingFont() {
        summaAllmoney.setTextSize(setFontSize(pref, 0));
        summaAvans.setTextSize(setFontSize(pref, 1));
        ostatok.setTextSize(setFontSize(pref, 2));

        summaAllmoney.setTypeface(null, setFontStyle(pref, 0));
        summaAvans.setTypeface(null, setFontStyle(pref, 1));
        ostatok.setTypeface(null, setFontStyle(pref, 2));
    }

    @Override
    protected void onStop() {
        super.onStop();
        window.dismiss();
//        Log.d("life", "onStop");

        preferences = getSharedPreferences(SchemaDB.Table.OLD_SIZE_ITEM, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(SchemaDB.Table.COUNTER, -2);
        editor.apply();

    }

    @Override
    protected void onResume() {
        super.onResume();
//        if (isPeriodShow) {
//            periodLayout.setVisibility(View.GONE);
//            isPeriodShow = false;
////            updateAll(-1);
//        }

        setTitleName();
        settingFont();
        setColorAll();

        handler.post(new Runnable() {
            @Override
            public void run() {
                updateAll(KEY_ID);
            }
        });

    }

    private void updateAll(int key_id) {
        updateMassivRecycler();
        // если период открыт то установить тот период который был после сворачивания
        if (first > 0 || last > 0) {
            setPeriod();
        }
        sort(sort, sortmetod);
        getRecycler(key_id);
        updateSummaAll();
    }

    private void setTitleName() {


        ColumnName columnName = ColumnName.getColumnName(this, nameTable);
        idTitle.setText(columnName.id);
        summaTitle.setText(columnName.summa);
        avansTitle.setText(columnName.avans);
        noteTitle.setText(columnName.note);
        dateTitle.setText(columnName.date);

        allSummaTitle.setText(columnName.allSumma);
        allAvansTitle.setText(columnName.allAvans);
        ostatotTitle.setText(columnName.ostatok);
    }

    @Override
    public void onBackPressed() {
        if (isBack)
            super.onBackPressed();
        else {
            isBack = true;
            mMenu.clear();
            getMenuInflater().inflate(R.menu.menu_zarplata, mMenu);
            animTitles(false);
            clickTitles(false);
            getRecycler(-1);
        }
    }
}

