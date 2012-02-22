package de.ecw.zabos.service.alarm.ext.interceptors;

import de.ecw.interceptors.executor.CommandLineExecutorAdapter;
import de.ecw.zabos.report.ReportCreationService;

public class ReportPrintingInterceptorAdapter extends
                CommandLineExecutorAdapter implements
                IReportPrintingInterceptor
{

    private boolean useAlwayDefaultPrinter = true;

    /**
     * Referenz zum ReportingCreationService
     */
    private ReportCreationService reportCreationService = null;

    /**
     * %uid%
     */
    public final static String OPTION_UID = "%uid%";

    /**
     * %drucker_kuerzel%
     */
    public final static String OPTION_DRUCKER_KUERZEL = "%drucker_kuerzel%";

    /**
     * %abs_path%
     */
    public final static String OPTION_ABSOLUTE_PATH = "%abs_path%";

    public void setUseAlwayDefaultPrinter(boolean useAlwayDefaultPrinter)
    {
        this.useAlwayDefaultPrinter = useAlwayDefaultPrinter;
    }

    /**
     * Setzt den ReportCreationService
     * 
     * @param reportCreationService
     */
    final public void setReportCreationService(
                    ReportCreationService reportCreationService)
    {
        this.reportCreationService = reportCreationService;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.ecw.zabos.service.alarm.ext.interceptors.IReportPrintingInterceptor
     * #getReportCreationService()
     */
    final public ReportCreationService getReportCreationService()
    {
        return reportCreationService;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.ecw.zabos.service.alarm.ext.interceptors.IReportPrintingInterceptor
     * #isUseAlwayDefaultPrinter()
     */
    public boolean isUseAlwayDefaultPrinter()
    {
        return useAlwayDefaultPrinter;
    }

    public String getOptionAbsolutePath()
    {
        return OPTION_ABSOLUTE_PATH;
    }

    public String getOptionDruckerKuerzel()
    {
        return OPTION_DRUCKER_KUERZEL;
    }

    public String getOptionUid()
    {
        return OPTION_UID;
    }
}
