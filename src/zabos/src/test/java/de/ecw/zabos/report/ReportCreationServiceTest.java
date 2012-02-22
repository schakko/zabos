package de.ecw.zabos.report;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.ecw.report.types.IReportModel;
import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.report.ReportCreationService.REPORT_CREATION_FLAG;
import de.ecw.zabos.report.mock.ReportCreationServiceMock;
import de.ecw.zabos.sql.vo.AlarmVO;
import de.ecw.zabos.test.ZabosTestAdapter;
import de.ecw.zabos.types.id.AlarmId;
import de.ecw.zabos.types.id.SchleifeId;

public class ReportCreationServiceTest extends ZabosTestAdapter
{
    private static boolean isInitialized = false;

    @Before
    public synchronized void initEnvironmentSub()
    {
        if (!isInitialized)
        {
        }
    }

    private ReportCreationServiceMock createTestMock(AlarmVO _alarmVO)
    {
        ReportCreationServiceMock r = new ReportCreationServiceMock(dbResource);
        r.setAlarmMock(_alarmVO);

        return r;
    }

    @Test
    public void isReportOfAlarmCreateable()
    {
        ReportCreationServiceMock test = null;
        IReportModel model = null;

        try
        {
            model = new ReportModel(new AlarmId(1), null);
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }

        test = createTestMock(null);
        assertEquals(REPORT_CREATION_FLAG.ALARM_NICHT_IN_DATENBANK_GEFUNDEN,
                        test.isReportOfAlarmCreatable(model));

        AlarmVO alarmVO = daoFactory.getObjectFactory().createAlarm();
        try
        {
            alarmVO.setAlarmId(new AlarmId(1));
        }
        catch (StdException e)
        {
            e.printStackTrace();
        }
        alarmVO.setAktiv(true);

        test = createTestMock(alarmVO);
        assertEquals(REPORT_CREATION_FLAG.ALARM_NOCH_AKTIV,
                        test.isReportOfAlarmCreatable(model));

        alarmVO.setAktiv(false);
        test = createTestMock(alarmVO);
        test.mockAddModelToQueue(model);

        assertEquals(REPORT_CREATION_FLAG.REPORT_WIRD_GERADE_ERSTELLT,
                        test.isReportOfAlarmCreatable(model));

        test = createTestMock(alarmVO);
        assertEquals(REPORT_CREATION_FLAG.IST_ERSTELLBAR,
                        test.isReportOfAlarmCreatable(model));
    }

    @Test
    public void createReportModel()
    {
        ReportCreationServiceMock test = createTestMock(null);
        Object o = null;
        IReportModel r = test.createReportModel(o);
        assertEquals(null, r);

        r = test.createReportModel(new SchleifeId(1));
        assertEquals(null, r);

        r = test.createReportModel(new AlarmId(1));
        assertTrue((r != null));
        assertEquals("1", r.getReportUid());

        r = test.createReportModel(new AlarmId(1), new SchleifeId(2));
        assertTrue((r != null));
        assertEquals("1_2", r.getReportUid());

        r = test.createReportModel(new AlarmId(1), new SchleifeId(2),
                        new AlarmId(3), new SchleifeId(4));
        assertTrue((r != null));
        assertEquals("3_4", r.getReportUid());
    }

    private int testValueForListener = 0;

    synchronized protected void setTestValueForListener(int _v)
    {
        testValueForListener = _v;
    }

    @Test
    public void startReportCreation()
    {
        AlarmVO erstellbarerAlarm = daoFactory.getObjectFactory().createAlarm();
        try
        {
            erstellbarerAlarm.setAlarmId(new AlarmId(1));
        }
        catch (StdException e)
        {
            e.printStackTrace();
        }
        ReportCreationServiceMock test = createTestMock(erstellbarerAlarm);
        IReportModel reportModel = test.createReportModel(new AlarmId(1));

        List<IReportListener> testListeners = new ArrayList<IReportListener>();
        testListeners.add(new IReportListener()
        {

            public void onExecute(IReportModel reportModel)
            {
                setTestValueForListener(1);
            }
        });

        test.startReportCreation(reportModel, testListeners);

        // Warten, so dass der Report in der Queue auftaucht
        try
        {
            Thread.sleep(1000);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        // Alarm befindet sich noch in Queue
        assertEquals(REPORT_CREATION_FLAG.REPORT_WIRD_GERADE_ERSTELLT,
                        test.isReportOfAlarmCreatable(reportModel));

        // Warten, so dass der Report aus der Queue verschwindet
        try
        {
            Thread.sleep(3000);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        // Alarm befindet sich nicht mehr in der Queue
        assertEquals(REPORT_CREATION_FLAG.IST_ERSTELLBAR,
                        test.isReportOfAlarmCreatable(reportModel));

        // Listener muss ausgeführt und die 1 gesetzt worden sein
        assertEquals(testValueForListener, 1);
    }
}
