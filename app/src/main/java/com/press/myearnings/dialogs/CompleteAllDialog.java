package com.press.myearnings.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;

import com.press.myearnings.R;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

/**
 * Created by PRESS on 25.11.2017.
 */

public class CompleteAllDialog extends DialogFragment {

    Handler handler;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(requireActivity());
        alertDialog.setMessage(R.string.complete_all);
        alertDialog.setPositiveButton(R.string.OK, (dialogInterface, i) -> {
            handler.sendEmptyMessage(5);
            dialogInterface.cancel();
        });
        alertDialog.setNegativeButton(R.string.CANCEL, (dialogInterface, i) -> dialogInterface.cancel());

        return alertDialog.create();
    }
    public void setHandler(Handler ha){
        handler = ha;
    }

}
