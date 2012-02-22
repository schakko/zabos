package de.ecw.zabos.frontend.dispatchers;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import de.ecw.zabos.Globals;
import de.ecw.zabos.SpringContext;
import de.ecw.zabos.frontend.objects.BaseDispatcher;
import de.ecw.zabos.frontend.objects.IBaseController;
import de.ecw.zabos.frontend.objects.IOnProcessACLFailed;
import de.ecw.zabos.frontend.ressources.RequestResources;

/**
 * Der {@link FrontendDispatcher} nimmt alle Anfragen entgegen und leitet sie
 * dann die passenden Controller weiter
 * 
 * @author ckl
 * 
 */
public class FrontendDispatcher extends BaseDispatcher
{
    private static final long serialVersionUID = -4528471043842006933L;

    // Logger-Instanz
    private final static Logger log = Logger
                    .getLogger(FrontendDispatcher.class);

    @Override
    protected void dispatchRequest(final HttpServletRequest req,
                    final HttpServletResponse res) throws UnavailableException
    {
        RequestResources reqRes = new RequestResources(req, res, servletConfig,
                        dbResource);

        String name = servletConfig.getServletName();
        IBaseController controller = (IBaseController) SpringContext
                        .getInstance().getBean(name, IBaseController.class);

        if (controller == null)
        {
            throw new UnavailableException("Konnte keinen Controller fuer "
                            + servletConfig.getServletName() + " erstellen");
        }

        try
        {
            processRequest(controller, reqRes);
        }
        catch (Exception e)
        {
            log.error("Fehler waehrend des Verarbeitens des Controllers "
                            + controller.getClass().toString() + ": "
                            + e.getMessage());
            log.error("Exception: ", e);
        }
    }

    /**
     * Verarbeitet den Request in dem an den jeweiligen Controller
     * weiterdelegiert wird.
     * <ul>
     * <li>Zuerst werden die angefragten ID-Parameter über
     * {@link IBaseController#setRequestIds(RequestResources)} gesetzt</li>
     * <li>Attribut "tab" setzen</li>
     * <li>Verarbeiten der Richtlinien über
     * {@link IBaseController#processACL(RequestResources)}</li>
     * <li>Wenn die Richtlinie nicht durchgesetzt werden sollte, bricht die
     * Methode ab. Wenn der controller noch das Interface
     * {@link IOnProcessACLFailed} implementiert, wird
     * {@link IOnProcessACLFailed#onProcessACLFailed(IBaseController, FrontendDispatcher, RequestResources)}
     * ausgeführt</li>
     * <li>Ausführen von Aktionen (Submit) über
     * {@link IBaseController#run(RequestResources)}</li>
     * <li>Setzen der View-Inhalte über
     * {@link IBaseController#setViewData(RequestResources)}</li>
     * <li>Setzen grundlegender Umgebungsvariablen</li>
     * <li>Forwarding über {@link #forward(RequestResources, String)}</li>
     * </ul>
     * 
     * @param controller
     *            Ist *immer* ein gültiger Controller
     * @param req
     * @throws ServletException
     * @throws IOException
     */
    protected void processRequest(final IBaseController controller,
                    final RequestResources req) throws ServletException, IOException
    {
        // Request-IDs aufloesen
        controller.setRequestIds(req);

        // Tab setzen
        if (req.getServletRequest().getParameter("tab") != null)
        {
            req.getServletRequest().setAttribute(
                            "tab",
                            req.getServletRequest().getParameter("tab")
                                            .toString());
        }

        // Sicherheits-Policies verarbeiten
        if (!controller.processACL(req))
        {
            log
                            .error("Die Richtlinienbestimmungen konnten nicht durchgesetzt werden.");

            if (controller instanceof IOnProcessACLFailed)
            {
                ((IOnProcessACLFailed) controller).onProcessACLFailed(
                                controller, this, req);
            }

            log.info("Controller wird nicht weiter bearbeitet");
            return;
        }

        // Action ausfuehren
        controller.run(req);

        // Geaenderte Daten anzeigen
        controller.setViewData(req);

        // 2006-06-09 CKL: Versionsinformationen
        req.getServletRequest().setAttribute("zabos_version",
                        Globals.getVersion());
        req.getServletRequest().setAttribute("zabos_revision",
                        Globals.getRevision());
        req.getServletRequest().setAttribute("zabos_builder",
                        Globals.getBuilder());
        req.getServletRequest().setAttribute("zabos_builddate",
                        Globals.getBuildDate());

        // Wenn automatisch geforwardet werden soll, geschieht das an dieser
        // Stelle
        if (controller.isImplicitForward())
        {
            // Forward auf einen anderen Controller
            if ((req.getForwardPage() != null)
                            && (!req.getForwardPage().equals("")))
            {
                this.forward(req, req.getForwardPage());
            }
            // Forward an die JSP-Seite
            else
            {
                // Weiterleiten zur Location bzw. JSP-Datei
                this.forward(req, "/jsp/" + controller.getActionDir() + "/"
                                + controller.getDefaultJspFile());
            }
        }
    }

    /**
     * Forwarded das Servlet zur angegeben Ressource
     * 
     * @param _req
     * @param _res
     * @param _path
     * @throws ServletException
     * @throws IOException
     */
    public void forward(final RequestResources req, String _path) throws ServletException, IOException
    {
        log.info("Forwarde an " + _path);

        String newPath = _path.replace("/"
                        + req.getServletConfig().getServletContext()
                                        .getServletContextName(), "");
        ServletContext ctx = req.getServletConfig().getServletContext();
        RequestDispatcher disp = ctx.getRequestDispatcher(newPath);
        disp.forward(req.getServletRequest(), req.getServletResponse());
    }

}
