package de.ecw.zabos.frontend.beans;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Das DateBean nimmt verschiedene Datums/Zeitangaben entgegen und erzeugt
 * formatierten Output, der auf ZABOS zugeschnitten ist
 * 
 * @author ckl
 */
public class DateBean
{
    private String date = "";

    private String time = "";

    private int year = 0;

    private int month = 0;

    private int day = 0;

    private int hour = 0;

    private int minute = 0;

    /**
     * Setzt das Datum. Als Parameter wir ein String der Form dd.mm.yyyyy
     * erwartet
     * 
     * @param _date
     *            Datum als String in der Form dd.mm.yyyy
     */
    public void setDate(String _date)
    {
        Pattern p = Pattern.compile("^(\\d\\d)\\.(\\d\\d)\\.(\\d\\d\\d\\d)$");
        Matcher m = p.matcher(_date);

        if (m.matches() == true)
        {
            this.day = Integer.valueOf(m.group(1));
            this.month = Integer.valueOf(m.group(2));
            this.year = Integer.valueOf(m.group(3));
            this.date = _date;
        }
    }

    /**
     * Liefert das übermittelte Datum
     * 
     * @return Datum
     */
    public String getDate()
    {
        return this.date;
    }

    /**
     * Liefert den Monat
     * 
     * @return Monat
     */
    public int getMonth()
    {
        return month;
    }

    /**
     * Liefert das Jahr
     * 
     * @return Jahr
     */
    public int getYear()
    {
        return year;
    }

    /**
     * Liefert den Tag
     * 
     * @return Tag
     */
    public int getDay()
    {
        return day;
    }

    /**
     * Liefert die Stunde
     * 
     * @return Stunde
     */
    public int getHour()
    {
        return hour;
    }

    /**
     * Liefert die Minute
     * 
     * @return Minute
     */
    public int getMinute()
    {
        return minute;
    }

    /**
     * Setzt den Zeit-String in der Form mm:hh
     * 
     * @param _time
     *            Zeit in der Form mm:hh
     */
    public void setTime(String _time)
    {
        Pattern p = Pattern.compile("^(\\d\\d):(\\d\\d)$");
        Matcher m = p.matcher(_time);

        if (m.matches() == true)
        {
            this.hour = Integer.valueOf(m.group(1));
            this.minute = Integer.valueOf(m.group(2));
            this.time = _time;
        }
    }

    /**
     * Liefert die Zeit, die übergeben wurde, als String
     * 
     * @return Zeit
     */
    public String getTime()
    {
        return this.time;
    }

    /**
     * Liefert den Timestamp dieses Beans
     * 
     * @return Timestamp
     */
    public long getTimestamp()
    {
        GregorianCalendar cal = new GregorianCalendar(TimeZone
                        .getTimeZone("Europe/Berlin"));
        cal.setFirstDayOfWeek(Calendar.SUNDAY);

        cal.set(Calendar.YEAR, this.getYear());
        cal.set(Calendar.DAY_OF_MONTH, this.getDay());
        cal.set(Calendar.MONTH, (this.getMonth() - 1));
        cal.set(Calendar.HOUR_OF_DAY, this.getHour());
        cal.set(Calendar.MINUTE, this.getMinute());
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        return cal.getTime().getTime();
    }

    /**
     * Liefert den Tag der Woche
     * 
     * @return Tag der Woche
     */
    public int getDayOfWeek()
    {
        GregorianCalendar cal = new GregorianCalendar(TimeZone
                        .getTimeZone("Europe/Berlin"));
        cal.setFirstDayOfWeek(Calendar.SUNDAY);

        cal.set(Calendar.YEAR, this.getYear());
        cal.set(Calendar.MONTH, (this.getMonth() - 1));
        cal.set(Calendar.DAY_OF_MONTH, this.getDay());
        cal.set(Calendar.HOUR_OF_DAY, this.getHour());
        cal.set(Calendar.MINUTE, this.getMinute());
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        return cal.get(Calendar.DAY_OF_WEEK);
    }

    /**
     * Liefert den Tag eines Monats
     * 
     * @return Tag des Monats
     */
    public int getWeekOfMonth()
    {
        GregorianCalendar cal = new GregorianCalendar(TimeZone
                        .getTimeZone("Europe/Berlin"));
        cal.setFirstDayOfWeek(Calendar.SUNDAY);

        cal.set(Calendar.YEAR, this.getYear());
        cal.set(Calendar.MONTH, (this.getMonth() - 1));
        cal.set(Calendar.DAY_OF_MONTH, this.getDay());
        cal.set(Calendar.HOUR_OF_DAY, this.getHour());
        cal.set(Calendar.MINUTE, this.getMinute());
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        return cal.get(Calendar.WEEK_OF_MONTH);
    }
}
