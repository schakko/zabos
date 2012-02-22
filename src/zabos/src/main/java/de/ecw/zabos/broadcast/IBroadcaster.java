package de.ecw.zabos.broadcast;

import java.util.List;

/**
 * Interface für die Versendung der Nachrichten
 * 
 * @author ckl
 */
public interface IBroadcaster extends Runnable
{
    /**
     * no-op
     */
    public static final int SMS_STATUS_IDLE = 0;

    /**
     * retrying request
     */
    public static final int SMS_STATUS_RETRY = 7;

    /**
     * Time out while waiting for server response
     */
    public static final int SMS_STATUS_TIME_OUT = 8;

    /**
     * Person is queued for SMS message delivery
     */
    public static final int SMS_STATUS_QUEUED = 9;

    /**
     * Server request has been sent but has not been acknowledged, yet.
     */
    public static final int SMS_STATUS_VERSCHICKT = 10;

    /**
     * Failed to establish connection to host
     */
    public static final int SMS_STATUS_SOCKET_ERROR = 11;

    /**
     * Illegal response from gateway
     */
    public static final int SMS_STATUS_GATEWAY_ERROR = 12;

    /**
     * Internal error
     */
    public static final int SMS_STATUS_INTERNAL_ERROR = 13;

    /**
     * SMS message has been sent successfully (although it is unknown whether it
     * has really been received!)
     */
    public static final int SMS_STATUS_ANGEKOMMEN = 200;

    /**
     * Legt die Empfänger-Liste fest
     * 
     * @param recipients
     */
    public void setRecipients(List<Recipient> recipients);

    /**
     * Setzt den Callback-Handler, der aufgerufen werden soll, sobald der
     * Verstand beendet worden ist
     * 
     * @param _listener
     */
    public void setFinishBroadcastingListener(
                    IFinishBroadcastingListener _listener);
}