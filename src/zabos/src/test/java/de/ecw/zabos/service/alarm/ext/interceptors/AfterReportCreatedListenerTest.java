package de.ecw.zabos.service.alarm.ext.interceptors;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.ecw.report.types.IReportModel;
import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.report.ReportModel;
import de.ecw.zabos.test.ZabosTestAdapter;
import de.ecw.zabos.types.id.AlarmId;

public class AfterReportCreatedListenerTest extends ZabosTestAdapter
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
    public void create()
    {
        try
        {
            new AfterReportCreatedListener(null, "", null);
            fail("Objekt konnte trotz fehlendem IReportPrintingInterceptor erstellt werden");
        }
        catch (StdException e)
        {
        }
    }

    @Test
    public void buildCommandList()
    {
        try
        {
            ReportPrintingInterceptorAdapter adapter = new ReportPrintingInterceptorAdapter();

            AfterReportCreatedListener test = new AfterReportCreatedListener(
                            adapter, "", null);

            List<String> commandArguments = new ArrayList<String>();
            commandArguments.add("a=" + adapter.getOptionUid() + "b="
                            + adapter.getOptionDruckerKuerzel() + "c="
                            + adapter.getOptionAbsolutePath());
            IReportModel reportModel = new ReportModel(new AlarmId(1), null);
            String pfad = "c:\\test";
            String druckerKuerzel = "kuerzel";

            List<String> r = test.buildCommandList(commandArguments,
                            reportModel, new File(pfad), druckerKuerzel);

            assertEquals(1, r.size());
            String testResult = r.get(0);
            assertEquals("a=" + reportModel.getReportUid() + "b="
                            + druckerKuerzel + "c=" + pfad, testResult);
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }
    }
}
