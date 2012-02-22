package de.ecw.zabos.service.alarm.klinikum;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import de.ecw.zabos.alarm.RueckmeldeStatistik;
import de.ecw.zabos.alarm.ZwischenStatistik;
import de.ecw.zabos.bo.SchleifeBO;
import de.ecw.zabos.broadcast.sms.SmsContent;
import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.service.alarm.AlarmServiceAdapter;
import de.ecw.zabos.service.alarm.ext.AlarmInterceptorActionType;
import de.ecw.zabos.service.alarm.ext.AlarmInterceptorDelegator;
import de.ecw.zabos.service.alarm.ext.IAlarmInterceptor;
import de.ecw.zabos.sql.Scheme;
import de.ecw.zabos.sql.dao.AlarmDAO;
import de.ecw.zabos.sql.dao.BereichInSchleifeDAO;
import de.ecw.zabos.sql.dao.OrganisationDAO;
import de.ecw.zabos.sql.dao.OrganisationsEinheitDAO;
import de.ecw.zabos.sql.dao.PersonDAO;
import de.ecw.zabos.sql.dao.PersonInAlarmDAO;
import de.ecw.zabos.sql.dao.SchleifenDAO;
import de.ecw.zabos.sql.dao.SmsOutDAO;
import de.ecw.zabos.sql.resource.DBResource;
import de.ecw.zabos.sql.tao.SmsOutTAO;
import de.ecw.zabos.sql.vo.AlarmVO;
import de.ecw.zabos.sql.vo.BereichInSchleifeVO;
import de.ecw.zabos.sql.vo.OrganisationVO;
import de.ecw.zabos.sql.vo.OrganisationsEinheitVO;
import de.ecw.zabos.sql.vo.PersonInAlarmVO;
import de.ecw.zabos.sql.vo.PersonVO;
import de.ecw.zabos.sql.vo.SchleifeVO;
import de.ecw.zabos.sql.vo.SmsOutVO;
import de.ecw.zabos.sql.vo.SystemKonfigurationVO;
import de.ecw.zabos.sql.vo.TelefonVO;
import de.ecw.zabos.types.UnixTime;
import de.ecw.zabos.types.id.AlarmId;
import de.ecw.zabos.types.id.AlarmQuelleId;
import de.ecw.zabos.types.id.BereichId;
import de.ecw.zabos.types.id.FunktionstraegerId;
import de.ecw.zabos.types.id.PersonId;
import de.ecw.zabos.types.id.RechtId;
import de.ecw.zabos.types.id.RueckmeldungStatusId;
import de.ecw.zabos.types.id.SchleifeId;
import de.ecw.zabos.util.StringUtils;

/**
 * Diese Alarmierungsvariante unterstützt die Nachalarmierung der verschiedenen Schleifen bzw. deren
 * Bereiche/Funktionsträger.
 * 
 * @author ckl
 */
public class KlinikumAlarmService extends AlarmServiceAdapter
{
    /**
     * Status-Codes, die in bereichNachalarmieren auftreten können
     * 
     * @author ckl
     * 
     */
    public static enum STATUS_BEREICH_NACHALARMIEREN
    {
        FOLGEBEREICH_ALARMIERT, FOLGEBEREICH_NICHT_GEFUNDEN, FOLGESCHLEIFE_NICHT_DEFINIERT, FOLGESCHLEIFE_NICHT_GEFUNDEN
    }

    /**
     * Logging-Instanz
     */
    private final static Logger log = Logger
                    .getLogger(KlinikumAlarmService.class);

    /**
     * Interceptoren
     */
    private IAlarmInterceptor alarmInterceptor = new AlarmInterceptorDelegator();

    /**
     * Konstruktor
     * 
     * @param _dbresource
     *            Unbedingt eine neue Datenbankinstanz übergeben, da diese
     *            Klasse gethreadet wird.
     * @param _systemKonfiguration
     * @param _i18n
     */
    public KlinikumAlarmService(DBResource _dbresource,
                    SystemKonfigurationVO _systemKonfiguration,
                    SmsContent _smsContent)
    {
        super(_dbresource, _systemKonfiguration, _smsContent);
    }

