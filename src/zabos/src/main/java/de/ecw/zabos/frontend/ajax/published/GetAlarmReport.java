package de.ecw.zabos.frontend.ajax.published;

import java.util.HashMap;
import java.util.Map;

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
import de.ecw.zabos.sql.vo.OrganisationVO;
import de.ecw.zabos.sql.vo.OrganisationsEinheitVO;
import de.ecw.zabos.sql.vo.PersonInAlarmVO;
import de.ecw.zabos.sql.vo.PersonVO;
import de.ecw.zabos.sql.vo.SchleifeVO;
import de.ecw.zabos.types.id.AlarmId;
import de.ecw.zabos.types.id.RechtId;
import de.ecw.zabos.types.id.RueckmeldungStatusId;

/**
 * Liefert den Alarm-Report für eine Alarm-Id zurück.<br>
 * 
 * <table border="1">
 * <tr>
 * <td>Zurückgeliefert wird {@link #convertAlarmVO(AlarmVO)}</td>
 * <td>---</td></td>
 * </tr>
 * <tr>
 * <td> {@link JsonConstants#ARR_}</td>
 * <td>Liste mit den alarmierten Schleifen. Zu jeder Schleife wird Organisation
 * und Organisationseinheit hinterlegt</td>
 * </tr>
 * <tr>
 * <td> {@link JsonConstants#ARR_ALARMIERTE_PERSONEN_JA}</td>
 * <td>Liste mit den Personen die sich mit "Ja" gemeldet haben</td>
 * </tr>
 * <tr>
 * <td> {@link JsonConstants#ARR_ALARMIERTE_PERSONEN_NEIN}</td>
 * <td>Liste mit den Personen die sich mit "Nein" gemeldet haben</td>
 * </tr>
 * <tr>
 * <td> {@link JsonConstants#ARR_ALARMIERTE_PERSONEN_SPAETER}</td>
 * <td>Liste mit den Personen die sich mit "Später" gemeldet haben</td>
 * </tr>
 * <tr>
 * <td> {@link JsonConstants#ARR_ALARMIERTE_PERSONEN_UNBEKANNT}</td>
 * <td>Liste mit den Personen deren Status unbekannt ist</td>
 * </tr>
 * 
 *</table>
 * 
 * #json-param int AlarmId
 * 
 * @return JSON-Objekt
 */
public class GetAlarmReport extends AbstractAjaxMethodAdapter
{
    private final static Logger log = Logger.getLogger(GetAlarmReport.class);

    public GetAlarmReport(final DBResource db)
    {
        super(db);
    }

