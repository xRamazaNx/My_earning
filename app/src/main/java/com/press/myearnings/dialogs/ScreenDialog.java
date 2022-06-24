package com.press.myearnings.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.press.myearnings.R;
import com.press.myearnings.StaticUtil;

import java.io.File;
import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

public class ScreenDialog extends DialogFragment {

    private static final String SCREEN_KEY = "screenkey";

    public static ScreenDialog createScreenDialog(String screenFilePath) {
        ScreenDialog screenDialog = new ScreenDialog();
        Bundle args = new Bundle();
        args.putString(SCREEN_KEY, screenFilePath);
        screenDialog.setArguments(args);
        return screenDialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        assert getArguments() != null;
        final File pic = getFilePic();
        FragmentActivity activity = requireActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        ScrollView layout = (ScrollView) activity.getLayoutInflater().inflate(R.layout.screen_dialog, null);
        ImageView screen = layout.findViewById(R.id.screen);
        Glide.with(this)
                .load(pic)
                .into(screen);

        builder.setPositiveButton("Отправить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("image/*");
                Uri uri = FileProvider.getUriForFile(activity, "com.a4165816.press.earning.provider", pic);
                intent.putExtra(Intent.EXTRA_STREAM, uri);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("Сохранить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (!StaticUtil.permissionCheck(getActivity())){
                    return;
                }
                saveImage(pic);
            }
        });

        builder.setView(layout);
        return builder.create();
    }

    private File getFilePic() {
        String filePath = requireArguments().getString(SCREEN_KEY);
        return new File(filePath);
    }

    private void saveImage(File pic) {
        String myEarning = Environment.getExternalStoragePublicDirectory("MyEarning").getPath();
        File directory = new File(myEarning);
        directory.mkdir();
        File file = new File(myEarning +"/"+pic.getName());
        try {
            StaticUtil.copyFileUsingStream(pic, file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            Toast.makeText(getActivity(), "Скрин сохранен в папке MyEarning", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 777) {
            if (grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getActivity(), "Отказано в доступе!", Toast.LENGTH_SHORT).show();
            } else {
                for (int grantResult : grantResults) {
                    if (grantResult == PackageManager.PERMISSION_GRANTED) {
                        saveImage(getFilePic());
                        return;
                    }
                }
            }
        }
    }
}
