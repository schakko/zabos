package de.ecw.zabos.sql.ao.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.sql.dao.SmsOutDAO;
import de.ecw.zabos.sql.vo.AlarmVO;
import de.ecw.zabos.sql.vo.OrganisationVO;
import de.ecw.zabos.sql.vo.OrganisationsEinheitVO;
import de.ecw.zabos.sql.vo.PersonVO;
import de.ecw.zabos.sql.vo.SchleifeVO;
import de.ecw.zabos.sql.vo.SmsOutVO;
import de.ecw.zabos.sql.vo.TelefonVO;
import de.ecw.zabos.test.ZabosTestAdapter;
import de.ecw.zabos.types.TelefonNummer;
import de.ecw.zabos.types.UnixTime;
import de.ecw.zabos.types.id.AlarmQuelleId;
import de.ecw.zabos.types.id.SmsOutStatusId;
import de.ecw.zabos.types.id.TelefonId;

public class SmsOutAOTest extends ZabosTestAdapter
{
    private static TelefonId TELEFON_ID;

    private static final String NACHRICHT = "nachricht";

    private static UnixTime ZEITPUNKT = UnixTime.now();

    private static final String CONTEXT = "context";

    private static final String CONTEXT_ALARM = "context-alarm";

    private static final String CONTEXT_ORGANISATION = "context-organisation";

    private static final String CONTEXT_ORGANISATIONSEINHEIT = "context-organisationseinheit";

    private static SmsOutStatusId STATUS_ID = SmsOutStatusId.ID_UNSENT;

    private static final boolean FESTNETZ_SMS = false;

    private static SmsOutVO testObject = null;

    private static SmsOutDAO daoSmsOut;

    private static boolean isInitialized = false;

    private void assertObject(SmsOutVO r)
    {
        assertNotNull(r);
        assertEquals(r.getTelefonId(), TELEFON_ID);
        assertEquals(r.getNachricht(), NACHRICHT);
        assertEquals(r.getZeitpunkt().getTimeStamp(), ZEITPUNKT.getTimeStamp());
        assertEquals(r.getContext(), CONTEXT);
        assertEquals(r.getContextAlarm(), CONTEXT_ALARM);
        assertEquals(r.getContextO(), CONTEXT_ORGANISATION);
        assertEquals(r.getContextOE(), CONTEXT_ORGANISATIONSEINHEIT);
        assertEquals(r.isFestnetzSms(), FESTNETZ_SMS);
        assertEquals(r.getStatusId(), STATUS_ID);
    }

    @Before
    public synchronized void initEnvironmentSub()
    {
        if (!isInitialized)
        {
            TelefonVO t = daoFactory.getObjectFactory().createTelefon();
            PersonVO p = daoFactory.getObjectFactory().createPerson();
            try
            {
                p.setNachname("nachname");
                p.setVorname("vorname");
                p.setName("name");

                p = taoBV.createPerson(p);

                t.setNummer(new TelefonNummer("01747642078"));
                t.setPersonId(p.getPersonId());
                t = taoBV.createTelefon(t);

                TELEFON_ID = t.getTelefonId();
            }
            catch (StdException e)
            {
                fail(e.getMessage());
            }

            daoSmsOut = dbResource.getDaoFactory().getSmsOutDAO();
            isInitialized = true;
        }
    }

