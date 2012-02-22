package de.ecw.zabos.report;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.test.ZabosTestAdapter;
import de.ecw.zabos.types.id.AlarmId;
import de.ecw.zabos.types.id.SchleifeId;

public class ReportModelTest extends ZabosTestAdapter
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
    public void createReportModel()
    {
        try
        {
            new ReportModel(null, null);
            fail("ReportModel konnte ohne AlarmId erstellt werden");
        }
        catch (StdException e)
        {
        }
    }

    @Test
    public void testGetReportUid()
    {
        try
        {
            ReportModel testMitSchleifenId = new ReportModel(new AlarmId(1),
                            new SchleifeId(2));
            assertEquals("1_2", testMitSchleifenId.getReportUid());
            assertTrue(testMitSchleifenId.getOptions().containsKey(
                            ReportModel.KEY_ALARM_ID));
            assertEquals("1", testMitSchleifenId.getOptions().get(
                            ReportModel.KEY_ALARM_ID));
            assertTrue(testMitSchleifenId.getOptions().containsKey(
                            ReportModel.KEY_SCHLEIFE_ID));
            assertEquals("2", testMitSchleifenId.getOptions().get(
                            ReportModel.KEY_SCHLEIFE_ID));

            ReportModel testOhneSchleifenId = new ReportModel(new AlarmId(1),
                            null);
            assertTrue(testOhneSchleifenId.getOptions().containsKey(
                            ReportModel.KEY_ALARM_ID));
            assertEquals("1", testOhneSchleifenId.getOptions().get(
                            ReportModel.KEY_ALARM_ID));

            assertEquals(1, testOhneSchleifenId.getOptions().size());

        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }

    }
}
