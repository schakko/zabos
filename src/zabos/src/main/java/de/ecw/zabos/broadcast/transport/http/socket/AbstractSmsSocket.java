package de.ecw.zabos.broadcast.transport.http.socket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.apache.log4j.Logger;

import de.ecw.zabos.broadcast.IBroadcaster;
import de.ecw.zabos.broadcast.transport.http.client.ISmsClient;

/**
 * Abstrakte Klasse, die die Standard-Methoden und Eigenschaften der SMS-Sockets
 * bereitstellen
 * 
 * @author ckl
 * 
 */
abstract public class AbstractSmsSocket implements ISmsSocket
{
    /**
     * Mit diesem Host werden die ersten MAX_RETRIES_PER_HOST
     * Verbindungsversuche probiert
     * 
     */
    protected int defaultHostIndex = 0;

    /**
     * Number of milliseconds until the request is aborted with
     * SMS_STATUS_TIME_OUT<br />
     * 2006-11-08 CST: Der Timeout wird auf 30 Sekunden festgelegt, da ein
     * kuerzerer Wert zu Fehlern führen kann.
     */
    protected static final long TIME_OUT = 1000 * 30;

    /**
     * Aktueller Offset im SmsClient.SMS_SERVICE_HOSTS array
     * 
     */
    protected int hostIndex;

    /**
     * Liefert zurück, ob eine Proxy-Verbindung genutzt wird
     */
    private boolean isProxyUsed = false;

    /**
     * Aktueller Host
     */
    protected String host_string;

    /**
     * Ergebnis des letzten Sende-Versuchs
     */
    protected int last_retry_status_code;

    protected final static Logger log = Logger
                    .getLogger(AbstractSmsSocket.class);

    /**
     * Anzahl der Versuche der Kommunikation mit einem Host.
     * 
     * 2006-08-19 CKL: Retries auf 2 herabgesetzt<br/>
     * 2006-11-08 CST: Retries auf 1 herabgesetzt damit bei Problemen nicht zu
     * viele SMS versendet werden. <br />
     * 2007-02-02 CKL: Refactoring der Konstante von MAX_RETRIES_PER_HOST zu
     * MAX_TRIES_PER_HOST. Der Name MAX_TRIES_PER_HOST ist irreführend.
     * 
     * @since 2007-02-02: MAX_TRIES_PER_HOST = 1 bedeutet, dass jeder Host genau
     *        einmal kontaktiert wird - und nicht zweimal (RETRY)
     */
    public final static int MAX_TRIES_PER_HOST = 1;

    /**
     * Port-Nummer
     */
    protected int portNumber;

    /**
     * Result-String
     */
    protected String result;

    /**
     * Countdown für den Host-Wechsel
     */
    protected int retryCountdown;

    /**
     * Wird gerade ein neuer Versuch gestartet?
     */
    protected boolean retrying;

    private ISmsClient smsClient;

    /**
     * Socket
     */
    protected Socket socket;

    /**
     * Startzeit dieser Socket-Verbindung
     */
    protected long start_time;

    /**
     * Status-Code
     */
    protected int status_code;

    /**
     * Id fuer Nachverfolgung der einzelnen Verbindungsversuche
     */
    protected String tracker_id = "unknown";

