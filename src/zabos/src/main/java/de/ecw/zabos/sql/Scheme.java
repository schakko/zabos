package de.ecw.zabos.sql;

/**
 * Konstanten für Tabellen- und Spaltennamen des 'Zabos' Schemas
 * 
 * @author ckl
 * 
 */
public interface Scheme
{
    public static final String FUNC_FIND_REFERENZ_SCHLEIFE_IN_ALARM = "find_zuerst_ausgeloeste_schleife";

    public static final String FUNC_FIND_NACHFOLGENDE_SCHLEIFEN = "find_nachfolgende_schleifen";

    public static final String FUNC_FIND_AUSGELOESTE_SCHLEIFEN = "find_ausgeloeste_schleifen";

    // shared

    public static final String COLUMN_ID = "id";

    public static final String COLUMN_BESCHREIBUNG = "beschreibung";

    public static final String COLUMN_GELOESCHT = "geloescht";

    public static final String COLUMN_NAME = "name";

    public static final String COLUMN_KUERZEL = "kuerzel";

    // zabos.alarm

    public static final String ALARM_TABLE = "alarm";

    public static final String ALARM_COLUMN_ALARM_ZEIT = "alarm_zeit";

    public static final String ALARM_COLUMN_ENTWARN_ZEIT = "entwarn_zeit";

    public static final String ALARM_COLUMN_ALARM_PERSON_ID = "alarm_person_id";

    public static final String ALARM_COLUMN_ENTWARN_PERSON_ID = "entwarn_person_id";

    public static final String ALARM_COLUMN_ALARM_QUELLE_ID = "alarm_quelle_id";

    public static final String ALARM_COLUMN_KOMMENTAR = "kommentar";

    public static final String ALARM_COLUMN_AKTIV = "aktiv";

    public static final String ALARM_COLUMN_REIHENFOLGE = "reihenfolge";

    public static final String ALARM_COLUMN_GPS_KOORDINATE = "gps_koordinate";

    // zabos.alarm_quelle

    public static final String ALARM_QUELLE_TABLE = "alarm_quelle";

    public static final String ALARM_QUELLE_COLUMN_NAME = "name";

    // zabos.fuenfton

    public static final String FUENFTON_TABLE = "fuenfton";

    public static final String FUENFTON_COLUMN_FOLGE = "folge";

    public static final String FUENFTON_COLUMN_ZEITPUNKT = "zeitpunkt";

    // zabos.funktionstraeger

    public static final String FUNKTIONSTRAEGER_TABLE = "funktionstraeger";

    public static final String FUNKTIONSTRAEGER_COLUMN_BESCHREIBUNG = "beschreibung";

    // zabos.organisation

    public static final String ORGANISATION_TABLE = "organisation";

    public static final String ORGANISATION_COLUMN_NAME = "name";

    // zabos.organisationseinheit

    public static final String ORGANISATIONSEINHEIT_TABLE = "organisationseinheit";

    public static final String ORGANISATIONSEINHEIT_COLUMN_NAME = "name";

    public static final String ORGANISATIONSEINHEIT_COLUMN_ORGANISATION_ID = "organisation_id";

    // zabos.nachricht

    public static final String NACHRICHT_TABLE = "nachricht";

    public static final String NACHRICHT_COLUMN_NACHRICHT = "nachricht";

    public static final String NACHRICHT_COLUMN_SPRACHE_ID = "sprache_id";

    // zabos.person

    public static final String PERSON_TABLE = "person";

    public static final String PERSON_COLUMN_NAME = "name";

    public static final String PERSON_COLUMN_VORNAME = "vorname";

    public static final String PERSON_COLUMN_NACHNAME = "nachname";

    public static final String PERSON_COLUMN_PIN = "pin";

    public static final String PERSON_COLUMN_PASSWD = "passwd";

    public static final String PERSON_COLUMN_FUNKTIONSTRAEGER_ID = "funktionstraeger_id";

    public static final String PERSON_COLUMN_REPORT_OPTIONEN = "report_optionen";

    // 2006-07-12 CKL: Abwesend von
    public static final String PERSON_COLUMN_ABWESEND_VON = "abwesend_von";

    public static final String PERSON_COLUMN_ABWESEND_BIS = "abwesend_bis";

