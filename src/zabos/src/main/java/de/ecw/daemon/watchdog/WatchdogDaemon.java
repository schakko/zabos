package de.ecw.daemon.watchdog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import de.ecw.daemon.ChildDaemonStatus;
import de.ecw.daemon.IDaemon;
import de.ecw.interceptors.InterceptorDelegator;
import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.types.UnixTime;
import de.ecw.zabos.util.IWatchdog;
import de.ecw.zabos.util.TraceDog;

/**
 * Die Klasse WatchdogDaemon dient für die Überwachung einzelner Child-Daemons.
 * WatchdogDaemon besitzt n Child-Daemons.
 * 
 * @author ckl
 */
public class WatchdogDaemon implements IDaemon, IWatchdog
{
    /**
     * Interceptoren, die aufgerufen werden, sobald ein Child-Daemon in den
     * Status "offline" wechselt
     */
    private InterceptorDelegator onOfflineInterceptors = new InterceptorDelegator();

    /**
     * Watchdog-Datei
     */
    private String watchdogFile = "zabos_WatchdogDaemon.wd";

    /**
     * Interceptoren, die aufgerufen werden, sobald ein Child-Daemon in den
     * Status "online" wechselt
     */
    private InterceptorDelegator onOnlineInterceptors = new InterceptorDelegator();

    /**
     * Liste mit den Child-Daemons, die als *online* markiert sind
     */
    private List<IDaemon> listMarkedAsOnline = new ArrayList<IDaemon>();

    /**
     * Map mit den einzelnen Child-Daemons und deren Status
     */
    private Map<IDaemon, ChildDaemonStatus> mapObservableChildDaemons = new HashMap<IDaemon, ChildDaemonStatus>();

    /**
     * Dateiname der Watchdog-Datei des einzelnen Child-Daemons
     */
    private String watchdogObserveableObjectFilePattern = "zabos_daemon_"
                    + OPT_FILENAME + ".wd";

    public final static String OPT_FILENAME = "%name%";

    private final static Logger log = Logger.getLogger(WatchdogDaemon.class);

    private DAEMON_STATUS daemonStatus = DAEMON_STATUS.OFFLINE;

    private String watchdogChildName = "Watchdog-Child '" + OPT_FILENAME + "'";

    /**
     * Thread, der die Child-Daemons überwacht
     */
    private Thread watchdogThread = null;

    /**
     * Zeit, in der der Watchdog vor sich hin schlummert in Millisekunden
     */
    private int watchdogSleepTime = 5000;

    /**
     * Freet die einzelnen Child-Daemons
     */
    public void free()
    {
        daemonStatus = DAEMON_STATUS.SHUTTING_DOWN;

        Iterator<IDaemon> it = getObserveableChildDaemonsWithStatus().keySet()
                        .iterator();

        while (it.hasNext())
        {
            IDaemon daemon = it.next();
            ChildDaemonStatus status = getObserveableChildDaemonsWithStatus()
                            .get(daemon);
            freeDaemonInstance(daemon, status);
        }

        daemonStatus = DAEMON_STATUS.OFFLINE;
    }

    /**
     * Initalisiert die einzelnen Überwachungs-Threads
     */
    public void init() throws StdException
    {
        daemonStatus = DAEMON_STATUS.STARTING_UP;

        if (watchdogThread == null)
        {
            watchdogThread = new Thread(getEventThreadRunnable());
        }

        watchdogThread.setName("Watchdog-Thread");

        watchdogThread.start();
    }

    /**
     * Beendet einen Child-Daemon und entfernt sie aus der Liste der als
     * online markierten Child-Daemons
     * 
     * @param _daemon
     * @param _status
     */
    private void freeDaemonInstance(IDaemon _daemon, ChildDaemonStatus _status)
    {
        // Inaktivität setzen und Daemon aus der
        // Liste der als online markierten Daemons
        // entfernen
        _status.setInactiveSince(UnixTime.now());
        listMarkedAsOnline.remove(_daemon);
        // Daemon befreien
        _daemon.free();
        log.info(getWatchdogChildName(_status.getName()) + " beendet.");

        onOfflineInterceptors.intercept(_daemon);
    }

