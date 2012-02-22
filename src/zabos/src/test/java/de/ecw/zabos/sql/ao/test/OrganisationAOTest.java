package de.ecw.zabos.sql.ao.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.sql.dao.OrganisationDAO;
import de.ecw.zabos.sql.vo.OrganisationVO;
import de.ecw.zabos.test.ZabosTestAdapter;

public class OrganisationAOTest extends ZabosTestAdapter
{
    private final static String BESCHREIBUNG = "beschreibung";

    private final static String NAME = "name";

    private static OrganisationVO testObject = null;

    private static OrganisationDAO daoOrganisation;

    private static boolean isInitialized = false;

    private void assertObject(OrganisationVO r)
    {
        assertNotNull(r);
        assertEquals(r.getBeschreibung(), BESCHREIBUNG);
        assertEquals(r.getName(), NAME);
        assertNotNull(r.getOrganisationId());
        assertTrue(r.getOrganisationId().getLongValue() > 0);
    }

    @Before
    public synchronized void initEnvironmentSub()
    {
        if (!isInitialized)
        {
            daoOrganisation = dbResource.getDaoFactory().getOrganisationDAO();
        }
    }

    @Test
    public void createOrganisation()
    {
        OrganisationVO vo = daoFactory.getObjectFactory().createOrganisation();
        try
        {
            vo.setBeschreibung(BESCHREIBUNG);
            vo.setName(NAME);
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }

        OrganisationVO r = taoBV.createOrganisation(vo);

        assertObject(r);

        testObject = r;
    }

    @Test
    public void updateOrganisation()
    {
        try
        {
            String beschreibungNeu = "Organisation-Beschreibung-Neu";
            String nameNeu = "Organisation-Name-Neu";
            testObject.setBeschreibung(beschreibungNeu);
            testObject.setName(nameNeu);

            OrganisationVO updated = taoBV.updateOrganisation(testObject);
            assertNotNull(updated);
            assertEquals(updated.getOrganisationId().getLongValue(), testObject
                            .getOrganisationId().getLongValue());
            assertEquals(updated.getBeschreibung(), beschreibungNeu);
            assertEquals(updated.getName(), nameNeu);
            testObject.setBeschreibung(BESCHREIBUNG);
            testObject.setName(NAME);
            testObject = taoBV.updateOrganisation(testObject);
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void deleteOrganisation()
    {
        try
        {
            OrganisationVO deleteTest = daoFactory.getObjectFactory()
                            .createOrganisation();
            deleteTest.setBeschreibung("Organisation-zu-loeschen");
            deleteTest.setName("Organisation-zu-loeschen");
            deleteTest = taoBV.createOrganisation(deleteTest);
            assertNotNull(deleteTest);

            boolean result = taoBV.deleteOrganisation(deleteTest
                            .getOrganisationId());
            assertTrue(result);

            OrganisationVO[] objects = daoOrganisation.findAll();
            assertNotNull(objects);
            assertEquals(1, objects.length);

            for (int i = 0, m = objects.length; i < m; i++)
            {
                if (objects[i].getOrganisationId().equals(
                                deleteTest.getOrganisationId()))
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
    public void findOrganisationById()
    {
        assertNotNull(testObject);

        try
        {
            OrganisationVO r = daoOrganisation.findOrganisationById(testObject
                            .getOrganisationId());
            assertObject(r);
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void findOrganisationByName()
    {
        assertNotNull(testObject);

        try
        {
            OrganisationVO r = daoOrganisation
                            .findOrganisationByName(testObject.getName());
            assertObject(r);
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void setOrganisationIdNull()
    {
        OrganisationVO vo = daoFactory.getObjectFactory().createOrganisation();
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
        OrganisationVO vo = daoFactory.getObjectFactory().createOrganisation();
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
        OrganisationVO vo = daoFactory.getObjectFactory().createOrganisation();
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
    public void testToString()
    {
        assertNotNull(testObject);

        assertEquals(testObject.toString(), NAME);
    }
}
