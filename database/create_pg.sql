--
-- create script fuer die 'zabos' PostgreSQL 8.1 Datenbank
--
-- History:
--	2006-02-01 <bsp>
--	2006-02-03 <bsp>
--	2006-02-09 <bsp>
--	2006-02-15 <bsp> Portierung für PostgreSQL
--      2006-02-21 <bsp> SmsOut Status ids, MC35 Konfiguration
--      2006-03-01 <bsp> reihenfolge fuer zabos.alarm, neue probe_termin tabelle
--      2006-03-02 <bsp> person_in_rolle_in_<o,oe,schleife,system>
--      2006-03-07 <bsp> zabos.person.organisationseinheit_id entfernt
--      2006-03-07 <bsp> zabos.system_konfiguration: neue felder sms_user, sms_passwd
--                       zabos.system_konfiguration_mc35: neues feld alarm_modem
--
--      2006-03-27 <bsp> person.oe_kostenstelle feld hinzugefuegt
--                       smsout.context_alarm, context_o, context_oe hinzugefuegt
--                       person.pin, person.passwd feldgroesse erhoeht damit MD5 hash reinpasst
--      2006-03-29 <bsp> rechte lizenz einsehen, allg. syskonfig.
--		2006-11-10 <cst> VIEW v_sms_statistik und FUNCTION u2time() hinzugefuegt 


--DROP DATABASE zabos;
--CREATE DATABASE zabos;
--USE zabos;

--drop sequence id_seq;

--DROP TABLE probe_termin;
--DROP TABLE person_in_rolle;
--DROP TABLE person_in_alarm;
--DROP TABLE schleife_in_smsout;
--DROP TABLE schleife_in_alarm;
--DROP TABLE person_in_rolle_in_schleife;
--DROP TABLE person_in_rolle_in_organisationseinheit;
--DROP TABLE person_in_rolle_in_organisation;
--DROP TABLE person_in_rolle_in_system;
--DROP TABLE smsout;
--DROP TABLE smsout_status;
--DROP TABLE schleife;
--DROP TABLE alarm;
--DROP TABLE recht_in_rolle;
--DROP TABLE rolle;
--DROP TABLE telefon;
--DROP TABLE person;
--DROP TABLE organisationseinheit;
--DROP TABLE organisation;
--DROP TABLE alarm_quelle;"Das Zabos-System wurde aktiviert"
--DROP TABLE rueckmeldung_status_alias;
--DROP TABLE rueckmeldung_status;
--DROP TABLE recht;
--DROP TABLE smsin;
--DROP TABLE fuenfton;
--DROP TABLE system_konfiguration_mc35;
--DROP TABLE system_konfiguration;
--DROP TABLE webclient_konfiguration;
--DROP TABLE funktionstraeger;
--DROP TABLE bereich
--
-- zabos.id_seq
-- SEQUENCE für IDs
--

-- Handler fuer PL/PGSQL
CREATE TRUSTED PROCEDURAL LANGUAGE "plpgsql"  HANDLER "plpgsql_call_handler" VALIDATOR "plpgsql_validator";

create sequence id_seq start 2001;



--
-- zabos.alarm_quelle
--
CREATE TABLE alarm_quelle (
  id   bigint      NOT NULL , -- 'absolute ID',
  name varchar(32) NOT NULL , -- 'sms, 5ton, web, ...',
  PRIMARY KEY(id),
  UNIQUE(name)
); --='Stammdaten';

INSERT INTO alarm_quelle (id, name) VALUES(0, 'sms');
INSERT INTO alarm_quelle (id, name) VALUES(1, '5-tonfolge-ruf');
INSERT INTO alarm_quelle (id, name) VALUES(2, 'webclient');
INSERT INTO alarm_quelle (id, name) VALUES(3, 'pocsag');
INSERT INTO alarm_quelle (id, name) VALUES(4, 'fms');



--
-- zabos.rueckmeldung_status
--
CREATE TABLE rueckmeldung_status (
  id   bigint      NOT NULL , -- 'absolute ID',
  name varchar(64) NOT NULL , -- 'ja,nein,spaeter...',
  PRIMARY KEY(id),
  UNIQUE(name)
); --='Stammdaten';

INSERT INTO rueckmeldung_status (id, name) VALUES(0, 'nein');
INSERT INTO rueckmeldung_status (id, name) VALUES(1, 'ja');
INSERT INTO rueckmeldung_status (id, name) VALUES(2, 'spaeter');

INSERT INTO rueckmeldung_status (id, name) VALUES(100, 'timeout');
INSERT INTO rueckmeldung_status (id, name) VALUES(101, 'nicht erreichbar');



--
-- zabos.organisation
--
CREATE TABLE organisation (
  id           bigint       NOT NULL ,
  geloescht    boolean      NOT NULL default false,
  name         varchar(64)  NOT NULL     , -- 'Name der Organisationeinheit',
  beschreibung text         default NULL , -- 'optionaler Freitext',
  PRIMARY KEY(id),
  UNIQUE(name)
); --='Organisation, z.b. Gemeindefeuerwehr Papenteich';


--
-- zabos.organisationseinheit
--
CREATE TABLE organisationseinheit (
  id           bigint       NOT NULL ,
  geloescht    boolean      NOT NULL default false,
  name         varchar(64)  NOT NULL     , -- 'Name der Organisationeinheit',
  beschreibung text         default NULL , -- 'optionaler Freitext',
  organisation_id bigint    NOT NULL,
  PRIMARY KEY(id),
  UNIQUE(name),
  FOREIGN KEY(organisation_id) REFERENCES organisation(id)
); --='Organisationseinheiten, z.b. Wehr FF Meine';


--
-- zabos.probe_termin
--
CREATE TABLE probe_termin (
  id                      bigint NOT NULL,
  organisationseinheit_id bigint NOT NULL, -- FK organisation.id
  start                   bigint NOT NULL, -- unix timestamp 
  ende                    bigint NOT NULL, -- unix timestamp 
  PRIMARY KEY(id),
  FOREIGN KEY(organisationseinheit_id) REFERENCES organisationseinheit(id)
);



--
-- zabos.rolle
--
CREATE TABLE rolle (
  id           bigint        NOT NULL ,
  name         varchar(64)   NOT NULL     , -- 'Name der Rolle',
  beschreibung text          default NULL , -- 'optionaler Freitext',
  PRIMARY KEY(id),
  UNIQUE(name)
); --='Benennt ein Set von Rechten, welches dann n Personen zugewiesen werden kann';


--
-- 2007-06-07 CKL: Funktionstraeger
-- zabos.funktionstraeger
--
CREATE TABLE funktionstraeger (
  id 				bigint		NOT NULL ,
  kuerzel				varchar(16)	NOT NULL , -- Name des Funktionstraegers / Kuerzel
  beschreibung		varchar(64) NOT NULL , -- Beschreibung des Funktionstraegers
  geloescht       boolean        NOT NULL default false , -- 1=Funktionstraeger wurde geloescht
  PRIMARY KEY(id),
  UNIQUE(kuerzel)
); --='Die Funktionen, die eine Person in der O/OE/S auslebt

