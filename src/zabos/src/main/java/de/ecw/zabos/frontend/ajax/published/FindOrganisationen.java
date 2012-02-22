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
import de.ecw.zabos.sql.vo.OrganisationVO;

/**
 * Liefert die Organisationen im System zurück<br>
 * 
 * <table border="1">
 * <tr>
 * <td> {@link JsonConstants#ARR_ORGANISATIONEN}</td>
 * <td>Für jedes Element im Array:
 * <table>
 * <tr>
 * <td> {@link JsonConstants#NAME}</td>
 * <td> {@link OrganisationVO#getName()}</td>
 * </tr>
 * <tr>
 * <td> {@link JsonConstants#ID}</td>
 * <td> {@link OrganisationVO#getOrganisationId()}</td>
 * </tr>
 * </table>
 * </td>
 * </tr>
 * </table>
 * 
 * @return JSON-Objekt
 */
public class FindOrganisationen extends AbstractAjaxMethodAdapter
{
    private final static Logger log = Logger.getLogger(FindOrganisationen.class);

    public FindOrganisationen(final DBResource db)
    {
        super(db);
    }

    public JSONObject run(RequestResources req, JSONObject jsonRequest) throws JSONException, StdException
    {
        JSONObject r = new JSONObject();
        JSONArray jsonArrOrganisationen = new JSONArray();
        JSONObject jsonTempObject = null;
        OrganisationVO[] organisationenVO = null;

        try
        {
            organisationenVO = daoOrganisation.findAll();

            if (organisationenVO != null)
            {
                for (int i = 0, m = organisationenVO.length; i < m; i++)
                {
                    jsonTempObject = new JSONObject();

                    try
                    {
                        jsonTempObject.put(JsonConstants.NAME,
                                        organisationenVO[i].getName());
                        jsonTempObject
                                        .put(
                                                        JsonConstants.ID,
                                                        organisationenVO[i]
                                                                        .getOrganisationId());
                        jsonArrOrganisationen.put(jsonTempObject);
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
            r.put(JsonConstants.ARR_ORGANISATIONEN, jsonArrOrganisationen);
        }
        catch (JSONException e)
        {
            log.error(e);
        }

        return r;
    }
}
