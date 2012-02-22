package de.ecw.report.task;

import java.util.HashMap;

import org.eclipse.birt.report.data.oda.jdbc.IConnectionFactory;
import org.eclipse.birt.report.engine.api.EngineConstants;
import org.eclipse.birt.report.engine.api.HTMLRenderOption;
import org.eclipse.birt.report.engine.api.IReportRunnable;
import org.eclipse.birt.report.engine.api.IRunAndRenderTask;
import org.eclipse.birt.report.engine.api.RenderOption;

import de.ecw.report.exception.ReportException;

/**
 * Task for creating CSV files
 * 
 * @author ckl
 */
public class CsvTask extends TaskAdapter
{

    public void run() throws ReportException
    {
        RenderOption renderOptions = new RenderOption();

        HashMap<Object, Object> contextMap = new HashMap<Object, Object>();

        contextMap.put(EngineConstants.APPCONTEXT_PDF_RENDER_CONTEXT,
                        renderOptions);
        contextMap.put(IConnectionFactory.PASS_IN_CONNECTION, connection);
        contextMap.put(IConnectionFactory.CLOSE_PASS_IN_CONNECTION, false);

        IReportRunnable design;

        try
        {
            // Open report design
            design = reportEngine.openReportDesign(reportDesignPath);
            // create task to run and render report
            IRunAndRenderTask task = reportEngine
                            .createRunAndRenderTask(design);
            task.setAppContext(contextMap);
            TaskUtil.setParameterValues(task, report.getOptions());

            // set output options
            renderOptions.setOutputFormat(HTMLRenderOption.OUTPUT_FORMAT_PDF);
            renderOptions.setOutputFileName(targetReportPath);
            task.setRenderOption(renderOptions);
            renderOptions.closeOutputStreamOnExit(true);

            // run report
            task.run();
            task.close();
        }
        catch (Exception e)
        {
            throw new ReportException(e);
        }
    }
}
