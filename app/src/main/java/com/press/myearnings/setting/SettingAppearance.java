package com.press.myearnings.setting;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.util.TypedValue;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.press.myearnings.R;
import com.press.myearnings.dialogs.ColorPickerDialog;
import com.press.myearnings.model.Dannie;
import com.press.myearnings.setting.fontsetting.SettingFontSize;
import com.press.myearnings.setting.fontsetting.SettingFontStyle;

import org.jetbrains.annotations.Nullable;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

/**
 * Created by PRESS on 13.12.2017.
 */

@SuppressLint("Registered")
public class SettingAppearance extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    Handler handler;
    private AppCompatDelegate appCompatDelegate;
    SharedPreferences pref;

    //    TextView title_primer;
    LinearLayout linearSizeZapisi;

    TextView idTitleZ;
    TextView summaTitleZ;
    TextView avansTitleZ;
    TextView dateTitleZ;
    TextView zametkaTitleZ;

    TextView id;
    TextView summa;
    TextView avans;
    TextView date;
    TextView zametka;
    TextView name;
    TextView dateC;

    TextView summaC;
    TextView avansC;
    TextView ostatok;

//    TextView summaToday;
//    TextView avansToday;
//    TextView ostatokToday;

    TextView summaTitleC;
    TextView avansTitleC;
    TextView ostatokTitle;

    public static final String KEY_ID_TITLE = "ID_TITLE";
    public static final String KEY_ID_TITLE_NAME = "ID_TITLE_NAME";
    public static final String KEY_SUMMA_TITLE = "SUMMA_TITLE";
    public static final String KEY_SUMMA_TITLE_NAME = "SUMMA_TITLE_NAME";
    public static final String KEY_AVANS_TITLE = "AVANS_TITLE";
    public static final String KEY_AVANS_TITLE_NAME = "AVANS_TITLE_NAME";
    public static final String KEY_DATE_TITLE = "DATE_TITLE";
    public static final String KEY_DATE_TITLE_NAME = "DATE_TITLE_NAME";
    public static final String KEY_ZAMETKA_TITLE = "ZAMETKA_TITLE";
    public static final String KEY_ZAMETKA_TITLE_NAME = "ZAMETKA_TITLE_NAME";

    public static final String KEY_ID = "ID";
    public static final String KEY_SUMMA = "SUMMA";
    public static final String KEY_AVANS = "AVANS";
    public static final String KEY_DATE = "DATE";
    public static final String KEY_ZAMETKA = "ZAMETKA";

    public static final String KEY_NAME_CARD = "NAME_CARD";
    public static final String KEY_DATE_CARD = "DATE_CARD";
    public static final String KEY_SUMMA_CARD = "SUMMA_CARD";
    public static final String KEY_AVANS_CARD = "AVANS_CARD";
    public static final String KEY_OSTATOK = "OSTATOK";

//    public static final String KEY_SUMMA_TODAY = "summa_today";
//    public static final String KEY_AVANS_TODAY = "avans_today";
//    public static final String KEY_OSTATOK_TODAY = "ostatok_today";

    public static final String KEY_SUMMA_CARD_TITLE = "SUMMA_CARD_TITLE_";
    public static final String KEY_SUMMA_CARD_TITLE_NAME = "SUMMA_CARD_TITLE_NAME";
    public static final String KEY_AVANS_CARD_TITLE = "AVANS_CARD_TITLE";
    public static final String KEY_AVANS_CARD_TITLE_NAME = "AVANS_CARD_TITLE_NAME";
    public static final String KEY_OSTATOK_TITLE = "OSTATOK_TITLE";
    public static final String KEY_OSTATOK_TITLE_NAME = "OSTATOK_TITLE_NAME";
//    public static final String KEY_TOOLBAR = "TOOLBAR";

    ListPreference itemHeight;
