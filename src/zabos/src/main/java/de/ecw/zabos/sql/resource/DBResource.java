package de.ecw.zabos.sql.resource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.DisposableBean;

import de.ecw.zabos.bo.BOFactory;
import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.sql.dao.DAOFactory;
import de.ecw.zabos.sql.tao.TaoFactory;
import de.ecw.zabos.sql.util.DBConnection;
import de.ecw.zabos.sql.vo.ObjectFactory;

/**
 * Beschreibt eine Datenbank-Resource.
 * 
 * Wenn aus mehreren Threads auf eine Datenbank zugegriffen wird muss jeder
 * Thread seine eigene Datenbank-Resource instanzieren.
 * 
 * Die Resource wird definiert durch Treiber, URL, Username und Passwort.
 * 
 * Die {@link DBResource} implementiert {@link DisposableBean}, so dass beim
 * Herunterfahren der Spring-Applikation automatisch die Verbindungen
 * geschlossen werden
 */
final public class DBResource implements DisposableBean
{
    public static int totalInstances = 0;

    /**
     * Inkrementiert die Anzahl der Datenbank-Instanzen
     * 
     * @return
     */
    private synchronized int incrementTotalInstances()
    {
        return ++totalInstances;
    }

    private int connectionId = 0;

    protected final static Logger log = Logger.getLogger(DBResource.class);

    private String className;

    private String jdbcUrl;

    private String username;

    private String password;

    @SuppressWarnings("unchecked")
    private Class driverClass;

    private DBConnection dbconnection;

    private DAOFactory daoFactory;

    private TaoFactory taoFactory;

    private BOFactory boFactory;

    private ObjectFactory objectFactory = new ObjectFactory();

    /**
     * Konstruktor, die Methode {@link #init()} wird automatisch aufgerufen
     * 
     * @param _className
     *            Name der zu ladenden Klasse
     * @param _jdbcUrl
     *            JDBC-URL
     * @param _username
     *            Benutzername
     * @param _password
     *            Passwort
     * @throws StdException
     */
    public DBResource(String _className, String _jdbcUrl, String _username,
                    String _password) throws StdException
    {
        className = _className;
        jdbcUrl = _jdbcUrl;
        username = _username;
        password = _password;

        init();
    }

    public DBResource(String _className, String _jdbcUrl, String _username,
                    String _password, ObjectFactory _objectFactory)
                    throws StdException
    {
        className = _className;
        jdbcUrl = _jdbcUrl;
        username = _username;
        password = _password;
        objectFactory = _objectFactory;

        init();

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

    /**
     * Liefert die {@link DAOFactory}
     * 
     * @return
     */
    public DAOFactory getDaoFactory()
    {
        return daoFactory;
    }

    /**
     * Liefert die {@link TaoFactory}
     * 
     * @return
     */
    public TaoFactory getTaoFactory()
    {
        return taoFactory;
    }

    /**
     * Liefert die {@link BOFactory}
     * 
     * @return
     */
    public BOFactory getBoFactory()
    {
        return boFactory;
    }

    /**
     * Liefert die {@link ObjectFactory}
     * 
     * @return
     */
    public ObjectFactory getObjectFactory()
    {
        return objectFactory;
    }

    /**
     * Initalisiert die Datenbankverbindung
     * 
     * @throws StdException
     *             Wenn {@link #className}, {@link #jdbcUrl} oder
     *             {@link #username} null sind
     * @throws StdException
     *             Wenn die Datenbank-Klasse nicht gefunden wurde
     * @throws StdException
     *             Wenn die Datenbank-Verbindung nicht hergestellt werden konnte
     */
    public void init() throws StdException
    {
        try
        {
            if (className == null || jdbcUrl == null || username == null)
            {

                throw new StdException(
                                "Attribute fuer Datenbankkonfiguration unvollstaendig");
            }

            try
            {
                driverClass = Class.forName(className);
            }
            catch (ClassNotFoundException e)
            {
                throw new StdException("unbekannte db-treiber Klasse \""
                                + className + "\"");
            }

            dbconnection = (DBConnection) driverClass.newInstance();

            if (!dbconnection
                            .openDB(getJdbcUrl(), getUsername(), getPassword()))
            {
                throw new StdException("failed to open connection to database");
            }

            connectionId = incrementTotalInstances();

            log.info("Datenbank-Verbindung #" + connectionId + " zu "
                            + getJdbcUrl() + " (user: " + getUsername()
                            + ") hergestellt");

            dbconnection.setAutoCommit(false);

            daoFactory = new DAOFactory(dbconnection, objectFactory);

            taoFactory = new TaoFactory(this);

            boFactory = new BOFactory(this);
        }
        catch (InstantiationException e)
        {
            throw new StdException(e);
        }
        catch (IllegalAccessException e)
        {
            throw new StdException(e);
        }
    }

    /**
     * Gibt die Resourcen wieder frei
     */
    public void free()
    {
        if (dbconnection != null)
        {
            dbconnection.closeDB();
            log.info("Datenbankverbindung #" + connectionId + " geschlossen");
        }
    }

    /**
     * Liefert die {@link #driverClass}
     * 
     * @return
     */
    @SuppressWarnings(
    { "unchecked" })
    public Class getDriverClass()
    {
        return driverClass;
    }

    /**
     * Liefert die {@link #jdbcUrl}
     * 
     * @return
     */
    public String getJdbcUrl()
    {
        return jdbcUrl;
    }

    /**
     * Liefert den {@link #username}
     * 
     * @return
     */
    public String getUsername()
    {
        return username;
    }

    /**
     * Liefert das {@link #password}
     * 
     * @return
     */
    public String getPassword()
    {
        return password;
    }

    /**
     * Delegiert an {@link #free()}
     */
    public void destroy() throws Exception
    {
        free();
    }

}
