package de.ecw.zabos.frontend.ajax;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.ecw.zabos.alarm.ZwischenStatistik;
import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.service.alarm.IAlarmService;
import de.ecw.zabos.sql.dao.AlarmDAO;
import de.ecw.zabos.sql.dao.BereichDAO;
import de.ecw.zabos.sql.dao.FunktionstraegerDAO;
import de.ecw.zabos.sql.dao.OrganisationDAO;
import de.ecw.zabos.sql.dao.OrganisationsEinheitDAO;
import de.ecw.zabos.sql.dao.PersonDAO;
import de.ecw.zabos.sql.dao.PersonInAlarmDAO;
import de.ecw.zabos.sql.dao.RechtDAO;
import de.ecw.zabos.sql.dao.SchleifenDAO;
import de.ecw.zabos.sql.dao.SystemKonfigurationDAO;
import de.ecw.zabos.sql.dao.TelefonDAO;
import de.ecw.zabos.sql.resource.DBResource;
import de.ecw.zabos.sql.tao.BenutzerVerwaltungTAO;
import de.ecw.zabos.sql.vo.AlarmVO;
import de.ecw.zabos.sql.vo.BereichVO;
import de.ecw.zabos.sql.vo.FunktionstraegerVO;
import de.ecw.zabos.sql.vo.OrganisationsEinheitVO;
import de.ecw.zabos.sql.vo.PersonInAlarmVO;
import de.ecw.zabos.sql.vo.PersonVO;
import de.ecw.zabos.sql.vo.SchleifeVO;
import de.ecw.zabos.sql.vo.TelefonVO;
import de.ecw.zabos.types.id.AlarmQuelleId;
import de.ecw.zabos.types.id.RueckmeldungStatusId;

/**
 * Adapter für die veröffentlichten AJAX-Methoden
 * 
 * @author ckl
 * 
 */
abstract public class AbstractAjaxMethodAdapter implements IAjaxMethod
{

    private IAlarmService alarmService;

    protected AlarmDAO daoAlarm = null;

    protected BereichDAO daoBereich = null;

    protected FunktionstraegerDAO daoFunktionstraeger = null;

    protected OrganisationDAO daoOrganisation = null;

    protected OrganisationsEinheitDAO daoOrganisationseinheit = null;

    protected PersonDAO daoPerson = null;

    protected PersonInAlarmDAO daoPersonInAlarm = null;
    
    protected BenutzerVerwaltungTAO benutzerVerwaltungTAO = null;

    protected RechtDAO daoRecht = null;

    protected SchleifenDAO daoSchleife = null;

    protected SystemKonfigurationDAO daoSystemkonfiguration = null;

    protected TelefonDAO daoTelefon = null;

    protected DBResource db = null;

    private final static Logger log = Logger
                    .getLogger(AbstractAjaxMethodAdapter.class);

    /**
     * Konstruktor
     * 
     * @param _dbResource
     */
    public AbstractAjaxMethodAdapter(final DBResource _dbResource)
    {
        this.db = _dbResource;

        this.daoAlarm = db.getDaoFactory().getAlarmDAO();
        this.daoPerson = db.getDaoFactory().getPersonDAO();
        this.daoRecht = db.getDaoFactory().getRechtDAO();
        this.daoPersonInAlarm = db.getDaoFactory().getPersonInAlarmDAO();
        this.daoSystemkonfiguration = db.getDaoFactory()
                        .getSystemKonfigurationDAO();
        this.daoSchleife = db.getDaoFactory().getSchleifenDAO();
        this.daoOrganisation = db.getDaoFactory().getOrganisationDAO();
        this.daoOrganisationseinheit = db.getDaoFactory()
                        .getOrganisationsEinheitDAO();
        this.daoFunktionstraeger = db.getDaoFactory().getFunktionstraegerDAO();
        this.daoTelefon = db.getDaoFactory().getTelefonDAO();
        this.daoBereich = db.getDaoFactory().getBereichDAO();
        this.benutzerVerwaltungTAO = db.getTaoFactory().getBenutzerVerwaltungTAO();
    }

