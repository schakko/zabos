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
import de.ecw.zabos.sql.vo.AlarmVO;
import de.ecw.zabos.sql.vo.SchleifeVO;
import de.ecw.zabos.sql.vo.SystemKonfigurationVO;
import de.ecw.zabos.types.id.RechtId;

/**
 * Liefert die letzten Alarmierungen zurück, in der ein Benutzer das Recht hat,
 * die Details des Alarms sehen zu dürfen
 * 
 * <table border="1">
 * <tr>
 * <td> {@link JsonConstants#ARR_ALARMIERUNGEN}</td>
 * <td>Für jeden Alarm wird ein Objekt über
 * {@link #convertAlarmVO(AlarmVO)} erzeugt</td>
 * </tr>
 * </table>
 * 
 * @return JSON-Objekt
 * @author ckl
 */
public class FindLetzteAlarmierungenMitBerechtigung extends
                AbstractAjaxMethodAdapter
{
    private final static Logger log = Logger
                    .getLogger(FindLetzteAlarmierungenMitBerechtigung.class);

    public FindLetzteAlarmierungenMitBerechtigung(final DBResource db)
    {
        super(db);
    }

    public JSONObject run(RequestResources req, JSONObject jsonRequest) throws JSONException, StdException
    {
        JSONObject r = new JSONObject();
        JSONArray jsonArrAlarmierungen = new JSONArray();
        AlarmVO[] alarmeVO = null;
        SchleifeVO[] schleifenInAlarmVO = null;

        // Systemkonfiguration
        SystemKonfigurationVO systemKonfigurationVO = null;

        // Die Daten fuer den Alarm zusammen sammeln
        try
        {
            systemKonfigurationVO = daoSystemkonfiguration.readKonfiguration();

            boolean istHistorieGefuellt = false;
            int gefundeneAlarme = 0;
            int idx = 0;
            int alarmeProAbfrage = 20;

            while (!istHistorieGefuellt)
            {
                // Alarme finden
                alarmeVO = daoAlarm.findAlarmeByOffset(alarmeProAbfrage, idx);

                if (alarmeVO != null)
                {
                    if (alarmeVO.length > 0)
                    {
                        // Alarme durchlaufen
                        for (int i = 0, m = alarmeVO.length; i < m; i++)
                        {
                            schleifenInAlarmVO = daoSchleife
                                            .findSchleifenByAlarmId(alarmeVO[i]
                                                            .getAlarmId());

                            if (schleifenInAlarmVO != null)
                            {
                                // Schleifen eines Alarms ueberpruefen
                                for (int j = 0, n = schleifenInAlarmVO.length; j < n; j++)
                                {
                                    // Person hat das Recht, in dieser Schleife
                                    // mindestens einen
                                    // Alarm sehen zu duerfen
                                    if (daoPerson
                                                    .hatPersonRechtInSchleife(
                                                                    req
                                                                                    .getUserBean()
                                                                                    .getPerson()
                                                                                    .getPersonId(),
                                                                    RechtId.ALARMHISTORIE_DETAILS_SEHEN,
                                                                    schleifenInAlarmVO[j]
                                                                                    .getSchleifeId()))
                                    {
                                        jsonArrAlarmierungen
                                                        .put(convertAlarmVO(alarmeVO[i]));
                                        gefundeneAlarme++;
                                        break;
                                    } // if Person hat Recht
                                } // for schleifenInAlarm
                            } // schleifenInAlarm != null

                            if (gefundeneAlarme == systemKonfigurationVO
                                            .getAlarmHistorieLaenge())
                            {
                                istHistorieGefuellt = true;
                                break;
                            }

                        } // for alarmVO
                    }
                    // Es existieren keine Alarme mehr
                    else
                    {
                        istHistorieGefuellt = true;
                    }
                }
                // Keine Alarme
                else
                {
                    istHistorieGefuellt = true;
                }

                idx += alarmeProAbfrage;
            }

        }
        catch (StdException e)
        {
            log.error(e);
        }

        try
        {
            r.put(JsonConstants.ARR_ALARMIERUNGEN, jsonArrAlarmierungen);
        }
        catch (JSONException e)
        {
            log.error(e);
        }

        return r;
    }
}
