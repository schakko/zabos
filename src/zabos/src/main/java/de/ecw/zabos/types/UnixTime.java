package de.ecw.zabos.types;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

/**
 * Wrapper für einen Unix Timestamp (MilliSekunden seit 1.1.1970)
 * 
 * (Ein "wirklicher" Unix Timestamp wuerde natuerlich die *Sekunden* speichern!)
 * 
 * @author bsp
 * 
 */
public class UnixTime
{

    public static final long SEKUNDEN_PRO_TAG = 24 * 60 * 60;

    public static final TimeZone TIMEZONE = TimeZone
                    .getTimeZone("Europe/Berlin");

    public static final String[] month_names = new String[]
    { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };

    private long timeStamp;

    public UnixTime(long _time)
    {
        timeStamp = _time;
    }

    public UnixTime(Date _d)
    {
        timeStamp = _d.getTime();
    }

    /**
     * Liefert ein UnixTime Objekt welches mit der aktuellen Zeit
     * vorinitialisiert ist
     * 
     * @return
     */
    public static UnixTime now()
    {
        Date d = new Date();
        UnixTime ut = new UnixTime(d.getTime());
        return ut;
    }

    public long getTimeStamp()
    {
        return timeStamp;
    }

    public void setTimeStamp(long _timeStamp)
    {
        timeStamp = _timeStamp;
    }

    /**
     * Testet ob dieser Zeitpunkt im Zeitfenster [_start, _end] liegt
     * 
     * @param _start
     * @param _end
     * @return
     */
    public boolean isBetween(UnixTime _start, UnixTime _end)
    {
        return (timeStamp >= _start.timeStamp) && (timeStamp <= _end.timeStamp);
    }

    public boolean isLaterThan(UnixTime _o)
    {
        return (timeStamp > _o.timeStamp);
    }

    public void add(UnixTime _t)
    {
        timeStamp += _t.timeStamp;
    }

    public void sub(UnixTime _t)
    {
        long time_stamp_old = timeStamp;
        timeStamp -= _t.timeStamp;
        if (timeStamp > time_stamp_old)
        {
            timeStamp = 0;
        }
    }

    /**
     * Generiert ein Array von Start/End Terminpaaren für ein ggb. Zeitfenster.
     * 
     * Die Wertebereiche für die Jahr/Monat/Tag Parameter entsprechen denen der
     * Java Calendar Klasse.
     * 
     * @since 2006-06-08 CKL: _dayOfMonth hinzugefügt
     * @param _startYear
     * @param _startMonth
     *            Januar ist 0!
     * @param _startHour
     * @param _startMin
     * @param _endYear
     * @param _endMonth
     * @param _endHour
     * @param _endMin
     * @param _dayOfMonth
     *            Tag des Monats
     * @return
     */
    public static UnixTime[] generiereMonatlicheTermine(int _startYear,
                    int _startMonth, int _startHour, int _startMin,
                    int _endYear, int _endMonth, int _endHour, int _endMin,
                    int _dayOfMonth)
    {

        List<UnixTime> al = new ArrayList<UnixTime>();

        GregorianCalendar calStart = new GregorianCalendar(TimeZone
                        .getTimeZone("Europe/Berlin"));
        GregorianCalendar calEnd;

        calStart.setFirstDayOfWeek(Calendar.SUNDAY);
        calStart.set(Calendar.YEAR, _startYear);
        calStart.set(Calendar.MONTH, _startMonth);
        calStart.set(Calendar.HOUR_OF_DAY, _startHour);
        calStart.set(Calendar.MINUTE, _startMin);
        calStart.set(Calendar.SECOND, 0);
        calStart.set(Calendar.MILLISECOND, 0);

        int cYear = _startYear;
        int cMonth = _startMonth;

        while (cYear <= _endYear)
        {
            int eMonth = (cYear == _endYear) ? _endMonth : 11;
            calStart.set(Calendar.YEAR, cYear);

            while (cMonth <= eMonth)
            {
                calStart.set(Calendar.MONTH, cMonth);
                calStart.set(Calendar.DAY_OF_MONTH, _dayOfMonth);

                al.add(new UnixTime(calStart.getTime().getTime()));

                // End-Datum bzw. Endzeit (olol)
                calEnd = (GregorianCalendar) calStart.clone();
                calEnd.set(Calendar.MINUTE, _endMin);
                calEnd.set(Calendar.HOUR_OF_DAY, _endHour);
                al.add(new UnixTime(calEnd.getTime().getTime()));

                // Naechster Schleifendurchlauf
                cMonth++;
            } // while cMonth <= eMonth

            cMonth = 0;
            cYear++;
        } // while cYear <= eYear

        UnixTime[] r = new UnixTime[al.size()];
        al.toArray(r);
        return r;
    }

