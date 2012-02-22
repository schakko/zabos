package de.ecw.zabos.util;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Utility-Klasse für Datumseinträge
 * 
 * @author ckl
 * @deprecated
 */
public class DateUtils
{
    public static String getLogTimeString()
    {
        Date d = new Date();

        GregorianCalendar cal = new GregorianCalendar(
                        TimeZone.getTimeZone("Europe/Berlin"));

        cal.setTime(d);

        return "" + sprintf02d(cal.get(GregorianCalendar.DAY_OF_MONTH)) + "."
                        + sprintf02d((cal.get(GregorianCalendar.MONTH) + 1))
                        + "." + cal.get(GregorianCalendar.YEAR) + " "
                        + sprintf02d(cal.get(GregorianCalendar.HOUR_OF_DAY))
                        + ":" + sprintf02d(cal.get(GregorianCalendar.MINUTE))
                        + ":" + sprintf02d(cal.get(GregorianCalendar.SECOND))
                        + "."
                        + sprintf03d(cal.get(GregorianCalendar.MILLISECOND));
    }

    private static String sprintf02d(int _i)
    {
        return ((_i < 10) ? ("0" + _i) : ("" + _i));
    }

    private static String sprintf03d(int _i)
    {
        return (_i < 10) ? ("00" + _i) : ((_i < 100) ? ("0" + _i) : ("" + _i));
    }
}
