package de.ecw.zabos.alarm.consumer.zvei.jms;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.apache.log4j.Logger;

import de.ecw.zabos.alarm.consumer.jms.IMonitordQueueListener;
import de.ecw.zabos.alarm.consumer.zvei.ZveiConsumer;

/**
 * JMS-Listener für Monitord/ZVEI (Fünfton)
 * 
 * @author ckl
 * 
 */
public class ZveiQueueListener implements IMonitordQueueListener,
                MessageListener
{
    public final static String ZVEI_KEY_ZVEI = "zvei";

    private final static Logger log = Logger.getLogger(ZveiQueueListener.class);

    public final static String ZVEI_KEY_WECKTON = "weckton";

    public final static String ZVEI_KEY_TEXT = "text";

    private ZveiConsumer zveiConsumer;

    public ZveiQueueListener(ZveiConsumer _zveiConsumer)
    {
        this.zveiConsumer = _zveiConsumer;
    }

    public void onMessage(Message arg0)
    {
        TextMessage textMessage = (TextMessage) arg0;
        String fuenfton = null;

        try
        {
            fuenfton = textMessage.getStringProperty(ZVEI_KEY_ZVEI);
        }
        catch (JMSException e)
        {
            log.error("JMS-Fehler beim Empfangen des Fuenfton-Folgerufs: \""
                            + e.getMessage());
            return;
        }

        if (fuenfton == null)
        {
            log.error("Fuenfton, der in der JMS-Nachricht sein sollte, ist leer gewesen");
            return;
        }

        log.info("JMS-Nachricht fuer Fuenfton " + fuenfton + " empfangen");

        // synchronized, so dass asynchron die eingehenden Nachrichten empfangen
        // werden können
        zveiConsumer.process5Ton(fuenfton);
    }

    public String getListenerTyp()
    {
        return TYP_ZVEI;
    }
}
