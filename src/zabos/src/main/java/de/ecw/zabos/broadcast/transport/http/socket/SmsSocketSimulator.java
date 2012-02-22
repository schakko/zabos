package de.ecw.zabos.broadcast.transport.http.socket;

import org.apache.log4j.Logger;

import de.ecw.zabos.broadcast.IBroadcaster;
import de.ecw.zabos.broadcast.transport.http.client.ISmsClient;

/**
 * Simuliert das Versenden der GET-Requests. Ist nicht innerhalb des
 * test-Packages, da man hiermit die Funktionsweise eines laufenden Systems
 * testen kann.
 * 
 * @author ckl
 */
public class SmsSocketSimulator implements ISmsSocket
{
    protected final static Logger log = Logger
                    .getLogger(SmsSocketSimulator.class);

    private ISmsClient smsClient;

    public SmsSocketSimulator(ISmsClient _smsClient)
    {
        setSmsClient(_smsClient);
    }

    public boolean checkSocket()
    {
        return true;
    }

    public boolean connect()
    {
        return true;
    }

    public boolean disconnect()
    {
        return true;
    }

    public String getDisplayTrackerId()
    {
        return "__trackerId__";
    }

    public String getResult()
    {
        return null;
    }

    public int getRetriesLeft()
    {
        return 0;
    }

    public ISmsClient getSmsClient()
    {
        return smsClient;
    }

    public int getStatusCode()
    {
        return IBroadcaster.SMS_STATUS_ANGEKOMMEN;
    }

    public boolean isRetrying()
    {
        return false;
    }

    public boolean sendGETRequest(String requestURI)
    {
        log.debug("Simuliere Senden des Strings \"" + requestURI + "\"");
        return true;
    }

    public void setSmsClient(ISmsClient smsClient)
    {
        this.smsClient = smsClient;
    }

    public void setTrackerId(String trackerId)
    {
    }

}
