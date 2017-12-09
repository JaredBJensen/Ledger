package edu.ucsb.cs.cs184.jaredbjensen.ledger;


import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.text.NumberFormat;

public class MyYAxisValueFormatter implements IAxisValueFormatter{

    NumberFormat format;

    public MyYAxisValueFormatter() {
        format = NumberFormat.getCurrencyInstance();
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        return format.format(value);
    }

}