INSERT INTO funktionstraeger (id, beschreibung, kuerzel, geloescht) VALUES((SELECT NEXTVAL('id_seq')), 'Nicht definiert', 'ND', false);
INSERT INTO funktionstraeger (id, beschreibung, kuerzel, geloescht) VALUES((SELECT NEXTVAL('id_seq')), 'Angriffstrupp - Führer', 'AF', false);
INSERT INTO funktionstraeger (id, beschreibung, kuerzel, geloescht) VALUES((SELECT NEXTVAL('id_seq')), 'Angriffstrupp - Mann', 'AM', false);
INSERT INTO funktionstraeger (id, beschreibung, kuerzel, geloescht) VALUES((SELECT NEXTVAL('id_seq')), 'Atemschutzgeräteträger', 'AGT', false);
INSERT INTO funktionstraeger (id, beschreibung, kuerzel, geloescht) VALUES((SELECT NEXTVAL('id_seq')), 'Gruppenführer', 'GF', false);
INSERT INTO funktionstraeger (id, beschreibung, kuerzel, geloescht) VALUES((SELECT NEXTVAL('id_seq')), 'Maschenist', 'MA', false);
INSERT INTO funktionstraeger (id, beschreibung, kuerzel, geloescht) VALUES((SELECT NEXTVAL('id_seq')), 'Melder', 'ME', false);
INSERT INTO funktionstraeger (id, beschreibung, kuerzel, geloescht) VALUES((SELECT NEXTVAL('id_seq')), 'Schlauchtrupp - Führer', 'SF', false);
INSERT INTO funktionstraeger (id, beschreibung, kuerzel, geloescht) VALUES((SELECT NEXTVAL('id_seq')), 'Schlauchtrupp - Mann', 'SM', false);
INSERT INTO funktionstraeger (id, beschreibung, kuerzel, geloescht) VALUES((SELECT NEXTVAL('id_seq')), 'Wassertrupp - Führer', 'WF', false);
INSERT INTO funktionstraeger (id, beschreibung, kuerzel, geloescht) VALUES((SELECT NEXTVAL('id_seq')), 'Wassertrupp - Mann', 'WM', false);
INSERT INTO funktionstraeger (id, beschreibung, kuerzel, geloescht) VALUES((SELECT NEXTVAL('id_seq')), 'Zugführer', 'ZF', false);

--
-- 2009-11-23 CKL:Anpassung fuer ZABOS 1.2.0
-- zabos.bereich
--
CREATE TABLE bereich (
  id 				bigint		NOT NULL ,
  name				varchar(128)	NOT NULL , -- Name des Bereichs
  beschreibung 		varchar(64) NOT NULL, -- Beschreibung des Bereichs
  geloescht       boolean        NOT NULL default false , -- 1=Bereich wurde geloescht
  PRIMARY KEY(id),
  UNIQUE(name)
); --='Bereich, in dem die Person taetig ist

INSERT INTO bereich(id, name, beschreibung) VALUES((SELECT NEXTVAL('id_seq')), 'Nicht definiert', 'Nicht definiert');

--
-- zabos.person
--
CREATE TABLE person (
  id              bigint         NOT NULL ,
  name            varchar(64)    NOT NULL      , -- Screenname der Person
  vorname         varchar(64)    NOT NULL      , -- Vorname der Person
  nachname        varchar(64)    NOT NULL      , -- Nachname der Person
  pin             varchar(64)    default NULL  , -- Zur Authentifizierung ueber SMS Nachricht (Alarmausloesung) (MD5)
  passwd          varchar(64)    default NULL  , -- Zur Authentifizierung ueber Web Schnittstelle (MD5)
  abwesend_von    bigint         default NULL  , -- Falls Person nicht verfuegbar steht hier der Zeitpunkt (unix time) zu dem die Person abwesend ist
  abwesend_bis    bigint         default NULL  , -- Falls Person nicht verfuegbar steht hier der Zeitpunkt (unix time) zu dem die Person wieder verfuegbar ist
  beschreibung    text           default NULL  , -- optionaler Freitext
  email           varchar(96)    default NULL  , -- optionale EMail Kontaktadresse
  geloescht       boolean        NOT NULL default false , -- 1=Person wurde geloescht
  oe_kostenstelle bigint         default NULL  , -- optional: Kostenstelle fuer Person (SMS Abrechnung)
  funktionstraeger_id	bigint	NULL , -- 2007-06-07 CKL: Funktionstraeger, Referenz auf funktionstraeger, gwuenscht von MOE
  bereich_id bigint default NULL , -- 2009-11-23 CKL: Anpassung fuer ZABOS 1.2.0
  report_optionen	BYTEA default NULL, -- 2009-12-08 CKL: Report-Optionen
  in_folgeschleife boolean 		NOT NULL default false, -- 2010-05-05 CKL: Zur Information, ob eine Person in einer Folgeschleife ist; Anforderung Fr. Menzel
  erstellt_von_person_id bigint	default NULL, -- 2010-06-10 CKL: Darin wird gespeichert, welche Person diese Person erstellt hat; Anforderung Fr. Menzel
  UNIQUE(email),
  UNIQUE(name),
  PRIMARY KEY(id),
  FOREIGN KEY(funktionstraeger_id) REFERENCES funktionstraeger(id),
  FOREIGN KEY(erstellt_von_person_id) REFERENCES person(id),
  FOREIGN KEY(bereich_id) REFERENCES bereich(id)
); --='Personen';

--
-- zabos.telefon
--
CREATE TABLE telefon (
  id                bigint       NOT NULL ,
  person_id         bigint       NOT NULL           , -- 'FK person.id',
  nummer            varchar(32)  NOT NULL           , -- 'Handy Nummer, z.b. 00491601518423',
  aktiv             boolean      NOT NULL default true , -- 'true=telefon ist verfuegbar',
  zeitfenster_start bigint       default NULL       , -- 'sek. seit tagesanbruch ab dem diese nummer erreichbar ist. NULL=nummer ist immer verfuegbar wenn aktiv',
  zeitfenster_ende  bigint       default NULL       , -- 'sek seit tagesanbruch ab dem diese nummer nicht mehr erreichbar ist. wird nur ausgewertet wenn zeitfenster_start NOT NULL ist',
  geloescht         boolean      NOT NULL default false , -- true=Telefonnummer wurde geloescht
  flash_sms         boolean      NOT NULL default false , -- true=Handy ist Flash SMS faehig
  PRIMARY KEY(id),
  UNIQUE(nummer),
  FOREIGN KEY(person_id) REFERENCES person(id)
); --='Telefonnummern';




--
-- zabos.alarm
--
CREATE TABLE alarm (
  id                bigint        NOT NULL ,
  alarm_zeit        bigint        NOT NULL     , -- Zeitpunkt zu dem der Alarm ausgeloest wurde (sekunden seit 1.1.1970)
  entwarn_zeit      bigint        default NULL , -- Zeitpunkt zu dem Entwarnung gegeben wurde (sekunden seit 1.1.1970)
  alarm_person_id   bigint                     , -- Person, die den Alarm ausgeloest hat. NULL=Ausloesung ueber 5 Tonfolge Ruf
  entwarn_person_id bigint        default NULL , -- Person, die Entwarnung gegeben hat
  alarm_quelle_id   bigint        NOT NULL     , -- FK alarm_quelle.id
  kommentar         varchar(60)   default NULL , -- optionaler Zusatz-Text, der an eine Benachrichtigung mit angehaengt wird
  aktiv             boolean       NOT NULL default true  , -- 1=Alarm ist aktiv, 0=abgelaufen; einmal abgelaufene Alarme koennen nicht wieder gueltig werden!
  reihenfolge       serial        NOT NULL,      -- Alarm nummer, wird u.a. in SMS Nachrichten verwendet
  gps_koordinate	varchar(20)		default NULL , -- 2007-06-07 CKL: GPS-Koordinate des Alarms, gewuenscht von MOE
  UNIQUE(reihenfolge),
  PRIMARY KEY(id),
  FOREIGN KEY(alarm_person_id) REFERENCES person(id),
  FOREIGN KEY(entwarn_person_id) REFERENCES person(id),
  FOREIGN KEY(alarm_quelle_id) REFERENCES alarm_quelle(id)
); --='Ausgeloester Alarm';



--
-- zabos.person_in_alarm
--
CREATE TABLE person_in_alarm (
  id                     bigint NOT NULL ,
  person_id              bigint NOT NULL     , -- 'FK person.id ',
  alarm_id               bigint NOT NULL     , -- 'FK alarm.id',
  rueckmeldung_status_id bigint              , -- 'FK rueckmeldung_status.id',
  kommentar              varchar(140)     default NULL , -- 'Zusatztext in SMS Rueckmeldung (falls vorhanden)',
  kommentar_leitung		varchar(255)	default NULL , -- 'Zusatztext der Einsatzleitung
  ist_entwarnt			boolean default false,
  PRIMARY KEY(id),
  UNIQUE(person_id,alarm_id),
  FOREIGN KEY(person_id) REFERENCES person(id),
  FOREIGN KEY(alarm_id) REFERENCES alarm(id),
  FOREIGN KEY(rueckmeldung_status_id) REFERENCES rueckmeldung_status(id)
); -- intern; Zuweisung einer Person zu einem Alarm; wird bei Alarmausloesung gefuellt



