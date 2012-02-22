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

/**
 * Liefert die Personen nach dem angegebenem Pattern-Muster zurück<br>
 * 
 * <table border="1">
 * <tr>
 * <td> {@link JsonConstants#ARR_PERSONEN}</td>
 * <td>Array mit Objekten, die über
 * {@link AbstractAjaxMethodAdapter#convertPersonVOToExtended(PersonVO)}
 * konvertiert wurde</td>
 * </tr>
 * </table>
 * 
 * 
 * @return JSON-Objekt
 */
public class FindPersonByPattern extends AbstractAjaxMethodAdapter
{
    private final static Logger log = Logger
                    .getLogger(FindPersonByPattern.class);

    public FindPersonByPattern(final DBResource db)
    {
        super(db);
    }

    public JSONObject run(RequestResources req, JSONObject jsonRequest) throws JSONException, StdException
    {
        JSONObject r = new JSONObject();
        JSONArray jsonArrPersonen = new JSONArray();
        PersonVO[] personenVO = null;

        // Suchmuster
        String szPattern = "";

        // Wurde das Suchmuster als Parameter übergeben?
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
            personenVO = daoPerson.findPersonenByPattern(szPattern, szPattern,
                            szPattern, szPattern);
            PersonVO.sortPersonenByNachnameVorname(personenVO);

            if (personenVO != null)
            {
                for (int i = 0, m = personenVO.length; i < m; i++)
                {
                    try
                    {
                        jsonArrPersonen
                                        .put(convertPersonVOToExtended(personenVO[i]));
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
            r.put(JsonConstants.ARR_PERSONEN, jsonArrPersonen);
        }
        catch (JSONException e)
        {
            log.error(e);
        }

        return r;
    }
}