    @Override
    public AlarmVO alarmAusloesen(String _zusatzText, SchleifeVO[] _schleifen,
                    AlarmQuelleId _alarmQuelleId, PersonId _alarmPersonId,
                    String _rpUnbekannt, String _gpsKoordinate)
    {
        // Neuen Alarm anlegen
        AlarmVO alarmVO = daoFactory.getObjectFactory().createAlarm();

        try
        {
            begin();

            PersonDAO personDAO = daoFactory.getPersonDAO();
            SchleifeBO schleifeBO = dbresource.getBoFactory().getSchleifeBO();

            // null oder ' ' separierte Liste von Schleifenkuerzeln
            String rpAusgeloest = null;
            String rpBereitsAusgeloest = null;
            String rpKeineBerechtigung = null;

            // List<SchleifeVO>
            List<SchleifeVO> alSchleifen = findAuszuloesendeSchleifen(_schleifen);

            // Sind noch auszuloesende Schleifen uebrig?
            if (alSchleifen.size() > 0)
            {
                alarmVO.setAlarmPersonId(_alarmPersonId);
                alarmVO.setAlarmQuelleId(_alarmQuelleId);
                alarmVO.setKommentar(_zusatzText);
                UnixTime alarmZeit = UnixTime.now();
                alarmVO.setAlarmZeit(alarmZeit);
                // 2007-06-21 CKL: GPS-Koordinaten hinzugefuegt
                alarmVO.setGpsKoordinate(_gpsKoordinate);
                alarmVO.setAktiv(true);

                alarmVO = daoFactory.getAlarmDAO().createAlarm(alarmVO);

                getAlarmInterceptor()
                                .intercept(AlarmInterceptorActionType.BEFORE_ALARM_AUSLOESEN,
                                                alarmVO);

                AlarmId alarmId = alarmVO.getAlarmId();

                OrganisationDAO organisationDAO = daoFactory
                                .getOrganisationDAO();
                OrganisationsEinheitDAO organisationsEinheitDAO = daoFactory
                                .getOrganisationsEinheitDAO();

                // Schleifen und deren Personen zum Alarm hinzufuegen
                // Alarmbenachrichtungen an Personen via SMS verschicken
                // Ausloesungsbestaetigung an ausloesende Person (falls
                // vorhanden)
                // verschicken
                for (int iSchleife = 0; iSchleife < alSchleifen.size(); iSchleife++)
                {
                    SchleifeVO schleifeVO = (SchleifeVO) alSchleifen
                                    .get(iSchleife);

                    SchleifeId schleifeId = schleifeVO.getSchleifeId();

                    OrganisationsEinheitVO oeVO = organisationsEinheitDAO
                                    .findOrganisationsEinheitById(schleifeVO
                                                    .getOrganisationsEinheitId());
                    OrganisationVO oVO = organisationDAO
                                    .findOrganisationById(oeVO
                                                    .getOrganisationId());

                    String kuerzel = schleifeVO.getKuerzel();

                    // Hat die Person die Berechtigung diese Schleife
                    // auszuloesen?
                    boolean bPerm;

                    if (_alarmPersonId == null)
                    {
                        bPerm = true;
                    }
                    else
                    {
                        bPerm = personDAO.hatPersonRechtInSchleife(
                                        _alarmPersonId,
                                        RechtId.ALARM_AUSLOESEN, schleifeId);
                    }

                    if (bPerm)
                    {
                        // Zuweisung der Schleife zum Alarm
                        daoFactory.getAlarmDAO().addSchleifeToAlarm(schleifeId,
                                        alarmId);

                        // Bereiche der Schleife zum Alarm hinzufuegen
                        BereichInSchleifeVO[] bereicheInSchleife = daoFactory
                                        .getBereichInSchleifeDAO()
                                        .findBereicheInSchleifeBySchleifeId(
                                                        schleifeId);

                        int numPersons = 0;

                        for (int iBereiche = 0; iBereiche < bereicheInSchleife.length; iBereiche++)
                        {
                            BereichInSchleifeVO bereichInSchleife = bereicheInSchleife[iBereiche];

                            numPersons += funktionstraegerBereichAlarmieren(
                                            schleifeVO, bereichInSchleife,
                                            alarmVO, _zusatzText, alarmZeit,
                                            oVO.getName(), oeVO.getName());

                        }

                        // Personen in der Schleife alarmieren, die *nicht* in
                        // den Funktionsträger/Bereichen existieren, aber
                        // trotzdem eine Empfangsberechtigung haben
                        int numPersonenOhneKonkreteZuordnung = personenAlarmieren(
                                        schleifeVO,
                                        alarmVO,
                                        _zusatzText,
                                        alarmZeit,
                                        oeVO.getName(),
                                        oeVO.getName(),
                                        schleifeBO.findPersonenMitEmpfangsberechtigungOhneKonkrekteZurodnung(
                                                        schleifeId,
                                                        bereicheInSchleife));

                        log.info("Innerhalb der Schleife \""
                                        + schleifeVO.getDisplayName()
                                        + "\" wurden "
                                        + numPersons
                                        + " Personen in den zugeordneten Funktiontraeger-/Bereiches-Kombinationen alarmiert; "
                                        + numPersonenOhneKonkreteZuordnung
                                        + " Personen wurden zusaetzlich alarmiert, da sie die Empfangsberechtigung fuer die Schleife besassen, obwohl sie keine der noetigen Kombinationen zugeordnet waren");

                        rpAusgeloest = StringUtils.addToCSV(rpAusgeloest,
                                        kuerzel + "(" + numPersons + ")");

                    } // if bperm
                    else
                    {
                        // Person hat keine Berechtigung diese Schleife
                        // auszuloesen
                        log.debug("Person id=\""
                                        + _alarmPersonId
                                        + " hat keine Berechtigung die Schleife kuerzel=\""
                                        + kuerzel + "\" auszuloesen");

                        rpKeineBerechtigung = StringUtils.addToCSV(
                                        rpKeineBerechtigung, kuerzel);
                    }

                } // for iSchleife

                // Wurde der Alarm manuell ueber SMS ausgelöst?
                if ((_alarmPersonId != null)
                                && (_alarmQuelleId == AlarmQuelleId.ID_SMS)) // 2006-06-01
                // cst:
                // Issue
                // #279
                {
                    // Bestaetigungsnachricht (ueber alle Schleifen)fuer Person,
                    // welche
                    // den Alarm ausgeloest hat, verschicken
                    String nachricht = getSmsContent().resolveAusloesung(
                                    alarmVO, rpAusgeloest, _rpUnbekannt,
                                    rpKeineBerechtigung, rpBereitsAusgeloest,
                                    null);

                    String context_o = null;
                    String context_oe = null;
                    PersonVO personVO = personDAO
                                    .findPersonById(_alarmPersonId);

                    if (personVO.getOEKostenstelle() != null)
                    {
                        OrganisationsEinheitVO oeVO = organisationsEinheitDAO
                                        .findOrganisationsEinheitById(personVO
                                                        .getOEKostenstelle());
                        if (oeVO != null)
                        {
                            OrganisationVO oVO = organisationDAO
                                            .findOrganisationById(oeVO
                                                            .getOrganisationId());
                            context_oe = oeVO.getName();
                            context_o = oVO.getName();
                        }
                    }

                    SmsOutTAO smsOutTAO = taoFactory.getSmsOutTAO();
                    smsOutTAO.sendeNachrichtAnPerson(_alarmPersonId, nachricht,
                                    "Alarmauslösung",
                                    StringUtils.intToHexString(alarmVO
                                                    .getReihenfolge()),
                                    context_o, context_oe);

                } // if _alarmPersonId != null
            } // if alSchleifen.size>0

            commit();

            getAlarmInterceptor().intercept(
                            AlarmInterceptorActionType.AFTER_ALARM_AUSLOESEN,
                            alarmVO);
        }
        catch (StdException e)
        {
            log.error(e);
            rollback();
        }

        return alarmVO;
    }

    /**
     * Findet die Sollstärke, die für die zuerst ausgelöste Schleife des Alarms
     * zuständig ist. Es werden maximal die Vorgänger von 5 Schleifen gefunden!
     * 
     * <ul>
     * <li>Schleifen S1, S2 und S3 existieren.</li>
     * <li>S3 ist Folgeschleife von S2; S2 ist Folgeschleife von S1</li>
     * <li>BereichInSchleife BIS1 ist S1, BereichInSchleife BIS2 ist S2 und
     * BereichInSchleife BIS3 ist S3 zugewiesen</li>
     * <li>Die Funktionsträger/Bereichs-Kombination aller ausgelösten Schleifen
     * ist identisch</li>
     * </ul>
     * 
     * <ul>
     * <li>Wenn Alarm S2 ausgelöst wurde BIS3 übergeben wird, wird die
     * Sollstärke BIS2 zurückgegeben</li>
     * <li>Wenn Alarm S1 ausgelöst wurde und BIS3 übergeben wird, wird die
     * Sollstärke BIS1 zurückgegeben</li>
     * <li>Wenn Alarm S3 ausgelöst wurde und BIS3 übergeben wird, die Sollstärke
     * von BIS3 zurückgegeben</li>
     * </ul>
     * 
     * @param _alarmId
     * @param _bis
     * @return
     * @throws StdException
     */
    public int findReferenceSollstaerke(AlarmId _alarmId,
                    BereichInSchleifeVO _bis) throws StdException
    {
        BereichInSchleifeDAO bisDao = daoFactory.getBereichInSchleifeDAO();

        BereichInSchleifeVO bis = bisDao.findReferenzBereichInSchleifeInAlarm(
                        _bis, _alarmId);

        return bis.getSollstaerke();
    }