    /**
     * Liefert den Watchdog-Thread zurück
     * 
     * @return
     */
    private Runnable getEventThreadRunnable()
    {
        return new Runnable()
        {
            /**
             * Wenn der Status des Daemons als Unavailable gekennzeichnet wurde,
             * muss er aus der Liste der als online markierten DAemons entfernt
             * werden.
             * 
             * @param _observedDaemon
             * @param _status
             */
            private void handleStatusUnavailable(IDaemon _observedDaemon,
                            ChildDaemonStatus _status)
            {
                log.info(" Daemon ist als *unavailable* markiert.");

                if (listMarkedAsOnline.contains(_observedDaemon))
                {
                    listMarkedAsOnline.remove(_observedDaemon);
                }
            }

            /**
             * Wenn der Status des Daemons als Online gekennzeichnet wurde, muss
             * der Status der Reaktivierungs-Versuche resettet und danach der
             * Daemon in die Liste der als online markierten Daemons verschoben
             * werden.
             * 
             * @param _observedDaemon
             * @param _status
             */
            private void handleStatusOnline(IDaemon _observedDaemon,
                            ChildDaemonStatus _status)
            {
                if (!listMarkedAsOnline.contains(_observedDaemon))
                {
                    log
                                    .info(" Daemon war als offline markiert und ist nun wieder *online*");

                    _status.resetReactivationTries();
                    onOnlineInterceptors.intercept(_observedDaemon);
                    listMarkedAsOnline.add(_observedDaemon);
                }

                log.info(" Daemon ist online");
            }

            /**
             * Wenn der Status des Daemons als Offline gekennzeichnet wurde
             * <ul>
             * <li>
             * muss er - wenn er im letzten Lauf als online gekennzeichnet
             * gewesen ist - beendet und danach aus der Liste der als online
             * markierten Daemons entfernt werden. Somit wird ein sauberer
             * Shutdown gewährleistet.</li>
             * <li>
             * Ist er hingegen bei der letzten Überprüfung offline gewesen, muss
             * er nun wieder gestartet werden.</li>
             * </ul>
             * 
             * @param _observedDaemon
             * @param _status
             */
            private void handleStatusOffline(IDaemon _observedDaemon,
                            ChildDaemonStatus _status)
            {
                // Child-Daemon war während des letzten Laufes
                // noch
                // als Online markiert
                if (listMarkedAsOnline.contains(_observedDaemon))
                {
                    log
                                    .error(" Daemon war als online markiert, aber laeuft nicht mehr. Ist nun als *offline* markiert");
                    freeDaemonInstance(_observedDaemon, _status);
                }
                else
                {
                    log.info(" Versuche Daemon zu aktivieren...");

                    _status.setLastReactivationPoint(UnixTime.now());
                    _status.incrementReactivationTries();

                    try
                    {
                        _observedDaemon.init();
                        log.info(" Daemon wurde erfolgreich gestartet");
                    }
                    catch (StdException e)
                    {
                        log.error(" Daemon konnte *nicht* gestartet werden: "
                                        + e.getMessage());
                    }
                }
            }

            public void run()
            {
                TraceDog traceDog = new TraceDog(getWatchdogFile());
                daemonStatus = DAEMON_STATUS.ONLINE;

                while ((getDaemonStatus() == DAEMON_STATUS.ONLINE))
                {
                    try
                    {
                        Iterator<IDaemon> it = mapObservableChildDaemons
                                        .keySet().iterator();

                        while (it.hasNext())
                        {
                            IDaemon observedDaemon = it.next();
                            ChildDaemonStatus status = mapObservableChildDaemons
                                            .get(observedDaemon);

                            log.info("Ueberpruefe Daemon "
                                            + getWatchdogChildName(status
                                                            .getName())
                                            + " ... :");

                            // Daemon läuft wieder
                            switch (observedDaemon.getDaemonStatus())
                            {
                                case UNAVAILABLE:
                                    handleStatusUnavailable(observedDaemon,
                                                    status);
                                    break;
                                case STARTING_UP:
                                    log.info(" Daemon startet gerade");
                                    break;
                                case ONLINE:
                                    handleStatusOnline(observedDaemon, status);
                                    break;
                                case SHUTTING_DOWN:
                                    log.info(" Daemon wird gerade beendet");
                                    break;
                                case OFFLINE:
                                    handleStatusOffline(observedDaemon, status);
                                    break;
                            }
                        }
                        traceDog.trace();

                        Thread.sleep(getWatchdogSleepTime());
                    }
                    catch (InterruptedException e)
                    {
                        log
                                        .error("InterruptedException waehrend Watchdog-Ausfuerhung: "
                                                        + e.getMessage());
                    }
                }
            }
        };
    }