//    private LinearLayout linearSizeZapisiToday;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        getDelegate().installViewFactory();
        getDelegate().onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.setting_appearance);
        pref = getSharedPreferences(getPackageName() + "_preferences", MODE_MULTI_PROCESS);
        itemHeight = (ListPreference) findPreference(getString(R.string.zapisi_size_key));

        LinearLayout l = (LinearLayout) getLayoutInflater().inflate(R.layout.color_picker_pref_view, null);
        findForAllTextView(l);
        onClickAll();

        getListView().addHeaderView(l);

        PreferenceScreen fontSize = (PreferenceScreen) findPreference(getString(R.string.font_size));
        PreferenceScreen fontStyle = (PreferenceScreen) findPreference(getString(R.string.font_style));
        PreferenceScreen reset = (PreferenceScreen) findPreference(getString(R.string.reset));

        fontSize.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(SettingAppearance.this, SettingFontSize.class);
                startActivity(intent);
                return true;
            }
        });
        fontStyle.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(SettingAppearance.this, SettingFontStyle.class);
                startActivity(intent);
                return true;
            }
        });
        reset.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                setDefaultColorForText();
                updateColorAndText();
                getListView().setSelection(0);
//                String s = getFilesDir().getPath();
//                File file = new File(s.substring(0, s.lastIndexOf("/") + 1) + "shared_prefs/" + getPackageName() + "_preferences.xml");
//                if (file.exists()) {
//                    file.delete();
//  надо все делать вручную ((
//                    finish();
//                    startActivity(getIntent());
//                    updateColorAndText();
//                    Log.d("preferencePath ", file.getPath());
//                }
                return true;
            }
        });

    }

    private void findForAllTextView(LinearLayout linearLayout) {
//        title_primer = linearLayout.findViewById(R.id.title_primer);
//        title_primer.setText(R.string.tit_primer);
        linearSizeZapisi = linearLayout.findViewById(R.id.linear_height_zapisi_primer);
//        linearSizeZapisiToday = linearLayout.findViewById(R.id.id_schet_1_dey_date);

        idTitleZ = linearLayout.findViewById(R.id.zarplata_title_number_primer);
        idTitleZ.setText(pref.getString(KEY_ID_TITLE_NAME, getString(R.string.number)));

        summaTitleZ = linearLayout.findViewById(R.id.zarplata_title_summa_primer);
        summaTitleZ.setText(pref.getString(KEY_SUMMA_TITLE_NAME, getString(R.string.summa)));

        avansTitleZ = linearLayout.findViewById(R.id.zarplata_title_avans_primer);
        avansTitleZ.setText(pref.getString(KEY_AVANS_TITLE_NAME, getString(R.string.avans)));

        dateTitleZ = linearLayout.findViewById(R.id.zarplata_title_date_primer);
        dateTitleZ.setText(pref.getString(KEY_DATE_TITLE_NAME, getString(R.string.date)));

        zametkaTitleZ = linearLayout.findViewById(R.id.zarplata_title_zametka_primer);
        zametkaTitleZ.setText(pref.getString(KEY_ZAMETKA_TITLE_NAME, getString(R.string.note)));

        id = linearLayout.findViewById(R.id.id_numeraciya_primer);
        summa = linearLayout.findViewById(R.id.id_colichestvo_rulonov_1_kolona_primer);
        avans = linearLayout.findViewById(R.id.id_avans_primer);
        date = linearLayout.findViewById(R.id.id_data_primer);
        zametka = linearLayout.findViewById(R.id.id_comment_1_dey_primer);

        name = linearLayout.findViewById(R.id.id_name_card_primer);
        dateC = linearLayout.findViewById(R.id.id_date_card_primer);

        summaC = linearLayout.findViewById(R.id.id_summ_all_money_card_primer);
        avansC = linearLayout.findViewById(R.id.id_summ_avans_card_primer);
        ostatok = linearLayout.findViewById(R.id.id_summ_money_k_vidache_card_primer);

//        summaToday = linearLayout.findViewById(R.id.id_summa_today);
//        avansToday = linearLayout.findViewById(R.id.id_avans_today);
//        ostatokToday = linearLayout.findViewById(R.id.id_ostatok_today);

        summaTitleC = linearLayout.findViewById(R.id.card_title_vsego_primer);
        summaTitleC.setText(pref.getString(KEY_SUMMA_CARD_TITLE_NAME, getString(R.string.EARNING)));

        avansTitleC = linearLayout.findViewById(R.id.card_title_avans_primer);
        avansTitleC.setText(pref.getString(KEY_AVANS_CARD_TITLE_NAME, getString(R.string.cash_advance)));

        ostatokTitle = linearLayout.findViewById(R.id.card_title_ostatok_primer);
        ostatokTitle.setText(pref.getString(KEY_OSTATOK_TITLE_NAME, getString(R.string.THE_REST)));

    }

    private void onClickAll() {
        View.OnClickListener clickColor = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = "";
                switch (view.getId()) {
                    case R.id.zarplata_title_number_primer:
                        id = KEY_ID_TITLE_NAME;
                        break;
                    case R.id.zarplata_title_summa_primer:
                        id = KEY_SUMMA_TITLE_NAME;
                        break;
                    case R.id.zarplata_title_avans_primer:
                        id = KEY_AVANS_TITLE_NAME;
                        break;
                    case R.id.zarplata_title_date_primer:
                        id = KEY_DATE_TITLE_NAME;
                        break;
                    case R.id.zarplata_title_zametka_primer:
                        id = KEY_ZAMETKA_TITLE_NAME;
                        break;
                    case R.id.card_title_vsego_primer:
                        id = KEY_SUMMA_CARD_TITLE_NAME;
                        break;
                    case R.id.card_title_avans_primer:
                        id = KEY_AVANS_CARD_TITLE_NAME;
                        break;
                    case R.id.card_title_ostatok_primer:
                        id = KEY_OSTATOK_TITLE_NAME;
                        break;
                }
                if (id.length() > 0) {
                    showChangedWin(view, id);
                } else
                    showColorDialog(view);
            }

            private void showChangedWin(final View view, final String id) {
                final TextView title = (TextView) view;
                PopupMenu popupMenu = new PopupMenu(SettingAppearance.this, view);
                popupMenu.inflate(R.menu.change_setting_of_title);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        if (menuItem.getItemId() == R.id.name_column) {
                            new ColumnName.TitleNameChange(title, id).invoke(SettingAppearance.this);
                        } else {
                            showColorDialog(view);
                        }

                        return true;
                    }
                });

                popupMenu.show();
            }

            private void showColorDialog(final View view) {

                ColorPickerDialog colorPickerDialog;
                colorPickerDialog = new ColorPickerDialog(SettingAppearance.this, new ColorPickerDialog.OnColorChangedListener() {
                    @Override
                    public void colorChanged(int color) {
                        ((TextView) view).setTextColor(color);
                        setCyrentColorForText();
                    }
                },
                        ((TextView) view).getCurrentTextColor());
                colorPickerDialog.show();
            }
        };