    /**
     * Report fuer ausloesende Person generieren und verschicken. Wird
     * aufgerufen, wenn ein Alarm entweder durch Timeout abgelaufen ist oder
     * sich alle Personen zurueckgemeldet haben. <br />
     * 2007-06-19 CKL: Statistik fuer die Funktionstraeger ist hinzugekommen. <br />
     * Diese Methode <strong>ist</strong> transaktionssicher.
     * 
     * @param _alarmVO
     * @throws StdException
     */
    public void alarmDeaktivieren(AlarmVO _alarmVO,
                    FunktionstraegerBereichRueckmeldung _rueckmeldung) throws StdException
    {
        try
        {
            begin();

            getAlarmInterceptor()
                            .intercept(AlarmInterceptorActionType.BEFORE_ALARM_DEAKTIVIEREN,
                                            _alarmVO);

            AlarmDAO alarmDAO = dbresource.getDaoFactory().getAlarmDAO();
            SchleifenDAO schleifenDAO = dbresource.getDaoFactory()
                            .getSchleifenDAO();
            PersonDAO personDAO = dbresource.getDaoFactory().getPersonDAO();

            UnixTime jetzt = UnixTime.now();

            AlarmId alarmId = _alarmVO.getAlarmId();

            // Alle Bereiche innerhalb des Alarms deaktivieren
            alarmDAO.deaktivereAlleBereicheInAlarm(alarmId);

            // Alarmdeaktivierung in Datenbank schreiben
            alarmDAO.deaktiviereAlarmById(alarmId);

            // Gesamt Statistik erstellen
            RueckmeldeStatistik gesamt = new RueckmeldeStatistik(
                            _rueckmeldung.getPersonenInAlarm().length);
            alarmStatistik(_alarmVO, _rueckmeldung.getPersonenInAlarm(), gesamt);

            SchleifeVO[] alarmSchleifen = schleifenDAO
                            .findSchleifenByAlarmId(alarmId);

            // Key ist SchleifeVO, Value ist String
            HashMap<SchleifeVO, String> hmReportNachricht = new HashMap<SchleifeVO, String>();

            // Reportnachrichten generieren
            for (int i = 0; i < alarmSchleifen.length; i++)
            {
                SchleifeVO schleifeVO = alarmSchleifen[i];

                String nachricht = generateRueckmeldungReport(_alarmVO,
                                schleifeVO, gesamt);

                hmReportNachricht.put(schleifeVO, nachricht);
            }

            PersonVO ausloeserPersonVO;

            if (_alarmVO.getAlarmPersonId() != null)
            {
                ausloeserPersonVO = personDAO.findPersonById(_alarmVO
                                .getAlarmPersonId());
            }
            else
            {
                ausloeserPersonVO = null;
            }

            OrganisationsEinheitDAO oeDAO = daoFactory
                            .getOrganisationsEinheitDAO();
            OrganisationDAO oDAO = daoFactory.getOrganisationDAO();

            // Rückmeldungsreport-SMS an jeden Schleifenverantwortlichen
            // verschicken
            for (int i = 0; i < alarmSchleifen.length; i++)
            {
                SchleifeVO schleifeVO = alarmSchleifen[i];

                // 2006-06-09 CKL: Siehe #287: Statusreport nur senden, wenn
                // SMS-Ausloesung
                // ODER 5-Ton und Schleife.Statusreport aktiv
                // 2006-10-30 CKL: Ueberpruefung auf AlarmQuelleId mit Hilfe von
                // getLongValue() hinzugefuegt
                if ((_alarmVO.getAlarmQuelleId().getLongValue() == AlarmQuelleId.ID_SMS
                                .getLongValue())
                                || ((_alarmVO.getAlarmQuelleId().getLongValue() == AlarmQuelleId.ID_5TON
                                                .getLongValue()) && (schleifeVO
                                                .getStatusreportFuenfton() == true)))
                {
                    PersonVO[] verantwortliche = personDAO
                                    .findPersonenByRechtInSchleife(
                                                    RechtId.ALARM_RUECKMELDUNGSREPORT_EMPFANGEN,
                                                    schleifeVO.getSchleifeId());
                    // ArrayList<PersonVO>
                    ArrayList<PersonVO> alVerantwortliche = new ArrayList<PersonVO>();

                    // Ist die Alarmausloesende Person gleichzeitig auch
                    // Schleifenverantwortlicher?
                    boolean bAusloeserIstVerantwortlicher = false;

                    for (int j = 0; j < verantwortliche.length; j++)
                    {
                        PersonVO verantwortlicher = verantwortliche[j];
                        if (verantwortlicher.isAnwesend(jetzt))
                        {
                            alVerantwortliche.add(verantwortlicher);
                        }
                        bAusloeserIstVerantwortlicher |= (verantwortlicher
                                        .getBaseId().equals(_alarmVO
                                        .getAlarmPersonId()));
                    }

                    if (!bAusloeserIstVerantwortlicher)
                    {
                        if (ausloeserPersonVO != null)
                        {
                            // Alarm wurde manuell von einem Veranwortlichen
                            // ausgeloest
                            // der nicht explizit als Schleifenverantwortlicher
                            // eingetragen ist
                            alVerantwortliche.add(ausloeserPersonVO);
                        }
                    }
                    String nachricht = (String) hmReportNachricht
                                    .get(schleifeVO);
                    SmsOutTAO smsOutTAO = taoFactory.getSmsOutTAO();

                    String context_alarm = StringUtils.intToHexString(_alarmVO
                                    .getReihenfolge());

                    OrganisationsEinheitVO oeVO = oeDAO
                                    .findOrganisationsEinheitById(schleifeVO
                                                    .getOrganisationsEinheitId());
                    OrganisationVO oVO = oDAO.findOrganisationById(oeVO
                                    .getOrganisationId());

                    String context_o = oVO.getName();
                    String context_oe = oeVO.getName();

                    for (int j = 0; j < alVerantwortliche.size(); j++)
                    {
                        PersonVO personVO = (PersonVO) alVerantwortliche.get(j);

                        smsOutTAO.sendeNachrichtAnPerson(
                                        personVO.getPersonId(), nachricht,
                                        "Schleifenreport", context_alarm,
                                        context_o, context_oe);

                        log.debug("Schleifenreport an Verantwortlichen \""
                                        + personVO.getName() + ","
                                        + personVO.getVorname()
                                        + "\" verschickt.");
                    }
                } // if Statusreport senden
                else
                {
                    log.debug("Fuer die Schleife \""
                                    + schleifeVO.getKuerzel()
                                    + "\" wird keine Schleifenreport-SMS versendet.");
                }
            }

            log.debug("Fuer den Alarm id=" + _alarmVO.getAlarmId() + " ("
                            + alarmSchleifen.length + " Schleifen) haben sich "
                            + gesamt.getNumJa() + " mit \"Ja\", "
                            + gesamt.getNumNein() + " mit \"Nein\", "
                            + gesamt.getNumUnbekannt()
                            + " Personen gar nicht zurueckgemeldet.");
            commit();

            // Deaktivierten Alarm laden
            AlarmVO deaktiviertesAlarmVO = alarmDAO.findAlarmById(alarmId);

            getAlarmInterceptor()
                            .intercept(AlarmInterceptorActionType.AFTER_ALARM_DEAKTIVIEREN,
                                            deaktiviertesAlarmVO);
        }
        catch (StdException e)
        {
            log.error(e.getMessage());
            rollback();
        }
    }

    @Override
    public void alarmEntwarnung(AlarmVO _alarmVO, PersonInAlarmVO[] _piaVOs) throws StdException
    {
        try
        {
            begin();

            getAlarmInterceptor().intercept(
                            AlarmInterceptorActionType.BEFORE_ALARM_ENTWARNEN,
                            _alarmVO);

            AlarmDAO alarmDAO = dbresource.getDaoFactory().getAlarmDAO();

            AlarmId alarmId = _alarmVO.getAlarmId();

            // Alle Bereiche deaktivieren
            alarmDAO.deaktivereAlleBereicheInAlarm(alarmId);

            // Alarmdeaktivierung in Datenbank schreiben
            alarmDAO.deaktiviereAlarmById(alarmId);

            personenEntwarnen(_alarmVO, _piaVOs);

            commit();

            getAlarmInterceptor().intercept(
                            AlarmInterceptorActionType.AFTER_ALARM_ENTWARNEN,
                            alarmDAO.findAlarmById(alarmId));
        }
        catch (StdException e)
        {
            log.error(e.getMessage());
            rollback();
        }
    }

    /**
     * Aktualisiert die Alarm-Statistik
     * 
     * @param _alarmVO
     * @param _piaVOs
     * @param _ret_gesamt
     */
    protected void alarmStatistik(AlarmVO _alarmVO, PersonInAlarmVO[] _piaVOs,
                    RueckmeldeStatistik _ret_gesamt)
    {
        for (int i = 0, m = _piaVOs.length; i < m; i++)
        {
            RueckmeldungStatusId rsId = _piaVOs[i].getRueckmeldungStatusId();

            long statusId = -1;

            if (rsId != null)
            {
                statusId = rsId.getLongValue();
            }

            if (statusId == RueckmeldungStatusId.STATUS_JA)
            {
                _ret_gesamt.incJa();
            }
            else if ((statusId == RueckmeldungStatusId.STATUS_NEIN)
                            || (statusId == RueckmeldungStatusId.STATUS_SPAETER))
            {
                _ret_gesamt.incNein();
            }
            else
            {
                _ret_gesamt.incUnbekannt();
            }
            // SPAETER ist fuer das Klinikum nicht noetig
        }
    }

