package de.ecw.zabos.frontend.ajax.published;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.ecw.report.types.IReportModel;
import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.frontend.ajax.AbstractAjaxMethodAdapter;
import de.ecw.zabos.frontend.ajax.JsonConstants;
import de.ecw.zabos.frontend.ressources.RequestResources;
import de.ecw.zabos.report.ReportCreationService;
import de.ecw.zabos.service.alarm.klinikum.FunktionstraegerBereichRueckmeldung;
import de.ecw.zabos.service.alarm.klinikum.PersonRueckmeldungCVO;
import de.ecw.zabos.sql.dao.BereichInSchleifeDAO;
import de.ecw.zabos.sql.resource.DBResource;
import de.ecw.zabos.sql.vo.AlarmVO;
import de.ecw.zabos.sql.vo.BereichInSchleifeVO;
import de.ecw.zabos.sql.vo.BereichVO;
import de.ecw.zabos.sql.vo.FunktionstraegerVO;
import de.ecw.zabos.sql.vo.PersonInAlarmVO;
import de.ecw.zabos.sql.vo.PersonVO;
import de.ecw.zabos.sql.vo.SchleifeVO;
import de.ecw.zabos.types.id.AlarmId;
import de.ecw.zabos.types.id.RechtId;
import de.ecw.zabos.types.id.RueckmeldungStatusId;

/**
 * Liefert den Alarm-Report des Klinikums für eine Alarm-Id zurück.<br>
 * 
 * <table border="1">
 * <tr>
 * <td>Zurückgeliefert wird {@link #convertAlarmVO(AlarmVO)} und zusätzlich <ul><li> {@link JsonConstants#IST_NACHALARMIERT} ob einer der Bereiche nachalarmiert wurde</li></ul></td>
 * <td>---</td></td>
 * </tr>
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
 * <ul><li>{@link JsonConstants#SOLLSTAERKE} mit der Sollstärke des Bereichs</li>
 * <li>{@link JsonConstants#IST_NACHALARMIERT} ob der Bereich nachalarmiert wurde</li>
 * </ul>
 * </td>
 * </tr>
 * <tr>
 * <td> {@link JsonConstants#ARR_ALARMIERTE_PERSONEN_JA}</td>
 * <td>Array mit den alarmierten Personen die mit Ja geantwortet haben:
 * <table border="1">
 * <tr><td colspan="2">Person über {@link #convertPersonVO(PersonVO)}</td></tr>
 * <tr><td> {@link JsonConstants#OBJ_PERSON_IN_ALARM}</td><td>Daten über {@link #convertPersonInAlarmVO(PersonInAlarmVO)}</td></tr>
 * </table>
 * </td>
 * </tr>
 * <tr>
 * <td> {@link JsonConstants#ARR_ALARMIERTE_PERSONEN_NEIN}</td>
 * <td>Array mit den alarmierten Personen die mit Nein geantwortet haben:
 * <table border="1">
 * <tr><td colspan="2">Person über {@link #convertPersonVO(PersonVO)}</td></tr>
 * <tr><td> {@link JsonConstants#OBJ_PERSON_IN_ALARM}</td><td>Daten über {@link #convertPersonInAlarmVO(PersonInAlarmVO)}</td></tr>
 * </table>
 * </td>
 * </tr>
 * <tr>
 * <td> {@link JsonConstants#ARR_ALARMIERTE_PERSONEN_UNBEKANNT}</td>
 * <td>Array mit den alarmierten Personen deren Status unbekannt ist:
 * <table border="1">
 * <tr><td colspan="2">Person über {@link #convertPersonVO(PersonVO)}</td></tr>
 * <tr><td> {@link JsonConstants#OBJ_PERSON_IN_ALARM}</td><td>Daten über {@link #convertPersonInAlarmVO(PersonInAlarmVO)}</td></tr>
 * </table>
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
 * #json-param int AlarmId
 * 
 * @return JSON-Objekt
 */