    public static final String PERSON_COLUMN_ORGANISATIONSEINHEIT_ID = "organisationseinheit_id";

    public static final String PERSON_COLUMN_EMAIL = "email";

    public static final String PERSON_COLUMN_OE_KOSTENSTELLE = "oe_kostenstelle";

    // 2009-11-23 CKL: Bereich
    public static final String PERSON_COLUMN_BEREICH_ID = "bereich_id";

    // 2010-05-05 CKL: Ist in Folgeschleife
    public static final String PERSON_COLUMN_IST_IN_FOLGESCHLEIFE = "in_folgeschleife";

    // 2010-06-10 CKL: Person erstellt von
    public static final String PERSON_COLUMN_ERSTELLT_VON = "erstellt_von_person_id";

    // zabos.probe_termin

    public static final String PROBE_TERMIN_TABLE = "probe_termin";

    public static final String PROBE_TERMIN_COLUMN_START = "start";

    public static final String PROBE_TERMIN_COLUMN_ENDE = "ende";

    public static final String PROBE_TERMIN_COLUMN_ORGANISATIONSEINHEIT_ID = "organisationseinheit_id";

    // zabos.person_in_alarm

    public static final String PERSON_IN_ALARM_TABLE = "person_in_alarm";

    public static final String PERSON_IN_ALARM_COLUMN_PERSON_ID = "person_id";

    public static final String PERSON_IN_ALARM_COLUMN_ALARM_ID = "alarm_id";

    public static final String PERSON_IN_ALARM_COLUMN_RUECKMELDUNG_STATUS_ID = "rueckmeldung_status_id";

    public static final String PERSON_IN_ALARM_COLUMN_KOMMENTAR = "kommentar";

    public static final String PERSON_IN_ALARM_COLUMN_IST_ENTWARNT = "ist_entwarnt";

    public static final String PERSON_IN_ALARM_COLUMN_KOMMENTAR_LEITUNG = "kommentar_leitung";

    // zabos.person_in_rolle_in_schleife

    public static final String PERSON_IN_ROLLE_IN_SCHLEIFE_TABLE = "person_in_rolle_in_schleife";

    public static final String PERSON_IN_ROLLE_IN_SCHLEIFE_COLUMN_PERSON_ID = "person_id";

    public static final String PERSON_IN_ROLLE_IN_SCHLEIFE_COLUMN_ROLLE_ID = "rolle_id";

    public static final String PERSON_IN_ROLLE_IN_SCHLEIFE_COLUMN_SCHLEIFE_ID = "schleife_id";

    // zabos.person_in_rolle_in_organisationseinheit

    public static final String PERSON_IN_ROLLE_IN_ORGANISATIONSEINHEIT_TABLE = "person_in_rolle_in_organisationseinheit";

    public static final String PERSON_IN_ROLLE_IN_ORGANISATIONSEINHEIT_COLUMN_PERSON_ID = "person_id";

    public static final String PERSON_IN_ROLLE_IN_ORGANISATIONSEINHEIT_COLUMN_ROLLE_ID = "rolle_id";

    public static final String PERSON_IN_ROLLE_IN_ORGANISATIONSEINHEIT_COLUMN_ORGANISATIONSEINHEIT_ID = "organisationseinheit_id";

    // zabos.person_in_rolle_in_organisation

    public static final String PERSON_IN_ROLLE_IN_ORGANISATION_TABLE = "person_in_rolle_in_organisation";

    public static final String PERSON_IN_ROLLE_IN_ORGANISATION_COLUMN_PERSON_ID = "person_id";

    public static final String PERSON_IN_ROLLE_IN_ORGANISATION_COLUMN_ROLLE_ID = "rolle_id";

    public static final String PERSON_IN_ROLLE_IN_ORGANISATION_COLUMN_ORGANISATION_ID = "organisation_id";

    // zabos.person_in_rolle_in_system

    public static final String PERSON_IN_ROLLE_IN_SYSTEM_TABLE = "person_in_rolle_in_system";

    public static final String PERSON_IN_ROLLE_IN_SYSTEM_COLUMN_PERSON_ID = "person_id";

    public static final String PERSON_IN_ROLLE_IN_SYSTEM_COLUMN_ROLLE_ID = "rolle_id";

    // zabos.recht