    /**
     * Konvertiert einen Alarm in eine JSON-Object
     * <table border="1">
     * <tr>
     * <td>{@link JsonConstants#ID}</td>
     * <td> {@link AlarmVO#getAlarmId()}</td>
     * </tr>
     * <tr>
     * <td>{@link JsonConstants#ALARM_IST_AKTIV}</td>
     * <td> {@link AlarmVO#getAktiv()}</td>
     * </tr>
     * <tr>
     * <td>{@link JsonConstants#ALARM_REIHENFOLGE}</td>
     * <td> {@link AlarmVO#getReihenfolge()}</td>
     * </tr>
     * <tr>
     * <td>{@link JsonConstants#ALARM_AUSLOESUNG_DATUM}</td>
     * <td> {@link AlarmVO#getReihenfolge()} TODO: Bug</td>
     * </tr>
     * <tr>
     * <td> {@link JsonConstants#TS_ENTWARNUNG}</td>
     * <td> {@link AlarmVO#getEntwarnZeit()} als Timestamp</td>
     * </tr>
     * <tr>
     * <td>{@link JsonConstants#PERSON}</td>
     * <td>Ist nur gesetzt, wenn der Alarm kein Fünfton-Alarm ist; beschreibt
     * die ausloesende Person
     * <table border="1">
     * <tr>
     * <td>{@link JsonConstants#VORNAME}</td>
     * <td> {@link PersonVO#getVorname()}</td>
     * <tr>
     * <td>{@link JsonConstants#NACHNAME}</td>
     * <td> {@link PersonVO#getNachname()}</td>
     * <tr>
     * <td>{@link JsonConstants#ID}</td>
     * <td> {@link PersonVO#getPersonId()}</td>
     * </tr>
     * </table>
     * </td>
     * </tr>
     * <tr>
     * <td> {@link JsonConstants#IST_FUENFTON}</td>
     * <td>Liefert, ob der Alarm über Fünfton ausgelöst wurde</td>
     * </tr>
     * <tr>
     * <td> {@link JsonConstants#ARR_SCHLEIFEN}</td>
     * <td>Array mit den Schleifen
     * <table border="1">
     * <tr>
     * <td>{@link JsonConstants#ID}</td>
     * <td> {@link SchleifeVO#getSchleifeId()}</td>
     * </tr>
     * <tr>
     * <td> {@link JsonConstants#KUERZEL}</td>
     * <td> {@link SchleifeVO#getKuerzel()}</td>
     * </tr>
     * </table>
     * </td>
     * </tr>
     * <tr>
     * <td> {@link JsonConstants#ALARM_ANTWORTEN_JA}</td>
     * <td>Personen, die mit Ja geantwortet haben</td>
     * </tr>
     * <tr>
     * <td> {@link JsonConstants#ALARM_ANTWORTEN_NEIN}</td>
     * <td>Personen, die mit Nein geantwortet haben</td>
     * </tr>
     * <tr>
     * <td> {@link JsonConstants#ALARM_ANTWORTEN_SPAETER}</td>
     * <td>Personen, die mit Später geantwortet haben</td>
     * </tr>
     * <tr>
     * <td> {@link JsonConstants#ALARM_ANTWORTEN_UNBEKANNT}</td>
     * <td>Personen, deren Status unbekannt ist</td>
     * </tr>
     * <tr>
     * <td> {@link JsonConstants#ALARM_ANTWORTEN_TOTAL}</td>
     * <td>Personen, die insgesamt geantwortet haben</td>
     * </tr>
     * <tr>
     * <td> {@link JsonConstants#ALARM_PERSONEN_TOTAL}</td>
     * <td>Anzahl der Personen, die insgesamt alarmiert wurden sind</td>
     * </tr>
     * </table>
     * 
     * @param _alarmVO
     * @return
     * @author ckl
     * @since 200627.06.2006_13:53:16
     */
    public JSONObject convertAlarmVO(AlarmVO _alarmVO)
    {
        final ZwischenStatistik zs = alarmService.zwischenStatistik(_alarmVO);
        PersonVO voPerson = null;
        SchleifeVO[] schleifenVO = null;

        JSONObject jsonTempObjectPerson = new JSONObject();
        JSONObject jsonTempObjectSchleife = new JSONObject();
        JSONArray jsonArraySchleifen = new JSONArray();

        JSONObject retJSONObject = new JSONObject();

        try
        {
            voPerson = daoPerson.findPersonById(_alarmVO.getAlarmPersonId());

            retJSONObject.put(JsonConstants.ID, _alarmVO.getAlarmId()
                            .getLongValue());
            retJSONObject.put(JsonConstants.ALARM_IST_AKTIV, _alarmVO
                            .getAktiv());
            retJSONObject.put(JsonConstants.ALARM_REIHENFOLGE, _alarmVO
                            .getReihenfolge());
            retJSONObject.put(JsonConstants.ALARM_AUSLOESUNG_DATUM, _alarmVO
                            .getReihenfolge());
            retJSONObject.put(JsonConstants.TS_AUSLOESUNG, _alarmVO
                            .getAlarmZeit().getTimeStamp());

            retJSONObject
                            .put(
                                            JsonConstants.ALARM_DEAKTIVIERUNG_NACHALARMIERUNGS_ZEITPUNKT,
                                            daoAlarm
                                                            .findNachalarmierungsDeaktivierungsZeitpunkt(
                                                                            _alarmVO
                                                                                            .getAlarmId())
                                                            .getTimeStamp());

            if (_alarmVO.getEntwarnZeit() != null)
            {
                retJSONObject.put(JsonConstants.TS_ENTWARNUNG, _alarmVO
                                .getEntwarnZeit().getTimeStamp());
            }
            else
            {
                retJSONObject.put(JsonConstants.TS_ENTWARNUNG, 0);
            }

            if (_alarmVO.getAlarmQuelleId().getLongValue() == AlarmQuelleId.ID_5TON
                            .getLongValue())
            {
                retJSONObject.put(JsonConstants.IST_FUENFTON, true);
            }
            else
            {
                jsonTempObjectPerson = new JSONObject();
                jsonTempObjectPerson.put(JsonConstants.VORNAME, voPerson
                                .getVorname());
                jsonTempObjectPerson.put(JsonConstants.NACHNAME, voPerson
                                .getNachname());
                jsonTempObjectPerson.put(JsonConstants.ID, voPerson
                                .getPersonId().getLongValue());
                retJSONObject.put(JsonConstants.PERSON, jsonTempObjectPerson);
                retJSONObject.put(JsonConstants.IST_FUENFTON, false);
            }

            // 2007-01-12 CKL: Schleifen fuer die Alarm-Id laden
            schleifenVO = daoSchleife.findSchleifenByAlarmId(_alarmVO
                            .getAlarmId());

            if (schleifenVO != null)
            {
                for (int i = 0, m = schleifenVO.length; i < m; i++)
                {
                    jsonTempObjectSchleife = new JSONObject();
                    jsonTempObjectSchleife.put(JsonConstants.ID, schleifenVO[i]
                                    .getSchleifeId().getLongValue());
                    jsonTempObjectSchleife.put(JsonConstants.KUERZEL,
                                    schleifenVO[i].getKuerzel());
                    jsonArraySchleifen.put(jsonTempObjectSchleife);
                }
            }

            retJSONObject.put(JsonConstants.ARR_SCHLEIFEN, jsonArraySchleifen);

            if (zs != null)
            {
                retJSONObject.put(JsonConstants.ALARM_ANTWORTEN_JA, zs.gesamt
                                .getNumJa());
                retJSONObject.put(JsonConstants.ALARM_ANTWORTEN_NEIN, zs.gesamt
                                .getNumNein());
                retJSONObject.put(JsonConstants.ALARM_ANTWORTEN_SPAETER,
                                zs.gesamt.getNumSpaeter());
                retJSONObject.put(JsonConstants.ALARM_ANTWORTEN_UNBEKANNT,
                                zs.gesamt.getNumUnbekannt());
                retJSONObject.put(JsonConstants.ALARM_ANTWORTEN_TOTAL,
                                zs.gesamt.getNumTotal());
            }

            retJSONObject
                            .put(
                                            JsonConstants.ALARM_PERSONEN_TOTAL,
                                            daoPersonInAlarm
                                                            .countAlarmiertePersonenInAlarm(_alarmVO
                                                                            .getAlarmId()));
        }
        catch (StdException e)
        {
            log.error(e);
        }
        catch (JSONException e)
        {
            log.error(e);
        }

        return retJSONObject;
    }

