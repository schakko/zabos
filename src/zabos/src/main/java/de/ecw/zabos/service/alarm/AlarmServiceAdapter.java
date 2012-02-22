package de.ecw.zabos.service.alarm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;

import de.ecw.zabos.alarm.FunktionstraegerStatistik;
import de.ecw.zabos.alarm.RueckmeldeStatistik;
import de.ecw.zabos.alarm.ZwischenStatistik;
import de.ecw.zabos.alarm.daemon.AlarmDaemon;
import de.ecw.zabos.broadcast.sms.SmsContent;
import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.sql.dao.AlarmDAO;
import de.ecw.zabos.sql.dao.FunktionstraegerDAO;
import de.ecw.zabos.sql.dao.OrganisationDAO;
import de.ecw.zabos.sql.dao.OrganisationsEinheitDAO;
import de.ecw.zabos.sql.dao.PersonDAO;
import de.ecw.zabos.sql.dao.PersonInAlarmDAO;
import de.ecw.zabos.sql.dao.SchleifenDAO;
import de.ecw.zabos.sql.dao.SmsOutDAO;
import de.ecw.zabos.sql.resource.DBResource;
import de.ecw.zabos.sql.tao.BaseTAO;
import de.ecw.zabos.sql.tao.SmsOutTAO;
import de.ecw.zabos.sql.vo.AlarmVO;
import de.ecw.zabos.sql.vo.FunktionstraegerVO;
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
import de.ecw.zabos.types.id.PersonId;
import de.ecw.zabos.types.id.RechtId;
import de.ecw.zabos.types.id.RueckmeldungStatusId;
import de.ecw.zabos.types.id.SchleifeId;
import de.ecw.zabos.util.StringUtils;

/**
 * Adapter-Klasse für die Standard-Alarmierung.
 * <ul>
 * <li>Benutzer löst Alarm aus (
 * {@link #alarmAusloesen(String, SchleifeVO[], AlarmQuelleId, PersonId, String, String)}
 * )</li>
 * <li> {@link AlarmDaemon} verarbeitet die aktiven Alarme (
 * {@link #processAktiveAlarme()})
 * <ul>
 * <li>Sobald der {@link SystemKonfigurationVO#getAlarmTimeout()} abgelaufen
 * ist, wird der Alarm deaktiviert (
 * {@link #alarmDeaktivieren(AlarmVO, PersonInAlarmVO[])})</li>
 * <li>Wenn der Benutzer den Alarm entwarnt (
 * {@link #alarmEntwarnung(AlarmVO, PersonInAlarmVO[])}) wird der Alarm
 * deaktiviert {@link #alarmDeaktivieren(AlarmVO, PersonInAlarmVO[])})</li>
 * </ul>
 * </li>
 * </ul>
 */
public class AlarmServiceAdapter extends BaseTAO implements IAlarmService
{
    private final static Logger log = Logger
                    .getLogger(AlarmServiceAdapter.class);

    /**
     * Internationalisierung der SMSen
     */
    private SmsContent smsContent;

    /**
     * Referenz auf die {@link SystemKonfigurationVO}
     */
    private SystemKonfigurationVO systemKonfiguration;

    /**
     * Konstruktor
     * 
     * @param _dbresource
     *            Unbedingt eine neue Datenbankinstanz übergeben, da diese
     *            Klasse gethreadet wird.
     * @param _systemKonfiguration
     * @param _smsContent
     */
    public AlarmServiceAdapter(final DBResource _dbresource,
                    SystemKonfigurationVO _systemKonfiguration,
                    SmsContent _smsContent)
    {
        super(_dbresource);
        setSystemKonfiguration(_systemKonfiguration);
        setSmsContent(_smsContent);
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.ecw.zabos.sql.tao.IAlarmTAO#alarmAusloesen(java.lang.String,
     * de.ecw.zabos.sql.vo.SchleifeVO[], de.ecw.zabos.types.id.AlarmQuelleId,
     * de.ecw.zabos.types.id.PersonId, java.lang.String, java.lang.String)
     */
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

            // null oder ' ' separierte Liste von Schleifenkuerzeln
            String rpAusgeloest = null;
            String rpBereitsAusgeloest = null;
            String rpKeineBerechtigung = null;

            // ArrayList<SchleifeVO>
            ArrayList<SchleifeVO> alSchleifen = new ArrayList<SchleifeVO>();

