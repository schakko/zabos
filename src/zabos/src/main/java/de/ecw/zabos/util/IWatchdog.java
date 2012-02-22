package de.ecw.zabos.util;

/**
 * Interface für Watchdog-Dateien
 * 
 * @author ckl
 * 
 */
public interface IWatchdog
{
    /**
     * Liefert das Watchdog-File
     * 
     * @return
     */
    public String getWatchdogFile();

    /**
     * Setzt das Watchdog-Datei
     * 
     * @param _file
     */
    public void setWatchdogFile(String _file);
}
