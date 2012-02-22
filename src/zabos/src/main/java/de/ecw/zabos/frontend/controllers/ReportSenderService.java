package de.ecw.zabos.frontend.controllers;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import de.ecw.report.types.IReportModel;
import de.ecw.report.types.ReportFormat;
import de.ecw.zabos.SpringContext;
import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.frontend.ressources.RequestResources;
import de.ecw.zabos.report.ReportCreationService;
import de.ecw.zabos.report.ReportModel;
import de.ecw.zabos.types.id.AlarmId;

/**
 * Sendet den Report direkt an den Client
 * 
 * @author ckl
 * 
 */
public class ReportSenderService extends HttpServlet
{
    private static final long serialVersionUID = -3167123818108944129L;

    /**
     * Logger-Instanz
     */
    private final static Logger log = Logger
                    .getLogger(ReportSenderService.class);

    /**
     * Einzig erlaubte Methode. Es werden die passenden HTTP-Errorcodes
     * zurückgeliefert, wenn
     * <ul>
     * <li> {@link HttpServletResponse#SC_UNAUTHORIZED} wenn der Benutzer nicht
     * angemeldet ist</li>
     * <li> {@link HttpServletResponse#SC_NOT_IMPLEMENTED} wenn der
     * {@link ReportCreationService} nicht eingebunden oder ein ungültige
     * {@link AlarmId} übergeben wurde</li>
     * <li> {@link HttpServletResponse#SC_NO_CONTENT} wenn der Bericht momentan
     * erstellt wurde bzw. durch den GET-Aufruf die Erstellung des Berichts
     * angetriggert wurde</li>
     * <li> {@link HttpServletResponse#SC_NOT_FOUND}> wenn der Bericht im
     * Dateisystem nicht gefunden werden konnte</li>
     * <li> {@link HttpServletResponse#SC_INTERNAL_SERVER_ERROR} Wenn ein Fehler
     * bei der Ausgabe der Report-Daten aufgetreten ist</li>
     * </ul>
     */
    @Override
    protected void doGet(HttpServletRequest _req, HttpServletResponse _res) throws ServletException, IOException
    {
        final RequestResources req = new RequestResources(_req, _res,
                        getServletConfig(), null);

        if (!req.getUserBean().isLoggedIn())
        {
            generateError(HttpServletResponse.SC_UNAUTHORIZED,
                            "Sie sind nicht angemeldet", _res);
            return;
        }

        ReportCreationService rcs = (ReportCreationService) SpringContext
                        .getInstance().getBean(
                                        SpringContext.BEAN_REPORT_CREATION,
                                        ReportCreationService.class);

        if (rcs == null)
        {
            generateError(HttpServletResponse.SC_NOT_IMPLEMENTED,
                            "Der ReportCreationService wurde nicht aktiviert.",
                            _res);
            return;
        }

        // Korrekte Report-ID übergeben?
        final AlarmId alarmId = extractAlarmId(_req);

        if (alarmId == null)
        {
            generateError(HttpServletResponse.SC_NOT_IMPLEMENTED,
                            "Sie haben keine g&uuml;tige Alarm-ID &uuml;bergeben",
                            _res);
            return;
        }

        IReportModel reportModel = null;

        try
        {
            reportModel = new ReportModel(alarmId, null);

            if (rcs.findReport(reportModel) == null)
            {
                if (rcs.isAlarmInReportQueue(alarmId))
                {
                    generateError(HttpServletResponse.SC_NO_CONTENT,
                                    "Der Report wird momentan erstellt. Bitte versuchen Sie es zu einem sp&auml;teren Zeitpunkt wieder.",
                                    _res);
                    return;
                }

                rcs.startReportCreation(reportModel, null);

                generateError(HttpServletResponse.SC_NO_CONTENT,
                                "Die Erstellung des Reports wurde so eben gestartet. Bitte versuchen Sie es zu einem sp&auml;teren Zeitpunkt wieder.",
                                _res);
                return;
            }
        }
        catch (StdException e)
        {
            generateError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                            e.getMessage(), _res);
            return;
        }

        try
        {
            File outputFile = rcs.findReport(reportModel);

            if (outputFile == null)
            {
                generateError(HttpServletResponse.SC_NOT_FOUND,
                                "Die Report-Datei konnte nicht gefunden werden",
                                _res);
                return;
            }

            log.debug("Lokaler Pfad des zu sendenden Reports: " + outputFile);

            ServletOutputStream sos = _res.getOutputStream();

            ReportFormat rft = ReportFormat.PDF;

            _res.setContentType(rft.getMimeType());
            _res.addHeader("Content-Disposition",
                            "attachment; filename="
                                            + reportModel.getReportUid() + "."
                                            + rft.getExtension());
            _res.setContentLength((int) outputFile.length());

            FileInputStream fis = new FileInputStream(outputFile);
            BufferedInputStream bis = new BufferedInputStream(fis);
            int bytesRead = 0;

            while ((bytesRead = bis.read()) != -1)
            {
                sos.write(bytesRead);
            }
            if (sos != null)
            {
                sos.close();
            }

            if (bis != null)
            {
                bis.close();
            }
        }
        catch (IOException e)
        {
            generateError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                            e.getMessage(), _res);
        }
    }

    /**
     * Extrahiert aus dem Query-String den Pfad zum zu liefernden Dokument
     * 
     * @param _req
     * @return
     */
    protected AlarmId extractAlarmId(HttpServletRequest _req)
    {
        AlarmId r = null;

        try
        {
            String qs = _req.getQueryString();
            log.debug("Loese '" + qs + "' zur ID auf");

            long l = Long.valueOf(qs);
            r = new AlarmId(l);
        }
        catch (NumberFormatException e)
        {
            log.error("ID konnte nicht aufgeloest werden: " + e.getMessage());
            return null;
        }

        return r;
    }

    /**
     * Nicht implementiert
     */
    protected void doPost(HttpServletRequest _req, HttpServletResponse _res) throws ServletException, IOException
    {
        notImplemented(_res);
    }

    /**
     * Nicht implementiert
     */
    protected void doHead(HttpServletRequest _req, HttpServletResponse _res) throws ServletException, IOException
    {
        notImplemented(_res);
    }

    /**
     * Nicht implementiert
     */
    protected void doOptions(HttpServletRequest _req, HttpServletResponse _res) throws ServletException, IOException
    {
        notImplemented(_res);
    }

    /**
     * Nicht implementiert
     */
    protected void doPut(HttpServletRequest _req, HttpServletResponse _res) throws ServletException, IOException
    {
        notImplemented(_res);
    }

    /**
     * Nicht implementiert
     */
    protected void doTrace(HttpServletRequest _req, HttpServletResponse _res) throws ServletException, IOException
    {
        notImplemented(_res);
    }

    /**
     * Erzeugt einen "nicht implementiert"-Fehler
     */
    protected void notImplemented(HttpServletResponse _res) throws ServletException, IOException
    {
        generateError(HttpServletResponse.SC_NOT_IMPLEMENTED,
                        "Diese Anfrage ist nicht g&uuml;ltig.<br />Es sind nur GET-Anfragen zul&auuml;ssig.",
                        _res);
    }

    protected void generateError(int _sc, String _error,
                    HttpServletResponse _res) throws ServletException, IOException
    {
        log.error("SenderService: Status-Code " + _sc + ", " + _error);

        _res.setStatus(HttpServletResponse.SC_NOT_IMPLEMENTED);
        ServletOutputStream sos = _res.getOutputStream();
        sos.println("<html><head><title>ZABOS @VERSION@</title></head><body>"
                        + _error + "</body></html>");

    }
}
