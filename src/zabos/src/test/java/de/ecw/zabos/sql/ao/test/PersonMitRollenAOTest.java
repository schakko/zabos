package de.ecw.zabos.sql.ao.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.sql.cvo.PersonMitRollenCVO;
import de.ecw.zabos.sql.dao.PersonMitRollenDAO;
import de.ecw.zabos.sql.vo.OrganisationVO;
import de.ecw.zabos.sql.vo.OrganisationsEinheitVO;
import de.ecw.zabos.sql.vo.PersonVO;
import de.ecw.zabos.sql.vo.RechtVO;
import de.ecw.zabos.sql.vo.RolleVO;
import de.ecw.zabos.sql.vo.SchleifeVO;
import de.ecw.zabos.test.ZabosTestAdapter;
import de.ecw.zabos.types.id.RechtId;

public class PersonMitRollenAOTest extends ZabosTestAdapter
{
    private static boolean isInitialized = false;

    private static PersonVO p;

    private static OrganisationVO o;

    private static OrganisationsEinheitVO oe;

    private static SchleifeVO s;

    private static RolleVO ro;

    private static PersonMitRollenDAO daoPersonMitRollen;

    @Before
    public synchronized void initEnvironmentSub()
    {
        if (!isInitialized)
        {
            try
            {
                p = daoFactory.getObjectFactory().createPerson();
                p.setVorname("vorname");
                p.setNachname("nachname");
                p.setName("benutzername");

                HashMap<String, String> hmOptions = new HashMap<String, String>();
                hmOptions.put("KEY", "VALUE");
                p.setReportOptionen(hmOptions);

                p = taoBV.createPerson(p);

                assertTrue((p != null));

                assertTrue((p.getReportOptionen() != null));
                assertTrue((p.getReportOptionen().containsKey("KEY")));
                assertEquals(1, p.getReportOptionen().size());
                assertEquals("VALUE", p.getReportOptionen().get("KEY"));

                ro = daoFactory.getObjectFactory().createRolle();
                ro.setName("rolle");
                ro = taoBV.createRolle(ro);

                RechtVO r = daoFactory.getRechtDAO().findRechtById(
                                RechtId.SYSTEM_DEAKTIVIEREN);
                daoFactory.getRolleDAO().addRechtToRolle(r.getRechtId(),
                                ro.getRolleId());

                o = daoFactory.getObjectFactory().createOrganisation();
                o.setName("o");
                o.setBeschreibung("o");
                o = taoBV.createOrganisation(o);

                oe = daoFactory.getObjectFactory().createOrganisationsEinheit();
                oe.setName("oe");
                oe.setBeschreibung("oe");
                oe.setOrganisationId(o.getOrganisationId());
                oe = taoBV.createOrganisationseinheit(oe);

                s = daoFactory.getObjectFactory().createSchleife();
                s.setName("s");
                s.setBeschreibung("s");
                s.setKuerzel("s");
                s.setOrganisationsEinheitId(oe.getOrganisationsEinheitId());
                s = taoBV.createSchleife(s);

                daoPersonMitRollen = dbResource.getDaoFactory()
                                .getPersonMitRollenDAO();

                dbResource.getDaoFactory()
                                .getPersonDAO()
                                .addPersonInRolleToSystem(p.getPersonId(),
                                                ro.getRolleId());

                isInitialized = true;
            }
            catch (StdException e)
            {
                fail(e.getMessage());
            }
        }
    }

    @Test
    public void findPersonenMitVererbtenRollenInSystem()
    {
        try
        {
            PersonMitRollenCVO[] personMitRollen = daoPersonMitRollen
                            .findPersonenMitVererbtenRollenInSystem();

            assertNotNull(personMitRollen);
            assertTrue(personMitRollen.length >= 1);
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void findPersonenMitVererbtenRollenInOrganisation()
    {
        try
        {
            PersonMitRollenCVO[] personMitRollen = daoPersonMitRollen
                            .findPersonenMitVererbtenRollenInOrganisation(o
                                            .getOrganisationId());

            assertNotNull(personMitRollen);
            assertTrue(personMitRollen.length >= 1);
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void findPersonenMitVererbtenRollenInOrganisationsenheit()
    {
        try
        {
            PersonMitRollenCVO[] personMitRollen = daoPersonMitRollen
                            .findPersonenMitVererbtenRollenInOrganisationsenheit(
                                            o.getOrganisationId(),
                                            oe.getOrganisationsEinheitId());

            assertNotNull(personMitRollen);
            assertTrue(personMitRollen.length >= 1);
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void findPersonenMitVererbtenRollenInSchleife()
    {
        try
        {
            PersonMitRollenCVO[] personMitRollen = daoPersonMitRollen
                            .findPersonenMitVererbtenRollenInSchleife(
                                            o.getOrganisationId(),
                                            oe.getOrganisationsEinheitId(),
                                            s.getSchleifeId());

            assertNotNull(personMitRollen);
            assertTrue(personMitRollen.length >= 1);
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void findPersonenMitRollenInSystem()
    {
        try
        {
            PersonMitRollenCVO[] personMitRollen = daoPersonMitRollen
                            .findPersonenMitVererbtenRollenInSystem();

            assertNotNull(personMitRollen);
            assertTrue(personMitRollen.length >= 1);
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void findPersonenMitRollenInOrganisation()
    {
        try
        {
            PersonMitRollenCVO[] personMitRollen = daoPersonMitRollen
                            .findPersonenMitRollenInOrganisation(o
                                            .getOrganisationId());

            assertNotNull(personMitRollen);
            assertTrue(personMitRollen.length == 0);

            dbResource.getDaoFactory()
                            .getOrganisationDAO()
                            .addPersonInRolleToOrganisation(p.getPersonId(),
                                            ro.getRolleId(),
                                            o.getOrganisationId());
            personMitRollen = daoPersonMitRollen
                            .findPersonenMitRollenInOrganisation(o
                                            .getOrganisationId());

            assertNotNull(personMitRollen);
            assertTrue(personMitRollen.length >= 1);
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void findPersonenMitRollenInOrganisationseinheit()
    {
        try
        {
            PersonMitRollenCVO[] personMitRollen = daoPersonMitRollen
                            .findPersonenMitRollenInOrganisationseinheit(oe
                                            .getOrganisationsEinheitId());

            assertNotNull(personMitRollen);
            assertTrue(personMitRollen.length == 0);

            dbResource.getDaoFactory()
                            .getOrganisationsEinheitDAO()
                            .addPersonInRolleToOrganisationseinheit(
                                            p.getPersonId(), ro.getRolleId(),
                                            oe.getOrganisationsEinheitId());
            personMitRollen = daoPersonMitRollen
                            .findPersonenMitRollenInOrganisationseinheit(oe
                                            .getOrganisationsEinheitId());

            assertNotNull(personMitRollen);
            assertTrue(personMitRollen.length >= 1);
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void findPersonenMitRollenInSchleife()
    {
        try
        {
            PersonMitRollenCVO[] personMitRollen = daoPersonMitRollen
                            .findPersonenMitRollenInSchleife(s.getSchleifeId());

            assertNotNull(personMitRollen);
            assertTrue(personMitRollen.length == 0);

            dbResource.getDaoFactory()
                            .getSchleifenDAO()
                            .addPersonInRolleToSchleife(p.getPersonId(),
                                            ro.getRolleId(), s.getSchleifeId());
            personMitRollen = daoPersonMitRollen
                            .findPersonenMitRollenInSchleife(s.getSchleifeId());

            assertNotNull(personMitRollen);
            assertTrue(personMitRollen.length >= 1);
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }
    }

}
