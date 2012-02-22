package de.ecw.zabos.sql.ao.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.sql.dao.SystemKonfigurationDAO;
import de.ecw.zabos.sql.tao.SystemKonfigurationTAO;
import de.ecw.zabos.sql.vo.SystemKonfigurationVO;
import de.ecw.zabos.test.ZabosTestAdapter;

public class SystemKonfigurationAOTest extends ZabosTestAdapter
{
    private final static long ALARM_TIMEOUT = 4;

    private final static String ALARM_MSG_HEADER = "alarm_msg_header";

    private final static String AUSLOESUNG_MSG_HEADER = "ausloesung_msg_header";

    private final static String RUECKMELDUNG_MSG_HEADER = "rueckmeldung_msg_header";

    private final static String ENTWARN_MSG_HEADER = "entwarn_msg_header";

    private final static Integer COM5TON = 4;

    private final static int REAKTIVIERUNGS_TIMEOUT = 4;

    private final static int SMSIN_TIMEOUT = 4;

    private final static int ALARMHISTORIE_LAENGE = 4;

    private static SystemKonfigurationVO testObject = null;

    private static SystemKonfigurationDAO daoSystemKonfiguration;

    private static SystemKonfigurationTAO taoSystemKonfiguration;

    private static boolean isInitialized = false;

    private void assertObject(SystemKonfigurationVO r)
    {
        assertNotNull(r);
        assertEquals(r.getAlarmTimeout(), ALARM_TIMEOUT);
        assertEquals(r.getCom5Ton(), COM5TON);
        assertEquals(r.getReaktivierungTimeout(), REAKTIVIERUNGS_TIMEOUT);
        assertEquals(r.getSmsInTimeout(), SMSIN_TIMEOUT);
        assertEquals(r.getAlarmHistorieLaenge(), ALARMHISTORIE_LAENGE);
    }

    @Before
    public synchronized void initEnvironmentSub()
    {
        if (!isInitialized)
        {
            taoSystemKonfiguration = dbResource.getTaoFactory()
                            .getSystemKonfigurationTAO();
            daoSystemKonfiguration = dbResource.getDaoFactory()
                            .getSystemKonfigurationDAO();

            isInitialized = true;
        }
    }

    @Test
    public void createSystemKonfiguration()
    {
        SystemKonfigurationVO vo = daoFactory.getObjectFactory()
                        .createSystemKonfiguration();

        try
        {
            vo.setAlarmTimeout(ALARM_TIMEOUT);
            vo.setCom5Ton(COM5TON);
            vo.setReaktivierungTimeout(REAKTIVIERUNGS_TIMEOUT);
            vo.setSmsInTimeout(SMSIN_TIMEOUT);
            vo.setAlarmHistorieLaenge(ALARMHISTORIE_LAENGE);
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void setBaseIdNull()
    {
        SystemKonfigurationVO vo = daoFactory.getObjectFactory()
                        .createSystemKonfiguration();

        try
        {
            vo.setBaseId(null);
            fail("Test did not catch StdException.");
        }
        catch (StdException e)
        {

        }
    }
}
