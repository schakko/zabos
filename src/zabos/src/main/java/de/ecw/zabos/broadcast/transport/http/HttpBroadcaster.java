package de.ecw.zabos.broadcast.transport.http;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import de.ecw.zabos.SpringContext;
import de.ecw.zabos.broadcast.IBroadcaster;
import de.ecw.zabos.broadcast.Recipient;
import de.ecw.zabos.broadcast.transport.http.socket.ISmsSocket;

/**
 * Arbeitet einen "Broadcast-Job" ab
 * 
 * @author bsp
 * @deprecated
 * @since 2006-06-12 SmsBroadcasterGroup ersetzt SmsBroadcaster
 */
public class HttpBroadcaster implements Runnable
{

    private final static Logger log = Logger.getLogger(HttpBroadcaster.class);

    /**
     * Maximale Anzahl von Verbindungen zum SMS Gateway
     */
    private static final int MAX_CONNECTIONS = 10;

    private static final long SMS_SOCKET_CHECK_INTERVAL = 200;

    private int numConnections;

    private int numFinishedCalls;

    private List<Recipient> recipients;

    private ISmsSocket[] sockets;

    private long msStart;

    private long msEnd;

    /**
     * 
     * @param _recipients
     *            ArrayList<Recipient>
     * @param _from
     * @param _content
     * @param _notifyCallback
     * @param _finalizeCallback
     */
    public HttpBroadcaster(ArrayList<Recipient> _recipients)
    {
        recipients = _recipients;
    }

    public void run()
    {
        int totalNumRetries = 0;

        msStart = System.currentTimeMillis();

        int numPersons = recipients.size();

        sockets = new ISmsSocket[numPersons];

        numConnections = 0;

        numFinishedCalls = 0;

        for (int i = 0; i < numPersons; i++)
        {
            Recipient r = (Recipient) recipients.get(i);
            r.setStatusCode(IBroadcaster.SMS_STATUS_QUEUED);
        }

        // Solange die gesendeten SMS noch nicht der Anzahl der Personen
        // entspricht
        while (numFinishedCalls != numPersons)
        {
            // Initiate new connections until connection limit is reached
            for (int i = 0; (i < numPersons)
                            && (numConnections != MAX_CONNECTIONS); i++)
            {
                Recipient r = (Recipient) recipients.get(i);

                // Wenn die SMS noch nicht gesendet ist
                if ((r.getStatusCode() == IBroadcaster.SMS_STATUS_QUEUED)
                                || (r.getStatusCode() == IBroadcaster.SMS_STATUS_RETRY))
                {
                    ISmsSocket socket = sockets[i];

                    // Socket existiert noch nicht
                    if (socket == null)
                    {
                        // Fuer die Person einen Socket erstellen
                        socket = (ISmsSocket) SpringContext.getInstance()
                                        .getBean(SpringContext.BEAN_SMS_SOCKET,
                                                        ISmsSocket.class);
                        sockets[i] = socket;
                    }

                    // Verbindung herstellen
                    socket.connect();

                    ArrayList<Recipient> alRecp = new ArrayList<Recipient>();
                    alRecp.add(r);

                    socket.sendGETRequest(socket.getSmsClient()
                                    .buildSMSRequest(alRecp));
                    // log.debug("sent GET request to Person \"" +
                    // p.getSurname() + ", "
                    // + p.getName() + " host=\"" + socket.getHostString() +
                    // "\".");

                    r.setStatusCode(socket.getStatusCode());

                    // Anzahl der Verbindungen, die aufgebaut sind
                    numConnections++;
                }
            }

            try
            {
                Thread.sleep(SMS_SOCKET_CHECK_INTERVAL);
            }
            catch (InterruptedException e)
            {
                log.error(e);
            }

            for (int i = 0; i < numPersons; i++)
            {
                Recipient recipient = (Recipient) recipients.get(i);

                // log.debug("check sockets");
                ISmsSocket socket = sockets[i];
                if (socket != null)
                {
                    boolean bSocketClosed = socket.checkSocket();
                    recipient.setStatusCode(socket.getStatusCode());
                    if (bSocketClosed)
                    {
                        log.debug("connection closed (handynr=\""
                                        + recipient.getHandyNr() + "\").");
                        log.debug(" return_msg=\"" + socket.getResult() + "\".");
                        numConnections--;
                        if (!socket.isRetrying())
                        {
                            numFinishedCalls++;
                            sockets[i] = null;
                        }
                        else
                        {
                            log.debug("retrying..last result was \""
                                            + socket.getResult() + "\".");
                            totalNumRetries++;
                        }
                    }
                }
            }

            // /log.debug("num_finished_calls = " + )
        } // num_finished_calls != numPersons
        log.debug("all calls finished.");

        msEnd = System.currentTimeMillis();

        long delta = msEnd - msStart;

        log.debug("It took " + ((delta / 1000) / 60) + "min, "
                        + ((delta / 1000) % 1000) + "sec and " + (delta % 1000)
                        + "ms to send " + numPersons + " SMS messages ("
                        + totalNumRetries + " retries needed).");

    }

    public String toString()
    {
        return "Protokoll [HTTP], Sendemodus: [Single]";
    }
}