    /**
     * Wird ausgefuehrt, sobald das Timeout innerhalb eines Alarms abgelaufen
     * ist.<br />
     * <ul>
     * <li>Es werden nur die Personen per SMS entwarnt, die sich *nicht* mit
     * "Ja" zurueckgemeldet haben</li>
     * </ul>
     * <br />
     * Diese Methode ist <b>transaktionssicher</b>
     * 
     * @param _bereichInSchleifeVO
     * @param _alarmVO
     * @param _statistik
     * @throws StdException
     */
    protected void bereichInAlarmDeaktivieren(
                    BereichInSchleifeVO _bereichInSchleifeVO, AlarmVO _alarmVO,
                    FunktionstraegerBereichRueckmeldung _statistik) throws StdException
    {
        try
        {
            begin();

            AlarmDAO alarmDAO = dbresource.getDaoFactory().getAlarmDAO();
            alarmDAO.deaktiviereBereichInAlarm(_alarmVO.getAlarmId(),
                            _bereichInSchleifeVO.getBereichInSchleifeId());

            // Rueckmeldungen des Bereichs/Funktionstraeger laden
            List<PersonRueckmeldungCVO> alRueckmeldungen = _statistik
                            .findByFunktionstraegerIdAndBereichId(
                                            _bereichInSchleifeVO
                                                            .getFunktionstraegerId(),
                                            _bereichInSchleifeVO.getBereichId());

            ArrayList<PersonInAlarmVO> alPiaVO = new ArrayList<PersonInAlarmVO>();

            // Alle Benutzer, die *keinen* positiven Status zurueckgeliefert
            // haben,
            // den zu entwarnenden Personen hinzufuegen
            for (int i = 0, m = alRueckmeldungen.size(); i < m; i++)
            {
                PersonRueckmeldungCVO rueckmeldung = alRueckmeldungen.get(i);
                PersonInAlarmVO piaVO = rueckmeldung.getPersonInAlarmVO();
                RueckmeldungStatusId rsId = piaVO.getRueckmeldungStatusId();

                if ((rsId != null)
                                && (rsId.getLongValue() != RueckmeldungStatusId.STATUS_JA))
                {
                    alPiaVO.add(rueckmeldung.getPersonInAlarmVO());
                }
            }

            PersonInAlarmVO[] piaVOs = new PersonInAlarmVO[alPiaVO.size()];
            alPiaVO.toArray(piaVOs);

            personenEntwarnen(_alarmVO, piaVOs);

            int iPositiveRueckmeldungen = getPositiveRueckmeldungen(
                            _bereichInSchleifeVO.getBereichId(),
                            _bereichInSchleifeVO.getFunktionstraegerId(),
                            _statistik);

            log.debug("Kombination " + _bereichInSchleifeVO + " der Schleife "
                            + _bereichInSchleifeVO.getSchleifeId()
                            + " des Alarms " + _alarmVO.getReihenfolge()
                            + " wurde deaktiviert; positive Rueckmeldungen: "
                            + iPositiveRueckmeldungen
                            + ", keine oder negative Rueckmeldungen: "
                            + piaVOs.length);
            commit();
        }
        catch (StdException e)
        {
            rollback();
            throw e;
        }

    }

    /**
     * Alarmiert einen Bereich nach, falls in der _originalSchleife sich der
     * Bereich nicht vollstaendig zurueckgemeldet hat
     * <ul>
     * <li>Falls Folgeschleife dem Alarm noch nicht hinzugefuegt wurde:
     * Folgeschleife hinzufuegen</li>
     * <li>Nachzualarmierenden Bereich in den Alarm aufnehmen</li>
     * <li>Personen der Folgeschleife alarmieren</li>
     * <li>Personen, die sich in der Originalschleife nicht gemeldet haben,
     * bekommen eine De-Alarmierung</li>
     * </ul>
     * Auf jeden Fall deaktiviert die Methode den übergebenen Schleifen-Bereich,
     * dies geschieht ueber
     * {@link #bereichInAlarmDeaktivieren(BereichInSchleifeVO, AlarmVO, FunktionstraegerBereichRueckmeldung)}
     * <br />
     * Aufrufe in dieser Methode sind <strong>nicht</strong>
     * transaktionsgesichert!
     * 
     * @param _alarmVO
     * @param _originalSchleife
     * @param _originalBereichInSchleifeVO
     * @return
     */
    protected STATUS_BEREICH_NACHALARMIEREN bereichNachalarmieren(
                    AlarmVO _alarmVO, SchleifeVO _originalSchleife,
                    BereichInSchleifeVO _originalBereichInSchleifeVO,
                    FunktionstraegerBereichRueckmeldung _statistik) throws StdException
    {
        STATUS_BEREICH_NACHALARMIEREN r = null;

        SchleifenDAO daoSchleife = daoFactory.getSchleifenDAO();
        OrganisationDAO daoOrganisation = daoFactory.getOrganisationDAO();
        OrganisationsEinheitDAO daoOrganisationsEinheit = daoFactory
                        .getOrganisationsEinheitDAO();

        BereichInSchleifeDAO daoBereichInSchleife = daoFactory
                        .getBereichInSchleifeDAO();

        SchleifeVO folgeschleifeVO = null;
        BereichInSchleifeVO nextBereichInSchleifeVO = null;

        if (_originalSchleife.getFolgeschleifeId() != null)
        {
            folgeschleifeVO = daoSchleife.findSchleifeById(_originalSchleife
                            .getFolgeschleifeId());

            if (folgeschleifeVO != null)
            {
                BereichInSchleifeVO[] bereicheInFolgeschleife = daoBereichInSchleife
                                .findBereicheInSchleifeBySchleifeId(folgeschleifeVO
                                                .getSchleifeId());

                // Den naechsten Bereich innerhalb einer Schleife finden
                for (int i = 0, m = bereicheInFolgeschleife.length; (i < m)
                                && (nextBereichInSchleifeVO == null); i++)
                {
                    BereichInSchleifeVO potentiellNaechsterBereich = bereicheInFolgeschleife[i];

                    // Die Bereich/Schleifen-Kombination der Folgeschleife ist
                    // identisch mit der Original-Schleife
                    if (_originalBereichInSchleifeVO.getBereichId().equals(
                                    potentiellNaechsterBereich.getBereichId())
                                    && _originalBereichInSchleifeVO
                                                    .getFunktionstraegerId()
                                                    .equals(potentiellNaechsterBereich
                                                                    .getFunktionstraegerId()))
                    {
                        nextBereichInSchleifeVO = potentiellNaechsterBereich;
                    }
                } // for potentielle Folgebereiche
            } // if Folgeschleife gefunden
            else
            {
                log.error("Die Folgeschleife der Schleife " + _originalSchleife
                                + " konnte nicht gefunden werden");
                r = STATUS_BEREICH_NACHALARMIEREN.FOLGESCHLEIFE_NICHT_GEFUNDEN;
            } // if Folgeschleife nicht gefunden
        } // if Folgeschleife definiert
        else
        {
            log.error("Die Bereich/Funktionstraeger-Kombination der Schleife "
                            + _originalSchleife
                            + " konnte nicht nachalarmiert werden, da keine Folgeschleife hinterlegt ist");
            r = STATUS_BEREICH_NACHALARMIEREN.FOLGESCHLEIFE_NICHT_DEFINIERT;
        }

        // Auf jeden Fall muss der aktive Bereich des Alarms deaktiviert
        // und alle Personen, die ***nicht*** mit "Ja" geantwortet haben,
        // entwarnt
        // werden
        bereichInAlarmDeaktivieren(_originalBereichInSchleifeVO, _alarmVO,
                        _statistik);

        // Es wurde kein Folge-Bereich gefunden
        if (nextBereichInSchleifeVO == null)
        {
            log.error("Es konnte kein Folge-Bereich fuer die Schleife "
                            + _originalSchleife.getDisplayName()
                            + " gefunden werden");

            // Wenn Status-Code nicht gesetzt ist, wurde die Folgeschleife nur
            // nicht gefunden
            if (r == null)
            {
                return STATUS_BEREICH_NACHALARMIEREN.FOLGEBEREICH_NICHT_GEFUNDEN;
            }

            return r;
        }

        OrganisationsEinheitVO oeVO = daoOrganisationsEinheit
                        .findOrganisationsEinheitById(_originalSchleife
                                        .getOrganisationsEinheitId());
        OrganisationVO oVO = daoOrganisation.findOrganisationById(oeVO
                        .getOrganisationId());

        log.info("Nachalarmierung der Kombination "
                        + _originalBereichInSchleifeVO.toString()
                        + " der Schleife " + _originalSchleife.getName()
                        + ". Folgeschleife ist " + folgeschleifeVO.getName());

        // Bereich der nächsten Schleife alarmieren
        int anzahlNachalarmiertePersonen = funktionstraegerBereichAlarmieren(
                        folgeschleifeVO, nextBereichInSchleifeVO, _alarmVO,
                        _alarmVO.getKommentar(), UnixTime.now(), oVO.getName(),
                        oeVO.getName());

        log.info("FBK " + nextBereichInSchleifeVO + " wurde mit ["
                        + anzahlNachalarmiertePersonen
                        + "] Personen nachalarmiert");

        return STATUS_BEREICH_NACHALARMIEREN.FOLGEBEREICH_ALARMIERT;
    }

