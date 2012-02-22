package de.ecw.zabos.broadcast;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import de.ecw.daemon.IDaemon;
import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.mc35.MC35ManagerDaemon;
import de.ecw.zabos.sql.dao.SmsOutDAO;
import de.ecw.zabos.sql.dao.TelefonDAO;
import de.ecw.zabos.sql.resource.DBResource;
import de.ecw.zabos.sql.tao.SmsOutTAO;
import de.ecw.zabos.sql.vo.SmsOutVO;
import de.ecw.zabos.sql.vo.TelefonVO;
import de.ecw.zabos.types.TelefonNummer;
import de.ecw.zabos.types.id.SmsOutId;
import de.ecw.zabos.types.id.SmsOutStatusId;
import de.ecw.zabos.util.IWatchdog;
import de.ecw.zabos.util.TraceDog;

/**
 * Startet einen Hintergrundthread zum Nachrichtenversand; init() wartet auf den
 * Abschluss der Initialisierung und kehrt dann zu "Main" zurück.
 * 
 * Der Hintergrundthread prüft im UNSENT_SMS_POLLING_INTERVAL Abständen ob in
 * der Datenbank neue Nachricht zum Versand vorliegen.
 * 
 * Der Manager ist nur dafür zuständig, dass die Versendung eingeleitet werden.
 * Der reale Versand erfolgt über die über die einzelnen Transportschichten.
 * 
 * @author ckl
 * 
 */
