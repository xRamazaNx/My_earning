package com.press.myearnings.model;

public abstract class DannieDefault {
    public int type = 0;
    private String summa;
    private String avans;
    private String ostatok;

    public String getSumma() {
        return summa;
    }

    public void setSumma(String summa) {
        this.summa = summa;
    }

    public String getAvans() {
        return avans;
    }

    public void setAvans(String avans) {
        this.avans = avans;
    }

    public String getOstatok() {
        return ostatok;
    }

    public void setOstatok(String ostatok) {
        this.ostatok = ostatok;
    }

}
