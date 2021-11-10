/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bootstrap;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Class with configurations of the program
 * @author lucas
 */
public class Config {
    /**
     * Default float number conversion for this program
     * @param number Float the number to be converted to string
     * @return String the resulting converted number
     */
    public static String numberFormat(float number) {
        NumberFormat format = NumberFormat.getNumberInstance(Locale.UK);
        format.setMaximumFractionDigits(3);
        return format.format(number);
    }
}