    /**
     * Konvertiert ein uebergebenes PersonVO[] in ein JSONArray
     * 
     * <table border="1">
     * <tr>
     * <td>ID der Personen</td>
     * <td>
     * <table border="1">
     * <tr>
     * <td> {@link JsonConstants#DISPLAY_NAME}</td>
     * <td> {@link PersonVO#getDisplayName()}</td>
     * </tr>
     * <tr>
     * <td> {@link JsonConstants#ID}</td>
     * <td> {@link PersonVO#getPersonId()}</td>
     * </tr>
     * </table>
     * </td>
     * </tr>
     * </table>
     * 
     * @param PersonVO
     *            [] _personenVO
     * @return JSONArray mit den geaenderten Werten
     */
    public JSONArray convertPersonVO(final PersonVO[] _personenVO)
    {
        // Bereits gespeicherte Personen
        HashMap<String, String> hmFoundPersonen = new HashMap<String, String>();
        // Temporaeres Objekt für eine Person im Array
        JSONObject jsonTempObject = null;
        // Enthaelt die gefunden Personen
        JSONArray jsonArrPersonen = new JSONArray();

        PersonVO.sortPersonenByNachnameVorname(_personenVO);

        if (_personenVO != null)
        {
            for (int i = 0, m = _personenVO.length; i < m; i++)
            {
                // Die Person wurde noch nicht zugewiesen
                if (!hmFoundPersonen.containsKey(String.valueOf(_personenVO[i]
                                .getPersonId().getLongValue())))
                {
                    try
                    {
                        jsonTempObject = new JSONObject();
                        jsonTempObject.put(JsonConstants.DISPLAY_NAME,
                                        _personenVO[i].getDisplayName());
                        jsonTempObject.put(JsonConstants.ID, _personenVO[i]
                                        .getPersonId().getLongValue());
                        jsonArrPersonen.put(jsonTempObject);
                        hmFoundPersonen.put(String.valueOf(_personenVO[i]
                                        .getPersonId().getLongValue()), "1");
                    }
                    catch (JSONException e)
                    {
                        log.error(e);
                    }
                }
            }
        }

        return jsonArrPersonen;
    }