--
-- zabos.recht
--
CREATE TABLE recht (
  id           bigint      NOT NULL     , -- absolute ID
  name         varchar(64) NOT NULL     , -- Eindeutiger Name der Berechtigung
  beschreibung text        default NULL , -- optionaler Freitext
  PRIMARY KEY(id),
  UNIQUE(name)
); -- Stammdaten; Enthaelt alle zu Rollen zuweisbaren Berechtigungen

INSERT INTO recht (id, name) VALUES(10, 'System deaktivieren');
INSERT INTO recht (id, name) VALUES(11, 'Systemkonfiguration ändern');
INSERT INTO recht (id, name) VALUES(12, 'Lizenz einsehen');
INSERT INTO recht (id, name) VALUES(13, 'COM-Ports festlegen');
INSERT INTO recht (id, name) VALUES(14, 'Bereiche definieren');
INSERT INTO recht (id, name) VALUES(15, 'Funktionsträger definieren');

INSERT INTO recht (id, name) VALUES(20, 'Probealarm administrieren');

INSERT INTO recht (id, name) VALUES(30, 'Organisation anlegen/löschen');
INSERT INTO recht (id, name) VALUES(31, 'Organisation ändern');

INSERT INTO recht (id, name) VALUES(40, 'Organisationseinheit anlegen/löschen');
INSERT INTO recht (id, name) VALUES(41, 'Organisationseinheit ändern');

INSERT INTO recht (id, name) VALUES(50, 'Person anlegen/löschen');
INSERT INTO recht (id, name) VALUES(51, 'Person ändern');
INSERT INTO recht (id, name) VALUES(52, 'Seine eigene Person ändern');
INSERT INTO recht (id, name) VALUES(53, 'Seine eigenen Telefone ändern');
INSERT INTO recht (id, name) VALUES(54, 'Seine eigenen Abwesenheitszeiten ändern');

INSERT INTO recht (id, name) VALUES(60, 'Schleife anlegen/löschen');
INSERT INTO recht (id, name) VALUES(61, 'Schleife ändern');

INSERT INTO recht (id, name) VALUES(70, 'Alarm ausloesen');
INSERT INTO recht (id, name) VALUES(71, 'Alarmhistorie sehen');
INSERT INTO recht (id, name) VALUES(72, 'Alarmhistorie Details sehen');
INSERT INTO recht (id, name) VALUES(73, 'Alarmbenachrichtigung empfangen');
INSERT INTO recht (id, name) VALUES(74, 'Alarmrückmeldungsreport empfangen');

INSERT INTO recht (id, name) VALUES(80, 'Rollen anlegen/löschen');
INSERT INTO recht (id, name) VALUES(81, 'Rollen ändern');

INSERT INTO recht (id, name) VALUES(82, 'Abrechnung für Schleife festlegen'); -- 7-06-07 CKL: Der Benutzer darf die Abrechnung fuer eine Schleife festlegen
INSERT INTO recht (id, name) VALUES(83, 'Leitungs-Kommentar festlegen'); -- 2009-12-15 CKL: Der Benutzer darf Leitungs-Kommentare festlegen
INSERT INTO recht (id, name) VALUES(84, 'OE-Kostenstelle festlegen'); -- 2010-01-18 CKL: Der Benutzer darf die Kostenstelle anderer Benutzer festlegen
INSERT INTO recht (id, name) VALUES(85, 'Statistik/Berichte anzeigen'); -- 2010-05-05 CKL: Der Benutzer darf Statistiken und Berichte anzeigen
INSERT INTO recht (id, name) VALUES(86, 'Personen Rollen zuweisen'); -- 2011-02-23 CKL: Benutzer darf andere Personen Rollen zuweisen


--
-- zabos.recht_in_rolle
--
CREATE TABLE recht_in_rolle (
  id       bigint NOT NULL ,
  recht_id bigint NOT NULL , -- 'FK recht.id',
  rolle_id bigint NOT NULL , -- 'FK rolle.id',
  PRIMARY KEY(id),
  UNIQUE(recht_id,rolle_id),
  FOREIGN KEY(recht_id) REFERENCES recht(id),
  FOREIGN KEY(rolle_id) REFERENCES rolle(id)
); --='Zuweisung eines Rechts zu einer Rolle';



--
-- zabos.rueckmeldung_status_alias
--
CREATE TABLE rueckmeldung_status_alias (
  id                     bigint NOT NULL ,
  rueckmeldung_status_id bigint NOT NULL , -- 'FK rueckmeldung_status.id',
  alias                  varchar(20)      NOT NULL , -- 'ja; nein; spaeter usw..',
  PRIMARY KEY(id),
  UNIQUE(rueckmeldung_status_id,alias),
  FOREIGN KEY(rueckmeldung_status_id) REFERENCES rueckmeldung_status(id)
); --='Legt (globale) Alias Namen fuer Rueckmelde Stati fest';

INSERT INTO rueckmeldung_status_alias (id, rueckmeldung_status_id, alias) values((SELECT NEXTVAL('id_seq')),0, '0');
INSERT INTO rueckmeldung_status_alias (id, rueckmeldung_status_id, alias) values((SELECT NEXTVAL('id_seq')),0, 'n');
INSERT INTO rueckmeldung_status_alias (id, rueckmeldung_status_id, alias) values((SELECT NEXTVAL('id_seq')),0, 'nein');
INSERT INTO rueckmeldung_status_alias (id, rueckmeldung_status_id, alias) values((SELECT NEXTVAL('id_seq')),0, 'antwort ist nein');
INSERT INTO rueckmeldung_status_alias (id, rueckmeldung_status_id, alias) values((SELECT NEXTVAL('id_seq')),0, 'no');
INSERT INTO rueckmeldung_status_alias (id, rueckmeldung_status_id, alias) values((SELECT NEXTVAL('id_seq')),1, '1');
INSERT INTO rueckmeldung_status_alias (id, rueckmeldung_status_id, alias) values((SELECT NEXTVAL('id_seq')),1, 'j');
INSERT INTO rueckmeldung_status_alias (id, rueckmeldung_status_id, alias) values((SELECT NEXTVAL('id_seq')),1, 'ja');
INSERT INTO rueckmeldung_status_alias (id, rueckmeldung_status_id, alias) values((SELECT NEXTVAL('id_seq')),1, 'antwort ist ja');
INSERT INTO rueckmeldung_status_alias (id, rueckmeldung_status_id, alias) values((SELECT NEXTVAL('id_seq')),1, 'yes');
INSERT INTO rueckmeldung_status_alias (id, rueckmeldung_status_id, alias) values((SELECT NEXTVAL('id_seq')),2, '2');
INSERT INTO rueckmeldung_status_alias (id, rueckmeldung_status_id, alias) values((SELECT NEXTVAL('id_seq')),2, 's');
INSERT INTO rueckmeldung_status_alias (id, rueckmeldung_status_id, alias) values((SELECT NEXTVAL('id_seq')),2, 'spaeter');
INSERT INTO rueckmeldung_status_alias (id, rueckmeldung_status_id, alias) values((SELECT NEXTVAL('id_seq')),2, 'später');
INSERT INTO rueckmeldung_status_alias (id, rueckmeldung_status_id, alias) values((SELECT NEXTVAL('id_seq')),2, 'later');
INSERT INTO rueckmeldung_status_alias (id, rueckmeldung_status_id, alias) values((SELECT NEXTVAL('id_seq')),2, 'ich komme später');



