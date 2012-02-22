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
import de.ecw.zabos.sql.vo.SystemKonfigurationVO;

/**
 * Liefert die letzten x Alarmierungen für die Alarm-Übersicht zurück.<br>
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
public class FindLetzteAlarmierungen extends AbstractAjaxMethodAdapter
{
    private final static Logger log = Logger.getLogger(FindLetzteAlarmierungen.class);

    public FindLetzteAlarmierungen(final DBResource db)
    {
        super(db);
    }

    public JSONObject run(RequestResources req, JSONObject jsonRequest) throws JSONException, StdException
    {
        JSONObject r = new JSONObject();
        JSONArray jsonArrAlarmierungen = new JSONArray();
        AlarmVO[] alarmeVO = null;
        SystemKonfigurationVO systemKonfigurationVO = null;

        try
        {
            systemKonfigurationVO = daoSystemkonfiguration.readKonfiguration();
            alarmeVO = daoAlarm.findAlarmeByLimit(systemKonfigurationVO
                            .getAlarmHistorieLaenge());

            if (alarmeVO != null)
            {
                for (int i = 0, m = alarmeVO.length; i < m; i++)
                {
                    jsonArrAlarmierungen
                                    .put(convertAlarmVO(alarmeVO[i]));
                }
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
