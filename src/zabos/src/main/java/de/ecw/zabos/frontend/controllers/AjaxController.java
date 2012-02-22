package de.ecw.zabos.frontend.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.frontend.ajax.IAjaxMethod;
import de.ecw.zabos.frontend.dispatchers.FrontendDispatcher;
import de.ecw.zabos.frontend.objects.BaseControllerAdapter;
import de.ecw.zabos.frontend.objects.IBaseController;
import de.ecw.zabos.frontend.ressources.RequestResources;
import de.ecw.zabos.sql.resource.DBResource;

public class AjaxController extends BaseControllerAdapter
{
    // Logger-Instanz
    private static final Logger log = Logger.getLogger(AjaxController.class);

    /**
     * Veröffentlichte AJAX-Methoden Route -> Methode
     */
    private final Map<String, IAjaxMethod> mapPublishedAjaxMethods = new HashMap<String, IAjaxMethod>();

    /**
     * Liste mit den veröffentlichten AJAX-Methoden
     */
    private List<IAjaxMethod> listAjaxMethods = new ArrayList<IAjaxMethod>();

    private boolean logJsonCommunication = true;

    /**
     * Konstruktor
     * 
     * @param dbRessource
     */
    public AjaxController(final DBResource dbRessource)
    {
        super(dbRessource);
        setImplicitForward(false);
    }

    @Override
    public boolean processACL(final RequestResources req)
    {
        if (!req.getUserBean().isLoggedIn())
        {
            sendOutput(req,
                            buildErrorObject("Sie müssen eingeloggt sein, um die AJAX-Funktionalitäten nutzen zu können"));

            return false;
        }

        return true;
    }

    @Override
    public void run(final RequestResources req)
    {
        JSONObject r = null;

        try
        {
            final String methodCall = this.getStringForParam(req, "method");
            final String inData = this.getStringForParam(req, "data");
            final JSONObject jsonObject = new JSONObject(
                            new JSONTokener(inData));

            if (isLogJsonCommunication())
            {
                log.debug(buildLogMessage(req, "Eingehender AJAX-Request: "
                                + methodCall));
            }

            if (!mapPublishedAjaxMethods.containsKey(methodCall))
            {
                throw new StdException("Die Methode " + methodCall
                                + " existiert nicht");
            }

            r = mapPublishedAjaxMethods.get(methodCall).run(req, jsonObject);
        }
        catch (Exception e)
        {
            r = buildErrorObject(e.getMessage());
        }

        sendOutput(req, r);
    }

    /**
     * Setzt die veröffentlichten Methoden
     * 
     * @param _alMethods
     */
    public void setMethods(final List<IAjaxMethod> _alMethods)
    {
        listAjaxMethods = _alMethods;

        for (int i = 0, m = listAjaxMethods.size(); i < m; i++)
        {
            IAjaxMethod iam = listAjaxMethods.get(i);
            String methodName = iam.getClass().getSimpleName();

            log.info("AJAX-Methode " + methodName + " registriert");
            mapPublishedAjaxMethods.put(methodName, iam);
        }
    }

    /**
     * Liefert die veröffentlichten Methoden
     * 
     * @return
     */
    public List<IAjaxMethod> getMethods()
    {
        return listAjaxMethods;
    }

    /**
     * Sendet den erzeugten Output an den Client
     * 
     * @param req
     * @param _json
     */
    private void sendOutput(final RequestResources req, final JSONObject _json)
    {
        try
        {
            String sJsonOutput = _json.toString();

            if (isLogJsonCommunication())
            {
                log.debug(buildLogMessage(req, "AJAX-Antwort: " + sJsonOutput));
            }

            req.getServletResponse().setCharacterEncoding("UTF-8");
            // Content-Type MUSS unbedingt auf text/plain bleiben, da ansonsten
            // Opera
            // Probleme beim Encoden der Daten bekommt
            req.getServletResponse().setContentType("text/plain");
            req.getServletResponse().getWriter().print(sJsonOutput);
        }
        catch (IOException e)
        {
            log.error(buildLogMessage(req, "Beim Senden des Reponse-Strings "
                            + _json.toString()
                            + " trat folgende Exception auf: " + e.getMessage()));
        }
    }

    /**
     * Erzeugt ein Error-Objekt
     * 
     * @param _error
     * @return
     */
    private JSONObject buildErrorObject(String _error)
    {
        final JSONObject r = new JSONObject();

        try
        {
            r.put("error", _error);
        }
        catch (JSONException e)
        {
        }

        return r;
    }

    /**
     * Liefert den Parameter als String. Ist der Parameter nicht gesetzt, wird
     * ein leerer String zurückgeliefert
     * 
     * @param _param
     * @return Wert des Parameters oder ""
     * @since 2006-07-03 CKL: Zuerst muss der String urldecoded werden!
     */
    protected String getStringForParam(final RequestResources req, String _param)
    {
        if (req.getServletRequest().getParameter(_param) != null)
        {
            try
            {
                String unencoded = req.getServletRequest().getParameter(_param);
                /*
                 * Folgende beiden Varianten funktionieren *nicht* String
                 * rString = URLDecoder.decode(unencoded, "utf-8"); String
                 * rString = new String(unencoded.getBytes(), "utf-8"); Damit
                 * das Encoding korrekt funktioniert, *muss* in der server.xml
                 * von Tomcat der Eintrag <Connector ...
                 * useBodyEncodingForURI="true" URIEncoding="UTF-8" /> gesetzt
                 * sein!
                 */
                return unencoded;
            }
            catch (Exception e)
            {
                log.error(buildLogMessage(
                                req,
                                "Fehler beim Parsen des Parameters: "
                                                + e.getMessage()));
            }
        }

        return "";
    }

    /**
     * Sobald die Richtlinien nicht durchgesetzt worden sind, soll nichts weiter
     * passieren, standardmäßig wird auf die Login-Seite weitergeleitet
     */
    public void onProcessACLFailed(IBaseController controller,
                    FrontendDispatcher frontendDispatcher, RequestResources req)
    {
        log.error(buildLogMessage(req,
                        "Benutzer, der die AJAX-Methoden aufruft, ist nicht eingeloggt"));
    }

    public void setLogJsonCommunication(boolean logJsonCommunication)
    {
        this.logJsonCommunication = logJsonCommunication;
    }

    public boolean isLogJsonCommunication()
    {
        return logJsonCommunication;
    }
}
