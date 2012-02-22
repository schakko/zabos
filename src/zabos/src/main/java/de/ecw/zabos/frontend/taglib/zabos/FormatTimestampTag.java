package de.ecw.zabos.frontend.taglib.zabos;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import de.ecw.zabos.frontend.utils.DateUtils;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import de.ecw.zabos.types.UnixTime;

/**
 * Taglib<br>
 * Formatiert ein Timestamp in ein Datums/Zeit-Format
 * 
 * @author ckl
 */
public class FormatTimestampTag extends TagSupport
{
    public final static long serialVersionUID = 123981938;

    public final static String DEFAULT_FORMAT = "date";

    public static final TimeZone TIMEZONE = TimeZone
                    .getTimeZone("Europe/Berlin");

    private String sprintf = "";

    private String format = "";

    private String defaultString = "Timestamp nicht verfügbar ";

    private long ts = 0;

    /*
     * (non-Javadoc)
     * 
     * @see javax.servlet.jsp.tagext.TagSupport#doStartTag()
     */
    public int doStartTag()
    {
        try
        {
            JspWriter out = pageContext.getOut();
            GregorianCalendar cal = new GregorianCalendar(TIMEZONE);
            cal.setFirstDayOfWeek(Calendar.SUNDAY);

            String publishDate = "";

            // Werte setzen
            if (this.ts >= 0)
            {
                cal.setTimeInMillis(this.ts);
            }

            // Kein Wert gesetzt
            if (this.ts == 0)
            {
                publishDate = this.defaultString;
            }
            else if (sprintf.equals("") == false)
            { // Sprintf hat Vorrang
                sprintf = sprintf
                                .replaceAll(
                                                "d",
                                                ""
                                                                + DateUtils
                                                                                .fillTwoSigns(cal
                                                                                                .get(GregorianCalendar.DAY_OF_MONTH)));
                sprintf = sprintf
                                .replaceAll(
                                                "m",
                                                ""
                                                                + DateUtils
                                                                                .fillTwoSigns((cal
                                                                                                .get(GregorianCalendar.MONTH) + 1)));
                sprintf = sprintf
                                .replaceAll(
                                                "H",
                                                ""
                                                                + DateUtils
                                                                                .fillTwoSigns(cal
                                                                                                .get(GregorianCalendar.HOUR_OF_DAY)));
                sprintf = sprintf
                                .replaceAll(
                                                "i",
                                                ""
                                                                + DateUtils
                                                                                .fillTwoSigns(cal
                                                                                                .get(GregorianCalendar.MINUTE)));
                sprintf = sprintf
                                .replaceAll(
                                                "W",
                                                ""
                                                                + DateUtils
                                                                                .getDayName(cal
                                                                                                .get(GregorianCalendar.DAY_OF_WEEK)));
                sprintf = sprintf.replaceAll("Y", ""
                                + cal.get(GregorianCalendar.YEAR));
                publishDate = sprintf;
            }
            else
            { // Ansonsten wird nur ein popeliges Standard-Datum/Zeit ausgegeben
                String outTime = "";
                String outDate = "";

                if (format.equals("time") || format.equals("both"))
                {
                    outTime = DateUtils.fillTwoSigns(cal
                                    .get(GregorianCalendar.HOUR_OF_DAY))
                                    + ":"
                                    + DateUtils
                                                    .fillTwoSigns(cal
                                                                    .get(GregorianCalendar.MINUTE));
                }

                if (format.equals("date") || format.equals("both"))
                {
                    outDate = DateUtils.fillTwoSigns(cal
                                    .get(GregorianCalendar.DAY_OF_MONTH))
                                    + "."
                                    + DateUtils
                                                    .fillTwoSigns((cal
                                                                    .get(GregorianCalendar.MONTH) + 1))
                                    + "." + cal.get(GregorianCalendar.YEAR);
                }

                if (outDate.equals("") == false)
                {
                    publishDate += outDate;
                }

                if (format.equals("both"))
                {
                    publishDate += ", ";
                }

                if (outTime.equals("") == false)
                {
                    publishDate += outTime;
                }
            }

            out.print(publishDate);
        }
        catch (Exception e)
        {
            throw new Error("Config-Pfad konnte nicht erzeugt werden: "
                            + e.getMessage());
        }

        return SKIP_BODY;
    }

    /**
     * Setzt den Parameter #format#
     * 
     * @param _format
     */
    public void setFormat(String _format)
    {
        if (_format.equals("both") || _format.equals("time")
                        || _format.equals("both"))
        {
            this.format = _format;
        }
        else
        {
            this.format = DEFAULT_FORMAT;
        }
    }

    /**
     * Setzt den Parameter #sprintf#
     * 
     * @param _sprintf
     */
    public void setSprintf(String _sprintf)
    {
        this.sprintf = _sprintf;
    }

    /**
     * Setzt den Parameter #ts#
     * 
     * @param _ts
     */
    public void setTimeStamp(long _ts)
    {
        this.ts = _ts;
    }

    /**
     * Setzt den Parameter #ts#
     * 
     * @param _ut
     */
    public void setUnixTime(UnixTime _ut)
    {
        if (_ut != null)
        {
            this.ts = _ut.getTimeStamp();
        }
    }

    /**
     * setzt den Parameter #defaultString#
     * 
     * @param _defaultString
     */
    public void setDefaultString(String _defaultString)
    {
        this.defaultString = _defaultString;
    }
}
