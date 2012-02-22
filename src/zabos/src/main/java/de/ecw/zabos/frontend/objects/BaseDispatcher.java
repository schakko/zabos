package de.ecw.zabos.frontend.objects;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import de.ecw.zabos.Globals;
import de.ecw.zabos.sql.resource.DBResource;

/**
 * Diese abstrakte Basis-Klasse dient als Vater aller Dispatcher.<br>
 * <br>
 * Ein Dispatcher dient als "erste Anlaufstelle" für eine Anfrage von einem
 * Client.<br>
 * Der Dispatcher erzeugt danach einen Controller des zugeh�rigen Dispatchers
 * und verarbeitet die Eingabedaten.<br>
 * <br>
 * Durch diese Lösung Dispatcher > Controller > Action > View wird
 * sichergestellt, dass keine Nebenläufigkeiten oder invalide Sessions entstehen
 * können.
 * 
 * @author ckl
 */
abstract public class BaseDispatcher extends HttpServlet implements
                javax.servlet.Servlet
{
    // Serial
    public final static long serialVersionUID = 1209312049;

    // Logger-Instanz
    private final static Logger log = Logger.getLogger(BaseDispatcher.class);

    // Datenbank-Ressource
    protected DBResource dbResource = null;

    // Servlet-Konfiguration
    protected ServletConfig servletConfig = null;

    // Servlet-Kontext
    protected ServletContext ctx = null;

    /*
     * (non-Javadoc)
     * 
     * @see javax.servlet.GenericServlet#init()
     */
    @Override
    public void init(ServletConfig conf) throws ServletException
    {
        super.init(conf);
        log.debug("Initalisiere Ressourcen.");

        this.servletConfig = conf;
        this.ctx = conf.getServletContext();

        try
        {
            if (!Globals.isInitialized())
            {
                Globals.init(conf);
                this.dbResource = Globals.getDBResource();
                log.debug("Datenbank initalisiert");
            }
            else
            {
                this.dbResource = Globals.getDBResource();
            }
        }
        catch (ServletException e)
        {
            log.error(e);
            throw new UnavailableException(e.getMessage());
        }
    }

    /*
     * (non-Java-doc)
     * 
     * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest arg0,
     * HttpServletResponse arg1)
     */
    protected void doGet(HttpServletRequest _req, HttpServletResponse _res) throws ServletException, IOException
    {
        // Wichtig fuers Encoding, ansonsten funktioniert UTF-8 nicht
        _req.setCharacterEncoding("UTF-8");
        dispatchRequest(_req, _res);
    }

    /*
     * (non-Java-doc)
     * 
     * @see javax.servlet.http.HttpServlet#doPost(HttpServletRequest arg0,
     * HttpServletResponse arg1)
     */
    protected void doPost(HttpServletRequest _req, HttpServletResponse _res) throws ServletException, IOException
    {
        // Wichtig fürs Encoding, ansonsten funktioniert UTF-8 nicht
        _req.setCharacterEncoding("UTF-8");
        dispatchRequest(_req, _res);
    }

    /**
     * Servlet zerststören und Datenbankressourcen wieder freigeben
     */
    public void destory()
    {
        if (dbResource != null)
        {
            dbResource.free();
        }
    }

    /**
     * Ruft den passenden Controller auf.
     * 
     * @param _req
     * @param _res
     */
    abstract protected void dispatchRequest(HttpServletRequest _req,
                    HttpServletResponse _res) throws UnavailableException;
}
