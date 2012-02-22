package de.ecw.zabos.sql.util;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;

import org.apache.log4j.Logger;

import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.types.id.BaseId;

/**
 * Basisklasse für eine Datenbankverbindung
 * 
 * @author bsp
 * 
 */
public abstract class DBConnection
{
    private final static Logger log = Logger.getLogger(DBConnection.class);

    protected Connection connection;

    public abstract boolean openDB(String _url, String _user, String _passwd);

    public void closeDB()
    {
        if (connection != null)
        {
            try
            {
                connection.close();
            }
            catch (SQLException e)
            {
                log.error(e);
            }
            connection = null;
        }
    }

    /**
     * Liefert die java.sql.Connection für diese Datenbankverbindung.
     * 
     * @return
     */
    public Connection getConnection()
    {
        return connection;
    }

    /**
     * Liefert die nächste Sequence-Id.
     * 
     * @return
     * @throws StdException
     */
    public abstract long nextId() throws StdException;

    /**
     * Liefert die nächste Sequence-Id als BaseId verpackt.
     * 
     * @return
     * @throws StdException
     */
    public BaseId nextBaseId() throws StdException
    {
        return new BaseId(nextId());
    }

    /**
     * Schaltet den auto-commit Modus der Datenbank aus bzw. an.
     * 
     * @param _bEnabled
     * @throws StdException
     */
    public void setAutoCommit(boolean _bEnabled) throws StdException
    {
        try
        {
            connection.setAutoCommit(_bEnabled);
        }
        catch (SQLException e)
        {
            throw new StdException(e);
        }
    }

    /**
     * Legt einen neuen Savepoint für eine Transaktion an.
     * 
     * @return
     * @throws StdException
     */
    public Savepoint setSavepoint() throws StdException
    {
        try
        {
            return connection.setSavepoint();
        }
        catch (SQLException e)
        {
            throw new StdException(e);
        }
    }

    /**
     * Führt ein Rollback zum ggb. Savepoint aus.
     * 
     * @param _savepoint
     * @throws StdException
     */
    public void rollback(Savepoint _savepoint) throws StdException
    {
        try
        {
            connection.rollback(_savepoint);
        }
        catch (SQLException e)
        {
            throw new StdException(e);
        }
    }

    /**
     * Schliesst eine Transaktion ab.
     * 
     * @throws StdException
     */
    public void commit() throws StdException
    {
        try
        {
            connection.commit();
        }
        catch (SQLException e)
        {
            throw new StdException(e);
        }
    }

}