    public JSONObject run(RequestResources req, JSONObject jsonRequest) throws JSONException, StdException
    {
        // Das JSON-Objekt, das zurueckgegeben wird
        JSONObject r = null;
        // ID des Alarms
        int idAlarm = 0;
        // Der eigentliche Alarm
        AlarmVO voAlarm = null;
        // Alarmierte Schleifen
        SchleifeVO[] voSchleifen = null;
        // Schleifen-Objekt
        SchleifeVO voSchleife = null;
        // Personen
        PersonVO[] voPersonen = null;
        // Organisation
        OrganisationVO voOrganisation = null;
        // Organisationseinheit
        OrganisationsEinheitVO voOrganisationseinheit = null;

        // Haelt ein Array mit den alarmierten Personen vor
        PersonInAlarmVO[] voAlarmiertePersonen = null;
        // Darf Person den Alarm ueberhaupt sehen
        boolean bHatRechtAlarmZuSehen = false;

        // Wurde die Alarm-Id als Parameter uebergeben?
        if (jsonRequest == null)
        {
            throw new StdException(
                            "Fuer die Methode muessen Parameter gesetzt sein.");
        }

        // Den Parameter AlarmId zu einem int umwandeln
        try
        {
            if ((idAlarm = jsonRequest.getInt("AlarmId")) == 0)
            {
                throw new StdException("Der Parameter AlarmId ist leer.");
            }
            else
            {
                log.debug("Parameter AlarmId ist " + idAlarm);
            }
        }
        catch (JSONException e)
        {
            throw new StdException("Der Parameter AlarmId wurde nicht gesetzt.");
        }

        // Die Daten fuer den Alarm zusammen sammeln
        try
        {
            voAlarm = daoAlarm.findAlarmById(new AlarmId(idAlarm));

            // Alarm gefunden
            if (voAlarm != null)
            {
                SchleifeVO[] schleifen = daoSchleife
                                .findSchleifenByAlarmId(voAlarm.getAlarmId());

                // ueberpruefen, ob der User das Recht hat, die Statisik
                // anzuschauen
                if (schleifen != null)
                {
                    for (int i = 0, m = schleifen.length; i < m; i++)
                    {
                        voSchleife = schleifen[i];

                        if (daoPerson.hatPersonRechtInSchleife(req
                                        .getUserBean().getPerson()
                                        .getPersonId(),
                                        RechtId.ALARMHISTORIE_SEHEN, voSchleife
                                                        .getSchleifeId()))
                        {
                            bHatRechtAlarmZuSehen = true;
                        }
                    }
                }

                // User darf Statistik sehen
                if (bHatRechtAlarmZuSehen)
                {
                    voAlarmiertePersonen = daoPersonInAlarm
                                    .findPersonenInAlarmByAlarmId(voAlarm
                                                    .getAlarmId());

                    if (voAlarmiertePersonen == null)
                    {
                        throw new StdException(
                                        "Es konnten keine alarmierten Personen für den Alarm mit der Id "
                                                        + idAlarm
                                                        + " geladen werden.");
                    }
                }
            }
            else
            {
                throw new StdException("Der Alarm mit der Id " + idAlarm
                                + " konnte nicht gefunden werden.");
            }
        }
        catch (StdException e)
        {
            throw new StdException("Fehler bei Laden des Alarms: "
                            + e.getMessage());
        }

        // User darf Statistik sehen > JSON-Objekt zusammen bauen
        if (bHatRechtAlarmZuSehen == true)
        {
            r = new JSONObject();

            try
            {
                // Key: ID der Person als String
                Map<String, PersonVO> hmPersonenInAlarm = new HashMap<String, PersonVO>();
                // Key: ID der alarmierten Person als String
                Map<String, RueckmeldungStatusId> hmRueckmeldungVonPersonen = new HashMap<String, RueckmeldungStatusId>();

                for (int i = 0, m = voAlarmiertePersonen.length; i < m; i++)
                {
                    hmPersonenInAlarm.put(""
                                    + voAlarmiertePersonen[i].getPersonId()
                                                    .getLongValue(), daoPerson
                                    .findPersonById(voAlarmiertePersonen[i]
                                                    .getPersonId()));
                    hmRueckmeldungVonPersonen.put(""
                                    + voAlarmiertePersonen[i].getPersonId()
                                                    .getLongValue(),
                                    voAlarmiertePersonen[i]
                                                    .getRueckmeldungStatusId());
                }

                voSchleifen = daoSchleife.findSchleifenByAlarmId(voAlarm
                                .getAlarmId());

                // Allgemeine Infos zusammenstellen
                r = convertAlarmVO(voAlarm);

                JSONArray jsonArraySchleifen = new JSONArray();

                // Die einzelnen Schleifen durchlaufen
                for (int i = 0, m = voSchleifen.length; i < m; i++)
                {
                    JSONObject jsonObjectSchleife = new JSONObject();
                    JSONObject jsonObjectOrganisation = new JSONObject();
                    JSONObject jsonObjectOrganisationseinheit = new JSONObject();
                    JSONObject jsonObjectPersonenContainer = new JSONObject();

                    // Array mit den alarmierten Personen
                    JSONArray jsonArrJa = new JSONArray();
                    JSONArray jsonArrNein = new JSONArray();
                    JSONArray jsonArrSpaeter = new JSONArray();
                    JSONArray jsonArrUnbekannt = new JSONArray();

                    jsonObjectSchleife.put(JsonConstants.ID, voSchleifen[i]
                                    .getSchleifeId().getLongValue());
                    jsonObjectSchleife.put(JsonConstants.DISPLAY_NAME,
                                    voSchleifen[i].getDisplayName());

                    voOrganisationseinheit = daoOrganisationseinheit
                                    .findOrganisationsEinheitById(voSchleifen[i]
                                                    .getOrganisationsEinheitId());
                    jsonObjectOrganisationseinheit.put(JsonConstants.ID,
                                    voOrganisationseinheit.getBaseId()
                                                    .getLongValue());
                    jsonObjectOrganisationseinheit.put(JsonConstants.NAME,
                                    voOrganisationseinheit.getName());
                    jsonObjectSchleife.put(JsonConstants.ORGANISATIONSEINHEIT,
                                    jsonObjectOrganisationseinheit);

                    voOrganisation = daoOrganisation
                                    .findOrganisationById(voOrganisationseinheit
                                                    .getOrganisationId());
                    jsonObjectOrganisation.put(JsonConstants.ID, voOrganisation
                                    .getOrganisationId().getLongValue());
                    jsonObjectOrganisation.put(JsonConstants.DISPLAY_NAME,
                                    voOrganisation.getName());
                    jsonObjectSchleife.put(JsonConstants.ORGANISATION,
                                    jsonObjectOrganisation);

                    voPersonen = daoPerson.findPersonenByRechtInSchleife(
                                    RechtId.ALARMBENACHRICHTIGUNG_EMPFANGEN,
                                    voSchleifen[i].getSchleifeId());

                    // Personen aufloesen
                    for (int j = 0, n = voPersonen.length; j < n; j++)
                    {
                        log
                                        .debug(voPersonen[j].getDisplayName()
                                                        + " hat Recht Alarmbenachrichtung zu empfangen");

                        // Person wurde alarmiert
                        if (hmPersonenInAlarm.containsKey(""
                                        + voPersonen[j].getPersonId()
                                                        .getLongValue()))
                        {
                            log.debug(voPersonen[j].getDisplayName()
                                            + " wurde alarmiert.");

                            JSONObject jsonObjectPerson = convertPersonVOToExtended(voPersonen[j]);

                            RueckmeldungStatusId rueckmeldungsStatusId = (RueckmeldungStatusId) hmRueckmeldungVonPersonen
                                            .get(""
                                                            + voPersonen[j]
                                                                            .getPersonId()
                                                                            .getLongValue());

                            // Person je nach Rueckmeldestatus einem der Arrays
                            // zuweisen
                            if (rueckmeldungsStatusId == null)
                            {
                                jsonArrUnbekannt.put(jsonObjectPerson);
                            }
                            else if (rueckmeldungsStatusId.getLongValue() == RueckmeldungStatusId.STATUS_JA)
                            {
                                jsonArrJa.put(jsonObjectPerson);
                            }
                            else if (rueckmeldungsStatusId.getLongValue() == RueckmeldungStatusId.STATUS_NEIN)
                            {
                                jsonArrNein.put(jsonObjectPerson);
                            }
                            else if (rueckmeldungsStatusId.getLongValue() == RueckmeldungStatusId.STATUS_SPAETER)
                            {
                                jsonArrSpaeter.put(jsonObjectPerson);
                            }
                        } // if Person wurde alarmiert
                    } // for Personen

                    jsonObjectPersonenContainer.put(
                                    JsonConstants.ARR_ALARMIERTE_PERSONEN_JA,
                                    jsonArrJa);
                    jsonObjectPersonenContainer.put(
                                    JsonConstants.ARR_ALARMIERTE_PERSONEN_NEIN,
                                    jsonArrNein);
                    jsonObjectPersonenContainer
                                    .put(
                                                    JsonConstants.ARR_ALARMIERTE_PERSONEN_SPAETER,
                                                    jsonArrSpaeter);
                    jsonObjectPersonenContainer
                                    .put(
                                                    JsonConstants.ARR_ALARMIERTE_PERSONEN_UNBEKANNT,
                                                    jsonArrUnbekannt);

                    jsonObjectSchleife.put(JsonConstants.ARR_PERSONEN,
                                    jsonObjectPersonenContainer);
                    jsonArraySchleifen.put(jsonObjectSchleife);
                } // for voSchleifen

                // Als letztes die Schleifen zum Objekt hinzufuegen
                r.put(JsonConstants.ARR_SCHLEIFEN, jsonArraySchleifen);
            } // try
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
        } // if bPersonHatRechtAlarmZuSehen
        else
        {
            throw new StdException(
                            "Sie haben kein Recht, die Alarmhistorie zu sehen.");
        }

        return r;
    }

}
