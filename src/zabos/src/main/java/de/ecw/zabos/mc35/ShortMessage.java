package de.ecw.zabos.mc35;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

/**
 * Interne Datenstruktur, die beim Empfang von SMS Nachrichten aus der MC35
 * Hardware verwendet wird
 * 
 * @author bsp
 * 
 */
public class ShortMessage
{

    private final static Logger log = Logger.getLogger(ShortMessage.class);

    // private int port_number;

    /**
     * ME message id (in the ME)
     */
    private int id;

    /**
     * Caller-ID in 01719562825 format
     */
    private String phoneNumber;

    /**
     * Reception date in yy/mm/dd,hh:mm:ss+ms format
     */
    private Date date;

    /**
     * Message
     */
    private String body;

    public ShortMessage(String _phoneNumber, String _body)
    {
        id = 1;
        date = Calendar.getInstance().getTime();
        body = _body;
        setPhoneNumber(_phoneNumber);
    }

    /**
     * 
     * @param _id
     * @param _phoneNumber
     * @param _date
     *            <strong>Must be</strong> "YY/MM/DD,HH:II:SS+ss" (without
     *            quote). If _date is not compliant, the current date will be
     *            used as fallback
     */
    public ShortMessage(/* int _portNumber, */String _id, String _phoneNumber,
                    String _date)
    {

        // Remember the port number on which this message has been received
        // port_number = _portNumber;

        // Parse ME message id
        try
        {
            id = Integer.parseInt(_id);
        }
        catch (NumberFormatException e)
        {
            log.error("Failed to parse short message ID [" + _id
                            + "] to integer: " + e.getMessage());
        }

        // Parse caller ID
        setPhoneNumber(_phoneNumber);

        // Parse GSM date string
        GregorianCalendar cal = new GregorianCalendar(TimeZone
                        .getTimeZone("Europe/Berlin"));

        try
        {
            // use regular expression for consuming all date parts
            Pattern p = Pattern.compile("[\\/|,|\\+|:]+");
            String[] dateParts = p.split(_date);

            cal.set(Calendar.YEAR, 2000 + Integer.parseInt(dateParts[0]));
            cal.set(Calendar.MONTH, Integer.parseInt(dateParts[1]) - 1);
            cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateParts[2]));
            cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(dateParts[3]));
            cal.set(Calendar.MINUTE, Integer.parseInt(dateParts[4]));
            cal.set(Calendar.SECOND, Integer.parseInt(dateParts[5]));
            cal.set(Calendar.MILLISECOND, 10 * Integer.parseInt(dateParts[6]));
        }
        catch (Exception e)
        {
            log.error("Failed to convert DATE \"" + _date + "\", ["
                            + e.getMessage() + "]; using current time");

            cal = new GregorianCalendar(TimeZone.getTimeZone("Europe/Berlin"));
        }

        date = cal.getTime();

        log.debug("converted DATE \"" + _date + "\" to \"" + date.toString()
                        + "\"");

        body = null;
    }

    public void debug()
    {
        // log.debug(" port#: " + port_number);
        log.debug("    id: " + id);
        log.debug("number: " + phoneNumber);
        log.debug("  date: " + date);
        log.debug("  body: ***" + body + "***");
        log.debug("EOL");
    }

    public void setBody(String _body)
    {
        body = _body;
    }

    public int getId()
    {
        return id;
    }

    public void setPhoneNumber(String _phoneNumber)
    {
        if (_phoneNumber.charAt(0) == '+') // +49171...
        {
            phoneNumber = "0" + _phoneNumber.substring(3);
        }
        else
        {
            phoneNumber = _phoneNumber;
        }
    }

    public String getPhoneNumber()
    {
        return phoneNumber;
    }

    public Date getDate()
    {
        return date;
    }

    public String getBody()
    {
        return body;
    }
}
