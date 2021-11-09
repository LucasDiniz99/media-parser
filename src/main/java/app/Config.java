/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app;

import java.text.NumberFormat;
import java.util.Locale;

/**
 *
 * @author lucas
 */
public class Config {
    
    public static String numberFormat(float number) {
        NumberFormat format = NumberFormat.getNumberInstance(Locale.UK);
        format.setMaximumFractionDigits(3);
        return format.format(number);
    }
}