    /**
     * Findet die Schleifen, die in einem Alarm auch wirklich alarmierbar.<br />
     * Eine Schleife ist alarmierbar, wenn
     * <ul>
     * <li>nicht aktiv ist</li>
     * <li>nicht ausgeschlossen ist</li>
     * </ul>
     * 
     * Ist eine Schleife nicht alarmierbar, wird versucht, die nächste freie
     * Folgeschleife zu alarmieren.
     * 
     * @param _schleifeVO
     * @param _schleifenAusschliessen
     *            Liste mit den Schleifen, die ausgelassen werden soll; es kann
     *            auch null als Parameter übergeben werden
     * @return
     * @throws StdException
     */
    public SchleifeVO findAlarmierbareSchleife(SchleifeVO _schleifeVO,
                    List<SchleifeId> _schleifenAusschliessen) throws StdException
    {
        if (_schleifenAusschliessen == null)
        {
            _schleifenAusschliessen = new ArrayList<SchleifeId>();
        }

        do
        {
            // Schleife ist nicht von der Suche ausgeschlossen (zirkulare
            // Abhaengigkeit zwischen den einzelnen Schleifen)
            if (!_schleifenAusschliessen.contains(_schleifeVO.getSchleifeId()))
            {
                // Wenn die Schleife nicht aktiv ist, kann sie ausgel�st werden
                if (!daoFactory.getAlarmDAO().isSchleifeAktiv(
                                _schleifeVO.getSchleifeId()))
                {
                    log.debug("Die Schleife kuerzel=\""
                                    + _schleifeVO.getKuerzel()
                                    + "\" ist inaktiv und kann ausgeloest werden");

                    return _schleifeVO;
                }
                else
                {
                    log.error("Die Schleife kuerzel=\""
                                    + _schleifeVO.getKuerzel()
                                    + "\" ist bereits ausgeloest!");
                }
            }
            // if Schleife != ausgeschlossen
            else
            {
                log.error("Die Schleife kuerzel=\"" + _schleifeVO.getKuerzel()
                                + "\" wurde fuer diesen Alarm ausgeschlossen");
            }

            log.info("Suche Folgeschleife fuer Schleife mit kuerzel=\""
                            + _schleifeVO.getKuerzel() + "\"");

            SchleifeId folgeschleifeId = _schleifeVO.getFolgeschleifeId();

            // Es existiert keine Folgeschleife => Fehler
            if (folgeschleifeId == null)
            {
                log.error("Die Schleife kuerzel=\""
                                + _schleifeVO.getKuerzel()
                                + "\" besitzt keine Folgeschleife - kann keine Alarmierung durchfuehren!");

                return null;
            }

            _schleifeVO = daoFactory.getSchleifenDAO().findSchleifeById(
                            folgeschleifeId);

            // Es konnte keine Folgeschleife mit der angegebenen ID gefunden
            // werden
            if (_schleifeVO == null)
            {
                log.error("Die Folgeschleife mit der ID \"" + folgeschleifeId
                                + "\" konnte nicht gefunden werden!");
            }
        }
        while (_schleifeVO != null);

        return null;
    }

    /**
     * Findet die Schleife(n), die als nächstes alarmierbar sind. Es werden
     * dabei doppelte Schleifen-Einträge herausgefiltert, so dass wir in keinen
     * Abhängigkeitskreis gelangen.
     * 
     * @param _schleifen
     * @return
     */
    public List<SchleifeVO> findAuszuloesendeSchleifen(SchleifeVO[] _schleifen) throws StdException
    {
        StringBuffer sb = new StringBuffer();

        List<SchleifeVO> r = new ArrayList<SchleifeVO>();
        List<SchleifeId> schleifenInBenutzung = new ArrayList<SchleifeId>();

        // Alle Schleifen ausfiltern, die bereits ausgeloest sind
        for (int i = 0; i < _schleifen.length; i++)
        {
            SchleifeVO schleifeVO = _schleifen[i];

            sb.append(schleifeVO.getKuerzel());
            sb.append("=");

            if (!r.contains(schleifeVO))
            {
                SchleifeVO schleifeZumAusloesen = findAlarmierbareSchleife(
                                schleifeVO, schleifenInBenutzung);

                if (schleifeZumAusloesen != null)
                {
                    sb.append(schleifeZumAusloesen.getKuerzel());
                    r.add(schleifeZumAusloesen);
                    schleifenInBenutzung.add(schleifeZumAusloesen
                                    .getSchleifeId());
                }
                else
                {
                    sb.append("---");
                }
            }
            else
            {
                sb.append(schleifeVO.getKuerzel());
                sb.append(" (bereits ausgeloest)");
            }

            if ((i + 1) != _schleifen.length)
            {
                sb.append(", ");
            }
        }

        log.info("Schleifen zum Auloesen (Soll=Ist): " + sb.toString());

        return r;
    };

