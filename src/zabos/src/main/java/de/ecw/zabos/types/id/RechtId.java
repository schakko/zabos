package de.ecw.zabos.types.id;

/**
 * Primary Key der zabos.recht Tabelle
 * 
 * @author bsp
 * 
 */
public class RechtId extends BaseId
{

    // System
    public static final RechtId SYSTEM_DEAKTIVIEREN = new RechtId(10);

    public static final RechtId SYSTEMKONFIGURATION_AENDERN = new RechtId(11);

    public static final RechtId LIZENZ_EINSEHEN = new RechtId(12);

    public static final RechtId COMPORTS_FESTLEGEN = new RechtId(13);

    public static final RechtId BEREICHE_FESTLEGEN = new RechtId(14);

    public static final RechtId FUNKTIONSTRAEGER_FESTLEGEN = new RechtId(15);

    // Probealarm
    public static final RechtId PROBEALARM_ADMINISTRIEREN = new RechtId(20);

    // Organisation
    public static final RechtId ORGANISATION_ANLEGEN_LOESCHEN = new RechtId(30);

    public static final RechtId ORGANISATION_AENDERN = new RechtId(31);

    // Organisationseinheit
    public static final RechtId ORGANISATIONSEINHEIT_ANLEGEN_LOESCHEN = new RechtId(
                    40);

    public static final RechtId ORGANISATIONSEINHEIT_AENDERN = new RechtId(41);

    // Person anlegen/l�schen/�ndern zu O,OE,Schleife,System zuordnen
    public static final RechtId PERSON_ANLEGEN_LOESCHEN = new RechtId(50);

    public static final RechtId PERSON_AENDERN = new RechtId(51);

    public static final RechtId EIGENE_PERSON_AENDERN = new RechtId(52);

    public static final RechtId EIGENE_TELEFONE_AENDERN = new RechtId(53);

    // Eigene Abwesenheitszeiten ändern
    public static final RechtId EIGENE_ABWESENHEITSZEITEN_AENDERN = new RechtId(
                    54);

    // Schleife
    public static final RechtId SCHLEIFE_ANLEGEN_LOESCHEN = new RechtId(60);

    public static final RechtId SCHLEIFE_AENDERN = new RechtId(61);

    // Alarm
    public static final RechtId ALARM_AUSLOESEN = new RechtId(70);

    public static final RechtId ALARMHISTORIE_SEHEN = new RechtId(71);

    public static final RechtId ALARMHISTORIE_DETAILS_SEHEN = new RechtId(72);

    public static final RechtId ALARMBENACHRICHTIGUNG_EMPFANGEN = new RechtId(
                    73);

    public static final RechtId ALARM_RUECKMELDUNGSREPORT_EMPFANGEN = new RechtId(
                    74);

    // Rollen
    public static final RechtId ROLLEN_ANLEGEN_LOESCHEN = new RechtId(80);

    public static final RechtId ROLLEN_AENDERN = new RechtId(81);

    public static final RechtId SCHLEIFEN_ABRECHNUNG_FESTLEGEN = new RechtId(82);

    public static final RechtId LEITUNGS_KOMMENTAR_FESTLEGEN = new RechtId(83);

    public static final RechtId OE_KOSTENSTELLE_FESTLEGEN = new RechtId(84);

    public static final RechtId STATISTIK_ANZEIGEN = new RechtId(85);
    
    public static final RechtId PERSONEN_ROLLEN_ZUWEISEN = new RechtId(86);
    
    public RechtId(long _value)
    {
        super(_value);
    }

}