--
-- zabos.schleife
--
CREATE TABLE schleife (
  id              bigint NOT NULL ,
  name            varchar(64)      NOT NULL,
  kuerzel         varchar(5)       NOT NULL            , -- 'Dient z.b. bei SMS Alarmierung als Ident.Merkmal; z.b. Kleine Schleife',
  fuenfton        varchar(5)                            , -- 'Dient bei Funkalarmierung als Ident.Merkmal',
  organisationseinheit_id bigint NOT NULL     , -- 'FK organisationseinheit.id',
  beschreibung    text     default NULL        , -- 'optionaler Freitext',
  geloescht       boolean        NOT NULL default false , -- '1=schleife wurde geloescht',
  statusreport_fuenfton       boolean        NOT NULL 	default false , -- '1=zu dieser Schleife soll eine Statusreport-sms gesendet werden',
  ist_abrechenbar	boolean	NOT NULL default true , -- ' 2007-06-07 CKL: Ist die Schleife abrechenbar? Gewuenscht von CST
  folgeschleife_id int NULL , -- '2009-11-23 CKL: ZABOS 1.2.0
  rueckmeldeintervall bigint NULL default '900' , -- '2009-11-23 CKL: ZABOS 1.2.0
  drucker_kuerzel	varchar(255) NULL , -- 'Kuerzel/IP fuer den Drucker, auf dem diese Schleife gedruckt wird
  PRIMARY KEY(id),
  UNIQUE(name),
  UNIQUE(kuerzel),
  FOREIGN KEY(organisationseinheit_id) REFERENCES organisationseinheit(id),
  FOREIGN KEY(folgeschleife_id) REFERENCES schleife(id)
); --='Gruppiert eine Anzahl Personen';

--
-- bereich_in_schleife
-- 
CREATE TABLE bereich_in_schleife (
  id 			bigint NOT NULL ,
  bereich_id	bigint NOT NULL ,
  funktionstraeger_id bigint NOT NULL,
  schleife_id bigint NOT NULL,
  sollstaerke int NOT NULL,
  geloescht       boolean        NOT NULL default false , -- 1=Funktionstraeger wurde geloescht
  PRIMARY KEY(id),
  FOREIGN KEY(bereich_id) REFERENCES bereich(id),
  FOREIGN KEY(funktionstraeger_id) REFERENCES funktionstraeger(id),
  FOREIGN KEY(schleife_id) REFERENCES schleife(id)
); --='Definiert die Bereiche der Funktionstraeger innerhalb einer Schleife
--
-- zabos.person_in_rolle_in_system
--
CREATE TABLE person_in_rolle_in_system (
  id        bigint NOT NULL ,
  person_id bigint NOT NULL , -- 'FK person.id',
  rolle_id  bigint NOT NULL , -- 'FK rolle.id',
  PRIMARY KEY(id),
  UNIQUE(person_id,rolle_id),
  FOREIGN KEY(person_id) REFERENCES person(id),
  FOREIGN KEY(rolle_id) REFERENCES rolle(id)
); -- Zuweisung einer Rolle zu einer Person in Bezug auf das gesamte System



--
-- zabos.person_in_rolle_in_organisation
--
CREATE TABLE person_in_rolle_in_organisation (
  id              bigint NOT NULL,
  person_id       bigint NOT NULL, -- FK person.id
  rolle_id        bigint NOT NULL, -- FK rolle.id
  organisation_id bigint NOT NULL, -- FK organisation.id
  PRIMARY KEY(id),
  UNIQUE(person_id,rolle_id,organisation_id),
  FOREIGN KEY(person_id) REFERENCES person(id),
  FOREIGN KEY(rolle_id) REFERENCES rolle(id),
  FOREIGN KEY(organisation_id) REFERENCES organisation(id)
); -- Zuweisung einer Rolle zu einer Person in Bezug auf eine Organisation



--
-- zabos.person_in_rolle_in_organisationseinheit
--
CREATE TABLE person_in_rolle_in_organisationseinheit (
  id                      bigint NOT NULL,
  person_id               bigint NOT NULL, -- FK person.id
  rolle_id                bigint NOT NULL, -- FK rolle.id
  organisationseinheit_id bigint NOT NULL, -- FK organisationseinheit.id
  PRIMARY KEY(id),
  UNIQUE(person_id,rolle_id,organisationseinheit_id),
  FOREIGN KEY(person_id) REFERENCES person(id),
  FOREIGN KEY(rolle_id) REFERENCES rolle(id),
  FOREIGN KEY(organisationseinheit_id) REFERENCES organisationseinheit(id)
); -- Zuweisung einer Rolle zu einer Person in Bezug auf eine Organisationseinheit



--
-- zabos.person_in_rolle_in_schleife
--
CREATE TABLE person_in_rolle_in_schleife (
  id          bigint NOT NULL,
  person_id   bigint NOT NULL, -- FK person.id
  rolle_id    bigint NOT NULL, -- FK rolle.id
  schleife_id bigint NOT NULL, -- FK schleife.id
  PRIMARY KEY(id),
  UNIQUE(person_id,rolle_id,schleife_id),
  FOREIGN KEY(person_id) REFERENCES person(id),
  FOREIGN KEY(rolle_id) REFERENCES rolle(id),
  FOREIGN KEY(schleife_id) REFERENCES schleife(id)
); -- Zuweisung einer Rolle zu einer Person in Bezug auf eine Schleife



--
-- zabos.schleife_in_alarm
--
CREATE TABLE schleife_in_alarm (
  id          bigint NOT NULL ,
  schleife_id bigint NOT NULL , -- 'FK schleife.id',
  alarm_id    bigint NOT NULL , -- 'FK alarm.id',
  PRIMARY KEY(id),
  UNIQUE (schleife_id,alarm_id),
  FOREIGN KEY(schleife_id) REFERENCES schleife(id),
  FOREIGN KEY(alarm_id) REFERENCES alarm(id)
); --='Zuordnung einer Schleife zu einem Alarm';

--
-- zabos.bereich_in_alarm
--
CREATE TABLE bereich_in_alarm (
  id 		bigint NOT NULL ,
  alarm_id	bigint NOT NULL,
  bereich_in_schleife_id bigint NOT NULL ,
  aktivierung bigint NOT NULL,
  aktiv boolean NOT NULL default true, 
  PRIMARY KEY(id),
  UNIQUE(bereich_in_schleife_id, alarm_id),
  FOREIGN KEY(bereich_in_schleife_id) REFERENCES bereich_in_schleife(id),
  FOREIGN KEY(alarm_id) REFERENCES alarm(id)
);
--
-- zabos.smsout_statusid
--
CREATE TABLE smsout_status (
  id bigint NOT NULL,
  name varchar(96) NOT NULL,
  PRIMARY KEY(id)
); -- Hinweis: einige StatusIds werden nur intern waehrend des Nachrichtenversands verwendet

INSERT INTO smsout_status (id,name) VALUES(0,'idle'); -- alias 'unsent'
INSERT INTO smsout_status (id,name) VALUES(7,'retrying');
INSERT INTO smsout_status (id,name) VALUES(8,'Time out');
INSERT INTO smsout_status (id,name) VALUES(9,'queued');
INSERT INTO smsout_status (id,name) VALUES(10,'SMS verschickt');
INSERT INTO smsout_status (id,name) VALUES(11,'Socket Fehler');
INSERT INTO smsout_status (id,name) VALUES(12,'Gateway Fehler');

INSERT INTO smsout_status (id,name) VALUES(200, 'SMS erfolgreich übertragen');
INSERT INTO smsout_status (id,name) VALUES(100, 'Keine Verbindung zum Datenbankserver');
INSERT INTO smsout_status (id,name) VALUES(101, 'Keine Verbindung zur Datenbank');
INSERT INTO smsout_status (id,name) VALUES(102, 'Datenbankfehler');
INSERT INTO smsout_status (id,name) VALUES(111, 'Benutzer unbekannt');
INSERT INTO smsout_status (id,name) VALUES(112, 'Authentifizierung fehlgeschlagen');
INSERT INTO smsout_status (id,name) VALUES(113, 'Versandzeitpunkt falsch');
INSERT INTO smsout_status (id,name) VALUES(114, 'Maximale Anzahl von Empfängern überschritten');
INSERT INTO smsout_status (id,name) VALUES(400, 'SMS-ONE: Schnittstellenfehler');
INSERT INTO smsout_status (id,name) VALUES(401, 'SMS-ONE: Falsche Benutzerdaten');
INSERT INTO smsout_status (id,name) VALUES(402, 'SMS-ONE: SMS-Nachricht fehlt');
INSERT INTO smsout_status (id,name) VALUES(403, 'SMS-ONE: Empfängernummer fehlt');
INSERT INTO smsout_status (id,name) VALUES(404, 'SMS-ONE: Falsche Absenderkennung');
INSERT INTO smsout_status (id,name) VALUES(405, 'SMS-ONE: Kein Prepaid-Guthaben');
INSERT INTO smsout_status (id,name) VALUES(406, 'SMS-ONE: SMS-Limit überschritten');

