package de.ecw.zabos.sql.ao.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.sql.dao.BereichInSchleifeDAO;
import de.ecw.zabos.sql.vo.BereichInSchleifeVO;
import de.ecw.zabos.sql.vo.BereichVO;
import de.ecw.zabos.sql.vo.FunktionstraegerVO;
import de.ecw.zabos.sql.vo.OrganisationVO;
import de.ecw.zabos.sql.vo.OrganisationsEinheitVO;
import de.ecw.zabos.sql.vo.SchleifeVO;
import de.ecw.zabos.test.ZabosTestAdapter;

public class BereichInSchleifeAOTest extends ZabosTestAdapter
{

    private void assertObject(BereichInSchleifeVO bis)
    {
        assertNotNull(bis);
        assertNotNull(bis.getBereichInSchleifeId());
        assertNotNull(bis.getBereichId());
        assertNotNull(bis.getFunktionstraegerId());
        assertEquals(SOLLSTAERKE, bis.getSollstaerke());
        assertTrue((bis.getBereichId().getLongValue() > 0));
    }

    private void assertObject(BereichInSchleifeVO[] bis)
    {
        assertNotNull(bis);
        assertEquals(1, bis.length);

        assertObject(bis[0]);
    }

    private static BereichInSchleifeVO testObject;

    private static BereichInSchleifeDAO daoBereichInSchleife;

    public final int SOLLSTAERKE = 100;

    private static boolean isInitialized = false;

    static OrganisationVO o;

    static OrganisationsEinheitVO oe;

    static SchleifeVO s;

    static SchleifeVO s2;

    static BereichVO b;

    static BereichVO b2;

    static FunktionstraegerVO f;

    static FunktionstraegerVO f2;

    @Before
    public synchronized void initEnvironmentSub()
    {
        if (!isInitialized)
        {
            isInitialized = true;

            daoBereichInSchleife = dbResource.getDaoFactory()
                            .getBereichInSchleifeDAO();

            try
            {
                testObject = daoFactory.getObjectFactory()
                                .createBereichInSchleife();
                o = daoFactory.getObjectFactory().createOrganisation();
                oe = daoFactory.getObjectFactory().createOrganisationsEinheit();
                s = daoFactory.getObjectFactory().createSchleife();
                s2 = daoFactory.getObjectFactory().createSchleife();
                b = daoFactory.getObjectFactory().createBereich();
                b2 = daoFactory.getObjectFactory().createBereich();
                f = daoFactory.getObjectFactory().createFunktionstraeger();
                f2 = daoFactory.getObjectFactory().createFunktionstraeger();

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

                b2.setName("B2");
                b2.setBeschreibung("B2");
                b2 = taoBV.createBereich(b2);
                assertNotNull(b2);

                f.setBeschreibung("F");
                f.setKuerzel("F");
                f = taoBV.createFunktionstraeger(f);
                assertNotNull(f);

                f2.setBeschreibung("F2");
                f2.setKuerzel("F2");
                f2 = taoBV.createFunktionstraeger(f2);
                assertNotNull(f2);
            }
            catch (StdException e)
            {
                fail(e.getMessage());
            }
        }
    }

    @Test
    public void createBereichInSchleife()
    {
        testObject.setFunktionstraegerId(f.getFunktionstraegerId());
        testObject.setBereichId(b.getBereichId());
        testObject.setSchleifeId(s.getSchleifeId());
        testObject.setSollstaerke(SOLLSTAERKE);

        testObject = taoBV.createBereichInSchleife(testObject);

        assertNotNull(testObject);
        assertObject(testObject);

        assertEquals(b.getBereichId(), testObject.getBereichId());
        assertEquals(s.getSchleifeId(), testObject.getSchleifeId());
        assertEquals(f.getFunktionstraegerId(),
                        testObject.getFunktionstraegerId());
        assertEquals(SOLLSTAERKE, testObject.getSollstaerke());
    }

