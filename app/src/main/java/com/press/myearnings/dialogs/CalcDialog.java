package com.press.myearnings.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.press.myearnings.R;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Locale;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

/**
 * Created by PRESS on 10.01.2018.
 */

public class CalcDialog extends DialogFragment {

    private TextView textView;
    private DecimalFormat numberFormat;

    private Handler returnVal;
    private String text = "";
    private StringBuilder tempOperandOne = new StringBuilder();
    private StringBuilder tempOperandTwo = new StringBuilder();
    private static final String KEY_CALC_TEXT = "CALC_TEXT";
    private static final String KEY_HANDLER = "HANDLER";

    private String operationOne = "";
    private String operationtwo;
    private double operandOne = 0;
    private double operandTwo = 0;
    private double result = 0;
    private boolean isOperationAdd = true;
    private LinearLayout layout;
    private Button[] button;

    DisplayMetrics metrics;


    public static CalcDialog createCalcDialog(String text) {
        CalcDialog calcDialog = new CalcDialog();
        Bundle args = new Bundle();
        args.putString(KEY_CALC_TEXT, text);
        calcDialog.setArguments(args);
        return calcDialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        metrics = getResources().getDisplayMetrics();
        numberFormat = (DecimalFormat) DecimalFormat.getNumberInstance(Locale.US);
        numberFormat.setGroupingUsed(false);
//        numberFormat.setMaximumFractionDigits(2);
        numberFormat.setRoundingMode(RoundingMode.HALF_EVEN);

        LayoutInflater inflater = LayoutInflater.from(getActivity());
        layout = (LinearLayout) inflater.inflate(R.layout.calculator, null);
        String val = requireArguments().getString(KEY_CALC_TEXT);
        if (val != null && val.length() > 0) {
            operandOne = Double.parseDouble(val);
            tempOperandOne.append(numberFormat.format(operandOne));
        }

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity(), R.style.transperent_calc);
        builder.setView(calcView());
//        builder.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                Message m = new Message();
//                m.what = 0;
//                m.obj = String.valueOf(result);
//                returnVal.sendMessage(m);
//            }
//        });
        return builder.create();
    }

    private LinearLayout calcView() {

        View.OnClickListener clickOperator = this::operatonMetod;

        View.OnClickListener clickNumber = view -> {
            String buttonText = ((Button) view).getText().toString();
//            String lastChar = "";
//            if (text.length() > 0) {
//                lastChar = text.substring(text.length() - 1);
//            }
//            if (buttonText.equals("+") || buttonText.equals("-") || buttonText.equals("/") || buttonText.equals("*")) {
//
//                if (text.length() == 0) return;
//                if (lastChar.equals("+") || lastChar.equals("-") || lastChar.equals("/") || lastChar.equals("*")) {
//                    text = text.substring(0, text.length() - 1);
//                }
//
//                for (int i = 0; i < text.length(); i++) {
//                    if (text.charAt(i) == '+' || text.charAt(i) == '-' || text.charAt(i) == '/' || text.charAt(i) == '*'){
//                        char operator = text.charAt(i);
//                        equalsNumber();
//
//                    }
//                }
//            }

            if (isOperationAdd) {
                if (buttonText.equals(".")) {

                    for (int i = 0; i < tempOperandOne.length(); i++) {
                        boolean tempOneBool = tempOperandOne.charAt(i) == '.';
                        if (tempOneBool) return;
                    }
                }
                tempOperandOne.append(buttonText);
            } else {
                if (buttonText.equals(".")) {
                    for (int i = 0; i < tempOperandTwo.length(); i++) {
                        boolean tempTwoBool = tempOperandTwo.charAt(i) == '.';
                        if (tempTwoBool) return;
                    }
                }
                tempOperandTwo.append(buttonText);
            }
            updateText();
        };
        View.OnClickListener clickFormating = view -> {
            int id = view.getId();
            if (id == R.id.delete_button) {
                if (tempOperandTwo.length() > 0) {
                    tempOperandTwo.deleteCharAt(tempOperandTwo.length() - 1);
                } else {
                    if (!operationOne.equals("")) {
                        operationOne = "";
                        isOperationAdd = true;
                    } else {
                        if (tempOperandOne.length() > 0) {
                            tempOperandOne.deleteCharAt(tempOperandOne.length() - 1);
                        }
                    }
                }
            } else if (id == R.id.clear_button) {
                tempOperandOne = new StringBuilder();
                tempOperandTwo = new StringBuilder();
                operationOne = "";
                operationtwo = "";
                operandOne = 0;
                operandTwo = 0;
                result = 0;
                isOperationAdd = true;
            }
            updateText();
        };

        textView = layout.findViewById(R.id.calc_text);
        updateText();

        Button zero = layout.findViewById(R.id.zero_button);
        Button one = layout.findViewById(R.id.one_button);
        Button two = layout.findViewById(R.id.two_button);
        Button three = layout.findViewById(R.id.three_button);
        Button four = layout.findViewById(R.id.four_button);
        Button five = layout.findViewById(R.id.five_button);
        Button six = layout.findViewById(R.id.six_button);
        Button seven = layout.findViewById(R.id.seven_button);
        Button eitgh = layout.findViewById(R.id.eight_button);
        Button nine = layout.findViewById(R.id.nine_button);

        Button point = layout.findViewById(R.id.point_button);

        Button subtraction = layout.findViewById(R.id.subtraction_button);
        Button summ = layout.findViewById(R.id.sum_button);
        Button multiplication = layout.findViewById(R.id.multiplication_button);
        Button divider = layout.findViewById(R.id.divider_button);
        final Button equals = layout.findViewById(R.id.equals_button);
        Button procent = layout.findViewById(R.id.procent_button);

        Button delete = layout.findViewById(R.id.delete_button);
        Button clear = layout.findViewById(R.id.clear_button);
        Button ok = layout.findViewById(R.id.result_ok_for_calc);

        button = new Button[]{zero, one, two, three, four, five, six, seven, eitgh, nine, point, equals, subtraction, summ, multiplication, divider, procent, clear, ok, delete};

        ok.setOnClickListener(view -> {
            if (operatonMetod(equals)) {

                Message m = new Message();
                m.what = 0;
                String res = "";
                if (result > 0) {
                    res = String.valueOf(result);
                }
                m.obj = res;
                returnVal.sendMessage(m);
                Objects.requireNonNull(getDialog()).cancel();
            }
        });

        zero.setOnClickListener(clickNumber);
        one.setOnClickListener(clickNumber);
        two.setOnClickListener(clickNumber);
        three.setOnClickListener(clickNumber);
        four.setOnClickListener(clickNumber);
        five.setOnClickListener(clickNumber);
        six.setOnClickListener(clickNumber);
        seven.setOnClickListener(clickNumber);
        eitgh.setOnClickListener(clickNumber);
        nine.setOnClickListener(clickNumber);
        point.setOnClickListener(clickNumber);

        subtraction.setOnClickListener(clickOperator);
        summ.setOnClickListener(clickOperator);
        multiplication.setOnClickListener(clickOperator);
        divider.setOnClickListener(clickOperator);
        equals.setOnClickListener(clickOperator);
        procent.setOnClickListener(clickOperator);

        delete.setOnClickListener(clickFormating);
        clear.setOnClickListener(clickFormating);

        if (metrics.widthPixels > 1000) {
//            layout.getChildAt(1).setLayoutParams(new LinearLayout.LayoutParams(setDpToPx(280, metrics), setDpToPx(350, metrics)));
            textView.setLayoutParams(new LinearLayout.LayoutParams(setDpToPx(65*4, metrics), setDpToPx(55, metrics)));
            textView.setTextSize(30);
            setSizeAllButton(65);
            setTextSizeButton(24);
        } else if (metrics.widthPixels > 700) {
//            layout.getChildAt(1).setLayoutParams(new LinearLayout.LayoutParams(setDpToPx(240, metrics), setDpToPx(300, metrics)));
            textView.setLayoutParams(new LinearLayout.LayoutParams(setDpToPx(55*4, metrics), setDpToPx(50, metrics)));
            textView.setTextSize(24);
            setSizeAllButton(55);
            setTextSizeButton(20);
        }


        return layout;

    }

    private int setDpToPx(int dp, DisplayMetrics metrics) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, metrics);
    }

    private void setTextSizeButton(float size) {
        for (Button b :
                button) {
            if (!b.getText().toString().equals(""))
                b.setTextSize(size);
        }
    }

    private void setSizeAllButton(int size) {
        for (Button b :
                button) {
            b.getLayoutParams().width = (setDpToPx(size, metrics));
            b.getLayoutParams().height = (setDpToPx(size, metrics));
        }
    }


    private boolean operatonMetod(View view) {
        if (text.length() < 3) {
            result = 0;
            return true;
        } else {
            operandOne = Double.parseDouble(tempOperandOne.toString());
            result = operandOne;

        }
        int id = view.getId();
        if (id == R.id.sum_button) {
            operationOne = "+";
        } else if (id == R.id.subtraction_button) {
            operationOne = "-";
        } else if (id == R.id.divider_button) {
            operationOne = "÷";
        } else if (id == R.id.multiplication_button) {
            operationOne = "x";
        } else if (id == R.id.equals_button) {
            operationOne = "=";
        } else if (id == R.id.procent_button) {
            operationOne = "%";
        }


        if (isOperationAdd) {
            isOperationAdd = false;
        } else {

            if (tempOperandTwo.length() > 0) {
                operandTwo = Double.parseDouble(tempOperandTwo.toString());


                switch (operationtwo) {

                    case "+":
                        result = operandOne + operandTwo;
                        break;
                    case "-":
                        result = operandOne - operandTwo;
                        break;
                    case "÷":
                        if (operandTwo != 0) {
                            result = operandOne / operandTwo;
                        } else {
                            Toast.makeText(getActivity(), R.string.divider_zero, Toast.LENGTH_SHORT).show();
                            operationOne = operationtwo;
                            return false;
                        }
                        break;
                    case "x":
                        result = operandOne * operandTwo;
                        break;
                    case "%":
                        result = (operandOne / 100) * operandTwo;
                        break;
                }

                tempOperandOne = new StringBuilder();
                tempOperandTwo = new StringBuilder();
                tempOperandOne.append(numberFormat.format(result));
                operandOne = result;
                operandTwo = 0;
            } else {
                if (operationtwo.equals("%")) {
                    result = operandOne / 100;
                    tempOperandOne = new StringBuilder();
                    tempOperandOne.append(numberFormat.format(result));
                    operandOne = result;
                }
            }
        }
        if (operationOne.equals("=")) {
            operationOne = "";
            isOperationAdd = true;
        }
        operationtwo = operationOne;
        updateText();
        return true;
    }


