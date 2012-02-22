package de.ecw.zabos.frontend.ajax.published;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.frontend.ajax.AbstractAjaxMethodAdapter;
import de.ecw.zabos.frontend.ajax.JsonConstants;
import de.ecw.zabos.frontend.ressources.RequestResources;
import de.ecw.zabos.sql.dao.BereichInSchleifeDAO;
import de.ecw.zabos.sql.resource.DBResource;
import de.ecw.zabos.sql.tao.BenutzerVerwaltungTAO;
import de.ecw.zabos.types.id.BereichId;
import de.ecw.zabos.types.id.FunktionstraegerId;

/**
 * Setzt die anzuzeigenden Funktionsträger-/Bereichs-Kombinationen.<br />
 * <ul>
 * <li> {@link JsonConstants#ARR_REPORT_OPTIONEN} als Array; Jedes Element des
 * Arrays muss in der Kombination $FunktionstraegerId
 * {@link FindFunktionstraegerMitBereichen#MAPPING_SEPERATOR}$BereichId
 * vorliegen.
 * </ul>
 * 
 * @return JSON-Objekt
 */
public class SetReportOptionen extends AbstractAjaxMethodAdapter
{
    private final static Logger log = Logger.getLogger(SetReportOptionen.class);

    public SetReportOptionen(final DBResource db)
    {
        super(db);
    }

    public JSONObject run(RequestResources req, JSONObject jsonRequest) throws JSONException, StdException
    {
        BereichInSchleifeDAO bereichInSchleifeDAO = db.getDaoFactory()
                        .getBereichInSchleifeDAO();
        BenutzerVerwaltungTAO taoBV = db.getTaoFactory()
                        .getBenutzerVerwaltungTAO();

        JSONObject returnObject = new JSONObject();

        if (jsonRequest == null)
        {
            throw new StdException(
                            "fuer die Methode muessen Parameter gesetzt sein.");
        }

        try
        {
            JSONArray arrReportOptionen = jsonRequest
                            .getJSONArray(JsonConstants.ARR_REPORT_OPTIONEN);
            req.getUserBean().getPerson().getReportOptionen().clear();

            for (int i = 0, m = arrReportOptionen.length(); i < m; i++)
            {
                String mapping = arrReportOptionen.getString(i);
                String funktionstraegerVal = mapping
                                .substring(
                                                0,
                                                mapping
                                                                .indexOf(FindFunktionstraegerMitBereichen.MAPPING_SEPERATOR));
                String bereichVal = mapping
                                .substring((mapping
                                                .indexOf(FindFunktionstraegerMitBereichen.MAPPING_SEPERATOR) + 1));

                try
                {
                    FunktionstraegerId fId = new FunktionstraegerId(Long
                                    .parseLong(funktionstraegerVal));
                    BereichId bId = new BereichId(Long.parseLong(bereichVal));

                    if (bereichInSchleifeDAO
                                    .istBereichFunktionstraegerZuordnungExistent(
                                                    fId, bId))
                    {
                        {
                            req.getUserBean().getPerson().getReportOptionen()
                                            .put(mapping, mapping);
                        }
                    }
                }
                catch (NumberFormatException e)
                {
                    log
                                    .error("Konnte Funktionstraeger/Bereich-Mapping nicht nach Long konvertieren: "
                                                    + e.getMessage());
                }
            }

            taoBV.updatePerson(req.getUserBean().getPerson());
        }
        catch (StdException e)
        {
            log.error(e);
        }

        returnObject.put(JsonConstants.STATUS, 1);
        return returnObject;
    }
}
