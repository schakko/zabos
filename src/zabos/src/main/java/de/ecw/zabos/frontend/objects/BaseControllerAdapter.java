package de.ecw.zabos.frontend.objects;

import org.apache.log4j.Logger;

import de.ecw.zabos.frontend.Navigation;
import de.ecw.zabos.frontend.controllers.SecurityController;
import de.ecw.zabos.frontend.dispatchers.FrontendDispatcher;
import de.ecw.zabos.frontend.ressources.RequestResources;
import de.ecw.zabos.license.License;
import de.ecw.zabos.sql.resource.DBResource;
import de.ecw.zabos.sql.vo.properties.IPropertyKuerzel;
import de.ecw.zabos.sql.vo.properties.IPropertyName;

/**
 * BaseController ist der Vater aller Controller.<br>
 * Ein Controller nimmt alle Anfragen von dem zugehoerigen Dispatcher entgegen
 * und verwaltet dann die passenden Actions/Views.
 * 
 * @author ckl
 */
public class BaseControllerAdapter implements IBaseController,
                IOnProcessACLFailed
{
    // Serial
    public final static long serialVersionUID = 1209312049;

    // Logger-Instanz
    private final static Logger log = Logger
                    .getLogger(BaseControllerAdapter.class);

    /**
     * Verzeichnis mit den Templates
     */
    private String dirAction = "";

    /**
     * Datei, die als Standard fuer den Controller gilt
     */
    private String defaultJspFile = "index.jsp";

    /**
     * Legt fest, ob gelöschte Objekte automatisch umbenannt werden, wenn neue
     * Objekte mit dem selben Namen ({@link IPropertyName}) oder Kürzel (
     * {@link IPropertyKuerzel}) erstellt werden.<br />
     * Standardmäßig ist dies der Fall
     */
    private boolean renameDeletedObject = true;

    /**
     * Lizenz-Datei
     */
    private License license = null;

    /**
     * Legt fest, ob automatisch nach Abarbeiten des Controllers geforwardet
     * werden soll.<br />
     * Standard ist true.
     */
    private boolean implicitForward = true;

    /**
     * Datenbank-Verbindung
     */
    protected DBResource dbResource;

    public BaseControllerAdapter(final DBResource _dbRessource)
    {
        dbResource = _dbRessource;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.ecw.zabos.frontend.objects.IController#setActionDir(java.lang.String)
     */
    public void setActionDir(String _dir)
    {
        this.dirAction = _dir;
    }

    /**
     * Liefert den Ordnernamen des Verzeichnisses zurueck, in dem sich die
     * Templates befinden
     * 
     * @return String mit dem Verzeichnis, in dem sich die Templates befinden
     */
    public String getActionDir()
    {
        return this.dirAction;
    }

    /**
     * Muss von den Child-Klassen implementiert werden.<br>
     * Diese Methode wird aufgerufen, wenn die Action dispatcht wird.<br>
     * setRequestIds ist dafuer gedacht, dass die richtigen Parameter gesetzt
     * sind.
     * 
     * @param req
     */
    public void setRequestIds(RequestResources req)
    {

    }

    /**
     * Kann von den jeweiligen Child-Klassen überschrieben werden, damit die
     * Sicherheits-Bestimmungen fuer einzelne Bereiche des Systems durchgesetzt
     * werden koennen.<br>
     * Standardmaessig ueberprueft diese Routine, ob der Benutzer eingeloggt
     * ist.<br>
     * Falls dies nicht der Fall ist, wird er auf die Seite
     * /jsp/security/index.jsp weitergeleitet
     * 
     * @return false, wenn die ACLs nicht durchgesetzt werden konnten
     */
    public boolean processACL(RequestResources req)
    {
        // Benutzer ist nicht eingeloggt => View und Action �ndern
        if ((this instanceof SecurityController) == false)
        {
            if (req.getUserBean().isLoggedIn() == false)
            {
                String forwardPage = req.getServletRequest().getRequestURI();

                if (req.getServletRequest().getMethod().equals("GET")
                                && req.getServletRequest().getQueryString() != null)
                {
                    forwardPage += "?"
                                    + req.getServletRequest().getQueryString();
                }

                req.getServletRequest()
                                .setAttribute("forwardPage", forwardPage);

                log
                                .debug("User ist nicht eingeloggt, setze Weiterleitung zum Login-Controller");
                req.setForwardPage("/controller/"
                                + Navigation.ACTION_DIR_SECURITY);

                // 20060601_1200 CKL: Gefixt - Personen konnten vorher Aktionen
                // ausfuehren, OBWOHL sie nicht eingeloggt waren.
                return false;
            }
        }

        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.ecw.zabos.frontend.objects.IBaseController#run(de.ecw.zabos.frontend
     * .ressources.RequestResources)
     */
    public void run(RequestResources req)
    {
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.ecw.zabos.frontend.objects.IBaseController#setViewData(de.ecw.zabos
     * .frontend.ressources.RequestResources)
     */
    public void setViewData(RequestResources req)
    {

    }

    /**
     * Liefert die Datenbankresource
     * 
     * @return
     */
    public DBResource getDbResource()
    {
        return dbResource;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.ecw.zabos.frontend.objects.IBaseController#setDefaultJspFile(java.
     * lang.String)
     */
    public void setDefaultJspFile(String defaultJspFile)
    {
        this.defaultJspFile = defaultJspFile;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.ecw.zabos.frontend.objects.IBaseController#getDefaultJspFile()
     */
    public String getDefaultJspFile()
    {
        return defaultJspFile;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.ecw.zabos.frontend.objects.IBaseController#setImplicitForward(boolean)
     */
    public void setImplicitForward(boolean implicitForward)
    {
        this.implicitForward = implicitForward;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.ecw.zabos.frontend.objects.IBaseController#isImplicitForward()
     */
    public boolean isImplicitForward()
    {
        return implicitForward;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.ecw.zabos.frontend.objects.IOnProcessACLFailed#onProcessACLFailed(
     * de.ecw.zabos.frontend.objects.IBaseController,
     * de.ecw.zabos.frontend.dispatchers.FrontendDispatcher,
     * de.ecw.zabos.frontend.ressources.RequestResources)
     */
    public void onProcessACLFailed(IBaseController controller,
                    FrontendDispatcher frontendDispatcher, RequestResources req)
    {
        if ((req.getForwardPage() != null)
                        && (!req.getForwardPage().equals("")))
        {
            log
                            .info("Da die Sicherheitsrichtlinie nicht durchgesetzt werden konnte (Benutzer nicht eingeloggt), wird auf die Login-Seite weitergeleitet");

            try
            {
                frontendDispatcher.forward(req, req.getForwardPage());
            }
            catch (Exception e)
            {
                log.error("Konnte nicht zur Weiterleitungsseite forwarden: "
                                + e.getMessage());
            }
        }
    }

    /**
     * Setzt die Lizenz
     */
    public void setLicense(License _license)
    {
        license = _license;
    }

    /**
     * Liefert die Lizenz
     * 
     * @return
     */
    public License getLicense()
    {
        return license;
    }

    /**
     * Baut einen String für die Log-Message
     * 
     * @param _req
     * @param _msg
     * @return
     */
    protected String buildLogMessage(final RequestResources req, String _msg)
    {
        StringBuffer sb = new StringBuffer();
        sb.append("[");
        if (req.getUserBean().getPerson() != null)
        {
            sb.append(req.getUserBean().getPerson().getName());
        }
        else
        {
            sb.append("???");
        }
        sb.append("] ");
        sb.append(_msg);

        return sb.toString();
    }

    public void setRenameDeletedObject(boolean renameDeletedObject)
    {
        this.renameDeletedObject = renameDeletedObject;
    }

    public boolean isRenameDeletedObject()
    {
        return renameDeletedObject;
    }
}
