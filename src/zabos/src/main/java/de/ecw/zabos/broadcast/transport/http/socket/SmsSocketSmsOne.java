package de.ecw.zabos.broadcast.transport.http.socket;

import org.apache.log4j.Logger;

import de.ecw.zabos.broadcast.IBroadcaster;
import de.ecw.zabos.broadcast.transport.http.client.ISmsClient;

/**
 * Helper-Klasse für das Socket-Interface von SMS-One. Der Zugriff erfolgt über
 * die verschiedenen Gateways gate.sms-one.de gate.emediaserver.de
 * 
 * @author ckl
 * 
 */
public class SmsSocketSmsOne extends AbstractSmsSocket
{
    private final static Logger log = Logger.getLogger(SmsSocketSmsOne.class);

    /**
     * Konstruktor
     * 
     * @param smsClient
     */
    public SmsSocketSmsOne(ISmsClient smsClient)
    {
        super(smsClient);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.ecw.zabos.sms.gateway.socket.AbstractSmsSocket#extractStatusCodeFromResult
     * ()
     */
    protected void extractStatusCodeFromResult()
    {
        // 2006-08-18 CKL: Status-Code wird nun auch aus chunked Bodys gelesen
        String[] lines = result.split("\n");
        boolean bIsTransferEncodingChunked = false;
        boolean bIsInHeader = true;
        int iLinesInBody = 1;

        if (lines.length == 0)
        {
            // Gateway konnte nicht erreicht werden bzw. wurden keine
            // Status-Codes gesendet
            log.debug(getDisplayTrackerId() + "No repsonse from gateway");
            retry(IBroadcaster.SMS_STATUS_GATEWAY_ERROR);
        }

        // Zeilen parsen
        for (int i = 0, m = lines.length; i < m; i++)
        {
            // Zeile trimmen
            lines[i] = lines[i].trim();

            // Transfer-Encoding wurde noch nicht gefunden und der Header wird
            // noch geparst
            if (!bIsTransferEncodingChunked && bIsInHeader)
            {
                // ueberpruefen, ob im Header das Transfer-Encoding angeschaltet
                // ist
                if (lines[i].matches("(.*)[tT]ransfer\\-[eEncoding](.*)chunked(.*)"))
                {
                    log.debug(getDisplayTrackerId()
                                    + "Transfer-Encoding is enabled");
                    bIsTransferEncodingChunked = true;
                }
            }

            // Body wird geparst
            if (!bIsInHeader)
            {
                if ((bIsTransferEncodingChunked == true && iLinesInBody == 2)
                                || (bIsTransferEncodingChunked == false))
                {
                    String statusString = lines[i];

                    // Fehler vom Gateway empfangen.
                    // Moegliche Ursachen sind: Falsche Account-Daten,
                    // Schnittstellenfehler
                    if (statusString.contains("error"))
                    {
                        log.debug(getDisplayTrackerId()
                                        + "parsed status string: "
                                        + statusString);
                        log.debug(getDisplayTrackerId()
                                        + "server returned an error as status string");
                        retry(IBroadcaster.SMS_STATUS_GATEWAY_ERROR);
                    }
                    // SMS Gateway hat gueltige Antwort geliefert
                    else if (statusString.contains("OK"))
                    {
                        log.debug(getDisplayTrackerId()
                                        + "line contains magic keyword OK: "
                                        + statusString);
                        finish(IBroadcaster.SMS_STATUS_ANGEKOMMEN);
                    }
                    else
                    {
                        // 2006-05-30 CKL: Ganz wichtig: Result-Code anzeigen,
                        // damit es naehere Infos zum Fehler gibt
                        log.debug(getDisplayTrackerId()
                                        + "invalid statusString \""
                                        + statusString + "\".");
                        log.debug(getDisplayTrackerId() + "  full result: \""
                                        + result + "\"");
                        retry(IBroadcaster.SMS_STATUS_GATEWAY_ERROR);
                    }

                    return;
                }

                iLinesInBody++;
            }

            // Header Seperator
            if (lines[i].length() == 0)
            {
                log.debug(getDisplayTrackerId()
                                + "header seperator in response found");
                bIsInHeader = false;
            }
        }

        // 2007-01-18 CKL: Bugfix gegen Endlos-Schleife
        log.error(getDisplayTrackerId()
                        + "did not receive any status from gateway!");
        retry(IBroadcaster.SMS_STATUS_GATEWAY_ERROR);
    }
}