    /**
     * Konvertiert ein {@link PersonVO} in ein {@link JSONObject}.
     * <table border="1">
     * <tr>
     * <td> {@link JsonConstants#NAME}</td>
     * <td> {@link PersonVO#getName()}</td>
     * </tr>
     * <tr>
     * <td> {@link JsonConstants#DISPLAY_NAME}</td>
     * <td> {@link PersonVO#getDisplayName()}</td>
     * </tr>
     * <tr>
     * <td> {@link JsonConstants#NACHNAME}</td>
     * <td> {@link PersonVO#getNachname()}</td>
     * </tr>
     * <tr>
     * <td> {@link JsonConstants#VORNAME}</td>
     * <td> {@link PersonVO#getVorname()}</td>
     * </tr>
     * <tr>
     * <td> {@link JsonConstants#ID}</td>
     * <td> {@link PersonVO#getPersonId()}</td>
     * </tr>
     * <tr>
     * <td> {@link JsonConstants#EMAIL}</td>
     * <td> {@link PersonVO#getEmail()}</td>
     * </tr>
     * <tr>
     * <td> {@link JsonConstants#KOSTENSTELLE}</td>
     * <td>Wird nur gesetzt, wenn eine Kostenstelle definiert wurde
     * <table border="1">
     * <tr>
     * <td> {@link JsonConstants#ID}</td>
     * <td> {@link OrganisationsEinheitVO#getOrganisationsEinheitId()}</td>
     * </tr>
     * <td> {@link JsonConstants#NAME}</td>
     * <td> {@link OrganisationsEinheitVO#getName()}</td>
     * </tr>
     * </table>
     * </td></tr>
     * <tr>
     * <td> {@link JsonConstants#TELEFON}</td>
     * <td>
     * Wird nur gesetzt, wenn ein Telefon definiert wurde
     * <table border="1">
     * <tr>
     * <td> {@link JsonConstants#ID}</td>
     * <td> {@link TelefonVO#getTelefonId()}</td>
     * <tr>
     * <td> {@link JsonConstants#IST_AKTIV}</td>
     * <td> {@link TelefonVO#getAktiv()}</td>
     * <tr>
     * <td> {@link JsonConstants#TELEFON_NUMMER}</td>
     * <td> {@link TelefonVO#getNummer()}</td>
     * </tr>
     * </table>
     * </td>
     * </tr>
     * </table>
     * 
     * @param personVO
     * @return
     * @throws StdException
     * @throws JSONException
     */
    public JSONObject convertPersonVOToExtended(final PersonVO personVO) throws StdException, JSONException
    {
        JSONObject r = convertPersonVO(personVO);

        if (personVO.getOEKostenstelle() != null)
        {
            JSONObject kostenstelle = new JSONObject();
            kostenstelle.put(JsonConstants.ID, personVO.getOEKostenstelle()
                            .getLongValue());
            OrganisationsEinheitVO oeVO = daoOrganisationseinheit
                            .findOrganisationsEinheitById(personVO
                                            .getOEKostenstelle());
            kostenstelle.put(JsonConstants.NAME, oeVO.getName());
            r.put(JsonConstants.KOSTENSTELLE, kostenstelle);
        }

        TelefonVO telefonVO = daoTelefon.findCurrentTelefonByPersonId(personVO
                        .getPersonId());

        if (personVO.getFunktionstraegerId() != null)
        {
            FunktionstraegerVO funktionstraegerVO = daoFunktionstraeger
                            .findFunktionstraegerById(personVO
                                            .getFunktionstraegerId());

            if (funktionstraegerVO != null)
            {
                r.put(JsonConstants.FUNKTIONSTRAEGER,
                                convertFunktionstreagerVO(funktionstraegerVO));
            }
        }

        if (personVO.getBereichId() != null)
        {
            BereichVO bereichVO = daoBereich.findBereichById(personVO
                            .getBereichId());

            if (bereichVO != null)
            {
                r.put(JsonConstants.BEREICH, convertBereichVO(bereichVO));
            }
        }

        if (telefonVO != null)
        {
            JSONObject telefon = new JSONObject();
            telefon.put(JsonConstants.ID, telefonVO.getTelefonId().toString());
            telefon.put(JsonConstants.IST_AKTIV, telefonVO.getAktiv());
            telefon.put(JsonConstants.TELEFON_NUMMER, telefonVO.getNummer()
                            .toString());
            r.put(JsonConstants.TELEFON, telefon);
        }

        return r;
    }

