package de.ecw.zabos.broadcast.transport.http.socket;

import org.apache.log4j.Logger;

import de.ecw.zabos.broadcast.IBroadcaster;
import de.ecw.zabos.broadcast.transport.http.client.ISmsClient;
import de.ecw.zabos.broadcast.transport.http.socket.ISmsSocket;

public class SmsSocketStub implements ISmsSocket
{
    private final static Logger log = Logger.getLogger(SmsSocketStub.class);

    private ISmsClient smsClient;

    private String trackerId;

    public SmsSocketStub(ISmsClient _smsClient)
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
        return trackerId;
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
        log.debug("Sending " + requestURI);

        return true;
    }

    final public void setSmsClient(ISmsClient _smsClient)
    {
        smsClient = _smsClient;
    }

    public void setTrackerId(String _trackerId)
    {
        trackerId = _trackerId;
    }
}
