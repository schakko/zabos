package de.ecw.zabos;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;

import org.apache.log4j.Logger;
import org.springframework.web.context.support.XmlWebApplicationContext;

import de.ecw.daemon.DaemonMgr;
import de.ecw.report.IReportService;
import de.ecw.zabos.license.License;
import de.ecw.zabos.sql.resource.DBResource;
import de.ecw.zabos.util.SpringConfigLoader;

/**
 * Wird von der web.xml als Einstiegs-Klasse benutzt
 * 
 * @author ckl
 */
final public class Globals
{
    /**
     * Version
     */
    private static final String VERSION = "@VERSION@";

    /**
     * Build-Datum
     */
    private static final String BUILD_DATE = "@BUILD_DATE@";

    /**
     * SCM-Revision
     */
    private static final String REVISION = "@REVISION@";

    /**
     * Erstellt von
     */
    private static final String BUILDER = "@BUILDER@";

    private Globals()
    {
    }

    private final static Logger log = Logger.getLogger(Globals.class);

    /**
     * Datenbankverbindung
     */
    static private DBResource dbresource;

    /**
     * Reporting-Service
     */
    static private IReportService reportService;

    static private boolean bInitialized = false;

    static private boolean bDestroyed = false;

    /**
     * Liefert die Datenbankverbindung für den "Main" Thread
     * 
     * @return
     */
    public static DBResource getDBResource()
    {
        return dbresource;
    }

    /**
     * Liefert den Reporting Service
     * 
     * @return
     */
    public static IReportService getReportService()
    {
        return reportService;
    }

    /**
     * Liefert das Build-Datum
     * 
     * @return
     */
    public static String getBuildDate()
    {
        return BUILD_DATE;
    }

    /**
     * Liefert den Ersteller des Packages
     * 
     * @return
     */
    public static String getBuilder()
    {
        return BUILDER;
    }

    /**
     * Liefert die offizielle Version
     * 
     * @return
     */
    public static String getVersion()
    {
        return VERSION;
    }

    /**
     * Liefert die SCM-Revision
     * 
     * @return
     */
    public static String getRevision()
    {
        return REVISION;
    }

    /**
     * Service Initialisierung. Sollte die Initalisierung fehlschlagen wird dem
     * ServletContainer dies durch eine "UnavailableException" signalisiert.
     * <ul>
     * <li>Laden der Konfigurationsdatei über
     * {@link SpringConfigLoader#loadConfiguration()}</li>
     * <li>Initialisierung der Subsysteme {@link #initSubsystem()}</li>
     * </ul
     * 
     * @param _servletConfig
     * @throws ServletException
     */
    public static void init(ServletConfig _servletConfig) throws ServletException
    {
        synchronized (Globals.class)
        {
            if (!bInitialized)
            {
                // Application-Kontext initalisieren
                XmlWebApplicationContext awac = new XmlWebApplicationContext();
                awac.setServletConfig(_servletConfig);

                // Host-spezifische Konfiguration laden
                new SpringConfigLoader(_servletConfig, awac)
                                .loadConfiguration();

                awac.refresh();
                SpringContext.getInstance().setApplicationContext(awac);

                initSubsystem();

                log.info("Initialisierung abgeschlossen");
            }
        }
    }

    /**
     * Initalisiert
     * <ul>
     * <li>{@link #readLicense()}</li>
     * <li>{@link #readBuild()}</li>
     * <li>{@link #initDatabase()}</li>
     * <li>{@link #initDaemons()}</li>
     * </ul>
     */
    public static void initSubsystem() throws UnavailableException
    {
        // Lizenz-file lesen
        readLicense();

        // Build-Informationen lesen bzw. schreiben
        readBuild();

        // Datenbankverbindung oeffnen
        initDatabase();

        // Daemons initalisieren
        initDaemons();

        bInitialized = true;
    }

