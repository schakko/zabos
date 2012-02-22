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
import de.ecw.zabos.sql.tao.RolleTAO;
import de.ecw.zabos.sql.vo.OrganisationVO;
import de.ecw.zabos.sql.vo.OrganisationsEinheitVO;
import de.ecw.zabos.sql.vo.PersonVO;
import de.ecw.zabos.sql.vo.RechtVO;
import de.ecw.zabos.sql.vo.RolleVO;
import de.ecw.zabos.sql.vo.SchleifeVO;
import de.ecw.zabos.test.ZabosTestAdapter;
import de.ecw.zabos.types.id.RechtId;

public class RechtAOTest extends ZabosTestAdapter
{
    private static RechtDAO daoRecht;

    private static boolean isInitialized = false;

    @Before
    public synchronized void initEnvironmentSub()
    {
        if (!isInitialized)
        {
            daoRecht = dbResource.getDaoFactory().getRechtDAO();
            isInitialized = true;
        }
    }

    @Test
    public void findRechtById()
    {
        try
        {
            RechtVO recht = daoRecht.findRechtById(RechtId.SYSTEM_DEAKTIVIEREN);
            assertNotNull(recht);
            assertEquals(recht.getRechtId(), RechtId.SYSTEM_DEAKTIVIEREN);
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void findRechteByRolleId()
    {
        RolleVO rolle = daoFactory.getObjectFactory().createRolle();

        try
        {
            RechtVO recht = daoRecht.findRechtById(RechtId.SYSTEM_DEAKTIVIEREN);

            String rolleBeschreibung = "Recht-Rolle-Test";
            String rolleName = "Recht-Rolle-Test";

            rolle.setBeschreibung(rolleBeschreibung);
            rolle.setName(rolleName);
            rolle = taoBV.createRolle(rolle);
            assertNotNull(rolle);
            assertEquals(rolle.getBeschreibung(), rolleBeschreibung);
            assertEquals(rolle.getName(), rolleName);

            RolleDAO daoRolle = dbResource.getDaoFactory().getRolleDAO();

            daoRolle.addRechtToRolle(recht.getRechtId(), rolle.getRolleId());

            RechtVO[] objects = daoRecht
                            .findRechteByRolleId(rolle.getRolleId());
            assertNotNull(objects);
            assertTrue(objects.length > 0);
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void findRechteByPersonInSystem()
    {
        try
        {

            // Erstellen einer neuen Person
            PersonVO person = daoFactory.getObjectFactory().createPerson();
            person.setName("test-find-rechte-by-person-in-system");
            person.setVorname("test-find-rechte-by-person-in-system-vorname");
            person.setNachname("test-find-rechte-by-person-in-system-nachname");
            person = taoBV.createPerson(person);
            assertNotNull(person);

            // Rechte "SYSTEM_DEAKTIVIEREN" und "SYSTEMKONFIGURATION_AENDERN"
            // aus der Stammdaten-Tabelle holen
            RechtDAO daoRecht = dbResource.getDaoFactory().getRechtDAO();
            RechtVO recht1 = daoRecht
                            .findRechtById(RechtId.SYSTEM_DEAKTIVIEREN);
            RechtVO recht2 = daoRecht
                            .findRechtById(RechtId.SYSTEMKONFIGURATION_AENDERN);

            // Neue Rolle erstellen und die beiden Rechte der Rolle zuweisen
            RolleVO rolle = daoFactory.getObjectFactory().createRolle();
            rolle.setBeschreibung("test-find-rechte-by-person-in-system-rolle-beschreibung");
            rolle.setName("test-find-rechte-by-person-in-system-rolle-name");
            rolle.setGeloescht(false);
            RolleTAO taoRolle = dbResource.getTaoFactory().getRolleTAO();
            rolle = taoRolle.createRolle(rolle);

            RolleDAO daoRolle = dbResource.getDaoFactory().getRolleDAO();
            daoRolle.addRechtToRolle(recht1.getRechtId(), rolle.getRolleId());
            daoRolle.addRechtToRolle(recht2.getRechtId(), rolle.getRolleId());

            // Der Person die Rolle im System zuweisen
            RechteTAO taoRechte = dbResource.getTaoFactory().getRechteTAO();
            taoRechte.addPersonInRolleToSystem(person.getPersonId(),
                            rolle.getRolleId());

            // Die Rechte der neuen Person im System anhand der PersonId
            // wiederfinden
            RechtVO[] objects = daoRecht.findRechteByPersonInSystem(person
                            .getPersonId());
            assertNotNull(objects);
            assertEquals(objects.length, 2);

            // Überprüfung, ob die beiden Rechte der Person zugeteilt wurden
            boolean b1 = false, b2 = false;

            for (int i = 0, m = objects.length; i < m; i++)
            {
                if (objects[i].getRechtId().getLongValue() == recht1
                                .getRechtId().getLongValue())
                {
                    b1 = true;
                }

                if (objects[i].getRechtId().getLongValue() == recht2
                                .getRechtId().getLongValue())
                {
                    b2 = true;
                }
            }

            if (!b1 || !b2)
            {
                fail("Die der Person im System zugeordneten Rechte wurden nicht gefunden.");
            }
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void findRechteByPersonInOrganisation()
    {
        try
        {

            // Erstellen einer neuen Person
            PersonVO person = daoFactory.getObjectFactory().createPerson();
            person.setName("test-find-rechte-by-person-in-organisation");
            person.setVorname("test-find-rechte-by-person-in-organisation-vorname");
            person.setNachname("test-find-rechte-by-person-in-organisation-nachname");
            person = taoBV.createPerson(person);
            assertNotNull(person);

            // Rechte "SYSTEM_DEAKTIVIEREN" und "SYSTEMKONFIGURATION_AENDERN"
            // aus der Stammdaten-Tabelle holen
            RechtDAO daoRecht = dbResource.getDaoFactory().getRechtDAO();
            RechtVO recht1 = daoRecht
                            .findRechtById(RechtId.SYSTEM_DEAKTIVIEREN);
            RechtVO recht2 = daoRecht
                            .findRechtById(RechtId.SYSTEMKONFIGURATION_AENDERN);

            // Neue Rolle erstellen und die beiden Rechte der Rolle zuweisen
            RolleVO rolle = daoFactory.getObjectFactory().createRolle();
            rolle.setBeschreibung("test-find-rechte-by-person-in-organisation-rolle-beschreibung");
            rolle.setName("test-find-rechte-by-person-in-organisation-rolle-name");
            rolle.setGeloescht(false);
            RolleTAO taoRolle = dbResource.getTaoFactory().getRolleTAO();
            rolle = taoRolle.createRolle(rolle);

            RolleDAO daoRolle = dbResource.getDaoFactory().getRolleDAO();
            daoRolle.addRechtToRolle(recht1.getRechtId(), rolle.getRolleId());
            daoRolle.addRechtToRolle(recht2.getRechtId(), rolle.getRolleId());

            // Neue Organisation erstellen
            OrganisationVO organisation = daoFactory.getObjectFactory()
                            .createOrganisation();
            organisation.setBeschreibung("test-find-rechte-by-person-in-organisation-organisation-beschreibung");
            organisation.setName("test-find-rechte-by-person-in-organisation-organisation-name");
            organisation.setGeloescht(false);
            organisation = taoBV.createOrganisation(organisation);

            // Der Person die Rolle in der Organisation zuweisen
            RechteTAO taoRechte = dbResource.getTaoFactory().getRechteTAO();
            taoRechte.addPersonInRolleToOrganisation(person.getPersonId(),
                            rolle.getRolleId(),
                            organisation.getOrganisationId());

            // Die Rechte der neuen Person in der Organisation anhand der
            // PersonId und OrganisationId wiederfinden
            RechtVO[] objects = daoRecht.findRechteByPersonInOrganisation(
                            person.getPersonId(),
                            organisation.getOrganisationId());
            assertNotNull(objects);
            assertEquals(objects.length, 2);

            // Überprüfung, ob die beiden Rechte der Person zugeteilt wurden
            boolean b1 = false, b2 = false;

            for (int i = 0, m = objects.length; i < m; i++)
            {
                if (objects[i].getRechtId().getLongValue() == recht1
                                .getRechtId().getLongValue())
                {
                    b1 = true;
                }

                if (objects[i].getRechtId().getLongValue() == recht2
                                .getRechtId().getLongValue())
                {
                    b2 = true;
                }
            }

            if (!b1 || !b2)
            {
                fail("Die der Person in der Organisation zugeordneten Rechte wurden nicht gefunden.");
            }
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void findRechteByPersonInOrganisationsEinheit()
    {
        try
        {
            // Erstellen einer neuen Person
            PersonVO person = daoFactory.getObjectFactory().createPerson();
            person.setName("test-find-rechte-by-person-in-organisationseinheit");
            person.setVorname("test-find-rechte-by-person-in-organisationseinheit-vorname");
            person.setNachname("test-find-rechte-by-person-in-organisationseinheit-nachname");
            person = taoBV.createPerson(person);
            assertNotNull(person);

            // Rechte "SYSTEM_DEAKTIVIEREN" und "SYSTEMKONFIGURATION_AENDERN"
            // aus der Stammdaten-Tabelle holen
            RechtDAO daoRecht = dbResource.getDaoFactory().getRechtDAO();
            RechtVO recht1 = daoRecht
                            .findRechtById(RechtId.SYSTEM_DEAKTIVIEREN);
            RechtVO recht2 = daoRecht
                            .findRechtById(RechtId.SYSTEMKONFIGURATION_AENDERN);

            // Neue Rolle erstellen und die beiden Rechte der Rolle zuweisen
            RolleVO rolle = daoFactory.getObjectFactory().createRolle();
            rolle.setBeschreibung("test-find-rechte-by-person-in-organisationseinheit-rolle-beschreibung");
            rolle.setName("test-find-rechte-by-person-in-organisationseinheit-rolle-name");
            rolle.setGeloescht(false);
            RolleTAO taoRolle = dbResource.getTaoFactory().getRolleTAO();
            rolle = taoRolle.createRolle(rolle);

            RolleDAO daoRolle = dbResource.getDaoFactory().getRolleDAO();
            daoRolle.addRechtToRolle(recht1.getRechtId(), rolle.getRolleId());
            daoRolle.addRechtToRolle(recht2.getRechtId(), rolle.getRolleId());

            // Neue Organisation erstellen
            OrganisationVO organisation = daoFactory.getObjectFactory()
                            .createOrganisation();
            organisation.setBeschreibung("test-find-rechte-by-person-in-organisationseinheit-organisation-beschreibung");
            organisation.setName("rechte-person-organisationseinheit-organisation-name");
            organisation.setGeloescht(false);
            organisation = taoBV.createOrganisation(organisation);

            // Neue OrganisationsEinheit erstellen und der Organisation zuweisen
            OrganisationsEinheitVO organisationseinheit = daoFactory
                            .getObjectFactory().createOrganisationsEinheit();
            organisationseinheit
                            .setBeschreibung("test-find-rechte-by-person-in-organisationseinheit-organisationseinheit-beschreibung");
            organisationseinheit
                            .setName("rechte-person-organisationseinheit-organisationseinheit-name");
            organisationseinheit.setGeloescht(false);
            organisationseinheit.setOrganisationId(organisation
                            .getOrganisationId());
            organisationseinheit = taoBV
                            .createOrganisationseinheit(organisationseinheit);

            // Der Person die Rolle in der OrganisationsEinheit zuweisen
            RechteTAO taoRechte = dbResource.getTaoFactory().getRechteTAO();
            taoRechte.addPersonInRolleToOrganisationseinheit(
                            person.getPersonId(), rolle.getRolleId(),
                            organisationseinheit.getOrganisationsEinheitId());

            // Die Rechte der neuen Person in der OrganisationsEinheit anhand
            // der PersonId und OrganisationsEinheitId wiederfinden
            RechtVO[] objects = daoRecht
                            .findRechteByPersonInOrganisationsEinheit(
                                            person.getPersonId(),
                                            organisationseinheit
                                                            .getOrganisationsEinheitId());
            assertNotNull(objects);
            assertEquals(objects.length, 2);

            // Überprüfung, ob die beiden Rechte der Person zugeteilt wurden
            boolean b1 = false, b2 = false;

            for (int i = 0, m = objects.length; i < m; i++)
            {
                if (objects[i].getRechtId().getLongValue() == recht1
                                .getRechtId().getLongValue())
                {
                    b1 = true;
                }

                if (objects[i].getRechtId().getLongValue() == recht2
                                .getRechtId().getLongValue())
                {
                    b2 = true;
                }
            }

            if (!b1 || !b2)
            {
                fail("Die der Person in der Organisationseinheit zugeordneten Rechte wurden nicht gefunden.");
            }
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void findRechteByPersonInSchleife()
    {
        try
        {
            // Erstellen einer neuen Person
            PersonVO person = daoFactory.getObjectFactory().createPerson();
            person.setName("test-find-rechte-by-person-in-schleife");
            person.setVorname("test-find-rechte-by-person-in-schleife-vorname");
            person.setNachname("test-find-rechte-by-person-in-schleife-nachname");
            person = taoBV.createPerson(person);
            assertNotNull(person);

            // Rechte "SYSTEM_DEAKTIVIEREN" und "SYSTEMKONFIGURATION_AENDERN"
            // aus der Stammdaten-Tabelle holen
            RechtDAO daoRecht = dbResource.getDaoFactory().getRechtDAO();
            RechtVO recht1 = daoRecht
                            .findRechtById(RechtId.SYSTEM_DEAKTIVIEREN);
            RechtVO recht2 = daoRecht
                            .findRechtById(RechtId.SYSTEMKONFIGURATION_AENDERN);

            // Neue Rolle erstellen und die beiden Rechte der Rolle zuweisen
            RolleVO rolle = daoFactory.getObjectFactory().createRolle();
            rolle.setBeschreibung("test-find-rechte-by-person-in-schleife-rolle-beschreibung");
            rolle.setName("test-find-rechte-by-person-in-schleife-rolle-name");
            rolle.setGeloescht(false);
            RolleTAO taoRolle = dbResource.getTaoFactory().getRolleTAO();
            rolle = taoRolle.createRolle(rolle);

            RolleDAO daoRolle = dbResource.getDaoFactory().getRolleDAO();
            daoRolle.addRechtToRolle(recht1.getRechtId(), rolle.getRolleId());
            daoRolle.addRechtToRolle(recht2.getRechtId(), rolle.getRolleId());

            // Neue Organisation erstellen
            OrganisationVO organisation = daoFactory.getObjectFactory()
                            .createOrganisation();
            organisation.setBeschreibung("test-find-rechte-by-person-in-schleife-organisation-beschreibung");
            organisation.setName("test-find-rechte-by-person-in-schleife-organisation-name");
            organisation.setGeloescht(false);
            organisation = taoBV.createOrganisation(organisation);

            // Neue OrganisationsEinheit erstellen und der Organisation zuweisen
            OrganisationsEinheitVO organisationseinheit = daoFactory
                            .getObjectFactory().createOrganisationsEinheit();
            organisationseinheit
                            .setBeschreibung("test-find-rechte-by-person-in-schleife-organisationseinheit-beschreibung");
            organisationseinheit
                            .setName("test-find-rechte-by-person-in-schleife-organisationseinheit-name");
            organisationseinheit.setGeloescht(false);
            organisationseinheit.setOrganisationId(organisation
                            .getOrganisationId());
            organisationseinheit = taoBV
                            .createOrganisationseinheit(organisationseinheit);

            // Neue Schleife erstellen und der OrganisationsEinheit zuweisen
            SchleifeVO schleife = daoFactory.getObjectFactory()
                            .createSchleife();
            schleife.setAbrechenbar(false);
            schleife.setBeschreibung("test-find-rechte-by-person-in-schleife-schleife-beschreibung");
            schleife.setDruckerKuerzel("test-find-rechte-by-person-in-schleife-schleife-druckerkuerzel");
            schleife.setFuenfton("12345");
            schleife.setGeloescht(false);
            schleife.setKuerzel("krzl1");
            schleife.setName("test-find-rechte-by-person-in-schleife-schleife-name");
            schleife.setOrganisationsEinheitId(organisationseinheit
                            .getOrganisationsEinheitId());
            schleife.setRueckmeldeintervall(15);
            schleife.setStatusreportFuenfton(false);
            schleife = taoBV.createSchleife(schleife);

            // Der Person die Rolle in der Organisation zuweisen
            RechteTAO taoRechte = dbResource.getTaoFactory().getRechteTAO();
            taoRechte.addPersonInRolleToSchleife(person.getPersonId(),
                            rolle.getRolleId(), schleife.getSchleifeId());

            // Die Rechte der neuen Person in der Organisation anhand der
            // PersonId und SchleifeId wiederfinden
            RechtVO[] objects = daoRecht.findRechteByPersonInSchleife(
                            person.getPersonId(), schleife.getSchleifeId());
            assertNotNull(objects);
            assertEquals(objects.length, 2);

            // Überprüfung, ob die beiden Rechte der Person zugeteilt wurden
            boolean b1 = false, b2 = false;

            for (int i = 0, m = objects.length; i < m; i++)
            {
                if (objects[i].getRechtId().getLongValue() == recht1
                                .getRechtId().getLongValue())
                {
                    b1 = true;
                }

                if (objects[i].getRechtId().getLongValue() == recht2
                                .getRechtId().getLongValue())
                {
                    b2 = true;
                }
            }

            if (!b1 || !b2)
            {
                fail("Die der Person in der Organisationseinheit zugeordneten Rechte wurden nicht gefunden.");
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
        try
        {
            RechtVO[] objects = daoRecht.findAll();
            assertNotNull(objects);
            assertTrue(objects.length > 0);
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void setRechtIdNull()
    {
        RechtVO vo = daoFactory.getObjectFactory().createRecht();
        try
        {
            vo.setRechtId(null);
            fail("Test did not catch StdException.");
        }
        catch (StdException e)
        {

        }
    }

    @Test
    public void setNameNull()
    {
        RechtVO vo = daoFactory.getObjectFactory().createRecht();
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
        RechtVO vo = daoFactory.getObjectFactory().createRecht();
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
    public void getName()
    {
        RechtVO vo;

        try
        {
            vo = daoRecht.findRechtById(RechtId.SYSTEM_DEAKTIVIEREN);
            assertEquals(vo.getName(), "System deaktivieren");
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }
    }
}
