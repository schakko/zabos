package de.ecw.zabos.frontend.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import de.ecw.zabos.exceptions.StdException;

/**
 * Klasse mit statischen Methoden für das Vearbeiten von verschiedenen
 * Datumsformaten
 * 
 * @author ckl
 */
public class DateUtils
{
    /**
     * Deaktiviert das Blättern
     */
    public final static int SCROLL_NONE = 0;

    /**
     * Bezeichnet das Blättern in die Vergangenheit
     */
    public final static int SCROLL_BACKWARD = -1;

    /**
     * Bezeichnet das Blättern in die Zukunft
     */
    public final static int SCROLL_FORWARD = 1;

    /**
     * Füllt die Zahl auf zwei Stellen auf. Aus 9 wird z.B. 09
     * 
     * @param _number
     * @return String
     */
    public static String fillTwoSigns(int _number)
    {
        String ret = String.valueOf(_number);

        if (_number <= 9)
        {
            ret = "0" + ret;
        }

        return ret;
    }

    /**
     * Liefert das Datum als Zahl vom Typ Long zurück. Der Long-Wert entspricht
     * dabei der Zahl yyyymmdd.
     * 
     * @param _date
     * @return Long der Art yyyymmdd
     */
    public static Long getDateAsLong(java.util.Date _date)
    {
        long ret = 0;

        GregorianCalendar calDatum = new GregorianCalendar(TimeZone
                        .getTimeZone("Europe/Berlin"));
        calDatum.setFirstDayOfWeek(Calendar.SUNDAY);
        calDatum.setTime(_date);

        try
        {
            ret = Long
                            .valueOf(calDatum.get(GregorianCalendar.YEAR)
                                            + ""
                                            + fillTwoSigns((calDatum
                                                            .get(GregorianCalendar.MONTH) + 1))
                                            + ""
                                            + fillTwoSigns(calDatum
                                                            .get(GregorianCalendar.DAY_OF_MONTH)));
        }
        catch (NumberFormatException e)
        {
        }

        return ret;
    }

    /**
     * Liefert das übergebene Date-Objekt als Datums-String der Forma dd.mm.yyyy
     * zurück
     * 
     * @param _date
     * @return
     */
    public static String getDateAsString(java.util.Date _date)
    {
        String ret = "";
        Long date = getDateAsLong(_date);

        if (date.toString().length() == 8)
        {
            ret += date.toString().substring(6, 8) + ".";
            ret += date.toString().substring(4, 6) + ".";
            ret += date.toString().substring(0, 4);
        }
        else
        {
            ret = "00.00.0000";
        }

        return ret;
    }

    /**
     * Liefert das Datum als Zahl vom Typ Long zurück. Der Long-Wert entspricht
     * dabei der Zahl yyyymmddhhiiss.
     * 
     * @param _date
     * @return Long der Art yyyymmddhhiiss
     */
    public static Long getDateTimeAsLong(java.util.Date _date)
    {
        long ret = 0;

        GregorianCalendar calDatum = new GregorianCalendar(TimeZone
                        .getTimeZone("Europe/Berlin"));
        calDatum.setFirstDayOfWeek(Calendar.SUNDAY);
        calDatum.setTime(_date);

        try
        {
            ret = Long
                            .valueOf(calDatum.get(GregorianCalendar.YEAR)
                                            + ""
                                            + fillTwoSigns((calDatum
                                                            .get(GregorianCalendar.MONTH) + 1))
                                            + ""
                                            + fillTwoSigns(calDatum
                                                            .get(GregorianCalendar.DAY_OF_MONTH))
                                            + ""
                                            + fillTwoSigns(calDatum
                                                            .get(GregorianCalendar.HOUR_OF_DAY))
                                            + ""
                                            + fillTwoSigns(calDatum
                                                            .get(GregorianCalendar.MINUTE))
                                            + ""
                                            + fillTwoSigns(calDatum
                                                            .get(GregorianCalendar.SECOND)));
        }
        catch (NumberFormatException e)
        {
        }

        return ret;
    }

