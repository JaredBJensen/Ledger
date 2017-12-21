package edu.ucsb.cs.cs184.jaredbjensen.ledger;


import android.text.format.DateFormat;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.text.NumberFormat;
import java.util.Calendar;

public class MyXAxisValueFormatter implements IAxisValueFormatter{

    Calendar calendar;

    public MyXAxisValueFormatter() {
        calendar = calendar.getInstance();
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        calendar.setTimeInMillis((long)value);
        return DateFormat.format(MainActivity.SIMPLE_DATE_FORMAT, calendar).toString();
    }

}