    /**
     * Konvertiert ein {@link PersonVO} in ein {@link JSONObject}.
     * <table border="1">
     * <tr>
     * <td> {@link JsonConstants#NAME}</td>
     * <td> {@link PersonVO#getName()}</td>
     * </tr>
     * <tr>
     * <td> {@link JsonConstants#DISPLAY_NAME}</td>
     * <td> {@link PersonVO#getDisplayName()}</td>
     * </tr>
     * <tr>
     * <td> {@link JsonConstants#NACHNAME}</td>
     * <td> {@link PersonVO#getNachname()}</td>
     * </tr>
     * <tr>
     * <td> {@link JsonConstants#VORNAME}</td>
     * <td> {@link PersonVO#getVorname()}</td>
     * </tr>
     * <tr>
     * <td> {@link JsonConstants#ID}</td>
     * <td> {@link PersonVO#getPersonId()}</td>
     * </tr>
     * <tr>
     * <td> {@link JsonConstants#EMAIL}</td>
     * <td> {@link PersonVO#getEmail()}</td>
     * </tr>
     * </table>
     * 
     * @param voPerson
     * @return
     * @throws StdException
     * @throws JSONException
     */
    public JSONObject convertPersonVO(final PersonVO voPerson) throws StdException, JSONException
    {
        JSONObject r = new JSONObject();

        r.put(JsonConstants.NAME, voPerson.getName());
        r.put(JsonConstants.DISPLAY_NAME, voPerson.getDisplayName());
        r.put(JsonConstants.NACHNAME, voPerson.getNachname());
        r.put(JsonConstants.VORNAME, voPerson.getVorname());
        r.put(JsonConstants.EMAIL, voPerson.getEmail());
        r.put(JsonConstants.ID, voPerson.getPersonId().getLongValue());

        return r;
    }

