package de.ecw.zabos.sql.ao.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.sql.dao.OrganisationsEinheitDAO;
import de.ecw.zabos.sql.vo.OrganisationVO;
import de.ecw.zabos.sql.vo.OrganisationsEinheitVO;
import de.ecw.zabos.test.ZabosTestAdapter;
import de.ecw.zabos.types.id.OrganisationId;

public class OrganisationsEinheitAOTest extends ZabosTestAdapter
{
    private static OrganisationId ORGANISATION_ID;

    private final static String NAME = "name";

    private final static String BESCHREIBUNG = "beschreibung";

    private static boolean GELOESCHT = false;

    private static OrganisationsEinheitVO testObject = null;

    private static OrganisationsEinheitDAO daoOrganisationsEinheit;

    private static boolean isInitialized = false;

    private void assertObject(OrganisationsEinheitVO r)
    {
        assertNotNull(r);
        assertEquals(r.getBeschreibung(), BESCHREIBUNG);
        assertEquals(r.getGeloescht(), GELOESCHT);
        assertEquals(r.getName(), NAME);
        assertEquals(r.getOrganisationId(), ORGANISATION_ID);
    }

    @Before
    public synchronized void initEnvironmentSub()
    {
        if (!isInitialized)
        {
            OrganisationVO organisation = daoFactory.getObjectFactory()
                            .createOrganisation();

            try
            {
                organisation.setBeschreibung("organisation-" + BESCHREIBUNG);
                organisation.setName("organisation-" + NAME);
                organisation = taoBV.createOrganisation(organisation);
            }
            catch (StdException e)
            {
                fail(e.getMessage());
            }

            assertNotNull(organisation);

            ORGANISATION_ID = organisation.getOrganisationId();

            daoOrganisationsEinheit = dbResource.getDaoFactory()
                            .getOrganisationsEinheitDAO();
            isInitialized = true;
        }
    }

    @Test
    public void createOrganisationsEinheit()
    {
        if (null == testObject)
        {
            OrganisationsEinheitVO vo = daoFactory.getObjectFactory()
                            .createOrganisationsEinheit();

            try
            {
                vo.setBeschreibung(BESCHREIBUNG);
                vo.setGeloescht(GELOESCHT);
                vo.setName(NAME);
                vo.setOrganisationId(ORGANISATION_ID);
            }
            catch (StdException e)
            {
                fail(e.getMessage());
            }

            OrganisationsEinheitVO r = taoBV.createOrganisationseinheit(vo);

            assertObject(r);

            testObject = r;
        }
    }

