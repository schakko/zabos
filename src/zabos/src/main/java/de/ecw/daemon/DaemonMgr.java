package de.ecw.daemon;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import de.ecw.zabos.exceptions.StdException;

/**
 * Verwaltet die Hintergrund-Threads
 * 
 * @author ckl
 */
final public class DaemonMgr implements IDaemon
{
    /**
     * Läuft der {@link DaemonMgr}
     */
    private DAEMON_STATUS daemonStatus = DAEMON_STATUS.OFFLINE;

    private final static Logger log = Logger.getLogger(DaemonMgr.class);

    /**
     * Liste mit den verwalteten Daemons
     */
    private List<IDaemon> alManagedDaemons = new ArrayList<IDaemon>();

    /**
     * Privater Konstruktor, da wir eine Factory-Method besitzen
     */
    private DaemonMgr()
    {
    }

    /**
     * Setzt die zu verwaltenden Daemons
     * 
     * @param _alDaemons
     */
    public void setManagedDaemons(List<IDaemon> _alDaemons)
    {
        alManagedDaemons = _alDaemons;
    }

    /**
     * Liefert die zu verwaltenden Daemons zur�ck.
     * 
     * @return Ist *immer* eine ArrayList<IDaemon>
     */
    public List<IDaemon> getManagedDaemons()
    {
        if (alManagedDaemons == null)
        {
            alManagedDaemons = new ArrayList<IDaemon>();
        }

        return alManagedDaemons;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.ecw.zabos.daemon.IDaemon#free()
     */
    public void free()
    {
        daemonStatus = DAEMON_STATUS.SHUTTING_DOWN;
        log.info("Beende Daemons (" + getManagedDaemons().size() + ") ...");

        /*
         * zuerst die als letztes initalisierten Daemons deaktivieren, da sie
         * von den anderen evtl. abhängen
         */
        for (int i = (getManagedDaemons().size() - 1); i >= 0; i--)
        {
            IDaemon daemon = getManagedDaemons().get(i);
            daemon.free();
            log.info("  Daemon " + daemon + " beendet");
        }

        log.info("Alle Daemons beendet");
        daemonStatus = DAEMON_STATUS.OFFLINE;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.ecw.zabos.daemon.IDaemon#init()
     */
    public void init() throws StdException
    {
        daemonStatus = DAEMON_STATUS.STARTING_UP;

        log.info("Initialisiere Daemons (" + getManagedDaemons().size()
                        + ") ...");

        for (int i = 0, m = getManagedDaemons().size(); i < m; i++)
        {
            IDaemon daemon = getManagedDaemons().get(i);

            log.debug("  [" + (i + 1) + "/" + m + "] Initalisiere Daemon \""
                            + daemon + "\" ..");
            daemon.init();

            log.info("  [" + (i + 1) + "/" + m + "] Daemon \"" + daemon
                            + "\" initalisiert");
        }

        log.info("Alle Daemons initalisiert");
        daemonStatus = DAEMON_STATUS.ONLINE;
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

}
