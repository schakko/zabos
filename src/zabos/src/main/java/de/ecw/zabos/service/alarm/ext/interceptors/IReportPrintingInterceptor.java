package de.ecw.zabos.service.alarm.ext.interceptors;

import de.ecw.interceptors.ICommandLineExecutor;
import de.ecw.zabos.report.ReportCreationService;

/**
 * Interface des Interceptors zum Drucken von Dateien
 * 
 * @author ckl
 * 
 */
public interface IReportPrintingInterceptor extends ICommandLineExecutor
{

    /**
     * Liefert den {@link ReportCreationService}
     * 
     * @return
     */
    public ReportCreationService getReportCreationService();

    /**
     * Liefert zurück, ob standardmäßig immer mindestens auf dem Default-Printer
     * gedruckt werden soll
     * 
     * @return
     */
    public boolean isUseAlwayDefaultPrinter();

    /**
     * Liefert den String zurück, der durch die konkrete UID ersetzt werden
     * soll. Standard ist <code>%uid%</code>
     * 
     * @return
     */
    public String getOptionUid();

    /**
     * Liefert den String zurück, der durch den konkreten absoluten Pfad ersetzt
     * werden soll. Standard ist <code>%abs_path%</code>.
     * 
     * @return
     */
    public String getOptionAbsolutePath();

    /**
     * Liefert den String zurück, der durch das konkrete Drucker-Kürzel ersetzt
     * werden soll. Standard ist <code>%drucker_kuerzel%</code>.
     * 
     * @return
     */
    public String getOptionDruckerKuerzel();

}