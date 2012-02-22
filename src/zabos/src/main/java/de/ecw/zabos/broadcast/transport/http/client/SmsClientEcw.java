package de.ecw.zabos.broadcast.transport.http.client;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Hashtable;
import java.util.List;

import org.apache.log4j.Logger;

import de.ecw.zabos.broadcast.Recipient;
import de.ecw.zabos.license.License;
import de.ecw.zabos.types.TelefonNummer;

/**
 * Helper Klasse zum Encoden von SMS Requests für das ECW Gateway
 * 
 * 
 * @author bsp
 * @deprecated
 */
public class SmsClientEcw extends AbstractSmsClient
{
    public SmsClientEcw(License license)
    {
        super(license);
    }

    /**
     * Logging-Interface
     */
    private final static Logger log = Logger.getLogger(SmsClientEcw.class);

    /**
     * true = debug sms.ecw.de gateway script, false= send for real
     */
    private static final boolean ENABLE_DEBUG = false;

    /**
     * Host name of SMS gateway
     */
    protected String[] sms_service_hosts = new String[]
    { "sms.your-domain.local", "sms2.your-domain.local" };

    /**
     * Name of the gateway script (URI header)
     */
    private static final String SMS_SERVICE_SCRIPT = "/sms_send.php";

    /**
     * The secret is used to XOR-encode the request
     * (sender=<x>&recipient=<y>&content=<z>&type=<w>)
     */
    private static final String SMS_SECRET = "kdU)_L14$";

    /**
     * The charset to use for URL encoding of the sender, recipient and content
     * arguments
     */
    // private static final String URL_CHARSET = "UTF-8";
    private static final String URL_CHARSET = "ISO-8859-1";

    private static final Hashtable<String, String> status_codes = new Hashtable<String, String>();

    static
    {
        // Internal status codes
        status_codes.put("0", "idle");
        status_codes.put("7", "retrying");
        status_codes.put("8", "Time out");
        status_codes.put("9", "queued");
        status_codes.put("10", "SMS verschickt");
        status_codes.put("11", "Socket Fehler");
        status_codes.put("12", "Gateway Fehler");

        status_codes.put("200", "SMS erfolgreich übertragen");
        status_codes.put("100", "Keine Verbindung zum Datenbankserver");
        status_codes.put("101", "Keine Verbindung zur Datenbank");
        status_codes.put("102", "Datenbankfehler");
        status_codes.put("111", "Benutzer unbekannt");
        status_codes.put("112", "Authentifizierung fehlgeschlagen");
        status_codes.put("113", "Versandzeitpunkt falsch");
        status_codes.put("114", "Maximale Anzahl von Empfängern �berschritten");
        status_codes.put("400", "SMS-ONE: Schnittstellenfehler");
        status_codes.put("401", "SMS-ONE: Falsche Benutzerdaten");
        status_codes.put("402", "SMS-ONE: SMS-Nachricht fehlt");
        status_codes.put("403", "SMS-ONE: Empfängernummer fehlt");
        status_codes.put("404", "SMS-ONE: Falsche Absenderkennung");
        status_codes.put("405", "SMS-ONE: Kein Prepaid-Guthaben");
        status_codes.put("406", "SMS-ONE: SMS-Limit überschritten");
    }

    /**
     * Liefert die Service-Hosts
     */
    public String[] getSmsServiceHosts()
    {
        return sms_service_hosts;
    }

    /**
     * XOR-Verschlüsselung einer Zeichenkette mit einem "secret". Das Secret
     * wird wiederholt falls der zu verschlüsselnde String länger als das Secret
     * ist.
     */
    private static String encodeXOR(String _src, String _secret)
    {
        int srcLen = _src.length();
        int keyLen = _secret.length();
        byte[] dst = new byte[srcLen];
        for (int i = 0; i < srcLen; i++)
        {
            dst[i] = (byte) (_src.charAt(i) ^ _secret.charAt(i % keyLen));
        }
        return new String(dst);
    }

    /**
     * Baut aus den ggb. Parametern einen URL-encodete Aufrufstring zusammen. Es
     * werden die Daten des ersten Empf�ngers aus der ArrayList als
     * SMS-Informationen gewählt!
     * 
     * @param _r
     *            ArrayList mit den Empfängern
     * @return
     */
    public String buildSMSRequest(List<Recipient> _r)
    {
        if (_r.size() == 0)
        {
            log.error("Empty ArrayList given!");
            return "";
        }

        try
        {
            Recipient rTemplate = _r.get(0);

            TelefonNummer _sender = rTemplate.getAbsenderRufnummer();
            String content = rTemplate.getNachricht();

            StringBuffer sb = new StringBuffer();

            sb.append("user=");

            sb.append(URLEncoder.encode(getSmsUsername(), URL_CHARSET));
            sb.append("&password=");
            sb.append(URLEncoder.encode(getSmsPasswordMd5(), URL_CHARSET));
            if (rTemplate.getContext() != null)
            {
                sb.append("&context=");
                sb.append(URLEncoder
                                .encode(rTemplate.getContext(), URL_CHARSET));
            }
            if (rTemplate.getContextAlarm() != null)
            {
                sb.append("&alarmid=");
                sb.append(URLEncoder.encode(rTemplate.getContextAlarm(),
                                URL_CHARSET));
            }
            if (rTemplate.getContextO() != null)
            {
                sb.append("&organization=");
                sb.append(URLEncoder.encode(rTemplate.getContextO(),
                                URL_CHARSET));
            }
            if (rTemplate.getContextOE() != null)
            {
                sb.append("&unit=");
                sb.append(URLEncoder.encode(rTemplate.getContextOE(),
                                URL_CHARSET));
            }
            sb.append("&sender=");
            sb.append(URLEncoder.encode(_sender.getNummer(), URL_CHARSET));
            sb.append("&recipients=");

            // Empfaenger-Liste zusammenbauen
            for (int i = 0, m = _r.size(); i < m; i++)
            {
                sb.append(URLEncoder.encode(_r.get(i).getHandyNr().getNummer(),
                                URL_CHARSET));

                if ((i + 1) != m)
                {
                    sb.append(URLEncoder.encode(",", URL_CHARSET));
                }
            }

            sb.append("&content=");
            sb.append(URLEncoder.encode(content, URL_CHARSET));

            log.debug("raw SMS request=\"" + sb.toString() + "\"");

            // //log.debug("unencoded request = \"" + sb.toString() + "\".");

            // Den Request nun XOR verschluesseln
            String xorRequest = encodeXOR(sb.toString(), SMS_SECRET);

            StringBuffer uri = new StringBuffer();
            uri.append(SMS_SERVICE_SCRIPT);
            uri.append("?request=");
            uri.append(custUrlEncode(xorRequest));
            if (ENABLE_DEBUG)
            {
                uri.append("&debug=1");
            }

            return uri.toString();

        }
        catch (UnsupportedEncodingException e)
        {
            log.error("charset unsuported", e);
            return null;
        }
    }
}
