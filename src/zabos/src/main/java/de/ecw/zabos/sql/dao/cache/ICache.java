package de.ecw.zabos.sql.dao.cache;

import de.ecw.zabos.exceptions.StdException;

/**
 * Interface für Caching
 * 
 * @author ckl
 * 
 */
public interface ICache
{
    /**
     * Updatet den Cache
     */
    public void update() throws StdException;
}
