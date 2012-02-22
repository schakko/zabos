package de.ecw.zabos.sql.vo;

import de.ecw.zabos.types.UnixTime;
import de.ecw.zabos.types.id.AlarmId;
import de.ecw.zabos.types.id.BereichId;
import de.ecw.zabos.types.id.FunktionstraegerId;
import de.ecw.zabos.types.id.SchleifeId;

/**
 * Bildet die Statistik für v_bereich_report_detail ab
 * 
 * @author ckl
 * 
 */
public class BereichReportStatistikVO
{
    BereichReportStatistikVO()
    {
    }

    public AlarmId getAlarmId()
    {
        return alarmId;
    }

    public void setAlarmId(AlarmId alarmId)
    {
        this.alarmId = alarmId;
    }

    public int getAlarmReihenfolge()
    {
        return alarmReihenfolge;
    }

    public void setAlarmReihenfolge(int alarmReihenfolge)
    {
        this.alarmReihenfolge = alarmReihenfolge;
    }

    public UnixTime getAlarmZeit()
    {
        return alarmZeit;
    }

    public void setAlarmZeit(UnixTime alarmZeit)
    {
        this.alarmZeit = alarmZeit;
    }

    public UnixTime getEntwarnZeit()
    {
        return entwarnZeit;
    }

    public void setEntwarnZeit(UnixTime entwarnZeit)
    {
        this.entwarnZeit = entwarnZeit;
    }

    public SchleifeId getSchleifeId()
    {
        return schleifeId;
    }

    public void setSchleifeId(SchleifeId schleifeId)
    {
        this.schleifeId = schleifeId;
    }

    public String getSchleifeName()
    {
        return schleifeName;
    }

    public void setSchleifeName(String schleifeName)
    {
        this.schleifeName = schleifeName;
    }

    public FunktionstraegerId getFunktionstraegerId()
    {
        return funktionstraegerId;
    }

    public void setFunktionstraegerId(FunktionstraegerId funktionstraegerId)
    {
        this.funktionstraegerId = funktionstraegerId;
    }

    public String getFunktionstraegerKuerzel()
    {
        return funktionstraegerKuerzel;
    }

    public void setFunktionstraegerKuerzel(String funktionstraegerKuerzel)
    {
        this.funktionstraegerKuerzel = funktionstraegerKuerzel;
    }

    public String getFunktionstraegerBeschreibung()
    {
        return funktionstraegerBeschreibung;
    }

    public void setFunktionstraegerBeschreibung(
                    String funktionstraegerBeschreibung)
    {
        this.funktionstraegerBeschreibung = funktionstraegerBeschreibung;
    }

    public BereichId getBereichId()
    {
        return bereichId;
    }

    public void setBereichId(BereichId bereichId)
    {
        this.bereichId = bereichId;
    }

    public String getBereichName()
    {
        return bereichName;
    }

    public void setBereichName(String bereichName)
    {
        this.bereichName = bereichName;
    }

    public int getBereichSollstaerke()
    {
        return bereichSollstaerke;
    }

    public void setBereichSollstaerke(int bereichSollstaerke)
    {
        this.bereichSollstaerke = bereichSollstaerke;
    }

    public int getRueckmeldungPositiv()
    {
        return rueckmeldungPositiv;
    }

    public void setRueckmeldungPositiv(int rueckmeldungPositiv)
    {
        this.rueckmeldungPositiv = rueckmeldungPositiv;
    }

    public int getRueckmeldungUnbekannt()
    {
        return rueckmeldungUnbekannt;
    }

    public void setRueckmeldungUnbekannt(int rueckmeldungUnbekannt)
    {
        this.rueckmeldungUnbekannt = rueckmeldungUnbekannt;
    }

    public int getPersonenInAlarmGesamt()
    {
        return personenInAlarmGesamt;
    }

    public void setPersonenInAlarmGesamt(int personenInAlarmGesamt)
    {
        this.personenInAlarmGesamt = personenInAlarmGesamt;
    }

    private AlarmId alarmId;

    private int alarmReihenfolge;

    private UnixTime alarmZeit;

    private UnixTime entwarnZeit;

    private SchleifeId schleifeId;

    private String schleifeName;

    private FunktionstraegerId funktionstraegerId;

    private String funktionstraegerKuerzel;

    private String funktionstraegerBeschreibung;

    private BereichId bereichId;

    private String bereichName;

    private int bereichSollstaerke;

    private int rueckmeldungPositiv;

    private int rueckmeldungUnbekannt;

    private int personenInAlarmGesamt;
}
