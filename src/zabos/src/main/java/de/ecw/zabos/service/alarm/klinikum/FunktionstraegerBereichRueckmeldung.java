package de.ecw.zabos.service.alarm.klinikum;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.sql.dao.PersonDAO;
import de.ecw.zabos.sql.vo.PersonInAlarmVO;
import de.ecw.zabos.sql.vo.PersonVO;
import de.ecw.zabos.types.id.BereichId;
import de.ecw.zabos.types.id.FunktionstraegerId;

/**
 * Statistik-Klasse für die Rückmeldungen, aufgeschlüsselt nach dem
 * Funktionsträger und dessen Bereichen
 * 
 * @author ckl
 * 
 */
final public class FunktionstraegerBereichRueckmeldung
{
    private Map<FunktionstraegerId, HashMap<BereichId, ArrayList<PersonRueckmeldungCVO>>> mapRueckmeldung = new HashMap<FunktionstraegerId, HashMap<BereichId, ArrayList<PersonRueckmeldungCVO>>>();

    private PersonInAlarmVO[] personenInAlarm;

    /**
     * Kontruktor
     * 
     * @param _r
     */
    private FunktionstraegerBereichRueckmeldung(
                    Map<FunktionstraegerId, HashMap<BereichId, ArrayList<PersonRueckmeldungCVO>>> _r,
                    PersonInAlarmVO[] _piaVO)
    {
        setRueckmeldung(_r);
        setPersonenInAlarm(_piaVO);
    }

    /**
     * Setzt die Rückmeldung
     * 
     * @param _r
     */
    private void setRueckmeldung(
                    Map<FunktionstraegerId, HashMap<BereichId, ArrayList<PersonRueckmeldungCVO>>> _r)
    {
        mapRueckmeldung = _r;
    }

    /**
     * Liefert die Funktionsträger dieser Statistik zurück
     * 
     * @return
     */
    public FunktionstraegerId[] findFunktionstraegerIds()
    {
        Set<FunktionstraegerId> funktionstraegerSet = mapRueckmeldung.keySet();
        FunktionstraegerId[] r = new FunktionstraegerId[funktionstraegerSet
                        .size()];

        r = funktionstraegerSet.toArray(r);

        return r;
    }

    /**
     * Findet die Personen-Rückmeldungen innerhalb Bereich und Funktionsträger
     * 
     * @param _funktionstraegerId
     * @param _bereichId
     * @return In jedem Fall eine ArrayList
     */
    public List<PersonRueckmeldungCVO> findByFunktionstraegerIdAndBereichId(
                    FunktionstraegerId _funktionstraegerId, BereichId _bereichId)
    {

        List<PersonRueckmeldungCVO> r = findByFunktionstraegerId(
                        _funktionstraegerId).get(_bereichId);

        if (r != null)
        {
            return r;
        }

        return new ArrayList<PersonRueckmeldungCVO>();
    }

    /**
     * Findet die Zuordnung von Personen-Rückmeldungen innerhalb eines Bereichs
     * 
     * @param _funktionstraegerId
     * @return In jedem Fall eine HashMap
     */
    public Map<BereichId, ArrayList<PersonRueckmeldungCVO>> findByFunktionstraegerId(
                    FunktionstraegerId _funktionstraegerId)
    {
        Map<BereichId, ArrayList<PersonRueckmeldungCVO>> r = mapRueckmeldung
                        .get(_funktionstraegerId);

        if (r != null)
        {
            return r;
        }

        return new HashMap<BereichId, ArrayList<PersonRueckmeldungCVO>>();
    }

    /**
     * Liefert die Anzahl der registrierten Funktionsträger zurück
     * 
     * @return
     */
    public int getTotalFunktionstraeger()
    {
        return mapRueckmeldung.size();
    }

    /**
     * Liefert die Anzahl der Bereiche innerhalb eines Funktionsträgers zurück
     * 
     * @param _funktionstraegerId
     * @return
     */
    public int getTotalBereicheInFunktionstraeger(
                    FunktionstraegerId _funktionstraegerId)
    {
        if (mapRueckmeldung.get(_funktionstraegerId) == null)
        {
            return 0;
        }

        return mapRueckmeldung.get(_funktionstraegerId).size();
    }

    /**
     * Liefert die Personen zurück die innerhalb einer spezifischen
     * Bereich/Funktionsträger-Kombination registriert sind
     * 
     * @param _bereichId
     * @param _funktionstraegerId
     * @return
     */
    public int getTotalPersonenInBereichInFunktionstraeger(
                    BereichId _bereichId, FunktionstraegerId _funktionstraegerId)
    {
        return findByFunktionstraegerIdAndBereichId(_funktionstraegerId,
                        _bereichId).size();
    }

    public void setPersonenInAlarm(PersonInAlarmVO[] personenInAlarm)
    {
        this.personenInAlarm = personenInAlarm;
    }

    public PersonInAlarmVO[] getPersonenInAlarm()
    {
        return personenInAlarm;
    }

    /**
     * Liefert die Anzahl der Personen zurück
     * 
     * @return
     */
    public int getTotalPersonen()
    {
        Iterator<FunktionstraegerId> it = mapRueckmeldung.keySet().iterator();

        int r = 0;

        while (it.hasNext())
        {
            FunktionstraegerId fId = (FunktionstraegerId) it.next();
            Iterator<BereichId> itBereich = (mapRueckmeldung.get(fId)).keySet()
                            .iterator();

            while (itBereich.hasNext())
            {
                BereichId bId = (BereichId) itBereich.next();
                r += mapRueckmeldung.get(fId).get(bId).size();
            }
        }

        return r;
    }

    /**
     * Erstellt aus den Personen und deren Rückmeldung ein kombiniertes
     * VO-Objekt, das die Informationen über den Alarmierungsgrad der
     * Bereiche/Funktionsträger enthält
     * 
     * @param _personInAlarmVO
     * @param _person
     * @return
     */
    public static FunktionstraegerBereichRueckmeldung buildPersonenRueckmeldung(
                    PersonInAlarmVO[] _personInAlarmVO, PersonDAO _daoPerson) throws StdException
    {
        HashMap<FunktionstraegerId, HashMap<BereichId, ArrayList<PersonRueckmeldungCVO>>> r = new HashMap<FunktionstraegerId, HashMap<BereichId, ArrayList<PersonRueckmeldungCVO>>>();

        for (int i = 0, m = _personInAlarmVO.length; i < m; i++)
        {
            PersonInAlarmVO personInAlarm = _personInAlarmVO[i];
            PersonVO person = _daoPerson.findPersonById(personInAlarm
                            .getPersonId());

            if (person != null)
            {
                FunktionstraegerId fId = person.getFunktionstraegerId();
                BereichId bId = person.getBereichId();

                PersonRueckmeldungCVO personRueckmeldung = new PersonRueckmeldungCVO(
                                person, personInAlarm);

                if (r.get(fId) == null)
                {
                    HashMap<BereichId, ArrayList<PersonRueckmeldungCVO>> bereichDefault = new HashMap<BereichId, ArrayList<PersonRueckmeldungCVO>>();
                    r.put(fId, bereichDefault);
                }

                if (r.get(fId).get(bId) == null)
                {
                    r.get(fId).put(bId, new ArrayList<PersonRueckmeldungCVO>());
                }

                r.get(fId).get(bId).add(personRueckmeldung);
            }
        }

        return new FunktionstraegerBereichRueckmeldung(r, _personInAlarmVO);
    }
}