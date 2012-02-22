package de.ecw.daemon;

public interface IDaemonStatus
{
    public enum DAEMON_STATUS
    {
        STARTING_UP, ONLINE, SHUTTING_DOWN, OFFLINE, UNAVAILABLE
    };

    /**
     * Liefert zurueck ob der Daemon noch läuft
     * 
     * @return
     */
    public DAEMON_STATUS getDaemonStatus();
}
