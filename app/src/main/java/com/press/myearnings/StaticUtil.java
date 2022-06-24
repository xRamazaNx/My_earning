package com.press.myearnings;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.TypedValue;
import android.widget.Toast;

import com.press.myearnings.dialogs.NewZapis;
import com.press.myearnings.model.Dannie;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Locale;

public class StaticUtil {
    public static Intent getIntentOfShowEditWin(Context context, Dannie d, int viewToLongClick) { // на какой вью нажали долго чтоб переключить фокус
        DecimalFormat nf = (DecimalFormat) DecimalFormat.getNumberInstance(Locale.US);
        nf.setMaximumFractionDigits(2);
        nf.setRoundingMode(RoundingMode.HALF_EVEN);
        nf.setGroupingUsed(false);

        String summa = d.getSumma();
        String avans = d.getAvans();
        float summaF = 0;
        float avansF = 0;
        if (summa.length() > 0)
            summaF = Float.parseFloat(d.getSumma());
        if (avans.length() > 0)
            avansF = Float.parseFloat(d.getAvans());

        if (summaF > 0)
            summa = nf.format(summaF);
        if (avansF > 0)
            avans = nf.format(avansF);
        String sComent = d.getComent();

        Intent intent = new Intent(context, NewZapis.class);
        intent.putExtra(NewZapis.KEY_ID, d.getID());
        intent.putExtra(NewZapis.KEY_SUMMA, summa);
        intent.putExtra(NewZapis.KEY_AVANS, avans);
        intent.putExtra(NewZapis.KEY_DATE, d.getDateMl());
        intent.putExtra(NewZapis.KEY_COMENT, sComent);
        intent.putExtra(NewZapis.INDEX_OF_FOCUS, viewToLongClick);
        intent.putExtra(NewZapis.KEY_IS_EDIT, true);

        return intent;
    }
    public static int convertDpToPixels(float dp, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }
    public static void copyFileUsingStream(File source, File dest) throws IOException {
        InputStream is = null;
        OutputStream os = null;
        try {
            is = new FileInputStream(source);
            os = new FileOutputStream(dest);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        } finally {
            assert is != null;
            is.close();
            assert os != null;
            os.close();
        }
    }
    public static boolean permissionCheck(Activity activity) {
        int perm = activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (perm != PackageManager.PERMISSION_GRANTED) {
            if (activity.shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Toast.makeText(activity, "Включите разрешение памяти в настройках приложения"
                        , Toast.LENGTH_SHORT).show();
            } else {
                activity.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 777);
            }
            return false;
        }
        return true;
    }
}