--
-- zabos.smsout
--
CREATE TABLE smsout (
  id            bigint       NOT NULL ,
  telefon_id    bigint       NOT NULL ,    -- Die Nummer an die die SMS Nachricht verschickt wurde
  nachricht     varchar(160) NOT NULL ,    -- Der Inhalt der SMS Nachricht
  zeitpunkt     bigint       NOT NULL ,    -- Unix timestamp; Absende Zeitpunkt
  status_id     integer      NOT NULL ,    -- Aktueller Status Code des Versand Prozesses.
  context       varchar(128) default NULL, -- Kontext fuer Gateway-Script (optional)
  context_alarm varchar(128) default NULL, -- Kontext fuer Gateway-Script (AlarmNr)
  context_o     varchar(128) default NULL, -- Kontext fuer Gateway-Script (Organisationsname)
  context_oe    varchar(128) default NULL, -- Kontext fuer Gateway-Script (Organisationseinheitname)
  ist_festnetz	boolean default FALSE NOT NULL,
  PRIMARY KEY(id),
  FOREIGN KEY(telefon_id) REFERENCES telefon(id),
  FOREIGN KEY(status_id) REFERENCES smsout_status(id)
); --='Alle versendeten SMS Nachrichten';


--
-- zabos.schleife_in_smsout
--
CREATE TABLE schleife_in_smsout (
  id                   bigint     NOT NULL ,
  schleife_in_alarm_id bigint NOT NULL , -- 'Schleife, auf die sich die SMS bezieht',
  smsout_id            bigint NOT NULL , -- 'FK smsout.id',
  PRIMARY KEY(id),
  UNIQUE(schleife_in_alarm_id,smsout_id),
  FOREIGN KEY(schleife_in_alarm_id) REFERENCES schleife_in_alarm(id),
  FOREIGN KEY(smsout_id) REFERENCES smsout(id)
); --='Zuordnung einer versendeten SMS Nachricht zu Schleife';


--
-- zabos.smsin
--
CREATE TABLE smsin (
  id              bigint       NOT NULL,
  rufnummer       varchar(32)  NOT NULL, -- Die Nummer von der diese SMS Nachricht abgeschickt wurde
  modem_rufnummer varchar(32)  NOT NULL, -- Die Nummer des GSM-Modems, welches diese Nachricht empfangen hat
  nachricht       varchar(255) NOT NULL, -- Der Inhalt der SMS Nachricht
  zeitpunkt       bigint       NOT NULL, -- Unix timestamp; Empfangs Zeitpunkt
  gelesen         boolean      NOT NULL default false, -- true=nachricht wurde verarbeitet, false=ungelesen
  PRIMARY KEY(id)
 ); --='Alle empfangenen SMS Nachrichten';



--
-- zabos.fuenfton
--
CREATE TABLE fuenfton (
  id        bigint     NOT NULL,
  folge     varchar(5) NOT NULL, -- Tonfolge, z.b. 01w12
  zeitpunkt bigint     NOT NULL, -- Unix timestamp; Empfangs Zeitpunkt
  PRIMARY KEY(id)
); -- Alle empfangenen 5-Tonfolge-Rufe


--
-- zabos.system_konfiguration
--
CREATE TABLE system_konfiguration (
  id                      bigint      NOT NULL default 0   , -- Diese Tabelle darf nur einen Eintrag enthalten, id ist 0
  alarm_timeout           bigint      NOT NULL default 900 , -- Gueltigkeit in sek fuer einen Alarm
  com_5ton                integer,                      -- Portnummer des 5Ton Empfaengers 
  reaktivierung_timeout   integer     NOT NULL default 1800, -- Timeout, nach dem das System automatisch wieder aktiviert wird
  reaktivierung_zeitpunkt bigint,                       -- Zeitpunkt zu dem das System automatisch wieder aktiviert wird
  smsin_timeout           integer     NOT NULL default 300, -- Zeit in sek. nach der eine empfangene SMS als zu alt betrachtet wird
  alarmhistorie_laenge    integer     NOT NULL default 10, -- Anzahl der Eintraege in der Alarmhistorie
  PRIMARY KEY(id)
);
INSERT INTO system_konfiguration (alarm_timeout,com_5ton,smsin_timeout) 
VALUES(900, NULL, 300);


--
-- zabos.system_konfiguration_mc35
--
CREATE TABLE system_konfiguration_mc35 (
  id            bigint      NOT NULL,
  com_port      integer     NOT NULL, -- port nummer 0..n
  rufnummer     varchar(64) NOT NULL, -- 0049160012345678 
  pin1          varchar(6)  NOT NULL, -- SIM PIN1
  alarm_modem   boolean     NOT NULL default false,
  zeitpunkt_letzter_sms_selbsttest bigint, 
  PRIMARY KEY(id)
); -- Konfiguration der verfuegbaren GSM Modems

-- zabos.webclient_konfiguration
--
CREATE TABLE webclient_konfiguration (
  id bigint NOT NULL ,
  PRIMARY KEY(id)
); --='Parameter fuer den Browserclient; evtl. nicht notwendig';


---
--- Funktion zur Umwandlung von Unixtimestamps in Millisekunden in einen TIMESTAMP
---
CREATE OR REPLACE FUNCTION "public"."u2time" (unixtime bigint) RETURNS TIMESTAMP WITHOUT TIME ZONE AS
$body$
DECLARE
  datum timestamp;
BEGIN
  SELECT TIMESTAMP WITH TIME ZONE 'epoch' + (unixtime/1000) * INTERVAL '1 second' INTO datum;
  RETURN datum;
END;
$body$
LANGUAGE 'plpgsql' VOLATILE CALLED ON NULL INPUT SECURITY INVOKER;

CREATE OR REPLACE FUNCTION public.first_agg ( anyelement, anyelement )
RETURNS anyelement AS $$
        SELECT CASE WHEN $1 IS NULL THEN $2 ELSE $1 END;
$$ LANGUAGE SQL STABLE;

-- And then wrap an aggreagate around it
CREATE AGGREGATE public.first (
        sfunc    = public.first_agg,
        basetype = anyelement,
        stype    = anyelement
);
--
-- Liefert die Laufzeit des Systems zurueck, von der ersten bis zur letzten SMS
-- 
CREATE OR REPLACE VIEW "public"."v_laufzeit" (
    jahr,
    monat)
AS
SELECT
    to_char(u2time(smsout.zeitpunkt), 'YYYY'::text) AS jahr,
    to_char(u2time(smsout.zeitpunkt), 'FMMM'::text) AS monat
FROM
  smsout
GROUP BY
  jahr,
  monat
ORDER BY
  jahr,
  monat;

--- 
--- Liefert die SMS, die in einer Schleife gesendet wurden.
--- Schleifen, in denen noch nichts gesendet wurde, werden ignoriert
---  
CREATE OR REPLACE VIEW "public"."v_sms_in_schleifen" (
    jahr,
    monat,
    sms_anzahl,
    schleife_id)
AS
SELECT
    to_char(u2time(smsout.zeitpunkt), 'YYYY'::text) AS jahr,
    to_char(u2time(smsout.zeitpunkt), 'FMMM'::text) AS monat,
    count(smsout.id) AS sms_anzahl,
    schleife.id AS schleife_id
FROM
  smsout,
  telefon,
  person,
  alarm,
  schleife_in_alarm,
  schleife
WHERE
  smsout.telefon_id = telefon.id
  AND
  telefon.person_id = person.id
  AND
  person.oe_kostenstelle IS NULL
  AND
  lower(smsout.context_alarm) = to_hex(alarm.reihenfolge)
  AND
  schleife_in_alarm.alarm_id = alarm.id
  AND
  schleife.id = schleife_in_alarm.schleife_id