    /**
     * Alarmiert die Kombination Funktionstraeger/Bereich innerhalb einer
     * Schleife.<br />
     * 
     * Ablauf
     * <ul>
     * <li>Bereich dem Alarm innerhalb der Datenbank hinzufuegen
     * {@link Scheme#BEREICH_IN_ALARM_TABLE}</li>
     * <li>Personen mit dem Recht
     * {@link RechtId#ALARMBENACHRICHTIGUNG_EMPFANGEN} in der betreffenden
     * Schleife finden</li>
     * <li>Funktionstraeger-/Bereichskombination der Benutzer mit
     * _bereichInSchleifeVO ueberpruefen: dies sind die zu alarmierenden
     * Benutzer; die Überprüfung geschieht über
     * {@link #findPersonenMitEmpfangsberechtigungBySchleifeAndBereichAndFunktionstraeger(SchleifeId, BereichId, FunktionstraegerId)}
     * </li>
     * <li>Eintragen der SMSen in die Versand-Queue über
     * {@link #personenAlarmieren(SchleifeVO, AlarmVO, String, UnixTime, String, String)}
     * </li>
     * </ul>
     * <br />
     * Ist die Schleife der BereichInSchleife noch nicht in die Datenbank
     * eingetragen, geschieht das nun.
     * 
     * <br />
     * Aufrufe in dieser Methode sind <strong>nicht</strong>
     * transaktionsgesichert!
     * 
     * @param _schleifeVO
     *            Schleife, in der sich der Bereich/Funktionstraeger befindet
     * @param _bereichInSchleifeVO
     *            Bereich/Funktionstraeger-Kombination
     * @param _alarmVO
     *            Alarm-Objekt
     * @param _zusatzText
     *            Zusatztext
     * @param _alarmZeit
     *            Alarm-Zeit
     * @param _kontextO
     * @param _kontextOE
     * @return Anzahl der Personen, die alarmiert wurden
     * @throws StdException
     */
    protected int funktionstraegerBereichAlarmieren(SchleifeVO _schleifeVO,
                    BereichInSchleifeVO _bereichInSchleifeVO, AlarmVO _alarmVO,
                    String _zusatzText, UnixTime _alarmZeit, String _kontextO,
                    String _kontextOE) throws StdException
    {
        int numPersons = 0;

        try
        {
            log.info("Versuche " + _bereichInSchleifeVO + " zu alarmieren");

            AlarmDAO alarmDAO = daoFactory.getAlarmDAO();
            SchleifeBO schleifeBO = dbresource.getBoFactory().getSchleifeBO();

            if (!alarmDAO.isSchleifeInAlarm(_schleifeVO.getSchleifeId(),
                            _alarmVO.getAlarmId()))
            {
                // Schleife hinzufügen
                alarmDAO.addSchleifeToAlarm(_schleifeVO.getSchleifeId(),
                                _alarmVO.getAlarmId());

                log.info("Die Schleife [" + _schleifeVO.getKuerzel() + "] ["
                                + _schleifeVO.getSchleifeId()
                                + "] wurde dem Alarm \""
                                + _alarmVO.getReihenfolge()
                                + "\" nachtraeglich zugeordnet.");
            }

            if (alarmDAO.isBereichInSchleifeInAlarmAktiv(_alarmVO.getAlarmId(),
                            _bereichInSchleifeVO.getBereichInSchleifeId()))
            {
                log.error("Die Kombination Funktionstraeger/Bereich der Schleife "
                                + _schleifeVO.getKuerzel()
                                + " ist bereits aktiv. Der Funktionstraeger/Bereich der Schleife kann nicht alarmiert werden");
                return 0;
            }

            daoFactory.getAlarmDAO().addBereichInAlarm(_alarmVO.getAlarmId(),
                            _bereichInSchleifeVO.getBereichInSchleifeId(),
                            _alarmZeit);

            PersonVO[] personen = schleifeBO
                            .findPersonenMitEmpfangsberechtigungBySchleifeAndBereichAndFunktionstraeger(
                                            _schleifeVO.getSchleifeId(),
                                            _bereichInSchleifeVO.getBereichId(),
                                            _bereichInSchleifeVO
                                                            .getFunktionstraegerId());

            numPersons = personenAlarmieren(_schleifeVO, _alarmVO, _zusatzText,
                            _alarmZeit, _kontextO, _kontextOE, personen);
        }
        catch (StdException e)
        {
            log.error(e.getMessage());
        }

        return numPersons;
    }

    public String generateRueckmeldungReport(AlarmVO alarmVO,
                    SchleifeVO _schleifeVO,
                    RueckmeldeStatistik _rueckmeldeStatistik)
    {
        String r = getSmsContent().resolveRueckmeldeStatistik(
                        SmsContent.RUECKMELDUNG_REPORT_KLINIKUM, alarmVO,
                        _schleifeVO, _rueckmeldeStatistik, null);
        return r;
    }

    /**
     * Liefert den Interceptor für die Alarme zurück
     * 
     * @return
     */
    final public IAlarmInterceptor getAlarmInterceptor()
    {
        return alarmInterceptor;
    }

    /**
     * Berechnet anhand der übergebenen Werte die Anzahl der Personen innerhalb
     * einer Funktionsträger/Bereichs-Kombination, die mit "Ja" geantwortet
     * haben.
     * 
     * @param _bereichId
     * @param _funktionstraegerId
     * @param _fbr
     * @return
     */
    public int getPositiveRueckmeldungen(BereichId _bereichId,
                    FunktionstraegerId _funktionstraegerId,
                    FunktionstraegerBereichRueckmeldung _fbr)
    {
        int iPositiv = 0;

        if (_fbr.findByFunktionstraegerId(_funktionstraegerId) != null)
        {
            List<PersonRueckmeldungCVO> rueckmeldung = _fbr
                            .findByFunktionstraegerIdAndBereichId(
                                            _funktionstraegerId, _bereichId);

            if (rueckmeldung != null)
            {
                for (int i = 0, m = rueckmeldung.size(); i < m; i++)
                {
                    PersonInAlarmVO piaVO = rueckmeldung.get(i)
                                    .getPersonInAlarmVO();

                    if (piaVO != null
                                    && piaVO.getRueckmeldungStatusId() != null)
                    {
                        if (piaVO.getRueckmeldungStatusId().getLongValue() == RueckmeldungStatusId.STATUS_JA)
                        {
                            iPositiv++;
                        } // status ja
                    } // rueckmeldungStatus valid
                } // for rueckmeldung
            } // funktionstraeger && bereich != null
        } // funktionstraeger != null

        return iPositiv;
    }

    /**
     * Liefert zurueck, ob aus den uebergebenen Statistik-Daten genug Personen
     * vorhanden sind, so dass die Sollstaerke erreicht bzw. ueberschritten
     * worden ist.<br />
     * Die Auswertung auf Schleifenebene spielt keine Rolle, da eine Person nur
     * spezifisch für einen Alarm sagen kann, ob sie erscheint oder nicht.<br />
     * <ul>
     * <li>Person P1 existiert in Schleife S1 und S2 mit Bereich B1 und
     * Funktionsträger F2</li>
     * <li>Alarm wird ausgelöst, Schleife S1 und S2 werden alarmiert</li>
     * <li>Person P1 meldet sich mit "JA" zurück</li>
     * <li>ZABOS kann <strong>nicht</strong> auswerten, welche Schleife gemeint
     * ist. Somit haben sowohl Schleife S1 und S2 eine positive Rückmeldung
     * mehr!</li>
     * </ul>
     * 
     * @param _sollstaerkeNeeded
     *            Die Sollstärke muss explizit angegeben werden, so dass die
     *            Benutzung von Referenz-Sollstärken (Vorgänge-/Folgeschleifen)
     *            benutzt werden kann.
     * @param _bereichInSchleifeVO
     * @param _statistik
     * @return
     */
    public boolean isSollstaerkeErreicht(int _sollstaerkeNeeded,
                    BereichInSchleifeVO _bereichInSchleifeVO,
                    FunktionstraegerBereichRueckmeldung _statistik)
    {
        int iPositiveRueckmeldung = getPositiveRueckmeldungen(
                        _bereichInSchleifeVO.getBereichId(),
                        _bereichInSchleifeVO.getFunktionstraegerId(),
                        _statistik);
        return (iPositiveRueckmeldung >= _sollstaerkeNeeded);
    }