    /**
     * Initalisiert das Bean {@link SpringContext#BEAN_DAEMON_MANAGER}, Klasse
     * {@link DaemonMgr}.
     * 
     * @throws UnavailableException
     *             Falls das Bean null ist
     */
    private static void initDaemons() throws UnavailableException
    {
        if (SpringContext.getInstance().getBean(
                        SpringContext.BEAN_DAEMON_MANAGER, DaemonMgr.class) == null)
        {
            throw new UnavailableException(SpringContext.BEAN_DAEMON_MANAGER
                            + " wurde nicht erstellt");
        }
    }

    /**
     * Schließt die offenen Services:
     * <ul>
     * <li>{@link #freeReportService()}</li>
     * <li>Schließen des Spring-Kontextes</li>
     * <li>{@link #freeDatabase()}</li>
     * </ul>
     */
    public static void destroy()
    {
        synchronized (Globals.class)
        {
            if (!bDestroyed)
            {
                bDestroyed = true;

                freeReportService();

                // Spring-Dependencies beenden
                SpringContext.getInstance().getApplicationContext().close();

                freeDatabase();

                log.info("Shutdown abgeschlossen");
            }
        }
    }

    /**
     * Liest die Build-Informationen und gibt diese an Standard-Error-Kanal aus
     */
    public static void readBuild()
    {
        System.err.println("====================================");
        System.err.println("VERSIONSINFORMATIONEN:");
        System.err.println("====================================");
        System.err.println("Version: " + getVersion());
        System.err.println("Revision: " + getRevision());
        System.err.println("Build: " + getBuildDate());
        System.err.println("Builder: " + getBuilder());
    }

    /**
     * Lizenz-Datei einlesen
     * 
     * @throws UnavailableException
     *             Wenn das Lizenz-Bean {@link SpringContext#BEAN_LICENSE} nicht
     *             definiert ist
     * @throws UnavailableException
     *             Wenn die Lizenz-Datei ungültig oder nicht vorhanden ist
     * @throws UnavailableException
     *             Wenn die Lizenz abgelaufen ist
     */
    private static void readLicense() throws UnavailableException
    {
        License license = (License) SpringContext.getInstance().getBean(
                        SpringContext.BEAN_LICENSE, License.class);

        if (license == null)
        {
            System.err.println("[---] Es wurde kein Lizenz-Bean definiert!");
            throw new UnavailableException(
                            "Es wurde kein Lizenz-Bean definiert!");
        }

        if (!license.readLicense())
        {
            // 2007-05-31 CKL: Informationen auf stderr ausgeben
            System.err
                            .println("[---] Die Lizenzdatei ist ungueltig oder nicht vorhanden!");

            throw new UnavailableException(
                            "Die Lizenzdatei ist ungueltig oder nicht vorhanden!");
        }
        else
        {
            if (!license.isStillValid())
            {
                // 2007-05-31 CKL: Informationen auf stderr ausgeben
                System.err.println("[---] Die Lizenz im Verzeichnis "
                                + System.getProperty("user.dir")
                                + " ist abgelaufen!");

                throw new UnavailableException("Die Lizenz im Verzeichnis "
                                + System.getProperty("user.dir")
                                + "ist abgelaufen!");
            }
        }
    }

    /**
     * Initialisierung das Bean {@link SpringContext#BEAN_DB_RESOURCE}, Klasse
     * {@link DBResource}
     * 
     * @throws UnavailableException
     *             Falls das Bean nicht definierte wurde
     */
    private static void initDatabase() throws UnavailableException
    {
        if ((dbresource = (DBResource) SpringContext.getInstance().getBean(
                        SpringContext.BEAN_DB_RESOURCE, DBResource.class)) == null)
        {
            throw new UnavailableException(
                            "Haupt-Datenbankverbindung wurde nicht definiert");
        }
    }

    /**
     * Schliesst die Datenbankverbindung
     */
    private static void freeDatabase()
    {
        if (dbresource != null)
        {
            dbresource.free();
        }
    }

    /**
     * Schließt den Reporting-Service
     */
    private static void freeReportService()
    {
        if (reportService != null)
        {
            reportService.destroy();
        }
    }

    /**
     * Liefert den Status der Initalisierung zurück
     * 
     * @return
     */
    public static boolean isInitialized()
    {
        return bInitialized;
    }
}