    /**
     * Wandelt ein {@link FunktionstraegerVO} in ein {@link JSONException}
     * 
     * <table border="1">
     * <tr>
     * <td> {@link JsonConstants#ID}</td>
     * <td> {@link FunktionstraegerVO#getFunktionstraegerId()}</td>
     * </tr>
     * <tr>
     * <td> {@link JsonConstants#BESCHREIBUNG}</td>
     * <td> {@link FunktionstraegerVO#getBeschreibung()}</td>
     * </tr>
     * <tr>
     * <td> {@link JsonConstants#KUERZEL}</td>
     * <td> {@link FunktionstraegerVO#getKuerzel()}</td>
     * </tr>
     * </table>
     * 
     * @param _funktionstreagerVO
     * @return
     * @throws JSONException
     */
    public JSONObject convertFunktionstreagerVO(
                    final FunktionstraegerVO _funktionstreagerVO) throws JSONException
    {
        JSONObject r = new JSONObject();
        r.put(JsonConstants.ID, _funktionstreagerVO.getFunktionstraegerId());
        r
                        .put(JsonConstants.BESCHREIBUNG, _funktionstreagerVO
                                        .getBeschreibung());
        r.put(JsonConstants.KUERZEL, _funktionstreagerVO.getKuerzel());

        return r;
    }

    /**
     * Wandelt ein {@link BereichVO} in ein {@link JSONObject}
     * 
     * <table border="1">
     * <tr>
     * <td> {@link JsonConstants#ID}</td>
     * <td> {@link BereichVO#getBereichId()}</td>
     * </tr>
     * <tr>
     * <td> {@link JsonConstants#BESCHREIBUNG}</td>
     * <td> {@link BereichVO#getBeschreibung()}</td>
     * </tr>
     * <tr>
     * <td> {@link JsonConstants#NAME}</td>
     * <td> {@link BereichVO#getName()}</td>
     * </tr>
     * </table>
     * 
     * @param _bereichVO
     * 
     * @return
     * @throws JSONException
     */
    public JSONObject convertBereichVO(final BereichVO _bereichVO) throws JSONException
    {
        JSONObject r = new JSONObject();
        r.put(JsonConstants.ID, _bereichVO.getBereichId());
        r.put(JsonConstants.BESCHREIBUNG, _bereichVO.getBeschreibung());
        r.put(JsonConstants.NAME, _bereichVO.getName());
        return r;
    }

    /**
     * Wandelt ein {@link SchleifeVO} in ein {@link JSONObject} um
     * <table border="1">
     * <tr>
     * <td> {@link JsonConstants#ID}</td>
     * <td> {@link SchleifeVO#getSchleifeId()}</td>
     * </tr>
     * <tr>
     * <td> {@link JsonConstants#NAME}</td>
     * <td> {@link SchleifeVO#getName()}</td>
     * </tr>
     * <tr>
     * <td> {@link JsonConstants#DISPLAY_NAME}</td>
     * <td> {@link SchleifeVO#getDisplayName()}</td>
     * </tr>
     * <tr>
     * <td> {@link JsonConstants#KUERZEL}</td>
     * <td> {@link SchleifeVO#getKuerzel()}</td>
     * </tr>
     * </table>
     * 
     * @param voSchleife
     * @return
     * @throws StdException
     * @throws JSONException
     */
    public JSONObject convertSchleifeVO(final SchleifeVO voSchleife) throws StdException, JSONException
    {
        JSONObject r = new JSONObject();
        r.put(JsonConstants.ID, voSchleife.getSchleifeId().getLongValue());
        r.put(JsonConstants.NAME, voSchleife.getName());
        r.put(JsonConstants.DISPLAY_NAME, voSchleife.getDisplayName());
        r.put(JsonConstants.KUERZEL, voSchleife.getKuerzel());

        return r;
    }