    synchronized public DAEMON_STATUS getDaemonStatus()
    {
        return daemonStatus;
    }

    /**
     * Liefert die überwachten Child-Daemons zurück
     * 
     * @return
     */
    public List<IDaemon> getObserveableChildDaemons()
    {
        return new ArrayList<IDaemon>(mapObservableChildDaemons.keySet());
    }

    /**
     * Liefert die Map mit den überwachten Child-Daemons und deren Status zurück
     * 
     * @return
     */
    synchronized public Map<IDaemon, ChildDaemonStatus> getObserveableChildDaemonsWithStatus()
    {
        return mapObservableChildDaemons;
    }

    /**
     * Fügt den Daemon zu den überwachten Objekten hinzu. Falls der Daemon eine
     * Instanz von {@link IWatchdog} ist, wird der Pfad zur Watchdogdatei aus
     * {@link #getWatchdogFilePattern()} generiert.
     * 
     * @param _daemon
     * @param _name
     *            Wird kein Name übergeben (null oder Länge = 0), wird als Name
     *            ein fortlaufender Counter benutzt
     */
    public void addObservableChildDaemon(IDaemon _daemon, String _name)
    {
        ChildDaemonStatus status = new ChildDaemonStatus();

        if (_name != null && _name.length() > 0)
        {
            status.setName(_name);
        }

        getObserveableChildDaemonsWithStatus().put(_daemon, status);
        log
                        .info(getWatchdogChildName(status.getName())
                                        + " wurde zu den ueberwachten Child-Daemons hinzugefuegt");

        if (_daemon instanceof IWatchdog)
        {
            String _wd = getWatchdogFilePattern();
            _wd = _wd.replace(OPT_FILENAME, status.getName());
            ((IWatchdog) _daemon).setWatchdogFile(_wd);
        }
    }

    /**
     * Liefert die Liste mit den als *online* markierten Child-Daemons zurück
     * 
     * @return
     */
    synchronized public List<IDaemon> getMarkedAsOnlineChildDaemons()
    {
        return listMarkedAsOnline;
    }

    /**
     * Setzt die Interceptoren, die aufgerufen werden sollen, wenn ein
     * Child-Daemon in den Status "Online" wechselt.
     * 
     * @param _delegator
     */
    public void setOnOnlineInterceptors(InterceptorDelegator _delegator)
    {
        this.onOnlineInterceptors = _delegator;
    }

    /**
     * Setzt die Interceptoren, die aufgerufen werden sollen, wenn ein
     * Child-Daemon in den Status "Offline" wechselt.
     * 
     * @param _delegator
     */
    public void setOnOfflineInterceptors(InterceptorDelegator _delegator)
    {
        this.onOfflineInterceptors = _delegator;
    }

    /**
     * Setzt die Zeitperiode, nach der die einzelnen Child-Daemons überprüft
     * werden sollen
     * 
     * @param watchdogSleepTime
     */
    public void setWatchdogSleepTime(int watchdogSleepTime)
    {
        this.watchdogSleepTime = watchdogSleepTime;
    }

    public int getWatchdogSleepTime()
    {
        return watchdogSleepTime;
    }

    /**
     * Setzt das File-Pattern, dass für die einzelnen Daemons verwendet werden
     * sollen. Parameter {@value #OPT_FILENAME} kann als Plathalter für den
     * Namen des Child-Daemons benutzt werden.
     * 
     * @param watchdogFilePattern
     */
    public void setWatchdogFilePattern(String watchdogFilePattern)
    {
        this.watchdogObserveableObjectFilePattern = watchdogFilePattern;
    }

    public String getWatchdogFilePattern()
    {
        return watchdogObserveableObjectFilePattern;
    }

    public String getWatchdogFile()
    {
        return watchdogFile;
    }

    public void setWatchdogFile(String _watchdogFile)
    {
        watchdogFile = _watchdogFile;
    }

    public void setWatchdogChildName(String watchdogChildName)
    {
        this.watchdogChildName = watchdogChildName;
    }

    public String getWatchdogChildName(String _childName)
    {
        return watchdogChildName.replace(OPT_FILENAME, _childName);
    }
}