    public static final String RECHT_TABLE = "recht";

    public static final String RECHT_COLUMN_NAME = "name";

    // zabos.recht_in_rolle

    public static final String RECHT_IN_ROLLE_TABLE = "recht_in_rolle";

    public static final String RECHT_IN_ROLLE_COLUMN_RECHT_ID = "recht_id";

    public static final String RECHT_IN_ROLLE_COLUMN_ROLLE_ID = "rolle_id";

    // zabos.rolle

    public static final String ROLLE_TABLE = "rolle";

    public static final String ROLLE_COLUMN_NAME = "name";

    // zabos.rueckmeldung_status

    public static final String RUECKMELDUNG_STATUS_TABLE = "rueckmeldung_status";

    public static final String RUECKMELDUNG_STATUS_COLUMN_NAME = "name";

    // zabos.rueckmeldung_status_alias

    public static final String RUECKMELDUNG_STATUS_ALIAS_TABLE = "rueckmeldung_status_alias";

    public static final String RUECKMELDUNG_STATUS_ALIAS_COLUMN_RUECKMELDUNG_STATUS_ID = "rueckmeldung_status_id";

    public static final String RUECKMELDUNG_STATUS_ALIAS_COLUMN_ALIAS = "alias";

    // zabos.schleife

    public static final String SCHLEIFE_TABLE = "schleife";

    public static final String SCHLEIFE_COLUMN_NAME = "name";

    public static final String SCHLEIFE_COLUMN_FUENFTON = "fuenfton";

    // 2006-06-09 CKL: Statusreport-Fünfton

    public static final String SCHLEIFE_COLUMN_STATUSREPORT_FUENFTON = "statusreport_fuenfton";

    public static final String SCHLEIFE_COLUMN_ORGANISATIONSEINHEIT_ID = "organisationseinheit_id";

    // 2007-06-07 CKL: Ist Schleife abrechenbar

    public static final String SCHLEIFE_COLUMN_IST_ABRECHENBAR = "ist_abrechenbar";

    // 2009-11-23 CKL: ZABOS 1.2.0

    public static final String SCHLEIFE_COLUMN_DRUCKER_KUERZEL = "drucker_kuerzel";

    public static final String SCHLEIFE_COLUMN_SOLLSTAERKE = "sollstaerke";

    public static final String SCHLEIFE_COLUMN_FOLGESCHLEIFE_ID = "folgeschleife_id";

    public static final String SCHLEIFE_COLUMN_RUECKMELDE_INTERVALL = "rueckmeldeintervall";

    // zabos.schleife_in_alarm

    public static final String SCHLEIFE_IN_ALARM_TABLE = "schleife_in_alarm";

    public static final String SCHLEIFE_IN_ALARM_COLUMN_SCHLEIFE_ID = "schleife_id";

    public static final String SCHLEIFE_IN_ALARM_COLUMN_ALARM_ID = "alarm_id";

    // zabos.schleife_in_smsout

    public static final String SCHLEIFE_IN_SMSOUT_TABLE = "schleife_in_smsout";

    public static final String SCHLEIFE_IN_SMSOUT_COLUMN_SCHLEIFE_IN_ALARM_ID = "schleife_in_alarm_id";

    public static final String SCHLEIFE_IN_SMSOUT_COLUMN_SMSOUT_ID = "smsout_id";

    // zabos.smsin

    public static final String SMSIN_TABLE = "smsin";

    public static final String SMSIN_COLUMN_RUFNUMMER = "rufnummer";

    public static final String SMSIN_COLUMN_MODEM_RUFNUMMER = "modem_rufnummer";

    public static final String SMSIN_COLUMN_NACHRICHT = "nachricht";

    public static final String SMSIN_COLUMN_ZEITPUNKT = "zeitpunkt";

    public static final String SMSIN_COLUMN_GELESEN = "gelesen";

    // zabos.smsout

    public static final String SMSOUT_TABLE = "smsout";

    public static final String SMSOUT_COLUMN_TELEFON_ID = "telefon_id";

    public static final String SMSOUT_COLUMN_NACHRICHT = "nachricht";

    public static final String SMSOUT_COLUMN_ZEITPUNKT = "zeitpunkt";

    public static final String SMSOUT_COLUMN_STATUS_ID = "status_id";

