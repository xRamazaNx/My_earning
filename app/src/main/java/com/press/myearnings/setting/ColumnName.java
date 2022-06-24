package com.press.myearnings.setting;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.press.myearnings.R;
import com.press.myearnings.database.SchemaDB;
import com.press.myearnings.database.ZarplataHelper;

import java.util.ArrayList;

import androidx.appcompat.app.AlertDialog;

import static android.view.View.inflate;

public class ColumnName {
    public String id;
    public String summa;
    public String avans;
    public String note;
    public String date;
    public String allSumma;
    public String allAvans;
    public String ostatok;
    private ArrayList <String> names;

    public ColumnName() {
    }

    public static ColumnName getColumnName(Context context, String nameTable) {
        ColumnName columnName = new ColumnName();
        SQLiteDatabase database = new ZarplataHelper(context).getWritableDatabase();
        columnName.inflateFromDB(database, nameTable);

        return columnName;
    }

    private void getNames() {
        names = new ArrayList<>();
        names.add(id);
        names.add(summa);
        names.add(avans);
        names.add(note);
        names.add(date);
        names.add(allSumma);
        names.add(allAvans);
        names.add(ostatok);
    }

    public void inflateFromPref(SharedPreferences pref) {
        id = pref.getString(SettingAppearance.KEY_ID_TITLE_NAME, "№");
        summa = pref.getString(SettingAppearance.KEY_SUMMA_TITLE_NAME, "Сумма");
        avans = pref.getString(SettingAppearance.KEY_AVANS_TITLE_NAME, "Аванс");
        note = pref.getString(SettingAppearance.KEY_ZAMETKA_TITLE_NAME, "Заметка");
        date = pref.getString(SettingAppearance.KEY_DATE_TITLE_NAME, "Дата");

        allSumma = pref.getString(SettingAppearance.KEY_SUMMA_CARD_TITLE_NAME, "ВСЕГО");
        allAvans = pref.getString(SettingAppearance.KEY_AVANS_CARD_TITLE_NAME, "АВАНС");
        ostatok = pref.getString(SettingAppearance.KEY_OSTATOK_TITLE_NAME, "ОСТАТОК");
    }

    public void inflateFromDBForMoveArchiveToActive(SQLiteDatabase database, String nameTable) {
        Cursor cursor = database.query(SchemaDB.Table.NAME_ARHIVE, null,
                SchemaDB.Table.NAME_TABLE_DATA + " = ?", new String[]{nameTable},
                null, null, null);

        if (cursor.moveToFirst()) {
            id = cursor.getString(cursor.getColumnIndexOrThrow(SettingAppearance.KEY_ID_TITLE_NAME));
            summa = cursor.getString(cursor.getColumnIndexOrThrow(SettingAppearance.KEY_SUMMA_TITLE_NAME));
            avans = cursor.getString(cursor.getColumnIndexOrThrow(SettingAppearance.KEY_AVANS_TITLE_NAME));
            note = cursor.getString(cursor.getColumnIndexOrThrow(SettingAppearance.KEY_ZAMETKA_TITLE_NAME));
            date = cursor.getString(cursor.getColumnIndexOrThrow(SettingAppearance.KEY_DATE_TITLE_NAME));

            allSumma = cursor.getString(cursor.getColumnIndexOrThrow(SettingAppearance.KEY_SUMMA_CARD_TITLE_NAME));
            allAvans = cursor.getString(cursor.getColumnIndexOrThrow(SettingAppearance.KEY_AVANS_CARD_TITLE_NAME));
            ostatok = cursor.getString(cursor.getColumnIndexOrThrow(SettingAppearance.KEY_OSTATOK_TITLE_NAME));
        }

        cursor.close();

    }
    public void inflateFromDB(SQLiteDatabase database, String nameTable) {

        Cursor cursor = database.query(SchemaDB.Table.NAME_CARD, null,
                SchemaDB.Table.NAME_TABLE_DATA + " = ?", new String[]{nameTable},
                null, null, null);

        if (cursor.moveToFirst()) {
            id = cursor.getString(cursor.getColumnIndexOrThrow(SettingAppearance.KEY_ID_TITLE_NAME));
            summa = cursor.getString(cursor.getColumnIndexOrThrow(SettingAppearance.KEY_SUMMA_TITLE_NAME));
            avans = cursor.getString(cursor.getColumnIndexOrThrow(SettingAppearance.KEY_AVANS_TITLE_NAME));
            note = cursor.getString(cursor.getColumnIndexOrThrow(SettingAppearance.KEY_ZAMETKA_TITLE_NAME));
            date = cursor.getString(cursor.getColumnIndexOrThrow(SettingAppearance.KEY_DATE_TITLE_NAME));

            allSumma = cursor.getString(cursor.getColumnIndexOrThrow(SettingAppearance.KEY_SUMMA_CARD_TITLE_NAME));
            allAvans = cursor.getString(cursor.getColumnIndexOrThrow(SettingAppearance.KEY_AVANS_CARD_TITLE_NAME));
            ostatok = cursor.getString(cursor.getColumnIndexOrThrow(SettingAppearance.KEY_OSTATOK_TITLE_NAME));
        }

        cursor.close();
    }

