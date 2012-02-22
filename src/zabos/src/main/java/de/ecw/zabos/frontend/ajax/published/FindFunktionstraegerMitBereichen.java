package de.ecw.zabos.frontend.ajax.published;

import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.frontend.ajax.AbstractAjaxMethodAdapter;
import de.ecw.zabos.frontend.ajax.JsonConstants;
import de.ecw.zabos.frontend.ressources.RequestResources;
import de.ecw.zabos.sql.resource.DBResource;
import de.ecw.zabos.sql.vo.BereichVO;
import de.ecw.zabos.sql.vo.FunktionstraegerVO;
import de.ecw.zabos.sql.vo.PersonVO;
import de.ecw.zabos.types.id.BereichId;
import de.ecw.zabos.types.id.FunktionstraegerId;

/**
 * <table border="1">
 * <tr>
 * <td> {@link JsonConstants#ARR_FUNKTIONSTRAEGER}</td>
 * <td>Array mit den Funktionsträgern des Alarms:
 * <table border="1">
 * <tr>
 * <td colspan="2">Daten, die von
 * {@link #convertFunktionstreagerVO(FunktionstraegerVO)} konvertiert wurden</td>
 * </tr>
 * <tr>
 * <td> {@link JsonConstants#ARR_BEREICH}</td>
 * <td>Array mit den alarmierten Bereichen:
 * <table border="1">
 * <tr>
 * <td colspan="2">Daten die von
 * {@link #convertBereichVO(de.ecw.zabos.sql.vo.BereichVO)} konvertiert wurden und außerdem 
 * <ul><li>{@link JsonConstants#IST_SICHTBAR} ob der Benutzer diesen Bereich in seinen Optionen sichtbar hat</li>
 * </ul>
 * </td>
 * </tr>
 * </table>
 * </td>
 * </tr>
 * </table>
 * </td>
 * </tr>
 * </table>
 * 
 * @return JSON-Objekt
 */
public class FindFunktionstraegerMitBereichen extends AbstractAjaxMethodAdapter
{
    public final static String MAPPING_SEPERATOR = "_";

    public FindFunktionstraegerMitBereichen(final DBResource db)
    {
        super(db);
    }

    public JSONObject run(RequestResources req, JSONObject jsonRequest) throws JSONException, StdException
    {
        // Das JSON-Objekt, das zurueckgegeben wird
        JSONObject r = null;

        r = new JSONObject();

        try
        {
            FunktionstraegerVO[] funktionstraegerVO = daoFunktionstraeger
                            .findFunktionstraegerMitAktiverBereichZuordnung();

            JSONArray jsonArrFunktionstraeger = new JSONArray();

            for (int i = 0, m = funktionstraegerVO.length; i < m; i++)
            {
                FunktionstraegerVO funktionstraeger = funktionstraegerVO[i];
                JSONObject jsonObjFunktionstraeger = convertFunktionstreagerVO(funktionstraeger);

                BereichVO[] bereiche = daoBereich
                                .findBereicheMitAktiverFunktionstraegerZuordnung(funktionstraeger
                                                .getFunktionstraegerId());

                JSONArray jsonArrBereiche = new JSONArray();

                for (int j = 0, n = bereiche.length; j < n; j++)
                {
                    BereichVO bereichVO = bereiche[j];
                    JSONObject jsonObjBereich = convertBereichVO(bereichVO);

                    jsonObjBereich
                                    .put(
                                                    JsonConstants.IST_SICHTBAR,
                                                    isBereichFunktionstraegerSichtbar(
                                                                    req,
                                                                    funktionstraeger
                                                                                    .getFunktionstraegerId(),
                                                                    bereichVO
                                                                                    .getBereichId()));
                    jsonArrBereiche.put(jsonObjBereich);
                }

                jsonObjFunktionstraeger.put(JsonConstants.ARR_BEREICH,
                                jsonArrBereiche);
                jsonArrFunktionstraeger.put(jsonObjFunktionstraeger);
            }

            r.put(JsonConstants.ARR_FUNKTIONSTRAEGER, jsonArrFunktionstraeger);
        }
        catch (StdException e)
        {
            throw new StdException(
                            "Beim Laden der Personen trat folgender Fehler auf: "
                                            + e.getMessage());
        }
        catch (JSONException e)
        {
            throw new StdException(
                            "Beim Erstellen des JSON-Objekts trat folgender Fehler auf: "
                                            + e.getMessage());
        }

        return r;
    }

    /**
     * Erzeugt eine Kombination aus den IDs beider Objekte
     * 
     * @param _funktionstraegerVO
     * @param _bereichVO
     * @return
     */
    public static String buildBereichFunktionstraegerMapping(
                    FunktionstraegerId _funktionstraegerId, BereichId _bereichId)
    {
        StringBuffer sb = new StringBuffer();
        sb.append(_funktionstraegerId);
        sb.append(MAPPING_SEPERATOR);
        sb.append(_bereichId);

        return sb.toString();
    }

    /**
     * Liefert zurück, ob die Funktionsträger-Bereich-Kombination angezeigt
     * werden soll. Wurden keine Report-Optionen definiert, wird automatisch
     * true zurückgeliefert. Wenn der die Bereichs-/Funktionsträger-Kombination
     * in {@link PersonVO#getReportOptionen()} definiert ist, wird true
     * zurückgeliefert.
     * 
     * @param req
     * @param _bereichId
     * @param _funktionstraegerId
     * @return
     */
    public static boolean isBereichFunktionstraegerSichtbar(
                    RequestResources req,
                    FunktionstraegerId _funktionstraegerId, BereichId _bereichId)
    {
        Map<String, String> mapReportOptionen = req.getUserBean().getPerson()
                        .getReportOptionen();

        if (mapReportOptionen != null && mapReportOptionen.size() > 0)
        {
            return mapReportOptionen
                            .containsKey(FindFunktionstraegerMitBereichen
                                            .buildBereichFunktionstraegerMapping(
                                                            _funktionstraegerId,
                                                            _bereichId));
        }

        return true;
    }
}
