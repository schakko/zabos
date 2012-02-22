package de.ecw.zabos.mc35;

import gnu.io.CommPortIdentifier;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import de.ecw.daemon.IDaemon;
import de.ecw.daemon.watchdog.WatchdogDaemon;
import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.service.smsin.ISmsInService;
import de.ecw.zabos.sql.resource.DBResource;
import de.ecw.zabos.sql.vo.SystemKonfigurationMc35VO;
import de.ecw.zabos.types.TelefonNummer;

/**
 * Die MC35MangerDaemon Klasse dient zur Verwaltung der einzelnen Treiber
 * Threads und erbt von WatchdogDaemon. Durch das Erben vom
 * {@link WatchdogDaemon} wird sichergestellt, dass die einzelnen
 * MC35-Treiber-Threads bei Ausfall neu gestartet werden.
 * 
 * 
 */
public class MC35ManagerDaemon extends WatchdogDaemon
{
    private final static Logger log = Logger.getLogger(MC35ManagerDaemon.class);

    /**
     * Nächstes Device
     */
    protected int nextDeviceIdx = 0;

    /**
     * Referenz auf Datenbanke
     */
    private DBResource dbResource;

    private ISmsInService smsInService;

    /**
     * Konstruktor
     * 
     * @param _dbResource
     *            Unbedingt eine neue Datenbankinstanz übergeben, da diese
     *            Klasse gethreadet wird.
     * 
     * @param _smsInService
     */
    public MC35ManagerDaemon(DBResource _dbResource, ISmsInService _smsInService)
    {
        dbResource = _dbResource;
        setSmsInService(_smsInService);
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.ecw.zabos.daemon.IDaemon#init()
     */
    public void init() throws StdException
    {
        SystemKonfigurationMc35VO[] mc35vos = dbResource.getDaoFactory()
                        .getSystemKonfigurationDAO().findAllMC35();

        for (int i = 0, m = mc35vos.length; i < m; i++)
        {
            MC35 mc35 = new MC35(getSmsInService(), mc35vos[i]);
            addObservableChildDaemon(mc35, mc35vos[i].getRufnummer().toString());
        }

        super.init();
    }

    /**
     * Lädt die Konfiguration eines Modems neu, beendet den zugehörigen Thread
     * und startet diesen neu. Dadurch kann im laufenden Betrieb der COM-Port
     * der Modems gewechselt werden.
     * 
     * @param _mc35KonfigurationVO
     *            Wenn null, wird die neue Konfiguration hinzugefügt
     * @throws StdException
     * @author ckl
     */
    public void reload(SystemKonfigurationMc35VO _mc35KonfigurationVO) throws StdException
    {
        MC35 mc35 = getMC35DriverThread(_mc35KonfigurationVO);
        CommPortIdentifier[] identifiers = getPortIdentifiers();

        String portName = "unbekannt";
        String portNameNeu = "unbekannt";

        if (mc35 != null)
        {
            // Identifier existiert
            if ((identifiers != null)
                            && (identifiers.length >= mc35
                                            .getSystemKonfigurationMC35()
                                            .getComPort()))
            {
                portName = identifiers[mc35.getSystemKonfigurationMC35()
                                .getComPort()].getName();
            }

            log.debug("Modem ["
                            + mc35.getSystemKonfigurationMC35().getRufnummer()
                            + "] an Port " + portName + " wird gestoppt.");
            getObserveableChildDaemonsWithStatus().remove(mc35);
            mc35.free();
            mc35 = null;
        }

        // Modem Releasen bzw. Referenz in der Liste löschen
        mc35 = new MC35(getSmsInService(), _mc35KonfigurationVO);

        if ((identifiers != null)
                        && (identifiers.length >= _mc35KonfigurationVO
                                        .getComPort()))
        {
            portNameNeu = identifiers[_mc35KonfigurationVO.getComPort()]
                            .getName();
        }

        addObservableChildDaemon(mc35, _mc35KonfigurationVO.getRufnummer()
                        .toString());

        log.debug("Modem [" + _mc35KonfigurationVO.getRufnummer()
                        + "] wurde an Port " + portNameNeu
                        + " neu initalisiert.");
    }

    /**
     * Stoppt und entfernt einen MC35-Thread
     * 
     * @param _vo
     */
    public void remove(SystemKonfigurationMc35VO _vo)
    {
        log.debug("Entferne Treiber-Thread [" + _vo.getRufnummer() + "]");
        MC35 mc35 = getMC35DriverThread(_vo);
        mc35.free();
        getMarkedAsOnlineChildDaemons().remove(mc35);
        getObserveableChildDaemonsWithStatus().remove(mc35);
        mc35 = null;
    }

    /**
     * Liefert das nächste device, das als Rückkanal (Absender) in eine
     * ausgehende SMS eingetragen werden soll.
     * 
     * Um eine möglichst gleichmässige Auslastung der GSM Modems zu erreichen
     * werden die Modems abwechselnd als Absender eingetragen.
     * 
     * @return
     */
    public MC35 getNextDevice()
    {
        List<IDaemon> devicesOnline = getMarkedAsOnlineChildDaemons();
        List<MC35> allowedDevices = new ArrayList<MC35>();

        for (int i = 0, m = devicesOnline.size(); i < m; i++)
        {
            MC35 device = (MC35) devicesOnline.get(i);

            // Alarmdaemons dürfen *nicht* als Rückrufnummer benutzt werden
            if (!device.getSystemKonfigurationMC35().getAlarmModem())
            {
                allowedDevices.add(device);
            }
        }

        int totalOnlineMc35Devices = allowedDevices.size();

        // Kein Modem da frei => Unbekannte Absendernummer
        MC35 r = null;

        // Liste mit den verfügbaren Modems erstellen, die *keine* Alarm-Modems
        // und Online sind
        if (totalOnlineMc35Devices > 0)
        {
            if ((totalOnlineMc35Devices == 1)
                            || (nextDeviceIdx >= totalOnlineMc35Devices))
            {
                // Es gibt nur ein Modem
                nextDeviceIdx = 0;
            }

            // Modem benutzen
            r = allowedDevices.get(nextDeviceIdx);

            // Nächstes Modem in der Liste nutzen
            nextDeviceIdx++;
        }

        return r;
    }

    /**
     * Liefert den Treiberthread zurück
     * 
     * @param _mc35VO
     * @return
     */
    public MC35 getMC35DriverThread(SystemKonfigurationMc35VO _mc35VO)
    {
        List<IDaemon> mc35s = getObserveableChildDaemons();

        for (int i = 0, m = mc35s.size(); i < m; i++)
        {
            MC35 mc35 = (MC35) mc35s.get(i);

            if (mc35.getSystemKonfigurationMC35() != null)
            {
                if (mc35.getSystemKonfigurationMC35().getBaseId()
                                .equals(_mc35VO.getBaseId()))
                {
                    return mc35;
                }
            }
        }
        return null;
    }

    /**
     * Liefert zurück, ob das Modem online ist
     * 
     * @param _mc35VO
     * @return
     */
    public boolean isModemOnline(SystemKonfigurationMc35VO _mc35VO)
    {
        MC35 mc35 = getMC35DriverThread(_mc35VO);

        if (mc35 == null)
        {
            return false;
        }

        return (mc35.getDaemonStatus() == DAEMON_STATUS.ONLINE);
    }

    /**
     * Liefert die Rufnummer des nächstes Rückkanal Devices
     * 
     * @return Handy-Telefonnummer
     */
    public TelefonNummer getNextDeviceRufnummer()
    {
        MC35 device = getNextDevice();

        if (device != null)
        {
            return device.getSystemKonfigurationMC35().getRufnummer();
        }
        else
        {
            return TelefonNummer.UNBEKANNT;
        }

    }

    /**
     * Delegiert an {@link AbstractBaseSerialIO#getPortIdentifiers()}
     * 
     * @return
     */
    public CommPortIdentifier[] getPortIdentifiers()
    {
        CommPortIdentifier[] r = null;
        
        try
        {
            r = AbstractBaseSerialIO.getPortIdentifiers();
        }
        catch (Exception e)
        {
            log.error("Failed to load serial port identifiers: "
                            + e.getMessage());
        }

        return r;
    }

    /**
     * Setzt den {@link ISmsInService}
     * 
     * @param smsInService
     */
    final public void setSmsInService(ISmsInService smsInService)
    {
        this.smsInService = smsInService;
    }

    /**
     * Liefert den {@link ISmsInService}
     * 
     * @return
     */
    final public ISmsInService getSmsInService()
    {
        return smsInService;
    }
}
