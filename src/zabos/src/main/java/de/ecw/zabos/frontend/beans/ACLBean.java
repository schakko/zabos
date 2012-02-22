package de.ecw.zabos.frontend.beans;

import java.util.ArrayList;
import java.util.List;

import de.ecw.zabos.sql.vo.RechtVO;
import de.ecw.zabos.types.id.RechtId;

/**
 * Das ACLBean dient für das Zwischenspeichern von Rechten, die der Benutzer in
 * einer View/Action hat.
 * 
 * @author ckl
 */
public class ACLBean
{
    /**
     * Rechte im aktuellen Kontext
     */
    List<String> listRechte = new ArrayList<String>();

    /**
     * Rechte im System
     */
    List<String> listGlobaleRechte = new ArrayList<String>();

    /**
     * Liefert zurück, ob ein Recht verfügbar ist (global oder im aktuellen
     * Kontext), oder nicht.<br />
     * Wurde das Recht nicht gesetzt, ist der Rückgabe-Wert immer false
     * 
     * @param _id
     * @return true|false
     */
    public boolean isRechtVerfuegbar(RechtId _id)
    {
        return (isRechtVerfuegbar(_id, true) || isRechtVerfuegbar(_id, false));
    }

    /**
     * Liefert zurück, ob das Recht im Kontext oder global verfügbar ist
     * 
     * @param _id
     * @param _istGlobalesRecht
     * @return
     */
    public boolean isRechtVerfuegbar(RechtId _id, boolean _istGlobalesRecht)
    {
        List<String> refList = (_istGlobalesRecht) ? (listGlobaleRechte)
                        : (listRechte);

        return refList.contains(_id.getString());
    }

    /**
     * Aktiviert oder deaktiviert ein Recht
     * 
     * @param _id
     * @param _istAktiviert
     * @param _istGlobalesRecht
     *            Legt fest, ob das Recht global benutzt werden soll oder im
     *            aktuellen Kontext
     */
    public void setRechtStatus(RechtId _id, boolean _istAktiviert,
                    boolean _istGlobalesRecht)
    {
        List<String> refList = (_istGlobalesRecht) ? (listGlobaleRechte)
                        : (listRechte);

        // Wenn das Recht noch nicht aktiviert ist und aktiviert werden soll
        if (!isRechtVerfuegbar(_id, _istGlobalesRecht) && _istAktiviert)
        {
            refList.add(_id.getString());
        }

        // Wenn das Recht verfügbar ist und deaktiviert werden soll
        if (isRechtVerfuegbar(_id, _istGlobalesRecht) && !_istAktiviert)
        {
            refList.remove(_id.getString());
        }
    }

    /**
     * Setzt die aktiven Rechte im aktuellen Kontext
     * 
     * @param _rechte
     */
    public void setAktiveRechteImKontext(RechtVO[] _rechte)
    {
        listRechte.clear();

        for (int i = 0, m = _rechte.length; i < m; i++)
        {
            listRechte.add(_rechte[i].getRechtId().getString());
        }
    }

    /**
     * Darf Benutzer Rollen anlegen oder löschen?
     * 
     * @return true|false
     */
    public boolean isRollenAnlegenLoeschenErlaubt()
    {
        return isRechtVerfuegbar(RechtId.ROLLEN_ANLEGEN_LOESCHEN);
    }

    /**
     * Darf Benutzer Rollen ändern?
     * 
     * @return true|false
     */
    public boolean isRollenAendernErlaubt()
    {
        return isRechtVerfuegbar(RechtId.ROLLEN_AENDERN);
    }

    /**
     * Darf Benutzer Alarm-Rückmeldungsreport empfangen?
     * 
     * @return true|false
     */
    public boolean isAlarmRueckmeldungsReportEmpfangenErlaubt()
    {
        return isRechtVerfuegbar(RechtId.ALARM_RUECKMELDUNGSREPORT_EMPFANGEN);
    }

    /**
     * Darf Benutzer Lizenz einsehen
     * 
     * @return true|false
     */
    public boolean isLizenzEinsehenErlaubt()
    {
        return isRechtVerfuegbar(RechtId.LIZENZ_EINSEHEN);
    }

    /**
     * Darf Benutzer die Systemkonfiguration ändern?
     * 
     * @return true|false
     */
    public boolean isSystemKonfigurationAendernErlaubt()
    {
        return isRechtVerfuegbar(RechtId.SYSTEMKONFIGURATION_AENDERN);
    }

    /**
     * Darf Benutzer Alarm auslösen?
     * 
     * @return true|false
     */
    public boolean isAlarmAusloesenErlaubt()
    {
        return isRechtVerfuegbar(RechtId.ALARM_AUSLOESEN);
    }

    /**
     * Darf Benutzer Alarmbenachrichtigung empfangen?
     * 
     * @return true|false
     */
    public boolean isAlarmbenachrichtigungEmpfangenErlaubt()
    {
        return isRechtVerfuegbar(RechtId.ALARMBENACHRICHTIGUNG_EMPFANGEN);
    }

    /**
     * Darf Benutzer Alarmhistorie sehen?
     * 
     * @return true|false
     */
    public boolean isAlarmhistorieSehenErlaubt()
    {
        return isRechtVerfuegbar(RechtId.ALARMHISTORIE_SEHEN);
    }

    /**
     * Darf Benutzer COMPort festlegen
     * 
     * @return true|false
     */
    public boolean isComPortFestlegenErlaubt()
    {
        return isRechtVerfuegbar(RechtId.COMPORTS_FESTLEGEN);
    }

    /**
     * Darf Benutzer Organisation ändern?
     * 
     * @return true|false
     */
    public boolean isOrganisationAendernErlaubt()
    {
        return isRechtVerfuegbar(RechtId.ORGANISATION_AENDERN);
    }

