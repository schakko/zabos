package de.ecw.zabos.sql.ao.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.sql.dao.RueckmeldungStatusAliasDAO;
import de.ecw.zabos.sql.tao.RueckmeldungStatusAliasTAO;
import de.ecw.zabos.sql.vo.RueckmeldungStatusAliasVO;
import de.ecw.zabos.sql.vo.SchleifeVO;
import de.ecw.zabos.test.ZabosTestAdapter;
import de.ecw.zabos.types.id.RueckmeldungStatusId;

public class RueckmeldungStatusAliasAOTest extends ZabosTestAdapter
{
    private final static String ALIAS = "alias";

    private static RueckmeldungStatusId RUECKMELDUNG_STATUS_ID = new RueckmeldungStatusId(
                    RueckmeldungStatusId.STATUS_JA);

    private static RueckmeldungStatusAliasVO testObject = null;

    private static RueckmeldungStatusAliasDAO daoRueckmeldungStatusAlias;

    private static boolean isInitialized = false;

    private static RueckmeldungStatusAliasTAO taoRueckmeldungStatusAlias;

    private void assertObject(RueckmeldungStatusAliasVO r)
    {
        assertNotNull(r);
        assertEquals(r.getAlias(), ALIAS);
        assertEquals(r.getRueckmeldungStatusId(), RUECKMELDUNG_STATUS_ID);
    }

    @Before
    public synchronized void initEnvironmentSub()
    {
        if (!isInitialized)
        {
            daoRueckmeldungStatusAlias = dbResource.getDaoFactory()
                            .getRueckmeldungStatusAliasDAO();
            taoRueckmeldungStatusAlias = dbResource.getTaoFactory()
                            .getRueckmeldungStatusAliasTAO();

            isInitialized = true;
        }
    }

    @Test
    public void createRueckmeldungStatusAlias()
    {
        RueckmeldungStatusAliasVO vo = daoFactory.getObjectFactory()
                        .createRueckmeldungStatusAlias();
        try
        {
            vo.setAlias(ALIAS);
            vo.setRueckmeldungStatusId(RUECKMELDUNG_STATUS_ID);
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }

        try
        {
            RueckmeldungStatusAliasVO[] rueckmeldungen = daoRueckmeldungStatusAlias
                            .findByRueckmeldungStatusAlias(ALIAS);
            assertNotNull(rueckmeldungen);
            assertEquals(0, rueckmeldungen.length);
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }

        RueckmeldungStatusAliasVO r = taoRueckmeldungStatusAlias
                        .createRueckmeldungStatusAlias(vo);

        assertObject(r);

        testObject = r;
    }