public class GetAlarmReportByBereichUndFunktionstraeger extends
                AbstractAjaxMethodAdapter
{
    private final static Logger log = Logger
                    .getLogger(GetAlarmReportByBereichUndFunktionstraeger.class);

    /**
     * {@link ReportCreationService}
     */
    private ReportCreationService reportCreationService;

    private String urlToReport = "../../report/?";

    public GetAlarmReportByBereichUndFunktionstraeger(final DBResource db)
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
        AlarmVO alarmVO = null;
        // Liste mit den Schleifen, die für den Benutzer sichtbar sind
        List<SchleifeVO> sichtbareSchleifen = new ArrayList<SchleifeVO>();

        // Wurde die Alarm-Id als Parameter uebergeben?
        if (jsonRequest == null)
        {
            throw new StdException(
                            "Fuer die Methode muessen Parameter gesetzt sein.");
        }

        // Den Parameter AlarmId zu einem int umwandeln
        try
        {
            if ((idAlarm = jsonRequest.getInt("iAlarmId")) == 0)
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
            alarmVO = daoAlarm.findAlarmById(new AlarmId(idAlarm));

            // Alarm gefunden
            if (alarmVO != null)
            {
                SchleifeVO[] schleifen = daoSchleife
                                .findZuerstAusgeloesteSchleifenInAlarm(alarmVO
                                                .getAlarmId());

                // ueberpruefen, ob der User das Recht hat, die Statisik
                // anzuschauen
                if (schleifen != null)
                {
                    for (SchleifeVO schleife : schleifen)
                    {
                        if (daoPerson.hatPersonRechtInSchleife(req
                                        .getUserBean().getPerson()
                                        .getPersonId(),
                                        RechtId.ALARMHISTORIE_SEHEN, schleife
                                                        .getSchleifeId()))
                        {
                            sichtbareSchleifen.add(schleife);
                        }
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

        if (sichtbareSchleifen.size() == 0)
        {
            throw new StdException(
                            "Sie besitzen in keiner der Schleifen das Recht, diese sich anzusehen.");
        }
        // Allgemeine Infos zusammenstellen
        r = convertAlarmVO(alarmVO);

        if (getReportCreationService() != null)
        {
            String linkToReport = "";

            if (!alarmVO.getAktiv())
            {
                IReportModel reportModel = getReportCreationService()
                                .createReportModel(alarmVO.getAlarmId());

                if (getReportCreationService().isAlarmInReportQueue(
                                alarmVO.getAlarmId()))
                {
                    linkToReport = "Wird erzeugt..";
                }
                else
                {
                    String urlToReport = getUrlToReport()
                                    + alarmVO.getAlarmId();

                    if (null == getReportCreationService().findReport(
                                    reportModel))
                    {
                        linkToReport = "<a href='"
                                        + urlToReport
                                        + "' target='__new'>Report erzeugen</a>";
                    }
                    else
                    {
                        linkToReport = "<a href='"
                                        + urlToReport
                                        + "' target='__new'>Report anzeigen</a>";
                    }
                }
            }

            r.put(JsonConstants.LINK_TO_REPORT, linkToReport);
        }

        r.put(JsonConstants.IST_NACHALARMIERT, daoAlarm
                        .istAlarmNachalarmiert(alarmVO.getAlarmId()));

        BereichInSchleifeDAO bereichInSchleifeDAO = db.getDaoFactory()
                        .getBereichInSchleifeDAO();

        try
        {
            JSONArray jsonArrSchleifen = new JSONArray();

            // Über alle sichtbaren Schleifen iterieren und die Statistik
            // zusammenbauen
            for (SchleifeVO schleife : sichtbareSchleifen)
            {
                JSONObject jsonSchleife = convertSchleifeVO(schleife);

                PersonInAlarmVO[] piaVOs = daoPersonInAlarm
                                .findPersonenInAlarmByAlarmIdAndSchleifeId(
                                                alarmVO.getAlarmId(),
                                                schleife.getSchleifeId());

                FunktionstraegerBereichRueckmeldung fbr = FunktionstraegerBereichRueckmeldung
                                .buildPersonenRueckmeldung(piaVOs, daoPerson);

                FunktionstraegerVO[] funktionstraegerVOs = daoFunktionstraeger
                                .findFunktionstraegerInAlarmAndSchleife(alarmVO
                                                .getAlarmId(), schleife
                                                .getSchleifeId());

                JSONArray jsonArrFunktionstraeger = new JSONArray();

                for (int i = 0, m = funktionstraegerVOs.length; i < m; i++)
                {
                    FunktionstraegerVO funktionstraegerVO = funktionstraegerVOs[i];

                    JSONObject jsonObjFunktionstraeger = convertFunktionstreagerVO(funktionstraegerVO);

                    BereichVO[] bereicheVO = daoBereich
                                    .findBereicheInAlarmByFunktionstragerIdAndSchleifeId(
                                                    alarmVO.getAlarmId(),
                                                    funktionstraegerVO
                                                                    .getFunktionstraegerId(),
                                                    schleife.getSchleifeId());

                    JSONArray jsonArrBereiche = new JSONArray();

                    for (int j = 0, n = bereicheVO.length; j < n; j++)
                    {
                        BereichVO bereichVO = bereicheVO[j];

                        List<PersonRueckmeldungCVO> alPersonRueckmeldung = fbr
                                        .findByFunktionstraegerIdAndBereichId(
                                                        funktionstraegerVO
                                                                        .getFunktionstraegerId(),
                                                        bereichVO
                                                                        .getBereichId());

                        BereichInSchleifeVO bereichInSchleifeVO = bereichInSchleifeDAO
                                        .findBereichInSchleifeBySchleifeIdAndBereichIdAndFunktionstraegerIdAndAlarmId(
                                                        schleife
                                                                        .getSchleifeId(),
                                                        bereichVO
                                                                        .getBereichId(),
                                                        funktionstraegerVO
                                                                        .getFunktionstraegerId(),
                                                        alarmVO.getAlarmId());

                        JSONArray jsonArrJa = new JSONArray();
                        JSONArray jsonArrNein = new JSONArray();
                        JSONArray jsonArrUnbekannt = new JSONArray();

                        JSONObject jsonObjectBereich = convertBereichVO(bereichVO);

                        jsonObjectBereich.put(JsonConstants.SOLLSTAERKE,
                                        bereichInSchleifeVO.getSollstaerke());

                        jsonObjectBereich
                                        .put(
                                                        JsonConstants.IST_SICHTBAR,
                                                        FindFunktionstraegerMitBereichen
                                                                        .isBereichFunktionstraegerSichtbar(
                                                                                        req,
                                                                                        funktionstraegerVO
                                                                                                        .getFunktionstraegerId(),
                                                                                        bereichVO
                                                                                                        .getBereichId()));

                        for (int k = 0, p = alPersonRueckmeldung.size(); k < p; k++)
                        {
                            PersonRueckmeldungCVO prCVO = alPersonRueckmeldung
                                            .get(k);
                            RueckmeldungStatusId rueckmeldungsStatusId = prCVO
                                            .getPersonInAlarmVO()
                                            .getRueckmeldungStatusId();

                            JSONObject jsonObjectPerson = convertPersonVO(prCVO
                                            .getPerson());
                            jsonObjectPerson
                                            .put(
                                                            JsonConstants.OBJ_PERSON_IN_ALARM,
                                                            convertPersonInAlarmVO(prCVO
                                                                            .getPersonInAlarmVO()));

                            // Person je nach Rueckmeldestatus einem der
                            // Arrays
                            // zuweisen
                            if ((rueckmeldungsStatusId == null)
                                            || (rueckmeldungsStatusId
                                                            .getLongValue() == RueckmeldungStatusId.STATUS_SPAETER))
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

                        } // for alPersonRueckemldung

                        jsonObjectBereich
                                        .put(
                                                        JsonConstants.ARR_ALARMIERTE_PERSONEN_JA,
                                                        jsonArrJa);
                        jsonObjectBereich
                                        .put(
                                                        JsonConstants.ARR_ALARMIERTE_PERSONEN_NEIN,
                                                        jsonArrNein);
                        jsonObjectBereich
                                        .put(
                                                        JsonConstants.ARR_ALARMIERTE_PERSONEN_UNBEKANNT,
                                                        jsonArrUnbekannt);

                        jsonArrBereiche.put(jsonObjectBereich);

                    } // while (itBereiche.hasNext)

                    jsonObjFunktionstraeger.put(JsonConstants.ARR_BEREICH,
                                    jsonArrBereiche);

                    jsonArrFunktionstraeger.put(jsonObjFunktionstraeger);
                } // for fIds
                jsonSchleife.put(JsonConstants.ARR_FUNKTIONSTRAEGER,
                                jsonArrFunktionstraeger);

                jsonArrSchleifen.put(jsonSchleife);
            } // for schleife
            r.put(JsonConstants.ARR_SCHLEIFEN, jsonArrSchleifen);

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

        return r;
    }

    /**
     * Setzt den {@link ReportCreationService}
     * 
     * @param reportCreationService
     */
    public void setReportCreationService(
                    ReportCreationService reportCreationService)
    {
        this.reportCreationService = reportCreationService;
    }

    /**
     * Liefert den {@link ReportCreationService}
     * 
     * @return
     */
    public ReportCreationService getReportCreationService()
    {
        return reportCreationService;
    }

    /**
     * Setzt die relative URL zum Report. Es wird automatisch die AlarmId
     * angehangen.
     * 
     * @param urlToReport
     */
    public void setUrlToReport(String urlToReport)
    {
        this.urlToReport = urlToReport;
    }

    /**
     * Liefert die URL zum Report
     * 
     * @return
     */
    public String getUrlToReport()
    {
        return urlToReport;
    }
}
