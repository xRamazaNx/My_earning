package com.press.myearnings.model;

public class CopyData {
    private static CopyData copyData;
    public Dannie copy;


    public static CopyData getCopy(){
        if (copyData == null)
            copyData = new CopyData();
        return  copyData;
    }

    public void removeCopy() {
        copy = null;
        copyData = null;
    }
}