    @Test
    public void findAll()
    {
        try
        {
            BereichInSchleifeVO[] bis = daoBereichInSchleife.findAll();
            assertObject(bis);
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void deleteBereichInSchleife()
    {
        try
        {
            BereichInSchleifeVO bisNeu = dbResource.getObjectFactory()
                            .createBereichInSchleife();
            bisNeu.setFunktionstraegerId(f.getFunktionstraegerId());
            bisNeu.setBereichId(b2.getBereichId());
            bisNeu.setSchleifeId(s.getSchleifeId());
            bisNeu.setSollstaerke(75);
            bisNeu = taoBV.createBereichInSchleife(bisNeu);

            BereichInSchleifeVO[] bis = daoBereichInSchleife.findAll();
            assertNotNull(bis);
            assertEquals(2, bis.length);

            taoBV.deleteBereichInSchleife(bisNeu.getBereichInSchleifeId());

            bis = daoBereichInSchleife.findAll();
            assertNotNull(bis);
            assertEquals(1, bis.length);
            assertEquals(testObject.getBereichInSchleifeId(),
                            bis[0].getBereichInSchleifeId());
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void findBereichInSchleifeById()
    {
        try
        {
            BereichInSchleifeVO bis = daoBereichInSchleife
                            .findBereichInSchleifeById(testObject
                                            .getBereichInSchleifeId());
            assertNotNull(bis);
            assertEquals(testObject.getBereichInSchleifeId(),
                            bis.getBereichInSchleifeId());
            assertObject(bis);
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void findAktiveBereicheInSchleifeInAlarmByAlarmId()
    {
        // TODO gehört in den AlarmService-Test
    }

    @Test
    public void findBereicheInSchleifeInAlarmByAlarmId()
    {
        // TODO gehört in den AlarmService-Test
    }

    @Test
    public void findBereicheInSchleifeByFunktionstraegerId()
    {
        try
        {
            BereichInSchleifeVO[] bis = daoBereichInSchleife
                            .findBereicheInSchleifeByFunktionstraegerId(f
                                            .getFunktionstraegerId());
            assertObject(bis);
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void findBereicheInSchleifeByBereichId()
    {
        try
        {
            BereichInSchleifeVO[] bis = daoBereichInSchleife
                            .findBereicheInSchleifeByBereichId(b.getBereichId());
            assertObject(bis);
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void findBereicheInSchleifeBySchleifeId()
    {
        try
        {
            BereichInSchleifeVO[] bis = daoBereichInSchleife
                            .findBereicheInSchleifeBySchleifeId(s
                                            .getSchleifeId());
            assertObject(bis);

            bis = daoBereichInSchleife.findBereicheInSchleifeBySchleifeId(s2
                            .getSchleifeId());
            assertNotNull(bis);
            assertEquals(0, bis.length);
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void updateBereichInSchleife()
    {
        testObject.setSollstaerke(50);
        testObject = taoBV.updateBereichInSchleife(testObject);

        assertNotNull(testObject);
        assertEquals(50, testObject.getSollstaerke());

        testObject.setSollstaerke(SOLLSTAERKE);
        testObject = taoBV.updateBereichInSchleife(testObject);
    }

    @Test
    public void setBereichInSchleifeIdNull()
    {
        BereichInSchleifeVO vo = dbResource.getObjectFactory()
                        .createBereichInSchleife();
        try
        {
            vo.setBereichInSchleifeId(null);
            fail("Test did not catch StdException.");
        }
        catch (StdException e)
        {

        }
    }

    @Test
    public void findVorgaenger()
    {
        // Es exisitiert kein Vorgänger
        try
        {
            assertNull(daoBereichInSchleife.findVorgaenger(testObject));
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }

        // Folgeschleife definieren
        s.setFolgeschleifeId(s2.getSchleifeId());
        s = taoBV.updateSchleife(s);

        BereichInSchleifeVO nonExistent = dbResource.getObjectFactory()
                        .createBereichInSchleife();
        nonExistent.setBereichId(b.getBereichId());
        nonExistent.setFunktionstraegerId(f2.getFunktionstraegerId());
        nonExistent.setSchleifeId(s2.getSchleifeId());
        nonExistent = taoBV.createBereichInSchleife(nonExistent);

        // Es exisitiert auch für dieses Objekt kein Vorgänger
        try
        {
            assertNull(daoBereichInSchleife.findVorgaenger(nonExistent));
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }

        BereichInSchleifeVO nachfolger = dbResource.getObjectFactory()
                        .createBereichInSchleife();
        nachfolger.setSchleifeId(s2.getSchleifeId());
        nachfolger.setFunktionstraegerId(f.getFunktionstraegerId());
        nachfolger.setBereichId(b.getBereichId());
        nachfolger.setSollstaerke(5);
        nachfolger = taoBV.createBereichInSchleife(nachfolger);

        try
        {
            BereichInSchleifeVO r = daoBereichInSchleife
                            .findVorgaenger(nachfolger);
            assertNotNull(r);
            assertEquals(testObject.getBereichInSchleifeId(),
                            r.getBereichInSchleifeId());
            assertEquals(testObject.getSollstaerke(), r.getSollstaerke());

            // Es existiert kein Vorgänger
            assertNull(daoBereichInSchleife.findVorgaenger(r));
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }
    }

}
