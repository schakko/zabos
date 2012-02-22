package de.ecw.zabos.report;

import java.util.HashMap;
import java.util.Map;

import de.ecw.report.types.IReportModel;
import de.ecw.report.types.ReportFormat;
import de.ecw.report.types.ReportType;
import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.types.id.AlarmId;
import de.ecw.zabos.types.id.SchleifeId;

/**
 * Stellt einen Report als Objekt dar
 * 
 * @author ckl
 * 
 */
public class ReportModel implements IReportModel
{
    private Map<String, String> mapOptions = new HashMap<String, String>();

    private AlarmId alarmId;

    private SchleifeId schleifeId;

    private boolean isCreated = false;

    /**
     * Dieser Key wird in der HashMap für die Id des Alarms benutzt
     */
    public final static String KEY_ALARM_ID = "ALARM_ID";

    /**
     * Dieser Key wird in der Hashmap für die Id der Schleife benutzt
     */
    public final static String KEY_SCHLEIFE_ID = "SCHLEIFE_ID";

    /**
     * Konstruktor mit der Alarm-ID.<br />
     * Die Alarm-ID wird im Report als Option {@value #KEY_ALARM_ID} übergeben,
     * die Schleife-ID als {@value #KEY_SCHLEIFE_ID}
     * 
     * @param _alarmId
     * @throws StdException
     *             Wenn keine {@link AlarmId} übergeben wurde
     */
    public ReportModel(AlarmId _alarmId, SchleifeId _schleifeId)
                    throws StdException
    {
        if (_alarmId == null)
        {
            throw new StdException("Es wurde keine AlarmId uebergeben");
        }

        alarmId = _alarmId;
        schleifeId = _schleifeId;

        long longAlarmId = 0;

        longAlarmId = _alarmId.getLongValue();
        mapOptions.put(KEY_ALARM_ID, "" + longAlarmId);

        if (_schleifeId != null)
        {
            long longSchleifeId = 0;
            longSchleifeId = _schleifeId.getLongValue();
            mapOptions.put(KEY_SCHLEIFE_ID, "" + longSchleifeId);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.ecw.report.types.IReportModel#getOptions()
     */
    public Map<String, String> getOptions()
    {
        return mapOptions;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.ecw.report.types.IReportModel#getReportFormat()
     */
    public ReportFormat getReportFormat()
    {
        return ReportFormat.PDF;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.ecw.report.types.IReportModel#getReportType()
     */
    public ReportType getReportType()
    {
        return new ReportType("alarmierung", "Alarmierung");
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.ecw.report.types.IReportModel#getReportUid()
     */
    public String getReportUid()
    {
        String r = "" + alarmId.getLongValue();

        if (schleifeId != null && schleifeId.getLongValue() > 0)
        {
            r += "_" + schleifeId.getLongValue();
        }

        return r;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.ecw.report.types.IReportModel#setIsCreated(boolean)
     */
    public void setIsCreated(boolean _isCreated)
    {
        isCreated = _isCreated;
    }

    public boolean isCreated()
    {
        return isCreated;
    }
}