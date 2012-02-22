package de.ecw.zabos.broadcast;

import java.util.List;


/**
 * Interface das aufgerufen wird, wenn ein Broadcast beendet ist
 * 
 * @author ckl
 * 
 */
public interface IFinishBroadcastingListener
{
    /**
     * Wird aufgerufen, sobald der Verstand einer Nachricht beendet ist
     * 
     * @param _recipients
     */
    public void finish(List<Recipient> _recipients);
}