//    private void equalsNumber() {
//        CharSequence s = text;
//        StringBuilder valTemp = new StringBuilder();
//        ArrayList<Double> numbers = new ArrayList<>();
//        Character operator = null;
//
//        for (int i = 0; i < s.length(); i++) {
//            Character c = s.charAt(i);
//
//            if (!c.equals("+") && !c.equals("-") && !c.equals("/") && !c.equals("*")) {
//                valTemp.append(c);
//                Log.d("calccc", c.toString());
//                if (i == s.length() - 1) {
//                    numbers.add(Double.parseDouble(valTemp.toString()));
//                }
//            } else {
//                numbers.add(Double.parseDouble(valTemp.toString()));
//                valTemp = new StringBuilder();
//                if (i != s.length() - 1)
//                    operator = c;
//            }
//        }
//
//        double one = numbers.get(0);
//        double two = numbers.get(1);
//        double result = 0;
//        if (operator != null) {
//            switch (operator) {
//                case '+':
//                    result = one + two;
//                    break;
//                case '-':
//                    result = one - two;
//                    break;
//                case '/':
//                    result = one / two;
//                    break;
//                case '*':
//                    result = one * two;
//                    break;
//            }
//
//        }
//
//        text = String.valueOf(result);
//    }

    private void updateText() {
//        double one = 0;
//        float two = 0;
//        if (tempOperandOne.length() > 0) {
//            one = Float.parseFloat(tempOperandOne.toString());
//            text = String.valueOf(numberFormat.format(one)) + " ";
//        }else text = "";
//        text = text + operationOne + " ";
//        if (tempOperandTwo.length() > 0) {
//            two = Float.parseFloat(tempOperandTwo.toString());
//            text = text + numberFormat.format(two);
//        }
        text = tempOperandOne + " "
                + operationOne + " "
                + tempOperandTwo;

        textView.setText(text);
//        Log.d("textLog", "tempOperandOne - " + tempOperandOne);
//        Log.d("textLog", "operationOne - " + operationOne);
//        Log.d("textLog", "tempOperandTwo - " + tempOperandTwo);
//        Log.d("textLog", "text - " + text);
//        Log.d("textLog", "______________________________");
    }

    public void setReturnVal(Handler returnVal) {
        this.returnVal = returnVal;
    }

    @Override
    public void show(@NonNull FragmentManager manager, String tag) {
        super.show(manager, tag);

    }

    @Override
    public void onResume() {
        super.onResume();
//        getDialog().getWindow().setGravity(Gravity.CENTER);
//        layout.post(new Runnable() {
//            @Override
//            public void run() {
//                int wight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200, getResources().getDisplayMetrics());
////                int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 284, getResources().getDisplayMetrics());
//                getDialog().getWindow().setLayout(wight, LinearLayout.LayoutParams.WRAP_CONTENT);
////                getDialog().getWindow().setGravity(Gravity.BOTTOM);
//
//
////                Log.d("params", wight + " " + height);
//
//            }
//        });
    }
}
