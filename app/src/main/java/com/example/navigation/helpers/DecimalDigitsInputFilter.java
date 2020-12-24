package com.example.navigation.helpers;

import android.text.InputFilter;
import android.text.Spanned;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DecimalDigitsInputFilter implements InputFilter {

    Pattern mPattern;

    public DecimalDigitsInputFilter() {

        mPattern=Pattern.compile("[0-9]{0," + (5) + "}+((\\.[1-9]{0})?)|(\\.)?");
        //mPattern=Pattern.compile("[0-9]{0," + (digitsBeforeZero) + "}+((\.[0-9]{0," + (digitsAfterZero) + "})?)||(\.)?");

    }


    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        Matcher matcher = mPattern.matcher(dest);
        if (!matcher.matches())
            return "";
        return null;
    }
}