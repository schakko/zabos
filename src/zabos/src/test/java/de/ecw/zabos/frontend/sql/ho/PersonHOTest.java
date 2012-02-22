package de.ecw.zabos.frontend.sql.ho;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.sql.tao.RechteTAO;
import de.ecw.zabos.sql.vo.OrganisationVO;
import de.ecw.zabos.sql.vo.OrganisationsEinheitVO;
import de.ecw.zabos.sql.vo.PersonVO;
import de.ecw.zabos.sql.vo.RolleVO;
import de.ecw.zabos.sql.vo.SchleifeVO;
import de.ecw.zabos.test.ZabosTestAdapter;
import de.ecw.zabos.types.id.RechtId;
import de.ecw.zabos.types.id.RolleId;

public class PersonHOTest extends ZabosTestAdapter
{
    private static boolean isInitialized = false;

    static PersonHO personHO = null;

    static OrganisationVO o1 = null;

    static OrganisationVO o2 = null;

    static OrganisationsEinheitVO oe1 = null;

    static OrganisationsEinheitVO oe2 = null;

    static SchleifeVO s1 = null;

    static SchleifeVO s2 = null;

    static PersonVO admin = null;

    static PersonVO personInO1 = null;

    static PersonVO personInO2 = null;

    static PersonVO personInOE1 = null;

    static PersonVO personInOE2 = null;

    static PersonVO personInS1 = null;

    static PersonVO personInS2 = null;

    static RolleVO rollePersonAendern = null;

    private static PersonVO createPerson(String _name) throws StdException
    {
        PersonVO r = daoFactory.getObjectFactory().createPerson();
        r.setName(_name);
        r.setVorname(_name);
        r.setNachname(_name);

        r = taoBV.createPerson(r);

        return r;
    }

    @Before
    public synchronized void initEnvironmentSub()
    {
        if (!isInitialized)
        {
            try
            {
                RechteTAO rechtTao = dbResource.getTaoFactory().getRechteTAO();

                personHO = new PersonHO(dbResource);

                // Neue Rolle anlegen, mit der Personen geändert werden dürfen
                rollePersonAendern = daoFactory.getObjectFactory()
                                .createRolle();
                rollePersonAendern.setName("Person aendern");
                rollePersonAendern = taoBV.createRolle(rollePersonAendern);
                RolleId rolleId = rollePersonAendern.getRolleId();

                dbResource.getTaoFactory()
                                .getRolleTAO()
                                .addRechtToRolle(RechtId.PERSON_AENDERN,
                                                rollePersonAendern.getRolleId());

                o1 = daoFactory.getObjectFactory().createOrganisation();
                o1.setName("O1");
                o1 = taoBV.createOrganisation(o1);

                o2 = daoFactory.getObjectFactory().createOrganisation();
                o2.setName("O2");
                o2 = taoBV.createOrganisation(o2);

                oe1 = daoFactory.getObjectFactory()
                                .createOrganisationsEinheit();
                oe1.setName("OE1");
                oe1.setOrganisationId(o1.getOrganisationId());
                oe1 = taoBV.createOrganisationseinheit(oe1);

                oe2 = daoFactory.getObjectFactory()
                                .createOrganisationsEinheit();
                oe2.setName("OE2");
                oe2.setOrganisationId(o2.getOrganisationId());
                oe2 = taoBV.createOrganisationseinheit(oe2);

                s1 = daoFactory.getObjectFactory().createSchleife();
                s1.setName("S1");
                s1.setKuerzel("s1");
                s1.setOrganisationsEinheitId(oe1.getOrganisationsEinheitId());
                s1 = taoBV.createSchleife(s1);

                s2 = daoFactory.getObjectFactory().createSchleife();
                s2.setName("S2");
                s2.setKuerzel("s2");
                s2.setOrganisationsEinheitId(oe2.getOrganisationsEinheitId());
                s2 = taoBV.createSchleife(s2);

                admin = createPerson("admin");
                personInO1 = createPerson("personInO1");
                personInO2 = createPerson("personInO2");
                personInOE1 = createPerson("personInOE1");
                personInOE2 = createPerson("personInOE2");
                personInS1 = createPerson("personInS1");
                personInS2 = createPerson("personInS2");

                rechtTao.addPersonInRolleToSystem(admin.getPersonId(), rolleId);
                rechtTao.addPersonInRolleToOrganisation(
                                personInO1.getPersonId(), rolleId,
                                o1.getOrganisationId());
                rechtTao.addPersonInRolleToOrganisation(
                                personInO2.getPersonId(), rolleId,
                                o2.getOrganisationId());
                rechtTao.addPersonInRolleToOrganisationseinheit(
                                personInOE1.getPersonId(), rolleId,
                                oe1.getOrganisationsEinheitId());
                rechtTao.addPersonInRolleToOrganisationseinheit(
                                personInOE2.getPersonId(), rolleId,
                                oe2.getOrganisationsEinheitId());
                rechtTao.addPersonInRolleToSchleife(personInS1.getPersonId(),
                                rolleId, s1.getSchleifeId());
                rechtTao.addPersonInRolleToSchleife(personInS2.getPersonId(),
                                rolleId, s2.getSchleifeId());
            }
            catch (StdException e)
            {
                Assert.fail(e.getMessage());
            }

            isInitialized = true;
        }
    }

