package de.ecw.zabos.sql.util;

import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.log4j.Logger;

/**
 * Datenbankverbindung zu einer MySQL Datenbank
 * 
 * @author bsp
 * 
 */
public class MySQLConnection extends DBConnection
{

    private final static Logger log = Logger.getLogger(MySQLConnection.class);

    private static final String MYSQL_DRIVER = "com.mysql.jdbc.Driver";

    /**
     * Öffnet eine Verbindung zur ggb. MySQL Datenbank.
     * 
     */
    public boolean openDB(String _url, String _user, String _passwd)
    {
        try
        {
            // Load MySQL JDBC driver
            Class.forName(MYSQL_DRIVER).newInstance();

            connection = DriverManager.getConnection(_url, _user, _passwd);

            return true;
        }
        catch (SQLException e)
        {
            log.error("unable to open database", e);
        }
        catch (InstantiationException e)
        {
            log.error("error loading MySQL JDBC driver", e);
        }
        catch (IllegalAccessException e)
        {
            log.error("error loading MySQL JDBC driver", e);
        }
        catch (ClassNotFoundException e)
        {
            log.error("error loading MySQL JDBC driver", e);
        }
        return false;
    }

    public long nextId()
    {
        // todo: emulate sequence behaviour
        return 0;
    }

}
