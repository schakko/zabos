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
import de.ecw.zabos.sql.vo.OrganisationsEinheitVO;
import de.ecw.zabos.types.id.OrganisationId;

/**
 * Liefert die Organisationenseinheiten einer Organisation zurueck<br>
 * 
 * <table border="1">
 * <tr>
 * <td> {@link JsonConstants#ARR_ORGANISATIONSEINHEITEN}</td>
 * <td>Für jedes Element im Array:
 * <table>
 * <tr>
 * <td> {@link JsonConstants#NAME}</td>
 * <td> {@link OrganisationsEinheitVO#getName()}</td>
 * </tr>
 * <tr>
 * <td> {@link JsonConstants#ID}</td>
 * <td> {@link OrganisationsEinheitVO#getOrganisationId()}</td>
 * </tr>
 * </table>
 * </td>
 * </tr>
 * </table>
 * 
 * @return JSON-Objekt
 */
public class FindOrganisationseinheitenInOrganisation extends
                AbstractAjaxMethodAdapter
{
    private final static Logger log = Logger
                    .getLogger(FindOrganisationseinheitenInOrganisation.class);

    public FindOrganisationseinheitenInOrganisation(final DBResource db)
    {
        super(db);
    }

    public JSONObject run(RequestResources req, JSONObject jsonRequest) throws JSONException, StdException
    {
        JSONObject returnObject = new JSONObject();
        JSONArray jsonArrOrganisationseinheiten = new JSONArray();
        JSONObject jsonTempObject = null;
        OrganisationsEinheitVO[] oesVO = null;

        // ID der Organisation
        int idOrganisation = 0;

        // Wurde die Alarm-Id als Parameter uebergeben?
        if (jsonRequest == null)
        {
            throw new StdException(
                            "Fuer die Methode muessen Parameter gesetzt sein.");
        }

        // Den Parameter AlarmId zu einem int umwandeln
        try
        {
            if ((idOrganisation = jsonRequest.getInt("OrganisationId")) != 0)
            {
                log.debug("Parameter OrganisationId ist " + idOrganisation);
            }
            else
            {
                log.debug("Keine OrganisationId gesetzt, lade alle OEs.");
            }
        }
        catch (JSONException e)
        {
            throw new StdException(
                            "Der Parameter OrganisationId wurde nicht gesetzt.");
        }

        try
        {
            // Alle OEs laden bzw. nur die OEs laden, deren Organisation
            // angegeben
            // wurde
            if (idOrganisation != 0)
            {
                oesVO = daoOrganisationseinheit
                                .findOrganisationsEinheitenByOrganisationId(new OrganisationId(
                                                idOrganisation));
            }
            else
            {
                oesVO = daoOrganisationseinheit.findAll();
            }

            if (oesVO != null)
            {
                for (int i = 0, m = oesVO.length; i < m; i++)
                {
                    jsonTempObject = new JSONObject();

                    try
                    {
                        jsonTempObject.put(JsonConstants.NAME, oesVO[i]
                                        .getName());
                        jsonTempObject.put(JsonConstants.ID, oesVO[i]
                                        .getOrganisationsEinheitId());
                        jsonArrOrganisationseinheiten.put(jsonTempObject);
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
                                "Die Organisationseinheiten der Organisation mit der Id "
                                                + idOrganisation
                                                + " konnte nicht gefunden werden.");
            }
        }
        catch (StdException e)
        {
            log.error(e);
        }

        try
        {
            returnObject.put(JsonConstants.ARR_ORGANISATIONSEINHEITEN,
                            jsonArrOrganisationseinheiten);
        }
        catch (JSONException e)
        {
            log.error(e);
        }

        return returnObject;
    }
}
