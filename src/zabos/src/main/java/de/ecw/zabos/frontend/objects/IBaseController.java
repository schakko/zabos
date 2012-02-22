package de.ecw.zabos.frontend.objects;

import de.ecw.zabos.frontend.ressources.RequestResources;
import de.ecw.zabos.license.License;

/**
 * Interface für die Controller
 * 
 * @author ckl
 * 
 */
public interface IBaseController
{
    /**
     * Setzt das Verzeichnis, in dem sich die JSP-Dateien/Views befinden
     * 
     * @param _dir
     */
    public void setActionDir(String _dir);

    /**
     * Liefert das Verzeichnis, in dem JSP-Dateien/Views liegen
     * 
     * @return
     */
    public String getActionDir();

    /**
     * Setzt die anzuzeigenden Daten
     * 
     * @param req
     */
    public void setViewData(RequestResources req);

    /**
     * Diese Methode wird aufgerufen, wenn die Action dispatcht wird.<br>
     * setRequestIds ist dafür gedacht, dass die richtigen Parameter gesetzt
     * sind.
     * 
     * @param req
     */
    public void setRequestIds(RequestResources req);

    /**
     * Kann von den jeweiligen Child-Klassen überschrieben werden, damit die
     * Sicherheits-Bestimmungen für einzelne Bereiche des Systems durchgesetzt
     * werden können.<br>
     * Standardäßig überprüft diese Routine, ob der Benutzer eingeloggt ist.<br>
     * Falls dies nicht der Fall ist, wird er auf die Seite
     * /jsp/security/index.jsp weitergeleitet
     * 
     * @param req
     * @return false, wenn die ACLs nicht durchgesetzt werden konnten
     */
    public boolean processACL(RequestResources req);

    /**
     * Führt die zugehörige Aktion innerhalb des Controllers aus
     * 
     * @param req
     */
    public void run(RequestResources req);

    /**
     * Liefert die Standard-Datei
     * 
     * @return
     */
    public String getDefaultJspFile();

    /**
     * Setzt die Standard-JSP-Datei, die beim Aufruf des Controllers ohne
     * Optionen angezeigt werden soll
     * 
     * @param _jspFile
     */
    public void setDefaultJspFile(String _jspFile);

    /**
     * Setzt die Lizenz-Datei
     * 
     * @param _license
     */
    public void setLicense(License _license);

    /**
     * Legt fest, ob nach Abarbeiten des Controllers automatisch geforwardet
     * werden soll
     * 
     * @param implicitForward
     */
    public void setImplicitForward(boolean implicitForward);

    /**
     * Liefert, ob nach Abarbeiten des Controllers automatisch geforwardet
     * werden soll
     * 
     * @param implicitForward
     */
    public boolean isImplicitForward();
}