    public static final String SMSOUT_COLUMN_CONTEXT = "context";

    public static final String SMSOUT_COLUMN_CONTEXT_ALARM = "context_alarm";

    public static final String SMSOUT_COLUMN_CONTEXT_O = "context_o";

    public static final String SMSOUT_COLUMN_CONTEXT_OE = "context_oe";

    public static final String SMSOUT_COLUMN_IST_FESTNETZ = "ist_festnetz";

    // zabos.system_konfiguration

    public static final String SYSTEM_KONFIGURATION_TABLE = "system_konfiguration";

    public static final String SYSTEM_KONFIGURATION_COLUMN_ALARM_TIMEOUT = "alarm_timeout";

    public static final String SYSTEM_KONFIGURATION_COLUMN_COM_5TON = "com_5ton";

    public static final String SYSTEM_KONFIGURATION_COLUMN_REAKTIVIERUNG_TIMEOUT = "reaktivierung_timeout";

    public static final String SYSTEM_KONFIGURATION_COLUMN_REAKTIVIERUNG_ZEITPUNKT = "reaktivierung_zeitpunkt";

    public static final String SYSTEM_KONFIGURATION_COLUMN_SMSIN_TIMEOUT = "smsin_timeout";

    public static final String SYSTEM_KONFIGURATION_COLUMN_ALARMHISTORIE_LAENGE = "alarmhistorie_laenge";

    // zabos.system_konfiguration

    public static final String SYSTEM_KONFIGURATION_MC35_TABLE = "system_konfiguration_mc35";

    public static final String SYSTEM_KONFIGURATION_MC35_COLUMN_COM_PORT = "com_port";

    public static final String SYSTEM_KONFIGURATION_MC35_COLUMN_RUFNUMMER = "rufnummer";

    public static final String SYSTEM_KONFIGURATION_MC35_COLUMN_PIN1 = "pin1";

    public static final String SYSTEM_KONFIGURATION_MC35_COLUMN_ALARM_MODEM = "alarm_modem";

    public static final String SYSTEM_KONFIGURATION_MC35_COLUMN_ZEITPUNKT_LETZTER_SMS_SELBSTTEST = "zeitpunkt_letzter_sms_selbsttest";

    // zabos.telefon

    public static final String TELEFON_TABLE = "telefon";

    public static final String TELEFON_COLUMN_PERSON_ID = "person_id";

    public static final String TELEFON_COLUMN_NUMMER = "nummer";

    public static final String TELEFON_COLUMN_AKTIV = "aktiv";

    public static final String TELEFON_COLUMN_ZEITFENSTER_START = "zeitfenster_start";

    public static final String TELEFON_COLUMN_ZEITFENSTER_ENDE = "zeitfenster_ende";

    public static final String TELEFON_COLUMN_FLASH_SMS = "flash_sms";

    // Views
    public static final String VERERBTE_ROLLEN_VIEW = "v_alle_rollen";

    public static final String VERERBTE_ROLLEN_COLUMN_KONTEXT = "kontext";

    public static final String VERERBTE_ROLLEN_COLUMN_KONTEXT_ID = "kontext_id";

    public static final String VERERBTE_ROLLEN_COLUMN_KONTEXT_NAME = "kontext_name";

    public static final String VERERBTE_ROLLEN_COLUMN_PARENT_ID = "parent_id";

    public static final String VERERBTE_ROLLEN_COLUMN_PARENT_NAME = "parent_name";

    public static final String VERERBTE_ROLLEN_COLUMN_ROLLE_ID = "rolle_id";

    public static final String VERERBTE_ROLLEN_COLUMN_ROLLE_NAME = "rolle_name";

    public static final String VERERBTE_ROLLEN_COLUMN_ROLLE_BESCHREIBUNG = "rolle_beschreibung";

    /**
     * Enumeration auf die Spalte
     * {@link Scheme#VERERBTE_ROLLEN_COLUMN_KONTEXT_ID}
     * 
     * @author ckl
     * 
     */
    public static enum VERERBTE_ROLLEN_KONTEXT_IDENTIFIER
    {
        SYSTEM, ORGANISATION, ORGANISATIONSEINHEIT, SCHLEIFE
    };

    // zabos.bereich

