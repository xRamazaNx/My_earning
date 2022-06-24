package com.press.myearnings.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;

import com.press.myearnings.database.SchemaDB.Table.Cols;
import com.press.myearnings.model.Dannie;
import com.press.myearnings.model.DannieCard;
import com.press.myearnings.setting.SettingAppearance;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.press.myearnings.database.SchemaDB.Table.Cols.AVANS;
import static com.press.myearnings.database.SchemaDB.Table.Cols.COMENT;
import static com.press.myearnings.database.SchemaDB.Table.Cols.DATE_LONG;
import static com.press.myearnings.database.SchemaDB.Table.Cols.SUMMA;


public class ZarplataHelper extends SQLiteOpenHelper {
    private static final int VERSION = 3;// для добавления колоны isArchive
    private static final String DATABASE_NAME = "myDataBase.db";

    public ZarplataHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);

    }

    public static void updateDBTable(SQLiteDatabase db, String nameTable, List<Dannie> listDannie) {
        db.beginTransaction();
        db.execSQL("drop table " + nameTable);
        db.execSQL(SchemaDB.createTable(nameTable));
        for (Dannie d : listDannie) {
            ContentValues values = new ContentValues();
            values.put(SUMMA, d.getSumma());
            values.put(AVANS, d.getAvans());
            values.put(COMENT, d.getComent());
            values.put(DATE_LONG, d.getDateMl());
            db.insert(nameTable, null, values);
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        updateDB(db, 0);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        updateDB(db, i);
    }

    private void updateDB(SQLiteDatabase db, int old) {

        if (old < 1) {
            db.execSQL("create table " + SchemaDB.Table.NAME_CARD + " (" +
                    Cols.ID + " integer primary key autoincrement, " +
                    Cols.NAME_FOR_TABLE_CARD + ", " +
                    SchemaDB.Table.NAME_TABLE_DATA + ", " +
                    Cols.DATE_CREATE_TABLE + ", " +
                    Cols.DATE_MODIFY_TABLE +
                    ");"
            );
            db.execSQL("create table " + SchemaDB.Table.NAME_ARHIVE + " (" +
                    Cols.ID + " integer primary key autoincrement, " +
                    Cols.NAME_FOR_TABLE_CARD + ", " +
                    SchemaDB.Table.NAME_TABLE_DATA + ", " +
                    Cols.DATE_CREATE_TABLE + ", " +
                    Cols.DATE_MODIFY_TABLE +
                    ");"
            );
        }
        if (old < 2) {
            db.execSQL("alter table " + SchemaDB.Table.NAME_CARD +
                    " add column " + SettingAppearance.KEY_ID_TITLE_NAME
            );
            db.execSQL("alter table " + SchemaDB.Table.NAME_CARD +
                    " add column " + SettingAppearance.KEY_SUMMA_TITLE_NAME
            );
            db.execSQL("alter table " + SchemaDB.Table.NAME_CARD +
                    " add column " + SettingAppearance.KEY_AVANS_TITLE_NAME
            );
            db.execSQL("alter table " + SchemaDB.Table.NAME_CARD +
                    " add column " + SettingAppearance.KEY_ZAMETKA_TITLE_NAME
            );
            db.execSQL("alter table " + SchemaDB.Table.NAME_CARD +
                    " add column " + SettingAppearance.KEY_DATE_TITLE_NAME
            );

            db.execSQL("alter table " + SchemaDB.Table.NAME_CARD +
                    " add column " + SettingAppearance.KEY_SUMMA_CARD_TITLE_NAME
            );
            db.execSQL("alter table " + SchemaDB.Table.NAME_CARD +
                    " add column " + SettingAppearance.KEY_AVANS_CARD_TITLE_NAME
            );
            db.execSQL("alter table " + SchemaDB.Table.NAME_CARD +
                    " add column " + SettingAppearance.KEY_OSTATOK_TITLE_NAME
            );


            db.execSQL("alter table " + SchemaDB.Table.NAME_ARHIVE +
                    " add column " + SettingAppearance.KEY_ID_TITLE_NAME
            );
            db.execSQL("alter table " + SchemaDB.Table.NAME_ARHIVE +
                    " add column " + SettingAppearance.KEY_SUMMA_TITLE_NAME
            );
            db.execSQL("alter table " + SchemaDB.Table.NAME_ARHIVE +
                    " add column " + SettingAppearance.KEY_AVANS_TITLE_NAME
            );
            db.execSQL("alter table " + SchemaDB.Table.NAME_ARHIVE +
                    " add column " + SettingAppearance.KEY_ZAMETKA_TITLE_NAME
            );
            db.execSQL("alter table " + SchemaDB.Table.NAME_ARHIVE +
                    " add column " + SettingAppearance.KEY_DATE_TITLE_NAME
            );

            db.execSQL("alter table " + SchemaDB.Table.NAME_ARHIVE +
                    " add column " + SettingAppearance.KEY_SUMMA_CARD_TITLE_NAME
            );
            db.execSQL("alter table " + SchemaDB.Table.NAME_ARHIVE +
                    " add column " + SettingAppearance.KEY_AVANS_CARD_TITLE_NAME
            );
            db.execSQL("alter table " + SchemaDB.Table.NAME_ARHIVE +
                    " add column " + SettingAppearance.KEY_OSTATOK_TITLE_NAME
            );
        }
        if (old < 3) {

            db.execSQL("alter table " + SchemaDB.Table.NAME_CARD +
                    " add column " + SchemaDB.Table.IS_ARCHIVE
            );

            moveArchiveToActive(db);

            db.execSQL("drop table " + SchemaDB.Table.NAME_ARHIVE);


        }
    }

    // move archive to active and put "isArchive" to true if table archived
    private void moveArchiveToActive(SQLiteDatabase db) {
        Cursor cursorCard = db.query(SchemaDB.Table.NAME_ARHIVE, null, null, null, null, null, null);
        ArrayList<DannieCard> list = new ArrayList<>();
        if (cursorCard.moveToFirst()) {
            do {
                String nameCard = cursorCard.getString(cursorCard.getColumnIndexOrThrow(Cols.NAME_FOR_TABLE_CARD));
                String nameTable = cursorCard.getString(cursorCard.getColumnIndexOrThrow(SchemaDB.Table.NAME_TABLE_DATA));
                String dateCreated = cursorCard.getString(cursorCard.getColumnIndexOrThrow(Cols.DATE_CREATE_TABLE));
                String dateModify = cursorCard.getString(cursorCard.getColumnIndexOrThrow(Cols.DATE_MODIFY_TABLE));

                DannieCard d = new DannieCard();
                d.setNameCard(nameCard);
                d.setNameTable(nameTable);
                d.setDateForCreated(dateCreated);
                d.setDateForModify(dateModify);
                d.isArchive = true;
                d.columnName.inflateFromDBForMoveArchiveToActive(db, nameTable);

                list.add(d);
            } while (cursorCard.moveToNext());
        }
        cursorCard.close();
        for (DannieCard dannieCard : list) {
            ContentValues contentValues = new ContentValues();

            contentValues.put(SchemaDB.Table.NAME_TABLE_DATA, dannieCard.getNameTable());
            contentValues.put(Cols.NAME_FOR_TABLE_CARD, dannieCard.getNameCard());
            contentValues.put(Cols.DATE_CREATE_TABLE, dannieCard.getDateForCreated());
            contentValues.put(Cols.DATE_MODIFY_TABLE, dannieCard.getDateForModify());
            contentValues.put(SchemaDB.Table.IS_ARCHIVE, 1);

            db.insert(SchemaDB.Table.NAME_CARD, null, contentValues);

            dannieCard.columnName.setNamesToDB(db, dannieCard.getNameTable());
        }
    }

    private void renameColumnAndAddDateModify(SQLiteDatabase db, String table) {
        ArrayList<DannieCard> cardList = new ArrayList<>();
        Cursor cursorCard = db.query(table, null, null, null, null, null, null);
        //  пересчет содиржомого
        if (cursorCard.moveToFirst()) {
            do {
                String nameCard = cursorCard.getString(cursorCard.getColumnIndexOrThrow(Cols.NAME_FOR_TABLE_CARD));
                String nameTable = cursorCard.getString(cursorCard.getColumnIndexOrThrow(SchemaDB.Table.NAME_DETAIL_ARHIVE));
                String dateCreated = cursorCard.getString(cursorCard.getColumnIndexOrThrow(Cols.COMENT));

                DannieCard d = new DannieCard();

                d.setNameCard(nameCard);
                d.setNameTable(nameTable);
                d.setDateForCreated(dateCreated);

                cardList.add(d);

            } while (cursorCard.moveToNext());
            cursorCard.close();
        }
        //удаляем таблицу
        db.execSQL("DROP TABLE " + table);
        //создаем таблицу заново с правильной колной "datecreated"
        db.execSQL("create table " + table + " (" +
                Cols.ID + " integer primary key autoincrement, " +
                Cols.NAME_FOR_TABLE_CARD + ", " +
                SchemaDB.Table.NAME_DETAIL_ARHIVE + ", " +
                Cols.DATE_CREATE_TABLE + ", " + // to
                Cols.DATE_MODIFY_TABLE +

                ");"
        );
        // заполнение таблицы
        for (DannieCard d : cardList) {
            ContentValues v = new ContentValues();

            v.put(Cols.NAME_FOR_TABLE_CARD, d.getNameCard());
            v.put(SchemaDB.Table.NAME_DETAIL_ARHIVE, d.getNameTable());
            v.put(Cols.DATE_CREATE_TABLE, d.getDateForCreated());

            db.insert(table, null, v);
        }

    }

    public static void changeStructureDb(Context context, SQLiteDatabase db, String table) {
        SQLiteDatabase database = new ZarplataHelper(context).getWritableDatabase();
        ArrayList<DannieCard> cardList = new ArrayList<>();
        Cursor cursorCard = db.query(table, null, null, null, null, null, null);
        boolean isArchive = false;

        if (cursorCard.moveToFirst()) {
            do {

                String nameCard = cursorCard.getString(cursorCard.getColumnIndexOrThrow(Cols.NAME_FOR_TABLE_CARD));
                String nameTableImported;
                String dateCreated;
                String dateModify;

                nameTableImported = cursorCard.getString(cursorCard.getColumnIndexOrThrow(SchemaDB.Table.NAME_TABLE_DATA));
                dateCreated = cursorCard.getString(cursorCard.getColumnIndexOrThrow(Cols.DATE_CREATE_TABLE));
                dateModify = cursorCard.getString(cursorCard.getColumnIndexOrThrow(Cols.DATE_MODIFY_TABLE));

                if (table.equals(SchemaDB.Table.NAME_ARHIVE)) {
                    isArchive = true;
                } else {
                    try {
                        isArchive = cursorCard.getInt(cursorCard.getColumnIndexOrThrow(SchemaDB.Table.IS_ARCHIVE)) == 1;
                    } catch (Exception ignored) {

                    }
                }

                Cursor cursor = db.query(nameTableImported, null, null, null, null, null, null);

                ArrayList<Dannie> listDannie = new ArrayList<>();
                if (cursor.moveToFirst()) { // пересчет внутреннего содержания
                    do {

                        Calendar c = Calendar.getInstance();

                        int id = cursor.getInt(cursor.getColumnIndexOrThrow(Cols.ID));
                        String rul;
                        String date;
                        String av = cursor.getString(cursor.getColumnIndexOrThrow(Cols.AVANS));
                        String coment = cursor.getString(cursor.getColumnIndexOrThrow(Cols.COMENT));

                        rul = cursor.getString(cursor.getColumnIndexOrThrow(SUMMA));
                        date = cursor.getString(cursor.getColumnIndexOrThrow(Cols.DATE_LONG));
                        c.setTimeInMillis(Long.parseLong(date));

                        Dannie dannie = new Dannie();

                        dannie.setID(id);
                        dannie.setSumma(rul);
                        dannie.setAvans(av);
                        dannie.setComent(coment);
                        dannie.setDateMl(c.getTimeInMillis());

                        listDannie.add(dannie);
                    } while (cursor.moveToNext());
                }
                cursor.close();
//                db.execSQL("drop oldTable " + nameTableImported);
                String newNameTable = SchemaDB.Table.NAME_TABLE_DATA + String.valueOf(Calendar.getInstance().getTimeInMillis());
                database.execSQL(SchemaDB.createTable(newNameTable));
                for (Dannie d : listDannie) {
                    int id = d.getID();
                    String summ = d.getSumma();
                    String avans = d.getAvans();
                    long dateMl = d.getDateMl();
                    String coment = d.getComent();

                    ContentValues values = new ContentValues();
                    values.put(Cols.ID, id);
                    values.put(SUMMA, summ);
                    values.put(Cols.AVANS, avans);
                    values.put(Cols.DATE_LONG, dateMl);
                    values.put(Cols.COMENT, coment);

                    database.insert(newNameTable, null, values);

                }

                DannieCard d = new DannieCard();

                d.setNameCard(nameCard);
                d.setNameTable(newNameTable);

                if (listDannie.size() > 0) {

                    if (dateCreated == null) {
                        dateCreated = String.valueOf(listDannie.get(0).getDateMl());
                    }

                    if (dateModify == null) {
                        Dannie dan = listDannie.get(listDannie.size() - 1);
                        dateModify = String.valueOf(dan.getDateMl());
                    }
                } else {
                    dateModify = dateCreated;
                }

                d.setDateForCreated(dateCreated);
                d.setDateForModify(dateModify);
                if (cursorCard.getColumnCount() > 10)
                    d.columnName.inflateFromDB(db, nameTableImported);
                else
                    d.columnName.inflateFromPref(PreferenceManager.getDefaultSharedPreferences(context));
                d.isArchive = isArchive;
                cardList.add(d);
            } while (cursorCard.moveToNext());
        }
        cursorCard.close();

        for (DannieCard da : cardList) {

            String nameCard = da.getNameCard();
            String nameTable = da.getNameTable();
            String dateCreated = da.getDateForCreated();
            String dateModify = da.getDateForModify();

            ContentValues values = new ContentValues();
            values.put(Cols.NAME_FOR_TABLE_CARD, nameCard);
            values.put(SchemaDB.Table.NAME_TABLE_DATA, nameTable);
            values.put(Cols.DATE_CREATE_TABLE, dateCreated);
            values.put(Cols.DATE_MODIFY_TABLE, dateModify);

            values.put(SchemaDB.Table.IS_ARCHIVE, da.isArchive ? 1 : 0);

            database.insert(SchemaDB.Table.NAME_CARD, null, values);
            da.columnName.setNamesToDB(database, nameTable);
        }

    }

    private static void importNewStructureDB(SQLiteDatabase db, String table) {

    }

    public static void updateDateModify(Context context, String tableData, String timeMls) {
        SQLiteDatabase database = new ZarplataHelper(context).getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Cols.DATE_MODIFY_TABLE, timeMls);
        database.update(SchemaDB.Table.NAME_CARD,
                contentValues,
                SchemaDB.Table.NAME_TABLE_DATA + " = ?", new String[]{tableData});
        database.close();
    }

