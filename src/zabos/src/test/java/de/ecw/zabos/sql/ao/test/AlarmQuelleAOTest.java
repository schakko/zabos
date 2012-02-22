package de.ecw.zabos.sql.ao.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.sql.dao.AlarmQuelleDAO;
import de.ecw.zabos.sql.vo.AlarmQuelleVO;
import de.ecw.zabos.test.ZabosTestAdapter;
import de.ecw.zabos.types.id.AlarmQuelleId;

public class AlarmQuelleAOTest extends ZabosTestAdapter
{
    private final static String NAME = "webclient";

    private static AlarmQuelleVO testObject = null;

    private static AlarmQuelleDAO daoAlarmQuelle;

    private static boolean isInitialized = false;

    private void assertObject(AlarmQuelleVO r)
    {
        assertNotNull(r);
        assertEquals(r.getName(), NAME);
        assertNotNull(r.getAlarmQuelleId());
        assertTrue(r.getAlarmQuelleId().getLongValue() > 0);
    }

    @Before
    public synchronized void initEnvironmentSub()
    {
        if (!isInitialized)
        {
            daoAlarmQuelle = dbResource.getDaoFactory().getAlarmQuelleDAO();
            isInitialized = true;
        }
    }

    @Test
    public void findAlarmQuelleById()
    {
        try
        {
            testObject = daoAlarmQuelle
                            .findAlarmQuelleById(AlarmQuelleId.ID_WEB);
            assertObject(testObject);
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void setAlarmQuelleIdNull()
    {
        AlarmQuelleVO vo = dbResource.getObjectFactory().createAlarmQuelle();
        try
        {
            vo.setAlarmQuelleId(null);
            fail("Test did not catch StdException.");
        }
        catch (StdException e)
        {

        }
    }

    @Test
    public void setNameNull()
    {
        AlarmQuelleVO vo = dbResource.getObjectFactory().createAlarmQuelle();
        try
        {
            vo.setName(null);
            fail("Test did not catch StdException.");
        }
        catch (StdException e)
        {

        }
    }
}
