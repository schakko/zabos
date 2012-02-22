package de.ecw.zabos.sql.ao.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.sql.dao.TelefonDAO;
import de.ecw.zabos.sql.vo.PersonVO;
import de.ecw.zabos.sql.vo.TelefonVO;
import de.ecw.zabos.test.ZabosTestAdapter;
import de.ecw.zabos.types.TelefonNummer;
import de.ecw.zabos.types.UnixTime;
import de.ecw.zabos.types.id.PersonId;

public class TelefonAOTest extends ZabosTestAdapter
{
    private final static boolean AKTIV = true;

    private final static boolean FLASH_SMS = true;

    private final static boolean GELOESCHT = false;

    private final static TelefonNummer NUMMER = new TelefonNummer(
                    "0179111111111");

    private static PersonId PERSON_ID;

    private final static UnixTime ZEITFENSTER_ENDE = UnixTime.now();

    private final static UnixTime ZEITFENSTER_START = UnixTime.now();

    private static TelefonVO testObject = null;

    private static TelefonDAO daoTelefon;

    private static boolean isInitialized = false;

    public void assertObject(TelefonVO r)
    {
        assertNotNull(r);
        assertEquals(r.getAktiv(), AKTIV);
        assertEquals(r.getFlashSms(), FLASH_SMS);
        assertEquals(r.getGeloescht(), GELOESCHT);
        assertEquals(r.getNummer(), NUMMER);
        assertEquals(r.getPersonId(), PERSON_ID);
        assertEquals(r.getZeitfensterEnde().getTimeStamp(),
                        ZEITFENSTER_ENDE.getTimeStamp());
        assertEquals(r.getZeitfensterStart().getTimeStamp(),
                        ZEITFENSTER_START.getTimeStamp());
    }

    @Before
    public synchronized void initEnvironmentSub()
    {
        if (!isInitialized)
        {

            PersonVO person = daoFactory.getObjectFactory().createPerson();
            try
            {
                person.setVorname("person-vorname");
                person.setNachname("person-nachname");
                person.setName("person-name");
            }
            catch (StdException e)
            {
                fail(e.getMessage());
            }

            person = taoBV.createPerson(person);
            assertNotNull(person);
            PERSON_ID = person.getPersonId();

            daoTelefon = dbResource.getDaoFactory().getTelefonDAO();
            isInitialized = true;
        }
    }

    @Test
    public void createTelefon()
    {
        TelefonVO vo = daoFactory.getObjectFactory().createTelefon();

        try
        {
            vo.setAktiv(AKTIV);
            vo.setFlashSms(FLASH_SMS);
            vo.setGeloescht(GELOESCHT);
            vo.setNummer(NUMMER);
            vo.setPersonId(PERSON_ID);
            vo.setZeitfensterEnde(ZEITFENSTER_ENDE);
            vo.setZeitfensterStart(ZEITFENSTER_START);
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }

        TelefonVO r = taoBV.createTelefon(vo);

        assertObject(r);

        testObject = r;
    }