    /**
     * Generiert ein Array von Start/End Terminpaaren für ein ggb. Zeitfenster.
     * 
     * Die Wertebereiche für die Jahr/Monat/Tag Parameter entsprechen denen der
     * Java Calendar Klasse.
     * 
     * @since 2006-06-08 CKL: _dayOfWeek hinzugefügt
     * @param _startYear
     * @param _startMonth
     *            Januar ist 0!
     * @param _startHour
     * @param _startMin
     * @param _endYear
     * @param _endMonth
     * @param _endHour
     * @param _endMin
     * @param _dayOfWeek
     *            Samstag ist 7!
     * @param _weekOfMonth
     *            Woche im Monat, 5 bedeutet letzte Woche
     * @return
     */
    public static UnixTime[] generiereWoechentlicheTermine(int _startYear,
                    int _startMonth, int _startHour, int _startMin,
                    int _endYear, int _endMonth, int _endHour, int _endMin,
                    int _dayOfWeek, int _weekOfMonth)
    {

        List<UnixTime> al = new ArrayList<UnixTime>();

        GregorianCalendar calStart = new GregorianCalendar(TimeZone
                        .getTimeZone("Europe/Berlin"));
        GregorianCalendar calEnd;

        calStart.setFirstDayOfWeek(Calendar.SUNDAY);
        calStart.set(Calendar.YEAR, _startYear);
        calStart.set(Calendar.MONTH, _startMonth);
        calStart.set(Calendar.HOUR_OF_DAY, _startHour);
        calStart.set(Calendar.MINUTE, _startMin);
        calStart.set(Calendar.SECOND, 0);
        calStart.set(Calendar.MILLISECOND, 0);

        int cYear = _startYear;
        int cMonth = _startMonth;

        while (cYear <= _endYear)
        {
            int eMonth = (cYear == _endYear) ? _endMonth : 11;
            calStart.set(Calendar.YEAR, cYear);

            while (cMonth <= eMonth)
            {
                // 2007-02-07 CKL: Der Monat wurde unter Umstaenden nicht
                // richtig gesetzt
                calStart.set(Calendar.MONTH, cMonth);

                int datumDesTagesImMonat = 1;
                int wocheDesMonats = 1;

                // Jetzt jeden Tag im Monat durchlaufen
                for (int i = 1, m = calStart
                                .getActualMaximum(Calendar.DAY_OF_MONTH); i <= m; i++)
                {
                    calStart.set(Calendar.DAY_OF_MONTH, i);

                    if (calStart.get(Calendar.DAY_OF_WEEK) == _dayOfWeek)
                    {
                        // Diese Woche soll heran gezogen werden
                        if (wocheDesMonats == _weekOfMonth)
                        {
                            datumDesTagesImMonat = i;
                            break;
                        } // if wocheDesMonats == _weekOfMonth

                        wocheDesMonats++;
                    } // if calStart.get(Calendar.DAY_OF_WEEK) == _dayOfWeek)
                } // for

                calStart.set(Calendar.MONTH, cMonth);
                calStart.set(Calendar.DAY_OF_MONTH, datumDesTagesImMonat);
                al.add(new UnixTime(calStart.getTime().getTime()));

                // End-Datum bzw. Endzeit (olol)
                calEnd = (GregorianCalendar) calStart.clone();
                calEnd.set(Calendar.MINUTE, _endMin);
                calEnd.set(Calendar.HOUR_OF_DAY, _endHour);
                al.add(new UnixTime(calEnd.getTime().getTime()));

                // Naechster Schleifendurchlauf
                cMonth++;

                // 2007-01-25 CKL: Ohne diese Ueberpruefung wird das das zweite
                // zu
                // erstellende Jahr uebersprungen
                if (cMonth < eMonth)
                {
                    // Monat um eins erhoehen
                    calStart.set(Calendar.MONTH, cMonth);
                }

                // Tag des Monats wieder auf den ersten zuruecksetzen
                calStart.set(Calendar.DAY_OF_MONTH, 1);
                // calStart.set(Calendar.DAY_OF_WEEK,0);
            } // while cMonth <= eMonth

            cMonth = 0;
            cYear++;
        } // while cYear <= eYear

        UnixTime[] r = new UnixTime[al.size()];
        al.toArray(r);
        return r;
    }

