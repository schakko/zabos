package de.ecw.zabos.frontend.ajax.published;

import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.frontend.ajax.AbstractAjaxMethodAdapter;
import de.ecw.zabos.frontend.ajax.JsonConstants;
import de.ecw.zabos.frontend.ressources.RequestResources;
import de.ecw.zabos.sql.resource.DBResource;
import de.ecw.zabos.sql.vo.SchleifeVO;
import de.ecw.zabos.types.id.RechtId;
import de.ecw.zabos.types.id.SchleifeId;

/**
 * Liefert die Schleifen im System zurueck, in der der Benutzer
 * ausloeseberechtigt ist<br>
 * 
 * <table border="1">
 * <tr>
 * <td> {@link JsonConstants#ARR_SCHLEIFEN}</td>
 * <td>Für jede Schleife wird ein Objekt über
 * {@link #convertSchleifeVO(SchleifeVO)} erstellt</td>
 * </tr>
 * </table>
 * 
 * @return JSON-Objekt
 */
public class FindSchleifenInOrganisationMitAusloeseberechtigung extends
                AbstractAjaxMethodAdapter
{
    private final static Logger log = Logger
                    .getLogger(FindSchleifenInOrganisationMitAusloeseberechtigung.class);

    public FindSchleifenInOrganisationMitAusloeseberechtigung(
                    final DBResource db)
    {
        super(db);
    }

    public JSONObject run(RequestResources req, JSONObject jsonRequest) throws JSONException, StdException
    {
        JSONObject r = new JSONObject();
        JSONArray jsonArrSchleifen = new JSONArray();
        Map<String, String> mapSchleifen = null;

        try
        {
            mapSchleifen = daoSchleife
                            .findSchleifenByPersonAndRechtForJSAsHashMap(req
                                            .getUserBean().getPerson()
                                            .getPersonId(),
                                            RechtId.ALARM_AUSLOESEN);

            if (mapSchleifen != null)
            {
                Iterator<String> it = mapSchleifen.keySet().iterator();

                while (it.hasNext())
                {
                    String key = it.next();

                    try
                    {
                        long id = 0;

                        try
                        {
                            id = Long.valueOf(key);
                        }
                        catch (NumberFormatException e)
                        {
                            log.error(e);
                        }

                        SchleifeVO voSchleife = daoSchleife
                                        .findSchleifeById(new SchleifeId(id));

                        if (voSchleife != null)
                        {
                            jsonArrSchleifen.put(convertSchleifeVO(voSchleife));
                        }
                    }
                    catch (JSONException e)
                    {
                        log.error(e);
                    }
                }
            }
        }
        catch (StdException e)
        {
            log.error(e);
        }

        try
        {
            sortJSONArraySchleifenByDisplayName(jsonArrSchleifen); // 2006-06-08
            // CST:
            // Issue #292 -
            // Sortierung
            r.put(JsonConstants.ARR_SCHLEIFEN, jsonArrSchleifen);
        }
        catch (JSONException e)
        {
            log.error(e);
        }

        return r;
    }
}
