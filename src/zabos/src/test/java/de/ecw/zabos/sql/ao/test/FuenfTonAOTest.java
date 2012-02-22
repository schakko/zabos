package de.ecw.zabos.sql.ao.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.sql.dao.FuenfTonDAO;
import de.ecw.zabos.sql.tao.FuenfTonTAO;
import de.ecw.zabos.sql.vo.FuenfTonVO;
import de.ecw.zabos.test.ZabosTestAdapter;
import de.ecw.zabos.types.UnixTime;

public class FuenfTonAOTest extends ZabosTestAdapter
{
    private final static String FOLGE = "folge";

    private static UnixTime ZEITPUNKT = UnixTime.now();

    private static FuenfTonVO testObject = null;

    private static FuenfTonDAO daoFuenfTon;

    private static boolean isInitialized = false;

    private static FuenfTonTAO taoFuenfTon;

    private void assertObject(FuenfTonVO r)
    {
        assertNotNull(r);
        assertEquals(r.getFolge(), FOLGE);
        assertEquals(r.getZeitpunkt().getTimeStamp(), ZEITPUNKT.getTimeStamp());
    }

    @Before
    public synchronized void initEnvironmentSub()
    {
        if (!isInitialized)
        {
            taoFuenfTon = dbResource.getTaoFactory().getFuenfTonTAO();
            daoFuenfTon = dbResource.getDaoFactory().getFuenfTonDAO();
        }
    }

    @Test
    public void createFuenfTon()
    {
        FuenfTonVO vo = daoFactory.getObjectFactory().createFuenfTon();

        try
        {
            vo.setFolge(FOLGE);
            vo.setZeitpunkt(ZEITPUNKT);
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }

        FuenfTonVO r = taoFuenfTon.createFuenfTon(vo);

        assertObject(r);

        testObject = r;
    }

    @Test
    public void findFuenfTonById()
    {
        assertNotNull(testObject);

        try
        {
            FuenfTonVO vo = daoFuenfTon.findFuenfTonById(testObject
                            .getFuenfTonId());
            assertObject(vo);
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void setFuenfTonIdNull()
    {
        FuenfTonVO vo = daoFactory.getObjectFactory().createFuenfTon();
        try
        {
            vo.setFuenfTonId(null);
            fail("Test did not catch StdException");
        }
        catch (StdException e)
        {

        }
    }

    @Test
    public void setFolgeNull()
    {
        FuenfTonVO vo = daoFactory.getObjectFactory().createFuenfTon();
        try
        {
            vo.setFolge(null);
            fail("Test did not catch StdException");
        }
        catch (StdException e)
        {

        }
    }

    @Test
    public void setZeitpunktNull()
    {
        FuenfTonVO vo = daoFactory.getObjectFactory().createFuenfTon();
        try
        {
            vo.setZeitpunkt(null);
            fail("Test did not catch StdException");
        }
        catch (StdException e)
        {

        }
    }
}
