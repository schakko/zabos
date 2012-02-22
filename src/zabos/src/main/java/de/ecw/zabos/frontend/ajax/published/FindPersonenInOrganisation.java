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
import de.ecw.zabos.sql.vo.PersonVO;
import de.ecw.zabos.types.id.OrganisationId;

/**
 * Liefert die Personen in einer Organisation zurueck<br>
 * 
 * <table border="1">
 * <tr>
 * <td> {@link JsonConstants#ARR_PERSONEN}</td>
 * <td>Array mit Objekten, die über
 * {@link AbstractAjaxMethodAdapter#convertPersonVO(PersonVO[])} konvertiert
 * wurde</td>
 * </tr>
 * </table>
 * 
 * @return JSON-Objekt
 */
public class FindPersonenInOrganisation extends AbstractAjaxMethodAdapter
{
    private final static Logger log = Logger
                    .getLogger(FindPersonenInOrganisation.class);

    public FindPersonenInOrganisation(final DBResource db)
    {
        super(db);
    }

    public JSONObject run(RequestResources req, JSONObject jsonRequest) throws JSONException, StdException
    {
        JSONObject r = new JSONObject();
        // Enthaelt die gefunden Personen
        JSONArray jsonArrPersonen = new JSONArray();
        // Die gefundenen Personen
        PersonVO[] personenVO = null;
        // Id
        int iOrganisationId = 0;
        // Id des Organisation
        OrganisationId organisationId = null;

        // Wurde die Alarm-Id als Parameter uebergeben?
        if (jsonRequest == null)
        {
            throw new StdException(
                            "fuer die Methode muessen Parameter gesetzt sein.");
        }

        // Den Parameter szPattern zu einem String umwandeln
        try
        {
            iOrganisationId = jsonRequest.getInt("OrganisationId");

            if (iOrganisationId != 0)
            {
                organisationId = new OrganisationId(iOrganisationId);
            }
        }
        catch (JSONException e)
        {
            throw new StdException(
                            "Der Parameter OrganisationId wurde nicht gesetzt.");
        }

        try
        {
            personenVO = daoPerson.findPersonenInOrganisation(organisationId);
            jsonArrPersonen = this.convertPersonVO(personenVO);
        }
        catch (StdException e)
        {
            log.error(e);
        }

        try
        {
            r.put(JsonConstants.ARR_PERSONEN, jsonArrPersonen);
        }
        catch (JSONException e)
        {
            log.error(e);
        }

        return r;
    }
}