    /**
     * Darf Benutzer Organisation anlegen/löschen ?
     * 
     * @return true|false
     */
    public boolean isOrganisationAnlegenLoeschenErlaubt()
    {
        return isRechtVerfuegbar(RechtId.ORGANISATION_ANLEGEN_LOESCHEN);
    }

    /**
     * Darf Benutzer Organisationseinheiten ändern?
     * 
     * @return true|false
     */
    public boolean isOrganisationseinheitAendernErlaubt()
    {
        return isRechtVerfuegbar(RechtId.ORGANISATIONSEINHEIT_AENDERN);
    }

    /**
     * Darf Benutzer Organisationseinheiten anlegen/löschen?
     * 
     * @return true|false
     */
    public boolean isOrganisationseinheitAnlegenLoeschenErlaubt()
    {
        return isRechtVerfuegbar(RechtId.ORGANISATIONSEINHEIT_ANLEGEN_LOESCHEN);
    }

    /**
     * Darf Benutzer Person ändern?
     * 
     * @return true|false
     */
    public boolean isPersonAendernErlaubt()
    {
        return isRechtVerfuegbar(RechtId.PERSON_AENDERN);
    }

    /**
     * Darf Benutzer Personen anlegen/löschen?
     * 
     * @return true|false
     */
    public boolean isPersonAnlegenLoeschenErlaubt()
    {
        return isRechtVerfuegbar(RechtId.PERSON_ANLEGEN_LOESCHEN);
    }

    /**
     * Darf Benutzer sich selbst ändern?
     * 
     * @return true|false
     */
    public boolean isEigenePersonAendernErlaubt()
    {
        return isRechtVerfuegbar(RechtId.EIGENE_PERSON_AENDERN);
    }

    /**
     * Darf Benutzer seine eigenen Telefone �ndern?
     * 
     * @return true|false
     */
    public boolean isEigeneTelefoneAendernErlaubt()
    {
        return isRechtVerfuegbar(RechtId.EIGENE_TELEFONE_AENDERN);
    }

    /**
     * Darf Benutzer seine eigenen Abwesenheitszeiten �ndern?
     * 
     * @return true|false
     * @since 2006-07-19 CKL: hinzugefügt
     */
    public boolean isEigeneAbwesenheitszeitenAendernErlaubt()
    {
        return isRechtVerfuegbar(RechtId.EIGENE_ABWESENHEITSZEITEN_AENDERN);
    }

    /**
     * Darf Benutzer Probealarme administrieren?
     * 
     * @return true|false
     */
    public boolean isProbealarmAdministrierenErlaubt()
    {
        return isRechtVerfuegbar(RechtId.PROBEALARM_ADMINISTRIEREN);
    }

    /**
     * Darf Benutzer Schleifen ändern?
     * 
     * @return true|false
     */
    public boolean isSchleifeAendernErlaubt()
    {
        return isRechtVerfuegbar(RechtId.SCHLEIFE_AENDERN);
    }

    /**
     * Darf Benutzer neue Schleifen anlegen bzw. bestehende Schleifen löschen?
     * 
     * @return true|false
     */
    public boolean isSchleifeAnlegenLoeschenErlaubt()
    {
        return isRechtVerfuegbar(RechtId.SCHLEIFE_ANLEGEN_LOESCHEN);
    }

    /**
     * Darf Benutzer das System deaktivieren?
     * 
     * @return true|false
     */
    public boolean isSystemDeaktivierenErlaubt()
    {
        return isRechtVerfuegbar(RechtId.SYSTEM_DEAKTIVIEREN);
    }

    /**
     * Darf der Benutzer die Schleife zum Abrechnen festlegen?
     * 
     * @return true|false
     * @author ckl
     */
    public boolean isSchleifeAbrechnungFestlegenErlaubt()
    {
        return isRechtVerfuegbar(RechtId.SCHLEIFEN_ABRECHNUNG_FESTLEGEN);
    }

    /**
     * Darf der Benutzer Funktionsträger festlegen?
     * 
     * @return true|false
     * @author mbi
     */
    public boolean isFunktionstraegerFestlegenErlaubt()
    {
        return isRechtVerfuegbar(RechtId.FUNKTIONSTRAEGER_FESTLEGEN);
    }

    /**
     * Darf der Benutzer Bereiche festlegen?
     * 
     * @return true|false
     * @author mbi
     */
    public boolean isBereicheFestlegenErlaubt()
    {
        return isRechtVerfuegbar(RechtId.BEREICHE_FESTLEGEN);
    }

    /**
     * Darf der Benutzer die OE-Kostenstelle einer Person festlegen
     * 
     * @return
     */
    public boolean isOeKostenstelleFestlegenErlaubt()
    {
        return isRechtVerfuegbar(RechtId.OE_KOSTENSTELLE_FESTLEGEN);
    }

    /**
     * Darf der Benutzer Statisken/Reporte anzeigen
     * 
     * @return
     */
    public boolean isStatistikAnzeigenErlaubt()
    {
        return isRechtVerfuegbar(RechtId.STATISTIK_ANZEIGEN);
    }

    /**
     * Dürfen Leitungskommentare festgelegt werden
     * 
     * @return
     */
    public boolean isLeitungsKommentarFestlegenErlaubt()
    {
        return isRechtVerfuegbar(RechtId.LEITUNGS_KOMMENTAR_FESTLEGEN);
    }

    /**
     * Darf die Person andere Personen Rollen zuweisen
     * 
     * @return
     */
    public boolean isPersonenRollenZuweisenErlaubt()
    {
        return isRechtVerfuegbar(RechtId.PERSONEN_ROLLEN_ZUWEISEN);
    }

}