package de.ecw.zabos.alarm.consumer.zvei.daemon;

import gnu.io.SerialPort;

import java.io.IOException;

import org.apache.log4j.Logger;

import de.ecw.daemon.IDaemon;
import de.ecw.zabos.alarm.consumer.zvei.ZveiConsumer;
import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.mc35.AbstractBaseSerialIO;
import de.ecw.zabos.sql.resource.DBResource;
import de.ecw.zabos.types.UnixTime;

/**
 * Dieser Daemon steuert den Fünfton-Folgeruf-Empfänger an
 * 
 * @author bsp
 * 
 */
public class ZveiSerialHardwareDaemon extends AbstractBaseSerialIO implements IDaemon
{

    // Zeitintervall in Millisekunden waehrend dem der serielle Treiber die
    // Queue
    // mit neuen Daten fuellen kann (250 = 1/4 Sekunde)
    private static final long POLLING_INTERVAL = 50;

    private static final int FUENFTON_BAUD_RATE = 9600;

    private static final int FUENFTON_DATA_BITS = SerialPort.DATABITS_8;

    private static final int FUENFTON_PARITY = SerialPort.PARITY_NONE;

    private static final int FUENFTON_STOP_BITS = SerialPort.STOPBITS_1;

    private final static Logger log = Logger.getLogger(ZveiSerialHardwareDaemon.class);

    /**
     * Zeitspanne in Millisekunden innerhalb derer auf ein "W-OK" vom 5Ton-Modul
     * gewartet wird (default ist 7 Minuten)
     * 
     */
    private static final UnixTime WOK_TIMEOUT = new UnixTime(7 * 60 * 1000);

    // Zeitpunkt, bis zu dem spätestens der nächste "W-OK" String gelesen werden
    // muss. Der Zeitpunkt wird erstmalig beim Starten des Daemons berechnet;
    // danach dann immer wenn eine 'W-OK\n\r' Zeichenkette empfangen wurde
    private UnixTime next_wok_time;

    // true solange der Daemon läuft
    private DAEMON_STATUS daemonStatus = DAEMON_STATUS.OFFLINE;

    // Fuer Zustandsautomaten: Status "Warte auf OK oder 5TonRuf"
    private static final int ST_AWAIT_WOK_OR_5TON = 0;

    // Fuer Zustandsautomaten: Status "Lese 'W-OK\n\r' Zeichenkette"
    private static final int ST_WOK = 1;

    // Fuer Zustandsautomaten: Status "Lese 12345\n\r Zeichenkette"
    private static final int ST_5TON = 2;

    // Fuer Zustandsautomaten: Status "Lese bis Ende der Zeile"
    private static final int ST_GARBAGE = 3;

    // Speichert FuenfTonfolge waehrend des Empfangs zwischen
    private int[] current_fuenfton = new int[5];

    private DBResource dbresource;

    private ZveiConsumer zveiConsumer;

