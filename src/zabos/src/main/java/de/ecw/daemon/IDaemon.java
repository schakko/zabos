package de.ecw.daemon;

import de.ecw.zabos.exceptions.StdException;

/**
 * Interface zur Definition von Daemons, die im Hintergrund als Thread laufen
 * 
 * @author ckl
 */
public interface IDaemon extends IDaemonStatus
{

    /**
     * Hintergrundthread starten und Umgebungsvariablen setzen
     * 
     * @throws StdException
     */
    public void init() throws StdException;

    /**
     * Hintergrundthread stoppen
     * 
     */
    public void free();
}
