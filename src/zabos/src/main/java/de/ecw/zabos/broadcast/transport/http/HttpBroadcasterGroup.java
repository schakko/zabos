package de.ecw.zabos.broadcast.transport.http;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import de.ecw.zabos.SpringContext;
import de.ecw.zabos.broadcast.BroadcasterAdapter;
import de.ecw.zabos.broadcast.IBroadcaster;
import de.ecw.zabos.broadcast.Recipient;
import de.ecw.zabos.broadcast.transport.http.socket.ISmsSocket;
import de.ecw.zabos.util.StringUtils;

/**
 * Arbeitet einen "Broadcast-Job" ab Im Gegensatz zu bsps'
 * {@link HttpBroadcaster} Klasse, sendet diese Klasse nur einen GET-Request pro
 * Empfänger-Gruppe. Die Idee dahinter ist, dass der Datendurchsatz verringert
 * und die Performance der SMS-Versendung gesteigert wird.
 * 
 * @author ckl
 */
public class HttpBroadcasterGroup extends BroadcasterAdapter
{
    private final static Logger log = Logger
                    .getLogger(HttpBroadcasterGroup.class);

    /**
     * Maximale Anzahl von Verbindungen zum SMS Gateway
     */
    private static final int MAX_CONNECTIONS = 10;

    /**
     * Maximale Anzahl von Empfaengern pro Gruppe
     */
    private static final int MAX_RECIPIENTS_PER_GROUP = 15;

    private static final long SMS_SOCKET_CHECK_INTERVAL = 200;

    /**
     * Anzahl der geöffneten Connections
     */
    private int numConnections;

    /**
     * Anzahl der erledigten Aufrufe
     */
    private int numFinishedCalls;

    /**
     * ID der ausgehenden Verbindung
     */
    private long idSmsout = 0;

    /**
     * Geöffnete Sockets zum Verschicken der SMSen
     */
    private ISmsSocket[] sockets;

    private long msStart;

    private long msEnd;