    @Test
    public void updateRueckmeldungStatusAlias()
    {
        assertNotNull(testObject);

        try
        {
            String aliasNeu = "alias-neu";
            RueckmeldungStatusId rueckmeldungStatusIdNeu = new RueckmeldungStatusId(
                            RueckmeldungStatusId.STATUS_NEIN);

            testObject.setAlias(aliasNeu);
            testObject.setRueckmeldungStatusId(rueckmeldungStatusIdNeu);

            RueckmeldungStatusAliasVO updated = taoRueckmeldungStatusAlias
                            .updateRueckmeldungStatusAlias(testObject);

            assertNotNull(updated);
            assertEquals(updated.getAlias(), aliasNeu);
            assertEquals(updated.getRueckmeldungStatusId(),
                            rueckmeldungStatusIdNeu);

            testObject.setAlias(ALIAS);
            testObject.setRueckmeldungStatusId(RUECKMELDUNG_STATUS_ID);
            testObject = taoRueckmeldungStatusAlias
                            .updateRueckmeldungStatusAlias(testObject);
            assertObject(testObject);
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void deleteRueckmeldungStatusAlias()
    {
        try
        {
            RueckmeldungStatusAliasVO deleteTest = daoFactory
                            .getObjectFactory().createRueckmeldungStatusAlias();
            deleteTest.setAlias("alias-zu-loeschen");
            deleteTest.setRueckmeldungStatusId(new RueckmeldungStatusId(
                            RueckmeldungStatusId.STATUS_SPAETER));

            deleteTest = taoRueckmeldungStatusAlias
                            .createRueckmeldungStatusAlias(deleteTest);
            assertNotNull(deleteTest);

            boolean result = taoRueckmeldungStatusAlias
                            .deleteRueckmeldungStatusAlias(deleteTest
                                            .getRueckmeldungStatusAliasId());
            assertTrue(result);

            RueckmeldungStatusAliasVO[] objects = daoRueckmeldungStatusAlias
                            .findAll();
            assertNotNull(objects);
            assertTrue(objects.length > 0);

            for (int i = 0, m = objects.length; i < m; i++)
            {
                if (objects[i].getRueckmeldungStatusAliasId().equals(
                                deleteTest.getRueckmeldungStatusAliasId()))
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
    public void findByRueckmeldungStatusAliasId()
    {
        assertNotNull(testObject);

        try
        {
            RueckmeldungStatusAliasVO vo = daoRueckmeldungStatusAlias
                            .findByRueckmeldungStatusAliasId(testObject
                                            .getRueckmeldungStatusAliasId());
            assertObject(vo);
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void findByRueckmeldungStatusAlias()
    {
        assertNotNull(testObject);

        try
        {
            boolean b = false;

            RueckmeldungStatusAliasVO[] objects = daoRueckmeldungStatusAlias
                            .findByRueckmeldungStatusAlias(ALIAS);
            assertNotNull(objects);
            assertTrue(objects.length > 0);

            for (int i = 0, m = objects.length; i < m; i++)
            {
                if (objects[i].getRueckmeldungStatusAliasId().equals(
                                testObject.getRueckmeldungStatusAliasId()))
                {
                    assertObject(objects[i]);
                    b = true;
                }
            }

            if (false == b)
            {
                fail("Der Datensatz wurde nicht gefunden");
            }
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void findByRueckmeldungStatusId()
    {
        assertNotNull(testObject);

        try
        {
            boolean b = false;

            RueckmeldungStatusAliasVO[] objects = daoRueckmeldungStatusAlias
                            .findByRueckmeldungStatusId(testObject
                                            .getRueckmeldungStatusId());
            assertNotNull(objects);
            assertTrue(objects.length > 0);

            for (int i = 0, m = objects.length; i < m; i++)
            {
                if (objects[i].getRueckmeldungStatusAliasId().equals(
                                testObject.getRueckmeldungStatusAliasId()))
                {
                    assertObject(objects[i]);
                    b = true;
                }
            }

            if (false == b)
            {
                fail("Der Datensatz wurde nicht gefunden");
            }
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void setRueckmeldungStatusAliasIdNull()
    {
        RueckmeldungStatusAliasVO vo = daoFactory.getObjectFactory()
                        .createRueckmeldungStatusAlias();
        try
        {
            vo.setRueckmeldungStatusAliasId(null);
            fail("Test did not catch StdException.");
        }
        catch (StdException e)
        {

        }
    }

    @Test
    public void setRueckmeldungStatusIdNull()
    {
        RueckmeldungStatusAliasVO vo = daoFactory.getObjectFactory()
                        .createRueckmeldungStatusAlias();
        try
        {
            vo.setRueckmeldungStatusId(null);
            fail("Test did not catch StdException.");
        }
        catch (StdException e)
        {

        }
    }

    @Test
    public void setAliasNull()
    {
        RueckmeldungStatusAliasVO vo = daoFactory.getObjectFactory()
                        .createRueckmeldungStatusAlias();
        try
        {
            vo.setAlias(null);
            fail("Test did not catch StdException.");
        }
        catch (StdException e)
        {

        }
    }

    @Test
    public void setAliasEmpty()
    {
        RueckmeldungStatusAliasVO vo = daoFactory.getObjectFactory()
                        .createRueckmeldungStatusAlias();
        try
        {
            vo.setAlias("");
            fail("Test did not catch StdException.");
        }
        catch (StdException e)
        {

        }
    }

    @Test
    public void setOrganisationsEinheitIdNull()
    {
        SchleifeVO vo = daoFactory.getObjectFactory().createSchleife();
        try
        {
            vo.setOrganisationsEinheitId(null);
            fail("Test did not catch StdException.");
        }
        catch (StdException e)
        {

        }

    }
}