    /**
     * Überprüft, ob das Timeout für einen Bereich erreicht ist.
     * <ul>
     * <li>Laden der Schleife des Bereiches</li>
     * <li>Laden des Bereiches innerhalb des Alarms</li>
     * <li>Ist Schleifen-Timeout + Beginn der Bereichsalarmierung < jetzt?
     * <ul>
     * <li>Ja: Bereich hat den Timeout erreicht</li>
     * <li>Nein: Bereich hat den Timeout noch nicht erreicht</li>
     * </ul>
     * </li>
     * 
     * @param _bereichInSchleifeVO
     * @param _alarmVO
     * @return
     */
    public boolean isTimeoutFuerBereichErreicht(
                    BereichInSchleifeVO _bereichInSchleifeVO, AlarmVO _alarmVO) throws StdException
    {
        SchleifenDAO daoSchleife = dbresource.getDaoFactory().getSchleifenDAO();
        AlarmDAO daoAlarm = dbresource.getDaoFactory().getAlarmDAO();

        UnixTime startZeit = daoAlarm.findBereichsAlarmAktivierung(
                        _alarmVO.getAlarmId(),
                        _bereichInSchleifeVO.getBereichInSchleifeId());

        SchleifeVO schleifeVO = daoSchleife
                        .findSchleifeById(_bereichInSchleifeVO.getSchleifeId());

        long timeoutMs = 1000 * schleifeVO.getRueckmeldeintervall();

        UnixTime endZeit = new UnixTime(timeoutMs);
        endZeit.add(startZeit);

        return !(UnixTime.now().isBetween(startZeit, endZeit));
    }

    /**
     * Alarmiert die übergebenen Personen.<br />
     * Aufrufe in dieser Methode sind <strong>nicht</strong>
     * transaktionsgesichert!
     * 
     * @param _schleifeVO
     * @param _alarmVO
     * @param _zusatzText
     * @param _alarmZeit
     * @param _kontextO
     * @param _kontextOE
     * @param personen
     * @return
     * @throws StdException
     */
    protected int personenAlarmieren(SchleifeVO _schleifeVO, AlarmVO _alarmVO,
                    String _zusatzText, UnixTime _alarmZeit, String _kontextO,
                    String _kontextOE, PersonVO[] personen) throws StdException
    {
        int numPersons = 0;

        if (personen != null && personen.length > 0)
        {
            try
            {
                // Key ist PersonId, Value ist AlarmId
                HashMap<PersonId, AlarmId> hmPersonInAlarm = new HashMap<PersonId, AlarmId>();
                AlarmId alarmId = _alarmVO.getAlarmId();

                // Nachrichtentext erstellen
                String smsNachricht = getSmsContent()
                                .resolveBestMatchingContentForAlarm(_alarmVO,
                                                _schleifeVO, _zusatzText, "");

                log.debug("smsnachricht=\"" + smsNachricht + "\"");

                // Zuweisung der Schleifen-Personen zum Alarm
                for (int j = 0; j < personen.length; j++)
                {
                    PersonVO personVO = personen[j];

                    PersonId personId = personVO.getPersonId();

                    // Person wurde weder während des Methodenaufrufs noch
                    // während des Alarms alarmiert
                    if (!hmPersonInAlarm.containsKey(personId)
                                    && !daoFactory.getAlarmDAO()
                                                    .isPersonInAlarm(personId,
                                                                    alarmId))
                    {
                        // Wer den Alarm ausloest muss sich nicht
                        // zurueckmelden
                        if (!personId.equals(_alarmVO.getAlarmPersonId()))
                        {
                            if (personVO.isAnwesend(_alarmZeit))
                            {

                                TelefonVO[] telefonVOs = daoFactory
                                                .getTelefonDAO()
                                                .findAktiveTelefoneByPersonId(
                                                                personId);

                                if (telefonVOs.length == 0)
                                {
                                    log.error("der Person id="
                                                    + personId
                                                    + " name="
                                                    + personen[j].getName()
                                                    + " ist kein Telefon zugewiesen!");
                                }
                                else
                                {
                                    hmPersonInAlarm.put(personId,
                                                    _alarmVO.getAlarmId());
                                    daoFactory.getAlarmDAO().addPersonToAlarm(
                                                    personId,
                                                    _alarmVO.getAlarmId());

                                    SmsOutDAO smsOutDAO = daoFactory
                                                    .getSmsOutDAO();

                                    String context_alarm = StringUtils
                                                    .intToHexString(_alarmVO
                                                                    .getReihenfolge());

                                    for (int k = 0; k < telefonVOs.length; k++)
                                    {
                                        TelefonVO telefonVO = telefonVOs[k];

                                        // Alarmbenachrichtung an
                                        // Einsatzkraft verschicken
                                        SmsOutVO smsOutVO = smsOutDAO
                                                        .getObjectFactory()
                                                        .createSmsOut();
                                        smsOutVO.setTelefonId(telefonVO
                                                        .getTelefonId());
                                        smsOutVO.setNachricht(smsNachricht);
                                        smsOutVO.setZeitpunkt(UnixTime.now());
                                        smsOutVO.setContext("Alarmierung");
                                        smsOutVO.setContextAlarm(context_alarm); // 2006-05-17
                                        // CST: Issue
                                        // #253
                                        smsOutVO.setContextO(_kontextO);
                                        smsOutVO.setContextOE(_kontextOE);

                                        // TODO Refactoring, da identischer Code
                                        // an
                                        // anderer Stelle vorhanden
                                        smsOutVO.setFestnetzSms(telefonVO
                                                        .getNummer()
                                                        .isFestnetzNummer());

                                        smsOutVO = smsOutDAO
                                                        .createSmsOut(smsOutVO);

                                        smsOutDAO.addSmsOutToSchleifeInAlarm(
                                                        smsOutVO.getSmsOutId(),
                                                        _schleifeVO.getSchleifeId(),
                                                        _alarmVO.getAlarmId());
                                    }
                                    numPersons++;
                                }
                            }
                            else
                            {
                                log.debug("Person \""
                                                + personVO.getName()
                                                + "\" ist abwesend und erhaelt keine Alarmbenachrichtigung");
                            }
                        } // if person bereits alarmiert
                    } // if person != alarmAusloesender
                } // for personen
            }
            catch (StdException e)
            {
                log.error(e.getMessage());
            }
        }

        return numPersons;
    }

    /**
     * Schickt eine SMS zur Entwarnung
     * 
     * @param _alarmVO
     * @param _piaVOs
     * @throws StdException
     */
    protected void personenEntwarnen(AlarmVO _alarmVO, PersonInAlarmVO[] _piaVOs) throws StdException
    {
        Map<String, Object> smsContentArgs = new HashMap<String, Object>();
        smsContentArgs.put(SmsContent.PARAM_ALARM, _alarmVO);
        String nachricht = getSmsContent().resolve(SmsContent.ALARM_ENTWARNUNG,
                        smsContentArgs);

        // Alle Personen in diesem Alarm benachrichtigen
        for (int i = 0, m = _piaVOs.length; i < m; i++)
        {
            PersonInAlarmVO piaVO = _piaVOs[i];

            // Die Benutzer, die bereits von anderen entwarnt worden
            // sind, müssen nicht noch einmal entwarnt werden. Das führt
            // zu doppelten SMSen
            if (!piaVO.isEntwarnt())
            {

                SmsOutTAO smsOutTAO = taoFactory.getSmsOutTAO();

                String context_alarm = StringUtils.intToHexString(_alarmVO
                                .getReihenfolge());

                // TODO CKL: Wem wird diese SMS in Rechnung gestellt?
                boolean isGesendet = smsOutTAO.sendeNachrichtAnPerson(
                                piaVO.getPersonId(), nachricht, "Entwarnung",
                                context_alarm, null, null);

                smsOutTAO.markPersonAsEntwarnt(_alarmVO.getAlarmId(),
                                piaVO.getPersonId());

                if (isGesendet)
                {
                    log.debug("Entwarnung an Person mit Id \""
                                    + piaVO.getPersonId() + "\" verschickt");
                }
            }
            else
            {
                log.debug("Die Person ["
                                + piaVO.getPersonId()
                                + "] muss fuer den Alarm ["
                                + _alarmVO.getAlarmId()
                                + "] nicht mehr entwarnt werden, da sie dies bereits vorher schon wurde");
            }
        }
    }

