package com.press.myearnings;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class LockActivity extends AppCompatActivity {
    public static final String TAG = "LockActivity";
    public static final String USER_PASS = "password";

    protected TextView tvMessage;
    public String userPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        userPass = PreferenceManager.getDefaultSharedPreferences(this).getString(LockActivity.USER_PASS, "");
        if (userPass.equals(""))
            finish();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_basscode);

        tvMessage = findViewById(R.id.tv_message);
        EditText codeField = findViewById(R.id.code_field);

        codeField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (userPass.equals(s.toString())) {
                    finish();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        // back to home screen
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        this.startActivity(intent);
//        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING |
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }
}
