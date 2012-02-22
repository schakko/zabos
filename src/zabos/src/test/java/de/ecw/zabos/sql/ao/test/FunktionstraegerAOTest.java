package de.ecw.zabos.sql.ao.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.sql.dao.FunktionstraegerDAO;
import de.ecw.zabos.sql.vo.BereichInSchleifeVO;
import de.ecw.zabos.sql.vo.BereichVO;
import de.ecw.zabos.sql.vo.FunktionstraegerVO;
import de.ecw.zabos.sql.vo.OrganisationVO;
import de.ecw.zabos.sql.vo.OrganisationsEinheitVO;
import de.ecw.zabos.sql.vo.PersonVO;
import de.ecw.zabos.sql.vo.SchleifeVO;
import de.ecw.zabos.test.ZabosTestAdapter;

public class FunktionstraegerAOTest extends ZabosTestAdapter
{
    public final static String BESCHREIBUNG = "beschreibung";

    public final static String KUERZEL = "name";

    public final static int SOLLSTAERKE = 100;

    private static FunktionstraegerVO testObject = null;

    private static FunktionstraegerDAO daoFunktionstraeger;

    private static boolean isInitialized = false;

    private void assertObject(FunktionstraegerVO r)
    {
        assertNotNull(r);
        assertEquals(r.getBeschreibung(), BESCHREIBUNG);
        assertEquals(r.getKuerzel(), KUERZEL);
        assertNotNull(r.getFunktionstraegerId());
        assertTrue((r.getFunktionstraegerId().getLongValue() > 0));
    }

    @Before
    public synchronized void initEnvironmentSub()
    {
        if (!isInitialized)
        {
            daoFunktionstraeger = dbResource.getDaoFactory()
                            .getFunktionstraegerDAO();
        }
    }

    @Test
    public void createFunktionstraeger()
    {
        FunktionstraegerVO vo = daoFactory.getObjectFactory()
                        .createFunktionstraeger();
        try
        {
            vo.setBeschreibung(BESCHREIBUNG);
            vo.setKuerzel(KUERZEL);
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }

        FunktionstraegerVO r = taoBV.createFunktionstraeger(vo);

        assertObject(r);

        testObject = r;
    }