    /**
     * Liefert das Datum als Zahl vom Typ Long zurück. Der Long-Wert entspricht
     * dabei der Zahl yyyymmdd.
     * 
     * @param _date
     * @param _format
     * @return Long der Art yyyymmdd
     */
    public static Long getDateStringAsLong(String _date, String _format)
    {
        long ret = 0;

        try
        {
            ret = getDateAsLong(getDateStringAsDate(_date, _format));
        }
        catch (ParseException e)
        {

        }

        return ret;
    }

    /**
     * Liefert den übergebenen String als java.util.Date Objekt zurück
     * 
     * @param _date
     * @param _format
     * @return
     * @throws ParseException
     */
    public static Date getDateStringAsDate(String _date, String _format) throws ParseException
    {
        SimpleDateFormat sdf = new SimpleDateFormat(_format);
        java.util.Date ret = null;
        ret = sdf.parse(_date);

        return ret;
    }

    /**
     * Liefert das heutige Datum als long
     * 
     * @return
     */
    public static long getTodayAsLong()
    {
        long ret = 0;

        GregorianCalendar calToday = new GregorianCalendar(TimeZone
                        .getTimeZone("Europe/Berlin"));
        calToday.setFirstDayOfWeek(Calendar.SUNDAY);

        try
        {
            ret = getDateAsLong(calToday.getTime());
        }
        catch (NumberFormatException e)
        {
        }

        return ret;
    }

    /**
     * Liefert Datum und Uhrzeit des Longs
     * 
     * @param _date
     */
    public static String getLongAsDateTimeString(long _date)
    {
        return getLongAsDateString(_date) + " " + getLongAsTimeString(_date);
    }

    /**
     * Liefert das Datum des Longs zurück
     * 
     * @param _date
     * @return
     */
    public static String getLongAsDateString(long _date)
    {
        GregorianCalendar cal = new GregorianCalendar(TimeZone
                        .getTimeZone("Europe/Berlin"));
        cal.setFirstDayOfWeek(Calendar.SUNDAY);
        cal.setTimeInMillis(_date);

        return getDayName(cal.get(Calendar.DAY_OF_WEEK)) + ", "
                        + cal.get(Calendar.DAY_OF_MONTH) + "."
                        + (cal.get(Calendar.MONTH) + 1) + "."
                        + cal.get(Calendar.YEAR);
    }

    /**
     * Liefert die Uhrzeit des Long-Wertes zurück
     * 
     * @param _date
     * @return
     */
    public static String getLongAsTimeString(long _date)
    {
        GregorianCalendar cal = new GregorianCalendar(TimeZone
                        .getTimeZone("Europe/Berlin"));
        cal.setFirstDayOfWeek(Calendar.SUNDAY);
        cal.setTimeInMillis(_date);

        return (cal.get(Calendar.HOUR_OF_DAY) + 1) + ":"
                        + cal.get(Calendar.MINUTE);
    }