    @Test
    public void kannAdministratorAlleAnderenPersonenAendern()
    {
        assertTrue(personHO.isRechtInBezugAufAnderePersonVerfuegbar(
                        admin.getPersonId(), RechtId.PERSON_AENDERN,
                        personInO1.getPersonId()));
        assertTrue(personHO.isRechtInBezugAufAnderePersonVerfuegbar(
                        admin.getPersonId(), RechtId.PERSON_AENDERN,
                        personInO2.getPersonId()));
        assertTrue(personHO.isRechtInBezugAufAnderePersonVerfuegbar(
                        admin.getPersonId(), RechtId.PERSON_AENDERN,
                        personInOE1.getPersonId()));
        assertTrue(personHO.isRechtInBezugAufAnderePersonVerfuegbar(
                        admin.getPersonId(), RechtId.PERSON_AENDERN,
                        personInOE2.getPersonId()));
        assertTrue(personHO.isRechtInBezugAufAnderePersonVerfuegbar(
                        admin.getPersonId(), RechtId.PERSON_AENDERN,
                        personInS1.getPersonId()));
        assertTrue(personHO.isRechtInBezugAufAnderePersonVerfuegbar(
                        admin.getPersonId(), RechtId.PERSON_AENDERN,
                        personInS2.getPersonId()));
    }

    @Test
    public void kannKeinePersonAdministratorAendern()
    {
        assertFalse(personHO.isRechtInBezugAufAnderePersonVerfuegbar(
                        personInO1.getPersonId(), RechtId.PERSON_AENDERN,
                        admin.getPersonId()));
        assertFalse(personHO.isRechtInBezugAufAnderePersonVerfuegbar(
                        personInO2.getPersonId(), RechtId.PERSON_AENDERN,
                        admin.getPersonId()));
        assertFalse(personHO.isRechtInBezugAufAnderePersonVerfuegbar(
                        personInOE1.getPersonId(), RechtId.PERSON_AENDERN,
                        admin.getPersonId()));
        assertFalse(personHO.isRechtInBezugAufAnderePersonVerfuegbar(
                        personInOE2.getPersonId(), RechtId.PERSON_AENDERN,
                        admin.getPersonId()));
        assertFalse(personHO.isRechtInBezugAufAnderePersonVerfuegbar(
                        personInS1.getPersonId(), RechtId.PERSON_AENDERN,
                        admin.getPersonId()));
        assertFalse(personHO.isRechtInBezugAufAnderePersonVerfuegbar(
                        personInS2.getPersonId(), RechtId.PERSON_AENDERN,
                        admin.getPersonId()));
    }

