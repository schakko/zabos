package de.ecw.zabos.sql.ao.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.sql.dao.RechtDAO;
import de.ecw.zabos.sql.dao.RolleDAO;
import de.ecw.zabos.sql.tao.RechteTAO;
import de.ecw.zabos.sql.vo.OrganisationVO;
import de.ecw.zabos.sql.vo.OrganisationsEinheitVO;
import de.ecw.zabos.sql.vo.PersonVO;
import de.ecw.zabos.sql.vo.RechtVO;
import de.ecw.zabos.sql.vo.RolleVO;
import de.ecw.zabos.sql.vo.SchleifeVO;
import de.ecw.zabos.test.ZabosTestAdapter;
import de.ecw.zabos.types.id.RechtId;

public class RolleAOTest extends ZabosTestAdapter
{
    private final static String BESCHREIBUNG = "beschreibung";

    private final static String NAME = "name";

    private static RolleVO testObject = null;

    private static RolleDAO daoRolle;

    private static boolean isInitialized = false;

    private void assertObject(RolleVO r)
    {
        assertNotNull(r);
        assertEquals(r.getBeschreibung(), BESCHREIBUNG);
        assertEquals(r.getName(), NAME);
        assertNotNull(r.getRolleId());
        assertTrue(r.getRolleId().getLongValue() > 0);
    }

    @Before
    public synchronized void initEnvironmentSub()
    {
        if (!isInitialized)
        {
            daoRolle = dbResource.getDaoFactory().getRolleDAO();
        }
    }

    @Test
    public void createRolle()
    {
        if (null == testObject)
        {
            RolleVO vo = daoFactory.getObjectFactory().createRolle();
            try
            {
                vo.setBeschreibung(BESCHREIBUNG);
                vo.setName(NAME);
            }
            catch (StdException e)
            {
                fail(e.getMessage());
            }

            RolleVO r = taoBV.createRolle(vo);

            assertObject(r);

            testObject = r;
        }
    }

