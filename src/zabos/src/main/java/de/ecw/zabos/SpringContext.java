package de.ecw.zabos;

import org.springframework.context.support.AbstractApplicationContext;

import de.ecw.daemon.DaemonMgr;
import de.ecw.report.IReportService;
import de.ecw.report.service.ReportServiceAdapter;
import de.ecw.report.service.WebReportService;
import de.ecw.zabos.alarm.consumer.zvei.daemon.ZveiSerialHardwareDaemon;
import de.ecw.zabos.alarm.daemon.AlarmDaemon;
import de.ecw.zabos.broadcast.IBroadcaster;
import de.ecw.zabos.broadcast.BroadcastMgr;
import de.ecw.zabos.broadcast.transport.http.client.ISmsClient;
import de.ecw.zabos.broadcast.transport.http.socket.ISmsSocket;
import de.ecw.zabos.license.License;
import de.ecw.zabos.mc35.MC35;
import de.ecw.zabos.mc35.MC35ManagerDaemon;
import de.ecw.zabos.report.ReportCreationService;
import de.ecw.zabos.service.alarm.AlarmServiceAdapter;
import de.ecw.zabos.service.alarm.IAlarmService;
import de.ecw.zabos.service.alarm.klinikum.KlinikumAlarmService;
import de.ecw.zabos.service.smsin.ISmsInService;
import de.ecw.zabos.service.smsin.SmsInServiceAdapter;
import de.ecw.zabos.service.smsin.klinikum.KlinikumSmsInService;
import de.ecw.zabos.sql.resource.DBResource;
import de.ecw.zabos.sql.vo.SystemKonfigurationVO;

/**
 * Diese Klasse enthält alle Bean-IDs, die im System benutzt werden. <br />
 * {@link SpringContext} ist ein Singleton
 * 
 * @author ckl
 * 
 */
final public class SpringContext
{
    /**
     * Spring-ApplicationContext
     */
    private AbstractApplicationContext applicationContext;

    /**
     * Singleton
     */
    private static SpringContext instance = null;

    /**
     * Haupt-Datenbankverbindung, Klasse {@link DBResource}
     */
    public final static String BEAN_DB_RESOURCE = "dbResource";

    /**
     * Service zum Verarbeiten eingehender SMSen, muss vom Typ
     * {@link ISmsInService} sein, z.B. {@link KlinikumSmsInService} oder
     * {@link SmsInServiceAdapter}
     */
    public final static String BEAN_SMS_IN_SERVICE = "smsInService";

    /**
     * Service zum Handeln der aktiven Alarme, muss das Interface
     * {@link IAlarmService} implementieren, z.B. {@link AlarmServiceAdapter}
     * oder {@link KlinikumAlarmService}
     */
    public final static String BEAN_ALARM_SERVICE = "alarmService";

    /**
     * Bean zum Ansteuern externer Geräte, wie z.B. dem Fünfton-Modul
     * {@link ZveiSerialHardwareDaemon}
     */
    public final static String BEAN_THIRD_PARTY_DAEMON = "thirdPartyDaemon";

    /**
     * Bean zum Erstellen der Reporte, muss das Interface {@link IReportService}
     * implementieren, z.B. {@link ReportServiceAdapter} oder
     * {@link WebReportService}
     */
    public final static String BEAN_REPORT = "reportService";

    /**
     * Bean zum Erstellen der spezifischen ZABOS-Reports, Klasse
     * {@link ReportCreationService}
     */
    public final static String BEAN_REPORT_CREATION = "reportCreationService";

    /**
     * Bean zum Verwalten der Hintergrund-Daemons, Klasse {@link DaemonMgr}
     */
    public final static String BEAN_DAEMON_MANAGER = "daemonManager";

    /**
     * Bean, der {@link #BEAN_ALARM_SERVICE} überwacht, Klasse
     * {@link AlarmDaemon}
     */
    public final static String BEAN_ALARM_DAEMON = "alarmDaemon";

    /**
     * Bean, der die einzelnen MC35-GSM-Modems der Klasse {@link MC35}
     * verwaltet; Klasse {@link MC35ManagerDaemon}
     */
    public final static String BEAN_MC35_MANAGER_DAEMON = "mc35ManagerDaemon";

    /**
     * Bean zum Versenden der SMSen, Klasse {@link BroadcastMgr}
     */
    public final static String BEAN_SMS_BROADCAST_DAEMON = "smsBroadcastDaemon";

    /**
     * Bean zum Erstellen der Broadcaster, muss das Interface
     * {@link IBroadcaster} implementieren
     */
    public final static String BEAN_SMS_BROADCASTER = "smsBroadcaster";

    /**
     * Bean zum Erzeugen der Socket-Verbindung beim Versenden der SMSen, muss
     * das Interface {@link ISmsSocket} implementieren
     */
    public final static String BEAN_SMS_SOCKET = "smsSocket";

    /**
     * Bean mit API-spezifischen Anforderungen der SMS-Versendung; muss das
     * Interface {@link ISmsClient} implementieren
     */
    public final static String BEAN_SMS_CLIENT = "smsClient";

    /**
     * Controller für die Schleifen
     */
    public final static String CONTROLLER_SCHLEIFE = "controllerSchleife";

    /**
     * Controller für die Personen
     */
    public final static String CONTROLLER_PERSON = "controllerPerson";

    /**
     * Controller für die Systemverwaltung
     */
    public final static String CONTROLLER_SYSTEM = "controllerSystem";

    /**
     * Controller für die Alarmierungsübersicht
     */
    public final static String CONTROLLER_ALARMIERUNG = "controllerAlarmierung";

    /**
     * Bean mit der ZABOS-Lizenz-Datei, Klasse {@link License}
     */
    public final static String BEAN_LICENSE = "license";

    /**
     * Bean mit der System-Konfiguration; Klasse {@link SystemKonfigurationVO}
     */
    public final static String SYSTEM_KONFIGURATION = "systemKonfiguration";

    /**
     * Bean für die Internationalisierung; Klasse {@link de.ecw.zabos.i18n.I18N}
     */
    public final static String I18N = "i18n";

    /**
     * privat, da Singleton
     */
    private SpringContext()
    {
    }

    /**
     * Singleton
     * 
     * @return
     */
    public synchronized static SpringContext getInstance()
    {
        if (instance == null)
        {
            instance = new SpringContext();
        }

        return instance;
    }

    /**
     * Setzt den ApplicationContext
     * 
     * @param _ac
     */
    public void setApplicationContext(AbstractApplicationContext _ac)
    {
        applicationContext = _ac;
    }

    /**
     * Liefert den ApplicationContext zurück
     * 
     * @return
     */
    public AbstractApplicationContext getApplicationContext()
    {
        return applicationContext;
    }

    /**
     * Liefert ein Bean zurück
     * 
     * @param _beanName
     * @param _clazz
     * @return
     */
    @SuppressWarnings("unchecked")
    public Object getBean(String _beanName, Class _clazz)
    {
        return getApplicationContext().getBean(_beanName, _clazz);
    }

    public void createSmsInServiceBean()
    {
        ISmsInService service = (ISmsInService) getApplicationContext()
                        .getBean(BEAN_SMS_IN_SERVICE);
        
        service.processSmsInbox();
    }
}
