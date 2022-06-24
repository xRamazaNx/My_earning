package com.press.myearnings.model;

import com.press.myearnings.setting.ColumnName;

/**
 * Created by PRESS on 09.10.2017.
 */

public class DannieCard extends DannieDefault {

    private String nameCard;
    private String nameTable;
    private String dateForArchive;
    private String dateForCreated;
    private String dateForModify;

    public ColumnName columnName;
    public boolean isArchive;

    public DannieCard(){
        columnName = new ColumnName();
    }

    public String getNameCard() {
        return nameCard;
    }

    public String getNameTable() {
        return nameTable;
    }

    public void setNameTable(String nameTable) {
        this.nameTable = nameTable;
    }

    public void setNameCard(String nameCard) {
        this.nameCard = nameCard;
    }

    public String getDateForArchive() {
        return dateForArchive;
    }

    public void setDateForArchive(String dateForArchive) {
        this.dateForArchive = dateForArchive;
    }

    public String getDateForCreated() {
        return dateForCreated;
    }

    public void setDateForCreated(String dateForCreated) {
        this.dateForCreated = dateForCreated;
    }

    public String getDateForModify() {
        return dateForModify;
    }

    public void setDateForModify(String dateForModify) {
        this.dateForModify = dateForModify;
    }
}
