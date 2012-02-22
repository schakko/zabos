package de.ecw.zabos.service.alarm.ext.interceptors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.ecw.zabos.report.IReportListener;
import de.ecw.zabos.report.mock.ReportCreationServiceMock;
import de.ecw.zabos.sql.vo.AlarmVO;
import de.ecw.zabos.sql.vo.SchleifeVO;
import de.ecw.zabos.test.ZabosTestAdapter;

public class ReportPrintingInterceptorTest extends ZabosTestAdapter
{
    private static boolean isInitialized = false;

    @Before
    public synchronized void initEnvironmentSub()
    {
        if (!isInitialized)
        {
        }
    }

    @Test
    public void createAfterReportCreatedListeners()
    {
        ReportPrintingInterceptor test = new ReportPrintingInterceptor(
                        new ReportCreationServiceMock(dbResource), dbResource);
        AlarmVO alarmVO = daoFactory.getObjectFactory().createAlarm();

        List<IReportListener> r = null;

        r = test.createAfterReportCreatedListeners(null, null);
        assertTrue((r != null));
        assertEquals(0, r.size());

        test.setUseAlwayDefaultPrinter(true);
        r = test.createAfterReportCreatedListeners(alarmVO, null);
        assertTrue((r != null));
        assertEquals(1, r.size());

        // Darf nur eins sein, da in der Schleife kein Drucker angegeben ist
        r = test.createAfterReportCreatedListeners(alarmVO, new SchleifeVO[]
        { daoFactory.getObjectFactory().createSchleife() });
        assertEquals(1, r.size());

        SchleifeVO schleifeMitDrucker = daoFactory.getObjectFactory()
                        .createSchleife();
        schleifeMitDrucker.setDruckerKuerzel("D");
        r = test.createAfterReportCreatedListeners(alarmVO, new SchleifeVO[]
        { schleifeMitDrucker });
        assertEquals(2, r.size());

        SchleifeVO schleifeMitZweiDruckernSemikolon = daoFactory
                        .getObjectFactory().createSchleife();
        schleifeMitZweiDruckernSemikolon.setDruckerKuerzel(" 1;2 ");
        r = test.createAfterReportCreatedListeners(alarmVO, new SchleifeVO[]
        { schleifeMitZweiDruckernSemikolon });
        assertEquals(3, r.size());

        SchleifeVO schleifeMitZweiDruckernKomma = daoFactory.getObjectFactory()
                        .createSchleife();
        schleifeMitZweiDruckernKomma.setDruckerKuerzel(" 1,2 ");
        r = test.createAfterReportCreatedListeners(alarmVO, new SchleifeVO[]
        { schleifeMitZweiDruckernKomma });
        assertEquals(3, r.size());

        SchleifeVO schleifeMitDreiDruckern = daoFactory.getObjectFactory()
                        .createSchleife();
        schleifeMitDreiDruckern.setDruckerKuerzel(" 3;1,2 ");
        r = test.createAfterReportCreatedListeners(alarmVO, new SchleifeVO[]
        { schleifeMitDreiDruckern });
        assertEquals(4, r.size());
    }
}