    /**
     * Konvert ein {@link PersonInAlarmVO} in ein {@link JSONObject}
     * <table border="1">
     * <tr>
     * <td> {@link JsonConstants#KOMMENTAR}</td>
     * <td> {@link PersonInAlarmVO#getKommentar()}</td>
     * </tr>
     * <tr>
     * <td> {@link JsonConstants#KOMMENTAR_LEITUNG}</td>
     * <td> {@link PersonInAlarmVO#getKommentarLeitung()}</td>
     * </tr>
     * <tr>
     * <td> {@link JsonConstants#PERSON_ID}</td>
     * <td> {@link PersonInAlarmVO#getPersonId()}</td>
     * </tr>
     * <tr>
     * <td> {@link JsonConstants#ALARM_ID}</td>
     * <td> {@link PersonInAlarmVO#getAlarmId()}</td>
     * </tr>
     * <tr>
     * <td> {@link JsonConstants#RUECKMELDUNG_STATUS_ID}</td>
     * <td> {@link PersonInAlarmVO#getRueckmeldungStatusId()}</td>
     * </tr>
     * </table>
     * 
     * @param _piaVO
     * @return
     * @throws StdException
     * @throws JSONException
     */
    public JSONObject convertPersonInAlarmVO(final PersonInAlarmVO _piaVO) throws StdException, JSONException
    {
        JSONObject r = new JSONObject();
        r.put(JsonConstants.KOMMENTAR, _piaVO.getKommentar());
        r.put(JsonConstants.KOMMENTAR_LEITUNG, _piaVO.getKommentarLeitung());

        RueckmeldungStatusId rsId = _piaVO.getRueckmeldungStatusId();

        long lRsId = 0;

        if (rsId != null)
        {
            lRsId = _piaVO.getRueckmeldungStatusId().getLongValue();
        }

        r.put(JsonConstants.ALARM_ID, _piaVO.getAlarmId().getLongValue());
        r.put(JsonConstants.PERSON_ID, _piaVO.getPersonId().getLongValue());
        r.put(JsonConstants.RUECKMELDUNG_STATUS_ID, lRsId);

        return r;
    }

    /**
     * Liefert den {@link IAlarmService}
     * 
     * @return
     */
    public IAlarmService getAlarmService()
    {
        return alarmService;
    }

    /**
     * Setzt den {@link IAlarmService}
     * 
     * @param _alarmService
     */
    public void setAlarmService(IAlarmService _alarmService)
    {
        this.alarmService = _alarmService;
    }

    /**
     * Sortiert ein JSONArray von Schleifen nach "szDisplayName"
     * 
     * @param _personen
     */
    public void sortJSONArraySchleifenByDisplayName(JSONArray _schleifen)
    {

        JSONObject l = new JSONObject();
        JSONObject t = new JSONObject();
        String lname = "";
        String rname = "";

        if (_schleifen.length() > 1)
        {
            boolean bSwapped;
            do
            {
                bSwapped = false;

                try
                {
                    l = _schleifen.getJSONObject(0); // PersonVO l =
                    // _personen[0];

                    // String lname = l.getNachname()+ " " + l.getVorname();
                    lname = l.getString(JsonConstants.DISPLAY_NAME)
                                    .toLowerCase().replace('ä', 'a').replace(
                                                    'ö', 'o').replace('ü', 'u')
                                    .replace('ß', 's');

                    for (int i = 1; i < _schleifen.length(); i++)
                    {
                        t = _schleifen.getJSONObject(i); // PersonVO t =
                        // _personen[i];
                        rname = t.getString(JsonConstants.DISPLAY_NAME)
                                        .toLowerCase().replace('ä', 'a')
                                        .replace('ö', 'o').replace('ü', 'u')
                                        .replace('ß', 's');
                        if (lname.compareTo(rname) > 0)
                        {
                            _schleifen.put(i, l);
                            _schleifen.put(i - 1, t);
                            bSwapped = true;
                        }
                        else
                        {
                            l = t;
                            lname = rname;
                        }
                    }
                }
                catch (JSONException e)
                {
                    log.error(e);
                }
            }
            while (bSwapped);
        }

    }
}
