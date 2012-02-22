package de.ecw.zabos.service.smsin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.log4j.Logger;

import de.ecw.zabos.alarm.daemon.AlarmDaemon;
import de.ecw.zabos.broadcast.sms.SmsContent;
import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.mc35.ShortMessage;
import de.ecw.zabos.service.alarm.IAlarmService;
import de.ecw.zabos.sql.dao.OrganisationDAO;
import de.ecw.zabos.sql.dao.OrganisationsEinheitDAO;
import de.ecw.zabos.sql.dao.PersonDAO;
import de.ecw.zabos.sql.dao.PersonInAlarmDAO;
import de.ecw.zabos.sql.dao.RueckmeldungStatusDAO;
import de.ecw.zabos.sql.dao.SchleifenDAO;
import de.ecw.zabos.sql.dao.SmsInDAO;
import de.ecw.zabos.sql.dao.SystemKonfigurationDAO;
import de.ecw.zabos.sql.dao.TelefonDAO;
import de.ecw.zabos.sql.resource.DBResource;
import de.ecw.zabos.sql.tao.BaseTAO;
import de.ecw.zabos.sql.tao.SmsOutTAO;
import de.ecw.zabos.sql.tao.SystemKonfigurationTAO;
import de.ecw.zabos.sql.vo.OrganisationVO;
import de.ecw.zabos.sql.vo.OrganisationsEinheitVO;
import de.ecw.zabos.sql.vo.PersonInAlarmVO;
import de.ecw.zabos.sql.vo.PersonVO;
import de.ecw.zabos.sql.vo.RueckmeldungStatusVO;
import de.ecw.zabos.sql.vo.SchleifeVO;
import de.ecw.zabos.sql.vo.SmsInVO;
import de.ecw.zabos.sql.vo.SystemKonfigurationMc35VO;
import de.ecw.zabos.sql.vo.SystemKonfigurationVO;
import de.ecw.zabos.sql.vo.TelefonVO;
import de.ecw.zabos.types.Pin;
import de.ecw.zabos.types.TelefonNummer;
import de.ecw.zabos.types.UnixTime;
import de.ecw.zabos.types.id.AlarmQuelleId;
import de.ecw.zabos.types.id.PersonId;
import de.ecw.zabos.types.id.RechtId;
import de.ecw.zabos.util.StringUtils;

/**
 * Diese Adapter-Klasse verarbeitet die eingehenden SMSen. Dazu wird von
 * {@link AlarmDaemon} die Methode {@link #processSmsInbox()} aufgerufen.
 * 
 * @author ckl
 */
public class SmsInServiceAdapter extends BaseTAO implements ISmsInService
{
    public static final String KEYWORD_ABWESEND = "abwesend";

    public static final String KEYWORD_ZABOS = "zabos";

    public static final String KEYWORD_ZABOS_AN = "an";

    public static final String KEYWORD_ZABOS_AUS = "aus";

    private final static Logger log = Logger
                    .getLogger(SmsInServiceAdapter.class);

    protected TelefonDAO telefonDAO;

    protected PersonDAO personDAO;

    protected SmsInDAO smsInDAO;

    protected PersonInAlarmDAO personInAlarmDAO;

    protected OrganisationsEinheitDAO oeDAO;

    protected RueckmeldungStatusDAO rmsDAO;

    protected SystemKonfigurationDAO systemKonfigurationDAO;

    protected SystemKonfigurationTAO systemKonfigurationTAO;

    protected OrganisationDAO oDAO;

    protected SchleifenDAO schleifenDAO;

    private IAlarmService alarmService;

    private SystemKonfigurationVO systemKonfiguration;

    private SmsContent smsContent;

    /**
     * Konstruktor
     * 
     * @param _dbresource
     *            Unbedingt eine neue Datenbankinstanz übergeben, da diese
     *            Klasse gethreadet wird.
     * 
     * @param _alarmService
     * @param _systemKonfiguration
     * @param _smsContent
     */
    public SmsInServiceAdapter(final DBResource _dbresource,
                    final IAlarmService _alarmService,
                    SystemKonfigurationVO _systemKonfiguration, SmsContent _smsContent)
    {
        super(_dbresource);

        smsInDAO = daoFactory.getSmsInDAO();
        telefonDAO = daoFactory.getTelefonDAO();
        personDAO = daoFactory.getPersonDAO();
        personInAlarmDAO = daoFactory.getPersonInAlarmDAO();
        oeDAO = daoFactory.getOrganisationsEinheitDAO();
        rmsDAO = daoFactory.getRueckmeldungStatusDAO();
        systemKonfigurationDAO = daoFactory.getSystemKonfigurationDAO();
        systemKonfigurationTAO = taoFactory.getSystemKonfigurationTAO();
        schleifenDAO = daoFactory.getSchleifenDAO();
        oDAO = daoFactory.getOrganisationDAO();
        setAlarmService(_alarmService);
        setSystemKonfiguration(_systemKonfiguration);
        setSmsContent(_smsContent);
    }