GROUP BY
  jahr,
  monat,
  schleife.id
ORDER BY
  jahr,
  monat;
  
--
-- Ermitteln der SMS-Anzahlen fuer einen bestimmten Monat
-- gegliedert nach Organisation und Organisationseinheiten
--
-- Aufruf zur Auswertung aus der Shell:
-- echo -e "\\\encoding iso-8859-1 \n select * from v_sms_statistik where jahr=2006 and monat=11" | psql -d zabos -t -A -F ";"
--  
CREATE OR REPLACE VIEW "public"."v_sms_statistik" (
    jahr,
    monat,
    o_name,
    oe_name,
    sms_anzahl)
AS
SELECT 
	s.jahr, 
	s.monat, 
	s.o_name, 
	s.oe_name, 
	SUM(sms_anzahl) as sms_anzahl
FROM 
(
	-- Statistik fuer die SMSen, die ueber eine spezifische Kostenstelle laufen
	(
		SELECT to_char(u2time(smsout.zeitpunkt), 'YYYY'::text) AS jahr,
			to_char(u2time(smsout.zeitpunkt), 'FMMM'::text) AS monat, organisation.name
			AS o_name, organisationseinheit.name AS oe_name, count(smsout.id) AS sms_anzahl
		FROM smsout, telefon, person, alarm, organisationseinheit, organisation
		WHERE ((((((smsout.telefon_id = telefon.id) AND (telefon.person_id =
			person.id)) AND (person.oe_kostenstelle IS NOT NULL)) AND
			(lower((smsout.context_alarm)::text) = to_hex(alarm.reihenfolge))) AND
			(organisationseinheit.id = person.oe_kostenstelle)) AND (organisation.id =
			organisationseinheit.organisation_id))
		GROUP BY to_char(u2time(smsout.zeitpunkt), 'YYYY'::text),
			to_char(u2time(smsout.zeitpunkt), 'FMMM'::text), organisation.name,
			organisationseinheit.name
		ORDER BY to_char(u2time(smsout.zeitpunkt), 'YYYY'::text),
			to_char(u2time(smsout.zeitpunkt), 'FMMM'::text), organisation.name,
			organisationseinheit.name
	)
	UNION ALL
	-- Statistik fuer die Personen, die keine Kostenstelle definiert haben
	(
		SELECT to_char(u2time(smsout.zeitpunkt), 'YYYY'::text) AS jahr,
			to_char(u2time(smsout.zeitpunkt), 'FMMM'::text) AS monat, organisation.name
			AS o_name, organisationseinheit.name AS oe_name, count(smsout.id) AS sms_anzahl
		FROM smsout, telefon, person, alarm, schleife_in_alarm, schleife,
			organisationseinheit, organisation
		WHERE ((((((((smsout.telefon_id = telefon.id) AND (telefon.person_id =
			person.id)) AND (person.oe_kostenstelle IS NULL)) AND
			(lower((smsout.context_alarm)::text) = to_hex(alarm.reihenfolge))) AND
			(schleife_in_alarm.alarm_id = alarm.id)) AND (schleife.id =
			schleife_in_alarm.schleife_id)) AND (organisationseinheit.id =
			schleife.organisationseinheit_id)) AND (organisation.id =
			organisationseinheit.organisation_id))
		GROUP BY to_char(u2time(smsout.zeitpunkt), 'YYYY'::text),
			to_char(u2time(smsout.zeitpunkt), 'FMMM'::text), organisation.name,
			organisationseinheit.name
		ORDER BY to_char(u2time(smsout.zeitpunkt), 'YYYY'::text),
			to_char(u2time(smsout.zeitpunkt), 'FMMM'::text), organisation.name,
			organisationseinheit.name
	)
)
AS s
GROUP BY s.jahr, s.monat, s.o_name, s.oe_name
ORDER BY s.jahr, s.monat;
  
  
--
-- Ermittelt die Anzahl der aktuell konfigurierten Schleifen 
-- gegliedert nach Organisation und Organisationseinheiten
--
-- Aufruf zur Auswertung aus der Shell:
-- echo -e "\\\encoding iso-8859-1 \n select * from v_schleifen_statistik" | psql -d zabos -t -A -F ";"
CREATE OR REPLACE VIEW "public"."v_schleifen_statistik" (
    o_name,
    oe_name,
    schleifen_anzahl,
    organisation_id,
    organissationseinheit_id)
AS
SELECT organisation.name AS o_name, organisationseinheit.name AS oe_name,
    count(schleife.name) AS schleifen_anzahl, organisationseinheit.id AS
    organisation_id, organisation.id AS organissationseinheit_id
FROM organisation, organisationseinheit, schleife
WHERE (((organisationseinheit.organisation_id = organisation.id) AND
    (schleife.organisationseinheit_id = organisationseinheit.id)) AND
    (schleife.geloescht = false))
GROUP BY organisation.name, organisationseinheit.name, organisation.id,
    organisationseinheit.id
ORDER BY organisation.name, organisationseinheit.name;


-- View zum einfacheren Auslesen der Person/Rollen-Kombination
-- Soll Performance-Probleme bei zabos1 behebenDROP VIEW "public"."v_alle_rollen";

CREATE OR REPLACE VIEW "public"."v_alle_rollen" (
    id,
    name,
    vorname,
    nachname,
    pin,
    passwd,
    abwesend_bis,
    beschreibung,
    email,
    geloescht,
    oe_kostenstelle,
    abwesend_von,
    funktionstraeger_id,
    bereich_id,
    report_optionen,
    in_folgeschleife,
    erstellt_von_person_id,
    rolle_id,
    rolle_name,
    rolle_beschreibung,
    kontext,
    kontext_id,
    kontext_name,
    parent_id,
    parent_name)
AS
(( SELECT p.id,
          p.name,
          p.vorname,
          p.nachname,
          p.pin,
          p.passwd,
          p.abwesend_bis,
          p.beschreibung,
          p.email,
          p.geloescht,
          p.oe_kostenstelle,
          p.abwesend_von,
          p.funktionstraeger_id,
          p.bereich_id,
          p.report_optionen,
          p.in_folgeschleife,
          p.erstellt_von_person_id,
          r.id AS rolle_id,
          r.name AS rolle_name,
          r.beschreibung AS rolle_beschreibung,
          0 AS kontext,
          0 AS kontext_id,
          'System'          AS kontext_name,
          0 AS parent_id,
          NULL::unknown AS parent_name
   FROM person_in_rolle_in_system pirisys
        LEFT JOIN person p ON p.id = pirisys.person_id
        LEFT JOIN rolle r ON pirisys.rolle_id = r.id
   UNION
   SELECT p.id,
          p.name,
          p.vorname,
          p.nachname,
          p.pin,
          p.passwd,
          p.abwesend_bis,
          p.beschreibung,
          p.email,
          p.geloescht,
          p.oe_kostenstelle,
          p.abwesend_von,
          p.funktionstraeger_id,
          p.bereich_id,
          p.report_optionen,
          p.in_folgeschleife,
          p.erstellt_von_person_id,
          r.id AS rolle_id,
          r.name AS rolle_name,
          r.beschreibung AS rolle_beschreibung,
          1 AS kontext,
          o.id AS kontext_id,
          o.name AS kontext_name,
          0 AS parent_id,
          'System'          AS parent_name
   FROM person_in_rolle_in_organisation pirio
        LEFT JOIN person p ON p.id = pirio.person_id
        LEFT JOIN rolle r ON pirio.rolle_id = r.id
        LEFT JOIN organisation o ON pirio.organisation_id = o.id)
UNION 
 SELECT p.id,
        p.name,
        p.vorname,
        p.nachname,
        p.pin,
        p.passwd,
        p.abwesend_bis,
        p.beschreibung,
        p.email,
        p.geloescht,
        p.oe_kostenstelle,
        p.abwesend_von,
        p.funktionstraeger_id,
        p.bereich_id,
        p.report_optionen,
        p.in_folgeschleife,
        p.erstellt_von_person_id,
        r.id AS rolle_id,
        r.name AS rolle_name,
        r.beschreibung AS rolle_beschreibung,
        2 AS kontext,
        oe.id AS kontext_id,
        oe.name AS kontext_name,
        oe.organisation_id AS parent_id,
        o.name AS parent_name
 FROM person_in_rolle_in_organisationseinheit pirioe
      LEFT JOIN person p ON p.id = pirioe.person_id
      LEFT JOIN rolle r ON pirioe.rolle_id = r.id
      LEFT JOIN organisationseinheit oe ON pirioe.organisationseinheit_id =
      oe.id
      LEFT JOIN organisation o ON oe.organisation_id = o.id)
