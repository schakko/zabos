package de.ecw.zabos.sql.dao.cache;

/**
 * Interface für die einzelnen Event-Listener die bei einer Cache-Änderung
 * durchgeführt werden
 * 
 * @author ckl
 * 
 */
public interface ICacheEventListener
{
    /**
     * {@link EVENT#AFTER_UPDATE} wird nach der Änderung des Caches aufgerufen,
     * {@link EVENT#BEFORE_UPDATE} davor
     * 
     * @author ckl
     * 
     */
    public static enum EVENT
    {
        BEFORE_UPDATE, AFTER_UPDATE
    };

    /**
     * Wird aufgerufen, wenn ein Event aufgetreten ist
     * 
     * @param _event
     *            Event-Typ, so dass der Event-Listener auf die
     *            unterschiedlichen Events reagieren kann
     */
    public void onEventOccured(EVENT _event);
}
