package de.ecw.zabos.mc35;

import gnu.io.CommPortIdentifier;

import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;

import de.ecw.daemon.IDaemon;
import de.ecw.zabos.exceptions.StdException;

/**
 * Zeigt die verfügbaren COM-Ports an
 * 
 * @author ckl
 * 
 */
public class SerialPortIdentifierDaemon implements IDaemon
{
    private final static Logger log = Logger
                    .getLogger(SerialPortIdentifierDaemon.class);

    public final static String SERIAL_PORT_PROPERTY = "gnu.io.rxtx.SerialPorts";

    /**
     * Liste mit den zusätzlich zu registrierenden Ports
     */
    private List<String> additionalPortNames;

    /*
     * (non-Javadoc)
     * 
     * @see de.ecw.zabos.daemon.IDaemon#free()
     */
    public void free()
    {
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.ecw.zabos.daemon.IDaemon#init()
     */
    public void init() throws StdException
    {
        if (additionalPortNames != null)
        {
            log.info("Registriere zusaetzliche serielle Ports");

            String portNameSetting = "";

            for (int i = 0, m = additionalPortNames.size(); i < m; i++)
            {
                portNameSetting += additionalPortNames.get(i)
                                + File.pathSeparator;
            }

            log.info("Zusaetzliche Ports: " + portNameSetting);
            System.setProperty(SERIAL_PORT_PROPERTY, portNameSetting);

        }

        log.info("Ueberpruefe verfuegbare seriellen Schnittstellen");
        CommPortIdentifier[] comPorts = AbstractBaseSerialIO
                        .getPortIdentifiers();

        if (comPorts == null || comPorts.length == 0)
        {
            log.warn("Es stehen keine seriellen Schnittstellen zur Verfuegung");
            return;
        }

        for (int i = 0, m = comPorts.length; i < m; i++)
        {
            CommPortIdentifier cpi = comPorts[i];

            log.info("COM [" + i + "] Owner: \"" + cpi.getCurrentOwner()
                            + "\", Name: \"" + cpi.getName()
                            + "\", Porttype: \"" + cpi.getPortType() + "\"");
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see de.ecw.zabos.daemon.IDaemon#isRunning()
     */
    public DAEMON_STATUS getDaemonStatus()
    {
        return DAEMON_STATUS.ONLINE;
    }

    /**
     * Setzt die zusätzlichen Port-Namen
     * 
     * @param additionalPortNames
     */
    final public void setAdditionalPortNames(List<String> additionalPortNames)
    {
        log.info("Port-Names gesetzt");
        this.additionalPortNames = additionalPortNames;
    }

    /**
     * Liefert die zusätzlichen Port-Namen
     * 
     * @return
     */
    final public List<String> getAdditionalPortNames()
    {
        return additionalPortNames;
    }
}
