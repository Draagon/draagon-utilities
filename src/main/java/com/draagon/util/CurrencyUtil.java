/*
 * Copyright 2001 Draagon Software LLC. All Rights Reserved.
 *
 * This software is the proprietary information of Draagon Software LLC.
 * Use is subject to license terms.
 */

package com.draagon.util;
import java.text.*;
import java.util.Locale;

/**
 *  This class contains static methods useful for formatting currency in
 *  various ways.
 */
public class CurrencyUtil
{
    public static String formatCurrency(String amountString)
    {
        try {
            double amount = Double.parseDouble(amountString);
            return formatCurrency(amount,"$###,###.00",null);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    public static String formatCurrency(String amountString, String pattern)
    {
        try {
            double amount = Double.parseDouble(amountString);
            return formatCurrency(amount,pattern,null);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    public static String formatCurrency(String amountString, Locale locale)
    {
        try {
            double amount = Double.parseDouble(amountString);
            return formatCurrency(amount,"$###,###.00",locale);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    public static String formatCurrency(String amountString, String pattern,
          Locale locale)
    {
        try {
            double amount = Double.parseDouble(amountString);
            return formatCurrency(amount,pattern,locale);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    public static String formatCurrency(double amount)
    {
        return formatCurrency(amount,"$###,###.00",null);
    }

    public static String formatCurrency(double amount, String pattern)
    {
        return formatCurrency(amount,pattern,null);
    }

    public static String formatCurrency(double amount, Locale locale)
    {
        return formatCurrency(amount,"$###,###.00",locale);
    }

    public static String formatCurrency(double amount, String pattern,
        Locale locale)
    {
        NumberFormat nf = null;
        if (locale != null)
        {
          nf = NumberFormat.getCurrencyInstance(locale);
        }
        else
        {
          nf = NumberFormat.getCurrencyInstance();
        }
        DecimalFormat df = (DecimalFormat)nf;
        df.setMinimumFractionDigits(2);
        df.setMaximumFractionDigits(2);
        df.setDecimalSeparatorAlwaysShown(true);
        df.applyPattern(pattern);
        return df.format(amount);
    }
}
