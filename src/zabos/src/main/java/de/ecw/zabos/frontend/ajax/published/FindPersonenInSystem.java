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
 * Liefert die Person mit der angegebenen Id zurueck.<br>
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
 * 
 * @return JSON-Objekt
 */
public class FindPersonenInSystem extends AbstractAjaxMethodAdapter
{
    private final static Logger log = Logger
                    .getLogger(FindPersonenInSystem.class);

    public FindPersonenInSystem(DBResource db)
    {
        super(db);
    }

    public JSONObject run(RequestResources req, JSONObject jsonRequest) throws JSONException, StdException
    {
        JSONObject r = new JSONObject();
        JSONArray jsonArrPersonen = new JSONArray();
        PersonVO[] personenVO = null;

        try
        {
            personenVO = daoPerson.findAll();
            jsonArrPersonen = convertPersonVO(personenVO);
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
