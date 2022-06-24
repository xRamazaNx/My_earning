package com.press.myearnings.model;

import android.annotation.SuppressLint;
import android.text.format.DateFormat;

import java.util.Calendar;

/**
 * Created by PRESS on 03.09.2017.
 */

@SuppressLint("Registered")
public class Dannie extends DannieDefault{
    public int position = 0; //  для позиции в списке, постоянно меняется
    private final Calendar calendar;
//    private int[] dateInt;
    private String coment;
    private long dateMl;
    private int ID = 0;
    private final String[] sDayOfWeek = {"вс, ", "пн, ", "вт, ", "ср, ", "чт, ", "пт, ", "сб, "};

    public Dannie() {

        calendar = Calendar.getInstance();
        dateMl = calendar.getTimeInMillis();

//        int day = calendar.get(Calendar.DAY_OF_MONTH);
//        int month = calendar.get(Calendar.MONTH);
//        int year = calendar.get(Calendar.YEAR);
//
//        dateInt = new int[]{year, month, day};
    }

    public long getDateMl() {
        return dateMl;
    }

    public void setDateMl(long dateMl) {
        this.dateMl = dateMl;
        calendar.setTimeInMillis(dateMl);
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

//    public void setDateInt(int day, int month, int year) {
//
//        dateInt[0] = year;
//        dateInt[1] = month;
//        dateInt[2] = day;
//
//        calendar.set(dateInt[0], dateInt[1], dateInt[2]);
//    }

//    private void updateDateMl() {
//        calendar.set(dateInt[0], dateInt[1], dateInt[2]);
//        dateMl = calendar.getTimeInMillis();
//    }

//    public void setDateInt(int[] dateInt) {
//        this.dateInt = dateInt;
//        calendar.set(dateInt[0], dateInt[1], dateInt[2]);
//    }

//    public int[] getDateIntArray() {
//        return dateInt;
//    }

//    public int getDateInt() { //
//
//        String year = String.valueOf(dateInt[0]);
//        String month = String.valueOf(dateInt[1]);
//        String day = String.valueOf(dateInt[2]);
//
//        if (month.length() < 2) {
//            month = "0" + month;
//        }
//        if (day.length() < 2) {
//            day = "0" + day;
//        }
//
//        String dateForSort = year + month + day;
//        return Integer.parseInt(dateForSort);
//    }

    //колличетсво рулонов 1


    // дата
    public String getDate(int variantDate) {
        String dayName = sDayOfWeek[calendar.get(Calendar.DAY_OF_WEEK) - 1];
        String timeFormat = "";
        switch (variantDate) {
            case 0:
                timeFormat = "dd.MM.yy";
                break;
            case 1:
                timeFormat = "dd.MM.yy kk:mm";
                break;
            case 2:
                timeFormat = dayName + "dd.MM.yy";
                break;
            case 3:
                timeFormat = dayName + "dd.MM.yy kk:mm";
                break;
            case 4:
                timeFormat = "dd.MM.yyyy";
                break;
            case 5:
            case -2:
                timeFormat = "dd.MM.yyyy kk:mm";
                break;
            case 6:
                timeFormat = dayName + "dd.MM.yyyy";
                break;
            case 7:
                timeFormat = dayName + "dd.MM.yyyy kk:mm";
                break;
            case 8:
                timeFormat = "dd MMMM yyyy";
                break;
            case 9:
                timeFormat = "dd MMMM yyyy kk:mm";
                break;
            case -1:
                timeFormat = "dd.MM.yyyy_kk-mm-ss";
                break;
        }
        return DateFormat.format(timeFormat, calendar.getTime()).toString();
    }



    public String getComent() {
        return coment;
    }

    public void setComent(String coment) {
        this.coment = coment;
    }
}
