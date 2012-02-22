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
import de.ecw.zabos.types.id.OrganisationsEinheitId;
import de.ecw.zabos.types.id.RechtId;

/**
 * Liefert die Schleifen einer Organisationseinheit zurueck<br>
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
public class FindSchleifenInOrganisationseinheitMitAusloeseberechtigung extends
                AbstractAjaxMethodAdapter
{
    private final static Logger log = Logger
                    .getLogger(FindSchleifenInOrganisationseinheitMitAusloeseberechtigung.class);

    public FindSchleifenInOrganisationseinheitMitAusloeseberechtigung(
                    final DBResource db)
    {
        super(db);
    }

    public JSONObject run(RequestResources req, JSONObject jsonRequest) throws JSONException, StdException
    {
        JSONObject r = new JSONObject();
        JSONArray jsonArrSchleifen = new JSONArray();
        SchleifeVO[] schleifenVO = null;
        int idOrganisationseinheit = 0;

        // Wurde die Alarm-Id als Parameter uebergeben?
        if (jsonRequest == null)
        {
            throw new StdException(
                            "Für die Methode müssen Parameter gesetzt sein.");
        }

        // Den Parameter AlarmId zu einem int umwandeln
        try
        {
            if ((idOrganisationseinheit = jsonRequest
                            .getInt("OrganisationseinheitId")) == 0)
            {
                throw new StdException(
                                "Der Parameter OrganisationseinheitId ist leer.");
            }
            else
            {
                log.debug("Parameter OrganisationseinheitId ist "
                                + idOrganisationseinheit);
            }
        }
        catch (JSONException e)
        {
            throw new StdException(
                            "Der Parameter OrganisationseinheitId wurde nicht gesetzt.");
        }

        try
        {
            schleifenVO = daoSchleife
                            .findSchleifenByOrganisationsEinheitId(new OrganisationsEinheitId(
                                            idOrganisationseinheit));

            if (schleifenVO != null)
            {
                for (int i = 0, m = schleifenVO.length; i < m; i++)
                {
                    try
                    {
                        if (daoPerson.hatPersonRechtInSchleife(req
                                        .getUserBean().getPerson()
                                        .getPersonId(),
                                        RechtId.ALARM_AUSLOESEN, schleifenVO[i]
                                                        .getSchleifeId()))
                        {
                            jsonArrSchleifen
                                            .put(convertSchleifeVO(schleifenVO[i]));
                        }
                        else
                        {
                            log
                                            .debug("Person hat kein Recht die Schleife mit der Id "
                                                            + schleifenVO[i]
                                                                            .getSchleifeId()
                                                            + " auszuloesen.");
                        }
                    }
                    catch (JSONException e)
                    {
                        log.error(e);
                    }
                }
            }
            else
            {
                throw new StdException(
                                "Die Schleifen der Organisationseinheit mit der Id "
                                                + idOrganisationseinheit
                                                + " konnte nicht gefunden werden.");
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
