package de.ecw.zabos.alarm.consumer.jms;

import javax.jms.MessageListener;

/**
 * Interface für die Listener, die eine Monitord-JMS-Queue verarbeiten
 * 
 * @author ckl
 * 
 */
public interface IMonitordQueueListener extends MessageListener
{
    public final static String KEY_TIMESTAMP = "timestamp";

    public final static String KEY_UHRZEIT = "uhrzeit";

    public final static String KEY_DATUM = "uhrzeit";

    public final static String KEY_SERVER_NAME = "servernamehex";

    public final static String KEY_CHANNEL_NAME = "channelnamehex";

    public final static String KEY_CHANNEL_NUMMER = "channelnum";

    public final static String KEY_TYP = "typ";

    public final static String TYP_FMS = "fms";

    public final static String TYP_ZVEI = "zvei";

    public final static String TYP_POCSAG = "pocsag";

    /**
     * Liefert den Typ zurück, auf den der Listener hört
     * 
     * @return
     */
    public abstract String getListenerTyp();
}