    @Test
    public void updateOrganisationsEinheit()
    {
        if (null == testObject)
        {
            createOrganisationsEinheit();
        }

        assertNotNull(testObject);

        try
        {
            OrganisationVO organisation = daoFactory.getObjectFactory()
                            .createOrganisation();
            organisation.setBeschreibung("organisation-beschreibung-neu");
            organisation.setName("organisation-name-neu");
            organisation.setGeloescht(false);
            organisation = taoBV.createOrganisation(organisation);
            assertNotNull(organisation);

            String beschreibungNeu = "beschreibung-neu";
            String nameNeu = "name-neu";

            testObject.setBeschreibung(beschreibungNeu);
            // testObject.setGeloescht(true);
            testObject.setName(nameNeu);
            testObject.setOrganisationId(organisation.getOrganisationId());

            OrganisationsEinheitVO updated = taoBV
                            .updateOrganisationsEinheit(testObject);

            assertNotNull(updated);
            assertEquals(updated.getBeschreibung(), beschreibungNeu);
            // assertEquals(updated.getGeloescht(), true);
            assertEquals(updated.getName(), nameNeu);
            assertEquals(updated.getOrganisationId(),
                            organisation.getOrganisationId());

            testObject.setBeschreibung(BESCHREIBUNG);
            testObject.setGeloescht(GELOESCHT);
            testObject.setName(NAME);
            testObject.setOrganisationId(ORGANISATION_ID);

            testObject = taoBV.updateOrganisationsEinheit(testObject);
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void deleteOrganisationsEinheit()
    {
        OrganisationsEinheitVO deleteTest = daoFactory.getObjectFactory()
                        .createOrganisationsEinheit();

        try
        {
            deleteTest.setBeschreibung("OrganisationsEinheit-zu-loeschen");
            deleteTest.setGeloescht(false);
            deleteTest.setName("OrganisationsEinheit-zu-loeschen");
            deleteTest.setOrganisationId(ORGANISATION_ID);
            deleteTest = taoBV.createOrganisationseinheit(deleteTest);
            assertNotNull(deleteTest);

            taoBV.deleteOrganisationseinheit(deleteTest
                            .getOrganisationsEinheitId());
            OrganisationsEinheitVO[] objects = daoOrganisationsEinheit
                            .findAll();
            assertNotNull(objects);
            assertTrue(objects.length >= 0);

            for (int i = 0, m = objects.length; i < m; i++)
            {
                if (objects[i].getOrganisationsEinheitId().equals(
                                deleteTest.getOrganisationsEinheitId()))
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
    public void findOrganisationsEinheitById()
    {
        if (null == testObject)
        {
            createOrganisationsEinheit();
        }

        assertNotNull(testObject);

        try
        {
            OrganisationsEinheitVO object = daoOrganisationsEinheit
                            .findOrganisationsEinheitById(testObject
                                            .getOrganisationsEinheitId());
            assertObject(object);
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void findOrganisationsEinheitByName()
    {
        if (null == testObject)
        {
            createOrganisationsEinheit();
        }

        assertNotNull(testObject);

        try
        {
            OrganisationsEinheitVO object = daoOrganisationsEinheit
                            .findOrganisationsEinheitByName(testObject
                                            .getName());
            assertObject(object);
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void findOrganisationsEinheitenByOrganisationId()
    {
        if (null == testObject)
        {
            createOrganisationsEinheit();
        }

        assertNotNull(testObject);

        try
        {
            OrganisationsEinheitVO[] objects = daoOrganisationsEinheit
                            .findOrganisationsEinheitenByOrganisationId(ORGANISATION_ID);
            assertNotNull(objects);
            assertTrue(objects.length > 0);

            boolean b = false;

            for (int i = 0, m = objects.length; i < m; i++)
            {
                if (objects[i].getOrganisationsEinheitId().equals(
                                testObject.getOrganisationsEinheitId()))
                {
                    assertObject(objects[i]);
                    b = true;
                }
            }

            if (false == b)
            {
                fail("Der gesuchte Datensatz wurde nicht gefunden.");
            }
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void findAll()
    {
        if (null == testObject)
        {
            createOrganisationsEinheit();
        }

        OrganisationsEinheitVO vo = daoFactory.getObjectFactory()
                        .createOrganisationsEinheit();

        try
        {
            vo.setBeschreibung("find-all");
            vo.setGeloescht(GELOESCHT);
            vo.setName("find-all");
            vo.setOrganisationId(ORGANISATION_ID);
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }

        vo = taoBV.createOrganisationseinheit(vo);
        assertNotNull(vo);

        try
        {
            OrganisationsEinheitVO[] objects = daoOrganisationsEinheit
                            .findAll();
            assertNotNull(objects);
            assertTrue(objects.length > 0);

            boolean b = false;

            for (int i = 0, m = objects.length; i < m; i++)
            {
                if (objects[i].getOrganisationsEinheitId().equals(
                                vo.getOrganisationsEinheitId()))
                {
                    assertEquals(objects[i].getBeschreibung(),
                                    vo.getBeschreibung());
                    assertEquals(objects[i].getGeloescht(), vo.getGeloescht());
                    assertEquals(objects[i].getName(), vo.getName());
                    assertEquals(objects[i].getOrganisationId(),
                                    vo.getOrganisationId());
                    b = true;
                }
            }

            if (false == b)
            {
                fail("Der gesuchte Datensatz wurde nicht gefunden.");
            }
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void setOrganisationsEinheitIdNull()
    {
        OrganisationsEinheitVO vo = daoFactory.getObjectFactory()
                        .createOrganisationsEinheit();
        try
        {
            vo.setOrganisationsEinheitId(null);
            fail("Test did not catch StdException.");
        }
        catch (Exception e)
        {

        }
    }

    @Test
    public void setOrganisationIdNull()
    {
        OrganisationsEinheitVO vo = daoFactory.getObjectFactory()
                        .createOrganisationsEinheit();
        try
        {
            vo.setOrganisationId(null);
            fail("Test did not catch StdException.");
        }
        catch (StdException e)
        {

        }
    }

    @Test
    public void setNameNull()
    {
        OrganisationsEinheitVO vo = daoFactory.getObjectFactory()
                        .createOrganisationsEinheit();
        try
        {
            vo.setName(null);
            fail("Test did not catch StdException");
        }
        catch (StdException e)
        {

        }
    }

    @Test
    public void setNameEmpty()
    {
        OrganisationsEinheitVO vo = daoFactory.getObjectFactory()
                        .createOrganisationsEinheit();
        try
        {
            vo.setName("");
            fail("Test did not catch StdException.");
        }
        catch (Exception e)
        {

        }
    }

    @Test
    public void testToString()
    {
        assertNotNull(testObject);

        assertEquals(testObject.toString(), NAME);
    }
}