    @Test
    public void findFunktionstraegerById()
    {
        try
        {
            FunktionstraegerVO r = daoFunktionstraeger
                            .findFunktionstraegerById(testObject
                                            .getFunktionstraegerId());
            assertNotNull(r);
            assertObject(r);
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void updateFunktionstraeger()
    {
        try
        {
            String kuerzelNeu = "Kürzel-Neu";
            String beschreibungNeu = "Beschreibung-Neu";
            testObject.setKuerzel(kuerzelNeu);
            testObject.setBeschreibung(beschreibungNeu);

            FunktionstraegerVO updated = taoBV
                            .updateFunktionstraeger(testObject);
            assertNotNull(updated);
            assertEquals(updated.getFunktionstraegerId().getLongValue(),
                            testObject.getFunktionstraegerId().getLongValue());
            assertEquals(updated.getKuerzel(), kuerzelNeu);
            assertEquals(beschreibungNeu, updated.getBeschreibung());
            testObject.setKuerzel(KUERZEL);
            testObject.setBeschreibung(BESCHREIBUNG);
            testObject = taoBV.updateFunktionstraeger(testObject);
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void deleteFunktionstraeger()
    {
        try
        {
            FunktionstraegerVO deleteTest = daoFactory.getObjectFactory()
                            .createFunktionstraeger();
            deleteTest.setKuerzel("zu-loeschen");
            deleteTest.setBeschreibung("zu-loeschen");
            deleteTest = taoBV.createFunktionstraeger(deleteTest);
            assertNotNull(deleteTest);

            taoBV.deleteFunktionstraeger(deleteTest.getFunktionstraegerId());
            FunktionstraegerVO[] objects = daoFunktionstraeger.findAll();
            assertNotNull(objects);
            assertEquals(1, objects.length);

            for (int i = 0, m = objects.length; i < m; i++)
            {
                if (objects[i].getFunktionstraegerId().equals(
                                deleteTest.getFunktionstraegerId()))
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
    public void findFunktionstraegerByPerson()
    {
        try
        {
            PersonVO person = daoFactory.getObjectFactory().createPerson();
            person.setNachname("Nachname");
            person.setVorname("Vorname");
            person.setName("Benutzername");
            person = taoBV.createPerson(person);

            FunktionstraegerVO r = daoFunktionstraeger
                            .findFunktionstraegerByPersonId(person
                                            .getPersonId());

            assertTrue((r == null));

            person.setFunktionstraegerId(testObject.getFunktionstraegerId());
            person = taoBV.updatePerson(person);

            r = daoFunktionstraeger.findFunktionstraegerByPersonId(person
                            .getPersonId());

            assertNotNull(r);
            assertObject(r);
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }

    }

    @Test
    public void findFunktionstraegerInSchleifeBySchleifeId()
    {
        OrganisationVO o = daoFactory.getObjectFactory().createOrganisation();
        OrganisationsEinheitVO oe = daoFactory.getObjectFactory()
                        .createOrganisationsEinheit();
        SchleifeVO s = daoFactory.getObjectFactory().createSchleife();
        SchleifeVO s2 = daoFactory.getObjectFactory().createSchleife();
        BereichVO b = daoFactory.getObjectFactory().createBereich();
        BereichInSchleifeVO bis = daoFactory.getObjectFactory()
                        .createBereichInSchleife();

        try
        {
            o.setName("O");
            o = taoBV.createOrganisation(o);
            assertNotNull(o);

            oe.setName("OE");
            oe.setOrganisationId(o.getOrganisationId());
            oe = taoBV.createOrganisationseinheit(oe);
            assertNotNull(oe);

            s.setName("S");
            s.setKuerzel("K");
            s.setOrganisationsEinheitId(oe.getOrganisationsEinheitId());
            s = taoBV.createSchleife(s);
            assertNotNull(s);

            s2.setName("S2");
            s2.setKuerzel("K2");
            s2.setOrganisationsEinheitId(oe.getOrganisationsEinheitId());
            s2 = taoBV.createSchleife(s2);
            assertNotNull(s2);

            b.setName("-K");
            b.setBeschreibung("F-B");
            b = taoBV.createBereich(b);
            assertNotNull(b);

            bis.setFunktionstraegerId(testObject.getFunktionstraegerId());
            bis.setBereichId(b.getBereichId());
            bis.setSchleifeId(s.getSchleifeId());
            bis.setSollstaerke(SOLLSTAERKE);

            bis = taoBV.createBereichInSchleife(bis);

            assertNotNull(bis);
            assertEquals(bis.getFunktionstraegerId(),
                            testObject.getFunktionstraegerId());
            assertEquals(bis.getSollstaerke(), SOLLSTAERKE);

            FunktionstraegerVO[] funktionstraeger = daoFunktionstraeger
                            .findFunktionstraegerInSchleifeBySchleifeId(s
                                            .getSchleifeId());

            assertNotNull(funktionstraeger);
            assertEquals(funktionstraeger.length, 1);

            assertObject(funktionstraeger[0]);
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }

    }

    @Test
    public void findFunktionstraegerByKuerzel()
    {
        try
        {
            FunktionstraegerVO object = daoFunktionstraeger
                            .findFunktionstraegerByKuerzel(testObject
                                            .getKuerzel());
            assertObject(object);
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void findFunktionstraegerByBeschreibung()
    {
        try
        {
            FunktionstraegerVO object = daoFunktionstraeger
                            .findFunktionstraegerByBeschreibung(testObject
                                            .getBeschreibung());
            assertObject(object);
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void setFunktionstraegerIdNull()
    {
        FunktionstraegerVO vo = daoFactory.getObjectFactory()
                        .createFunktionstraeger();
        try
        {
            vo.setFunktionstraegerId(null);
            fail("Test did not catch StdException.");
        }
        catch (StdException e)
        {

        }
    }

    @Test
    public void setKuerzelNull()
    {
        FunktionstraegerVO vo = daoFactory.getObjectFactory()
                        .createFunktionstraeger();
        try
        {
            vo.setKuerzel(null);
            fail("Test did not catch StdException.");
        }
        catch (Exception e)
        {

        }
    }

    @Test
    public void setKuerzelEmtpy()
    {
        FunktionstraegerVO vo = daoFactory.getObjectFactory()
                        .createFunktionstraeger();
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
    public void testToString()
    {
        assertNotNull(testObject);

        assertEquals(testObject.toString(), testObject.getBeschreibung() + " ("
                        + testObject.getKuerzel() + ")");
    }
}