    /**
     * Generiert ein Array von Start/End Terminpaaren für ein ggb. Zeitfenster.
     * 
     * Die Wertebereiche für die Jahr/Monat/Tag Parameter entsprechen denen der
     * Java Calendar Klasse.
     * 
     * @since 2006-06-08 CKL: _dayOfWeek hinzugefügt
     * @param _startYear
     * @param _startMonth
     *            Januar ist 0!
     * @param _startHour
     * @param _startMin
     * @param _endYear
     * @param _endMonth
     * @param _endHour
     * @param _endMin
     * @param _dayOfWeek
     *            Samstag ist 0!
     * @return
     */
    public static UnixTime[] generiereTaeglicheTermine(int _startYear,
                    int _startMonth, int _startHour, int _startMin,
                    int _endYear, int _endMonth, int _endHour, int _endMin,
                    int _dayOfWeek)
    {

        List<UnixTime> al = new ArrayList<UnixTime>();

        GregorianCalendar calStart = new GregorianCalendar(TimeZone
                        .getTimeZone("Europe/Berlin"));
        GregorianCalendar calEnd;

        calStart.setFirstDayOfWeek(Calendar.SUNDAY);
        calStart.set(Calendar.YEAR, _startYear);
        calStart.set(Calendar.MONTH, _startMonth);
        calStart.set(Calendar.HOUR_OF_DAY, _startHour);
        calStart.set(Calendar.MINUTE, _startMin);
        calStart.set(Calendar.SECOND, 0);
        calStart.set(Calendar.MILLISECOND, 0);
        calStart.set(Calendar.DAY_OF_WEEK, _dayOfWeek);

        int cYear = _startYear;
        int cMonth = _startMonth;

        while (cYear <= _endYear)
        {
            int eMonth = (cYear == _endYear) ? _endMonth : 11;

            while (cMonth <= eMonth)
            {
                boolean bAdd = true;

                while (bAdd)
                {
                    al.add(new UnixTime(calStart.getTime().getTime()));
                    calEnd = (GregorianCalendar) calStart.clone();
                    calEnd.set(Calendar.MINUTE, _endMin);
                    calEnd.set(Calendar.HOUR_OF_DAY, _endHour);
                    al.add(new UnixTime(calEnd.getTime().getTime()));

                    calStart.add(Calendar.WEEK_OF_MONTH, 1);

                    if (cMonth != calStart.get(Calendar.MONTH))
                    {
                        bAdd = false;
                    }
                }

                cMonth++;
            }
            cMonth = 0;
            cYear++;
        }

        UnixTime[] r = new UnixTime[al.size()];
        al.toArray(r);
        return r;
    }

    /**
     * Berechnet einen "abwesend-bis" Zeitpunkt unter Angabe der Anzahl der
     * Abwesenheitstage.
     * 
     * Die Anzahl der Tage schliesst auch den heutigen Tag mit ein.
     * 
     * @param _anzTage
     * @return
     */
    public static UnixTime calcAbwesendBis(int _anzTage)
    {
        GregorianCalendar cal = new GregorianCalendar(TIMEZONE);

        // Zeitpunkt auf Anfang des Tages zurueckrechnen
        cal.setTime(new Date());
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        UnixTime t = new UnixTime(cal.getTime());

        // Urlaubstage dazuaddieren
        t.add(new UnixTime(_anzTage * SEKUNDEN_PRO_TAG));
        return t;
    }

    public String toString()
    {
        return String.valueOf(timeStamp);
    }

    /**
     * Liefert das aktuelle Datum im Format 20060215, also yyyymmdd.
     * 
     * @return
     */
    public String toDateString()
    {
        GregorianCalendar cal = new GregorianCalendar(UnixTime.TIMEZONE);
        cal.setTimeInMillis(getTimeStamp());
        StringBuffer sb = new StringBuffer();
        sb.append(Integer.toString(cal.get(Calendar.YEAR)));

        int month = (cal.get(Calendar.MONTH) + 1);
        if (month < 10)
        {
            sb.append('0');
        }

        sb.append(Integer.toString(month));

        int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);

        if (dayOfMonth < 10)
        {
            sb.append('0');
        }

        sb.append(Integer.toString(dayOfMonth));
        return sb.toString();
    }

    /**
     * Liefert die Uhrzeit in Form h:i:s zurück
     * 
     * @return
     */
    public String toTimeString()
    {
        GregorianCalendar cal = new GregorianCalendar(UnixTime.TIMEZONE);
        cal.setTimeInMillis(getTimeStamp());
        StringBuffer sb = new StringBuffer();

        sb.append(cal.get(Calendar.HOUR_OF_DAY));
        sb.append(":");
        sb.append(cal.get(Calendar.MINUTE));
        sb.append(":");
        sb.append(cal.get(Calendar.SECOND));

        return sb.toString();
    }

    /**
     * Liefert das aktuelle Datum im Format 20060215, also yyyymmdd.
     * 
     * Delegiert an {@link UnixTime#toDateString()}
     * 
     * @return
     */
    public static String getCurrentDateString()
    {
        return new UnixTime(new Date()).toDateString();
    }
}