    @Test
    public void createSmsOut()
    {
        SmsOutVO vo = daoFactory.getObjectFactory().createSmsOut();
        SmsOutVO r = daoFactory.getObjectFactory().createSmsOut();
        try
        {
            vo.setContext(CONTEXT);
            vo.setContextAlarm(CONTEXT_ALARM);
            vo.setContextO(CONTEXT_ORGANISATION);
            vo.setContextOE(CONTEXT_ORGANISATIONSEINHEIT);
            vo.setFestnetzSms(FESTNETZ_SMS);
            vo.setNachricht(NACHRICHT);
            vo.setZeitpunkt(ZEITPUNKT);
            vo.setStatusId(STATUS_ID);
            vo.setTelefonId(TELEFON_ID);

            r = daoSmsOut.createSmsOut(vo);

            assertObject(r);

            testObject = r;
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void updateSmsOutStatus()
    {
        TelefonVO t = daoFactory.getObjectFactory().createTelefon();
        PersonVO p = daoFactory.getObjectFactory().createPerson();
        SmsOutVO vo = daoFactory.getObjectFactory().createSmsOut();

        try
        {
            p.setNachname("nachname-neu");
            p.setVorname("vorname-neu");
            p.setName("name-neu");
            p = taoBV.createPerson(p);

            t.setNummer(new TelefonNummer("01796537797"));
            t.setPersonId(p.getPersonId());
            t = taoBV.createTelefon(t);

            String nachrichtNeu = "nachricht-neu";
            UnixTime zeitpunktNeu = UnixTime.now();
            String contextNeu = "contextNeu";
            String contextAlarmNeu = "context-alarm-neu";
            String contextOrganisationNeu = "context-organisation-neu";
            String contextOrganisationseinheitNeu = "context-organisationseinheit-neu";
            boolean festnetzSmsNeu = false;

            vo.setTelefonId(t.getTelefonId());
            vo.setNachricht(nachrichtNeu);
            vo.setZeitpunkt(zeitpunktNeu);
            vo.setContext(contextNeu);
            vo.setContextAlarm(contextAlarmNeu);
            vo.setContextO(contextOrganisationNeu);
            vo.setContextOE(contextOrganisationseinheitNeu);
            vo.setFestnetzSms(festnetzSmsNeu);

            vo = daoSmsOut.createSmsOut(vo);

            SmsOutStatusId smsOutStatusId = new SmsOutStatusId(7); // "retrying"
            SmsOutVO updated = daoSmsOut.updateSmsOutStatus(vo.getSmsOutId(),
                            smsOutStatusId);

            assertEquals(t.getTelefonId(), updated.getTelefonId());
            assertEquals(nachrichtNeu, updated.getNachricht());
            assertEquals(zeitpunktNeu.getTimeStamp(), updated.getZeitpunkt()
                            .getTimeStamp());
            assertEquals(contextNeu, updated.getContext());
            assertEquals(contextAlarmNeu, updated.getContextAlarm());
            assertEquals(contextOrganisationNeu, updated.getContextO());
            assertEquals(contextOrganisationseinheitNeu, updated.getContextOE());
            assertEquals(festnetzSmsNeu, updated.isFestnetzSms());
            assertEquals(smsOutStatusId, updated.getStatusId());
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void findSmsOutById()
    {
        assertNotNull(testObject);

        try
        {
            SmsOutVO vo = daoSmsOut.findSmsOutById(testObject.getSmsOutId());
            assertObject(vo);
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void findSmsOutByTelefonId()
    {
        assertNotNull(testObject);

        try
        {
            SmsOutVO[] objects = daoSmsOut.findSmsOutByTelefonId(testObject
                            .getTelefonId());
            assertNotNull(objects);
            assertTrue(objects.length >= 1);

            boolean b = false;
            for (int i = 0, m = objects.length; i < m; i++)
            {
                if (objects[i].getSmsOutId().equals(testObject.getSmsOutId()))
                {
                    assertObject(objects[i]);
                    b = true;
                }
            }

            if (false == b)
            {
                fail("Der gesuchte Datensatz der SmsOut wurde nicht gefunden.");
            }
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void findSmsOutByStatusUnsent()
    {
        assertNotNull(testObject);

        try
        {
            SmsOutVO[] objects = daoSmsOut.findSmsOutByStatusUnsent();
            assertNotNull(objects);
            assertTrue(objects.length >= 1);

            boolean b = false;
            for (int i = 0, m = objects.length; i < m; i++)
            {
                if (objects[i].getSmsOutId().equals(testObject.getSmsOutId()))
                {
                    assertObject(objects[i]);
                    b = true;
                }
            }

            if (false == b)
            {
                fail("Der gesuchte Datensatz der SmsOut wurde nicht gefunden.");
            }
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void addSmsOutToSchleifeInAlarm()
    {
        AlarmVO a = daoFactory.getObjectFactory().createAlarm();
        OrganisationVO o = daoFactory.getObjectFactory().createOrganisation();
        OrganisationsEinheitVO oe = daoFactory.getObjectFactory()
                        .createOrganisationsEinheit();
        SchleifeVO s = daoFactory.getObjectFactory().createSchleife();

        try
        {
            a.setAlarmQuelleId(AlarmQuelleId.ID_5TON);
            a.setAlarmZeit(UnixTime.now());
            a = dbResource.getDaoFactory().getAlarmDAO().createAlarm(a);

            o.setName("o");
            o = taoBV.createOrganisation(o);

            oe.setName("oe");
            oe.setOrganisationId(o.getOrganisationId());
            oe = taoBV.createOrganisationseinheit(oe);

            s.setName("s");
            s.setKuerzel("s");
            s.setOrganisationsEinheitId(oe.getOrganisationsEinheitId());
            s = taoBV.createSchleife(s);

            dbResource.getDaoFactory()
                            .getAlarmDAO()
                            .addSchleifeToAlarm(s.getSchleifeId(),
                                            a.getAlarmId());

            daoSmsOut.addSmsOutToSchleifeInAlarm(testObject.getSmsOutId(),
                            s.getSchleifeId(), a.getAlarmId());
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void findSmsOutByAlarmId()
    {
        AlarmVO a = daoFactory.getObjectFactory().createAlarm();
        OrganisationVO o = daoFactory.getObjectFactory().createOrganisation();
        OrganisationsEinheitVO oe = daoFactory.getObjectFactory()
                        .createOrganisationsEinheit();
        SchleifeVO s = daoFactory.getObjectFactory().createSchleife();

        try
        {
            a.setAlarmQuelleId(AlarmQuelleId.ID_5TON);
            a.setAlarmZeit(UnixTime.now());
            a = dbResource.getDaoFactory().getAlarmDAO().createAlarm(a);

            o.setName("o2");
            o = taoBV.createOrganisation(o);

            oe.setName("oe2");
            oe.setOrganisationId(o.getOrganisationId());
            oe = taoBV.createOrganisationseinheit(oe);

            s.setName("s2");
            s.setKuerzel("s2");
            s.setOrganisationsEinheitId(oe.getOrganisationsEinheitId());
            s = taoBV.createSchleife(s);

            dbResource.getDaoFactory()
                            .getAlarmDAO()
                            .addSchleifeToAlarm(s.getSchleifeId(),
                                            a.getAlarmId());

            daoSmsOut.addSmsOutToSchleifeInAlarm(testObject.getSmsOutId(),
                            s.getSchleifeId(), a.getAlarmId());

            SmsOutVO[] objects = daoSmsOut.findSmsOutByAlarmId(a.getAlarmId());
            assertNotNull(objects);
            assertTrue(objects.length >= 1);

            boolean b = false;
            for (int i = 0, m = objects.length; i < m; i++)
            {
                if (objects[i].getSmsOutId().equals(testObject.getSmsOutId()))
                {
                    assertObject(objects[i]);
                    b = true;
                }
            }

            if (false == b)
            {
                fail("Die zur Schleife in Alarm hinzugefügte SMS wurde nicht gefunden.");
            }
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void findSmsOutBySchleifeInAlarm()
    {
        AlarmVO a = daoFactory.getObjectFactory().createAlarm();
        OrganisationVO o = daoFactory.getObjectFactory().createOrganisation();
        OrganisationsEinheitVO oe = daoFactory.getObjectFactory()
                        .createOrganisationsEinheit();
        SchleifeVO s = daoFactory.getObjectFactory().createSchleife();

        try
        {
            a.setAlarmQuelleId(AlarmQuelleId.ID_5TON);
            a.setAlarmZeit(UnixTime.now());
            a = dbResource.getDaoFactory().getAlarmDAO().createAlarm(a);

            o.setName("o3");
            o = taoBV.createOrganisation(o);

            oe.setName("oe3");
            oe.setOrganisationId(o.getOrganisationId());
            oe = taoBV.createOrganisationseinheit(oe);

            s.setName("s3");
            s.setKuerzel("s3");
            s.setOrganisationsEinheitId(oe.getOrganisationsEinheitId());
            s = taoBV.createSchleife(s);

            dbResource.getDaoFactory()
                            .getAlarmDAO()
                            .addSchleifeToAlarm(s.getSchleifeId(),
                                            a.getAlarmId());

            daoSmsOut.addSmsOutToSchleifeInAlarm(testObject.getSmsOutId(),
                            s.getSchleifeId(), a.getAlarmId());

            SmsOutVO[] objects = daoSmsOut.findSmsOutBySchleifeInAlarm(
                            s.getSchleifeId(), a.getAlarmId());
            assertNotNull(objects);
            assertTrue(objects.length >= 1);

            boolean b = false;
            for (int i = 0, m = objects.length; i < m; i++)
            {
                if (objects[i].getSmsOutId().equals(testObject.getSmsOutId()))
                {
                    assertObject(objects[i]);
                    b = true;
                }
            }

            if (false == b)
            {
                fail("Die zur Schleife in Alarm hinzugefügte SMS wurde nicht gefunden.");
            }
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }
    }
}