    /**
     * Default-Konstruktor
     * 
     * @param _port
     */
    public AbstractSmsSocket(ISmsClient _smsClient)
    {
        setSmsClient(_smsClient);

        // //host_string = _host;
        portNumber = _smsClient.getSmsServicePort();
        status_code = IBroadcaster.SMS_STATUS_IDLE;
        retryCountdown = getSmsClient().getSmsServiceHosts().length
                        * MAX_TRIES_PER_HOST;
        last_retry_status_code = status_code;
        retrying = false;
        hostIndex = defaultHostIndex;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.ecw.zabos.sms.gateway.socket.ISmsSocket#checkSocket()
     */
    public boolean checkSocket()
    {
        boolean bDisconnected = true;

        if (socket != null)
        {
            try
            {
                bDisconnected &= socket.isConnected();

                InputStream in = socket.getInputStream();

                int numAvailable = in.available();
                if (numAvailable > 0)
                {
                    StringBuffer sb = new StringBuffer();
                    for (int i = 0; i < numAvailable; i++)
                    {
                        int c = in.read();
                        if (c == 0)
                        {
                            bDisconnected = true;
                        }
                        else
                        {
                            sb.append((char) (c & 0xff));
                        }
                    }

                    if (result == null)
                    {
                        // first packet
                        result = sb.toString();
                        // //log.debug("first packet = " + result);
                    }
                    else
                    {
                        result = result + sb.toString();
                    }

                    if (bDisconnected)
                    {
                        // status_code = SmsClient.SMS_STATUS_ANGEKOMMEN;
                        extractStatusCodeFromResult();
                        disconnect();
                    }

                }
                else
                {
                    // Continue to check socket activity
                    bDisconnected = false;
                }

            }
            catch (IOException e)
            {
                log.error(getDisplayTrackerId()
                                + "failed to read from socket \"" + host_string
                                + "\".", e);
                e.printStackTrace();
                retry(IBroadcaster.SMS_STATUS_SOCKET_ERROR);
            }

            if (!bDisconnected)
            {
                if ((System.currentTimeMillis() - start_time) > TIME_OUT)
                {
                    disconnect();
                    retry(IBroadcaster.SMS_STATUS_TIME_OUT);
                }
            }

        }
        return bDisconnected;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.ecw.zabos.sms.gateway.socket.ISmsSocket#connect()
     */
    public boolean connect()
    {
        try
        {
            host_string = getSmsClient().getSmsServiceHosts()[hostIndex];

            String gatewayHost = host_string;
            int gatewayPort = portNumber;

            if ((getSmsClient().getProxyServer() != null)
                            && (getSmsClient().getProxyServer().length() > 0))
            {
                isProxyUsed = true;

                gatewayHost = getSmsClient().getProxyServer();

                if ((getSmsClient().getProxyPort() > 0)
                                && (getSmsClient().getProxyPort() != gatewayPort))
                {
                    gatewayPort = getSmsClient().getProxyPort();
                }

                log.info("using Proxy-Server " + gatewayHost + ":"
                                + gatewayPort + " for destination "
                                + host_string);
            }

            socket = new Socket(gatewayHost, gatewayPort);

            if (!socket.isBound())
            {
                log.error(getDisplayTrackerId() + "failed to bind to socket \""
                                + host_string + "\".");
                socket = null;
                retry(IBroadcaster.SMS_STATUS_SOCKET_ERROR);
                return false;
            }
            else
            {
                // Socket has been opened
                log.debug(getDisplayTrackerId() + "socket to \"" + host_string
                                + "\" opened (local port: "
                                + socket.getLocalPort() + ", socket-timeout: "
                                + socket.getSoTimeout() + ", socket-linger: "
                                + socket.getSoLinger() + ", keep-alive: "
                                + socket.getKeepAlive() + ", tcp-no-delay: "
                                + socket.getTcpNoDelay() + ")");
                return true;
            }
        }
        catch (IOException e)
        {
            log.error(getDisplayTrackerId() + "failed to open socket to \""
                            + host_string + "\".");
            retry(IBroadcaster.SMS_STATUS_SOCKET_ERROR);
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.ecw.zabos.sms.gateway.socket.ISmsSocket#disconnect()
     */
    public boolean disconnect()
    {
        if (socket != null)
        {
            try
            {
                log.debug(getDisplayTrackerId() + "disconnecting from host \""
                                + host_string + "\".");
                socket.close();
            }
            catch (IOException e)
            {
                log.error(getDisplayTrackerId() + "failed to close socket \""
                                + host_string + "\".");
            }
            socket = null;
            return true;
        }
        return false;
    }

    /**
     * Extrahiert aus dem Ergebnis der HTTP-Verbindung den Status-Code
     * 
     * @author ckl
     * @since 200708.01.2007_15:16:00
     */
    protected abstract void extractStatusCodeFromResult();

    /**
     * Beendet die Abfrage und legt den aktuellen Host als Default-Host fest
     * 
     * @param _statusCode
     */
    protected void finish(int _statusCode)
    {
        retrying = false;
        status_code = _statusCode;
        // Akt. Host als Default Host für nächste Request verwenden
        defaultHostIndex = hostIndex;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.ecw.zabos.sms.gateway.socket.ISmsSocket#getDisplayTrackerId()
     */
    public String getDisplayTrackerId()
    {
        return "[" + tracker_id + "] ";
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.ecw.zabos.sms.gateway.socket.ISmsSocket#getResult()
     */
    public String getResult()
    {
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.ecw.zabos.sms.gateway.socket.ISmsSocket#getRetriesLeft()
     */
    public int getRetriesLeft()
    {
        return retryCountdown;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.ecw.zabos.sms.gateway.socket.ISmsSocket#getSmsClient()
     */
    public ISmsClient getSmsClient()
    {
        return smsClient;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.ecw.zabos.sms.gateway.socket.ISmsSocket#getStatusCode()
     */
    public int getStatusCode()
    {
        return status_code;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.ecw.zabos.sms.gateway.socket.ISmsSocket#isRetrying()
     */
    public boolean isRetrying()
    {
        return retrying;
    }

    /**
     * Verbindungsversuch wiederholen. Für jeden Host werden
     * MAX_RETRIES_PER_HOST Verbindungsversuche durchgeführt; dann wird versucht
     * zum nächsten Backup-Host zu connecten. Wenn alle
     * (MAX_RETRIES_PER_HOST*NUM_HOSTS) Versuche fehlschlagen wird die SMS als
     * nicht zustellbar gekennzeichnet.<br />
     * 2007-02-02 CKL: Countdown der Retries umgebaut. Es wurden während der
     * Probephase z.T. drei SMS' pro Alarm gesendet.
     * 
     * @param _statusCode
     */
    protected void retry(int _statusCode)
    {
        retryCountdown--;

        if (retryCountdown <= 0)
        {
            status_code = _statusCode;
            retrying = false;
            log.debug(getDisplayTrackerId() + "all retries failed ("
                            + getSmsClient().getSmsServiceHosts().length
                            + " hosts, " + MAX_TRIES_PER_HOST
                            + " tries per host) !");
        }
        else
        {
            last_retry_status_code = _statusCode;
            status_code = IBroadcaster.SMS_STATUS_RETRY;
            retrying = true;
            result = null;

            // Ist die max. Zahl an Retries f�r den akt. Host �berschritten?
            if ((retryCountdown % MAX_TRIES_PER_HOST) == 0)
            {
                // Naechsten Host probieren
                hostIndex = (hostIndex + 1)
                                % getSmsClient().getSmsServiceHosts().length;
            }

            log.debug(getDisplayTrackerId()
                            + "retrying (next retry on host idx " + hostIndex
                            + ", total retries left: " + retryCountdown
                            + ") ...");
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.ecw.zabos.sms.gateway.socket.ISmsSocket#sendGETRequest(java.lang.String
     * )
     */
    public boolean sendGETRequest(String _requestURI)
    {
        if (socket != null)
        {
            try
            {
                OutputStream os = socket.getOutputStream();

                StringBuffer msg = new StringBuffer();
                msg.append("GET ");

                // wenn Proxy benutzt wird, muss der GET-String anders aussehen
                if (isProxyUsed)
                {
                    msg.append("http://");
                    msg.append(host_string);
                    msg.append(":");
                    msg.append(getSmsClient().getSmsServicePort());
                }

                msg.append(_requestURI);
                msg.append(" HTTP/1.1\n");
                msg.append("User-Agent: Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.8) Gecko/20051111 Firefox/1.5");
                msg.append("\n");
                msg.append("Host: ");
                msg.append(host_string);
                msg.append("\n");
                msg.append("Accept: text/plain, text/html\n");
                msg.append("Accept-Charset: ISO-8859-1,utf-8;q=0.7,*;q=0.7\n");
                // 2007-04-17 CKL: Keep-Alive deaktiviert, ansonsten Probleme mit Astaro HTTP-Proxy
                // msg.append("Keep-Alive: 300\n");
                // msg.append("Connection: keep-alive\n");
                msg.append("Connection: close\n");

                msg.append("\n");

                String msgString = msg.toString();

                os.write(msgString.getBytes());

                os.flush();

                status_code = IBroadcaster.SMS_STATUS_VERSCHICKT;

                start_time = System.currentTimeMillis();

                log.debug(getDisplayTrackerId()
                                + "given GET request is sent. string contains confidential data and is not showed in here.");

            }
            catch (IOException e)
            {
                log.error(getDisplayTrackerId()
                                + "failed to send GET request to \""
                                + host_string + "\".");
                retry(IBroadcaster.SMS_STATUS_SOCKET_ERROR);
            }
            return true;
        }

        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.ecw.zabos.sms.gateway.socket.ISmsSocket#setSmsClient(de.ecw.zabos.
     * sms.gateway.client.ISmsClient)
     */
    final public void setSmsClient(ISmsClient smsClient)
    {
        this.smsClient = smsClient;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.ecw.zabos.sms.gateway.socket.ISmsSocket#setTrackerId(java.lang.String)
     */
    public void setTrackerId(String _trackerId)
    {
        tracker_id = _trackerId;
    }
}