UNION 
 SELECT p.id,
        p.name,
        p.vorname,
        p.nachname,
        p.pin,
        p.passwd,
        p.abwesend_bis,
        p.beschreibung,
        p.email,
        p.geloescht,
        p.oe_kostenstelle,
        p.abwesend_von,
        p.funktionstraeger_id,
        p.bereich_id,
        p.report_optionen,
        p.in_folgeschleife,
        p.erstellt_von_person_id,
        r.id AS rolle_id,
        r.name AS rolle_name,
        r.beschreibung AS rolle_beschreibung,
        3 AS kontext,
        s.id AS kontext_id,
        s.name AS kontext_name,
        s.organisationseinheit_id AS parent_id,
        oe.name AS parent_name
 FROM person_in_rolle_in_schleife piris
      LEFT JOIN person p ON p.id = piris.person_id
      LEFT JOIN rolle r ON piris.rolle_id = r.id
      LEFT JOIN schleife s ON piris.schleife_id = s.id
      LEFT JOIN organisationseinheit oe ON s.organisationseinheit_id = oe.id;

--- View zum Anzeigen aller eingehender Alarme inkl. der Personen und deren Bereichen/Funktionstraegern
CREATE OR REPLACE VIEW "public"."v_alarme" (
    alarm_id,
    alarm_aktiv,
    alarm_zeit,
    alarm_entwarn_zeit,
    alarm_reihenfolge,
    person_id,
    pia_kommentar,
    pia_kommentar_leitung,
    pia_rueckmeldung_status_id,
    p_name,
    p_nachname,
    p_vorname,
    b_id,
    b_name,
    f_id,
    f_name)
AS
 SELECT a.id AS alarm_id,
        a.aktiv AS alarm_aktiv,
        a.alarm_zeit,
        a.entwarn_zeit AS alarm_entwarn_zeit,
        a.reihenfolge AS alarm_reihenfolge,
        pia.person_id,
        pia.kommentar AS pia_kommentar,
        pia.kommentar_leitung AS pia_kommentar_leitung,
        pia.rueckmeldung_status_id AS pia_rueckmeldung_status_id,
        p.name AS p_name,
        p.nachname AS p_nachname,
        p.vorname AS p_vorname,
        b.id AS b_id,
        b.name AS b_name,
        f.id AS f_id,
        f.beschreibung AS f_name
 FROM person_in_alarm pia,
      alarm a,
      person p,
      bereich b,
      funktionstraeger f
 WHERE pia.alarm_id = a.id AND
       pia.person_id = p.id AND
       f.id = p.funktionstraeger_id AND
       b.id = p.bereich_id
 ORDER BY a.id,
          f.kuerzel,
          b.name;

CREATE OR REPLACE VIEW "public"."v_hierarchie" (
    o_id,
    o_name,
    oe_id,
    oe_name,
    s_id,
    s_name)
AS
 SELECT o.id AS o_id,
        o.name AS o_name,
        oe.id AS oe_id,
        oe.name AS oe_name,
        s.id AS s_id,
        s.name AS s_name
 FROM organisation o
      LEFT JOIN organisationseinheit oe ON o.id = oe.organisation_id
      LEFT JOIN schleife s ON oe.id = s.organisationseinheit_id
 ORDER BY o.name,
          oe.name,
          s.name;

CREATE OR REPLACE VIEW "public"."v_personen_in_schleifen" (
    id,
    name,
    nachname,
    vorname,
    passwd,
    pin,
    abwesend_bis,
    beschreibung,
    funktionstraeger_id,
    bereich_id,
    email,
    geloescht,
    oe_kostenstelle,
    report_optionen,
    erstellt_von_person_id,
    schleife_id)
AS
SELECT DISTINCT var.id, var.name, var.nachname, var.vorname, var.passwd,
    var.pin, var.abwesend_bis, var.beschreibung, var.funktionstraeger_id,
    var.bereich_id, var.email, var.geloescht, var.oe_kostenstelle,
    var.report_optionen, var.erstellt_von_person_id, vh.s_id AS schleife_id
FROM v_alle_rollen var, v_hierarchie vh
WHERE var.kontext = 0 OR var.kontext = 1 AND var.kontext_id = vh.o_id OR
    var.kontext = 2 AND var.kontext_id = vh.oe_id OR var.kontext = 3 AND
    var.kontext_id = vh.s_id
ORDER BY var.id, var.name, var.nachname, var.vorname, var.passwd, var.pin,
    var.abwesend_bis, var.beschreibung, var.funktionstraeger_id,
    var.bereich_id, var.email, var.geloescht, var.oe_kostenstelle,
    var.report_optionen, var.erstellt_von_person_id, vh.s_id;

CREATE OR REPLACE FUNCTION "public"."find_zuerst_ausgeloeste_schleife" (
  "schleife_id" bigint,
  "alarm_id" bigint,
  "max_vorgaenger" bigint
)
RETURNS bigint AS
$body$
DECLARE
  ret bigint;
  temp_var bigint;
  cnt bigint;
BEGIN
    ret = schleife_id;
    cnt = max_vorgaenger;
    
    LOOP
      SELECT s.id into temp_var FROM 
             schleife s,   
             schleife_in_alarm sia,
             alarm a
      WHERE                 
             s.id = sia.schleife_id
             AND
             a.id = sia.alarm_id
             AND 
             s.folgeschleife_id = ret 
             AND                                  
             a.id = alarm_id;   
      
      cnt = cnt - 1;
    
      IF cnt = 0 OR temp_var IS NULL
      THEN
         return ret;
      END IF;
                   
      ret = temp_var;
      
    END LOOP;
END;
$body$
LANGUAGE 'plpgsql'
VOLATILE
CALLED ON NULL INPUT
SECURITY INVOKER
COST 100;

CREATE OR REPLACE FUNCTION "public"."find_ausgeloeste_schleifen" (
  "schleife_id" bigint,
  "alarm_id" bigint,
  "max_vorgaenger" integer
)
RETURNS bigint [] AS
$body$
/* 
Liefert alle Schleifen-IDs zurück, die *nachfolgend* zu der übergebenen 
Schleife übermittelt worden sind.
Schleifenkombination: S1 -> S2 -> S3
find_ausgeloeste_schleifen(S2, $alarmId, $maxNachfolger) => array(S1)
*/
DECLARE
  ret BIGINT[];
  temp_var bigint;
  cnt bigint;
BEGIN
    cnt = max_vorgaenger;
    temp_var = schleife_id;
        
    LOOP
      SELECT s.id into temp_var FROM 
             schleife s,   
             schleife_in_alarm sia,
             alarm a
      WHERE                 
             s.id = sia.schleife_id
             AND
             a.id = sia.alarm_id
             AND 
             s.folgeschleife_id = temp_var 
             AND                                  
             a.id = alarm_id;   
      
      cnt = cnt - 1;
    
      IF cnt = 0 OR temp_var IS NULL
      THEN
         return ret;
      END IF;
                   
      ret = array_append(ret, temp_var);
      
    END LOOP;
END;
$body$
LANGUAGE 'plpgsql'
VOLATILE
CALLED ON NULL INPUT
SECURITY INVOKER
COST 100;

