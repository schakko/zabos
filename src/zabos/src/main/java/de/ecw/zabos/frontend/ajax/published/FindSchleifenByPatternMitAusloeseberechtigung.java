package de.ecw.zabos.frontend.ajax.published;

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

/**
 * Liefert die Schleifen nach dem angegebenen Pattern zurueck in der der
 * Benutzer ausloeseberechtigt ist.<br>
 * 
 * <pre>
 * 
 *  #json-param pattern
 *  #json-return 
 *    array arrSchleifen
 *      szName Name der Schleife
 *      iId     ID der Schleife
 * 
 * </pre>
 * 
 * @return JSON-Objekt
 */
public class FindSchleifenByPatternMitAusloeseberechtigung extends
                AbstractAjaxMethodAdapter
{
    private final static Logger log = Logger
                    .getLogger(FindSchleifenByPatternMitAusloeseberechtigung.class);

    public FindSchleifenByPatternMitAusloeseberechtigung(final DBResource db)
    {
        super(db);
    }

    public JSONObject run(RequestResources req, JSONObject jsonRequest) throws JSONException, StdException
    {
        JSONObject r = new JSONObject();
        JSONArray jsonArrSchleifen = new JSONArray();
        SchleifeVO[] voSchleifen = null;
        String szPattern = "";

        // Wurde die Alarm-Id als Parameter uebergeben?
        if (jsonRequest == null)
        {
            throw new StdException(
                            "fuer die Methode muessen Parameter gesetzt sein.");
        }

        // Den Parameter szPattern zu einem String umwandeln
        try
        {
            szPattern = jsonRequest.getString("szPattern");
            szPattern = szPattern.replace("*", ".*");
            szPattern = ".*" + szPattern + ".*";
        }
        catch (JSONException e)
        {
            throw new StdException(
                            "Der Parameter OrganisationseinheitId wurde nicht gesetzt.");
        }

        try
        {
            voSchleifen = daoSchleife.findSchleifenByPattern(szPattern,
                            szPattern, szPattern);

            if (voSchleifen != null)
            {
                for (int i = 0, m = voSchleifen.length; i < m; i++)
                {
                    if (daoPerson.hatPersonRechtInSchleife(req.getUserBean()
                                    .getPerson().getPersonId(),
                                    RechtId.ALARM_AUSLOESEN, voSchleifen[i]
                                                    .getSchleifeId()))
                    {
                        try
                        {
                            jsonArrSchleifen
                                            .put(convertSchleifeVO(voSchleifen[i]));
                        }
                        catch (JSONException e)
                        {
                            log.error(e);
                        }
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
