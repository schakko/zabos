package de.ecw.zabos.mc35;

import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.RXTXCommDriver;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.apache.log4j.Logger;

import de.ecw.zabos.exceptions.StdException;

/**
 * Basisklasse für serielle Treiber. Verwendet das gnu.io Framework.
 * 
 * @author bsp
 * 
 */
public abstract class AbstractBaseSerialIO
{

    private final static Logger log = Logger
                    .getLogger(AbstractBaseSerialIO.class);

    protected static RXTXCommDriver comm_driver;

    /**
     * Alle verfuegbaren COM Ports
     */
    protected static CommPortIdentifier port_identifiers[];

    /**
     * Der ausgewaehlte COM Port (siehe port_number)
     */
    protected CommPortIdentifier port_identifier;

    protected SerialPort serial_port;

    /**
     * Ausgewaehlter COM Port (index in port_identifiers[])
     */
    protected int portNumber;

    protected int portBaudRate;

    protected int portDataBits;

    protected int portParity;

    protected int portStopBits;

    /**
     * Standard delay time in milli seconds
     */
    protected static final int SERIAL_DELAY = 10;

    /**
     * Timeout argument for call to gnu.io.CommPortIdentifier::open()
     */
    private static final int PORT_CONNECT_TIMEOUT = 1;

    /**
     * Used to write data to the ME
     */
    protected OutputStream serial_os;

    /**
     * Used to read data from the ME
     */
    protected InputStream serial_is;

    /**
     * Listens for ME->TE event messages (incoming SMS)
     */
    private Thread event_thread;

    /**
     * Used by startEventThread() to determine the actual code to run if
     * starting the event thread. Needs to be overwritten by derived classes.
     * 
     * @return
     */
    abstract protected Runnable getEventThreadRunnable();

    /**
     * Called during start() to initialize the mobile equipment. Needs to be
     * overwritten by derived classes.
     * 
     * @return Success of initialization (true=OK, false=failure)
     */
    abstract protected void initializeME() throws StdException;

    /**
     * Called during stop() to re-set the mobile equipment. Needs to be
     * overwritten by derived classes.
     * 
     */
    abstract protected void exitME();

    /**
     * Open and initialize serial port
     * 
     */
    public synchronized void initializeSerial() throws StdException
    {
        getPortIdentifiers();

        if (port_identifiers.length == 0)
        {
            throw new StdException(
                            "Es stehen keine freien Ports zur Verfuegung.");
        }

        try
        {
            port_identifier = port_identifiers[portNumber];

            log.debug("got port identifier");

            try
            {
                serial_port = (SerialPort) port_identifier.open(port_identifier
                                .getName(), PORT_CONNECT_TIMEOUT);

                log.debug("serial port #" + portNumber + " openend");

                serial_port.setSerialPortParams(portBaudRate, portDataBits,
                                portStopBits, portParity);

                serial_port.disableReceiveThreshold();

                log.debug("serial parameters set");

                serial_os = serial_port.getOutputStream();
                
                if (serial_os == null)
                {
                    throw new StdException(
                                    "Could not open output stream on serial port '"
                                                    + port_identifier.getName()
                                                    + "'");
                }

                log.debug("serial output stream opened");

                serial_is = serial_port.getInputStream();

                if (serial_is == null)
                {
                    throw new StdException(
                                    "Could not open input stream on serial port '"
                                                    + port_identifier.getName()
                                                    + "'");
                }

                log.debug("serial input stream opened");

                // Initialization succeeded
            }
            catch (PortInUseException e)
            {
                throw new StdException("Port \"" + port_identifier.getName()
                                + "\" is already in use ! ", e);
            }
            catch (IOException e)
            {
                throw new StdException(e);
            }

        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            log
                            .error("Der Port mit der Nummer "
                                            + portNumber
                                            + " existiert nicht. Bitte system_konfiguration_mc35 ueberpruefen!");
            throw new StdException(e);
        }
        /*
         * catch (NoSuchPortException e) { log.error("no such port \"" +
         * port_identifier.getName() + "\". ", e); }
         */
        catch (UnsupportedCommOperationException e)
        {
            throw new StdException(e);
        }
    }

