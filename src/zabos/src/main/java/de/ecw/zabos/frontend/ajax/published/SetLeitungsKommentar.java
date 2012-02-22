package de.ecw.zabos.frontend.ajax.published;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.frontend.ajax.AbstractAjaxMethodAdapter;
import de.ecw.zabos.frontend.ressources.RequestResources;
import de.ecw.zabos.sql.resource.DBResource;
import de.ecw.zabos.sql.vo.SchleifeVO;
import de.ecw.zabos.types.id.AlarmId;
import de.ecw.zabos.types.id.PersonId;
import de.ecw.zabos.types.id.RechtId;

/**
 * Setzt den Leitungs-Kommentar zu einer Person.<br />
 * <ul>
 * <li>Alle Parameter müssen gesetzt sein</li>
 * <li>Der eingeloggte Benutzer muss das Recht
 * {@link RechtId#LEITUNGS_KOMMENTAR_FESTLEGEN} innerhalb einer der alarmierten
 * Schleifen besitzen</li>
 * </ul>
 * Eingabe-Werte:
 * <ul>
 * <li>iPersonId (int) ID der Person</li>
 * <li>szLeitungsKommentar (String) Kommentar der Leitung</li>
 * <li>iAlarmId (int) ID des Alarms</li>
 * </ul>
 * 
 * @return JSON-Objekt
 */
public class SetLeitungsKommentar extends AbstractAjaxMethodAdapter
{
    private final static Logger log = Logger
                    .getLogger(SetLeitungsKommentar.class);

    public final static String PERSON_ID = "iPersonId";

    public final static String LEITUNGS_KOMMENTAR = "szLeitungsKommentar";

    public final static String ALARM_ID = "iAlarmId";

    public SetLeitungsKommentar(final DBResource db)
    {
        super(db);
    }

    public JSONObject run(RequestResources req, JSONObject jsonRequest) throws JSONException, StdException
    {
        JSONObject returnObject = new JSONObject();
        PersonId personId = null;
        AlarmId alarmId = null;
        String leitungsKommentar = "";

        if (jsonRequest == null)
        {
            throw new StdException(
                            "fuer die Methode muessen Parameter gesetzt sein.");
        }

        try
        {
            personId = new PersonId(jsonRequest.getLong(PERSON_ID));
            alarmId = new AlarmId(jsonRequest.getLong(ALARM_ID));
            leitungsKommentar = jsonRequest.getString(LEITUNGS_KOMMENTAR);
        }
        catch (JSONException e)
        {
            throw new StdException("Die Parameter " + PERSON_ID + ", "
                            + ALARM_ID + " und " + LEITUNGS_KOMMENTAR
                            + " muessen gesetzt sein");
        }

        try
        {
            // ueberpruefen, ob der Benutzer das Recht
            // LEITUNGS_KOMMENTAR_FESTLEGEN in einer der Schleifen besitzt
            SchleifeVO[] schleifen = daoSchleife
                            .findSchleifenByAlarmId(alarmId);

            PersonId adminId = req.getUserBean().getPerson().getPersonId();

            boolean bBesitztRecht = false;

            for (int i = 0, m = schleifen.length; (i < m) && (!bBesitztRecht); i++)
            {
                if (daoPerson.hatPersonRechtInSchleife(adminId,
                                RechtId.LEITUNGS_KOMMENTAR_FESTLEGEN,
                                schleifen[i].getSchleifeId()))
                {
                    bBesitztRecht = true;
                }
            }

            if (!bBesitztRecht)
            {
                throw new StdException(
                                "Der Benutzer besitzt nicht das Recht innerhalb einer der Schleifen, Leitungs-Kommentare festzulegen");
            }

            benutzerVerwaltungTAO
                            .updateKommentarLeitung(alarmId, personId, leitungsKommentar);
        }
        catch (StdException e)
        {
            log.error(e);
        }

        returnObject.put(LEITUNGS_KOMMENTAR, leitungsKommentar);
        
        return returnObject;
    }
}
