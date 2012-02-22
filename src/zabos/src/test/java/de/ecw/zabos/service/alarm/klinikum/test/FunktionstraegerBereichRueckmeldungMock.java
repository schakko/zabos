package de.ecw.zabos.service.alarm.klinikum.test;

import static junit.framework.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.sql.tao.BenutzerVerwaltungTAO;
import de.ecw.zabos.sql.vo.BereichVO;
import de.ecw.zabos.sql.vo.FunktionstraegerVO;
import de.ecw.zabos.sql.vo.ObjectFactory;
import de.ecw.zabos.sql.vo.PersonInAlarmVO;
import de.ecw.zabos.sql.vo.PersonVO;
import de.ecw.zabos.types.id.AlarmId;
import de.ecw.zabos.types.id.BereichId;
import de.ecw.zabos.types.id.FunktionstraegerId;
import de.ecw.zabos.types.id.RueckmeldungStatusId;

/**
 * Mock-Up für die Rückmeldungen
 * 
 * @author ckl
 * 
 */
public class FunktionstraegerBereichRueckmeldungMock
{
    BenutzerVerwaltungTAO taoBV;

    public AlarmId alarmId = new AlarmId(1);

    public Random rand = new Random();

    public ArrayList<PersonInAlarmVO> alPia = new ArrayList<PersonInAlarmVO>();

    public String s[] = new String[]
    { "A", "B", "C", "D", "E" };

    public long[] ris = new long[]
    { RueckmeldungStatusId.STATUS_JA, RueckmeldungStatusId.STATUS_NEIN, RueckmeldungStatusId.STATUS_SPAETER };

    public ArrayList<BereichVO> alBereiche = new ArrayList<BereichVO>();

    public HashMap<String, Integer> hmStatPersonenInBereichFunktionstraegerKombination = new HashMap<String, Integer>();

    public HashMap<String, Integer> hmStatPositiveRueckmeldung = new HashMap<String, Integer>();

    public ArrayList<FunktionstraegerVO> alFunktionstraeger = new ArrayList<FunktionstraegerVO>();

    public int personen = 50;

    public int pJa = 0;

    public PersonInAlarmVO[] piaVOs;

    public int pNein = 0;

    public FunktionstraegerBereichRueckmeldungMock(
                    BenutzerVerwaltungTAO _taoBV, int _personen)
                    throws StdException
    {
        taoBV = _taoBV;
        personen = _personen;

        init();
    }

    private void init() throws StdException
    {
        ObjectFactory objectFactory = new ObjectFactory();

        for (int i = 0, m = s.length; i < m; i++)
        {
            BereichVO b = objectFactory.createBereich();
            b.setBeschreibung("B-" + s[i]);
            b.setName("B-" + s[i]);
            b = taoBV.createBereich(b);
            assertNotNull(b);
            alBereiche.add(b);

            FunktionstraegerVO f = objectFactory.createFunktionstraeger();
            f.setBeschreibung("F-" + s[i]);
            f.setKuerzel("F-" + s[i]);
            f = taoBV.createFunktionstraeger(f);
            assertNotNull(f);
            alFunktionstraeger.add(f);
        }

        int idxBereichFunktionstraeger = 0;

        for (int i = 0; i < personen; i++)
        {
            PersonVO p = objectFactory.createPerson();
            p.setNachname("N-" + i);
            p.setVorname("V-" + i);
            p.setName("U-" + i);
            BereichId bId = alBereiche.get(idxBereichFunktionstraeger)
                            .getBereichId();
            p.setBereichId(bId);

            FunktionstraegerId fId = alFunktionstraeger.get(
                            idxBereichFunktionstraeger).getFunktionstraegerId();
            p.setFunktionstraegerId(fId);

            String id = bId.getLongValue() + "-" + fId.getLongValue();

            if (hmStatPersonenInBereichFunktionstraegerKombination.get(id) == null)
            {
                hmStatPersonenInBereichFunktionstraegerKombination.put(id, 0);
            }

            if (hmStatPositiveRueckmeldung.get(id) == null)
            {
                hmStatPositiveRueckmeldung.put(id, 0);
            }

            int v = hmStatPersonenInBereichFunktionstraegerKombination.get(id);
            hmStatPersonenInBereichFunktionstraegerKombination.put(id, ++v);

            p = taoBV.createPerson(p);
            assertNotNull(p);

            PersonInAlarmVO pia = objectFactory.createPersonInAlarm();
            pia.setAlarmId(alarmId);
            pia.setPersonId(p.getPersonId());

            int idxRueckmeldung = createRandomInteger(0, (ris.length - 1), rand);

            if (idxRueckmeldung == RueckmeldungStatusId.STATUS_JA)
            {
                pJa++;

                v = hmStatPositiveRueckmeldung.get(id);
                hmStatPositiveRueckmeldung.put(id, ++v);
            }
            else
            {
                pNein++;
            }

            pia.setRueckmeldungStatusId(new RueckmeldungStatusId(
                            idxRueckmeldung));

            alPia.add(pia);

            idxBereichFunktionstraeger++;

            if (idxBereichFunktionstraeger == s.length)
            {
                idxBereichFunktionstraeger = 0;
            }
        }

        piaVOs = new PersonInAlarmVO[alPia.size()];
        piaVOs = alPia.toArray(piaVOs);
    }

    private int createRandomInteger(int aStart, int aEnd, Random aRandom)
    {
        if (aStart > aEnd)
        {
            throw new IllegalArgumentException("Start cannot exceed End.");
        }
        // get the range, casting to long to avoid overflow problems
        long range = (long) aEnd - (long) aStart + 1;
        // compute a fraction of the range, 0 <= frac < range
        long fraction = (long) (range * aRandom.nextDouble());
        int randomNumber = (int) (fraction + aStart);

        return randomNumber;
    }

}
