package de.ecw.zabos.alarm.consumer.jms;

import java.util.HashMap;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.apache.log4j.Logger;

public class QueueListenerDelegator implements MessageListener
{
    private Map<String, IMonitordQueueListener> mapListeners = new HashMap<String, IMonitordQueueListener>();

    private final static Logger log = Logger
                    .getLogger(QueueListenerDelegator.class);

    public void onMessage(Message arg0)
    {
        if (!(arg0 instanceof TextMessage))
        {
            log.warn("Empfangene Nachricht war nicht vom Typ TextMessage");
            return;
        }

        TextMessage message = (TextMessage) arg0;

        try
        {
            String typ = message
                            .getStringProperty(IMonitordQueueListener.KEY_TYP);

            if (typ == null)
            {
                log.warn("Empfangene Nachricht enthaelt keine Eigenschaft 'typ'");
                return;
            }
            IMonitordQueueListener listener = null;

            if (null == (listener = mapListeners.get(typ)))
            {
                log.error("Kein Listener fuer den Typ '" + typ
                                + "' registriert");
                return;
            }

            listener.onMessage(message);
        }
        catch (JMSException e)
        {
            log.error("Konnte Nachricht nicht verarbeiten: " + e.getMessage());
        }
    }

    public void setListener(IMonitordQueueListener _listener)
    {
        mapListeners.put(_listener.getListenerTyp(), _listener);
    }
}