    public void setNamesToDB(SQLiteDatabase database, String name) {
        ContentValues cont = new ContentValues();
//        String genTable = isArchive ? SchemaDB.Table.NAME_ARHIVE : SchemaDB.Table.NAME_CARD;
        cont.put(SettingAppearance.KEY_ID_TITLE_NAME, id);
        cont.put(SettingAppearance.KEY_SUMMA_TITLE_NAME, summa);
        cont.put(SettingAppearance.KEY_AVANS_TITLE_NAME, avans);
        cont.put(SettingAppearance.KEY_ZAMETKA_TITLE_NAME, note);
        cont.put(SettingAppearance.KEY_DATE_TITLE_NAME, date);

        cont.put(SettingAppearance.KEY_SUMMA_CARD_TITLE_NAME, allSumma);
        cont.put(SettingAppearance.KEY_AVANS_CARD_TITLE_NAME, allAvans);
        cont.put(SettingAppearance.KEY_OSTATOK_TITLE_NAME, ostatok);

        database.update(SchemaDB.Table.NAME_CARD, cont, SchemaDB.Table.NAME_TABLE_DATA + " = ?", new String[]{name});

    }

    public boolean isAllNull() {
        getNames();
        for (String str : names) {
            if (str != null && str.length()> 0)
                return false;
        }
        return true;
    }

    public static class TitleNameChange {
        private final TextView title;
        private final String id;
        private String nameTable;

        public void setNameTable(String nameTable) {
            this.nameTable = nameTable;
        }

        public TitleNameChange(TextView title, String id) {
            this.title = title;
            this.id = id;
        }

        public void invoke(final Context context) {
            final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
            final AlertDialog builder = new AlertDialog.Builder(context).create();
            LinearLayout layout = (LinearLayout) inflate(context, R.layout.set_name_card, null);
            final EditText editName = layout.findViewById(R.id.id_name_card_edittext);
            editName.setText(title.getText());
            final TextView ok = layout.findViewById(R.id.id_button_set_name_ok);
            final TextView cancel = layout.findViewById(R.id.id_button_set_name_cansel);
            builder.setView(layout);
            builder.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
            builder.show();

            editName.requestFocus();
            editName.setSelection(editName.getText().length());
//                            InputMethodManager inputManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
//                            if (inputManager != null) {
//                                inputManager.showSoftInput(editName, WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING |
//                                        WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
//                            }


            View.OnClickListener klick = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v.getId() == R.id.id_button_set_name_ok) {
                        String nameString = editName.getText().toString();
                        title.setText(nameString);
                        if (nameTable != null) {
                            ContentValues value = new ContentValues();
                            value.put(id, nameString);
                            new ZarplataHelper(context).getWritableDatabase().update(SchemaDB.Table.NAME_CARD, value, SchemaDB.Table.NAME_TABLE_DATA + " = ?", new String[]{nameTable});
                        } else
                            pref.edit().putString(id, nameString).apply();

                        builder.dismiss();
                    } else {
                        builder.dismiss();
                    }
                }
            };
            ok.setOnClickListener(klick);
            cancel.setOnClickListener(klick);
        }
    }
}
