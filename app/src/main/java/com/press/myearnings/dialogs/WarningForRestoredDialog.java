package com.press.myearnings.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;

/**
 * Created by PRESS on 25.01.2018.
 */

public class WarningForRestoredDialog extends DialogFragment {
    private String title = null;
    private String messege = null;
    private String positiveMes = null;
    private String negativeMes = null;
    private View view = null;

    public void setOnClickPositiveButton(OnClickPositiveButton onClickPositiveButton) {
        this.onClickPositiveButton = onClickPositiveButton;
    }

    OnClickPositiveButton onClickPositiveButton;
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setView(view);
        alertDialog.setTitle(title);
        alertDialog.setMessage(messege);
        alertDialog.setPositiveButton(positiveMes, (dialogInterface, i) -> onClickPositiveButton.isPositiveButtonClick());
        alertDialog.setNegativeButton(negativeMes, (dialog, which) -> dialog.cancel());
        return alertDialog.create();

    }

    public interface OnClickPositiveButton{
        public void isPositiveButtonClick();
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setMessege(String messege) {
        this.messege = messege;
    }

    public void setPositiveMes(String positiveMes) {
        this.positiveMes = positiveMes;
    }

    public void setNegativeMes(String negativeMes) {
        this.negativeMes = negativeMes;
    }

    public void setView(View view) {
        this.view = view;
    }
}