//        title_primer.setOnClickListener(clickColor);
        idTitleZ.setOnClickListener(clickColor);
        summaTitleZ.setOnClickListener(clickColor);
        avansTitleZ.setOnClickListener(clickColor);
        dateTitleZ.setOnClickListener(clickColor);
        zametkaTitleZ.setOnClickListener(clickColor);

        id.setOnClickListener(clickColor);
        summa.setOnClickListener(clickColor);
        avans.setOnClickListener(clickColor);
        date.setOnClickListener(clickColor);
        zametka.setOnClickListener(clickColor);

        name.setOnClickListener(clickColor);
        dateC.setOnClickListener(clickColor);
        summaC.setOnClickListener(clickColor);
        avansC.setOnClickListener(clickColor);
        ostatok.setOnClickListener(clickColor);

//        summaToday.setOnClickListener(clickColor);
//        avansToday.setOnClickListener(clickColor);
//        ostatokToday.setOnClickListener(clickColor);

        summaTitleC.setOnClickListener(clickColor);
        avansTitleC.setOnClickListener(clickColor);
        ostatokTitle.setOnClickListener(clickColor);
    }

    private void setFontSize(SharedPreferences pref) {

        String keyNumber = getString(R.string.number);
        String keySumma = getString(R.string.summa);
        String keyAvans = getString(R.string.avans);
        String keyDate = getString(R.string.date);
        String keyNote = getString(R.string.note);

        String keyZarplata = getString(R.string.zarplata);
        String keyAvansCard = getString(R.string.avans_key_obshee);
        String keyOstatok = getString(R.string.Ostatok);


        id.setTextSize(Integer.parseInt(pref.getString(keyNumber, "14")));
        summa.setTextSize(Integer.parseInt(pref.getString(keySumma, "14")));
        avans.setTextSize(Integer.parseInt(pref.getString(keyAvans, "14")));
        date.setTextSize(Integer.parseInt(pref.getString(keyDate, "12")));
        zametka.setTextSize(Integer.parseInt(pref.getString(keyNote, "12")));


        summaC.setTextSize(Integer.parseInt(pref.getString(keyZarplata, "16")));
        avansC.setTextSize(Integer.parseInt(pref.getString(keyAvansCard, "16")));
        ostatok.setTextSize(Integer.parseInt(pref.getString(keyOstatok, "16")));

    }

    private void setFontStyle(SharedPreferences pref) {

        String keySumma = getString(R.string.summaKeyStyle);
        String keyAvans = getString(R.string.avansKeyStyle);
        String keyNote = getString(R.string.noteKeyStyle);
        String keyDate = getString(R.string.dateKeyStyle);
        String keyNumber = getString(R.string.numberKeyStyle);

        String keyZarplata = getString(R.string.zarplataKeyStyle);
        String keyAvansCard = getString(R.string.avans_key_obsheeKeyStyle);
        String keyOstatok = getString(R.string.OstatokKeyStyle);


        id.setTypeface(null, Integer.parseInt(pref.getString(keyNumber, "0")));
        summa.setTypeface(null, Integer.parseInt(pref.getString(keySumma, "0")));
        avans.setTypeface(null, Integer.parseInt(pref.getString(keyAvans, "0")));
        date.setTypeface(null, Integer.parseInt(pref.getString(keyDate, "0")));
        zametka.setTypeface(null, Integer.parseInt(pref.getString(keyNote, "0")));

        summaC.setTypeface(null, Integer.parseInt(pref.getString(keyZarplata, "0")));
        avansC.setTypeface(null, Integer.parseInt(pref.getString(keyAvansCard, "0")));
        ostatok.setTypeface(null, Integer.parseInt(pref.getString(keyOstatok, "0")));

    }


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

    private int heightZapisi(SharedPreferences pref) {
        int heightPX = Integer.parseInt(pref.getString(getString(R.string.zapisi_size_key), "30"));
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, heightPX, getResources().getDisplayMetrics());
    }

    @Override
    protected void onResume() {
        super.onResume();

//        title_primer.setTextColor(SettingColorSet.getToolbarTitleColor(pref, false));

        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
//                if (msg.what == 0)
                linearSizeZapisi.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, heightZapisi(pref)));
            }
        };


        Dannie dannie = new Dannie();
        int variableDateZapisi = Integer.parseInt(pref.getString(getString(R.string.date_pref_for_zapisi), "2"));
        int variableDateCard = Integer.parseInt(pref.getString(getString(R.string.date_pref_for_card), "2"));

        String dateCard = dannie.getDate(variableDateCard);
        String dateZapis = dannie.getDate(variableDateZapisi);

        this.date.setText(dateZapis);
        dateC.setText(String.format("%s - %s", dateCard, dateCard));

        updateColorAndText();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    private void updateColorAndText() {
        SettingColorSet.getTextColor(pref, idTitleZ, SettingColorSet.ViewName.idTitle);
        SettingColorSet.getTextColor(pref, summaTitleZ, SettingColorSet.ViewName.summaTitle);
        SettingColorSet.getTextColor(pref, avansTitleZ, SettingColorSet.ViewName.avansTitle);
        SettingColorSet.getTextColor(pref, dateTitleZ, SettingColorSet.ViewName.dateTitle);
        SettingColorSet.getTextColor(pref, zametkaTitleZ, SettingColorSet.ViewName.noteTitle);

        SettingColorSet.getTextColor(pref, id, SettingColorSet.ViewName.id);
        SettingColorSet.getTextColor(pref, summa, SettingColorSet.ViewName.summa);
        SettingColorSet.getTextColor(pref, avans, SettingColorSet.ViewName.avans);
        SettingColorSet.getTextColor(pref, date, SettingColorSet.ViewName.date);
        SettingColorSet.getTextColor(pref, zametka, SettingColorSet.ViewName.note);

        SettingColorSet.getTextColor(pref, name, SettingColorSet.ViewName.name);
        SettingColorSet.getTextColor(pref, dateC, SettingColorSet.ViewName.dateCard);
        SettingColorSet.getTextColor(pref, summaC, SettingColorSet.ViewName.summaCard);
        SettingColorSet.getTextColor(pref, avansC, SettingColorSet.ViewName.avansCard);
        SettingColorSet.getTextColor(pref, ostatok, SettingColorSet.ViewName.ostatok);

//        SettingColorSet.getTextColor(pref, summaToday, SettingColorSet.ViewName.summaToday);
//        SettingColorSet.getTextColor(pref, avansToday, SettingColorSet.ViewName.avansToday);
//        SettingColorSet.getTextColor(pref, ostatokToday, SettingColorSet.ViewName.ostatokToday);

        SettingColorSet.getTextColor(pref, summaTitleC, SettingColorSet.ViewName.summaCardTitle);
        SettingColorSet.getTextColor(pref, avansTitleC, SettingColorSet.ViewName.avansCardTitle);
        SettingColorSet.getTextColor(pref, ostatokTitle, SettingColorSet.ViewName.ostatokTitle);

        setFontSize(pref);
        setFontStyle(pref);

        ViewGroup.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, heightZapisi(pref));
        linearSizeZapisi.setLayoutParams(params);
