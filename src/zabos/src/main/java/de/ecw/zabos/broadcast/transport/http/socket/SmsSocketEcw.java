package de.ecw.zabos.broadcast.transport.http.socket;

import de.ecw.zabos.broadcast.IBroadcaster;
import de.ecw.zabos.broadcast.transport.http.client.ISmsClient;

/**
 * SMS-Socket für ECW-Gateway
 * 
 * @deprecated
 * @author ckl
 * 
 */
public class SmsSocketEcw extends AbstractSmsSocket
{
    public SmsSocketEcw(ISmsClient smsClient)
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

        int sc = 0;

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

                    try
                    {
                        sc = Integer.valueOf(statusString);

                        log.debug(getDisplayTrackerId()
                                        + "parsed status string: " + sc);

                        // ckl: if (sc != SmsClient.SMS_STATUS_ANGEKOMMEN)
                        if (sc < IBroadcaster.SMS_STATUS_ANGEKOMMEN)
                        {
                            retry(IBroadcaster.SMS_STATUS_GATEWAY_ERROR);
                        }
                        else
                        {
                            // SMS Gateway hat gueltige Antwort geliefert
                            finish(sc);
                        }
                    }
                    catch (NumberFormatException e)
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

        // 2007-01-18 CKL: Bugfix fuer Endlos-Schleife
        log.error(getDisplayTrackerId()
                        + "did not receive any status from gateway!");
        retry(IBroadcaster.SMS_STATUS_GATEWAY_ERROR);
    }

}
