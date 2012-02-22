package de.ecw.zabos.sql.ao.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import de.ecw.zabos.SpringContext;
import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.service.alarm.klinikum.test.KlinikumAlarmServiceMock;
import de.ecw.zabos.sql.dao.SchleifenDAO;
import de.ecw.zabos.sql.vo.AlarmVO;
import de.ecw.zabos.sql.vo.OrganisationVO;
import de.ecw.zabos.sql.vo.OrganisationsEinheitVO;
import de.ecw.zabos.sql.vo.PersonVO;
import de.ecw.zabos.sql.vo.ProbeTerminVO;
import de.ecw.zabos.sql.vo.RolleVO;
import de.ecw.zabos.sql.vo.SchleifeVO;
import de.ecw.zabos.test.ZabosTestAdapter;
import de.ecw.zabos.types.UnixTime;
import de.ecw.zabos.types.id.AlarmQuelleId;
import de.ecw.zabos.types.id.OrganisationId;
import de.ecw.zabos.types.id.OrganisationsEinheitId;
import de.ecw.zabos.types.id.RechtId;
import de.ecw.zabos.types.id.SchleifeId;

public class SchleifeAOTest extends ZabosTestAdapter
{
    private final static String NAME = "name";

    private final static String BESCHREIBUNG = "beschreibung";

    private final static String KUERZEL = "krzl1";

    private final static String FUENFTON = "fnft1";

    private static OrganisationId ORGANISATION_ID;

    private static OrganisationsEinheitId ORGANISATIONS_EINHEIT_ID;

    private static boolean STATUSREPORT_FUENFTON = true;

    private static boolean IST_ABRECHENBAR = true;

    private static SchleifeId FOLGESCHLEIFE_ID;

    private static long RUECKMELDEINTERVALL = 10;

    private static final String DRUCKER_KUERZEL = "drucker_kuerzel";

    private static SchleifeVO testObject = null;

    private static SchleifenDAO daoSchleife;

    private static boolean isInitialized = false;

    private static KlinikumAlarmServiceMock klinikumAlarmService;

    private void assertObject(SchleifeVO r)
    {
        assertNotNull(r);
        assertEquals(r.getName(), NAME);
        assertEquals(r.getBeschreibung(), BESCHREIBUNG);
        assertEquals(r.getKuerzel(), KUERZEL);
        assertEquals(r.getFuenfton(), FUENFTON);
        assertEquals(r.getOrganisationsEinheitId(), ORGANISATIONS_EINHEIT_ID);
        assertEquals(r.getStatusreportFuenfton(), STATUSREPORT_FUENFTON);
        assertEquals(r.getAbrechenbar(), IST_ABRECHENBAR);
        assertEquals(r.getFolgeschleifeId(), FOLGESCHLEIFE_ID);
        assertEquals(r.getRueckmeldeintervall(), RUECKMELDEINTERVALL);
        assertEquals(r.getDruckerKuerzel(), DRUCKER_KUERZEL);
    }

    @Before
    public synchronized void initEnvironmentSub()
    {
        if (!isInitialized)
        {
            daoSchleife = dbResource.getDaoFactory().getSchleifenDAO();

            OrganisationVO organisation = daoFactory.getObjectFactory()
                            .createOrganisation();

            try
            {
                organisation.setName("organisation-name");
                organisation.setBeschreibung("organisation-beschreibung");
                organisation.setGeloescht(false);

            }
            catch (StdException e)
            {
                fail(e.getMessage());
            }

            organisation = taoBV.createOrganisation(organisation);
            assertNotNull(organisation);

            ORGANISATION_ID = organisation.getOrganisationId();

            OrganisationsEinheitVO organisationseinheit = daoFactory
                            .getObjectFactory().createOrganisationsEinheit();

            try
            {
                organisationseinheit.setName("organisationseinheit-name");
                organisationseinheit
                                .setBeschreibung("organisationseinheit-beschreibung");
                organisationseinheit.setGeloescht(false);
                organisationseinheit.setOrganisationId(organisation
                                .getOrganisationId());
            }
            catch (StdException e)
            {
                fail(e.getMessage());
            }

            organisationseinheit = taoBV
                            .createOrganisationseinheit(organisationseinheit);
            assertNotNull(organisationseinheit);

            ORGANISATIONS_EINHEIT_ID = organisationseinheit
                            .getOrganisationsEinheitId();

            klinikumAlarmService = (KlinikumAlarmServiceMock) SpringContext
                            .getInstance().getBean(
                                            SpringContext.BEAN_ALARM_SERVICE,
                                            KlinikumAlarmServiceMock.class);

            isInitialized = true;
        }
    }

    @Test
    public void createSchleife()
    {
        if (null == testObject)
        {
            SchleifeVO vo = daoFactory.getObjectFactory().createSchleife();

            try
            {
                vo.setName(NAME);
                vo.setBeschreibung(BESCHREIBUNG);
                vo.setKuerzel(KUERZEL);
                vo.setFuenfton(FUENFTON);
                vo.setOrganisationsEinheitId(ORGANISATIONS_EINHEIT_ID);
                vo.setStatusreportFuenfton(STATUSREPORT_FUENFTON);
                vo.setAbrechenbar(IST_ABRECHENBAR);
                vo.setFolgeschleifeId(FOLGESCHLEIFE_ID);
                vo.setRueckmeldeintervall(RUECKMELDEINTERVALL);
                vo.setDruckerKuerzel(DRUCKER_KUERZEL);
            }
            catch (StdException e)
            {
                fail(e.getMessage());
            }

            SchleifeVO r = taoBV.createSchleife(vo);

            assertObject(r);

            testObject = r;
        }
    }

