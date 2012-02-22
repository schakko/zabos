package de.ecw.zabos.alarm.daemon;

import org.apache.log4j.Logger;

import de.ecw.daemon.IDaemon;
import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.service.alarm.IAlarmService;
import de.ecw.zabos.service.smsin.ISmsInService;
import de.ecw.zabos.sql.resource.DBResource;
import de.ecw.zabos.sql.tao.SystemKonfigurationTAO;
import de.ecw.zabos.util.IWatchdog;
import de.ecw.zabos.util.TraceDog;

/**
 * Hintergrund-Thread, der in regelmässigen Abständen die aktiven Alarme auf
 * Timeout bzw. Vollständigkeit der Rückmeldung überprüft.
 * 
 * Dieser Thread dient weiterhin dazu die SMS-Inbox in der Datenbank zu
 * verarbeiten.
 * 
 * 
 * @author bsp
 * 
 */
public class AlarmDaemon implements IDaemon, IWatchdog
{
    /**
     * Watchdog-File
     */
    private String watchdogFile = "zabos_AlarmDaemon.wd";

    private final static Logger log = Logger.getLogger(AlarmDaemon.class);

    /**
     * Zeitintervall in ms zwischen zwei Alarmüberprüfungen
     * 
     */
    private static final int ALARM_POLLING_INTERVAL = 1500;

    /**
     * Thread
     */
    private Thread thread;

    /**
     * AlarmService
     */
    private IAlarmService alarmService;

    /**
     * SmsInService
     */
    private ISmsInService smsInService;

    private DBResource dbresource;

    /**
     * Wird vom Daemon-Thread nur gelesen
     */
    private DAEMON_STATUS daemonStatus = DAEMON_STATUS.OFFLINE;

    private boolean bWaitFree;

    /**
     * Konstruktor
     * 
     * @param _dbResource
     *            Unbedingt neue Datenbankverbindung für diesen Thread
     *            initalisieren, da es ansonsten zu Problemen kommen kann
     * @param _alarmService
     * @param _smsInService
     */
    public AlarmDaemon(final DBResource _dbResource,
                    final IAlarmService _alarmService,
                    final ISmsInService _smsInService)
    {
        dbresource = _dbResource;
        setAlarmService(_alarmService);
        setSmsInService(_smsInService);
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.ecw.zabos.alarmdaemon.IAlarmDaemon#init()
     */
    public void init() throws StdException
    {
        thread = new Thread()
        {
            public void run()
            {
                SystemKonfigurationTAO systemKonfigurationTAO = dbresource
                                .getTaoFactory().getSystemKonfigurationTAO();

                TraceDog traceDog = new TraceDog(getWatchdogFile());

                while ((getDaemonStatus() == DAEMON_STATUS.ONLINE))
                {
                    try
                    {
                        getSmsInService().processSmsInbox();

                        getAlarmService().processAktiveAlarme();

                        systemKonfigurationTAO.ueberpruefeSystemReaktivierung();

                        Thread.sleep(ALARM_POLLING_INTERVAL);

                        traceDog.trace();
                    }
                    catch (InterruptedException e)
                    {
                        log.error(e);
                        daemonStatus = DAEMON_STATUS.OFFLINE;
                    }
                }
                bWaitFree = false;
            }
        };

        // Hintergrundthread starten
        thread.setName("AlarmDaemon");

        daemonStatus = DAEMON_STATUS.STARTING_UP;

        thread.start();

        daemonStatus = DAEMON_STATUS.ONLINE;

        log.debug("AlarmDaemon wurde gestartet");
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.ecw.zabos.alarmdaemon.IAlarmDaemon#free()
     */
    public void free()
    {
        bWaitFree = true;
        daemonStatus = DAEMON_STATUS.SHUTTING_DOWN;

        try
        {
            // Auf die Beendigung des Hintergrundthreads warten
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
        log.debug("AlarmDaemon wurde gestoppt");
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

    /**
     * Setzt den {@link ISmsInService}
     * 
     * @param smsInService
     */
    final public void setSmsInService(ISmsInService smsInService)
    {
        this.smsInService = smsInService;
    }

    /**
     * Liefert den {@link ISmsInService}
     * 
     * @return
     */
    final public ISmsInService getSmsInService()
    {
        return smsInService;
    }

    /**
     * Setzt den {@link IAlarmService}
     * 
     * @param alarmService
     */
    final public void setAlarmService(IAlarmService alarmService)
    {
        this.alarmService = alarmService;
    }

    /**
     * Liefert den {@link IAlarmService}
     * 
     * @return
     */
    final public IAlarmService getAlarmService()
    {
        return alarmService;
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
     * /(non-Javadoc)
     * 
     * @see de.ecw.zabos.util.IWatchdog#getWatchdogFile()
     */
    public String getWatchdogFile()
    {
        return watchdogFile;
    }

    public String toString()
    {
        return "AlarmDaemon / delegiert die Verarbeitung aktiver Alarme und Nachrichten";
    }

}
