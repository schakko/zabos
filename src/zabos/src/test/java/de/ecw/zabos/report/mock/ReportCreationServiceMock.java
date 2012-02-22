package de.ecw.zabos.report.mock;

import java.util.List;

import de.ecw.report.IReportService;
import de.ecw.report.exception.ReportException;
import de.ecw.report.types.IReportModel;
import de.ecw.zabos.report.IReportListener;
import de.ecw.zabos.report.ReportCreationService;
import de.ecw.zabos.sql.resource.DBResource;
import de.ecw.zabos.sql.vo.AlarmVO;

/**
 * Mock-Klasse für {@link ReportCreationService}. Das Erstellen des Reports wird
 * über einen {@link Thread#sleep(long)} simuliert.
 * 
 * @author ckl
 * 
 */
public class ReportCreationServiceMock extends ReportCreationService
{
    private AlarmVO alarmMock;

    public class ReportCreationThreadMock extends ReportCreationThread
    {

        public ReportCreationThreadMock(IReportModel reportModel)
        {
            super(reportModel);
        }

        protected void create(IReportModel _reportModel) throws ReportException
        {
            try
            {
                // Erstellen anhalten, so dass wir die Queue testen können
                Thread.sleep(2000);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }

    protected ReportCreationThread createNewReportCreationThread(
                    IReportModel _reportModel,
                    List<IReportListener> _afterReportCreated,
                    String _threadName)
    {
        ReportCreationThread thread = new ReportCreationThreadMock(_reportModel);
        thread.setReportListener(_afterReportCreated);
        thread.setName(_threadName);

        return thread;

    }

    public ReportCreationServiceMock(DBResource dbResource)
    {
        super(dbResource);
    }

    public ReportCreationServiceMock(DBResource dbResource, IReportService irs)
    {
        super(dbResource, irs);
    }

    public void mockAddModelToQueue(IReportModel _reportModel)
    {
        addReportUidToQueue(_reportModel.getReportUid());
    }

    protected AlarmVO findAlarmByReportModel(IReportModel _reportModel)
    {
        return alarmMock;
    }

    public void setAlarmMock(AlarmVO alarmMock)
    {
        this.alarmMock = alarmMock;
    }

    public AlarmVO getAlarmMock()
    {
        return alarmMock;
    }

}