    /**
     * Erfragt eine Liste aller verf?gbaren seriellen Schnittstellen. Diese
     * Methode kann z.B. auch dazu verwendet werden in einer GUI eine
     * komfortable Schnittstellenkonfiguration zu erm?glichen.
     * 
     * @return Gefundene serielle Schnittstellen
     */
    @SuppressWarnings("unchecked")
    public static CommPortIdentifier[] getPortIdentifiers()
    {
        // Query available com ports
        if (port_identifiers == null)
        {
            // Treiber initialisieren
            comm_driver = new RXTXCommDriver();
            comm_driver.initialize();

            // Liste aller verfuegbaren seriellen Ports erfragen

            Enumeration<CommPortIdentifier> e = CommPortIdentifier
                            .getPortIdentifiers();
            List<CommPortIdentifier> listComPorts = new ArrayList<CommPortIdentifier>();

            int i = 0;

            while (e.hasMoreElements())
            {
                CommPortIdentifier commPortIdentifier = (CommPortIdentifier) e
                                .nextElement();
                log.debug("port[" + i + "]= \"" + commPortIdentifier.getName()
                                + "\" type=\""
                                + commPortIdentifier.getPortType() + "\".");
                if (commPortIdentifier.getPortType() == CommPortIdentifier.PORT_SERIAL)
                {
                    listComPorts.add(commPortIdentifier);
                }
                i++;
            }
            port_identifiers = new CommPortIdentifier[listComPorts.size()];
            listComPorts.toArray(port_identifiers);
            log.debug("found " + port_identifiers.length + " serial ports.");
        }

        return port_identifiers;
    }

    /**
     * Open serial port, initialize ME, authenticate to SIM card, create new
     * thread and enter event listening mode.
     * 
     */
    public void start() throws StdException
    {
        initializeSerial();

        initializeME();

        startEventThread();
    }

    /**
     * Start serial listener background thread
     * 
     */
    protected void startEventThread()
    {
        // Now create event listener thread
        event_thread = new Thread(getEventThreadRunnable());
        event_thread.setName("serial[" + portNumber + "] event thread");
        event_thread.start();
    }

    /**
     * Interrupt/kill serial listener thread (if it has been started before)
     * 
     */
    protected void stopEventThread()
    {
        log.debug("trying to interrupt event thread");
        int countdown = 3;

        if (event_thread != null)
        {
            event_thread.interrupt();

            while ((event_thread != null) && (event_thread.isAlive()))
            {
                try
                {
                    Thread.sleep(10);
                }
                catch (InterruptedException e)
                {
                    log
                                    .debug(
                                                    "thread interruption while waiting for serial worker to die",
                                                    e);
                }

                countdown--;

                if (countdown <= 0)
                {
                    event_thread = null;
                }
            }
            log.debug("event thread died");
        }
    }

    /**
     * Kill the event listener thread and close the serial port (if open).
     * 
     */
    public void stop()
    {
        stopEventThread();

        if (serial_port != null)
        {
            exitME();

            log.debug("Closing serial input/output stream");

            try
            {
                serial_is.close();
                serial_os.close();
            }
            catch (IOException e)
            {
                log.error("Failed to close serial input/output stream: "
                                + e.getMessage());
            }

            log.debug("closing serial port #" + portNumber);
            serial_port.close();
            log.debug("serial port #" + portNumber + " closed.");
            serial_port = null;
        }
    }

    /**
     * Suspend the current thread for 10 milliseconds (and give the ME a chance
     * to send more data)
     * 
     */
    protected void delay() throws InterruptedException
    {
        Thread.sleep(SERIAL_DELAY);
    }

}
