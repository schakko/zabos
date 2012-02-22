package de.ecw.zabos.broadcast.transport.http.client;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import org.apache.log4j.Logger;

import de.ecw.zabos.broadcast.Recipient;
import de.ecw.zabos.license.License;
import de.ecw.zabos.types.TelefonNummer;

/**
 * Client für SMS-One.
 * 
 * @author ckl
 * 
 */
public class SmsClientSmsOne extends AbstractSmsClient
{
    /**
     * Konstruktor
     * 
     * @param license
     *            Lizenz-Datei muss für den Zugriff auf
     *            {@link License#getGatewayUser()} und
     *            {@link License#getGatewayPasswd()} übergeben werden
     */
    public SmsClientSmsOne(License license)
    {
        super(license);
    }

    /**
     * Logging-Interface
     */
    private final static Logger log = Logger.getLogger(SmsClientSmsOne.class);

    /**
     * Host name of SMS gateway
     */
    protected String[] sms_service_hosts = new String[]
    { "gate.emediaserver.de", "gate.sms-one.de" };

    /**
     * Name of the gateway script (URI header)
     */
    private static final String SMS_SERVICE_SCRIPT = "/sms.php4";

    /**
     * The charset to use for URL encoding of the sender, recipient and content
     * arguments
     */
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

            sb.append("user=");
            sb.append(URLEncoder.encode(getSmsUsername(), URL_CHARSET));
            sb.append("&pw=");
            sb.append(URLEncoder.encode(getSmsPasswordMd5(), URL_CHARSET));

            // Tarif hinzufügen
            sb.append("&tarif=2");

            sb.append("&snr=");
            sb.append(URLEncoder.encode(_sender.getNummer(), URL_CHARSET));

            sb.append("&dnr=");
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

            sb.append("&msg=");
            String content_linebreaks = content.replaceAll("\n", "\r\n");
            String content_encoded = URLEncoder.encode(content_linebreaks,
                            URL_CHARSET);
            sb.append(content_encoded);

            StringBuffer uri = new StringBuffer();
            uri.append(SMS_SERVICE_SCRIPT);
            uri.append("?");
            uri.append(sb);

            log.debug("raw SMS request=\""
                            + uri.toString()
                                            .replaceAll(getSmsPasswordMd5(),
                                                            "xxx")
                                            .replaceAll(getSmsUsername(), "xxx")
                            + "\"");

            return uri.toString();
        }
        catch (UnsupportedEncodingException e)
        {
            log.error("charset unsuported", e);
            return null;
        }
    }
}