//    private void portOldVersion(SQLiteDatabase db, String table) {
//
//        Cursor cursorCard = db.query(table, null, null, null, null, null, null);
//
//        ArrayList<DannieCard> cardList = new ArrayList<>();
//        ArrayList<DannieCard> newcardList = new ArrayList<>();
//        if (cursorCard.moveToFirst()) {
//
//            do {
//                String nameTable = cursorCard.getString(cursorCard.getColumnIndexOrThrow(SchemaDB.Table.NAME_DETAIL_ARHIVE));
//                DannieCard d = new DannieCard();
//                d.setNameTable(nameTable);
//                cardList.add(d);
//                Cursor cursor = db.query(nameTable, null, null, null, null, null, null);
//
//                ArrayList<Dannie> listDannie = new ArrayList<>();
//                if (cursor.moveToFirst()) { // пересчет внутреннего содержания
//                    do {
//                        int id = cursor.getInt(cursor.getColumnIndexOrThrow(SchemaDB.Table.Cols.ID));
//                        String rul = cursor.getString(cursor.getColumnIndexOrThrow(SchemaDB.Table.Cols.RUL1));
//                        String av = cursor.getString(cursor.getColumnIndexOrThrow(SchemaDB.Table.Cols.AVANS));
//                        String coment = cursor.getString(cursor.getColumnIndexOrThrow(SchemaDB.Table.Cols.COMENT));
//                        String date = cursor.getString(cursor.getColumnIndexOrThrow(SchemaDB.Table.Cols.DATE));
//
//                        int day = Integer.parseInt(date.substring(4, 6));
//                        int month = Integer.parseInt(date.substring(7, 9)) - 1;
//                        int year = Integer.parseInt(date.substring(10, 14));
//
//                        Dannie dannie = new Dannie();
//
//                        dannie.setID(id);
//                        dannie.setSumma(rul);
//                        dannie.setAvans(av);
//                        dannie.setComent(coment);
//                        dannie.setDateInt(day, month, year);
//
//                        listDannie.add(dannie);
//                    } while (cursor.moveToNext());
//                }
//                cursor.close();
//
//                String nameCard = cursorCard.getString(cursorCard.getColumnIndexOrThrow(SchemaDB.Table.Cols.NAME_FOR_TABLE_CARD));
//                String nameDB = SchemaDB.Table.NAME_DETAIL_ARHIVE + "port" + refractName;
//                DannieCard newDA = new DannieCard();
//
//                newDA.setNameCard(nameCard);
//                newDA.setNameTable(nameDB);
//                newcardList.add(newDA);
//
//                db.execSQL(SchemaDB.createTableArhive("port" + refractName));
//                refractName++;
//
//                for (Dannie dann : listDannie) {
//
//                    ContentValues values = new ContentValues();
//
//                    values.put(SchemaDB.Table.Cols.RUL1, dann.getSumma());
//                    values.put(SchemaDB.Table.Cols.AVANS, dann.getAvans());
//
//                    values.put(SchemaDB.Table.Cols.DATE_YEAR, dann.getDateIntArray()[0]);
//                    values.put(SchemaDB.Table.Cols.DATE_MONTH, dann.getDateIntArray()[1]);
//                    values.put(SchemaDB.Table.Cols.DATE_DAY, dann.getDateIntArray()[2]);
//
//                    values.put(SchemaDB.Table.Cols.COMENT, dann.getComent());
//
//                    db.insert(nameDB, null, values);
//                }
//
//
//            } while (cursorCard.moveToNext());
//            cursorCard.close();
//        } else {
//            return;
//        }
//
//        db.delete(table, null, null);
//        for (DannieCard da : cardList) {
//            db.execSQL("drop table " + da.getNameTable());
//        }
//        for (DannieCard da : newcardList) {
//
//            ContentValues contentValues = new ContentValues();
//
//            contentValues.put(SchemaDB.Table.NAME_DETAIL_ARHIVE, da.getNameTable());
//            contentValues.put(SchemaDB.Table.Cols.NAME_FOR_TABLE_CARD, da.getNameCard());
//
//            db.insert(table, null, contentValues);
//        }
//    }


}
