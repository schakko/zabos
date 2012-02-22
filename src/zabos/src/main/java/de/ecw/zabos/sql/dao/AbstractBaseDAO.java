package de.ecw.zabos.sql.dao;

import de.ecw.zabos.sql.util.DBConnection;
import de.ecw.zabos.sql.vo.ObjectFactory;

/**
 * Basisklasse für alle "Data Access Objects"
 * 
 * @author bsp
 * 
 */
public abstract class AbstractBaseDAO
{
    /**
     * Datenbankeverbindung
     */
    protected DBConnection dbconnection;

    private ObjectFactory objectFactory;

    public AbstractBaseDAO(DBConnection _dbconnection,
                    ObjectFactory _objectFactory)
    {
        dbconnection = _dbconnection;
        objectFactory = _objectFactory;
    }

    /**
     * Liefert die {@link DBConnection}
     * 
     * @return
     */
    public DBConnection getDBConnection()
    {
        return dbconnection;
    }

    public ObjectFactory getObjectFactory()
    {
        return objectFactory;
    }

}
