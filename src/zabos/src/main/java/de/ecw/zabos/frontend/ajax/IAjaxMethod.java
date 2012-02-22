package de.ecw.zabos.frontend.ajax;

import org.json.JSONException;
import org.json.JSONObject;

import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.frontend.ressources.RequestResources;

/**
 * Interface zum Erzeugen einer JSON-Antwort
 * 
 * @author ckl
 * 
 */
public interface IAjaxMethod
{
    /**
     * Führt die JSON-Methode im Servlet-Container aus
     * 
     * @param req
     *            HttpRequest
     * @param jsonRequest
     *            JSON-Anfrage aus JavaScript
     * @return
     * @throws JSONException
     * @throws StdException
     */
    public JSONObject run(RequestResources req, JSONObject jsonRequest) throws JSONException, StdException;
}