    /**
     * Folgender Ablauf:
     * <ul>
     * <li>Jeden aktiven Bereich des Alarms überprüfen
     * </ul>
     * <li>Ist der Timeout des Bereiches erreicht?
     * <ul>
     * <li>Ja: Wurde die Sollstärke im Bereich erreicht?
     * <ul>
     * <li>Ja: Bereich deaktivieren</li>
     * <li>Nein: Existiert eine Folgeschleife?
     * <ul>
     * <li>Ja: Funktionsträger/Bereich der Folgeschleife alarmieren</li>
     * <li>Nein: Nichts passiert - Alarm kann deaktiviert werden</li>
     * </ul>
     * </li>
     * </ul>
     * </li>
     * <li>Nein: Wurde die Sollstärke im Bereich erreicht?
     * <ul>
     * <li>Ja: Bereich deaktivieren</li>
     * <li>Nein: Nichts passiert</li>
     * </ul>
     * </li>
     * </ul>
     * </li> </ul>
     */
    public void processAktiveAlarme()
    {
        try
        {
            begin();

            AlarmDAO alarmDAO = dbresource.getDaoFactory().getAlarmDAO();
            SchleifenDAO schleifenDAO = dbresource.getDaoFactory()
                            .getSchleifenDAO();
            BereichInSchleifeDAO bereichInSchleifeDAO = dbresource
                            .getDaoFactory().getBereichInSchleifeDAO();

            PersonInAlarmDAO personInAlarmDAO = dbresource.getDaoFactory()
                            .getPersonInAlarmDAO();

            AlarmVO[] alarmVOs = alarmDAO.findAktiveAlarme();

            // Alle aktiven Alarme verarbeiten
            for (int i = 0; i < alarmVOs.length; i++)
            {
                AlarmVO alarmVO = alarmVOs[i];
                AlarmId alarmId = alarmVO.getAlarmId();

                BereichInSchleifeVO[] aktiveBereicheInSchleife = bereichInSchleifeDAO
                                .findAktiveBereicheInSchleifeInAlarmByAlarmId(alarmId);

                int iSollstaerkeInBereichenErreicht = 0;
                int iSollstaerkeInBereichenNichtErreicht = 0;
                int iAlarmierteBereicheInAlarm = aktiveBereicheInSchleife.length;

                // Alle Bereiche, die in einem Alarm aktiv sind, ueberpruefen
                for (int j = 0, n = aktiveBereicheInSchleife.length; j < n; j++)
                {
                    BereichInSchleifeVO aktiverBereichInSchleife = aktiveBereicheInSchleife[j];

                    PersonInAlarmVO[] piaVO = personInAlarmDAO
                                    .findPersonenInAlarmByAlarmIdAndSchleifeId(
                                                    alarmId,
                                                    aktiverBereichInSchleife
                                                                    .getSchleifeId());

                    FunktionstraegerBereichRueckmeldung _statistik = FunktionstraegerBereichRueckmeldung
                                    .buildPersonenRueckmeldung(piaVO,
                                                    daoFactory.getPersonDAO());

                    int sollstaerkeNeeded = findReferenceSollstaerke(alarmId,
                                    aktiverBereichInSchleife);

                    // Sollstaerke in diesem Bereich ist erreicht => Alarm
                    // deaktivieren
                    if (isSollstaerkeErreicht(sollstaerkeNeeded,
                                    aktiverBereichInSchleife, _statistik))
                    {
                        log.info("Sollstaerke ["
                                        + sollstaerkeNeeded
                                        + "] fuer "
                                        + aktiverBereichInSchleife
                                        + " ist erreicht - deaktiviere diesen Bereich");
                        bereichInAlarmDeaktivieren(aktiverBereichInSchleife,
                                        alarmVO, _statistik);
                        iSollstaerkeInBereichenErreicht++;
                    } // Sollstaerke wurde erreicht
                      // Sollstaerke ist noch nicht erreicht
                    else
                    {
                        // Ist das Timeout fuer den Alarm abgelaufen?
                        if (isTimeoutFuerBereichErreicht(
                                        aktiverBereichInSchleife, alarmVO))
                        {
                            log.warn("Sollstaerke ["
                                            + sollstaerkeNeeded
                                            + "] fuer "
                                            + aktiverBereichInSchleife
                                            + " ist *nach* Ablauf des Timeouts nicht erreicht!");

                            // Existiert eine Folgeschleife?
                            SchleifeVO schleifeVO = schleifenDAO
                                            .findSchleifeById(aktiverBereichInSchleife
                                                            .getSchleifeId());

                            if (schleifeVO.getFolgeschleifeId() != null)
                            {
                                bereichNachalarmieren(alarmVO, schleifeVO,
                                                aktiverBereichInSchleife,
                                                _statistik);
                                iAlarmierteBereicheInAlarm++;
                            }
                            // Folgeschleife existiert nicht, somit muss der
                            // Bereich deaktiviert werden
                            else
                            {
                                bereichInAlarmDeaktivieren(
                                                aktiverBereichInSchleife,
                                                alarmVO, _statistik);
                                iSollstaerkeInBereichenNichtErreicht++;
                            } // Bereich konnte nicht nachalarmiert werden:
                              // if Folgeschleife nicht existent
                        } // if Timeout erreicht
                    } // if Sollstaerke nicht erreicht
                } // for bereichInSchleifen

                // Die Sollstaerke wurde in allen Bereichen erreicht oder aber
                // es
                // koennen keine Bereiche mehr nachalarmiert werden
                if (iAlarmierteBereicheInAlarm <= (iSollstaerkeInBereichenErreicht + iSollstaerkeInBereichenNichtErreicht))
                {
                    PersonInAlarmVO[] pia = personInAlarmDAO
                                    .findPersonenInAlarmByAlarmId(alarmId);

                    FunktionstraegerBereichRueckmeldung _statistik = FunktionstraegerBereichRueckmeldung
                                    .buildPersonenRueckmeldung(pia, dbresource
                                                    .getDaoFactory()
                                                    .getPersonDAO());

                    log.info("Alarmierte Bereiche: "
                                    + iAlarmierteBereicheInAlarm
                                    + "; Sollstaerke in Bereichen erreicht: "
                                    + iSollstaerkeInBereichenErreicht
                                    + "; Sollstaerke in Bereichen *nicht* erreicht: "
                                    + iSollstaerkeInBereichenNichtErreicht
                                    + " => deaktivere Alarm");
                    alarmDeaktivieren(alarmVO, _statistik);
                } // Alarm deaktivieren
            }
            commit();
        }
        catch (StdException e)
        {
            log.error(e);
            rollback();
        }
    }

    /**
     * Setzt den Interceptor fuer die Alarme
     * 
     * @param alarmInterceptor
     */
    final public void setAlarmInterceptor(IAlarmInterceptor alarmInterceptor)
    {
        this.alarmInterceptor = alarmInterceptor;
    }

    public String toString()
    {
        return "Verarbeitung der Alarme fuer das Klinikum";
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.ecw.zabos.sql.tao.IAlarmTAO#zwischenStatistik(de.ecw.zabos.sql.vo.
     * AlarmVO)
     */
    public ZwischenStatistik zwischenStatistik(AlarmVO _alarmVO)
    {
        try
        {
            PersonInAlarmDAO personInAlarmDAO = dbresource.getDaoFactory()
                            .getPersonInAlarmDAO();

            PersonInAlarmVO[] piaVOs = personInAlarmDAO
                            .findPersonenInAlarmByAlarmId(_alarmVO.getAlarmId());

            ZwischenStatistik r = new ZwischenStatistik(piaVOs.length);

            alarmStatistik(_alarmVO, piaVOs, r.gesamt);

            return r;
        }
        catch (StdException e)
        {
            log.error(e);
            return null;
        }
    }
}