CREATE OR REPLACE FUNCTION "public"."find_nachfolgende_schleifen" (
  "schleife_id" bigint,
  "alarm_id" bigint,
  "max_nachfolger" integer
)
RETURNS bigint [] AS
$body$
/* 
Liefert alle Schleifen-IDs zurück, die *nachfolgend* zu der übergebenen 
Schleife übermittelt worden sind.
Schleifenkombination: S1 -> S2 -> S3
find_nachfolgende_schleifen(S2, $alarmId, $maxNachfolger) => array(S3)
*/

DECLARE
  ret BIGINT[];
  temp_var bigint;
  cnt bigint;
BEGIN
    cnt = max_nachfolger;
    temp_var = schleife_id;
        
    LOOP
      SELECT s.folgeschleife_id into temp_var FROM 
             schleife s,   
             schleife_in_alarm sia,
             alarm a
      WHERE                 
             s.id = sia.schleife_id
             AND
             a.id = sia.alarm_id
             AND 
             s.id = schleife_id
             AND                                  
             a.id = alarm_id;   
      
      cnt = cnt - 1;
    
      IF cnt = 0 OR temp_var IS NULL
      THEN
         return ret;
      END IF;
                   
      ret = array_append(ret, temp_var);
      
    END LOOP;
END;
$body$
LANGUAGE 'plpgsql'
VOLATILE
CALLED ON NULL INPUT
SECURITY INVOKER
COST 100;

CREATE OR REPLACE FUNCTION "public"."find_rueckmeldung" (
  "alarm_id" bigint,
  "schleife_id" bigint,
  "funktionstraeger_id" bigint,
  "bereich_id" bigint,
  "all_in_alarm" boolean
)
RETURNS SETOF "public"."v_alarme" AS
$body$
/*
Findet die Personen, die sich in der FBK einer Schleife (all_in_alarm = false) 
oder aller Schleifen des FBKs (all_in_alarm = true) befinden
*/
DECLARE
BEGIN
-- Alle Schleifen finden, die innerhalb der FBK wirklich ausgeloest worden sind
IF all_in_alarm = true THEN
   RETURN QUERY SELECT v.*
       FROM v_alarme v,
            v_personen_in_schleifen vpis
       WHERE v.alarm_id = alarm_id AND
             v.person_id = vpis.id AND
             v.f_id = funktionstraeger_id AND
             v.b_id = bereich_id AND 
             (vpis.schleife_id = ANY(find_ausgeloeste_schleifen(schleife_id, alarm_id, 5))
              OR vpis.schleife_id = schleife_id
              OR vpis.schleife_id = ANY(find_nachfolgende_schleifen(schleife_id, alarm_id, 5)));
ELSE

  -- Nur den letzten Eintrag finden
  RETURN QUERY 
         SELECT v.* 
         FROM v_alarme v,
              v_personen_in_schleifen vpis
         WHERE v.alarm_id = alarm_id AND
               v.person_id = vpis.id AND
               vpis.schleife_id = schleife_id AND
               v.f_id = funktionstraeger_id AND
               v.b_id = bereich_id;
END IF;

END;
$body$
LANGUAGE 'plpgsql'
VOLATILE
CALLED ON NULL INPUT
SECURITY INVOKER
COST 100 ROWS 1000;

CREATE OR REPLACE VIEW "public"."v_bereich_report_detail" (
    alarm_id,
    alarm_reihenfolge,
    alarm_zeit,
    entwarn_zeit,
    schleife_id,
    schleife_name,
    funktionstraeger_id,
    funktionstraeger_kuerzel,
    funktionstraeger_beschreibung,
    bereich_id,
    bereich_name,
    bereich_sollstaerke,
    hauptschleife_id)
AS
 SELECT a.id AS alarm_id,
        a.reihenfolge AS alarm_reihenfolge,
        a.alarm_zeit,
        a.entwarn_zeit,
        s.id AS schleife_id,
        s.name AS schleife_name,
        f.id AS funktionstraeger_id,
        f.kuerzel AS funktionstraeger_kuerzel,
        f.beschreibung AS funktionstraeger_beschreibung,
        b.id AS bereich_id,
        b.name AS bereich_name,
        bis.sollstaerke AS bereich_sollstaerke,
        find_zuerst_ausgeloeste_schleife(bis.schleife_id, a.id, 5::bigint) AS
        hauptschleife_id
 FROM alarm a,
      bereich_in_alarm bia,
      schleife s,
      bereich_in_schleife bis,
      funktionstraeger f,
      bereich b
 WHERE bia.alarm_id = a.id AND
       bis.id = bia.bereich_in_schleife_id AND
       s.id = bis.schleife_id AND
       f.id = bis.funktionstraeger_id AND
       b.id = bis.bereich_id
 ORDER BY s.name,
          f.beschreibung,
          b.name,
          a.id,
          a.reihenfolge,
          a.alarm_zeit,
          a.entwarn_zeit,
          s.id,
          f.id,
          f.kuerzel,
          b.id;

CREATE OR REPLACE VIEW "public"."v_bereich_report" (
    alarm_id,
    alarm_reihenfolge,
    alarm_zeit,
    entwarn_zeit,
    schleife_id,
    schleife_name,
    funktionstraeger_id,
    funktionstraeger_kuerzel,
    funktionstraeger_beschreibung,
    bereich_id,
    bereich_name,
    bereich_sollstaerke,
    hauptschleife_id,
    personen_in_alarm_gesamt,
    positive_rueckmeldung,
    unbekannt_rueckmeldung)
AS
 SELECT v.alarm_id,
        v.alarm_reihenfolge,
        v.alarm_zeit,
        v.entwarn_zeit,
        v.schleife_id,
        v.schleife_name,
        v.funktionstraeger_id,
        v.funktionstraeger_kuerzel,
        v.funktionstraeger_beschreibung,
        v.bereich_id,
        v.bereich_name,
        v.bereich_sollstaerke,
        v.hauptschleife_id,
        (
          SELECT count(*) AS count
          FROM person_in_alarm pia
          WHERE pia.alarm_id = v.alarm_id
        ) AS personen_in_alarm_gesamt,
        (
          SELECT DISTINCT count(*) AS count
          FROM find_rueckmeldung(v.alarm_id, v.schleife_id,
          v.funktionstraeger_id, v.bereich_id, true) r(alarm_id, alarm_aktiv,
          alarm_zeit, alarm_entwarn_zeit, alarm_reihenfolge, person_id,
          pia_kommentar, pia_kommentar_leitung, pia_rueckmeldung_status_id,
          p_name, p_nachname, p_vorname, b_id, b_name, f_id, f_name)
          WHERE r.pia_rueckmeldung_status_id = 1::bigint
          ORDER BY count(*)
        ) AS positive_rueckmeldung,
        (
          SELECT DISTINCT count(*) AS count
          FROM find_rueckmeldung(v.alarm_id, v.schleife_id,
          v.funktionstraeger_id, v.bereich_id, true) r(alarm_id, alarm_aktiv,
          alarm_zeit, alarm_entwarn_zeit, alarm_reihenfolge, person_id,
          pia_kommentar, pia_kommentar_leitung, pia_rueckmeldung_status_id,
          p_name, p_nachname, p_vorname, b_id, b_name, f_id, f_name)
          WHERE r.pia_rueckmeldung_status_id IS NULL OR
                r.pia_rueckmeldung_status_id <> 1::bigint
          ORDER BY count(*)
        ) AS unbekannt_rueckmeldung
 FROM v_bereich_report_detail v
 WHERE v.hauptschleife_id = v.schleife_id;

-- Wandelt einen Timestamp (als integer) in eine lesbare Zeitangabe der Form YYYY-MM-DD HH12:MI:SS (Dy) um
-- Es wird ein String vom Typ varchar zurueckgeliefert
CREATE OR REPLACE FUNCTION "public"."makedisplaydate" (_timestamp integer) RETURNS varchar AS
$body$
DECLARE
  rDate varchar;
BEGIN
  SELECT to_char('epoch'::timestamptz + (_timestamp) * '1 sec'::interval, 'YYYY-MM-DD HH12:MI:SS (Dy)') INTO rDate;
  RETURN rDate;
END;
$body$
LANGUAGE 'plpgsql' VOLATILE CALLED ON NULL INPUT SECURITY INVOKER;

