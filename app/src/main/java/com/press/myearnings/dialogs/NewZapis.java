package com.press.myearnings.dialogs;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.press.myearnings.R;
import com.press.myearnings.ZarplataActivity;
import com.press.myearnings.model.Dannie;
import com.press.myearnings.setting.ColumnName;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Calendar;
import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;

public class NewZapis extends AppCompatActivity {
    public static final String INDEX_OF_FOCUS = "index-of_focus";
    public static final String KEY_SUMMA = "summa";
    public static final String KEY_AVANS = "avans";
    public static final String KEY_COMENT = "coment";
    public static final String KEY_DATE = "date";
    public static final String KEY_ID = "_id";
    public static final String KEY_IS_EDIT = "edit";
    Dannie dannie;
    SharedPreferences pref;
    int varVisDate = 0;
    boolean isButtonOk;
    private EditText editText1;
    private EditText editAvans;
    private TextView editDate;
    private EditText editComent;
    private int key;

    // превращает строку в цифры если это возможно
    private float parseStringToNumber(String number) {
        DecimalFormat decimalFormat = new DecimalFormat("0.##", DecimalFormatSymbols.getInstance(Locale.US));
        decimalFormat.setMaximumFractionDigits(3);
        decimalFormat.setRoundingMode(RoundingMode.HALF_EVEN);
        decimalFormat.setGroupingUsed(false);
        // некоторая логика для точек с запятыми
        if (number.length() == 0)
            number = "0";
        try {
            double d = Double.parseDouble(number);
            return Float.parseFloat(decimalFormat.format(d));

        } catch (NumberFormatException n) {
            return Float.parseFloat(decimalFormat.format(0));
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_zapis);
        dannie = new Dannie();
        @SuppressLint("HandlerLeak") final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 0) {
                    DecimalFormat numberFormat = (DecimalFormat) DecimalFormat.getNumberInstance(Locale.US);
                    numberFormat.setGroupingUsed(false);
                    numberFormat.setRoundingMode(RoundingMode.HALF_EVEN);

                    String val = (String) msg.obj;
                    if (val.length() > 0) {
                        double d = Double.parseDouble(val);
                        val = numberFormat.format(d);
                    }
                    if (editText1.isFocused()) editText1.setText(val);
                    if (editAvans.isFocused()) editAvans.setText(val);
                    if (editComent.isFocused()) {
                        editComent.getText().insert(editComent.getSelectionStart(), val);
                    }
                }
            }
        };
        pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        varVisDate = Integer.parseInt(pref.getString(getString(R.string.date_pref_for_zapisi), "1"));

        getWindow().setGravity(Gravity.TOP);
        TextView buttonOk = findViewById(R.id.id_button_new_zapis_OK);
        TextView buttonCancel = findViewById(R.id.id_button_new_zapis_cansel);
        TextView calcRun = findViewById(R.id.calc_run);
        calcRun.setOnClickListener(view -> {

            String val = null;
            if (editText1.isFocused()) val = editText1.getText().toString();
            if (editAvans.isFocused()) val = editAvans.getText().toString();
            if (editComent.isFocused()) val = "";
            CalcDialog dialog = CalcDialog.createCalcDialog(val);
            dialog.setReturnVal(handler);
            dialog.show(getSupportFragmentManager(), "calc");
        });

        onClickNewZapisResult(buttonOk);
        onClickNewZapisResult(buttonCancel);

        Intent intent = getIntent();
        editText1 = findViewById(R.id.id_edit_new_zapis_1);
        editText1.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_NEXT) {
                editAvans.requestFocus();
                editAvans.setSelection(editAvans.getText().length());
                return true;
            }
            return false;
        });
        editAvans = findViewById(R.id.id_edit_new_avans);
        editDate = findViewById(R.id.id_edit_new_date);
        editComent = findViewById(R.id.id_comment);

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        if (metrics.heightPixels < 900) {
            editDate.setTextSize(12);
        }
        editDate.setOnClickListener(view -> setData(false));


        if (intent.getBooleanExtra(KEY_IS_EDIT, false)) {
            key = intent.getIntExtra(KEY_ID, 0);

            String summ = intent.getStringExtra(KEY_SUMMA);
            if (!summ.equals("")) {
                editText1.setText(summ);
            }

            String avans = intent.getStringExtra(KEY_AVANS);
            if (!avans.equals("")) {
                editAvans.setText(avans);
            }

            dannie.setDateMl(intent.getLongExtra(KEY_DATE, 0));
            editDate.setText(dannie.getDate(varVisDate));

            editComent.setText(intent.getStringExtra(KEY_COMENT));
            int indexOfEdit = getIntent().getIntExtra(INDEX_OF_FOCUS, 1);
            switch (indexOfEdit) {
                case 1:
                    focusCursor(editText1);
                    break;
                case 2:
                    focusCursor(editAvans);
                    break;
                case 3:
                    setData(true);
                    break;
                case 4:
                    focusCursor(editComent);
                    break;
            }
        } else {
            editDate.setText(dannie.getDate(varVisDate));
            focusCursor(editText1);
        }

        String nameTable = intent.getStringExtra(ZarplataActivity.NAME_TABLE);
        setTitleNames(nameTable);

    }

    private void setTitleNames(String nameTable) {
        ColumnName columnName = ColumnName.getColumnName(this, nameTable);
        TextView summa = findViewById(R.id.title_new_zapis_summa);
        TextView avans = findViewById(R.id.title_new_zapis_avans);
        TextView date = findViewById(R.id.title_new_zapis_date);
        TextView note = findViewById(R.id.title_new_zapis_note);

        summa.setText(columnName.summa);
        avans.setText(columnName.avans);
        date.setText(columnName.date);
        note.setText(columnName.note);
    }

    private void focusCursor(EditText editText) {
        editText.requestFocus();
        editText.setSelection(editText.getText().length());
        Window window = this.getWindow();
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING |
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    public void onClickNewZapisResult(View view) {
        view.setOnClickListener(view1 -> {

            if (view1.getId() == R.id.id_button_new_zapis_OK) {

                String rul1 = editText1.getText().toString();
                String avans1 = editAvans.getText().toString();
                float z1;
                float avans;

//                    if (!rul1.equals("") &&
//                            !rul1.equals(".") &&
//                            !rul1.equals(" ") &&
//                            !rul1.equals("-"))
//                        z1 = Float.parseFloat(rul1);
//                    if (!avans1.equals("") &&
//                            !avans1.equals(".") &&
//                            !avans1.equals(" ") &&
//                            !avans1.equals("-"))
//                        avans = Float.parseFloat(avans1);
                z1 = parseStringToNumber(rul1);
                avans = parseStringToNumber(avans1);
                String coment = editComent.getText().toString();

                boolean rul = rul1.equals("");
                boolean av = avans1.equals("");
                boolean com = coment.equals("");

                if (rul && av && com
                        || z1 == 0 && avans == 0 && com) {
                    Toast.makeText(NewZapis.this, R.string.not_data, Toast.LENGTH_SHORT).show();
                } else {

                    rul1 = String.valueOf(z1);
                    avans1 = String.valueOf(avans);

                    Intent data = new Intent();

                    data.putExtra(KEY_SUMMA, rul1);
                    data.putExtra(KEY_AVANS, avans1);
                    data.putExtra(KEY_DATE, dannie.getDateMl());
                    data.putExtra(KEY_COMENT, coment);
                    data.putExtra(KEY_ID, key);

                    setResult(RESULT_OK, data);
                    isButtonOk = true;
                    finish();

                }
            } else {
                setResult(RESULT_CANCELED);
                isButtonOk = false;
                finish();
            }
        });

    }

    private void setData(final boolean isFinish) {
        final Calendar c = Calendar.getInstance();
        c.setTimeInMillis(dannie.getDateMl());
        DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
//                Calendar c = Calendar.getInstance();
                c.set(i, i1, i2);
//                String dayName = sDayOfWeek[c.get(Calendar.DAY_OF_WEEK) - 1];
//                String year = String.valueOf(i);
//                String month = String.valueOf(i1 + 1);
//                if (month.length() < 2) {
//                    month = "0" + month;
//                }
//                String dayNum = String.valueOf(i2);
//                if (dayNum.length() < 2) {
//                    dayNum = "0" + dayNum;
//                }
//
//                date = String.format("%s%s.%s.%s", dayName, dayNum, month, year);
//                dannie.setDateForArchive(date);
//                dateSetForDatepicker();

                dannie.setDateMl(c.getTimeInMillis());

//                Log.d("dateVnewZapis", "god "+i+" mesac "+ i1+ " day "+ i2);
                editDate.setText(dannie.getDate(varVisDate));

                if (isFinish) {
                    String z1 = editText1.getText().toString();
                    String avans = editAvans.getText().toString();
                    String coment = editComent.getText().toString();

                    Intent data = new Intent();

                    data.putExtra(KEY_SUMMA, z1);
                    data.putExtra(KEY_AVANS, avans);
                    data.putExtra(KEY_DATE, dannie.getDateMl());
                    data.putExtra(KEY_COMENT, coment);
                    data.putExtra(KEY_ID, key);
                    setResult(RESULT_OK, data);

                    finish();
                }

            }
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        overridePendingTransition(R.anim.anim_open_new_zapis, R.anim.anim_open_new_zapis);
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    @Override
    public void finish() {
        super.finish();

    }
}
