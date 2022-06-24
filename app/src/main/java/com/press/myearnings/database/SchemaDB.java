package com.press.myearnings.database;

/**
 * Created by PRESS on 27.09.2017.
 */

public class SchemaDB {
    public static final class Name {
        public static final String PREFERENCES = "preferences";

        public static final class Item {
            public static final String RADIO_GROUP = "radiogroup";
            public static final String RADIO_GROUP_CARD = "radiogroupcard";

            public static final String RADIO_METOD = "radiometod";
            public static final String RADIO_METOD_CARD = "radiometodcard";

            public static final String SAVESWITCHSORT = "saveswitchsort";
            public static final String SAVESWITCHSORT_CARD = "saveswitchsortcard";

            public static final String SIZE_CARD_ITEM = "sizecarditem";
            public static final String DATE_SORT_SWITCH = "datesortswitch";
//            public static final String SIZE_ARHIVE_ITEM = "sizearhiveitem";
//
//            public static final String ADD_RUBLE_SYMBOL = "addrublesymbol";
//            public static final String THEME_SWITCH = "themeswitch";
        }

    }

    public static final class Table {
        //        public static final String NAME_ZAPISI = "ZAPISI";
        public static final String NAME_CARD_OLD_DB = "CARDZAPISI";
        public static final String NAME_CARD = "ACTIVE";
        public static final String NAME_ARHIVE_OLD_DB = "ARHIVEZAPISI";
        public static final String NAME_ARHIVE = "ARCHIVE";
        public static final String NAME_DETAIL_ARHIVE = "ARHIVEDETAILZAPISI";
        public static final String NAME_TABLE_DATA = "TABLEDATA";
        //        public static final String NAME_INDEX_ARHIVE_ITEM = "INDEXARHIVEITEM ";
        //для сохранения колличества строк в таблице записи
        public static final String OLD_SIZE_ITEM = "oldsizeitem";
        public static final String COUNTER = "counter";
        public static final String IS_ARCHIVE = "ISARCHIVE";

        public static final class Cols {
            public static final String ID = "_id";
            public static final String ID_MASSIV = "id_massive";
            public static final String NAME_FOR_TABLE_CARD = "id_name";

            public static final String RUL1 = "rul1";
            public static final String SUMMA = "summa";
            public static final String AVANS = "avans";
//            public static final String DATE = "date";
            public static final String DATE_LONG = "datelong";
            public static final String COMENT = "coment";
            public static final String DATE_CREATE_TABLE = "datecreated";
            public static final String DATE_MODIFY_TABLE = "datemodify";



            // для восстановления базы данных
            public static final String DATE_YEAR = "dateyear";
            public static final String DATE_MONTH = "datemonth";
            public static final String DATE_DAY = "dateday";
        }
    }

    public static String createTableArhive(String number) {
        return "create table " + Table.NAME_TABLE_DATA + number + " (" +
                Table.Cols.ID + " integer primary key autoincrement, " +
                Table.Cols.SUMMA + ", " +
                Table.Cols.AVANS + ", " +
                Table.Cols.DATE_LONG + ", " +
                Table.Cols.COMENT +
                ");";

    }

    public static String createTable(String fullName) {
        return "create table " + fullName + " (" +
                Table.Cols.ID + " integer primary key autoincrement, " +
                Table.Cols.SUMMA + ", " +
                Table.Cols.AVANS + ", " +
                Table.Cols.DATE_LONG + ", " +
                Table.Cols.COMENT +
                ");";

    }
}