    /*
     * (non-Javadoc)
     * 
     * @seede.ecw.zabos.sql.tao.ISmsInTAO#storeIncomingSMS(de.ecw.zabos.sql.vo.
     * SystemKonfigurationMc35VO, java.util.Vector)
     */
    public void storeIncomingSMS(SystemKonfigurationMc35VO _konfigurationVO,
                    Vector<ShortMessage> _messages)
    {
        try
        {
            begin();

            // Empfangene Nachrichten in Datenbank schreiben
            for (int i = 0; i < _messages.size(); i++)
            {
                // ShortMessage Objekt in SmsIn ValueObject konvertieren
                ShortMessage sms = (ShortMessage) _messages.get(i);
                SmsInVO smsInVO = smsInDAO.getObjectFactory().createSmsIn();
                smsInVO.setNachricht(sms.getBody());
                smsInVO.setRufnummer(new TelefonNummer(sms.getPhoneNumber()));
                smsInVO.setModemRufnummer(_konfigurationVO.getRufnummer());
                smsInVO.setZeitpunkt(new UnixTime(sms.getDate().getTime()));
                // Datenbankeintrag anlegen
                smsInVO = smsInDAO.createSmsIn(smsInVO);
            }

            log.debug(_konfigurationVO.getRufnummer().toString() + ": "
                            + _messages.size() + " SMS Nachrichten gespeichert");

            commit();
        }
        catch (StdException e)
        {
            log.error(e);
            rollback();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.ecw.zabos.sql.tao.ISmsInTAO#processSmsInbox()
     */
    public void processSmsInbox()
    {
        try
        {
            begin();

            // Ungelesene SMS Nachrichten erfragen
            SmsInVO[] smsInVOs = smsInDAO.findUngeleseneSmsIn();

            UnixTime zeitfensterStart = UnixTime.now();

            int timeoutInSec = getSystemKonfiguration().getSmsInTimeout();

            UnixTime timeout = new UnixTime(1000 * timeoutInSec);
            zeitfensterStart.sub(timeout);

            if (smsInVOs.length > 0)
            {
                log.debug(smsInVOs.length
                                + " ungelesene SMS Nachrichten gefunden");

                for (int i = 0; i < smsInVOs.length; i++)
                {
                    SmsInVO smsInVO = smsInVOs[i];

                    if (isSmsAllowed(smsInVO, zeitfensterStart))
                    {
                        // Absender ueberpruefen / zu Person mappen

                        TelefonVO telefonVO = telefonDAO
                                        .findTelefonByNummer(smsInVO
                                                        .getRufnummer());
                        if (telefonVO != null)
                        {
                            // Absendernummer ist bekannt; Person zu Rufnummer
                            // finden
                            PersonVO personVO = personDAO
                                            .findPersonByTelefonId(telefonVO
                                                            .getTelefonId());
                            if (personVO.getGeloescht() == false)
                            {
                                processSmsIn(smsInVO, personVO, telefonVO);
                            }
                            else
                            {
                                // Die Rufnummer bezieht sich auf eine bereits
                                // geloeschte Person
                                // ==>
                                // verwerfen
                                log.debug("SMS von bereits geloeschter Person id="
                                                + personVO.getPersonId()
                                                + " name=\""
                                                + personVO.getName()
                                                + "\" empfangen");
                            }
                        }
                        else
                        {
                            // Unbekannte Absender Nummer der SMS ==> verwerfen
                            log.debug("SMS von unbekannter Absendernummer "
                                            + smsInVO.getRufnummer()
                                            + " empfangen.");
                        }
                    }
                    else
                    {
                        log.debug("SMS id="
                                        + smsInVO.getSmsInId()
                                        + " wurde ignoriert da sie ausserhalb des Zeitfensters liegt");
                    }
                    // SMS als gelesen kennzeichnen
                    smsInDAO.kennzeichneAlsGelesenBySmsInId(smsInVO
                                    .getSmsInId());
                }
            } // if smsInVOs.length > 0
            commit();
        }
        catch (StdException e)
        {
            log.error(e);
            rollback();
        }
    }

    /**
     * Liefert zurück, ob die SMS innerhalb des Zeitfensters überhaupt
     * akzeptiert werden kann
     * 
     * @param _smsInVO
     * @param _zeitfensterStart
     * @return
     */
    public boolean isSmsAllowed(SmsInVO _smsInVO, UnixTime _zeitfensterStart)
    {
        return (_smsInVO.getZeitpunkt().isLaterThan(_zeitfensterStart));
    }

    /**
     * SMS Nachricht von gueltiger Person verarbeiten. Inhaltsanalyse des
     * Nachrichtentextes und Fallunterscheidung zwischen
     * <ol>
     * <li>Garbage (Klingelton etc..)</li>
     * <li>Alarmausloesung</li>
     * <li>Alarmentwarnung</li>
     * <li>temporaere Systemdeaktivierung (fuer Probealarm)</li>
     * <li>Systemaktivierung</li>
     * <li>
     * Rueckmeldung auf Alarmbenachrichtigung</li>
     * <li>Urlaubsmeldung</li>
     * </ol>
     */
    private void processSmsIn(SmsInVO _smsInVO, PersonVO _personVO,
                    TelefonVO _telefonVO) throws StdException
    {
        String nachricht = _smsInVO.getNachricht().trim();
        int anzahlWorte = 0;

        // Erstes Wort extrahieren
        String worte[] = StringUtils.findWords(nachricht);

        if (worte == null)
        {
            log.error("Es wurde eine leere SMS empfangen.");
            return;
        }
        else
        {
            anzahlWorte = worte.length;

            if (anzahlWorte == 0)
            {
                log.error("Es wurden eine SMS nur mit Sonderzeichen empfangen.");
                return;
            }
        }

        String wort1 = worte[0];
        String wort2;
        String wort3;

        if (worte.length >= 2)
            wort2 = worte[1];
        else
            wort2 = "";

        if (worte.length >= 3)
            wort3 = worte[2];
        else
            wort3 = "";

        // 2006-05-30 CST: Issue 266, Erste Zeile extrahieren
        int[] idx_line_ret = new int[1];
        String zeile = StringUtils.firstLine(nachricht, idx_line_ret);

        PersonId personId = _personVO.getPersonId();

        // Ist das Wort ein Rueckmelde-Alias?
        RueckmeldungStatusVO rmsVO = rmsDAO
                        .findRueckmeldungStatusByAlias(worte[0].toLowerCase());

        if (rmsVO == null)
        {
            // Ist die ganze Nachricht ein Rueckmelde-Alias?
            rmsVO = rmsDAO.findRueckmeldungStatusByAlias(nachricht
                            .toLowerCase());

            if (rmsVO == null)
            {
                // 2006-05-30 CST: Issue 266, Ist die erste Zeile ein
                // Rueckmelde-Alias?
                rmsVO = rmsDAO.findRueckmeldungStatusByAlias(zeile
                                .toLowerCase());
            }
        }

        if (rmsVO != null)
        {
            processRueckmeldungStatus(personId, _personVO, rmsVO);
        }
        else
        {
            log.debug("SMS ist *keine* Rueckmeldung. PIN-Ueberpruefung folgt.");

            // SMS ist *keine* Rueckmeldung
            String context_o = null;
            String context_oe = null;

            if (_personVO.getOEKostenstelle() != null)
            {
                OrganisationsEinheitVO oeVO = oeDAO
                                .findOrganisationsEinheitById(_personVO
                                                .getOEKostenstelle());

                if (oeVO != null)
                {
                    context_oe = oeVO.getName();
                    OrganisationVO oVO = oDAO.findOrganisationById(oeVO
                                    .getOrganisationId());
                    context_o = oVO.getName();
                }
            }

            boolean bIsOk = false;

            try
            {
                log.debug("Vergleiche Pin \"" + _personVO.getPin().toString()
                                + "\" mit Wort \"" + wort1 + "\"");

                bIsOk = StringUtils.compare(_personVO.getPin(), new Pin(wort1));
            }
            catch (StringIndexOutOfBoundsException e)
            {
                log.error("Fehler bei Ueberpruefen der PIN: " + e.getMessage()
                                + " Wort: " + wort1);
            }

            // Entspricht das erste Wort der PIN der Person?
            // 2006-05-10 CST: MD5-Verschluesselung entfernt!
            if (bIsOk)
            {
                // Pin ist OK.
                // Fallunterscheidung:
                // 1) Alarmausloesung (Liste von Schleifenkuerzeln folgt)
                // 2) Urlaubsmeldung ("abwesend <n>")
                // 3) Systemdeaktivierung (Probealarm) ("zabos an|aus")

                // 2006-06-23 CKL: Hotfix f�r Zabos
                if (wort2.compareTo(KEYWORD_ABWESEND) == 0)
                {
                    processAbwesenheit(_personVO, wort3, context_o, context_oe);
                }
                else if (wort2.compareTo(KEYWORD_ZABOS) == 0)
                {
                    processZabosSystem(_personVO, wort3, context_o, context_oe);
                }
                // Es soll eine Schleife oder mehrere ausgeloest werden
                else
                {
                    processAlarmAusloesung(_personVO, personId, wort1,
                                    nachricht);
                }
            }
            else
            {
                // Die NachrichtenPin entspricht nicht der Pin des Anrufers
                log.debug("Pin Authentifizierung fuer Person id=\"" + personId
                                + " name=\"" + _personVO.getName()
                                + "\" fehlgeschlagen");
            }
        } // else rmsVO != null
    }

    /**
     * Verarbeitet eine SMS zur Status-Rückmeldung
     * 
     * @param _personId
     * @param _personVO
     * @param rmsVO
     * @throws StdException
     */
    protected void processRueckmeldungStatus(PersonId _personId,
                    PersonVO _personVO, RueckmeldungStatusVO rmsVO) throws StdException
    {
        // SMS ist Rueckmeldung auf ausgeloesten Alarm.
        // Ist die Person einem aktiven Alarm zugeordnet?
        PersonInAlarmVO[] personInAlarmVOs = personInAlarmDAO
                        .findPersonenInAktivemAlarmByPersonId(_personId);

        if (personInAlarmVOs.length == 0)
        {
            // Rueckmeldung bezieht sich auf nicht mehr aktiven Alarm ==>
            // verwerfen
            log.debug("SMS Rueckmeldung von Person name=\""
                            + _personVO.getName()
                            + "\" bezieht sich auf inaktive(m) Alarm(e)");
            return;
        }

        // Die Person ist einem oder mehreren aktiven Alarmen zugeordnet
        // Fuer jeden dieser Alarme nun den RueckmeldeStatus eintragen.
        // Hat sich die Person bereits zurueckgemeldet wird der alte
        // Status
        // ueberschrieben.
        personInAlarmDAO.updateRueckmeldungStatus(_personId,
                        rmsVO.getRueckmeldungStatusId());

        log.debug("SMS Rueckmeldung \"" + rmsVO.getName() + "\" von Person \""
                        + _personVO.getName() + "\" akzeptiert");
    }

    /**
     * Verarbeitet die Abwesenheits-SMS einer Person
     * 
     * @param _personVO
     * @param _nachricht
     * @param kontext_o
     * @param kontext_oe
     * @throws StdException
     */
    protected void processAbwesenheit(PersonVO _personVO, String _nachricht,
                    String kontext_o, String kontext_oe) throws StdException
    {
        SmsOutTAO smsOutTAO = taoFactory.getSmsOutTAO();

        // <pin> abwesend <anzTage>
        // Abwesendheitszeit fuer Person updaten
        int anzTage;
        String abwesenheitsNachricht;
        try
        {
            anzTage = Integer.parseInt(_nachricht);
        }
        catch (NumberFormatException e)
        {
            // Das ist ein erlaubter Zustand also nicht loggen!
            anzTage = 0;
        }

        // 2006-07-19 CKL: Rechte ueberpruefen
        log.debug(_personVO.getDisplayName() + " will Abwesenheit auf "
                        + anzTage + " setzen");

        if (!personDAO.hatPersonRechtInSystem(_personVO.getPersonId(),
                        RechtId.EIGENE_PERSON_AENDERN))
        {
            log.error("Person "
                            + _personVO.getDisplayName()
                            + " hat kein Recht, seine Abwesenheitszeit zu aendern");
            return;
        }

        if (anzTage > 0)
        {
            // 2006-07-19 CKL: Abwesenheit VON setzen
            _personVO.setAbwesendVon(UnixTime.now());
            _personVO.setAbwesendBis(UnixTime.calcAbwesendBis(anzTage));
            abwesenheitsNachricht = getSmsContent().resolve(SmsContent.ABWESENHEITSZEIT_GESETZT, null);
            log.debug("Abwesenheitszeit fuer Person \"" + _personVO.getName()
                            + "\" via SMS gesetzt.");
        }
        else
        {
            // 2006-07-19 CKL: Abwesenheit VON setzen
            _personVO.setAbwesendVon(null);
            _personVO.setAbwesendBis(null);
            abwesenheitsNachricht = getSmsContent().resolve(SmsContent.ABWESENHEIT_AUFGEHOBEN, null);
            log.debug("Abwesenheit fuer Person \"" + _personVO.getName()
                            + "\" via SMS aufgehoben.");
        }

        // Rueckgabewert interessiert uns nicht
        if (null != personDAO.updatePerson(_personVO))
        {

            smsOutTAO.sendeNachrichtAnPerson(_personVO.getPersonId(),
                            abwesenheitsNachricht, "Urlaub", null, kontext_o,
                            kontext_oe);

            log.debug("Abwesenheit wurde fuer " + _personVO.getDisplayName()
                            + " gesetzt");
        }
    }

    /**
     * Verarbeitet eine SMS zur Alarm-Auslösung. Es wird davon ausgegangen, dass
     * die PIN-Überprüfung bereits erfolgreich geschehen ist.
     * 
     * @param _personVO
     * @param _personId
     * @param _pin
     * @param nachricht
     * @throws StdException
     */
    protected void processAlarmAusloesung(PersonVO _personVO,
                    PersonId _personId, String _pin, String nachricht) throws StdException
    {

        AlarmQuelleId alarmQuelleId = AlarmQuelleId.ID_SMS;

        // ':' Trennt Schleifenkuerzel-Liste und Hinweistext
        String zusatzText = null;
        int idxHinweis = nachricht.indexOf(':');

        if (idxHinweis == -1)
        {
            idxHinweis = nachricht.trim().length();
        }
        else
        {
            // Zusatztext extrahieren
            zusatzText = nachricht.substring(idxHinweis + 1).trim();
        }

        // Space-separierte Liste von Schleifen aus Nachricht
        // extrahieren
        String kuerzelListe = nachricht.substring(
                        (nachricht.indexOf(_pin) + _pin.length()), idxHinweis)
                        .trim();

        String[] kuerzels = StringUtils.ssvList(kuerzelListe);

        // ArrayList<SchleifeVO>
        List<SchleifeVO> alSchleifen = new ArrayList<SchleifeVO>();
        // Key ist KuerzelString
        Map<String, Boolean> hmUniqueSchleifen = new HashMap<String, Boolean>();

        // Schleifenkuerzel in Datenbank suchen
        String rpUnbekannt = null;
        for (int i = 0; i < kuerzels.length; i++)
        {
            String kuerzel = kuerzels[i];

            // 2006-05-23 CKL: Case-Insensitive
            if (!hmUniqueSchleifen.containsKey(kuerzel.toLowerCase()))
            {
                hmUniqueSchleifen.put(kuerzel.toLowerCase(), Boolean.TRUE);
                // 2006-05-23 CKL: Case-Insensitive Suche nach der
                // Schleife
                SchleifeVO schleifeVO = schleifenDAO
                                .findSchleifeByKuerzel(kuerzel);

                if (schleifeVO != null)
                {
                    alSchleifen.add(schleifeVO);
                }
                else
                {
                    // Unbekannte Schleife
                    log.debug("Person id=\""
                                    + _personId
                                    + " name=\""
                                    + _personVO.getName()
                                    + "\" versucht unbekannte Schleife kuerzel=\""
                                    + kuerzel + "\" auszuloesen");
                    rpUnbekannt = StringUtils.addToCSV(rpUnbekannt, kuerzel);
                }
            }
            else
            {
                log.warn("Mehrfache Angabe des Schleifenkuerzels \"" + kuerzel
                                + "\"");
            }
        } // for kuerzels

        if (alSchleifen.size() > 0)
        {
            // Alarm fuer alle berechtigten/existierenden Schleifen
            // ausloesen
            SchleifeVO[] arSchleifen = new SchleifeVO[alSchleifen.size()];
            alSchleifen.toArray(arSchleifen);
            alarmService.alarmAusloesen(zusatzText, arSchleifen, alarmQuelleId,
                            _personId, rpUnbekannt, null);
        }
    }

    /**
     * Verarbeitet eine SMS zum System aktivieren/deaktivieren
     * 
     * @param _personVO
     * @param _keyword
     * @param kontext_o
     * @param kontext_oe
     * @throws StdException
     */
    protected void processZabosSystem(PersonVO _personVO, String _keyword,
                    String kontext_o, String kontext_oe) throws StdException
    {

        PersonDAO personDAO = daoFactory.getPersonDAO();
        SmsOutTAO smsOutTAO = taoFactory.getSmsOutTAO();

        if (!personDAO.hatPersonRechtInSystem(_personVO.getPersonId(),
                        RechtId.SYSTEM_DEAKTIVIEREN))
        {
            log.debug("Person name="
                            + _personVO.getName()
                            + " hat keine Berechtigung das System zu (de-)aktivieren.");
            return;
        }

        // <pin> zabos [an]|[aus]
        String zabosNachricht = null;

        if (_keyword.compareTo(KEYWORD_ZABOS_AN) == 0)
        {
            if (systemKonfigurationDAO.istSystemDeaktiviert())
            {
                // System reaktivieren
                systemKonfigurationDAO.setSystemReaktivierungsZeitpunkt(null);
                zabosNachricht = getSmsContent().resolve(
                                SmsContent.SYSTEM_AKTIVIERT, null);
                log.debug("Das System wurde per SMS reaktiviert");
            }
            else
            {
                // System ist bereits aktiv
                zabosNachricht = getSmsContent().resolve(
                                SmsContent.SYSTEM_BEREITS_AKTIV, null);
            }
        }
        else if (_keyword.compareTo(KEYWORD_ZABOS_AUS) == 0)
        {
            if (!systemKonfigurationDAO.istSystemDeaktiviert())
            {
                // System deaktivieren
                systemKonfigurationTAO
                                .deaktiviereSystem(getSystemKonfiguration());
                zabosNachricht = getSmsContent().resolve(
                                SmsContent.SYSTEM_DEAKTIVIERT, null);
            }
            else
            {
                // System ist bereits deaktiviert
                zabosNachricht = getSmsContent().resolve(
                                SmsContent.SYSTEM_BEREITS_INAKTIV, null);
            }
        }
        else
        {
            // Unbekannter Befehl
            zabosNachricht = getSmsContent().resolve(
                            SmsContent.SYSTEM_UNBEKANNT, null);
        }

        if (zabosNachricht != null)
        {
            smsOutTAO.sendeNachrichtAnPerson(_personVO.getPersonId(),
                            zabosNachricht, "Systemaktivierung", null,
                            kontext_o, kontext_oe);
        }
    }

    /**
     * Setzt den {@link IAlarmService}
     * 
     * @param alarmService
     */
    final public void setAlarmService(IAlarmService alarmService)
    {
        this.alarmService = alarmService;
    }

    /**
     * Liefert den {@link IAlarmService}
     * 
     * @return
     */
    final public IAlarmService getAlarmService()
    {
        return alarmService;
    }

    public String toString()
    {
        return "Generische SMS-Verarbeitung";
    }

    /**
     * Setzt das {@link SystemKonfigurationVO}
     * 
     * @param systemKonfiguration
     */
    final public void setSystemKonfiguration(
                    SystemKonfigurationVO systemKonfiguration)
    {
        this.systemKonfiguration = systemKonfiguration;
    }

    /**
     * Liefert das {@link SystemKonfigurationVO}
     */
    final public SystemKonfigurationVO getSystemKonfiguration()
    {
        return systemKonfiguration;
    }

    /**
     * Setzt die Nachrichten für die SMSen
     * 
     * @param i18n
     */
    final public void setSmsContent(SmsContent smsContent)
    {
        this.smsContent = smsContent;
    }

    /**
     * Liefert die Nachrichten für die SMSen
     * 
     * @return
     */
    final public SmsContent getSmsContent()
    {
        return smsContent;
    }
}
