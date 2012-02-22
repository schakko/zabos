package de.ecw.zabos.frontend.objects.fassade;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.ecw.zabos.alarm.RueckmeldeStatistik;
import de.ecw.zabos.alarm.ZwischenStatistik;
import de.ecw.zabos.sql.vo.AlarmVO;
import de.ecw.zabos.sql.vo.PersonVO;
import de.ecw.zabos.sql.vo.SchleifeVO;
import de.ecw.zabos.types.id.AlarmQuelleId;

/**
 * Fassadenobjekt. Hält die Zwischentstatistik für einen Alarm
 * 
 * @author ckl
 * 
 */
public class ZwischenStatistikFassade
{
    /**
     * Zwischenstatistik
     */
    private ZwischenStatistik zwischenStatistik = null;

    /**
     * List<SchleifenStatistikFassade>
     */
    private List<SchleifenStatistikFassade> listSchleifen = new ArrayList<SchleifenStatistikFassade>();

    /**
     * List<AlarmiertePersonFassade>
     */
    private List<AlarmiertePersonFassade> listAlarmiertePersonen = new ArrayList<AlarmiertePersonFassade>();

    /**
     * List<AlarmiertePersonFassade>
     */
    private List<AlarmiertePersonFassade> listAlarmiertePersonenJa = new ArrayList<AlarmiertePersonFassade>();

    /**
     * List<AlarmiertePersonFassade>
     */
    private List<AlarmiertePersonFassade> listAlarmiertePersonenNein = new ArrayList<AlarmiertePersonFassade>();

    /**
     * List<AlarmiertePersonFassade>
     */
    private List<AlarmiertePersonFassade> listAlarmiertePersonenSpaeter = new ArrayList<AlarmiertePersonFassade>();

    private AlarmVO alarmVO = null;

    private PersonVO voPerson = null;

    /**
     * Setzt die Zwischenstatistik
     * 
     * @param _zs
     *            Zwischenstatistik
     */
    public void setZwischenStatisitik(ZwischenStatistik _zs)
    {
        this.zwischenStatistik = _zs;

        if (_zs != null)
        {
            setSchleifenStatistik(_zs.mapSchleifenStats);
        }
    }

    /**
     * Setzt die Schleifenstatistik
     * 
     * @param _hm
     *            Hashmap <SchleifeVO,RueckmeldeStatistik>
     */
    public void setSchleifenStatistik(Map<SchleifeVO, RueckmeldeStatistik> _hm)
    {
        if (_hm != null)
        {
            Iterator<SchleifeVO> itSchleifen = _hm.keySet().iterator();
            SchleifenStatistikFassade schleifenStatistik = null;
            SchleifeVO voSchleife = null;

            while (itSchleifen.hasNext())
            {
                voSchleife = (SchleifeVO) itSchleifen.next();
                schleifenStatistik = new SchleifenStatistikFassade();
                schleifenStatistik.setSchleife(voSchleife);
                schleifenStatistik
                                .setRueckmeldeStatistik((RueckmeldeStatistik) _hm
                                                .get(voSchleife));

                listSchleifen.add(schleifenStatistik);
            }
        }
    }

    /**
     * Liefert die Zwischenstatistik zur�ck
     * 
     * @return Zwischenstatistik
     */
    public ZwischenStatistik getZwischenStatistik()
    {
        return this.zwischenStatistik;
    }

    /**
     * Liefert die Rückmeldestatistik zur�ck
     * 
     * @return null oder Rückmeldestatistik
     */
    public RueckmeldeStatistik getRueckmeldeStatistik()
    {
        if (getZwischenStatistik() != null)
        {
            return getZwischenStatistik().gesamt;
        }

        return null;
    }

    /**
     * Liefert die Schleifen-Statistiken als ArrayList
     * 
     * @return ArrayList
     */
    public List<SchleifenStatistikFassade> getSchleifenStatistik()
    {
        return this.listSchleifen;
    }

    /**
     * Setzt den Alarm
     * 
     * @param _alarm
     *            Alarm
     */
    public void setAlarm(AlarmVO _alarm)
    {
        this.alarmVO = _alarm;
    }

    /**
     * Liefert den Alarm
     * 
     * @return AlarmVO
     */
    public AlarmVO getAlarm()
    {
        return this.alarmVO;
    }

    /**
     * Liefert die Person zurück, die den Alarm ausgelöst hat
     * 
     * @return Person
     */
    public PersonVO getPerson()
    {
        return this.voPerson;
    }

    /**
     * Setzt die Person, die den Alarm ausgelöst hat
     * 
     * @param _person
     *            Person
     */
    public void setPerson(PersonVO _person)
    {
        this.voPerson = _person;
    }

    /**
     * Liefert true, wenn die Alarm-Auslösung eine 5-Ton-Auslösung ist
     * 
     * @return true|false
     */
    public boolean isFuenfTonAusloesung()
    {
        return ((alarmVO != null) && (alarmVO.getAlarmQuelleId()
                        .equals(AlarmQuelleId.ID_5TON)));
    }

    /**
     * Liefert true zurück, wenn die Auslösung über das Web geschehen ist
     * 
     * @return true|false
     */
    public boolean isWebAusloesung()
    {
        return ((alarmVO != null) && (alarmVO.getAlarmQuelleId()
                        .equals(AlarmQuelleId.ID_WEB)));

    }

    /**
     * Liefert true zurück, wenn die Auslösung per SMS erfolgte
     * 
     * @return true|false
     */
    public boolean isSmsAusloesung()
    {
        return ((alarmVO != null) && (alarmVO.getAlarmQuelleId()
                        .equals(AlarmQuelleId.ID_SMS)));
    }

    /**
     * Setzt eine alermierte Person, entscheidet selbstständig, in welchem Array
     * sie auftaucht
     * 
     * @param _alarmiertePerson
     *            Alarmierte Person
     */
    public void setAlarmiertePerson(AlarmiertePersonFassade _alarmiertePerson)
    {
        if (_alarmiertePerson.isStatusJa())
        {
            listAlarmiertePersonenJa.add(_alarmiertePerson);
        }
        else if (_alarmiertePerson.isStatusNein())
        {
            listAlarmiertePersonenNein.add(_alarmiertePerson);
        }
        else if (_alarmiertePerson.isStatusSpaeter())
        {
            listAlarmiertePersonenSpaeter.add(_alarmiertePerson);
        }

        listAlarmiertePersonen.add(_alarmiertePerson);
    }

    /**
     * Liefert die Personen, die bei der Alarmierung JA geantwortet haben
     * 
     * @return ArrayList
     */
    public List<AlarmiertePersonFassade> getAlarmiertePersonenJa()
    {
        return listAlarmiertePersonenJa;
    }

    /**
     * Liefert die Personen, die bei der Alarmierung NEIN geantwortet haben
     * 
     * @return ArrayList
     */
    public List<AlarmiertePersonFassade> getAlarmiertePersonenNein()
    {
        return listAlarmiertePersonenNein;
    }

    /**
     * Liefert die Personen, die bei der Alarmierung mit SPAETER geantwortet
     * haben
     * 
     * @return ArrayList
     */
    public List<AlarmiertePersonFassade> getAlarmiertePersonenSpaeter()
    {
        return listAlarmiertePersonenSpaeter;
    }

    /**
     * Liefert ALLE alarmierten Personen. Kann z.B. für ein Delta mit dem Status
     * unbekannt benutzt werden
     * 
     * @return ArrayList
     */
    public List<AlarmiertePersonFassade> getAlarmiertePersonen()
    {
        return listAlarmiertePersonen;
    }
}
