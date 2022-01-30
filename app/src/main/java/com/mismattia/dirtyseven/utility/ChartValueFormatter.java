package com.mismattia.dirtyseven.utility;

import com.github.mikephil.charting.formatter.ValueFormatter;

public class ChartValueFormatter extends ValueFormatter {
    public String getFormattedValue(float value) {
        return "" + ((int) value);
    }
}