    @Test
    public void updateRolle()
    {
        try
        {
            String beschreibungNeu = "Rolle-Beschreibung-Neu";
            String nameNeu = "Rolle-Name-Neu";
            testObject.setBeschreibung(beschreibungNeu);
            testObject.setName(nameNeu);

            RolleVO updated = taoBV.updateRolle(testObject);
            assertNotNull(updated);
            assertEquals(updated.getRolleId().getLongValue(), testObject
                            .getRolleId().getLongValue());
            assertEquals(updated.getBeschreibung(), beschreibungNeu);
            assertEquals(updated.getName(), nameNeu);

            testObject.setBeschreibung(BESCHREIBUNG);
            testObject.setName(NAME);
            testObject = taoBV.updateRolle(testObject);
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void deleteRolle()
    {
        try
        {
            RolleVO deleteTest = daoFactory.getObjectFactory().createRolle();
            deleteTest.setBeschreibung("Rolle-zu-loeschen");
            deleteTest.setName("Rolle-zu-loeschen");
            deleteTest = taoBV.createRolle(deleteTest);
            assertNotNull(deleteTest);

            boolean result = taoBV.deleteRolle(deleteTest.getRolleId());
            assertTrue(result);

            RolleVO[] objects = daoRolle.findAll();
            assertNotNull(objects);
            assertEquals(1, objects.length);

            for (int i = 0, m = objects.length; i < m; i++)
            {
                if (objects[i].getRolleId().equals(deleteTest.getRolleId()))
                {
                    fail("Der geloeschte Datensatz wurde zurueckgeliefert");
                }
            }

        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void findById()
    {
        assertNotNull(testObject);

        try
        {
            testObject = daoRolle.findRolleById(testObject.getRolleId());
            assertObject(testObject);
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void findByName()
    {
        assertNotNull(testObject);

        try
        {
            testObject = daoRolle.findRolleByName(testObject.getName());
            assertObject(testObject);
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void addRechtToRolle()
    {
        try
        {
            RechtVO recht = dbResource.getDaoFactory().getRechtDAO()
                            .findRechtById(RechtId.SYSTEM_DEAKTIVIEREN);
            assertNotNull(recht);

            daoRolle.addRechtToRolle(recht.getRechtId(),
                            testObject.getRolleId());
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void removeRechtFromRolle()
    {
        try
        {
            RechtVO recht = dbResource.getDaoFactory().getRechtDAO()
                            .findRechtById(RechtId.SYSTEM_DEAKTIVIEREN);
            assertNotNull(recht);

            daoRolle.removeRechtFromRolle(recht.getRechtId(),
                            testObject.getRolleId());
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void removeRechteFromRolle()
    {
        try
        {
            daoRolle.removeRechteFromRolle(testObject.getRolleId());
        }
        catch (Exception e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void countRolleById()
    {
        assertNotNull(testObject);

        try
        {
            RechtDAO daoRecht = dbResource.getDaoFactory().getRechtDAO();
            RechtVO recht1 = daoRecht
                            .findRechtById(RechtId.SYSTEM_DEAKTIVIEREN);
            RechtVO recht2 = daoRecht
                            .findRechtById(RechtId.SYSTEMKONFIGURATION_AENDERN);

            daoRolle.addRechtToRolle(recht1.getRechtId(),
                            testObject.getRolleId());
            daoRolle.addRechtToRolle(recht2.getRechtId(),
                            testObject.getRolleId());

            RolleVO rolle = testObject;

            PersonVO person = daoFactory.getObjectFactory().createPerson();
            person.setName("count-rolle-by-id-person-name");
            person.setVorname("count-rolle-by-id-person-vorname");
            person.setNachname("count-rolle-by-id-person-nachname");
            person = taoBV.createPerson(person);

            // Erstellen der Organisation, OrganisationsEinheit und Schleife
            OrganisationVO organisation = daoFactory.getObjectFactory()
                            .createOrganisation();
            organisation.setBeschreibung("count-rolle-by-id-organisation-beschreibung");
            organisation.setName("count-rolle-by-id-organisation-name");
            organisation.setGeloescht(false);
            organisation = taoBV.createOrganisation(organisation);

            OrganisationsEinheitVO organisationseinheit = daoFactory
                            .getObjectFactory().createOrganisationsEinheit();
            organisationseinheit
                            .setBeschreibung("count-rolle-by-id-organisationseinheit-beschreibung");
            organisationseinheit
                            .setName("count-rolle-by-id-organisationseinheit-name");
            organisationseinheit.setGeloescht(false);
            organisationseinheit.setOrganisationId(organisation
                            .getOrganisationId());
            organisationseinheit = taoBV
                            .createOrganisationseinheit(organisationseinheit);

            SchleifeVO schleife = daoFactory.getObjectFactory()
                            .createSchleife();
            schleife.setName("count-rolle-by-id-schleife-name");
            schleife.setKuerzel("krzl1");
            schleife.setOrganisationsEinheitId(organisationseinheit
                            .getOrganisationsEinheitId());
            schleife = taoBV.createSchleife(schleife);

            RechteTAO taoRechte = dbResource.getTaoFactory().getRechteTAO();
            taoRechte.addPersonInRolleToSchleife(person.getPersonId(),
                            rolle.getRolleId(), schleife.getSchleifeId());

            long result = 0;

            result = daoRolle.countRolleById(rolle.getRolleId());
            assertEquals(result, 1);

            taoRechte.addPersonInRolleToOrganisationseinheit(
                            person.getPersonId(), rolle.getRolleId(),
                            organisationseinheit.getOrganisationsEinheitId());

            result = daoRolle.countRolleById(rolle.getRolleId());
            assertEquals(result, 2);

            taoRechte.addPersonInRolleToOrganisation(person.getPersonId(),
                            rolle.getRolleId(),
                            organisation.getOrganisationId());

            result = daoRolle.countRolleById(rolle.getRolleId());
            assertEquals(result, 3);

            taoRechte.addPersonInRolleToSystem(person.getPersonId(),
                            rolle.getRolleId());

            result = daoRolle.countRolleById(rolle.getRolleId());
            assertEquals(result, 4);

        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void setRolleIdNull()
    {
        RolleVO vo = daoFactory.getObjectFactory().createRolle();
        try
        {
            vo.setRolleId(null);
            fail("Test did not catch StdException.");
        }
        catch (StdException e)
        {

        }
    }

    @Test
    public void setNameNull()
    {
        RolleVO vo = daoFactory.getObjectFactory().createRolle();
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
    public void setNameEmtpy()
    {
        RolleVO vo = daoFactory.getObjectFactory().createRolle();
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
        if (null == testObject)
        {
            createRolle();
        }

        assertNotNull(testObject);

        assertEquals(testObject.toString(), NAME);
    }
}