    /**
     * Liefert ein Array mit zwei Werten zurück. <br />
     * Index 0 ist das kleinere der beiden Daten, Index 1 ist das größere.
     * 
     * @param _date
     *            Datum, ab dem gerechnet werden soll
     * @param _format
     *            Datumsformat
     * @param _rangeType
     *            Typ, ab dem gerechnet werden soll
     * @param _rangeNumber
     *            Die Zahl in Schritten, von der ab gerechnet werden soll
     * @return GregorianCalendar[] Index 0 ist das kleinere der beiden Daten,
     *         Index 1 ist das größere
     */
    public static GregorianCalendar[] getTimespanOfDate(String _dateZeitpunkt,
                    String _format, int _rangeType, int _rangeNumber,
                    int _scrollType) throws StdException
    {
        SimpleDateFormat sdf = new SimpleDateFormat(_format);
        GregorianCalendar[] ret = new GregorianCalendar[2];

        // Der uebergebene Zeitpunkt
        java.util.Date dateZeitpunkt = null;

        // Start-Zeitpunkt
        GregorianCalendar calZeitpunktFrom = new GregorianCalendar(TimeZone
                        .getTimeZone("Europe/Berlin"));
        // End-Zeitpunkt
        GregorianCalendar calZeitpunkt = new GregorianCalendar(TimeZone
                        .getTimeZone("Europe/Berlin"));

        calZeitpunktFrom.setFirstDayOfWeek(Calendar.SUNDAY);
        calZeitpunkt.setFirstDayOfWeek(Calendar.SUNDAY);

        // Falscher Scroll-Type
        if (_scrollType > 1 || _scrollType < -1)
        {
            throw new StdException("Parameter _scrollType with value "
                            + _scrollType + " is not supported.");
        }

        // Falscher Range-Type
        if (_rangeType != GregorianCalendar.MONTH
                        && _rangeType != GregorianCalendar.DAY_OF_MONTH
                        && _rangeType != GregorianCalendar.WEEK_OF_YEAR)
        {
            throw new StdException(
                            "Parameter _rangeType has an unallowed value.");
        }

        // Falsches Datums-Format
        try
        {
            dateZeitpunkt = sdf.parse(_dateZeitpunkt);
        }
        catch (ParseException e)
        {
            throw new StdException("Could not parse the date " + _dateZeitpunkt
                            + " with given format " + _format + ": "
                            + e.getMessage());
        }

        // Den geparsten Zeitpunkt setzen
        calZeitpunkt.setTime(dateZeitpunkt);

        // Zuerst muss ueberprueft werden, ob geblaettert werden soll
        if (_scrollType != SCROLL_NONE)
        {
            int multiplicator = _scrollType;

            // Neuen Zeitpunkt berechnen
            calZeitpunkt.add(_rangeType, (multiplicator * _rangeNumber));
        }

        // Immer von Datum-Start 00:00:00 Uhr bis Datum-Ende 23:59:59 Uhr
        calZeitpunkt.set(GregorianCalendar.HOUR_OF_DAY, 23);
        calZeitpunkt.set(GregorianCalendar.MINUTE, 59);
        calZeitpunkt.set(GregorianCalendar.SECOND, 59);

        calZeitpunktFrom = (GregorianCalendar) calZeitpunkt.clone();
        calZeitpunktFrom.set(GregorianCalendar.HOUR_OF_DAY, 0);
        calZeitpunktFrom.set(GregorianCalendar.MINUTE, 0);
        calZeitpunktFrom.set(GregorianCalendar.SECOND, 0);
        calZeitpunktFrom.add(GregorianCalendar.DAY_OF_YEAR, 1);
        calZeitpunktFrom.add(_rangeType, ((-1) * _rangeNumber));

        // Richtige Reihenfolge setzen
        if (calZeitpunkt.getTimeInMillis() > calZeitpunktFrom.getTimeInMillis())
        {
            ret[1] = calZeitpunkt;
            ret[0] = calZeitpunktFrom;
        }
        else
        {
            ret[0] = calZeitpunkt;
            ret[1] = calZeitpunktFrom;
        }

        return ret;
    }

    /**
     * Liefert den lokalisierten Namen des Tag
     * 
     * @param _dayOfWeek
     * @return
     */
    public static String getDayName(int _dayOfWeek)
    {
        switch (_dayOfWeek)
        {
            case 1:
                return "Sonntag";
            case 2:
                return "Montag";
            case 3:
                return "Dienstag";
            case 4:
                return "Mittwoch";
            case 5:
                return "Donnerstag";
            case 6:
                return "Freitag";
            case 7:
                return "Samstag";
        }

        return "Unbekannt";
    }
}