public class BroadcastMgr implements IDaemon, IWatchdog,
                ApplicationContextAware
{
    /**
     * Watchdog-Datei
     */
    private String watchdogFile = "zabos_BroadcastMgr.wd";

    private final static Logger log = Logger.getLogger(BroadcastMgr.class);

    /**
     * Zeitintervall in ms für das Erfragen von noch nicht gesendeten SMS
     * Nachrichten
     * 
     * 2006-06-12 CKL: Intervall von 2 Sekunden auf 1 Sekunde herab gesetzt
     */
    private static final int UNSENT_SMS_POLLING_INTERVAL = 1000;

    /**
     * Spring-Context
     */
    private ApplicationContext applicationContext;

    /**
     * Datenbank
     */
    private DBResource dbresource;

    /**
     * Flag, dass die Initalisierung stattfindet
     */
    private boolean bWaitInit;

    /**
     * Flag, dass das freen der Managers stattfindet
     */
    private boolean bWaitFree;

    /**
     * Flag, dass der Manager läuft
     */
    private DAEMON_STATUS daemonStatus = DAEMON_STATUS.OFFLINE;

    /**
     * Hintergrund-Thread
     */
    private Thread thread;

    private StdException stdException;

    /**
     * Referenz auf den Manager
     */
    private MC35ManagerDaemon mc35Manager;

    private BroadcasterFactory broadcasterFactory;

    /**
     * Konstruktor
     * 
     * @param _dbResource
     *            Unbedingt eine neue Datenbankinstanz übergeben, da diese
     *            Klasse gethreadet wird.
     * 
     * @param _mc35Mgr
     */
    public BroadcastMgr(final DBResource _dbResource,
                    MC35ManagerDaemon _mc35Mgr,
                    BroadcasterFactory _broadcasterFactory)
    {
        dbresource = _dbResource;
        setMc35Manager(_mc35Mgr);
        setBroadcasterFactory(_broadcasterFactory);
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.ecw.zabos.daemon.IDaemon#isRunning()
     */
    synchronized public DAEMON_STATUS getDaemonStatus()
    {
        return daemonStatus;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.ecw.zabos.daemon.IDaemon#init()
     */
    public void init() throws StdException
    {
        bWaitInit = true;

        stdException = null;

        thread = new Thread()
        {
            public void run()
            {
                // 2006-06-12 CKL: Prioritaet von MIN auf NORM geaendert
                Thread.currentThread().setPriority(Thread.NORM_PRIORITY);

                bWaitInit = false;
                daemonStatus = DAEMON_STATUS.ONLINE;

                if (getMc35Manager() == null)
                {
                    stdException = new StdException(
                                    "Es wurde kein MC35-Manager definiert!");
                    return;
                }

                // Initialisierung war OK, polling loop starten
                try
                {
                    TraceDog traceDog = new TraceDog(getWatchdogFile());

                    SmsOutDAO smsOutDAO = dbresource.getDaoFactory()
                                    .getSmsOutDAO();
                    TelefonDAO telefonDAO = dbresource.getDaoFactory()
                                    .getTelefonDAO();

                    while ((getDaemonStatus() == DAEMON_STATUS.ONLINE))
                    {
                        // Datenbank nach noch nicht versendeten Nachrichten
                        // durchsuchen
                        SmsOutVO[] vos = smsOutDAO.findSmsOutByStatusUnsent();

                        if (vos.length > 0)
                        {
                            // Noch nicht gesendete SMS-Nachrichten gefunden
                            log.debug(vos.length
                                            + " noch nicht gesendete SMS-Nachrichten gefunden");

                            // Liste der Recipients zusammenbauen
                            List<Recipient> alRecipients = new ArrayList<Recipient>();
                            for (int i = 0; i < vos.length; i++)
                            {
                                SmsOutVO smsOutVO = vos[i];
                                TelefonVO telefonVO = telefonDAO
                                                .findTelefonById(smsOutVO
                                                                .getTelefonId());
                                TelefonNummer telefonNummer = telefonVO
                                                .getNummer();

                                MC35ManagerDaemon mc35mgr = getMc35Manager();

                                TelefonNummer absenderNummer = mc35mgr
                                                .getNextDeviceRufnummer();

                                Recipient r = new Recipient(smsOutVO,
                                                telefonNummer, absenderNummer);
                                alRecipients.add(r);
                            }

                            // Versand der Nachrichten
                            createAndRunBroadcaster(alRecipients);
                        }
                        Thread.sleep(UNSENT_SMS_POLLING_INTERVAL);

                        traceDog.trace();
                    }
                }
                catch (InterruptedException e)
                {
                    // TODO Critical: Neustart des Broadcast Threads
                    // erforderlich
                    daemonStatus = DAEMON_STATUS.OFFLINE;
                    log.error(e);
                }
                catch (StdException e)
                {
                    // TODO Critical: Neustart des Broadcast Threads
                    // erforderlich
                    daemonStatus = DAEMON_STATUS.OFFLINE;
                    log.error(e);
                }

                bWaitFree = false;

            }
        };

        thread.setName("BroadcastMgr");

        // Broadcast thread starten und auf das Ende der Initialierung warten
        thread.start();

        try
        {
            while (bWaitInit)
            {
                Thread.sleep(200);
            }
        }
        catch (InterruptedException e)
        {
            throw new StdException(e);
        }

        // Ist waehrend der Initialisierung eine Exception aufgetreten?
        if (stdException != null)
        {
            throw stdException;
        }

        log.debug("SmsBroadcastDaemon wurde gestartet");

    }

    /*
     * (non-Javadoc)
     * 
     * @see de.ecw.zabos.daemon.IDaemon#free()
     */
    public void free()
    {
        bWaitFree = true;
        daemonStatus = DAEMON_STATUS.SHUTTING_DOWN;

        // Auf den Shutdown des Threads warten
        try
        {
            while (bWaitFree)
            {
                Thread.sleep(200);
            }
        }
        catch (InterruptedException e)
        {
            log.error(e);
        }

        daemonStatus = DAEMON_STATUS.OFFLINE;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.ecw.zabos.util.IWatchdog#setWatchdogFile(java.lang.String)
     */
    public void setWatchdogFile(String watchdogFile)
    {
        this.watchdogFile = watchdogFile;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.ecw.zabos.util.IWatchdog#getWatchdogFile()
     */
    public String getWatchdogFile()
    {
        return watchdogFile;
    }

    /**
     * Setzt den {@link MC35ManagerDaemon}
     * 
     * @param mc35Daemon
     */
    final public void setMc35Manager(MC35ManagerDaemon mc35Daemon)
    {
        this.mc35Manager = mc35Daemon;
    }

    /**
     * Liefert den {@link MC35ManagerDaemon}
     * 
     * @return
     */
    final public MC35ManagerDaemon getMc35Manager()
    {
        return mc35Manager;
    }

    public void setApplicationContext(ApplicationContext arg0) throws BeansException
    {
        applicationContext = arg0;
    }

    private void createAndRunBroadcaster(List<Recipient> _recipients)
    {
        if (applicationContext == null)
        {
            log.error("Application-Kontext ist nicht gesetzt");
            return;
        }

        IBroadcaster broadcaster = getBroadcasterFactory().create();

        if (broadcaster == null)
        {
            log.error("SMS-Broadcaster konnte nicht erstellt werden!");
            return;
        }

        log.info("Broadcaster erstellt: " + broadcaster);

        broadcaster.setRecipients(_recipients);

        broadcaster.setFinishBroadcastingListener(new IFinishBroadcastingListener()
        {
            public void finish(List<Recipient> recipients)
            {
                /*
                 * Nachrichten wurden verschickt; nun den Versandstatus in die
                 * Datenbank zurueckschreiben
                 */
                SmsOutTAO smsOutTAO = dbresource.getTaoFactory().getSmsOutTAO();

                for (int i = 0, m = recipients.size(); i < m; i++)
                {
                    Recipient r = recipients.get(i);

                    smsOutTAO.updateSmsOutStatus(new SmsOutId(r.getSmsOutId()),
                                    new SmsOutStatusId(r.getStatusCode()));
                }
            }
        });
        broadcaster.run();
    }

    public void setBroadcasterFactory(BroadcasterFactory broadcasterFactory)
    {
        this.broadcasterFactory = broadcasterFactory;
    }

    public BroadcasterFactory getBroadcasterFactory()
    {
        return broadcasterFactory;
    }
}
