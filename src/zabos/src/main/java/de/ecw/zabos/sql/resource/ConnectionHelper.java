package de.ecw.zabos.sql.resource;

import java.sql.Connection;

import de.ecw.zabos.sql.util.DBConnection;

/**
 * Hilfsklasse zum Umwandeln einer {@link DBConnection} in eine
 * {@link Connection}
 * 
 * @author ckl
 * 
 */
public class ConnectionHelper
{
    /**
     * Liefert die {@link Connection} einer {@link DBResource} zurück.
     * Hilfsmethode für das Spring-Framework.
     * 
     * @param _dbResource
     * @return
     */
    final public Connection createConnection(final DBResource _dbResource)
    {
        return _dbResource.getDBConnection().getConnection();
    }
}
