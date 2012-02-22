package de.ecw.zabos.sql.ao.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.sql.dao.SystemKonfigurationDAO;
import de.ecw.zabos.sql.tao.SystemKonfigurationTAO;
import de.ecw.zabos.sql.vo.SystemKonfigurationMc35VO;
import de.ecw.zabos.test.ZabosTestAdapter;
import de.ecw.zabos.types.TelefonNummer;

public class SystemKonfigurationMc35AOTest extends ZabosTestAdapter
{
    private final static int COM_PORT = 4;

    private static TelefonNummer RUFNUMMER = new TelefonNummer("01747642078");

    private final static String PIN1 = "pin";

    private final static boolean ALARM_MODEM = true;

    private static boolean isInitialized = false;

    private static SystemKonfigurationMc35VO testObject = null;

    private static SystemKonfigurationDAO daoSystemKonfigurationMc35;

    private static SystemKonfigurationTAO taoSystemkonfigurationMc35;

    private void assertObject(SystemKonfigurationMc35VO r)
    {
        assertNotNull(r);
        assertEquals(r.getComPort(), COM_PORT);
        assertEquals(r.getRufnummer(), RUFNUMMER);
        assertEquals(r.getPin1(), PIN1);
        assertEquals(r.getAlarmModem(), ALARM_MODEM);
    }

    @Before
    public synchronized void initEnvironmentSub()
    {
        if (!isInitialized)
        {
            taoSystemkonfigurationMc35 = dbResource.getTaoFactory()
                            .getSystemKonfigurationTAO();
            daoSystemKonfigurationMc35 = dbResource.getDaoFactory()
                            .getSystemKonfigurationDAO();
            isInitialized = true;
        }
    }

    @Test
    public void createSystemKonfigurationMc35()
    {
        if (null == testObject)
        {
            SystemKonfigurationMc35VO vo = daoFactory.getObjectFactory()
                            .createSystemKonfigurationMc35();

            try
            {
                vo.setComPort(COM_PORT);
                vo.setRufnummer(RUFNUMMER);
                vo.setPin1(PIN1);
                vo.setAlarmModem(ALARM_MODEM);
            }
            catch (StdException e)
            {
                fail(e.getMessage());
            }

            SystemKonfigurationMc35VO r = taoSystemkonfigurationMc35
                            .createSystemKonfigurationMc35(vo);

            assertObject(r);

            testObject = r;
        }
    }

    @Test
    public void updateSystemKonfigurationMc35()
    {
        assertNotNull(testObject);

        try
        {
            int comPortNeu = 2;
            TelefonNummer rufnummerNeu = new TelefonNummer("01796537797");
            String pin1Neu = "pin2";
            boolean alarmModemNeu = !ALARM_MODEM;

            testObject.setComPort(comPortNeu);
            testObject.setRufnummer(rufnummerNeu);
            testObject.setPin1(pin1Neu);
            testObject.setAlarmModem(alarmModemNeu);

            SystemKonfigurationMc35VO updated = taoSystemkonfigurationMc35
                            .updateSystemKonfigurationMc35(testObject);
            assertNotNull(updated);
            assertEquals(updated.getComPort(), comPortNeu);
            assertEquals(updated.getRufnummer(), rufnummerNeu);
            assertEquals(updated.getPin1(), pin1Neu);
            assertEquals(updated.getAlarmModem(), alarmModemNeu);

            testObject.setComPort(COM_PORT);
            testObject.setRufnummer(RUFNUMMER);
            testObject.setPin1(PIN1);
            testObject.setAlarmModem(ALARM_MODEM);

            testObject = taoSystemkonfigurationMc35
                            .updateSystemKonfigurationMc35(testObject);
            assertObject(testObject);

        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void deleteSystemKonfigurationMc35()
    {
        try
        {
            SystemKonfigurationMc35VO deleteTest = daoFactory
                            .getObjectFactory().createSystemKonfigurationMc35();
            deleteTest.setComPort(0);
            deleteTest.setRufnummer(new TelefonNummer("01796537797"));
            deleteTest.setPin1("pin3");
            deleteTest.setAlarmModem(true);

            deleteTest = taoSystemkonfigurationMc35
                            .createSystemKonfigurationMc35(deleteTest);
            assertNotNull(deleteTest);

            taoSystemkonfigurationMc35
                            .deleteSystemKonfigurationMc35(deleteTest);

            SystemKonfigurationMc35VO[] objects = daoSystemKonfigurationMc35
                            .findAllMC35();
            assertNotNull(objects);
            assertTrue(objects.length > 0);

            for (int i = 0, m = objects.length; i < m; i++)
            {
                if (objects[i].getBaseId().equals(deleteTest.getBaseId()))
                {
                    fail("Der geloeschte Datensatz wurde zurueckgeliefert");
                }
            }
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void findKonfigurationMc35ById()
    {
        assertNotNull(testObject);

        try
        {
            SystemKonfigurationMc35VO vo = daoSystemKonfigurationMc35
                            .findKonfigurationMc35ById(testObject.getBaseId());
            assertObject(vo);
        }
        catch (Exception e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void setBaseIdNull()
    {
        SystemKonfigurationMc35VO vo = daoFactory.getObjectFactory()
                        .createSystemKonfigurationMc35();
        try
        {
            vo.setBaseId(null);
            fail("Test did not catch StdException.");
        }
        catch (StdException e)
        {

        }
    }

    @Test
    public void setRufnummerNull()
    {
        SystemKonfigurationMc35VO vo = daoFactory.getObjectFactory()
                        .createSystemKonfigurationMc35();
        try
        {
            vo.setRufnummer(null);
            fail("Test did not catch StdException.");
        }
        catch (StdException e)
        {

        }
    }

    @Test
    public void setPin1Null()
    {
        SystemKonfigurationMc35VO vo = daoFactory.getObjectFactory()
                        .createSystemKonfigurationMc35();
        try
        {
            vo.setPin1(null);
            fail("Test did not catch StdException.");
        }
        catch (StdException e)
        {

        }
    }

    @Test
    public void setPin1Empty()
    {
        SystemKonfigurationMc35VO vo = daoFactory.getObjectFactory()
                        .createSystemKonfigurationMc35();
        try
        {
            vo.setPin1("");
            fail("Test did not catch StdException.");
        }
        catch (StdException e)
        {

        }
    }

    @Test
    public void testToString()
    {
        if (null == testObject)
        {
            createSystemKonfigurationMc35();
        }

        assertNotNull(testObject);

        assertEquals(testObject.toString(), "" + RUFNUMMER);
    }
}