//        linearSizeZapisiToday.setLayoutParams(params);
    }

    @Override
    protected void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if (s.equals(getString(R.string.zapisi_size_key))) {
            handler.sendEmptyMessage(0);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        getDelegate().onStop();

//        SettingColorSet.setToolbarTitleColor(pref, title_primer.getCurrentTextColor());

    }

    private void setCyrentColorForText() {
        SettingColorSet.setTextColor(pref, idTitleZ, SettingColorSet.ViewName.idTitle);
        SettingColorSet.setTextColor(pref, summaTitleZ, SettingColorSet.ViewName.summaTitle);
        SettingColorSet.setTextColor(pref, avansTitleZ, SettingColorSet.ViewName.avansTitle);
        SettingColorSet.setTextColor(pref, dateTitleZ, SettingColorSet.ViewName.dateTitle);
        SettingColorSet.setTextColor(pref, zametkaTitleZ, SettingColorSet.ViewName.noteTitle);

        SettingColorSet.setTextColor(pref, id, SettingColorSet.ViewName.id);
        SettingColorSet.setTextColor(pref, summa, SettingColorSet.ViewName.summa);
        SettingColorSet.setTextColor(pref, avans, SettingColorSet.ViewName.avans);
        SettingColorSet.setTextColor(pref, date, SettingColorSet.ViewName.date);
        SettingColorSet.setTextColor(pref, zametka, SettingColorSet.ViewName.note);

        SettingColorSet.setTextColor(pref, name, SettingColorSet.ViewName.name);
        SettingColorSet.setTextColor(pref, dateC, SettingColorSet.ViewName.dateCard);

        SettingColorSet.setTextColor(pref, summaC, SettingColorSet.ViewName.summaCard);
        SettingColorSet.setTextColor(pref, avansC, SettingColorSet.ViewName.avansCard);
        SettingColorSet.setTextColor(pref, ostatok, SettingColorSet.ViewName.ostatok);

//        SettingColorSet.setTextColor(pref, summaToday, SettingColorSet.ViewName.summaToday);
//        SettingColorSet.setTextColor(pref, avansToday, SettingColorSet.ViewName.avansToday);
//        SettingColorSet.setTextColor(pref, ostatokToday, SettingColorSet.ViewName.ostatokToday);

        SettingColorSet.setTextColor(pref, summaTitleC, SettingColorSet.ViewName.summaCardTitle);
        SettingColorSet.setTextColor(pref, avansTitleC, SettingColorSet.ViewName.avansCardTitle);
        SettingColorSet.setTextColor(pref, ostatokTitle, SettingColorSet.ViewName.ostatokTitle);
    }

    private void setDefaultColorForText() {

        itemHeight.setValueIndex(6);

        idTitleZ.setTextColor(ContextCompat.getColor(this, R.color.color_text_zagolovki_zarplata));
        summaTitleZ.setTextColor(ContextCompat.getColor(this, R.color.color_text_zagolovki_zarplata));
        avansTitleZ.setTextColor(ContextCompat.getColor(this, R.color.color_text_zagolovki_zarplata));
        dateTitleZ.setTextColor(ContextCompat.getColor(this, R.color.color_text_zagolovki_zarplata));
        zametkaTitleZ.setTextColor(ContextCompat.getColor(this, R.color.color_text_zagolovki_zarplata));

        id.setTextColor(ContextCompat.getColor(this, R.color.cent));
        summa.setTextColor(ContextCompat.getColor(this, R.color.text));
        avans.setTextColor(ContextCompat.getColor(this, R.color.color_avans_zapisi));
        date.setTextColor(ContextCompat.getColor(this, R.color.text));
        zametka.setTextColor(ContextCompat.getColor(this, R.color.text));

        name.setTextColor(ContextCompat.getColor(this, R.color.white));
        dateC.setTextColor(ContextCompat.getColor(this, R.color.color_date_arhive_card));
        summaC.setTextColor(ContextCompat.getColor(this, R.color.color_vsego_card));
        avansC.setTextColor(ContextCompat.getColor(this, R.color.color_avans_card));
        ostatok.setTextColor(ContextCompat.getColor(this, R.color.color_ostatok_card));

//        summaToday.setTextColor(ContextCompat.getColor(this, R.color.color_vsego_card));
//        avansToday.setTextColor(ContextCompat.getColor(this, R.color.color_avans_card));
//        ostatokToday.setTextColor(ContextCompat.getColor(this, R.color.color_ostatok_card));

        summaTitleC.setTextColor(ContextCompat.getColor(this, R.color.color_text_zagolovki_zarplata));
        avansTitleC.setTextColor(ContextCompat.getColor(this, R.color.color_text_zagolovki_zarplata));
        ostatokTitle.setTextColor(ContextCompat.getColor(this, R.color.color_text_zagolovki_zarplata));

        setCyrentColorForText();

        SharedPreferences.Editor editor = pref.edit();

        editor.putString(getString(R.string.number), "14");
        editor.putString(getString(R.string.summa), "14");
        editor.putString(getString(R.string.avans), "14");
        editor.putString(getString(R.string.note), "12");
        editor.putString(getString(R.string.date), "12");

        editor.putString(getString(R.string.zarplata), "16");
        editor.putString(getString(R.string.avans_key_obshee), "16");
        editor.putString(getString(R.string.Ostatok), "16");

        editor.putString(getString(R.string.numberKeyStyle), "0");
        editor.putString(getString(R.string.summaKeyStyle), "0");
        editor.putString(getString(R.string.avansKeyStyle), "0");
        editor.putString(getString(R.string.noteKeyStyle), "0");
        editor.putString(getString(R.string.dateKeyStyle), "0");

        editor.putString(getString(R.string.zarplataKeyStyle), "0");
        editor.putString(getString(R.string.avans_key_obsheeKeyStyle), "0");
        editor.putString(getString(R.string.OstatokKeyStyle), "0");

        editor.apply();
    }
}
