package com.baltazarstudio.kossen.component;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.google.android.material.textfield.TextInputEditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TimeInputText extends TextInputEditText {


    private OnFocusChangeListener focusChangeListener = null;
    private OnClickListener clickListener = null;

    private final DateMask mask = new DateMask(this);

    public TimeInputText(Context context, AttributeSet attrs) {
        super(context, attrs);
        addTextChangedListener(mask);
        setTextIsSelectable(false);
        setClickable(true);
        super.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(final View v, final boolean hasFocus) {
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        if (hasFocus) {
                            adaptSelection();
                        }

                        if (focusChangeListener != null) focusChangeListener.onFocusChange(v, hasFocus);
                    }
                });
            }
        });


        super.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                adaptSelection();

                if (clickListener != null) clickListener.onClick(v);
            }
        });

    }

    private void adaptSelection() {
        int selectionStart = getSelectionStart();

        if (selectionStart <= 2) {
            setSelection(0, 2);
        } else if (selectionStart <= 5) {
            setSelection(3, 5);
        } else {
            setSelection(6, length());
        }
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        this.clickListener = l;
    }

    @Override
    public void setOnFocusChangeListener(OnFocusChangeListener l) {
        this.focusChangeListener = l;
    }

    void setDate(Calendar calendar) {
        removeTextChangedListener(mask);
        mask.sync();
        addTextChangedListener(mask);
    }

    void setDate(Long time) {
        removeTextChangedListener(mask);
        mask.sync();
        addTextChangedListener(mask);
    }

    Long getTime() {
        long time = 0;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", getTextLocale());
            time = sdf.parse(getText().toString()).getTime();
        } catch (ParseException e) {
            return time;
        }

        return time;
    }

    @SuppressLint("SetTextI18n")
    public void resetTime() {
        removeTextChangedListener(mask);
        setText("00:00:00");
        addTextChangedListener(mask);
    }

    private static class DateMask implements TextWatcher {

        TextInputEditText editText;

        DateMask(TextInputEditText editText) {
            this.editText = editText;
            sync();
        }

        private String old = "";

        private boolean isHourSelection = false;
        private boolean isMinuteSelection = false;
        private boolean isSecondSelection = false;

        private String hour = "00";
        private String minutes = "00";
        private String seconds = "00";


        void sync() {

            if (editText.length() == 10) {
                hour = editText.getText().toString().substring(0, 2);
                minutes = editText.getText().toString().substring(3, 5);
                seconds = editText.getText().toString().substring(6);
            }

        }

        @Override
        public void afterTextChanged(Editable e) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            isHourSelection = editText.getSelectionStart() <= 2;
            isMinuteSelection = editText.getSelectionStart() >= 3 && editText.getSelectionStart() <= 5;
            isSecondSelection = editText.getSelectionStart() > 5;

            old = s.toString();
        }

        @Override
        public void onTextChanged(CharSequence c, int start, int before, int count) {
            String str = unmask(c.toString());
            //Log.i("Selection ON CHANGED", str)

            if (isHourSelection) {
                String input = str.substring(0, 1);

                if (!validInputString(input)) {
                    aplicarMascara(old);
                    return;
                }

                if (hour.length() < 2) {
                    hour += input;
                } else {
                    hour = input;
                }
            }

            if (isMinuteSelection) {
                String input = str.substring(2, 3);

                if (!validInputString(input)) {
                    aplicarMascara(old);
                    return;
                }

                if (minutes.length() < 2) {
                    minutes += input;
                } else {
                    minutes = input;
                }
            }

            if (isSecondSelection) {
                String input = str.substring(str.length() - 1);

                if (!validInputString(input)) {
                    aplicarMascara(old);
                    return;
                }

                if (seconds.length() == 2) {
                    seconds = input;
                } else {
                    seconds += input;
                }
            }

            String mascara = "";
            if (hour.length() == 1) mascara += "0";
            mascara += hour;

            mascara += ":";

            if (minutes.length() == 1) mascara += "0";
            mascara += minutes;

            mascara += ":";

            if (seconds.length() == 1) mascara += "0";
            mascara += seconds;


            aplicarMascara(mascara);


            if (isHourSelection) {
                editText.setSelection(0, 2);
            } else if (isMinuteSelection) {
                editText.setSelection(3, 5);
            } else if (isSecondSelection) {
                editText.setSelection(6, editText.length());
            }

        }

        private void aplicarMascara(String mascara) {
            editText.removeTextChangedListener(this);
            editText.setText(mascara);
            editText.addTextChangedListener(this);
        }

        private String unmask(String mascara) {
            return mascara.replace(":", "");
        }

        private boolean validInputString(String s) {
            for (int i = 0; i < s.length(); i++) {
                if (!Character.isDigit(s.charAt(i))) {
                    return false;
                }
            }

            return true;
        }
    }

}