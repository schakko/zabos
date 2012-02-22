package de.ecw.zabos.sql.dao.cache;

/**
 * Generischer Cache zum Finden von mehreren Objekten
 * 
 * @author ckl
 * 
 * @param <E>
 */
public interface ICacheMultiple<E> extends ICache
{
    /**
     * Fügt einen Event-Listener hinzu
     * 
     * @param _event
     * @param _listener
     */
    public void addEventListener(ICacheEventListener.EVENT _event,
                    ICacheEventListener _listener);

    /**
     * Findet mehrere Objekte
     * 
     * @return
     */
    public E[] findMultiple();
}
