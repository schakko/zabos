package de.ecw.zabos.mc35;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.mc35.MC35;
import de.ecw.zabos.mc35.MC35ManagerDaemon;
import de.ecw.zabos.service.smsin.ISmsInService;
import de.ecw.zabos.sql.resource.DBResource;
import de.ecw.zabos.sql.vo.ObjectFactory;
import de.ecw.zabos.sql.vo.SystemKonfigurationMc35VO;
import de.ecw.zabos.types.TelefonNummer;

public class MC35ManagerDaemonTest
{
    class MC35ManagerDaemonMock extends MC35ManagerDaemon
    {
        public MC35ManagerDaemonMock(DBResource dbResource,
                        ISmsInService smsInService)
        {
            super(dbResource, smsInService);
        }

        public MC35ManagerDaemonMock()
        {
            super(null, null);
        }

        public void resetNextId()
        {
            nextDeviceIdx = 0;
        }
    }

    @Test
    public void getNextDevice() throws StdException
    {
        ObjectFactory objectFactory = new ObjectFactory();

        // Kein Modem eingetragen => Unbekannte Rufnummer
        MC35ManagerDaemonMock mock = new MC35ManagerDaemonMock();
        assertNull(mock.getNextDevice());
        assertEquals(TelefonNummer.UNBEKANNT, mock.getNextDeviceRufnummer());

        // Ein Modem eingetragen (Alarm-Modem) => Unbekannte Rufnummer
        SystemKonfigurationMc35VO sysMc35 = objectFactory
                        .createSystemKonfigurationMc35();
        sysMc35.setRufnummer(new TelefonNummer("1"));
        sysMc35.setAlarmModem(true);
        MC35 mc35 = new MC35(null, sysMc35);
        mock.getMarkedAsOnlineChildDaemons().add(mc35);

        assertNull(mock.getNextDevice());
        assertEquals(TelefonNummer.UNBEKANNT, mock.getNextDeviceRufnummer());

        // Ein Modem eingetragen (kein Alarm-Modem)
        sysMc35.setAlarmModem(false);

        assertNotNull(mock.getNextDevice());
        assertEquals(mc35, mock.getNextDevice());
        assertEquals(new TelefonNummer("1"), mock.getNextDeviceRufnummer());

        // Zwei Modems eingetragen, beides Alarm => Unbekannt
        sysMc35.setAlarmModem(true);
        SystemKonfigurationMc35VO sysMc35Second = objectFactory
                        .createSystemKonfigurationMc35();
        sysMc35Second.setRufnummer(new TelefonNummer("2"));
        sysMc35Second.setAlarmModem(true);
        MC35 mc35Second = new MC35(null, sysMc35Second);
        mock.getMarkedAsOnlineChildDaemons().add(mc35Second);

        assertNull(mock.getNextDevice());
        assertEquals(TelefonNummer.UNBEKANNT, mock.getNextDeviceRufnummer());

        // Zweites Modem *kein* Alarm => Wird immer als Absender benutzt
        sysMc35Second.setAlarmModem(false);

        // getNextDeviceRufnummer() ruft getNextDevice() auf, sind also beide
        // von einander abhängig
        assertEquals(mc35Second, mock.getNextDevice());
        assertEquals(new TelefonNummer("2"), mock.getNextDeviceRufnummer());
        assertEquals(new TelefonNummer("2"), mock.getNextDeviceRufnummer());

        // Beide Modems *keine* Alarm-Modems
        sysMc35.setAlarmModem(false);
        mock.resetNextId();

        assertEquals(mc35, mock.getNextDevice());
        assertEquals(mc35Second, mock.getNextDevice());
        assertEquals(mc35, mock.getNextDevice());
        assertEquals(new TelefonNummer("2"), mock.getNextDeviceRufnummer());
        assertEquals(new TelefonNummer("1"), mock.getNextDeviceRufnummer());
        assertEquals(new TelefonNummer("2"), mock.getNextDeviceRufnummer());

    }
}
