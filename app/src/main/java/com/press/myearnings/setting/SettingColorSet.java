package com.press.myearnings.setting;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.widget.TextView;

/**
 * Created by PRESS on 13.12.2017.
 */

public class SettingColorSet {


    public static void getTextColor(SharedPreferences sp, TextView v, ViewName viewName) {
        String key = null;
        switch (viewName) {
            case idTitle:
                key = SettingAppearance.KEY_ID_TITLE;
                break;
            case summaTitle:
                key = SettingAppearance.KEY_SUMMA_TITLE;
                break;
            case avansTitle:
                key = SettingAppearance.KEY_AVANS_TITLE;
                break;
            case dateTitle:
                key = SettingAppearance.KEY_DATE_TITLE;
                break;
            case noteTitle:
                key = SettingAppearance.KEY_ZAMETKA_TITLE;
                break;

            case id:
                key = SettingAppearance.KEY_ID;
                break;
            case summa:
                key = SettingAppearance.KEY_SUMMA;
                break;
            case avans:
                key = SettingAppearance.KEY_AVANS;
                break;
            case date:
                key = SettingAppearance.KEY_DATE;
                break;
            case note:
                key = SettingAppearance.KEY_ZAMETKA;
                break;

            case name:
                key = SettingAppearance.KEY_NAME_CARD;
                break;
            case dateCard:
                key = SettingAppearance.KEY_DATE_CARD;
                break;
            case summaCard:
                key = SettingAppearance.KEY_SUMMA_CARD;
                break;
            case avansCard:
                key = SettingAppearance.KEY_AVANS_CARD;
                break;
            case ostatok:
                key = SettingAppearance.KEY_OSTATOK;
                break;


//            case summaToday:
//                key = SettingAppearance.KEY_SUMMA_TODAY;
//                break;
//            case avansToday:
//                key = SettingAppearance.KEY_AVANS_TODAY;
//                break;
//            case ostatokToday:
//                key = SettingAppearance.KEY_OSTATOK_TODAY;
//                break;


            case summaCardTitle:
                key = SettingAppearance.KEY_SUMMA_CARD_TITLE;
                break;
            case avansCardTitle:
                key = SettingAppearance.KEY_AVANS_CARD_TITLE;
                break;
            case ostatokTitle:
                key = SettingAppearance.KEY_OSTATOK_TITLE;
                break;
        }
        if (sp.contains(key)) {
            v.setTextColor(setColor(key, sp));
        }
    }

    public static int getToolbarTitleColor(SharedPreferences pref) {
        return pref.getInt(SettingAppearance.KEY_NAME_CARD, Color.WHITE);
    }

    public static void setTextColor(SharedPreferences sharedPreferences, TextView v, ViewName viewName) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        int color = v.getCurrentTextColor();
        String key = null;
        switch (viewName) {
            case idTitle:
                key = SettingAppearance.KEY_ID_TITLE;
                break;
            case summaTitle:
                key = SettingAppearance.KEY_SUMMA_TITLE;
                break;
            case avansTitle:
                key = SettingAppearance.KEY_AVANS_TITLE;
                break;
            case dateTitle:
                key = SettingAppearance.KEY_DATE_TITLE;
                break;
            case noteTitle:
                key = SettingAppearance.KEY_ZAMETKA_TITLE;
                break;

            case id:
                key = SettingAppearance.KEY_ID;
                break;
            case summa:
                key = SettingAppearance.KEY_SUMMA;
                break;
            case avans:
                key = SettingAppearance.KEY_AVANS;
                break;
            case date:
                key = SettingAppearance.KEY_DATE;
                break;
            case note:
                key = SettingAppearance.KEY_ZAMETKA;
                break;

            case name:
                key = SettingAppearance.KEY_NAME_CARD;
                break;
            case dateCard:
                key = SettingAppearance.KEY_DATE_CARD;
                break;
            case summaCard:
                key = SettingAppearance.KEY_SUMMA_CARD;
                break;
            case avansCard:
                key = SettingAppearance.KEY_AVANS_CARD;
                break;
            case ostatok:
                key = SettingAppearance.KEY_OSTATOK;
                break;


//            case summaToday:
//                key = SettingAppearance.KEY_SUMMA_TODAY;
//                break;
//            case avansToday:
//                key = SettingAppearance.KEY_AVANS_TODAY;
//                break;
//            case ostatokToday:
//                key = SettingAppearance.KEY_OSTATOK_TODAY;
//                break;


            case summaCardTitle:
                key = SettingAppearance.KEY_SUMMA_CARD_TITLE;
                break;
            case avansCardTitle:
                key = SettingAppearance.KEY_AVANS_CARD_TITLE;
                break;
            case ostatokTitle:
                key = SettingAppearance.KEY_OSTATOK_TITLE;
                break;
        }
        editor.putInt(key, color).apply();
    }

    private static int setColor(String key, SharedPreferences sp) {
        return sp.getInt(key, Color.WHITE);
    }


    public enum ViewName {
        idTitle, summaTitle, avansTitle, dateTitle, noteTitle,
        id, summa, avans, date, note,
        name, dateCard, summaCard, avansCard, ostatok,
        summaCardTitle, avansCardTitle, summaToday, avansToday, ostatokToday, ostatokTitle
    }
}
