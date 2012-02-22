package de.ecw.zabos.frontend.ajax.published;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.frontend.ajax.AbstractAjaxMethodAdapter;
import de.ecw.zabos.frontend.ajax.JsonConstants;
import de.ecw.zabos.frontend.ressources.RequestResources;
import de.ecw.zabos.frontend.sql.ho.PersonHO;
import de.ecw.zabos.sql.resource.DBResource;
import de.ecw.zabos.sql.vo.PersonVO;
import de.ecw.zabos.types.id.PersonId;
import de.ecw.zabos.types.id.RechtId;

/**
 * Liefert die Person mit der angegebenen Id zurueck.<br>
 * 
 * <table border="1">
 * <tr>
 * <td> {@link JsonConstants#PERSON}</td>
 * <td>Objekt, das über
 * {@link AbstractAjaxMethodAdapter#convertPersonVOToExtended(PersonVO)}
 * konvertiert wurde</td>
 * </tr>
 * </table>
 * 
 * @return JSON-Objekt
 */
public class FindPersonById extends AbstractAjaxMethodAdapter
{
    private final static Logger log = Logger.getLogger(FindPersonById.class);

    PersonHO personHO = null;

    public FindPersonById(final DBResource db)
    {
        super(db);
        personHO = new PersonHO(db);
    }

    public JSONObject run(RequestResources req, JSONObject jsonRequest) throws JSONException, StdException
    {
        JSONObject r = new JSONObject();
        PersonVO personVO = null;
        JSONObject jsonTempObject = null;
        // Id des Users
        PersonId personId = null;
        // Id
        int iIdUser = 0;

        // Wurde die Alarm-Id als Parameter übergeben?
        if (jsonRequest == null)
        {
            throw new StdException(
                            "Fuer die Methode muessen Parameter gesetzt sein.");
        }

        // Den Parameter szPattern zu einem String umwandeln
        try
        {
            iIdUser = jsonRequest.getInt("iIdUser");

            if (iIdUser != 0)
            {
                personId = new PersonId(iIdUser);
            }
        }
        catch (JSONException e)
        {
            throw new StdException("Der Parameter iIdUser wurde nicht gesetzt.");
        }

        try
        {
            personVO = daoPerson.findPersonById(personId);

            if (personVO != null)
            {
                if (personHO.isRechtInBezugAufAnderePersonVerfuegbar(req
                                .getUserBean().getPerson().getPersonId(),
                                RechtId.PERSON_AENDERN, personVO.getPersonId()))
                {

                    jsonTempObject = convertPersonVOToExtended(personVO);
                }
                else
                {
                    r
                                    .put(JsonConstants.ERROR,
                                                    "Sie besitzen nicht das Recht, diese Person zu ändern.");
                }
            }
        }
        catch (StdException e)
        {
            log.error(e);
        }

        try
        {
            r.put(JsonConstants.PERSON, jsonTempObject);
        }
        catch (JSONException e)
        {
            log.error(e);
        }

        return r;
    }
}
