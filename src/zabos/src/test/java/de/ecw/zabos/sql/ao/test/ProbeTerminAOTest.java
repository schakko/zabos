package de.ecw.zabos.sql.ao.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.sql.dao.ProbeTerminDAO;
import de.ecw.zabos.sql.tao.ProbeTerminTAO;
import de.ecw.zabos.sql.vo.OrganisationVO;
import de.ecw.zabos.sql.vo.OrganisationsEinheitVO;
import de.ecw.zabos.sql.vo.ProbeTerminVO;
import de.ecw.zabos.test.ZabosTestAdapter;
import de.ecw.zabos.types.UnixTime;
import de.ecw.zabos.types.id.OrganisationsEinheitId;

public class ProbeTerminAOTest extends ZabosTestAdapter
{
    private static OrganisationsEinheitId ORGANISATIONS_EINHEIT_ID;

    private static UnixTime START = UnixTime.now();

    private static UnixTime ENDE = UnixTime.now();

    private static ProbeTerminVO testObject = null;

    private static ProbeTerminDAO daoProbeTermin;

    private static ProbeTerminTAO taoProbeTermin;

    private static boolean isInitialized = false;

    private void assertObject(ProbeTerminVO r)
    {
        assertNotNull(r);
        assertEquals(r.getOrganisationsEinheitId(), ORGANISATIONS_EINHEIT_ID);
        assertEquals(r.getStart().getTimeStamp(), START.getTimeStamp());
        assertEquals(r.getEnde().getTimeStamp(), ENDE.getTimeStamp());
    }

    @Before
    public synchronized void initEnvironmentSub()
    {
        if (!isInitialized)
        {
            taoProbeTermin = dbResource.getTaoFactory().getProbeTerminTAO();
            daoProbeTermin = dbResource.getDaoFactory().getProbeTerminDAO();

            OrganisationVO organisation = daoFactory.getObjectFactory()
                            .createOrganisation();

            try
            {
                organisation.setBeschreibung("organisation-beschreibung");
                organisation.setName("organisation-name");
                organisation.setGeloescht(false);
            }
            catch (StdException e)
            {
                fail(e.getMessage());
            }

            organisation = dbResource.getTaoFactory()
                            .getBenutzerVerwaltungTAO()
                            .createOrganisation(organisation);

            OrganisationsEinheitVO organisationsEinheit = daoFactory
                            .getObjectFactory().createOrganisationsEinheit();

            try
            {
                organisationsEinheit
                                .setBeschreibung("organisationseinheit-beschreibung");
                organisationsEinheit.setName("organisationseinheit-name");
                organisationsEinheit.setGeloescht(false);
                organisationsEinheit.setOrganisationId(organisation
                                .getOrganisationId());
            }
            catch (StdException e)
            {
                fail(e.getMessage());
            }

            organisationsEinheit = dbResource.getTaoFactory()
                            .getBenutzerVerwaltungTAO()
                            .createOrganisationseinheit(organisationsEinheit);
            ORGANISATIONS_EINHEIT_ID = organisationsEinheit
                            .getOrganisationsEinheitId();

            isInitialized = true;
        }
    }

    @Test
    public void createProbeTermin()
    {
        ProbeTerminVO vo = daoFactory.getObjectFactory().createProbeTermin();

        try
        {
            vo.setOrganisationsEinheitId(ORGANISATIONS_EINHEIT_ID);
            vo.setStart(START);
            vo.setEnde(ENDE);
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }

        ProbeTerminVO r = taoProbeTermin.createProbeTermin(vo);

        assertObject(r);

        testObject = r;
    }