    @Test
    public void updateSchleife()
    {
        if (null == testObject)
        {
            createSchleife();
        }

        assertObject(testObject);

        try
        {
            String nameNeu = "schleife-name-neu";

            String beschreibungNeu = "schleife-beschreibung-neu";

            String kuerzelNeu = "krzl2";

            String fuenftonNeu = "fnft2";

            OrganisationsEinheitVO organisationseinheit = dbResource
                            .getDaoFactory()
                            .getOrganisationsEinheitDAO()
                            .findOrganisationsEinheitById(
                                            testObject.getOrganisationsEinheitId());
            OrganisationsEinheitVO organisationseinheitNeu = daoFactory
                            .getObjectFactory().createOrganisationsEinheit();
            organisationseinheitNeu.setName("organisationseinheit-name-neu");
            organisationseinheitNeu
                            .setBeschreibung("organisationseinheit-beschreibung-neu");
            organisationseinheitNeu.setGeloescht(false);
            organisationseinheitNeu.setOrganisationId(organisationseinheit
                            .getOrganisationId());
            organisationseinheitNeu = taoBV
                            .createOrganisationseinheit(organisationseinheitNeu);
            assertNotNull(organisationseinheitNeu);

            boolean statusreportFuenftonNeu = !STATUSREPORT_FUENFTON;

            boolean istAbrechenbarNeu = !IST_ABRECHENBAR;

            long rueckmeldeintervallNeu = 5;

            String druckerKuerzelNeu = "schleife-druckerkuerzel-neu";

            testObject.setName(nameNeu);
            testObject.setBeschreibung(beschreibungNeu);
            testObject.setKuerzel(kuerzelNeu);
            testObject.setFuenfton(fuenftonNeu);
            testObject.setOrganisationsEinheitId(organisationseinheitNeu
                            .getOrganisationsEinheitId());
            testObject.setStatusreportFuenfton(statusreportFuenftonNeu);
            testObject.setAbrechenbar(istAbrechenbarNeu);
            testObject.setRueckmeldeintervall(rueckmeldeintervallNeu);
            testObject.setDruckerKuerzel(druckerKuerzelNeu);

            SchleifeVO updated = taoBV.updateSchleife(testObject);
            assertNotNull(updated);
            assertEquals(updated.getName(), nameNeu);
            assertEquals(updated.getBeschreibung(), beschreibungNeu);
            assertEquals(updated.getKuerzel(), kuerzelNeu);
            assertEquals(updated.getFuenfton(), fuenftonNeu);
            assertEquals(updated.getOrganisationsEinheitId(),
                            organisationseinheitNeu.getOrganisationsEinheitId());
            assertEquals(updated.getStatusreportFuenfton(),
                            statusreportFuenftonNeu);
            assertEquals(updated.getAbrechenbar(), istAbrechenbarNeu);
            assertEquals(updated.getRueckmeldeintervall(),
                            rueckmeldeintervallNeu);
            assertEquals(updated.getDruckerKuerzel(), druckerKuerzelNeu);

            testObject.setName(NAME);
            testObject.setBeschreibung(BESCHREIBUNG);
            testObject.setKuerzel(KUERZEL);
            testObject.setFuenfton(FUENFTON);
            testObject.setOrganisationsEinheitId(ORGANISATIONS_EINHEIT_ID);
            testObject.setStatusreportFuenfton(STATUSREPORT_FUENFTON);
            testObject.setAbrechenbar(IST_ABRECHENBAR);
            testObject.setFolgeschleifeId(FOLGESCHLEIFE_ID);
            testObject.setRueckmeldeintervall(RUECKMELDEINTERVALL);
            testObject.setDruckerKuerzel(DRUCKER_KUERZEL);

            testObject = taoBV.updateSchleife(testObject);
            assertObject(testObject);
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void deleteSchleife()
    {
        SchleifeVO deleteTest = daoFactory.getObjectFactory().createSchleife();

        try
        {
            deleteTest.setName("schleife-zu-loeschen");
            deleteTest.setBeschreibung("schleife-zu-loeschen");
            deleteTest.setKuerzel("krzl3");
            deleteTest.setFuenfton("fnft3");
            deleteTest.setOrganisationsEinheitId(ORGANISATIONS_EINHEIT_ID);
            deleteTest.setStatusreportFuenfton(STATUSREPORT_FUENFTON);
            deleteTest.setAbrechenbar(IST_ABRECHENBAR);
            deleteTest.setFolgeschleifeId(FOLGESCHLEIFE_ID);
            deleteTest.setRueckmeldeintervall(RUECKMELDEINTERVALL);
            deleteTest.setDruckerKuerzel(DRUCKER_KUERZEL);

            deleteTest = taoBV.createSchleife(deleteTest);

            assertNotNull(deleteTest);

            boolean result = taoBV.deleteSchleife(deleteTest.getSchleifeId());
            assertTrue(result);

            SchleifeVO[] objects = daoSchleife.findAll();
            assertNotNull(objects);
            assertTrue(objects.length >= 0);

            for (int i = 0, m = objects.length; i < m; i++)
            {
                if (objects[i].getSchleifeId().equals(
                                deleteTest.getSchleifeId()))
                {
                    fail("Der gelöschte Datensatz wurde zurückgeliefert");
                }
            }
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void undeleteSchleife()
    {
        SchleifeVO undeleteTest = daoFactory.getObjectFactory()
                        .createSchleife();

        try
        {
            String nameNeu = "schleife-zu-wiederstellen";
            String beschreibungNeu = "schleife-zu-wiederherstellen";
            String kuerzelNeu = "krzl4";
            String fuenftonNeu = "fnft4";

            undeleteTest.setName(nameNeu);
            undeleteTest.setBeschreibung(beschreibungNeu);
            undeleteTest.setKuerzel(kuerzelNeu);
            undeleteTest.setFuenfton(fuenftonNeu);
            undeleteTest.setOrganisationsEinheitId(ORGANISATIONS_EINHEIT_ID);
            undeleteTest.setStatusreportFuenfton(STATUSREPORT_FUENFTON);
            undeleteTest.setAbrechenbar(IST_ABRECHENBAR);
            undeleteTest.setFolgeschleifeId(FOLGESCHLEIFE_ID);
            undeleteTest.setRueckmeldeintervall(RUECKMELDEINTERVALL);
            undeleteTest.setDruckerKuerzel(DRUCKER_KUERZEL);

            undeleteTest = taoBV.createSchleife(undeleteTest);

            assertNotNull(undeleteTest);

            boolean result = taoBV.deleteSchleife(undeleteTest.getSchleifeId());
            assertTrue(result);

            undeleteTest = daoSchleife.undeleteSchleife(undeleteTest
                            .getSchleifeId());
            assertNotNull(undeleteTest);
            assertEquals(nameNeu, undeleteTest.getName());
            assertEquals(beschreibungNeu, undeleteTest.getBeschreibung());
            assertEquals(kuerzelNeu, undeleteTest.getKuerzel());
            assertEquals(fuenftonNeu, undeleteTest.getFuenfton());
            assertEquals(ORGANISATIONS_EINHEIT_ID,
                            undeleteTest.getOrganisationsEinheitId());
            assertEquals(STATUSREPORT_FUENFTON,
                            undeleteTest.getStatusreportFuenfton());
            assertEquals(IST_ABRECHENBAR, undeleteTest.getAbrechenbar());
            assertEquals(FOLGESCHLEIFE_ID, undeleteTest.getFolgeschleifeId());
            assertEquals(RUECKMELDEINTERVALL,
                            undeleteTest.getRueckmeldeintervall());
            assertEquals(DRUCKER_KUERZEL, undeleteTest.getDruckerKuerzel());
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void countSchleifen()
    {
        SchleifeVO countTest = daoFactory.getObjectFactory().createSchleife();

        try
        {
            long c = daoSchleife.countSchleifen();
            assertTrue(c >= 0);

            countTest.setBeschreibung("count-test");
            countTest.setName("count-test");
            countTest.setKuerzel("cntst");
            countTest.setOrganisationsEinheitId(ORGANISATIONS_EINHEIT_ID);
            countTest = taoBV.createSchleife(countTest);

            assertEquals(daoSchleife.countSchleifen(), c + 1);
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void findSchleifeById()
    {
        if (null == testObject)
        {
            createSchleife();
        }

        assertObject(testObject);

        try
        {
            SchleifeVO vo = daoSchleife.findSchleifeById(testObject
                            .getSchleifeId());
            assertObject(vo);
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void findSchleifeByName()
    {
        if (null == testObject)
        {
            createSchleife();
        }

        assertObject(testObject);

        try
        {
            SchleifeVO vo = daoSchleife
                            .findSchleifeByName(testObject.getName());
            assertObject(vo);
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void findSchleifeByKuerzel()
    {
        if (null == testObject)
        {
            createSchleife();
        }

        assertObject(testObject);

        try
        {
            SchleifeVO vo = daoSchleife.findSchleifeByKuerzel(KUERZEL);
            assertObject(vo);
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void findSchleifeByKuerzelCaseSensitive()
    {
        if (null == testObject)
        {
            createSchleife();
        }

        assertObject(testObject);

        try
        {
            SchleifeVO vo = daoSchleife
                            .findSchleifeByKuerzelCaseSensitive(KUERZEL);
            assertObject(vo);

            vo = daoSchleife.findSchleifeByKuerzelCaseSensitive(KUERZEL
                            .toUpperCase());
            assertNull(vo);
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void findSchleifeByFuenfton()
    {
        if (null == testObject)
        {
            createSchleife();
        }

        assertObject(testObject);

        try
        {
            SchleifeVO vo = daoSchleife.findSchleifeByFuenfton(FUENFTON);
            assertObject(vo);
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void findSchleifenByOrganisationsEinheitId()
    {
        if (null == testObject)
        {
            createSchleife();
        }

        assertObject(testObject);

        try
        {
            boolean b = false;
            SchleifeVO[] objects = daoSchleife
                            .findSchleifenByOrganisationsEinheitId(testObject
                                            .getOrganisationsEinheitId());
            assertNotNull(objects);
            assertTrue(objects.length > 0);

            for (int i = 0, m = objects.length; i < m; i++)
            {
                if (objects[i].getSchleifeId().equals(
                                testObject.getSchleifeId()))
                {
                    assertObject(objects[i]);
                    b = true;
                }
            }

            if (false == b)
            {
                fail("Datensatz des Testobjekts wurde nicht gefunden");
            }
        }
        catch (Exception e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void findSchleifenByPattern()
    {
        if (null == testObject)
        {
            createSchleife();
        }

        assertObject(testObject);

        try
        {
            boolean b = false;
            SchleifeVO[] objects = daoSchleife.findSchleifenByPattern("nft",
                            "rzl", "am");
            assertNotNull(objects);
            assertTrue(objects.length >= 1);

            for (int i = 0, m = objects.length; i < m; i++)
            {
                if (objects[i].getSchleifeId().equals(
                                testObject.getSchleifeId()))
                {
                    assertObject(objects[i]);
                    b = true;
                }
            }

            if (false == b)
            {
                fail("Datensatz des Testobjekts wurde nicht gefunden");
            }
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void findMitgliedschaftInSchleifenVonPerson()
    {
        assertNotNull(testObject);

        try
        {
            RolleVO ro = daoFactory.getObjectFactory().createRolle();
            ro.setBeschreibung("rolle-beschreibung");
            ro.setName("rolle-name");
            ro = taoBV.createRolle(ro);

            PersonVO p = daoFactory.getObjectFactory().createPerson();
            p.setNachname("person-nachname");
            p.setName("person-name");
            p.setVorname("person-vorname");
            p = taoBV.createPerson(p);

            dbResource.getDaoFactory()
                            .getRolleDAO()
                            .addRechtToRolle(RechtId.SYSTEM_DEAKTIVIEREN,
                                            ro.getRolleId());
            daoSchleife.addPersonInRolleToSchleife(p.getPersonId(),
                            ro.getRolleId(), testObject.getSchleifeId());

            SchleifeVO[] objects = daoSchleife
                            .findMitgliedschaftInSchleifenVonPerson(p
                                            .getPersonId());
            assertNotNull(objects);
            assertTrue(objects.length >= 1);

            boolean b = false;

            for (int i = 0, m = objects.length; i < m; i++)
            {
                if (objects[i].getSchleifeId().equals(
                                testObject.getSchleifeId()))
                {
                    b = true;
                }
            }

            if (false == b)
            {
                fail("Die Mitgliedschaft der Person in der Schleife wurde nicht gefunden.");
            }
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void removePersonInRolleFromSchleife()
    {
        assertNotNull(testObject);

        try
        {
            RolleVO ro = daoFactory.getObjectFactory().createRolle();
            ro.setBeschreibung("rolle2-beschreibung");
            ro.setName("rolle2-name");
            ro = taoBV.createRolle(ro);

            PersonVO p = daoFactory.getObjectFactory().createPerson();
            p.setNachname("person2-nachname");
            p.setName("person2-name");
            p.setVorname("person2-vorname");
            p = taoBV.createPerson(p);

            dbResource.getDaoFactory()
                            .getRolleDAO()
                            .addRechtToRolle(RechtId.SYSTEM_DEAKTIVIEREN,
                                            ro.getRolleId());
            daoSchleife.addPersonInRolleToSchleife(p.getPersonId(),
                            ro.getRolleId(), testObject.getSchleifeId());

            SchleifeVO[] objects = daoSchleife
                            .findMitgliedschaftInSchleifenVonPerson(p
                                            .getPersonId());
            assertNotNull(objects);
            assertTrue(objects.length >= 1);

            boolean b = false;

            for (int i = 0, m = objects.length; i < m; i++)
            {
                if (objects[i].getSchleifeId().equals(
                                testObject.getSchleifeId()))
                {
                    b = true;
                }
            }

            if (false == b)
            {
                fail("Die Mitgliedschaft der Person in der Schleife wurde nicht gefunden.");
            }

            daoSchleife.removePersonInRolleFromSchleife(p.getPersonId(),
                            ro.getRolleId(), testObject.getSchleifeId());

            SchleifeVO[] objects2 = daoSchleife
                            .findMitgliedschaftInSchleifenVonPerson(p
                                            .getPersonId());
            assertNotNull(objects2);
            assertTrue(objects2.length >= 0);

            b = false;

            for (int i = 0, m = objects2.length; i < m; i++)
            {
                if (objects2[i].getSchleifeId().equals(
                                testObject.getSchleifeId()))
                {
                    b = true;
                }
            }

            if (true == b)
            {
                fail("Die Mitgliedschaft der Person in der Schleife wurde gefunden, obwohl diese bereits gelöscht wurde.");
            }
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void removePersonFromSchleife()
    {
        assertNotNull(testObject);

        try
        {
            RolleVO ro = daoFactory.getObjectFactory().createRolle();
            ro.setBeschreibung("rolle3-beschreibung");
            ro.setName("rolle3-name");
            ro = taoBV.createRolle(ro);

            PersonVO p = daoFactory.getObjectFactory().createPerson();
            p.setNachname("person3-nachname");
            p.setName("person3-name");
            p.setVorname("person3-vorname");
            p = taoBV.createPerson(p);

            dbResource.getDaoFactory()
                            .getRolleDAO()
                            .addRechtToRolle(RechtId.SYSTEM_DEAKTIVIEREN,
                                            ro.getRolleId());
            daoSchleife.addPersonInRolleToSchleife(p.getPersonId(),
                            ro.getRolleId(), testObject.getSchleifeId());

            SchleifeVO[] objects = daoSchleife
                            .findMitgliedschaftInSchleifenVonPerson(p
                                            .getPersonId());
            assertNotNull(objects);
            assertTrue(objects.length >= 1);

            boolean b = false;

            for (int i = 0, m = objects.length; i < m; i++)
            {
                if (objects[i].getSchleifeId().equals(
                                testObject.getSchleifeId()))
                {
                    b = true;
                }
            }

            if (false == b)
            {
                fail("Die Mitgliedschaft der Person in der Schleife wurde nicht gefunden.");
            }

            daoSchleife.removePersonFromSchleife(p.getPersonId(),
                            testObject.getSchleifeId());

            SchleifeVO[] objects2 = daoSchleife
                            .findMitgliedschaftInSchleifenVonPerson(p
                                            .getPersonId());
            assertNotNull(objects2);
            assertTrue(objects2.length >= 0);

            b = false;

            for (int i = 0, m = objects2.length; i < m; i++)
            {
                if (objects2[i].getSchleifeId().equals(
                                testObject.getSchleifeId()))
                {
                    b = true;
                }
            }

            if (true == b)
            {
                fail("Die Mitgliedschaft der Person in der Schleife wurde gefunden, obwohl diese bereits gelöscht wurde.");
            }
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void removeRolleFromSchleife()
    {
        assertNotNull(testObject);

        try
        {
            RolleVO ro = daoFactory.getObjectFactory().createRolle();
            ro.setBeschreibung("rolle4-beschreibung");
            ro.setName("rolle4-name");
            ro = taoBV.createRolle(ro);

            PersonVO p = daoFactory.getObjectFactory().createPerson();
            p.setNachname("person4-nachname");
            p.setName("person4-name");
            p.setVorname("person4-vorname");
            p = taoBV.createPerson(p);

            dbResource.getDaoFactory()
                            .getRolleDAO()
                            .addRechtToRolle(RechtId.SYSTEM_DEAKTIVIEREN,
                                            ro.getRolleId());
            daoSchleife.addPersonInRolleToSchleife(p.getPersonId(),
                            ro.getRolleId(), testObject.getSchleifeId());

            SchleifeVO[] objects = daoSchleife
                            .findMitgliedschaftInSchleifenVonPerson(p
                                            .getPersonId());
            assertNotNull(objects);
            assertTrue(objects.length >= 1);

            boolean b = false;

            for (int i = 0, m = objects.length; i < m; i++)
            {
                if (objects[i].getSchleifeId().equals(
                                testObject.getSchleifeId()))
                {
                    b = true;
                }
            }

            if (false == b)
            {
                fail("Die Mitgliedschaft der Person in der Schleife wurde nicht gefunden.");
            }

            daoSchleife.removeRolleFromSchleife(ro.getRolleId(),
                            testObject.getSchleifeId());

            SchleifeVO[] objects2 = daoSchleife
                            .findMitgliedschaftInSchleifenVonPerson(p
                                            .getPersonId());
            assertNotNull(objects2);
            assertTrue(objects2.length >= 0);

            b = false;

            for (int i = 0, m = objects2.length; i < m; i++)
            {
                if (objects2[i].getSchleifeId().equals(
                                testObject.getSchleifeId()))
                {
                    b = true;
                }
            }

            if (true == b)
            {
                fail("Die Mitgliedschaft der Person in der Schleife wurde gefunden, obwohl diese bereits gelöscht wurde.");
            }
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void removeAllPersonenFromSchleife()
    {
        assertNotNull(testObject);

        try
        {
            RolleVO ro = daoFactory.getObjectFactory().createRolle();
            ro.setBeschreibung("rolle5-beschreibung");
            ro.setName("rolle5-name");
            ro = taoBV.createRolle(ro);

            PersonVO p = daoFactory.getObjectFactory().createPerson();
            p.setNachname("person5-nachname");
            p.setName("person5-name");
            p.setVorname("person5-vorname");
            p = taoBV.createPerson(p);

            PersonVO p2 = daoFactory.getObjectFactory().createPerson();
            p2.setNachname("person6-nachname");
            p2.setName("person6-name");
            p2.setVorname("person6-vorname");
            p2 = taoBV.createPerson(p2);

            dbResource.getDaoFactory()
                            .getRolleDAO()
                            .addRechtToRolle(RechtId.SYSTEM_DEAKTIVIEREN,
                                            ro.getRolleId());
            daoSchleife.addPersonInRolleToSchleife(p.getPersonId(),
                            ro.getRolleId(), testObject.getSchleifeId());
            daoSchleife.addPersonInRolleToSchleife(p2.getPersonId(),
                            ro.getRolleId(), testObject.getSchleifeId());

            SchleifeVO[] objects = daoSchleife
                            .findMitgliedschaftInSchleifenVonPerson(p
                                            .getPersonId());
            assertNotNull(objects);
            assertTrue(objects.length >= 1);

            boolean b = false;

            for (int i = 0, m = objects.length; i < m; i++)
            {
                if (objects[i].getSchleifeId().equals(
                                testObject.getSchleifeId()))
                {
                    b = true;
                }
            }

            if (false == b)
            {
                fail("Die Mitgliedschaft der Person in der Schleife wurde nicht gefunden.");
            }

            objects = daoSchleife.findMitgliedschaftInSchleifenVonPerson(p
                            .getPersonId());
            assertNotNull(objects);
            assertTrue(objects.length >= 1);

            b = false;

            for (int i = 0, m = objects.length; i < m; i++)
            {
                if (objects[i].getSchleifeId().equals(
                                testObject.getSchleifeId()))
                {
                    b = true;
                }
            }

            if (false == b)
            {
                fail("Die Mitgliedschaft der Person in der Schleife wurde nicht gefunden.");
            }

            daoSchleife.removeAllPersonenFromSchleife(testObject
                            .getSchleifeId());

            SchleifeVO[] objects2 = daoSchleife
                            .findMitgliedschaftInSchleifenVonPerson(p
                                            .getPersonId());
            assertNotNull(objects2);
            assertTrue(objects2.length >= 0);

            b = false;

            for (int i = 0, m = objects2.length; i < m; i++)
            {
                if (objects2[i].getSchleifeId().equals(
                                testObject.getSchleifeId()))
                {
                    b = true;
                }
            }

            if (true == b)
            {
                fail("Die Mitgliedschaft der Person in der Schleife wurde gefunden, obwohl diese bereits gelöscht wurde.");
            }

            objects2 = daoSchleife.findMitgliedschaftInSchleifenVonPerson(p2
                            .getPersonId());
            assertNotNull(objects2);
            assertTrue(objects2.length >= 0);

            b = false;

            for (int i = 0, m = objects2.length; i < m; i++)
            {
                if (objects2[i].getSchleifeId().equals(
                                testObject.getSchleifeId()))
                {
                    b = true;
                }
            }

            if (true == b)
            {
                fail("Die Mitgliedschaft der Person in der Schleife wurde gefunden, obwohl diese bereits gelöscht wurde.");
            }
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void removePersonFromAllSchleifen()
    {
        try
        {
            SchleifeVO s1 = daoFactory.getObjectFactory().createSchleife();
            s1.setBeschreibung("s1-beschreibung");
            s1.setName("s1-name");
            s1.setKuerzel("s1kzl");
            s1.setOrganisationsEinheitId(ORGANISATIONS_EINHEIT_ID);
            s1 = taoBV.createSchleife(s1);

            SchleifeVO s2 = daoFactory.getObjectFactory().createSchleife();
            s2.setBeschreibung("s2-beschreibung");
            s2.setName("s2-name");
            s2.setKuerzel("s2kzl");
            s2.setOrganisationsEinheitId(ORGANISATIONS_EINHEIT_ID);
            s2 = taoBV.createSchleife(s2);

            RolleVO ro = daoFactory.getObjectFactory().createRolle();
            ro.setBeschreibung("rolle6-beschreibung");
            ro.setName("rolle6-name");
            ro = taoBV.createRolle(ro);

            PersonVO p = daoFactory.getObjectFactory().createPerson();
            p.setNachname("person7-nachname");
            p.setName("person7-name");
            p.setVorname("person7-vorname");
            p = taoBV.createPerson(p);

            dbResource.getDaoFactory()
                            .getRolleDAO()
                            .addRechtToRolle(RechtId.SYSTEM_DEAKTIVIEREN,
                                            ro.getRolleId());
            daoSchleife.addPersonInRolleToSchleife(p.getPersonId(),
                            ro.getRolleId(), s1.getSchleifeId());
            daoSchleife.addPersonInRolleToSchleife(p.getPersonId(),
                            ro.getRolleId(), s2.getSchleifeId());

            SchleifeVO[] objects = daoSchleife
                            .findMitgliedschaftInSchleifenVonPerson(p
                                            .getPersonId());
            assertNotNull(objects);
            assertTrue(objects.length >= 1);

            boolean b1 = false;
            boolean b2 = false;

            for (int i = 0, m = objects.length; i < m; i++)
            {
                if (objects[i].getSchleifeId().equals(s1.getSchleifeId()))
                {
                    b1 = true;
                }

                if (objects[i].getSchleifeId().equals(s1.getSchleifeId()))
                {
                    b2 = true;
                }
            }

            if (false == (b1 && b2))
            {
                fail("Die Mitgliedschaft der Person in der Schleife wurde nicht gefunden.");
            }

            daoSchleife.removePersonFromAllSchleifen(p.getPersonId());

            SchleifeVO[] objects2 = daoSchleife
                            .findMitgliedschaftInSchleifenVonPerson(p
                                            .getPersonId());
            assertNotNull(objects2);
            assertTrue(objects2.length >= 0);

            b1 = false;
            b2 = false;

            for (int i = 0, m = objects2.length; i < m; i++)
            {
                if (objects2[i].getSchleifeId().equals(s1.getSchleifeId()))
                {
                    b1 = true;
                }

                if (objects2[i].getSchleifeId().equals(s2.getSchleifeId()))
                {
                    b2 = true;
                }
            }

            if (true == (b1 || b2))
            {
                fail("Die Mitgliedschaft der Person in der Schleife wurde gefunden, obwohl diese bereits gelöscht wurde.");
            }
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void removeRolleFromAllSchleifen()
    {
        try
        {
            SchleifeVO s3 = daoFactory.getObjectFactory().createSchleife();
            s3.setBeschreibung("s3-beschreibung");
            s3.setName("s3-name");
            s3.setKuerzel("s3kzl");
            s3.setOrganisationsEinheitId(ORGANISATIONS_EINHEIT_ID);
            s3 = taoBV.createSchleife(s3);

            SchleifeVO s4 = daoFactory.getObjectFactory().createSchleife();
            s4.setBeschreibung("s4-beschreibung");
            s4.setName("s4-name");
            s4.setKuerzel("s4kzl");
            s4.setOrganisationsEinheitId(ORGANISATIONS_EINHEIT_ID);
            s4 = taoBV.createSchleife(s4);

            RolleVO ro = daoFactory.getObjectFactory().createRolle();
            ro.setBeschreibung("rolle7-beschreibung");
            ro.setName("rolle7-name");
            ro = taoBV.createRolle(ro);

            PersonVO p = daoFactory.getObjectFactory().createPerson();
            p.setNachname("person8-nachname");
            p.setName("person8-name");
            p.setVorname("person8-vorname");
            p = taoBV.createPerson(p);

            dbResource.getDaoFactory()
                            .getRolleDAO()
                            .addRechtToRolle(RechtId.SYSTEM_DEAKTIVIEREN,
                                            ro.getRolleId());
            daoSchleife.addPersonInRolleToSchleife(p.getPersonId(),
                            ro.getRolleId(), s3.getSchleifeId());
            daoSchleife.addPersonInRolleToSchleife(p.getPersonId(),
                            ro.getRolleId(), s4.getSchleifeId());

            SchleifeVO[] objects = daoSchleife
                            .findMitgliedschaftInSchleifenVonPerson(p
                                            .getPersonId());
            assertNotNull(objects);
            assertTrue(objects.length >= 1);

            boolean b1 = false;
            boolean b2 = false;

            for (int i = 0, m = objects.length; i < m; i++)
            {
                if (objects[i].getSchleifeId().equals(s3.getSchleifeId()))
                {
                    b1 = true;
                }

                if (objects[i].getSchleifeId().equals(s3.getSchleifeId()))
                {
                    b2 = true;
                }
            }

            if (false == (b1 && b2))
            {
                fail("Die Mitgliedschaft der Person in der Schleife wurde nicht gefunden.");
            }

            daoSchleife.removeRolleFromAllSchleifen(ro.getRolleId());

            SchleifeVO[] objects2 = daoSchleife
                            .findMitgliedschaftInSchleifenVonPerson(p
                                            .getPersonId());
            assertNotNull(objects2);
            assertTrue(objects2.length >= 0);

            b1 = false;
            b2 = false;

            for (int i = 0, m = objects2.length; i < m; i++)
            {
                if (objects2[i].getSchleifeId().equals(s3.getSchleifeId()))
                {
                    b1 = true;
                }

                if (objects2[i].getSchleifeId().equals(s4.getSchleifeId()))
                {
                    b2 = true;
                }
            }

            if (true == (b1 || b2))
            {
                fail("Die Mitgliedschaft der Person in der Schleife wurde gefunden, obwohl diese bereits gelöscht wurde.");
            }
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void createSchleifeAlreadyExistent()
    {
        SchleifeVO s = taoBV.createSchleife(testObject);
        assertNull(s);
    }

    @Test
    public void createSchleifeDeleted()
    {
        SchleifeVO s = daoFactory.getObjectFactory().createSchleife();
        try
        {
            s.setBeschreibung("createDeleted-Schleife");
            s.setName("createDeleted-Schleife");
            s.setKuerzel("crDeS");
            s.setOrganisationsEinheitId(ORGANISATIONS_EINHEIT_ID);
            s = taoBV.createSchleife(s);

            boolean result = taoBV.deleteSchleife(s.getSchleifeId());
            assertTrue(result);

            SchleifeVO createDeletedSchleife = taoBV.createSchleife(s);
            assertEquals(createDeletedSchleife.getBeschreibung(),
                            s.getBeschreibung());
            assertEquals(createDeletedSchleife.getName(), s.getName());
            assertEquals(createDeletedSchleife.getKuerzel(), s.getKuerzel());
            assertEquals(createDeletedSchleife.getOrganisationsEinheitId(),
                            s.getOrganisationsEinheitId());
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void findZuerstAusgeloesteSchleifeInAlarm()
    {
        assertNotNull(testObject);

        try
        {
            // Erste und einzige ausgeloeste Schleife: testObject
            AlarmVO alarm = klinikumAlarmService
                            .alarmAusloesen("", new SchleifeVO[]
                            { testObject }, AlarmQuelleId.ID_5TON, null, "", "");
            SchleifeVO vo = daoSchleife
                            .findZuerstAusgeloesteSchleifeInAlarm(alarm
                                            .getAlarmId());
            klinikumAlarmService.processAktiveAlarme();
            assertObject(vo);

            // Erstelle neue Schleife
            SchleifeVO s = daoFactory.getObjectFactory().createSchleife();
            String nameNeu = "s-test";
            String kuerzelNeu = "stest";
            s.setName(nameNeu);
            s.setKuerzel(kuerzelNeu);
            s.setOrganisationsEinheitId(ORGANISATIONS_EINHEIT_ID);
            s = taoBV.createSchleife(s);
            assertNotNull(s);

            // Erste von zwei ausgelosten Schleifen: testObject
            alarm = klinikumAlarmService.alarmAusloesen("", new SchleifeVO[]
            { testObject, s }, AlarmQuelleId.ID_5TON, null, "", "");
            vo = daoSchleife.findZuerstAusgeloesteSchleifeInAlarm(alarm
                            .getAlarmId());
            klinikumAlarmService.processAktiveAlarme();
            assertObject(vo);

            // Erste von zwei ausgeloseten Schleifen: s
            alarm = klinikumAlarmService.alarmAusloesen("", new SchleifeVO[]
            { s, testObject }, AlarmQuelleId.ID_5TON, null, "", "");
            vo = daoSchleife.findZuerstAusgeloesteSchleifeInAlarm(alarm
                            .getAlarmId());
            klinikumAlarmService.processAktiveAlarme();
            assertEquals(s, vo);

        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void findSchleifenByAlarmId()
    {
        assertNotNull(testObject);

        try
        {
            AlarmVO alarm = klinikumAlarmService
                            .alarmAusloesen("", new SchleifeVO[]
                            { testObject }, AlarmQuelleId.ID_5TON, null, "", "");

            SchleifeVO[] objects = daoSchleife.findSchleifenByAlarmId(alarm
                            .getAlarmId());

            klinikumAlarmService.processAktiveAlarme();

            assertNotNull(objects);
            assertTrue(objects.length >= 1);

            boolean b = false;

            for (int i = 0, m = objects.length; i < m; i++)
            {
                if (objects[i].getSchleifeId().equals(
                                testObject.getSchleifeId()))
                {
                    assertObject(testObject);
                    b = true;
                }
            }

            if (false == b)
            {
                fail("findSchleifenByAlarmId: Schleife nicht gefunden");
            }
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void istSchleifeInProbeAlarm()
    {
        assertNotNull(testObject);

        ProbeTerminVO pt = daoFactory.getObjectFactory().createProbeTermin();

        try
        {
            UnixTime ptStart = UnixTime.now();
            UnixTime ptEnde = new UnixTime(UnixTime.now().getTimeStamp() + 4000);

            pt.setStart(ptStart);
            pt.setEnde(ptEnde);
            pt.setOrganisationsEinheitId(ORGANISATIONS_EINHEIT_ID);

            boolean b = daoSchleife.istSchleifeInProbeAlarm(testObject
                            .getSchleifeId());
            assertFalse(b);

            pt = dbResource.getTaoFactory().getProbeTerminTAO()
                            .createProbeTermin(pt);
            assertNotNull(pt);
            assertEquals(ptStart.getTimeStamp(), pt.getStart().getTimeStamp());
            assertEquals(ptEnde.getTimeStamp(), pt.getEnde().getTimeStamp());
            assertEquals(ORGANISATIONS_EINHEIT_ID,
                            pt.getOrganisationsEinheitId());

            b = daoSchleife.istSchleifeInProbeAlarm(testObject.getSchleifeId());
            assertTrue(b);
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void findSchleifenByPersonAndRechtForJS()
    {
        assertNotNull(testObject);

        PersonVO p = daoFactory.getObjectFactory().createPerson();
        RolleVO ro = daoFactory.getObjectFactory().createRolle();

        try
        {
            p.setNachname("person9-nachname");
            p.setVorname("person9-vorname");
            p.setName("person9-name");
            p = taoBV.createPerson(p);

            ro.setName("rolle8-name");
            ro.setBeschreibung("rolle8-beschreibung");
            ro = taoBV.createRolle(ro);

            RechtId rechtId = RechtId.SYSTEM_DEAKTIVIEREN;

            dbResource.getDaoFactory().getRolleDAO()
                            .addRechtToRolle(rechtId, ro.getRolleId());

            daoSchleife.addPersonInRolleToSchleife(p.getPersonId(),
                            ro.getRolleId(), testObject.getSchleifeId());

            String expected = ORGANISATION_ID
                            + "?"
                            + dbResource.getDaoFactory()
                                            .getOrganisationDAO()
                                            .findOrganisationById(
                                                            ORGANISATION_ID)
                                            .getName()
                            + "?"
                            + ORGANISATIONS_EINHEIT_ID
                            + "?"
                            + dbResource.getDaoFactory()
                                            .getOrganisationsEinheitDAO()
                                            .findOrganisationsEinheitById(
                                                            ORGANISATIONS_EINHEIT_ID)
                                            .getName() + "?"
                            + testObject.getSchleifeId() + "?"
                            + testObject.getKuerzel() + "?"
                            + testObject.getName() + "\n";

            String r = daoSchleife.findSchleifenByPersonAndRechtForJS(
                            p.getPersonId(), rechtId);

            assertEquals(expected, r);
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void findSchleifenByPersonAndRechtForJSAsHashMap()
    {
        assertNotNull(testObject);

        PersonVO p = daoFactory.getObjectFactory().createPerson();
        RolleVO ro = daoFactory.getObjectFactory().createRolle();

        try
        {
            p.setNachname("person10-nachname");
            p.setVorname("person10-vorname");
            p.setName("person10-name");
            p = taoBV.createPerson(p);

            ro.setName("rolle9-name");
            ro.setBeschreibung("rolle9-beschreibung");
            ro = taoBV.createRolle(ro);

            RechtId rechtId = RechtId.SYSTEM_DEAKTIVIEREN;

            dbResource.getDaoFactory().getRolleDAO()
                            .addRechtToRolle(rechtId, ro.getRolleId());

            daoSchleife.addPersonInRolleToSchleife(p.getPersonId(),
                            ro.getRolleId(), testObject.getSchleifeId());

            Map<String, String> expected = new HashMap<String, String>();
            expected.put("" + testObject.getSchleifeId(),
                            testObject.getKuerzel());

            Map<String, String> r = daoSchleife
                            .findSchleifenByPersonAndRechtForJSAsHashMap(
                                            p.getPersonId(), rechtId);

            assertEquals(expected, r);
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void setSchleifeIdNull()
    {
        SchleifeVO vo = daoFactory.getObjectFactory().createSchleife();
        try
        {
            vo.setSchleifeId(null);
            fail("Test did not catch StdException.");
        }
        catch (StdException e)
        {

        }
    }

    @Test
    public void setNameNull()
    {
        SchleifeVO vo = daoFactory.getObjectFactory().createSchleife();
        try
        {
            vo.setName(null);
            fail("Test did not catch StdException.");
        }
        catch (StdException e)
        {

        }
    }

    @Test
    public void setNameEmpty()
    {
        SchleifeVO vo = daoFactory.getObjectFactory().createSchleife();
        try
        {
            vo.setName("");
            fail("Test did not catch StdException.");
        }
        catch (StdException e)
        {

        }
    }

    @Test
    public void setKuerzelNull()
    {
        SchleifeVO vo = daoFactory.getObjectFactory().createSchleife();
        try
        {
            vo.setKuerzel(null);
            fail("Test did not catch StdException.");
        }
        catch (StdException e)
        {

        }
    }

    @Test
    public void setKuerzelEmpty()
    {
        SchleifeVO vo = daoFactory.getObjectFactory().createSchleife();
        try
        {
            vo.setKuerzel("");
            fail("Test did not catch StdException.");
        }
        catch (StdException e)
        {

        }
    }

    @Test
    public void getDisplayName()
    {
        if (null == testObject)
        {
            createSchleife();
        }

        assertNotNull(testObject);

        assertEquals(testObject.getDisplayName(), NAME + " (" + KUERZEL + ")");
    }

    @Test
    public void testToString()
    {
        assertNotNull(testObject);

        assertEquals(testObject.toString(), NAME + " (" + KUERZEL + ")");
    }
}
