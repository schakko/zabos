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
import de.ecw.zabos.types.id.OrganisationsEinheitId;

/**
 * Liefert die Personen in einer Organisationseinheit zurueck<br>
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
public class FindPersonenInOrganisationseinheit extends
                AbstractAjaxMethodAdapter
{
    private final static Logger log = Logger
                    .getLogger(FindPersonenInOrganisationseinheit.class);

    public FindPersonenInOrganisationseinheit(final DBResource db)
    {
        super(db);
    }

    public JSONObject run(RequestResources req, JSONObject jsonRequest) throws JSONException, StdException
    {
        JSONObject r = new JSONObject();
        JSONArray jsonArrPersonen = new JSONArray();
        PersonVO[] voPersonen = null;
        int iOrganisationseinheitId = 0;
        OrganisationsEinheitId organisationseinheitId = null;

        // Wurde die Alarm-Id als Parameter uebergeben?
        if (jsonRequest == null)
        {
            throw new StdException(
                            "Fuer die Methode muessen Parameter gesetzt sein.");
        }

        // Den Parameter szPattern zu einem String umwandeln
        try
        {
            iOrganisationseinheitId = jsonRequest
                            .getInt("OrganisationseinheitId");

            if (iOrganisationseinheitId != 0)
            {
                organisationseinheitId = new OrganisationsEinheitId(
                                iOrganisationseinheitId);
            }
        }
        catch (JSONException e)
        {
            throw new StdException(
                            "Der Parameter OrganisationseinheitId wurde nicht gesetzt.");
        }

        try
        {
            voPersonen = daoPerson
                            .findPersonenInOrganisationseinheit(organisationseinheitId);
            jsonArrPersonen = this.convertPersonVO(voPersonen);
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
