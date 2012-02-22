package de.ecw.zabos.sql.dao.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import de.ecw.zabos.exceptions.StdException;

/**
 * Adapter-Cache für mehrere Elemente
 * 
 * @author ckl
 * 
 * @param <E>
 */
abstract public class CacheMultipleAdapter<E> implements ICacheMultiple<E>
{
    /**
     * Elemente des Caches
     */
    protected E[] elems = null;

    private final static Logger log = Logger
                    .getLogger(CacheMultipleAdapter.class);

    /**
     * Listener
     */
    private Map<ICacheEventListener.EVENT, ArrayList<ICacheEventListener>> mapEventListener = new HashMap<ICacheEventListener.EVENT, ArrayList<ICacheEventListener>>();

    /**
     * Setzt die Elemente
     * 
     * @param _elems
     */
    protected synchronized void setElements(E[] _elems)
    {
        fireEvent(ICacheEventListener.EVENT.BEFORE_UPDATE);

        elems = _elems;

        fireEvent(ICacheEventListener.EVENT.AFTER_UPDATE);
    }

    /**
     * Finale Methode, da die Elemente vorher auf null gesetzt werden müssen.
     */
    final public synchronized void update() throws StdException
    {
        elems = null;
        updateAfterElementReleasement();
    }

    /**
     * Updatet den Cache
     * 
     * @throws StdException
     */
    abstract public void updateAfterElementReleasement() throws StdException;

    /*
     * (non-Javadoc)
     * 
     * @see de.ecw.zabos.sql.dao.cache.ICacheMultiple#findMultiple()
     */
    public E[] findMultiple()
    {
        return elems;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.ecw.zabos.sql.dao.cache.ICacheMultiple#addEventListener(de.ecw.zabos
     * .sql.dao.cache.ICacheEventListener.EVENT,
     * de.ecw.zabos.sql.dao.cache.ICacheEventListener)
     */
    public void addEventListener(ICacheEventListener.EVENT _event,
                    ICacheEventListener _listener)
    {
        if (!mapEventListener.containsKey(_event))
        {
            mapEventListener.put(_event, new ArrayList<ICacheEventListener>());
        }

        mapEventListener.get(_event).add(_listener);
    }

    /**
     * Führt alle Listener für ein bestimmtes Event aus
     * 
     * @param _event
     */
    private synchronized void fireEvent(ICacheEventListener.EVENT _event)
    {
        if (mapEventListener.containsKey(_event))
        {
            ArrayList<ICacheEventListener> listeners = mapEventListener
                            .get(_event);

            for (int i = 0, m = listeners.size(); i < m; i++)
            {
                log.debug("Fuehre Listener fuer Event " + _event + " aus");
                listeners.get(i).onEventOccured(_event);
            }
        }
    }

}
