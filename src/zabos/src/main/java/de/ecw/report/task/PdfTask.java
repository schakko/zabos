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
 * Task for creating PDF files
 * 
 * @author ckl
 * 
 */
public class PdfTask extends TaskAdapter
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
            renderOptions.closeOutputStreamOnExit(true);
            task.setRenderOption(renderOptions);

            // run report
            task.run();
            task.close();
        }
        catch (Exception e)
        {
            // Falls Parameter fehlen, wird eine ParameterValidationException
            // geworfen, die ich aber nicht auffangen kann.
            throw new ReportException(
                            "Could not create report. Please check for availability of report"
                                            + reportDesignPath
                                            + " and check all needed parameters inside the report.");
        }
    }
}
