package de.ecw.zabos.broadcast.transport.http.client;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import org.apache.log4j.Logger;

import de.ecw.zabos.broadcast.Recipient;
import de.ecw.zabos.license.License;
import de.ecw.zabos.types.TelefonNummer;

/**
 * Client für den Anbieter SMS-77
 * @author ckl
 * 
 */
public class SmsClientSms77 extends AbstractSmsClient
{
    /**
     * Konstruktor
     * 
     * @param license
     *            Lizenz-Datei muss für den Zugriff auf
     *            {@link License#getGatewayUser()} und
     *            {@link License#getGatewayPasswd()} übergeben werden
     */
    public SmsClientSms77(License license)
    {
        super(license);
    }

    /**
     * Benutzername und Passwort lassen sich übergeben
     * 
     * @param _smsUsername
     * @param _smsPassword
     */
    public SmsClientSms77(String _smsUsername, String _smsPassword)
    {
        super(_smsUsername, _smsPassword);
    }

    /**
     * Logging-Interface
     */
    private final static Logger log = Logger.getLogger(SmsClientSms77.class);

    /**
     * Host name of SMS gateway
     */
    protected String[] sms_service_hosts = new String[]
    { "gateway.sms77.de" };

    /**
     * Name of the gateway script (URI header)
     */
    private static final String SMS_SERVICE_SCRIPT = "/";

    /**
     * Typ: Qualität
     */
    public final static String TYPE_QUALITY = "quality";

    /**
     * Typ: Festnetz
     */
    public final static String TYPE_FESTNETZ = "festnetz";

    /**
     * The charset to use for URL encoding of the sender, recipient and content
     * arguments
     */
    // private static final String URL_CHARSET = "UTF-8";
    private static final String URL_CHARSET = "ISO-8859-1";

    /**
     * Liefert die Service-Hosts zurück
     */
    public String[] getSmsServiceHosts()
    {
        return sms_service_hosts;
    }

    /**
     * Baut aus den ggb. Parametern einen URL-encodete Aufrufstring zusammen. Es
     * werden die Daten des ersten Empfängers aus der ArrayList als
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

            sb.append("u=");
            sb.append(URLEncoder.encode(getSmsUsername(), URL_CHARSET));
            sb.append("&p=");
            sb.append(URLEncoder.encode(getSmsPassword(), URL_CHARSET));

            boolean isFestnetzSms = false;

            sb.append("&from=");
            sb.append(URLEncoder.encode(_sender.getNummer(), URL_CHARSET));

            sb.append("&to=");
            // Empfaenger-Liste zusammenbauen
            for (int i = 0, m = _r.size(); i < m; i++)
            {
                Recipient r = _r.get(i);

                sb.append(URLEncoder.encode(r.getHandyNr().getNummer(),
                                URL_CHARSET));

                if (!isFestnetzSms && r.getSmsOutVO().isFestnetzSms())
                {
                    isFestnetzSms = true;
                }

                if ((i + 1) != m)
                {
                    sb.append(URLEncoder.encode(",", URL_CHARSET));
                }
            }

            // Tarif hinzufügen
            String type = TYPE_QUALITY;

            if (isFestnetzSms)
            {
                type = TYPE_FESTNETZ;
            }

            sb.append("&type=");
            sb.append(type);

            sb.append("&text=");
            String content_linebreaks = content.replaceAll("\n", "\r\n");
            String content_encoded = URLEncoder.encode(content_linebreaks,
                            URL_CHARSET);
            sb.append(content_encoded);

            StringBuffer uri = new StringBuffer();
            uri.append(SMS_SERVICE_SCRIPT);
            uri.append("?");
            uri.append(sb);

            log
                            .debug("raw SMS request=\""
                                            + uri.toString().replaceAll(
                                                            getSmsPassword(),
                                                            "xxx").replaceAll(
                                                            getSmsUsername(),
                                                            "xxx") + "\"");

            return uri.toString();
        }
        catch (UnsupportedEncodingException e)
        {
            log.error("charset unsuported", e);
            return null;
        }
    }
}
