package de.ecw.zabos.frontend.beans;

import java.util.ArrayList;
import java.util.List;

import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.frontend.Message;

/**
 * Repräsentiert Nachrichten.
 * 
 * @author ckl
 */
public class MessageContainerBean
{
    // ArrayList<Error>
    private List<Message> listMessages = new ArrayList<Message>();

    /**
     * Liefert aufgetreten Nachrichten zurück
     * 
     * @return alErrors
     */
    public List<Message> getMessages()
    {
        return listMessages;
    }

    /**
     * Setzt eine Nachricht
     * 
     * @param _msg
     */
    public void addMessage(String _msg)
    {
        addMessage(new Message(_msg));
    }

    /**
     * Setzt eine Nachricht
     * 
     * @param _msg
     *            Nachricht
     * @param _field
     *            Feld
     */
    public void addMessage(String _msg, String _field)
    {
        Message msg = new Message(_msg);
        msg.setField(_field);

        addMessage(msg);
    }

    /**
     * Setzt einen Fehler, der durch eine Exception des Typs StdException
     * aufgetreten ist
     * 
     * @param _exception
     *            StdException
     */
    public void addMessage(StdException _exception)
    {
        addMessage(new Message(_exception.getMessage() + " - "
                        + _exception.getClass()));
    }

    /**
     * Setzt einen Nachricht
     * 
     * @param _msg
     *            Fehler
     */
    public void addMessage(Message _msg)
    {
        if (_msg != null)
        {
            if (listMessages == null)
            {
                listMessages = new ArrayList<Message>();
            }

            listMessages.add(_msg);
        }
    }

    /**
     * Liefert die Anzahl der registrierten Nachrichten
     * 
     * @return
     */
    public int getTotalMessages()
    {
        return listMessages.size();
    }

    /**
     * Liefert zurück, ob Nachrichten vorhanden sind
     * 
     * @return
     */
    public boolean hasMessages()
    {
        return (listMessages.size() > 0);
    }
}