            // Alle Schleifen ausfiltern, die bereits ausgel�st sind
            for (int i = 0; i < _schleifen.length; i++)
            {
                SchleifeVO schleifeVO = _schleifen[i];
                // Ist die Schleife bereits ausgeloest?
                if (daoFactory.getAlarmDAO().isSchleifeAktiv(
                                schleifeVO.getSchleifeId()))
                {
                    // Schleife *ist* bereits ausgeloest!
                    log.debug("Die Schleife kuerzel=\""
                                    + schleifeVO.getKuerzel()
                                    + "\" ist bereits ausgeloest");

                    rpBereitsAusgeloest = StringUtils.addToCSV(
                                    rpBereitsAusgeloest,
                                    schleifeVO.getKuerzel());
                }
                else
                {
                    // Schleife ist noch nicht ausgeloest
                    alSchleifen.add(schleifeVO);
                }
            }

            // Sind noch auszuloesende Schleifen uebrig?
            if (alSchleifen.size() > 0)
            {
                alarmVO.setAlarmPersonId(_alarmPersonId);
                alarmVO.setAlarmQuelleId(_alarmQuelleId);
                alarmVO.setKommentar(_zusatzText);
                UnixTime alarmZeit = UnixTime.now();
                alarmVO.setAlarmZeit(alarmZeit);
                // 2007-06-21 CKL: GPS-Koordinaten hinzugef�gt
                alarmVO.setGpsKoordinate(_gpsKoordinate);
                alarmVO = daoFactory.getAlarmDAO().createAlarm(alarmVO);

                AlarmId alarmId = alarmVO.getAlarmId();

                // Key ist PersonId, Value ist AlarmId
                Map<PersonId, AlarmId> mapPersonInAlarm = new HashMap<PersonId, AlarmId>();

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

                    if (_alarmPersonId != null)
                    {
                        bPerm = personDAO.hatPersonRechtInSchleife(
                                        _alarmPersonId,
                                        RechtId.ALARM_AUSLOESEN, schleifeId);
                    }
                    else
                    {
                        bPerm = true;
                    }

                    if (bPerm)
                    {
                        // Ok, Berechtigung vorhanden. Schleife zum Alarm
                        // hinzufuegen
                        // alSchleifen.add(schleifeVO);

                        // Zuweisung der Schleife zum Alarm
                        daoFactory.getAlarmDAO().addSchleifeToAlarm(schleifeId,
                                        alarmId);

                        // Ermitteln der Alarmbenachrichtigungsempfaenger
                        // Personen
                        PersonVO[] personen = personDAO
                                        .findPersonenByRechtInSchleife(
                                                        RechtId.ALARMBENACHRICHTIGUNG_EMPFANGEN,
                                                        schleifeId);

                        int numPersons = 0;

                        // Nachrichtentext erstellen
                        String smsNachricht = getSmsContent()
                                        .resolveBestMatchingContentForAlarm(
                                                        alarmVO, schleifeVO,
                                                        _zusatzText,
                                                        _gpsKoordinate);

                        log.debug("smsnachricht=\"" + smsNachricht + "\"");

                        // Zuweisung der Schleifen-Personen zum Alarm
                        for (int j = 0; j < personen.length; j++)
                        {
                            PersonVO personVO = personen[j];

                            PersonId personId = personVO.getPersonId();

                            // Wer den Alarm ausloest muss sich nicht
                            // zurueckmelden
                            if (!personId.equals(_alarmPersonId))
                            {

                                if (personVO.isAnwesend(alarmZeit))
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
                                        if (!mapPersonInAlarm
                                                        .containsKey(personId))
                                        {
                                            mapPersonInAlarm.put(personId,
                                                            alarmId);
                                            daoFactory.getAlarmDAO()
                                                            .addPersonToAlarm(
                                                                            personId,
                                                                            alarmId);
                                        }

                                        SmsOutDAO smsOutDAO = daoFactory
                                                        .getSmsOutDAO();

                                        String context_alarm = StringUtils
                                                        .intToHexString(alarmVO
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
                                            smsOutVO.setZeitpunkt(UnixTime
                                                            .now());
                                            smsOutVO.setContext("Alarmierung");
                                            smsOutVO.setContextAlarm(context_alarm); // 2006-05-17
                                            // CST: Issue
                                            // #253
                                            smsOutVO.setContextO(oVO.getName());
                                            smsOutVO.setContextOE(oeVO
                                                            .getName());
                                            smsOutVO.setFestnetzSms(telefonVO
                                                            .getNummer()
                                                            .isFestnetzNummer());
                                            smsOutVO = smsOutDAO
                                                            .createSmsOut(smsOutVO);

                                            smsOutDAO.addSmsOutToSchleifeInAlarm(
                                                            smsOutVO.getSmsOutId(),
                                                            schleifeId, alarmId);
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
                            }

                        } // for personen

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

                // Wurde der Alarm manuell ueber SMS ausgeloest?
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
        }
        catch (StdException e)
        {
            log.error(e);
            rollback();
        }

        return alarmVO;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.ecw.zabos.sql.tao.IAlarmTAO#processAktiveAlarme()
     */
    public void processAktiveAlarme()
    {
        try
        {
            begin();

            UnixTime now = UnixTime.now();

            long timeOutMs = 1000 * getSystemKonfiguration().getAlarmTimeout();

            AlarmDAO alarmDAO = dbresource.getDaoFactory().getAlarmDAO();
            PersonInAlarmDAO personInAlarmDAO = dbresource.getDaoFactory()
                            .getPersonInAlarmDAO();

            AlarmVO[] alarmVOs = alarmDAO.findAktiveAlarme();

            for (int i = 0; i < alarmVOs.length; i++)
            {
                AlarmVO alarmVO = alarmVOs[i];

                UnixTime startZeit = alarmVO.getAlarmZeit();
                UnixTime endZeit = new UnixTime(timeOutMs);
                endZeit.add(startZeit);

                PersonInAlarmVO[] piaVOs = personInAlarmDAO
                                .findPersonenInAlarmByAlarmId(alarmVO
                                                .getAlarmId());

                // Alarm Time-Out?
                if (now.isBetween(startZeit, endZeit))
                {
                    // Alarm ist noch aktiv. Ueberpruefen ob die Rueckmeldungen
                    // komplett sind
                    boolean bComplete = true;
                    for (int j = 0; j < piaVOs.length; j++)
                    {
                        PersonInAlarmVO piaVO = piaVOs[j];
                        bComplete = bComplete
                                        && (piaVO.getRueckmeldungStatusId() != null);
                    }
                    if (bComplete)
                    {
                        // Alle benachrichtigten Personen haben sich
                        // zurueckgemeldet
                        alarmDeaktivieren(alarmVO, piaVOs);
                    }
                }
                else
                {
                    // Alarm-Timeout
                    alarmDeaktivieren(alarmVO, piaVOs);
                }
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
     * Aktuelle Rueckmelde-Statistik ermitteln.
     */
    private void alarmStatistik(
                    AlarmVO _alarmVO,
                    PersonInAlarmVO[] _piaVOs,
                    SchleifeVO[] _alarmSchleifen,
                    RueckmeldeStatistik _ret_gesamt,
                    Map<SchleifeVO, RueckmeldeStatistik> _ret_hmSchleifenStats,
                    Map<SchleifeVO, FunktionstraegerStatistik> _ret_hmFunktionstraegerStats) throws StdException
    {
        PersonDAO personDAO = dbresource.getDaoFactory().getPersonDAO();
        FunktionstraegerDAO funktionstraegerDAO = dbresource.getDaoFactory()
                        .getFunktionstraegerDAO();

        // Key ist SchleifeVO, Value ist PersonVO[]
        Map<SchleifeVO, PersonVO[]> mapSchleifenPersonen = new HashMap<SchleifeVO, PersonVO[]>();

        // Hashmaps vorbereiten
        for (int i = 0; i < _alarmSchleifen.length; i++)
        {
            SchleifeVO schleifeVO = _alarmSchleifen[i];

            // Alle Personen suchen, die fuer diese Alarmschleife benachrichtigt
            // wurden
            PersonVO[] personVOs = personDAO.findPersonenByRechtInSchleife(
                            RechtId.ALARMBENACHRICHTIGUNG_EMPFANGEN,
                            schleifeVO.getSchleifeId());
            mapSchleifenPersonen.put(schleifeVO, personVOs);
            _ret_hmSchleifenStats.put(schleifeVO, new RueckmeldeStatistik(
                            personVOs.length));
            _ret_hmFunktionstraegerStats.put(schleifeVO,
                            new FunktionstraegerStatistik());
        }

        // Alle Personen in allen Schleifen iterieren
        for (int i = 0; i < _piaVOs.length; i++)
        {
            PersonInAlarmVO piaVO = _piaVOs[i];
            RueckmeldungStatusId rsId = piaVO.getRueckmeldungStatusId();

            if (rsId != null)
            {
                long longRsId = rsId.getLongValue();

                if (longRsId == RueckmeldungStatusId.STATUS_JA)
                {
                    _ret_gesamt.incJa();
                }
                else if (longRsId == RueckmeldungStatusId.STATUS_NEIN)
                {
                    _ret_gesamt.incNein();
                }
                else if (longRsId == RueckmeldungStatusId.STATUS_SPAETER)
                {
                    _ret_gesamt.incSpaeter();
                }
            }
            else
            {
                _ret_gesamt.incUnbekannt();
            }

            // Alle Schleifen des Alarms iterieren
            for (int j = 0; j < _alarmSchleifen.length; j++)
            {
                SchleifeVO schleifeVO = _alarmSchleifen[j];
                PersonVO personVO = null;
                PersonVO[] personVOs = (PersonVO[]) mapSchleifenPersonen
                                .get(schleifeVO);

                // Ist die aktuelle Person der Schleife zugeordnet?
                boolean bZugeordnet = false;
                for (int k = 0; (!bZugeordnet) && (k < personVOs.length); k++)
                {
                    personVO = personVOs[k];
                    bZugeordnet = (personVO.getPersonId().equals(piaVO
                                    .getPersonId()));
                }

                if (bZugeordnet)
                {
                    // Person ist der Schleife zugeordnet => in
                    // Schleifenstatistik mit
                    // aufnehmen
                    RueckmeldeStatistik schleifenStat = (RueckmeldeStatistik) _ret_hmSchleifenStats
                                    .get(schleifeVO);

                    FunktionstraegerStatistik funktionstraegerStat = (FunktionstraegerStatistik) _ret_hmFunktionstraegerStats
                                    .get(schleifeVO);
                    FunktionstraegerVO funktionstraegerVO = funktionstraegerDAO
                                    .findFunktionstraegerByPersonId(personVO
                                                    .getPersonId());

                    // Rueckmeldestatus auswerten
                    if (rsId != null)
                    {
                        long longRsId = rsId.getLongValue();

                        if (longRsId == RueckmeldungStatusId.STATUS_JA)
                        {
                            schleifenStat.incJa();
                            funktionstraegerStat.incJa(funktionstraegerVO);
                        }
                        else if (longRsId == RueckmeldungStatusId.STATUS_NEIN)
                        {
                            schleifenStat.incNein();
                            funktionstraegerStat.incNein(funktionstraegerVO);
                        }
                        else if (longRsId == RueckmeldungStatusId.STATUS_SPAETER)
                        {
                            schleifenStat.incSpaeter();
                            funktionstraegerStat.incSpaeter(funktionstraegerVO);
                        }
                    }
                    else
                    {
                        schleifenStat.incUnbekannt();
                        funktionstraegerStat.incUnbekannt(funktionstraegerVO);
                    }
                } // if person ist schleife zugeordnet

            } // for schleifen
        } // for personen
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

            SchleifenDAO schleifenDAO = dbresource.getDaoFactory()
                            .getSchleifenDAO();

            SchleifeVO[] alarmSchleifen = schleifenDAO
                            .findSchleifenByAlarmId(_alarmVO.getAlarmId());

            ZwischenStatistik ret = new ZwischenStatistik(piaVOs.length);

            alarmStatistik(_alarmVO, piaVOs, alarmSchleifen, ret.gesamt,
                            ret.mapSchleifenStats, ret.mapFunktionstraegerStats);

            return ret;
        }
        catch (StdException e)
        {
            log.error(e);
            return null;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.ecw.zabos.sql.tao.IAlarmTAO#generateRueckmeldungReport(de.ecw.zabos
     * .sql.vo.AlarmVO, de.ecw.zabos.sql.vo.SchleifeVO,
     * de.ecw.zabos.alarmdaemon.RueckmeldeStatistik)
     */
    public String generateRueckmeldungReport(AlarmVO _alarmVO,
                    SchleifeVO _schleifeVO, RueckmeldeStatistik _schleifenStat)
    {

        String r = getSmsContent().resolveRueckmeldeStatistik(
                        SmsContent.RUECKMELDUNG_REPORT, _alarmVO, _schleifeVO,
                        _schleifenStat, null);

        return r;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.ecw.zabos.sql.tao.IAlarmTAO#generateRueckmeldungReport(de.ecw.zabos
     * .sql.vo.AlarmVO, de.ecw.zabos.sql.vo.SchleifeVO,
     * de.ecw.zabos.alarmdaemon.RueckmeldeStatistik,
     * de.ecw.zabos.alarmdaemon.FunktionstraegerStatistik)
     */
    public String generateRueckmeldungReport(AlarmVO _alarmVO,
                    SchleifeVO _schleifeVO, RueckmeldeStatistik _schleifenStat,
                    FunktionstraegerStatistik _funktionstraegerSchleifenStat)
    {
        Iterator<FunktionstraegerVO> statistikIterator = _funktionstraegerSchleifenStat
                        .findAll().keySet().iterator();

        StringBuffer sbFunktionstragerReport = new StringBuffer();

        Map<String, Object> mapSmsContentArgs = new HashMap<String, Object>();

        while (statistikIterator.hasNext())
        {
            FunktionstraegerVO funktionstraegerVO = (FunktionstraegerVO) statistikIterator
                            .next();
            RueckmeldeStatistik rueckmeldeStatistik = _funktionstraegerSchleifenStat
                            .findRueckmeldeStatistik(funktionstraegerVO);

            // Parameter für die SMS hinzufügen
            mapSmsContentArgs.put(SmsContent.PARAM_FUNKTIONSTRAEGER,
                            funktionstraegerVO);
            mapSmsContentArgs.put(SmsContent.PARAM_RUECKMELDESTATISTIK,
                            rueckmeldeStatistik);

            sbFunktionstragerReport.append(smsContent.resolve(
                            SmsContent.FUNKTIONSTRAEGER_STATISTIK,
                            mapSmsContentArgs));

            if (statistikIterator.hasNext())
            {
                sbFunktionstragerReport.append(',');
            }
        }

        mapSmsContentArgs.put(SmsContent.PARAM_FUNKTIONSTRAEGER_DETAILS,
                        sbFunktionstragerReport);

        String nachricht = getSmsContent().resolveRueckmeldeStatistik(
                        SmsContent.RUECKMELDUNG_FUNKTIONSTRAEGER_REPORT,
                        _alarmVO, _schleifeVO, _schleifenStat,
                        mapSmsContentArgs);

        return nachricht;
    }

    /**
     * Report für auslösende Person generieren und verschicken. Wird aufgerufen
     * wenn ein Alarm entweder durch Timeout abgelaufen ist oder sich alle
     * Personen zur�ckgemeldet haben. <br />
     * 2007-06-19 CKL: Statistik für die Funktionsträger ist hinzugekommen.
     * 
     * @param _alarmVO
     * @throws StdException
     */
    private void alarmDeaktivieren(AlarmVO _alarmVO, PersonInAlarmVO[] _piaVOs) throws StdException
    {
        AlarmDAO alarmDAO = dbresource.getDaoFactory().getAlarmDAO();
        SchleifenDAO schleifenDAO = dbresource.getDaoFactory()
                        .getSchleifenDAO();
        PersonDAO personDAO = dbresource.getDaoFactory().getPersonDAO();

        UnixTime jetzt = UnixTime.now();

        AlarmId alarmId = _alarmVO.getAlarmId();

        // Alarmdeaktivierung in Datenbank schreiben
        alarmDAO.deaktiviereAlarmById(alarmId);

        // Gesamt Statistik erstellen
        RueckmeldeStatistik gesamt = new RueckmeldeStatistik(_piaVOs.length);

        SchleifeVO[] alarmSchleifen = schleifenDAO
                        .findSchleifenByAlarmId(alarmId);

        // Key ist SchleifeVO, Value ist RueckmeldeStatistik
        Map<SchleifeVO, RueckmeldeStatistik> hmSchleifenStats = new HashMap<SchleifeVO, RueckmeldeStatistik>();

        // Key ist FunktionstraegerVO, Value ist RueckmeldeStatistik
        Map<SchleifeVO, FunktionstraegerStatistik> hmFunktionstraegerStats = new HashMap<SchleifeVO, FunktionstraegerStatistik>();

        // Statistik generieren
        alarmStatistik(_alarmVO, _piaVOs, alarmSchleifen, gesamt,
                        hmSchleifenStats, hmFunktionstraegerStats);

        // Key ist SchleifeVO, Value ist String
        HashMap<SchleifeVO, String> hmReportNachricht = new HashMap<SchleifeVO, String>();

        // Reportnachrichten generieren
        for (int i = 0; i < alarmSchleifen.length; i++)
        {
            SchleifeVO schleifeVO = alarmSchleifen[i];
            RueckmeldeStatistik schleifenStat = (RueckmeldeStatistik) hmSchleifenStats
                            .get(schleifeVO);

            // TODO: Mit der neuen Version muss die Statistik ueber die
            // Funktionstraeger gefuehrt werden
            // String nachricht = generateRueckmeldungReport(_alarmVO,
            // schleifeVO,
            // schleifenStat, funktionstraegerStat);
            String nachricht = generateRueckmeldungReport(_alarmVO, schleifeVO,
                            schleifenStat);

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

        OrganisationsEinheitDAO oeDAO = daoFactory.getOrganisationsEinheitDAO();
        OrganisationDAO oDAO = daoFactory.getOrganisationDAO();

        // Rueckmeldungsreport-SMS an jeden Schleifenverantwortlichen
        // verschicken
        for (int i = 0; i < alarmSchleifen.length; i++)
        {
            SchleifeVO schleifeVO = alarmSchleifen[i];

            // 2006-06-09 CKL: Siehe #287: Statusreport nur senden, wenn
            // SMS-Ausloeung
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
                // Ist die Alarmausl�sende Person gleichzeitig auch
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
                String nachricht = (String) hmReportNachricht.get(schleifeVO);
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

                    smsOutTAO.sendeNachrichtAnPerson(personVO.getPersonId(),
                                    nachricht, "Schleifenreport",
                                    context_alarm, context_o, context_oe);

                    log.debug("Schleifenreport an Verantwortlichen \""
                                    + personVO.getName() + ","
                                    + personVO.getVorname() + "\" verschickt.");
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
                        + gesamt.getNumSpaeter() + " mit \"Spaeter\" und "
                        + gesamt.getNumUnbekannt()
                        + " Personen garnicht zurueckgemeldet.");

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.ecw.zabos.sql.tao.IAlarmTAO#alarmEntwarnung(de.ecw.zabos.sql.vo.AlarmVO
     * , de.ecw.zabos.sql.vo.PersonInAlarmVO[])
     */
    public void alarmEntwarnung(AlarmVO _alarmVO, PersonInAlarmVO[] _piaVOs) throws StdException
    {
        AlarmDAO alarmDAO = dbresource.getDaoFactory().getAlarmDAO();

        AlarmId alarmId = _alarmVO.getAlarmId();

        // Alarmdeaktivierung in Datenbank schreiben
        alarmDAO.deaktiviereAlarmById(alarmId);

        Map<String, Object> smsContentArgs = new HashMap<String, Object>();
        smsContentArgs.put(SmsContent.PARAM_ALARM, _alarmVO);
        String nachricht = getSmsContent().resolve(SmsContent.ALARM_ENTWARNUNG,
                        smsContentArgs);

        // Alle Personen in diesem Alarm benachrichtigen
        for (int i = 0, m = _piaVOs.length; i < m; i++)
        {
            SmsOutTAO smsOutTAO = taoFactory.getSmsOutTAO();

            String context_alarm = StringUtils.intToHexString(_alarmVO
                            .getReihenfolge());

            // TODO CKL: Wem wird diese SMS in Rechnung gestellt?
            smsOutTAO.sendeNachrichtAnPerson(_piaVOs[i].getPersonId(),
                            nachricht, "Entwarnung", context_alarm, null, null);

            log.debug("Entwarnung an Person mit Id \""
                            + _piaVOs[i].getPersonId() + "\" verschickt");
        }

        log.debug("Der Alarm " + _alarmVO.getAlarmId().getLongValue()
                        + " wurde entwarnt, es wurden " + _piaVOs.length
                        + " Personen benachrichtigt.");
    }

    /**
     * Setzt die SystemKonfiguration
     * 
     * @param systemKonfiguration
     */
    final public void setSystemKonfiguration(
                    SystemKonfigurationVO systemKonfiguration)
    {
        this.systemKonfiguration = systemKonfiguration;
    }

    /**
     * Liefert die SystemKonfiguration
     * 
     * @return
     */
    final public SystemKonfigurationVO getSystemKonfiguration()
    {
        return systemKonfiguration;
    }

    public String toString()
    {
        return "Generische Verarbeitung der Alarme";
    }

    /**
     * Setzt die Internationalisierung
     * 
     * @param smsContent
     */
    final public void setSmsContent(SmsContent smsContent)
    {
        this.smsContent = smsContent;
    }

    /**
     * Liefert die Internationalisierung der SMSen
     * 
     * @return
     */
    public SmsContent getSmsContent()
    {
        return smsContent;
    }
}