    @Test
    public void updateProbeTermin()
    {
        assertNotNull(testObject);

        try
        {
            UnixTime startNeu = UnixTime.now();
            UnixTime endeNeu = UnixTime.now();

            OrganisationsEinheitVO oen = daoFactory.getObjectFactory()
                            .createOrganisationsEinheit();
            oen.setBeschreibung("organisationseinheit-beschreibung-neu");
            oen.setName("organisationseinheit-name-neu");
            oen.setGeloescht(false);
            oen.setOrganisationId(dbResource
                            .getDaoFactory()
                            .getOrganisationsEinheitDAO()
                            .findOrganisationsEinheitById(
                                            ORGANISATIONS_EINHEIT_ID)
                            .getOrganisationId());

            oen = dbResource.getTaoFactory().getBenutzerVerwaltungTAO()
                            .createOrganisationseinheit(oen);
            assertNotNull(oen);

            testObject.setStart(startNeu);
            testObject.setEnde(endeNeu);
            testObject.setOrganisationsEinheitId(oen
                            .getOrganisationsEinheitId());

            ProbeTerminVO updated = taoProbeTermin
                            .updateProbeTermin(testObject);
            assertNotNull(updated);
            assertEquals(updated.getStart().getTimeStamp(),
                            startNeu.getTimeStamp());
            assertEquals(updated.getEnde().getTimeStamp(),
                            endeNeu.getTimeStamp());
            assertEquals(updated.getOrganisationsEinheitId(),
                            oen.getOrganisationsEinheitId());

            testObject.setStart(START);
            testObject.setEnde(ENDE);
            testObject.setOrganisationsEinheitId(ORGANISATIONS_EINHEIT_ID);
            testObject = taoProbeTermin.updateProbeTermin(testObject);
            assertObject(testObject);
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void deleteProbeTermin()
    {
        try
        {
            ProbeTerminVO deleteTest = daoFactory.getObjectFactory()
                            .createProbeTermin();
            deleteTest.setStart(UnixTime.now());
            deleteTest.setEnde(UnixTime.now());
            deleteTest.setOrganisationsEinheitId(ORGANISATIONS_EINHEIT_ID);

            deleteTest = taoProbeTermin.createProbeTermin(deleteTest);
            assertNotNull(testObject);

            boolean result = taoProbeTermin.deleteProbeTermin(deleteTest
                            .getProbeTerminId());
            assertTrue(result);

            ProbeTerminVO vo = daoProbeTermin.findProbeTerminById(deleteTest
                            .getProbeTerminId());
            assertNull(vo);
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void findProbeTerminById()
    {
        assertNotNull(testObject);

        try
        {
            ProbeTerminVO vo = daoProbeTermin.findProbeTerminById(testObject
                            .getProbeTerminId());
            assertObject(vo);
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void findProbeTerminByOrganisationsEinheitId()
    {
        assertNotNull(testObject);

        try
        {
            boolean b = false;

            ProbeTerminVO[] objects = daoProbeTermin
                            .findProbeTermineByOrganisationsEinheitId(ORGANISATIONS_EINHEIT_ID);
            assertNotNull(objects);
            assertTrue(objects.length > 0);

            for (int i = 0, m = objects.length; i < m; i++)
            {
                if (objects[i].getProbeTerminId().equals(
                                testObject.getProbeTerminId()))
                {
                    assertObject(objects[i]);
                    b = true;
                }
            }

            if (false == b)
            {
                fail("Der Testdatensatz wurde nicht gefunden");
            }
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void findProbeTerminByZeitfenster()
    {
        assertNotNull(testObject);

        try
        {
            boolean b = false;

            ProbeTerminVO[] objects = daoProbeTermin
                            .findProbeTermineByZeitfenster(START, ENDE);
            assertNotNull(objects);
            assertTrue(objects.length > 0);

            for (int i = 0, m = objects.length; i < m; i++)
            {
                if (objects[i].getProbeTerminId().equals(
                                testObject.getProbeTerminId()))
                {
                    assertObject(objects[i]);
                    b = true;
                }
            }

            if (false == b)
            {
                fail("Der Testdatensatz wurde nicht gefunden");
            }
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void findProbeTerminByZeitfensterAndOrganisatiosEinheitId()
    {
        assertNotNull(testObject);

        try
        {
            boolean b = false;

            ProbeTerminVO[] objects = daoProbeTermin
                            .findProbeTermineByZeitfensterAndOrganisationsEinheitId(
                                            START, ENDE,
                                            ORGANISATIONS_EINHEIT_ID);
            assertNotNull(objects);
            assertTrue(objects.length > 0);

            for (int i = 0, m = objects.length; i < m; i++)
            {
                if (objects[i].getProbeTerminId().equals(
                                testObject.getProbeTerminId()))
                {
                    assertObject(objects[i]);
                    b = true;
                }
            }

            if (false == b)
            {
                fail("Der Testdatensatz wurde nicht gefunden");
            }
        }
        catch (Exception e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void setProbeTerminIdNull()
    {
        ProbeTerminVO vo = daoFactory.getObjectFactory().createProbeTermin();
        try
        {
            vo.setProbeTerminId(null);
            fail("Test did not catch StdException.");
        }
        catch (StdException e)
        {

        }
    }

    @Test
    public void setOrganisationsEinheitIdNull()
    {
        ProbeTerminVO vo = daoFactory.getObjectFactory().createProbeTermin();
        try
        {
            vo.setOrganisationsEinheitId(null);
            fail("Test did not catch StdException.");
        }
        catch (StdException e)
        {

        }
    }

    @Test
    public void setStartNull()
    {
        ProbeTerminVO vo = daoFactory.getObjectFactory().createProbeTermin();
        try
        {
            vo.setStart(null);
            fail("Test did not catch StdException.");
        }
        catch (StdException e)
        {

        }
    }

    @Test
    public void setEndeNull()
    {
        ProbeTerminVO vo = daoFactory.getObjectFactory().createProbeTermin();
        try
        {
            vo.setEnde(null);
            fail("Test did not catch StdException.");
        }
        catch (StdException e)
        {

        }
    }
}
