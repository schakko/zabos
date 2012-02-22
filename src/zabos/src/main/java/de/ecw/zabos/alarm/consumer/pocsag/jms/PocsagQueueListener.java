package de.ecw.zabos.alarm.consumer.pocsag.jms;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import de.ecw.zabos.alarm.consumer.jms.IMonitordQueueListener;

/**
 * JMS-Listener für Monitord/POCSAG
 * 
 * @author ckl
 * 
 */
public class PocsagQueueListener implements IMonitordQueueListener,
                MessageListener

{
    public final static String POCSAG_KEY_SUBHEX = "subhex";

    public final static String POCSAG_KEY_SUB = "sub";

    public final static String POCSAG_KEY_RIC = "ric";

    public final static String POCSAG_KEY_TEXT = "text";

    public void onMessage(Message arg0)
    {
        TextMessage textMessage = (TextMessage) arg0;
        // TODO POCSAG-Nachricht verarbeiten
    }

    public String getListenerTyp()
    {
        return TYP_POCSAG;
    }
}
