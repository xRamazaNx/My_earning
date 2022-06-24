package com.press.myearnings.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.press.myearnings.R;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

/**
 * Created by PRESS on 26.11.2017.
 */

public class InputNameCardDialog extends DialogFragment {
    private static final String NAME = "name_card";
    private String name;
    private EditText editText;
    private OkClickListener okClickListener;

    public static InputNameCardDialog get(@NonNull String name, OkClickListener okClickListener) {
        InputNameCardDialog dialog = new InputNameCardDialog();
        dialog.setName(name);
        dialog.setOkClickListener(okClickListener);

        return dialog;
    }

    private void setName(String name) {
        this.name = name;
    }

    private void setOkClickListener(OkClickListener okClickListener) {
        this.okClickListener = okClickListener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        LayoutInflater layoutInflaterCompat = LayoutInflater.from(getActivity());
        LinearLayout linearLayout = (LinearLayout) layoutInflaterCompat.inflate(R.layout.set_name_card, null);

        TextView ok = linearLayout.findViewById(R.id.id_button_set_name_ok);
        TextView cancel = linearLayout.findViewById(R.id.id_button_set_name_cansel);
        editText = linearLayout.findViewById(R.id.id_name_card_edittext);
        if (name != null) {
            editText.setText(name);
            editText.setSelection(name.length());
        } else name = "";

        View.OnClickListener onClickListener = view -> {
            if (view.getId() == R.id.id_button_set_name_ok) {
                name = editText.getText().toString();
                okClickListener.click(name);
            }
            Objects.requireNonNull(getDialog()).cancel();
        };
        ok.setOnClickListener(onClickListener);
        cancel.setOnClickListener(onClickListener);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setView(linearLayout);

        return builder.create();
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Window window = Objects.requireNonNull(getDialog()).getWindow();
        if (window != null) {
//            window.setBackgroundDrawableResource(R.drawable.shape_sort);
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING |
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
//            window.setWindowAnimations(R.style.ThemeOverlay_AppCompat);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public interface OkClickListener {
        public void click(String name);
    }
}
