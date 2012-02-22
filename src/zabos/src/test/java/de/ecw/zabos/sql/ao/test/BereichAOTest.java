package de.ecw.zabos.sql.ao.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.sql.dao.BereichDAO;
import de.ecw.zabos.sql.vo.BereichInSchleifeVO;
import de.ecw.zabos.sql.vo.BereichVO;
import de.ecw.zabos.sql.vo.FunktionstraegerVO;
import de.ecw.zabos.sql.vo.OrganisationVO;
import de.ecw.zabos.sql.vo.OrganisationsEinheitVO;
import de.ecw.zabos.sql.vo.SchleifeVO;
import de.ecw.zabos.test.ZabosTestAdapter;

public class BereichAOTest extends ZabosTestAdapter
{
    public final static String BESCHREIBUNG = "beschreibung";

    public final static String NAME = "name";

    public final static int SOLLSTAERKE = 100;

    private static BereichVO testObject = null;

    private static BereichDAO daoBereich;

    private static boolean isInitialized = false;

    private void assertObject(BereichVO r)
    {
        assertNotNull(r);
        assertEquals(r.getBeschreibung(), BESCHREIBUNG);
        assertEquals(r.getName(), NAME);
        assertNotNull(r.getBereichId());
        assertTrue((r.getBereichId().getLongValue() > 0));
    }

    @Before
    public synchronized void initEnvironmentSub()
    {
        if (!isInitialized)
        {
            daoBereich = dbResource.getDaoFactory().getBereichDAO();
            isInitialized = true;
        }
    }

    @Test
    public void createBereich()
    {
        BereichVO vo = dbResource.getObjectFactory().createBereich();
        try
        {
            vo.setBeschreibung(BESCHREIBUNG);
            vo.setName(NAME);
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }

        BereichVO r = taoBV.createBereich(vo);

        assertObject(r);

        testObject = r;
    }

    @Test
    public void updateBereich()
    {
        try
        {
            String nameNeu = "Bereich-Name-Neu";
            String beschreibungNeu = "Bereich-Beschreibung-Neu";
            testObject.setName(nameNeu);
            testObject.setBeschreibung(beschreibungNeu);

            BereichVO updated = taoBV.updateBereich(testObject);
            assertNotNull(updated);
            assertEquals(updated.getBereichId().getLongValue(), testObject
                            .getBereichId().getLongValue());
            assertEquals(updated.getName(), nameNeu);
            assertEquals(beschreibungNeu, updated.getBeschreibung());
            testObject.setName(NAME);
            testObject.setBeschreibung(BESCHREIBUNG);
            testObject = taoBV.updateBereich(testObject);
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void deleteBereich()
    {
        try
        {
            BereichVO deleteTest = dbResource.getObjectFactory()
                            .createBereich();
            deleteTest.setName("Bereich-zu-loeschen");
            deleteTest.setBeschreibung("Bereich-zu-loeschen");
            deleteTest = taoBV.createBereich(deleteTest);
            assertNotNull(deleteTest);

            taoBV.deleteBereich(deleteTest.getBereichId());
            BereichVO[] objects = daoBereich.findAll();
            assertNotNull(objects);
            assertEquals(1, objects.length);

            for (int i = 0, m = objects.length; i < m; i++)
            {
                if (objects[i].getBereichId().equals(deleteTest.getBereichId()))
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
    public void findBereichById()
    {
        assertNotNull(testObject);

        try
        {
            testObject = daoBereich.findBereichById(testObject.getBereichId());

            assertObject(testObject);
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void findBereichByName()
    {
        assertNotNull(testObject);

        try
        {
            BereichVO vo = daoBereich.findBereichByName(NAME);
            assertObject(vo);
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void findBereicheInSchleifeBySchleifeIdAndFunktionstraegerId()
    {
        OrganisationVO o = dbResource.getObjectFactory().createOrganisation();
        OrganisationsEinheitVO oe = dbResource.getObjectFactory()
                        .createOrganisationsEinheit();
        SchleifeVO s = dbResource.getObjectFactory().createSchleife();
        SchleifeVO s2 = dbResource.getObjectFactory().createSchleife();
        FunktionstraegerVO f = dbResource.getObjectFactory()
                        .createFunktionstraeger();
        BereichInSchleifeVO bis = dbResource.getObjectFactory()
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

            f.setKuerzel("F-K");
            f.setBeschreibung("F-B");
            f = taoBV.createFunktionstraeger(f);
            assertNotNull(f);

            bis.setBereichId(testObject.getBereichId());
            bis.setFunktionstraegerId(f.getFunktionstraegerId());
            bis.setSchleifeId(s.getSchleifeId());
            bis.setSollstaerke(SOLLSTAERKE);

            bis = taoBV.createBereichInSchleife(bis);

            assertNotNull(bis);
            assertEquals(bis.getBereichId(), testObject.getBereichId());
            assertEquals(bis.getSollstaerke(), SOLLSTAERKE);

            BereichVO[] bereichInSchleife = daoBereich
                            .findBereicheBySchleifeIdAndFunktionstraegerId(
                                            s.getSchleifeId(),
                                            f.getFunktionstraegerId());

            assertNotNull(bereichInSchleife);
            assertEquals(bereichInSchleife.length, 1);

            assertObject(bereichInSchleife[0]);
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }

    }
}
