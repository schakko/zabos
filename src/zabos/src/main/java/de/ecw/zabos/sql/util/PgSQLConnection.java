package de.ecw.zabos.sql.util;

import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import de.ecw.zabos.exceptions.StdException;

/**
 * Datenbankverbindung zu einer PostgreSQL Datenbank
 * 
 * @author bsp
 * 
 */
public class PgSQLConnection extends DBConnection
{

    private final static Logger log = Logger.getLogger(PgSQLConnection.class);

    private static final String PGSQL_DRIVER = "org.postgresql.Driver";

    private java.sql.PreparedStatement pst_nextId;

    /**
     * Öffnet eine Verbindung zur ggb. PostgreSQL Datenbank.
     * 
     */
    public boolean openDB(String _url, String _user, String _passwd)
    {
        try
        {
            // Load MySQL JDBC driver
            Class.forName(PGSQL_DRIVER).newInstance();

            connection = DriverManager.getConnection(_url, _user, _passwd);

            return true;
        }
        catch (SQLException e)
        {
            log.error("unable to open database", e);
        }
        catch (InstantiationException e)
        {
            log.error("error loading PostgreSQL JDBC driver", e);
        }
        catch (IllegalAccessException e)
        {
            log.error("error loading PostgreSQL JDBC driver", e);
        }
        catch (ClassNotFoundException e)
        {
            log.error("error loading PostgreSQL JDBC driver", e);
        }
        return false;
    }

    synchronized public long nextId() throws StdException
    {
        try
        {
            java.sql.PreparedStatement pst = getPstNextId();
            java.sql.ResultSet rs = pst.executeQuery();
            if (rs.next())
            {
                return rs.getLong(1);
            }
            else
            {
                throw new StdException(
                                "failed to exec nextId statement (no result set).");
            }
        }
        catch (SQLException e)
        {
            throw new StdException("failed to exec nextId statement.", e);
        }
    }

    private java.sql.PreparedStatement getPstNextId() throws StdException
    {
        try
        {
            if (pst_nextId == null)
            {
                pst_nextId = getConnection().prepareStatement(
                                "SELECT NEXTVAL(\'id_seq\');");
            }
            return pst_nextId;
            // TODO pst.close(-)?
        }
        catch (SQLException e)
        {
            throw new StdException("failed to create nextId statement.", e);
        }

    }

}
