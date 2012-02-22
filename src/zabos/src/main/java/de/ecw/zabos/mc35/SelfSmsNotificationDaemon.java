package de.ecw.zabos.mc35;

import java.util.List;

import org.apache.log4j.Logger;

import de.ecw.daemon.IDaemon;
import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.sql.resource.DBResource;
import de.ecw.zabos.sql.tao.SystemKonfigurationTAO;
import de.ecw.zabos.sql.vo.SystemKonfigurationMc35VO;
import de.ecw.zabos.types.UnixTime;
import de.ecw.zabos.util.IWatchdog;
import de.ecw.zabos.util.TraceDog;

public class SelfSmsNotificationDaemon implements IWatchdog, IDaemon
{
    private String watchdogFile = "zabos_SelfSmsNotification.wd";

    private final static Logger log = Logger
                    .getLogger(SelfSmsNotificationDaemon.class);

    /**
     * Thread
     */
    private Thread thread;

    private DBResource dbResource;

    private boolean bWaitFree;

    private MC35ManagerDaemon mc35ManagerDaemon;

    /**
     * Wird vom Daemon-Thread nur gelesen
     */
    private DAEMON_STATUS daemonStatus = DAEMON_STATUS.OFFLINE;

    /**
     * Interval, nach dem eine SMS gesendet wird - Standardmäßig werden SMSen
     * nach 30 Tagen gesendet
     */
    private long notificationIntervalInSeconds = (UnixTime.SEKUNDEN_PRO_TAG * 30);

    /**
     * Zeit, in der der Daemon schlafen gelegt - Standardmäßig wird nach 5
     * Minuten die einzelnen Modems überprüft
     */
    private long daemonSleepTimeInSeconds = (1000 * 60 * 5);

    public SelfSmsNotificationDaemon(DBResource _dbResource,
                    MC35ManagerDaemon _mc35ManagerDaemon)
    {
        setDbResource(_dbResource);
        setMc35ManagerDaemon(_mc35ManagerDaemon);
    }

    public void setNotificationIntervalInSeconds(
                    long notificationIntervalInSeconds)
    {
        this.notificationIntervalInSeconds = notificationIntervalInSeconds;
    }

    public long getNotificationIntervalInSeconds()
    {
        return notificationIntervalInSeconds;
    }

    public void setDaemonSleepTimeInSeconds(long daemonSleepTimeInSeconds)
    {
        this.daemonSleepTimeInSeconds = daemonSleepTimeInSeconds;
    }

    public long getDaemonSleepTimeInSeconds()
    {
        return daemonSleepTimeInSeconds;
    }

    public void setDbResource(DBResource dbResource)
    {
        this.dbResource = dbResource;
    }

    public DBResource getDbResource()
    {
        return dbResource;
    }

    public void setWatchdogFile(String watchdogFile)
    {
        this.watchdogFile = watchdogFile;
    }

    public String getWatchdogFile()
    {
        return watchdogFile;
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
                SystemKonfigurationTAO systemKonfigurationTAO = getDbResource()
                                .getTaoFactory().getSystemKonfigurationTAO();

                TraceDog traceDog = new TraceDog(getWatchdogFile());

                long notificationInterval = getNotificationIntervalInSeconds() * 1000;

                while ((getDaemonStatus() == DAEMON_STATUS.ONLINE))
                {
                    List<IDaemon> daemons = getMc35ManagerDaemon()
                                    .getMarkedAsOnlineChildDaemons();

                    log.info("Checking online instances [" + daemons.size()
                                    + "] of MC35 for self notification");

                    for (IDaemon daemon : daemons)
                    {
                        MC35 mc35 = (MC35) daemon;
                        SystemKonfigurationMc35VO mc35Konfiguration = mc35
                                        .getSystemKonfigurationMC35();
                        UnixTime now = UnixTime.now();
                        UnixTime lastSelfSmsNotify = UnixTime.now();

                        // Standardmäßig wird eine SMS gesendet, wenn noch kein Selbsttest stattgefunden hat
                        boolean bSendNotification = true;

                        if (mc35Konfiguration
                                        .getZeitpunktLetzterSmsSelbsttest() != null)
                        {
                            lastSelfSmsNotify = new UnixTime(mc35Konfiguration
                                            .getZeitpunktLetzterSmsSelbsttest()
                                            .getTimeStamp());
                            lastSelfSmsNotify.add(new UnixTime(
                                            notificationInterval));
                            bSendNotification = now
                                            .isLaterThan(lastSelfSmsNotify);
                        }

                        if (bSendNotification)
                        {
                            log.info("\"" + mc35.getAssignedPhoneNumber()
                                            + "\" needs self notification");

                            ShortMessage shortMessage = new ShortMessage(mc35
                                            .getAssignedPhoneNumber()
                                            .toString(), "Self notification");
                            mc35.scheduleOutgoingSms(shortMessage);
                            mc35.getSystemKonfigurationMC35()
                                            .setZeitpunktLetzterSmsSelbsttest(
                                                            now);

                            systemKonfigurationTAO
                                            .updateSystemKonfigurationMc35(mc35
                                                            .getSystemKonfigurationMC35());
                        }

                        UnixTime next = new UnixTime((mc35
                                        .getSystemKonfigurationMC35()
                                        .getZeitpunktLetzterSmsSelbsttest()
                                        .getTimeStamp() + notificationInterval));

                        log.info("Next self notification for \""
                                        + mc35.getAssignedPhoneNumber()
                                        + "\" is needed after passing "
                                        + next.toDateString() + " - "
                                        + next.toTimeString());
                    }

                    try
                    {
                        Thread.sleep((getDaemonSleepTimeInSeconds() * 1000));

                        traceDog.trace();
                    }
                    catch (InterruptedException e)
                    {
                        log.error(e);
                        daemonStatus = DAEMON_STATUS.OFFLINE;
                    }
                }
            }
        };

        // Hintergrundthread starten
        thread.setName("SelfSmsNotificationDaemon");

        daemonStatus = DAEMON_STATUS.STARTING_UP;

        thread.start();

        daemonStatus = DAEMON_STATUS.ONLINE;

        log.debug("SelfSmsNotificationDaemon wurde gestartet");
    }

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
        log.debug("SelfSmsNotificationDaemon wurde gestoppt");
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

    public void setMc35ManagerDaemon(MC35ManagerDaemon mc35ManagerDaemon)
    {
        this.mc35ManagerDaemon = mc35ManagerDaemon;
    }

    public MC35ManagerDaemon getMc35ManagerDaemon()
    {
        return mc35ManagerDaemon;
    }
}
