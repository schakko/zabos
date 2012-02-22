package de.ecw.zabos.sql.ao.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.sql.dao.SmsInDAO;
import de.ecw.zabos.sql.vo.SmsInVO;
import de.ecw.zabos.test.ZabosTestAdapter;
import de.ecw.zabos.types.TelefonNummer;
import de.ecw.zabos.types.UnixTime;

public class SmsInAOTest extends ZabosTestAdapter
{
    private final static TelefonNummer RUFNUMMER = new TelefonNummer(
                    "01796537797");

    private final static TelefonNummer MODEM_RUFNUMMER = new TelefonNummer(
                    "01747642078");

    private final static String NACHRICHT = "nachricht";

    private final static UnixTime ZEITPUNKT = UnixTime.now();

    private static SmsInVO testObject = null;

    private static SmsInDAO daoSmsIn;

    private static boolean isInitialized = false;

    private void assertObject(SmsInVO r)
    {
        assertNotNull(r);
        assertEquals(r.getRufnummer(), RUFNUMMER);
        assertEquals(r.getModemRufnummer(), MODEM_RUFNUMMER);
        assertEquals(r.getNachricht(), NACHRICHT);
        assertEquals(r.getZeitpunkt().getTimeStamp(), ZEITPUNKT.getTimeStamp());
    }

    @Before
    public synchronized void initEnvironmentSub()
    {
        if (!isInitialized)
        {
            daoSmsIn = dbResource.getDaoFactory().getSmsInDAO();
        }
    }

    @Test
    public void createSmsIn()
    {
        SmsInVO vo = daoFactory.getObjectFactory().createSmsIn();
        SmsInVO r = daoFactory.getObjectFactory().createSmsIn();

        try
        {
            vo.setRufnummer(RUFNUMMER);
            vo.setModemRufnummer(MODEM_RUFNUMMER);
            vo.setNachricht(NACHRICHT);
            vo.setZeitpunkt(ZEITPUNKT);

            r = daoSmsIn.createSmsIn(vo);
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }

        assertObject(r);

        testObject = r;
    }

    @Test
    public void findSmsInById()
    {
        assertNotNull(testObject);

        try
        {
            SmsInVO vo = daoSmsIn.findSmsInById(testObject.getSmsInId());
            assertObject(vo);
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void findUngeleseneSmsIn()
    {
        SmsInVO vo = daoFactory.getObjectFactory().createSmsIn();

        try
        {
            TelefonNummer modemRufnummerNeu = new TelefonNummer("01796537797");
            TelefonNummer rufnummerNeu = new TelefonNummer("01796537797");
            String nachrichtNeu = "nachrichtNeu";
            UnixTime zeitpunktNeu = UnixTime.now();

            vo.setModemRufnummer(modemRufnummerNeu);
            vo.setRufnummer(rufnummerNeu);
            vo.setNachricht(nachrichtNeu);
            vo.setZeitpunkt(zeitpunktNeu);

            vo = daoSmsIn.createSmsIn(vo);

            assertNotNull(vo);
            assertEquals(modemRufnummerNeu, vo.getModemRufnummer());
            assertEquals(rufnummerNeu, vo.getRufnummer());
            assertEquals(nachrichtNeu, vo.getNachricht());
            assertEquals(zeitpunktNeu.getTimeStamp(), vo.getZeitpunkt()
                            .getTimeStamp());

            SmsInVO[] objects = daoSmsIn.findUngeleseneSmsIn();
            assertNotNull(objects);
            assertTrue(objects.length >= 1);

            boolean b = false;

            for (int i = 0, m = objects.length; i < m; i++)
            {
                if (objects[i].getSmsInId().equals(vo.getSmsInId()))
                {
                    assertEquals(vo.getModemRufnummer(),
                                    objects[i].getModemRufnummer());
                    assertEquals(vo.getRufnummer(), objects[i].getRufnummer());
                    assertEquals(vo.getNachricht(), objects[i].getNachricht());
                    assertEquals(vo.getZeitpunkt().getTimeStamp(), objects[i]
                                    .getZeitpunkt().getTimeStamp());
                    b = true;
                }
            }

            if (false == b)
            {
                fail("findUngeleseneSmsIn: Ungelesene SMS wurde nicht gefunden.");
            }
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void kennzeichneAlsGelesenBySmsInId()
    {
        SmsInVO vo = daoFactory.getObjectFactory().createSmsIn();

        try
        {
            TelefonNummer modemRufnummerNeu = new TelefonNummer("01796537797");
            TelefonNummer rufnummerNeu = new TelefonNummer("01796537797");
            String nachrichtNeu = "nachrichtNeu";
            UnixTime zeitpunktNeu = UnixTime.now();

            vo.setModemRufnummer(modemRufnummerNeu);
            vo.setRufnummer(rufnummerNeu);
            vo.setNachricht(nachrichtNeu);
            vo.setZeitpunkt(zeitpunktNeu);

            vo = daoSmsIn.createSmsIn(vo);

            assertNotNull(vo);
            assertEquals(modemRufnummerNeu, vo.getModemRufnummer());
            assertEquals(rufnummerNeu, vo.getRufnummer());
            assertEquals(nachrichtNeu, vo.getNachricht());
            assertEquals(zeitpunktNeu.getTimeStamp(), vo.getZeitpunkt()
                            .getTimeStamp());

            SmsInVO[] objects = daoSmsIn.findUngeleseneSmsIn();
            assertNotNull(objects);
            assertTrue(objects.length >= 1);

            daoSmsIn.kennzeichneAlsGelesenBySmsInId(vo.getSmsInId());

            objects = daoSmsIn.findUngeleseneSmsIn();
            assertNotNull(objects);
            assertTrue(objects.length >= 1);

            boolean b = false;

            for (int i = 0, m = objects.length; i < m; i++)
            {
                if (objects[i].getSmsInId().equals(vo.getSmsInId()))
                {
                    assertEquals(vo.getModemRufnummer(),
                                    objects[i].getModemRufnummer());
                    assertEquals(vo.getRufnummer(), objects[i].getRufnummer());
                    assertEquals(vo.getNachricht(), objects[i].getNachricht());
                    assertEquals(vo.getZeitpunkt().getTimeStamp(), objects[i]
                                    .getZeitpunkt().getTimeStamp());
                    b = true;
                }
            }

            if (true == b)
            {
                fail("findUngeleseneSmsIn: Es wurde eine gelesene SMS als ungelesen zurückgeliefert.");
            }
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }
    }
}