    public static final String BEREICH_TABLE = "bereich";

    public static final String BEREICH_IN_SCHLEIFE_TABLE = "bereich_in_schleife";

    public static final String BEREICH_IN_SCHLEIFE_COLUMN_BEREICH_ID = "bereich_id";

    public static final String BEREICH_IN_SCHLEIFE_COLUMN_FUNKTIONSTRAEGER_ID = "funktionstraeger_id";

    public static final String BEREICH_IN_SCHLEIFE_COLUMN_SCHLEIFE_ID = "schleife_id";

    public static final String BEREICH_IN_SCHLEIFE_COLUMN_SOLLSTAERKE = "sollstaerke";

    public static final String BEREICH_IN_ALARM_TABLE = "bereich_in_alarm";

    public static final String BEREICH_IN_ALARM_COLUMN_BEREICH_IN_SCHLEIFE_ID = "bereich_in_schleife_id";

    public static final String BEREICH_IN_ALARM_COLUMN_ALARM_ID = "alarm_id";

    public static final String BEREICH_IN_ALARM_COLUMN_AKTIVIERUNG = "aktivierung";

    public static final String BEREICH_IN_ALARM_COLUMN_AKTIV = "aktiv";

    public static final String VIEW_BEREICH_REPORT = "v_bereich_report";

    public static final String VIEW_BEREICH_REPORT_DETAIL = "v_bereich_report_detail";

    public static final String VIEW_BEREICH_REPORT_DETAIL_COLUMN_SCHLEIFE_ID = "schleife_id";

    public static final String VIEW_BEREICH_REPORT_DETAIL_COLUMN_ALARM_ID = "alarm_id";

    public static final String VIEW_BEREICH_REPORT_DETAIL_COLUMN_HAUPTSCHLEIFE_ID = "hauptschleife_id";

    public static final String VIEW_BEREICH_REPORT_COLUMN_ALARM_ID = "alarm_id";

    public static final String VIEW_BEREICH_REPORT_COLUMN_ALARM_REIHENFOLGE = "alarm_reihenfolge";

    public static final String VIEW_BEREICH_REPORT_COLUMN_ALARM_ZEIT = "alarm_zeit";

    public static final String VIEW_BEREICH_REPORT_COLUMN_ENTWARN_ZEIT = "entwarn_zeit";

    public static final String VIEW_BEREICH_REPORT_COLUMN_SCHLEIFE_ID = "schleife_id";

    public static final String VIEW_BEREICH_REPORT_COLUMN_HAUPTSCHLEIFE_ID = "hauptschleife_id";

    public static final String VIEW_BEREICH_REPORT_COLUMN_SCHLEIFE_NAME = "schleife_name";

    public static final String VIEW_BEREICH_REPORT_COLUMN_FUNKTIONSTRAEGER_ID = "funktionstraeger_id";

    public static final String VIEW_BEREICH_REPORT_COLUMN_FUNKTIONSTRAEGER_KUERZEL = "funktionstraeger_kuerzel";

    public static final String VIEW_BEREICH_REPORT_COLUMN_FUNKTIONSTRAEGER_BESCHREIBUNG = "funktionstraeger_beschreibung";

    public static final String VIEW_BEREICH_REPORT_COLUMN_BEREICH_ID = "bereich_id";

    public static final String VIEW_BEREICH_REPORT_COLUMN_BEREICH_NAME = "bereich_name";

    public static final String VIEW_BEREICH_REPORT_COLUMN_BEREICH_SOLLSTAERKE = "bereich_sollstaerke";

    public static final String VIEW_BEREICH_REPORT_COLUMN_POSITIVE_RUECKMELDUNG = "positive_rueckmeldung";

    public static final String VIEW_BEREICH_REPORT_DETAIL_UNBEKANNT_RUECKMELDUNG = "unbekannt_rueckmeldung";

    public static final String VIEW_BEREICH_REPORT_DETAIL_PERSONEN_IN_ALARM_GESAMT = "personen_in_alarm_gesamt";

    public static final String VIEW_PERSONEN_IN_SCHLEIFEN = "v_personen_in_schleifen";

    public static final String VIEW_PERSONEN_IN_SCHLEIFEN_COLUMN_SCHLEIFE_ID = "schleife_id";
}