    /**
     * Konstruktor
     * 
     * @param _dbResource
     *            Unbedingt eine neue Datenbankinstanz übergeben, da diese
     *            Klasse gethreadet wird.
     * @param _zveiConsumer
     */
    public ZveiSerialHardwareDaemon(DBResource _dbResource, ZveiConsumer _zveiConsumer)
    {
        dbresource = _dbResource;
        zveiConsumer = _zveiConsumer;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.ecw.zabos.daemon.IDaemon#init()
     */
    public void init() throws StdException
    {
        daemonStatus = DAEMON_STATUS.STARTING_UP;

        Integer portNr = dbresource.getDaoFactory().getSystemKonfigurationDAO()
                        .readKonfiguration().getCom5Ton();

        if (portNr != null)
        {
            portNumber = portNr.intValue();

            portBaudRate = FUENFTON_BAUD_RATE;
            portDataBits = FUENFTON_DATA_BITS;
            portParity = FUENFTON_PARITY;
            portStopBits = FUENFTON_STOP_BITS;

            // Treiber Thread starten
            try
            {
                start();

                log.debug("5TonDaemon wurde gestartet");
            }
            catch (StdException e)
            {
                daemonStatus = DAEMON_STATUS.OFFLINE;
                log.error(e.getMessage());
            }
        }
        else
        {
            log.warn("keine Portnummer fuer 5Ton-Modul definiert");
        }

    }

    /**
     * Fuenfton Daemon stoppen und Datenbankresource freigeben
     */
    public void free()
    {
        stop();
        log.warn("5TonDaemon wurde beendet");
    }

    /**
     * Naechsten Timeout Zeitpunkt berechnen
     */
    private void calcNextTimeout()
    {
        next_wok_time = UnixTime.now();
        next_wok_time.add(WOK_TIMEOUT);
    }

    /**
     * Einen frisch empfangenen 5TonfolgeRuf weiterverarbeiten (Pruefung auf
     * Probealarmzeitfenster und evtl. Ausloesen der damit verbundenen
     * Alarmschleife)
     * 
     * @throws StdException
     */
    private void notify5TonAvailable() throws StdException
    {
        StringBuffer sb = new StringBuffer(5);
        sb.append((char) current_fuenfton[0]);
        sb.append((char) current_fuenfton[1]);
        sb.append((char) current_fuenfton[2]);
        sb.append((char) current_fuenfton[3]);
        sb.append((char) current_fuenfton[4]);
        String fuenfton = sb.toString();

        // An Consumer delegieren
        zveiConsumer.process5Ton(fuenfton);

    }

    /**
     * Wird von BaseSerialIO aufgerufen; gibt das eigentliche Daemon-Runnable
     * zurueck.
     */
    protected Runnable getEventThreadRunnable()
    {
        return new Runnable()
        {
            public void run()
            {
                daemonStatus = DAEMON_STATUS.ONLINE;

                // Initialen W-OK Timeout-Zeitpunkt berechnen
                calcNextTimeout();

                int state = ST_AWAIT_WOK_OR_5TON;
                int state_index = 0;

                while ((getDaemonStatus() == DAEMON_STATUS.ONLINE))
                {
                    try
                    {
                        int numAvail;
                        // Den aktuellen seriellen Empfangsbuffer auslesen
                        if (serial_is != null)
                        {
                            for (numAvail = serial_is.available(); (numAvail > 0); numAvail--)
                            {
                                int c = serial_is.read();
                                log.debug("read char 5ton: " + c + " char="
                                                + ((char) c));
                                switch (state)
                                {
                                    case ST_AWAIT_WOK_OR_5TON:
                                        if (c == 'W')
                                        {
                                            state = ST_WOK;
                                            state_index = 1;
                                        }
                                        else
                                        {
                                            // Start eines 5Ton Rufs?
                                            if (c == 'T')
                                            {
                                                state = ST_5TON;
                                                state_index = 0;
                                            }
                                            else
                                            {
                                                // Hier koennen wird landen wenn
                                                // waehrend des Starts des
                                                // 5Ton-Daemons gerade ein
                                                // 'W-OK\n\r' bzw. Fuenftonruf
                                                // empfangen wird.
                                                // Der "GARBAGE" State liest und
                                                // verwirft alle
                                                // Eingabezeichen bis zum
                                                // Ende der Zeile
                                                state = ST_GARBAGE;
                                                if (c == '\r')
                                                    state_index = 1;
                                                else if (c == '\n')
                                                    state = ST_AWAIT_WOK_OR_5TON;
                                                else
                                                    state_index = 0;
                                            }
                                        }
                                        break;
                                    case ST_WOK:
                                        switch (state_index)
                                        {
                                            case 1: // consume '-'
                                                state_index++;
                                                break;
                                            case 2: // consume 'O'
                                                state_index++;
                                                break;
                                            case 3: // consume 'K'
                                                state_index++;
                                                break;
                                            case 4: // consume '\r'
                                                state_index++;
                                                break;
                                            case 5: // consume '\n'
                                                state = ST_AWAIT_WOK_OR_5TON;
                                                log.debug("W-OK empfangen");
                                                calcNextTimeout();
                                                break;
                                        }
                                        break;

                                    case ST_5TON:
                                        switch (state_index)
                                        {
                                            case 0: // parse <digit0>
                                                current_fuenfton[0] = c;
                                                state_index++;
                                                break;
                                            case 1: // parse <digit1>
                                                current_fuenfton[1] = c;
                                                state_index++;
                                                break;
                                            case 2: // parse <digit2>
                                                current_fuenfton[2] = c;
                                                state_index++;
                                                break;
                                            case 3: // parse <digit3>
                                                current_fuenfton[3] = c;
                                                state_index++;
                                                break;
                                            case 4: // parse <digit4>
                                                current_fuenfton[4] = c;
                                                state_index++;
                                                break;
                                            case 5: // consume '\r'
                                                state_index++;
                                                break;
                                            case 6: // consume '\n'
                                                state = ST_AWAIT_WOK_OR_5TON;
                                                notify5TonAvailable();
                                                calcNextTimeout();
                                                break;
                                        }
                                        break;

                                    case ST_GARBAGE:
                                        if ((state_index == 0) && (c == '\r'))
                                        {
                                            state_index++;
                                        }
                                        else if (state_index == 1)
                                        {
                                            state = ST_AWAIT_WOK_OR_5TON;
                                        }
                                }
                                break;
                            } // while numAvail

                            // Dem Thread ein wenig Zeit einraeumen damit neue
                            // Daten
                            // empfangen
                            // werden koennen
                            // und die anderen Threads nicht blockiert werden
                            Thread.sleep(POLLING_INTERVAL);

                            // Pruefung ob seit dem letzten "W-OK\n\r" mehr Zeit
                            // als
                            // erlaubt
                            // vergangen ist
                            UnixTime jetzt = UnixTime.now();
                            if (jetzt.isLaterThan(next_wok_time))
                            {
                                // Es wurde kein "W-OK" String innerhalb der
                                // WOK_TIMEOUT Spanne
                                // gelesen.
                                log.error(new StdException(
                                                "W-OK Timeout abgelaufen. Es wurde seit "
                                                                + WOK_TIMEOUT
                                                                + " Sekunden kein W-OK String mehr vom Fuenftonmodul empfangen."));
                                log.debug("Jetzt = "
                                                + jetzt.getTimeStamp()
                                                + " TimeoutZeitpunkt="
                                                + next_wok_time.getTimeStamp()
                                                + " Differenz="
                                                + (jetzt.getTimeStamp() - next_wok_time
                                                                .getTimeStamp()));
                                daemonStatus = DAEMON_STATUS.OFFLINE;
                            }
                        }
                    }
                    catch (StdException e)
                    {
                        // Hier laden wir wenn ein VO-Setter aus process5Ton()
                        // fehlschlaegt
                        log.error(e);
                        daemonStatus = DAEMON_STATUS.OFFLINE;
                    }
                    catch (InterruptedException e)
                    {
                        // Hier landen wir wenn der Thread unvorhergesehen
                        // unterbrochen wird
                        log.error(e);
                        daemonStatus = DAEMON_STATUS.OFFLINE;
                    }
                    catch (IOException e)
                    {
                        // Beim Lesen von der seriellen Schnittstelle ist etwas
                        // schiefgegangen
                        log.error(e);
                        daemonStatus = DAEMON_STATUS.OFFLINE;
                    }
                }
            }
        };
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.ecw.zabos.daemon.IDaemon#isRunning()
     */
    synchronized public DAEMON_STATUS getDaemonStatus()
    {
        return daemonStatus;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.ecw.zabos.sms.mc35.BaseSerialIO#exitME()
     */
    protected void exitME()
    {
        // Device braucht kein Shutdown
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.ecw.zabos.sms.mc35.BaseSerialIO#initializeME()
     */
    protected void initializeME() throws StdException
    {
        // Device braucht keine Initialisierung
    }

    public String toString()
    {
        return "Fuenfton-Empfaenger";
    }
}