    @Test
    public void updateTelefon()
    {
        try
        {

            String personVornameNeu = "person-vorname-neu";
            String personNachnameNeu = "person-nachname-neu";
            String personNameNeu = "person-name-neu";

            PersonVO person = daoFactory.getObjectFactory().createPerson();
            person.setVorname(personVornameNeu);
            person.setNachname(personNachnameNeu);
            person.setName(personNameNeu);
            person = taoBV.createPerson(person);
            assertNotNull(person);

            TelefonNummer neueNummer = new TelefonNummer("0179222222222");
            UnixTime neuZeitfensterEnde = null, neuZeitfensterStart = null;

            try
            {
                Date date = new SimpleDateFormat("dd.MM.yyyy")
                                .parse("01.26.1986");
                neuZeitfensterStart = new UnixTime(date);
                neuZeitfensterEnde = new UnixTime(date);
            }
            catch (Exception e)
            {
                fail(e.getMessage());
            }

            testObject.setAktiv(!AKTIV);
            testObject.setFlashSms(!FLASH_SMS);
            // testObject.setGeloescht(!GELOESCHT);
            testObject.setNummer(neueNummer);
            testObject.setPersonId(person.getPersonId());
            testObject.setZeitfensterEnde(neuZeitfensterEnde);
            testObject.setZeitfensterStart(neuZeitfensterStart);

            TelefonVO updated = taoBV.updateTelefon(testObject);
            assertNotNull(updated);
            assertEquals(updated.getAktiv(), !AKTIV);
            assertEquals(updated.getFlashSms(), !FLASH_SMS);
            // assertEquals(updated.getGeloescht(), !GELOESCHT);
            assertEquals(updated.getNummer(), neueNummer);
            assertEquals(updated.getPersonId(), person.getPersonId());
            assertEquals(updated.getZeitfensterEnde().getTimeStamp(),
                            neuZeitfensterEnde.getTimeStamp());
            assertEquals(updated.getZeitfensterStart().getTimeStamp(),
                            neuZeitfensterStart.getTimeStamp());

            testObject.setAktiv(AKTIV);
            testObject.setFlashSms(FLASH_SMS);
            testObject.setGeloescht(GELOESCHT);
            testObject.setNummer(NUMMER);
            testObject.setPersonId(PERSON_ID);
            testObject.setZeitfensterEnde(ZEITFENSTER_ENDE);
            testObject.setZeitfensterStart(ZEITFENSTER_START);

            testObject = taoBV.updateTelefon(testObject);
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void deleteTelefon()
    {
        try
        {
            TelefonVO deleteTest = daoFactory.getObjectFactory()
                            .createTelefon();
            deleteTest.setAktiv(AKTIV);
            deleteTest.setFlashSms(FLASH_SMS);
            deleteTest.setGeloescht(GELOESCHT);
            deleteTest.setNummer(new TelefonNummer("01747642078"));
            deleteTest.setPersonId(PERSON_ID);
            deleteTest.setZeitfensterEnde(ZEITFENSTER_ENDE);
            deleteTest.setZeitfensterStart(ZEITFENSTER_START);

            deleteTest = taoBV.createTelefon(deleteTest);
            assertNotNull(deleteTest);

            boolean result = taoBV.deleteTelefon(deleteTest.getTelefonId());
            assertTrue(result);

            deleteTest = daoTelefon.findTelefonById(deleteTest.getTelefonId());
            assertNotNull(deleteTest);
            assertTrue(deleteTest.getAktiv());
            assertTrue(deleteTest.getGeloescht());

            TelefonVO[] objects = daoTelefon.findTelefoneByPersonId(PERSON_ID);
            assertNotNull(objects);
            assertTrue(objects.length > 0);

            /*
             * for (int i = 0, m = objects.length; i < m; i++) { if
             * (objects[i].getTelefonId().equals(deleteTest.getTelefonId())) {
             * fail("Der geloeschte Datensatz wurde zurueckgeliefert."); } }
             */

        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void undeleteTelefon()
    {
        try
        {
            TelefonVO undeleteTest = daoFactory.getObjectFactory()
                            .createTelefon();
            undeleteTest.setAktiv(AKTIV);
            undeleteTest.setFlashSms(FLASH_SMS);
            undeleteTest.setGeloescht(GELOESCHT);
            TelefonNummer nummer = new TelefonNummer("01747642078");
            undeleteTest.setNummer(nummer);
            undeleteTest.setPersonId(PERSON_ID);
            undeleteTest.setZeitfensterEnde(ZEITFENSTER_ENDE);
            undeleteTest.setZeitfensterStart(ZEITFENSTER_START);

            undeleteTest = taoBV.createTelefon(undeleteTest);
            assertNotNull(undeleteTest);

            boolean result = taoBV.deleteTelefon(undeleteTest.getTelefonId());
            assertTrue(result);

            undeleteTest = daoTelefon.findTelefonById(undeleteTest
                            .getTelefonId());
            assertNotNull(undeleteTest);
            assertTrue(undeleteTest.getAktiv());
            assertTrue(undeleteTest.getGeloescht());

            TelefonVO[] objects = daoTelefon.findTelefoneByPersonId(PERSON_ID);
            assertNotNull(objects);
            assertTrue(objects.length > 0);

            for (int i = 0, m = objects.length; i < m; i++)
            {
                if (objects[i].getTelefonId().equals(
                                undeleteTest.getTelefonId()))
                {
                    if (false == objects[i].getGeloescht())
                    {
                        fail("Der Datensatz wurde nicht als geloescht markiert.");
                    }
                }
            }

            undeleteTest = daoTelefon.undeleteTelefon(undeleteTest
                            .getTelefonId());

            assertNotNull(undeleteTest);
            assertEquals(undeleteTest.getAktiv(), AKTIV);
            assertEquals(undeleteTest.getFlashSms(), FLASH_SMS);
            assertEquals(undeleteTest.getGeloescht(), GELOESCHT);
            assertEquals(undeleteTest.getNummer(), nummer);
            assertEquals(undeleteTest.getPersonId(), PERSON_ID);
            assertEquals(undeleteTest.getZeitfensterEnde().getTimeStamp(),
                            ZEITFENSTER_ENDE.getTimeStamp());
            assertEquals(undeleteTest.getZeitfensterStart().getTimeStamp(),
                            ZEITFENSTER_START.getTimeStamp());

            undeleteTest.setAktiv(false);
            undeleteTest = taoBV.updateTelefon(undeleteTest);
            testObject = taoBV.updateTelefon(testObject);

        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void findTelefonById()
    {
        assertNotNull(testObject);

        try
        {
            testObject = daoTelefon.findTelefonById(testObject.getTelefonId());
            assertObject(testObject);
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void findCurrentTelefonByPersonId()
    {
        try
        {
            testObject = daoTelefon.findCurrentTelefonByPersonId(PERSON_ID);
            assertObject(testObject);
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void findTelefoneByPersonId()
    {
        try
        {
            TelefonVO[] objects = daoTelefon.findTelefoneByPersonId(PERSON_ID);
            assertNotNull(objects);
            assertTrue(objects.length > 0);
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void findTelefonByNummer()
    {
        assertNotNull(testObject);

        try
        {
            testObject = daoTelefon.findTelefonByNummer(NUMMER);
            assertObject(testObject);
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void hatPersonAktiveHandyNummer()
    {
        try
        {
            boolean result = daoTelefon.hatPersonAktiveHandyNummer(PERSON_ID);
            assertTrue(result);
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void setTelefonIdNull()
    {
        TelefonVO vo = daoFactory.getObjectFactory().createTelefon();
        try
        {
            vo.setTelefonId(null);
            fail("Test did not catch StdException.");
        }
        catch (StdException e)
        {

        }
    }

    @Test
    public void setPersonIdNull()
    {
        TelefonVO vo = daoFactory.getObjectFactory().createTelefon();
        try
        {
            vo.setPersonId(null);
            fail("Test did not catch StdException.");
        }
        catch (StdException e)
        {

        }
    }

    @Test
    public void setNummerNull()
    {
        TelefonVO vo = daoFactory.getObjectFactory().createTelefon();
        try
        {
            vo.setNummer(null);
            fail("Test did not catch StdException.");
        }
        catch (StdException e)
        {

        }
    }
}