    /*
     * (non-Javadoc)
     * 
     * @see de.ecw.zabos.sms.gateway.broadcast.IBroadcaster#run()
     */
    @SuppressWarnings("unchecked")
    public void run()
    {
        if (getRecipients() == null)
        {
            log.error("Es wurden keine Empfaenger fuer die SmsBroadcaster-Gruppe gesetzt");
            return;
        }

        int totalNumRetries = 0;

        msStart = System.currentTimeMillis();

        int numPersons = getRecipients().size();

        // Anzahl der Gruppen
        int numGroups = 0;

        // Hashmap mit den Gruppen
        // Key ist MD5 aus der Nachricht, Value ist ArrayList mit den
        // Empfaengern
        Map<String, ArrayList<Recipient>> mapGruppen = new HashMap<String, ArrayList<Recipient>>();

        // Gruppen erstellen
        for (int i = 0, m = getRecipients().size(); i < m; i++)
        {
            // Empfaenger
            Recipient recipient = (Recipient) getRecipients().get(i);

            // SMS-Informationen setzen
            if (idSmsout == 0)
            {
                idSmsout = recipient.getSmsOutId();
            }

            // Statuscode setzen
            recipient.setStatusCode(IBroadcaster.SMS_STATUS_QUEUED);

            int slotNummer = 1;

            // Solange keine Gruppe mit einem offenen Slot gefunden wird =>
            // cyclen
            boolean leereGruppeGefunden = false;

            // Key der Gruppe
            String keyGruppe = "";

            // Offenen slot suchen
            while (!leereGruppeGefunden)
            {
                // Festnetzgruppen werden extra behandelt
                keyGruppe = StringUtils.md5(recipient.getNachricht()
                                + slotNummer
                                + recipient.getSmsOutVO().isFestnetzSms());

                if (mapGruppen.containsKey(keyGruppe))
                {
                    ArrayList<Recipient> listRecipientsInGruppe = mapGruppen
                                    .get(keyGruppe);

                    // Gruppe hat noch Slots frei
                    if (listRecipientsInGruppe.size() < MAX_RECIPIENTS_PER_GROUP)
                    {
                        log.debug("Gruppe mit "
                                        + (MAX_RECIPIENTS_PER_GROUP - listRecipientsInGruppe
                                                        .size())
                                        + " freien Slots gefunden (Festnetz: "
                                        + recipient.getSmsOutVO()
                                                        .isFestnetzSms() + ").");
                        leereGruppeGefunden = true;
                    }
                }
                // Neue Gruppe erstellen
                else
                {
                    mapGruppen.put(keyGruppe, new ArrayList<Recipient>());
                    leereGruppeGefunden = true;
                }

                // Naechsten Slot suchen
                slotNummer++;
            }

            if (mapGruppen.containsKey(keyGruppe))
            {
                mapGruppen.get(keyGruppe).add(recipient);
            }
            else
            {
                log.error("Empfaenger konnte nicht zugewiesen werden: Die Gruppe mit dem Key "
                                + keyGruppe + " existiert nicht.");
            }
        }

        // Beinhaltet die Gruppen
        Object[] arrGroups = mapGruppen.values().toArray();

        numGroups = arrGroups.length;

        log.debug("Insgesamt " + arrGroups.length + " Gruppen erstellt");

        sockets = new ISmsSocket[numGroups];

        numConnections = 0;

        numFinishedCalls = 0;

        // Solange die gesendeten SMS noch nicht der Anzahl der Personen
        // entspricht
        while (numFinishedCalls != numGroups)
        {
            // Initiate new connections until connection limit is reached
            for (int i = 0; (i < numGroups)
                            && (numConnections != MAX_CONNECTIONS); i++)
            {
                List<Recipient> alRecipients = (ArrayList<Recipient>) arrGroups[i];

                // Wenn die SMS noch nicht gesendet ist
                if ((getRecipientStatus(alRecipients) == IBroadcaster.SMS_STATUS_QUEUED)
                                || (getRecipientStatus(alRecipients) == IBroadcaster.SMS_STATUS_RETRY))
                {
                    String recp = getLogStringOfRecipients(alRecipients);

                    ISmsSocket socket = sockets[i];

                    // Socket existiert noch nicht
                    if (socket == null)
                    {
                        // Fuer die Person einen Socket erstellen
                        // Die Erzeugung geschieht über Spring
                        if (getApplicationContext() == null)
                        {
                            log.error("ApplicationContext wurde nicht gesetzt, kann keinen neuen Socket erstellen");
                            break;
                        }

                        socket = (ISmsSocket) getApplicationContext().getBean(
                                        SpringContext.BEAN_SMS_SOCKET,
                                        ISmsSocket.class);

                        if (socket == null)
                        {
                            log.error("Konnte kein Bean vom Typ \""
                                            + SpringContext.BEAN_SMS_SOCKET
                                            + "\" erstellen");
                            break;
                        }

                        // Tracker-Id setzen
                        socket.setTrackerId(idSmsout + "-" + (i + 1));

                        log.debug(socket.getDisplayTrackerId()
                                        + "perf-trace: socket created, recipient(s)=\""
                                        + recp + "\"");
                        sockets[i] = socket;
                    }

                    // Verbindung herstellen
                    socket.connect();
                    log.debug(socket.getDisplayTrackerId()
                                    + "perf-trace: connection etablished, recipient(s)=\""
                                    + recp + "\"");

                    socket.sendGETRequest(socket.getSmsClient()
                                    .buildSMSRequest(alRecipients));

                    log.debug(socket.getDisplayTrackerId()
                                    + "sent GET request, recipient(s)=\""
                                    + recp + "\"");

                    setRecipientStatus(alRecipients, socket.getStatusCode());

                    log.debug(socket.getDisplayTrackerId()
                                    + "perf-trace: recipient status set, recipient(s)=\""
                                    + recp + "\"");

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

            // Gruppen ueberpruefen
            for (int i = 0; i < numGroups; i++)
            {
                List<Recipient> alRecipients = (ArrayList<Recipient>) arrGroups[i];

                ISmsSocket socket = sockets[i];

                if (socket != null)
                {
                    boolean bSocketClosed = socket.checkSocket();

                    setRecipientStatus(alRecipients, socket.getStatusCode());

                    if (bSocketClosed)
                    {
                        log.debug(socket.getDisplayTrackerId()
                                        + "connection closed ("
                                        + alRecipients.size()
                                        + " recipient(s)=\""
                                        + getLogStringOfRecipients(alRecipients)
                                        + "\"");
                        log.debug(socket.getDisplayTrackerId()
                                        + " return_msg=\"" + socket.getResult()
                                        + "\".");

                        numConnections--;

                        if (!socket.isRetrying())
                        {
                            numFinishedCalls++;
                            sockets[i] = null;
                        }
                        else
                        {
                            log.debug(socket.getDisplayTrackerId()
                                            + "retrying..last result was \""
                                            + socket.getResult() + "\".");
                            totalNumRetries++;
                        }
                    }
                }
            }
        } // num_finished_calls != numPersons

        log.debug("all calls finished.");

        if (getFinishBroadcastingListener() != null)
        {
            log.debug("Updating SMS status in database");
            getFinishBroadcastingListener().finish(getRecipients());
        }

        msEnd = System.currentTimeMillis();

        long delta = msEnd - msStart;

        log.debug("It took " + ((delta / 1000) / 60) + "min, "
                        + ((delta / 1000) % 1000) + "sec and " + (delta % 1000)
                        + "ms to send " + numPersons + " SMS in " + numGroups
                        + " group(s) (" + totalNumRetries + " retries needed).");
    }

    /**
     * Setzt den Status einer Gruppe von Empfängern
     * 
     * @param _recipients
     */
    public void setRecipientStatus(List<Recipient> _recipients, int _statusCode)
    {
        for (int i = 0, m = _recipients.size(); i < m; i++)
        {
            Recipient r = (Recipient) _recipients.get(i);
            r.setStatusCode(_statusCode);
        }
    }

    /**
     * Liefert den Status-Code einer Gruppe von Empfängern zurück
     * 
     * @param _recipients
     */
    public int getRecipientStatus(List<Recipient> _recipients)
    {
        return ((Recipient) _recipients.get(((_recipients.size() - 1))))
                        .getStatusCode();
    }

    /**
     * Liefert einen String mit dem Empfängern der Gruppe zurück
     * 
     * @param _recipients
     * @return
     */
    public String getLogStringOfRecipients(List<Recipient> _recipients)
    {
        String logRecipients = "";

        if (_recipients == null)
        {
            log.error("Keine Empfaenger uebergeben!");
        }

        if (_recipients.size() == 0)
        {
            logRecipients = "---Keine Empfaenger---";
        }

        for (int j = 0, m = _recipients.size(); j < m; j++)
        {
            logRecipients += ((Recipient) _recipients.get(j)).getHandyNr();

            if ((j + 1) != m)
            {
                logRecipients += ", ";
            }
        }

        return logRecipients;
    }

    public String toString()
    {
        return "Protokoll [HTTP], Sendemodus: [Gruppe]";
    }
}
