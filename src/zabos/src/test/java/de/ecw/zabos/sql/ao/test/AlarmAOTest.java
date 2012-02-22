package de.ecw.zabos.sql.ao.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.sql.dao.AlarmDAO;
import de.ecw.zabos.sql.vo.AlarmVO;
import de.ecw.zabos.sql.vo.PersonVO;
import de.ecw.zabos.test.ZabosTestAdapter;
import de.ecw.zabos.types.UnixTime;
import de.ecw.zabos.types.id.AlarmQuelleId;
import de.ecw.zabos.types.id.PersonId;

public class AlarmAOTest extends ZabosTestAdapter
{
    private static UnixTime ALARM_ZEIT = UnixTime.now();

    private static UnixTime ENTWARN_ZEIT = UnixTime.now();

    private static PersonId ALARM_PERSON_ID;

    private static PersonId ENTWARN_PERSON_ID;

    private static AlarmQuelleId ALARM_QUELLE_ID;

    private static final String KOMMENTAR = "kommentar";

    private static int REIHENFOLGE = 0;

    private static boolean AKTIV = true;

    private static boolean NACHALARMIERT = false;

    private static boolean GELOESCHT = false;

    private static final String GPS_KOORDINATE = "gps-koordinate";

    private static AlarmVO testObject = null;

    private static AlarmDAO daoAlarm;

    private static boolean isInitialized = false;

    private void assertObject(AlarmVO r)
    {
        assertNotNull(r);
        assertEquals(ALARM_ZEIT.getTimeStamp(), r.getAlarmZeit().getTimeStamp());
        assertEquals(ENTWARN_ZEIT.getTimeStamp(), r.getEntwarnZeit()
                        .getTimeStamp());
        assertEquals(ALARM_PERSON_ID, r.getAlarmPersonId());
        assertEquals(ENTWARN_PERSON_ID, r.getEntwarnPersonId());
        assertEquals(ALARM_QUELLE_ID, r.getAlarmQuelleId());
        assertEquals(KOMMENTAR, r.getKommentar());
        // assertEquals(REIHENFOLGE, r.getReihenfolge());
        assertEquals(AKTIV, r.getAktiv());
        assertEquals(NACHALARMIERT, r.getNachalarmiert());
        assertEquals(GELOESCHT, r.getGeloescht());
        assertEquals(GPS_KOORDINATE, r.getGpsKoordinate());
    }

    @Before
    public synchronized void initEnvironmentSub()
    {
        if (!isInitialized)
        {
            daoAlarm = dbResource.getDaoFactory().getAlarmDAO();

            PersonVO p = dbResource.getObjectFactory().createPerson();

            try
            {
                p.setNachname("nachname");
                p.setVorname("vorname");
                p.setName("name");
            }
            catch (Exception e)
            {
                fail(e.getMessage());
            }

            p = taoBV.createPerson(p);

            ALARM_PERSON_ID = p.getPersonId();
            ENTWARN_PERSON_ID = p.getPersonId();

            ALARM_QUELLE_ID = AlarmQuelleId.ID_5TON;

            isInitialized = true;
        }
    }

    @Test
    public void createAlarm()
    {
        if (null == testObject)
        {
            AlarmVO vo = dbResource.getObjectFactory().createAlarm();
            AlarmVO r = null;

            try
            {
                vo.setAlarmZeit(ALARM_ZEIT);
                vo.setEntwarnZeit(ENTWARN_ZEIT);
                vo.setAlarmPersonId(ALARM_PERSON_ID);
                vo.setEntwarnPersonId(ENTWARN_PERSON_ID);
                vo.setAlarmQuelleId(ALARM_QUELLE_ID);
                vo.setKommentar(KOMMENTAR);
                vo.setReihenfolge(REIHENFOLGE);
                vo.setAktiv(AKTIV);
                vo.setNachalarmiert(NACHALARMIERT);
                vo.setGeloescht(GELOESCHT);
                vo.setGpsKoordinate(GPS_KOORDINATE);

                r = daoAlarm.createAlarm(vo);
            }
            catch (StdException e)
            {
                fail(e.getMessage());
            }

            assertObject(r);

            testObject = r;
        }
    }

}