    @Test
    public void kannKeinePersonAusAndererEinheitAendern()
    {
        assertFalse(personHO.isRechtInBezugAufAnderePersonVerfuegbar(
                        personInO1.getPersonId(), RechtId.PERSON_AENDERN,
                        personInO2.getPersonId()));
        assertFalse(personHO.isRechtInBezugAufAnderePersonVerfuegbar(
                        personInO2.getPersonId(), RechtId.PERSON_AENDERN,
                        personInO1.getPersonId()));
        assertFalse(personHO.isRechtInBezugAufAnderePersonVerfuegbar(
                        personInOE1.getPersonId(), RechtId.PERSON_AENDERN,
                        personInOE2.getPersonId()));
        assertFalse(personHO.isRechtInBezugAufAnderePersonVerfuegbar(
                        personInOE2.getPersonId(), RechtId.PERSON_AENDERN,
                        personInOE1.getPersonId()));
        assertFalse(personHO.isRechtInBezugAufAnderePersonVerfuegbar(
                        personInS1.getPersonId(), RechtId.PERSON_AENDERN,
                        personInS2.getPersonId()));
        assertFalse(personHO.isRechtInBezugAufAnderePersonVerfuegbar(
                        personInS2.getPersonId(), RechtId.PERSON_AENDERN,
                        personInS1.getPersonId()));
    }

    @Test
    public void kannPersonAusUntergeordneterEinheitKeineUebergeordnetePersonAusEigenerEinheitAendern()
    {
        assertFalse(personHO.isRechtInBezugAufAnderePersonVerfuegbar(
                        personInS1.getPersonId(), RechtId.PERSON_AENDERN,
                        personInOE1.getPersonId()));
        assertFalse(personHO.isRechtInBezugAufAnderePersonVerfuegbar(
                        personInS1.getPersonId(), RechtId.PERSON_AENDERN,
                        personInO1.getPersonId()));
        assertFalse(personHO.isRechtInBezugAufAnderePersonVerfuegbar(
                        personInOE1.getPersonId(), RechtId.PERSON_AENDERN,
                        personInO1.getPersonId()));
        assertFalse(personHO.isRechtInBezugAufAnderePersonVerfuegbar(
                        personInS2.getPersonId(), RechtId.PERSON_AENDERN,
                        personInOE2.getPersonId()));
        assertFalse(personHO.isRechtInBezugAufAnderePersonVerfuegbar(
                        personInS2.getPersonId(), RechtId.PERSON_AENDERN,
                        personInO2.getPersonId()));
        assertFalse(personHO.isRechtInBezugAufAnderePersonVerfuegbar(
                        personInOE2.getPersonId(), RechtId.PERSON_AENDERN,
                        personInO2.getPersonId()));
    }

    @Test
    public void kannPersonAusUntergeordneterEinheitKeineUebergeordnetePersonAusAndererEinheitAendern()
    {
        assertFalse(personHO.isRechtInBezugAufAnderePersonVerfuegbar(
                        personInS1.getPersonId(), RechtId.PERSON_AENDERN,
                        personInOE2.getPersonId()));
        assertFalse(personHO.isRechtInBezugAufAnderePersonVerfuegbar(
                        personInS1.getPersonId(), RechtId.PERSON_AENDERN,
                        personInO2.getPersonId()));
        assertFalse(personHO.isRechtInBezugAufAnderePersonVerfuegbar(
                        personInOE1.getPersonId(), RechtId.PERSON_AENDERN,
                        personInO2.getPersonId()));
        assertFalse(personHO.isRechtInBezugAufAnderePersonVerfuegbar(
                        personInS2.getPersonId(), RechtId.PERSON_AENDERN,
                        personInOE1.getPersonId()));
        assertFalse(personHO.isRechtInBezugAufAnderePersonVerfuegbar(
                        personInS2.getPersonId(), RechtId.PERSON_AENDERN,
                        personInO1.getPersonId()));
        assertFalse(personHO.isRechtInBezugAufAnderePersonVerfuegbar(
                        personInOE2.getPersonId(), RechtId.PERSON_AENDERN,
                        personInO1.getPersonId()));
    }
}
