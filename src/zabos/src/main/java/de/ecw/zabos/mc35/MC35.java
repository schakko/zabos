package de.ecw.zabos.mc35;

import gnu.io.SerialPort;

import java.io.IOException;
import java.util.Vector;

import org.apache.log4j.Logger;

import de.ecw.daemon.IDaemon;
import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.service.smsin.ISmsInService;
import de.ecw.zabos.sql.vo.SystemKonfigurationMc35VO;
import de.ecw.zabos.types.TelefonNummer;
import de.ecw.zabos.types.UnixTime;
import de.ecw.zabos.util.IWatchdog;
import de.ecw.zabos.util.TraceDog;

/**
 * Handles the GSM modem communication.
 * 
 * ("ME"=Mobile Equipment)
 * 
 * Die SMS Nachrichtenliste wird beim Start des Threads, bei Eintreffen eines
 * "+CIEV message,1" bzw. "+CIEV smsfull,1" Events bzw. alle 10s
 * (FORCED_GETMESSAGELIST_INTERVAL) abgefragt.
 * 
 * @author bsp
 * 
 */
public class MC35 extends AbstractBaseSerialIO implements IWatchdog, IDaemon
{

    private final static Logger log = Logger.getLogger(MC35.class);

    /**
     * Watchdog-Datei
     */
    private String watchdogFile = "zabos_MC35";

    /**
     * {@link SystemKonfigurationMc35VO}
     */
    private SystemKonfigurationMc35VO systemKonfigurationMC35VO;

    /**
     * Contains outgoing short messages
     */
    private Vector<ShortMessage> outgoingSms = new Vector<ShortMessage>();

    /**
     * Liefert die {@link SystemKonfigurationMc35VO}
     * 
     * @return
     */
    public SystemKonfigurationMc35VO getSystemKonfigurationMC35()
    {
        return systemKonfigurationMC35VO;
    }

    /**
     * {@link ISmsInService}
     */
    private ISmsInService smsInService;

    /**
     * PIN der SIM-Karte
     */
    private String sim_pin;

    /**
   * 
   */
    private static final boolean DEBUG_READ = true;

    /**
     * Timeout in 1/10 ms
     * 
     */
    private static final int EXPECT_TIMEOUT = 2000;

    /**
     * Delay after sending an AT cmd in milli seconds
     */
    private static final int AT_DELAY = 200;

    /**
     * Delay after sending SIM PIN1 authentication.
     * 
     */
    private static final int SIM_CARD_READY_DELAY = 15000;

    /**
     * Delay after deleting a short message (adds to AT_DELAY)
     */
    // //private static final int AT_CMGD_DELAY = 400;
    private static final int MC35_BAUD_RATE = 115200;

    private static final int MC35_DATA_BITS = SerialPort.DATABITS_8;

    private static final int MC35_PARITY = SerialPort.PARITY_NONE;

    private static final int MC35_STOP_BITS = SerialPort.STOPBITS_1;

    // private static final String GETMSG_ALL = "ALL";
    private static final String GETMSG_UNREAD = "REC UNREAD";

    private static final String GETMSG_READ = "REC READ";

    private static final String GETMSG_STO_UNSENT = "STO UNSENT";

    private static final String GETMSG_STO_SENT = "STO SENT";

    private boolean bForceGetMessageList = false;

    private static final UnixTime FORCED_GETMESSAGELIST_INTERVAL = new UnixTime(
                    1000 * 10);

    private UnixTime next_forcedGetMessageList_time;

    /**
     * Legt fest, ob der MC35-Treiber-Thread noch läuft
     */
    private DAEMON_STATUS daemonStatus = DAEMON_STATUS.OFFLINE;

    /**
     * Counter, wie oft die PIN noch eingegeben werden darf
     */
    private int pinRetriesLeft = 1;

    /**
     * Legt fest, ob beim Aufruf von {@link #stop()} die Verbindung mit dem MC35
     * getrennt wird
     */
    private boolean isCleanShutdown = true;

    /**
     * Kann von außen gelesen werden, damit sich die Telefonnumer herausfinden
     * lässt
     */
    private TelefonNummer assignedPhoneNumber = TelefonNummer.UNBEKANNT;

    // //private static final String GETMSG_READ = "REC READ";

    public MC35(ISmsInService _smsInService,
                    SystemKonfigurationMc35VO _konfigurationVO)
                    throws StdException
    {
        systemKonfigurationMC35VO = _konfigurationVO;
        portNumber = systemKonfigurationMC35VO.getComPort();
        portBaudRate = MC35_BAUD_RATE;
        portDataBits = MC35_DATA_BITS;
        portParity = MC35_PARITY;
        portStopBits = MC35_STOP_BITS;
        sim_pin = systemKonfigurationMC35VO.getPin1();
        setSmsInService(_smsInService);

        if (systemKonfigurationMC35VO != null)
        {
            setAssignedPhoneNumber(systemKonfigurationMC35VO.getRufnummer());
        }
    }

    protected void initializeME() throws StdException
    {
        isCleanShutdown = true;

        if (daemonStatus == DAEMON_STATUS.UNAVAILABLE)
        {
            throw new StdException(
                            "This MC35 thread is unavailable. Please check the PIN for MC35 and restart the application/thread manually");
        }

        try
        {
            atEchoOff();
            atNop();
            atDisableAutoAnswer();
            atReadModelId();
            atReadSerialNumber();
            pinRetriesLeft = atGetPinRetriesLeft();

            log.info("MC35 has '" + pinRetriesLeft + "' PIN retries left");

            if (atNeedAuthentication())
            {
                log.debug("ME is *not* authenticated. Sending PIN");
                atAuthenticatePin();
                int pinRetriesNow = atGetPinRetriesLeft();

                // Es wurde eine falsche PIN eingegeben
                if (pinRetriesNow < pinRetriesLeft)
                {
                    String err = "PIN for this MC35 is wrong. Please check your MC35 configuration. This modem is *unavailable* for security reasons until you restart this thread";
                    log.error(err);
                    daemonStatus = DAEMON_STATUS.UNAVAILABLE;
                    throw new StdException(err);
                }
            }
            else
            {
                log.debug("ME is authenticated");
            }

            log.debug("modem initialization finished OK.");

            // Retrieve all messages that have piled up since the last time the
            // software was run
            // 2006-05-19 CST Auslesen und Loeschen aller READ, SENT and UNSENT
            // Nachrichten, damit der SIM-Kartenspeicher nicht ueberlaeuft.
            try
            {
                atSetSMSTextMode();

                // ungelesen Nachrichten lesen/loeschen
                Vector<ShortMessage> readMsg = atGetMessageList(GETMSG_READ);
                log
                                .debug("MESSAGES - got " + readMsg.size()
                                                + " READ messages");
                debugMessages(readMsg, log);

                // gesendete Nachrichten im Speicher lesen/loeschen
                Vector<ShortMessage> sentMsg = atGetMessageList(GETMSG_STO_SENT);
                log
                                .debug("MESSAGES - got " + sentMsg.size()
                                                + " SENT messages");
                debugMessages(sentMsg, log);

                // ungesendete Nachrichten im Speicher lesen/loeschen
                Vector<ShortMessage> unsentMsg = atGetMessageList(GETMSG_STO_UNSENT);
                log.debug("MESSAGES - got " + unsentMsg.size()
                                + " UNSENT messages");
                debugMessages(unsentMsg, log);

                // neue, ungelesene Nachrichten lesen/loeschen
                Vector<ShortMessage> newMsg = atGetMessageList(GETMSG_UNREAD);
                log.debug("MESSAGES - got " + newMsg.size()
                                + " UNREAD messages");
                debugMessages(newMsg, log);

                atSetPreferredStorage();

                // Configure MC35 event reporting
                atConfigureEventReporting();
                atConfigureEventIndicators();

                log.debug("event reporting configured.");

                // neue Nachrichten speichern!
                if (smsInService != null)
                {
                    smsInService.storeIncomingSMS(systemKonfigurationMC35VO,
                                    newMsg);
                }
                else
                {
                    log.error("SmsInService ist *nicht* konfiguriert!");
                }

            }
            catch (IOException e)
            {
                throw new StdException("error while getting message list(s)", e);
            }

        }
        catch (IOException e)
        {
            throw new StdException("error while initializing modem", e);
        }
        catch (InterruptedException e)
        {
            throw new StdException("interrupted while initializing modem", e);
        }
    }

    /**
     * Return the runnable that handles MC35 events (smsfull, message)
     * 
     * @return
     */
    public Runnable getEventThreadRunnable()
    {
        return new Runnable()
        {
            public void run()
            {
                log.debug("MC35[" + portNumber + "] Treiber Thread gestartet");

                boolean bExpectLf = false;

                TraceDog traceDog = new TraceDog(getWatchdogFile());

                next_forcedGetMessageList_time = UnixTime.now();
                next_forcedGetMessageList_time
                                .add(FORCED_GETMESSAGELIST_INTERVAL);

                while ((getDaemonStatus() == DAEMON_STATUS.ONLINE))
                {
                    try
                    {
                        // log.debug("try read char");
                        consumeOutgoingSms();

                        int numAvail = serial_is.available();
                        if (numAvail > 0)
                        {
                            int c = serial_is.read();
                            if (c != -1)
                            {

                                log.debug("read char code=" + c);

                                if (bExpectLf)
                                {
                                    bExpectLf = false;
                                    if (c != '\n')
                                    {
                                        log.warn("expected \\n, got ***" + c
                                                        + "***");
                                    }
                                }
                                else
                                {
                                    if (c == '\r')
                                    {
                                        bExpectLf = true;
                                    }
                                    else if (c == '+')
                                    {
                                        // Found start of MC35 event indication
                                        // string
                                        String line = serReadLineGSM();

                                        boolean bHandled = false;
                                        if (line.startsWith("CIEV:"))
                                        {
                                            boolean bGetMsg = (line
                                                            .indexOf("message,1") != -1)
                                                            || (line
                                                                            .indexOf("smsfull,1") != -1);
                                            if (bGetMsg)
                                            {
                                                log
                                                                .debug("detected +CIEV line=\""
                                                                                + line
                                                                                + "\"");
                                                Vector<ShortMessage> newMsg = atGetMessageList(GETMSG_UNREAD);

                                                smsInService
                                                                .storeIncomingSMS(
                                                                                systemKonfigurationMC35VO,
                                                                                newMsg);

                                                bHandled = true;
                                            }
                                        }
                                        if (!bHandled)
                                        {
                                            log
                                                            .debug("unhandled serial input: \""
                                                                            + line
                                                                            + "\"");
                                        }
                                    }
                                }
                            }
                        } // else nothing to read
                        else
                        {
                            bForceGetMessageList |= (UnixTime.now()
                                            .isLaterThan(next_forcedGetMessageList_time));
                            if (bForceGetMessageList)
                            {
                                bForceGetMessageList = false;
                                next_forcedGetMessageList_time = UnixTime.now();
                                next_forcedGetMessageList_time
                                                .add(FORCED_GETMESSAGELIST_INTERVAL);
                                // +CIEV ist w?hrend des Wartens auf OK
                                // aufgetreten
                                Vector<ShortMessage> newMsg = atGetMessageList(GETMSG_UNREAD);
                                smsInService.storeIncomingSMS(
                                                systemKonfigurationMC35VO,
                                                newMsg);
                            }
                        }

                        delay();

                        traceDog.trace();

                    }
                    catch (IOException e)
                    {
                        log.error(e);
                        // IOException bedeutet, dass die Verbindung mit dem
                        // GSM-Modem nicht hergestellt werden konnte. Somit
                        // brauchen auch keine AT-Kommandos mehr gesendet werden
                        isCleanShutdown = false;
                        log
                                        .error("dirty shutdown sequence. Will *not* sent any AT commands to MC35 modem");
                        free();
                    }
                    catch (InterruptedException e)
                    {
                        log.debug("event thread got InterruptedException");
                        free();
                    }
                } // while ! isInterrupted

                log.debug("event thread has been interrupted");

            } // run()
        };
    }

    protected void exitME()
    {
        // Falls die Verbindung zum Modem verloren gegangen ist, brauchen die
        // AT-Kommandos nicht mehr gesendet werden, da sie nicht mehr ankommen
        if (!isCleanShutdown)
        {
            return;
        }

        log.debug("disabling event reporting");

        try
        {
            atDisableEventIndicators();
        }
        catch (IOException e)
        {
            log.error(e);
        }
        catch (InterruptedException e)
        {
            log.error(e);
        }

        log.debug("re-enabling echo on mode");
        try
        {
            atEchoOn();
        }
        catch (IOException e)
        {
            log.error(e);
        }
        catch (InterruptedException e)
        {
            log.error(e);
        }
    }

    /**
     * Check if the given String matches the standard AT "ERR" response and
     * raise an IOException
     * 
     * @param _s
     * @throws IOException
     */
    private void atCheckErr(String _s) throws IOException, InterruptedException
    {
        if (_s.compareTo("ERR") == 0)
        {
            throw new IOException("AT error!");
        }
    }

    /**
     * Send AT command to ME
     * 
     * @param _cmd
     * @throws IOException
     */
    private void at(String _cmd) throws IOException, InterruptedException
    {
        String cmd = "AT" + _cmd + "\r\n";
        serial_os.write(cmd.getBytes());
        serial_os.flush();
        log.debug("SENT: \"" + cmd + "\"");
        Thread.sleep(AT_DELAY);
    }

    /**
     * Schedules a SMS for sending.<br />
     * <strong>Notice:</strong> {@link ShortMessage#getBody()} will <strong>not
     * be</strong> converted to ASCII!
     * 
     * @param _shortMessage
     */
    public void scheduleOutgoingSms(ShortMessage _shortMessage)
    {
        log.debug("Scheduling SMS for sending...");

        if (!outgoingSms.contains(_shortMessage))
        {
            outgoingSms.add(_shortMessage);
        }
    }

    /**
     * Sends any {@link ShortMessage} from {@link #outgoingSms} container.
     * Afterwards the {@link #outgoingSms} vector is cleared.
     * 
     * @throws IOException
     * @throws InterruptedException
     */
    private void consumeOutgoingSms() throws IOException, InterruptedException
    {
        for (ShortMessage shortMessage : outgoingSms)
        {
            atSendSms(shortMessage);
        }

        outgoingSms.clear();
    }

    /**
     * Sends a short message
     * 
     * @param shortMesage
     * @throws IOException
     * @throws InterruptedException
     */
    private void atSendSms(ShortMessage shortMesage) throws IOException, InterruptedException
    {
        log.debug("Sending SMS...");
        at("+CMGS=\"" + shortMesage.getPhoneNumber() + "\"");
        expectLineBreak();
        serial_os.write(shortMesage.getBody().getBytes());
        // Ctrl+Z
        serial_os.write(26);
        serial_os.flush();
        expectUntilOk();
    }

    /**
     * Consumes '\r\n' sequence from ME
     * 
     * @throws IOException
     *             , InterruptedException
     */
    private void expectLineBreak() throws IOException, InterruptedException
    {
        expectLine("");
    }

    /**
     * Read '\r\n' terminated line from ME and compare to _line. Raises an
     * IOException in case there is no match.
     * 
     * @param _line
     * @throws IOException
     *             , InterruptedException
     */
    private void expectLine(String _line) throws IOException, InterruptedException
    {
        String line = serReadLineGSM();
        log.debug("read line \"" + line + "\"");
        verifyLine(line, _line);
    }

    /**
     * Check whether _have matches _expect and raise an IOException if the
     * strings differ.
     * 
     * @param _have
     * @param _expect
     * @throws IOException
     *             , InterruptedException
     */
    private void verifyLine(String _have, String _expect) throws IOException, InterruptedException
    {
        if (_have.compareTo(_expect) != 0)
        {
            throw new IOException("Expected line \"" + _expect + "\", got \""
                            + _have + "\".");
        }
    }

    /**
     * Expect standard "\r\nOK\r\n" response from ME. Raises an IOException if
     * the response differs.
     * 
     * @throws IOException
     *             , InterruptedException
     */
    private void expectOk() throws IOException, InterruptedException
    {
        expectLineBreak();
        expectLine("OK");
    }

    /**
     * Nach dem Einschalten des Eventreportings antwortet das MC35 manchmal mit
     * einer Liste von +CIEV Zeilen bevor es OK schickt.
     * 
     * @throws IOException
     * @throws InterruptedException
     */
    private void expectUntilOk() throws IOException, InterruptedException
    {
        boolean bExpect = true;
        int timeOutCtr = 0;
        while (bExpect)
        {
            String line = serReadLineGSM();
            bExpect = (line.compareTo("OK") != 0);
            if (timeOutCtr++ > EXPECT_TIMEOUT)
            {
                log.debug("!! timed out !!");
                throw new IOException("timeout");
            }
        }
    }

    /**
     * Sets the preferred memory for received SMS messages.
     * 
     * The SIM memory is several times faster than the ME memory
     * 
     */
    private void atSetPreferredStorage() throws IOException, InterruptedException
    {
        at("+CPMS=SM,SM,SM");
        expectUntilOk();
        log.debug("Set preferred storage to SIM card memory");
    }

    /**
     * Enable MC35 event reporting for smsfull and message events
     * 
     * @throws IOException
     *             , InterruptedException
     */
    private void atConfigureEventReporting() throws IOException, InterruptedException
    {
        at("+CMER=2,0,0,2");
        expectOk();
    }

    /**
     * Enable MC35 event indicators for smsfull and message events. This should
     * be the last cmd before entering the event thread.
     * 
     * @throws IOException
     *             , InterruptedException
     */
    private void atConfigureEventIndicators() throws IOException, InterruptedException
    {
        at("+CIND=0,0,0,0,1,0,0,1");
        expectUntilOk();
    }

    private void atDisableEventIndicators() throws IOException, InterruptedException
    {
        at("+CIND=0,0,0,0,0,0,0,0");
        expectOk();
    }

    /**
     * Read a line (terminated with \r\n from serial port
     * 
     * @return The line String not including the \r\n characters
     * @throws IOException
     *             , InterruptedException
     */
    private String serReadLineGSM() throws IOException, InterruptedException
    {
        int timeOutCtr = 0;

        boolean bExpectLf = false;
        boolean bExpectMultiByte = false;

        StringBuffer line = new StringBuffer();

        while (true)
        {

            int num = serial_is.available();

            if (num > 0)
            {
                for (int i = 0; i < num; i++)
                {
                    char c = (char) (0xFF & serial_is.read());

                    if (bExpectMultiByte)
                    {
                        c = extendedGsmToUnicodeChar(c);
                        bExpectMultiByte = false;
                        line.append(c);
                    }
                    else
                    {
                        if (c == 0x1B) // GSM extended character escape
                        {
                            bExpectMultiByte = true;
                        }
                        else
                        {
                            c = gsmToUnicodeChar(c);
                            if (c != '\r')
                            {
                                line.append(c);
                            }
                        }
                    }

                    byte[] chars = new byte[1];
                    chars[0] = (byte) c;
                    if (DEBUG_READ)
                    {
                        log.debug(getSystemKonfigurationMC35().getRufnummer()
                                        .toString()
                                        + " : read char \'"
                                        + new String(chars)
                                        + "\' (code=" + ((int) c) + ").");
                    }

                    if (bExpectLf)
                    {
                        if (c == '\n')
                        {
                            // Line is complete
                            return line.substring(0, line.length() - 1);
                        }
                    }
                    else
                    {
                        bExpectLf = (c == '\r');
                    }
                }
            }
            else
            {
                // Give serial line some time to get next char ready
                delay();
                if (timeOutCtr++ > EXPECT_TIMEOUT)
                {
                    log.debug("!! timed out !!");
                    throw new IOException("timeout");
                }
            }
        }
    }

    /**
     * Converts a GSM encoded String to a Java (unicode) String.
     * 
     * @return The line String not including the \r\n characters
     */
    private static String gsmToUnicodeString(String _orig)
    {
        boolean bExpectMultiByte = false;

        StringBuffer line = new StringBuffer();

        int origLength = _orig.length();

        int k = 0;
        while (k < origLength)
        {
            char c = _orig.charAt(k++);

            if (bExpectMultiByte)
            {
                c = extendedGsmToUnicodeChar(c);
                bExpectMultiByte = false;
                line.append(c);
            }
            else
            {
                if (c == 0x1B) // GSM extended character escape
                {
                    bExpectMultiByte = true;
                }
                else
                {
                    c = gsmToUnicodeChar(c);
                    line.append(c);
                }
            }
        }
        return line.toString();
    }

    /**
     * Read a single GSM encoded char from the serial line
     * 
     * @return
     * @throws IOException
     *             , InterruptedException
     */
    /*
     * private char serReadGSM() throws IOException { boolean bExpectMultiByte =
     * false;
     * 
     * int timeOutCtr = 0;
     * 
     * while (true) { int num = serial_is.available();
     * 
     * if (num > 0) { for (int i = 0; i < num; i++) { char c = (char) (0xFF &
     * serial_is.read());
     * 
     * if (bExpectMultiByte) { c = extendedGsmToASCII(c); return c; } else { if
     * (c == 0x1B) // GSM extended character escape { bExpectMultiByte = true; }
     * else { c = gsmToASCII(c); return c; } }
     * 
     * byte[] chars = new byte[1]; chars[0] = (byte) c; if (DEBUG_READ) {
     * log.debug("read GSM char \'" + new String(chars) + "\' (code=" + ((int)
     * c) + ")."); } } } else { // Give serial line some time to get next char
     * ready delay(); if (timeOutCtr++ > EXPECT_TIMEOUT) {
     * log.debug("!! timed out !!"); throw new IOException("timeout"); } } } }
     */

    /**
     * Convert the given extended GSM character to a Unicode character. Extended
     * GSM characters are prefixed by the 0x1B (27) escape character.
     * 
     * @param _c
     *            Extended GSM character
     * @return Unicode character
     */
    private static char extendedGsmToUnicodeChar(char _c)
    {
        switch (_c)
        {
            case 0x0A:
                return 0xC; // form feed
            case 0x14:
                return 0x5E; // CIRCUMFLEX ACCENT
            case 0x28: // ????
            case 0xe4: // mit handy getestet
                return 0x7B; // left curly bracket
            case 0x29:
                return 0x7D; // RIGHT CURLY BRACKET
            case 0x2F:
                return 0x5C; // REVERSE SOLIDUS
            case 0x3C:
                return 0x5B; // LEFT SQUARE BRACKET
            case 0x3D:
                return 0x7E; // TILDE
            case 0x3E:
                return 0x5D; // RIGHT SQUARE BRACKET
            case 0x40:
                return 0x7C; // VERTICAL LINE
            case 0x65:
                return 'e'; // EURO SIGN
            default:
                return ' '; // unrecognized GSM character
        }
    }

    /**
     * Translate a (non-extended) GSM character to Unicode
     * 
     * @param _c
     *            GSM character
     * @return Unicode character
     */
    private static char gsmToUnicodeChar(char _c)
    {
        // Translate GSM to ASCII charset
        switch (_c)
        {
            case 0x00:
                return '@';
            case 0x01:
                return 0xA3; // Pound
            case 0x02:
                return 0x24; // Dollar
            case 0x03:
                return 0xA5; // Yen
            case 0x11:
                return '_'; // low line
            case 0x1C:
                return 0xC6;
            case 0x1D:
                return 0xE6;
            case 0x1E:
                return '?';
            case 0x24:
                return 0xA4; // currency sign
            case 0x40:
                return 0xA1; // inverted exclamation mark
            case 0x5B:
                return 0xC4; // ?
            case 0x5C:
                return 0xD6; // ?
            case 0x5E:
                return 0xDC; // ?
            case 0x5F:
                return 0xA7; // section sign
            case 0x60:
                return 0xBF; // inverted question mark
            case 0x7B:
                return 0xE4; // ?
            case 0x7C:
                return 0xF6; // ?
            case 0x7E:
                return 0xFC; // ?
        }
        return _c;
    }

    /**
     * Converts the given String from GSM or ASCII-Unicode encoding to a Java
     * unicode String The encoding type is determined by looking at the String
     * content: - If the string length is a multiple of 4 and the String only
     * contains the characters [0..9,A..F] it is taken for a ASCII-encoded
     * Unicode String encoded ("0020004100420043" style) - Otherwise the message
     * is assumed to use the (extended) GSM encoding
     * 
     * @param _s
     * @return
     */
    private static String stringToUnicode(String _s)
    {
        int l = _s.length();
        if ((l & 3) == 0)
        {
            // Stringl?nge ist ein Vielfaches von 4
            // Nun testen ob der String nur 0..9,A-Z Zeichen enth?lt
            boolean bUnicode = true;
            int i = l;
            while (bUnicode && --i >= 0)
            {
                char c = _s.charAt(i);
                bUnicode &= ((c >= '0') && (c <= '9'))
                                || ((c >= 'A') && (c <= 'F'));
            }
            if (bUnicode)
            {
                StringBuffer sb = new StringBuffer(l >> 2);
                i = 0;
                for (i = 0; i < l; i += 4)
                {
                    String hex = _s.substring(i, i + 4);
                    int c = Integer.parseInt(hex, 16);
                    sb.append((char) c);
                }
                return sb.toString();
            }
        }
        // Kein UniCode -> GSM nach Unicode konvertieren
        return gsmToUnicodeString(_s);
    }

    /**
     * Send no-op AT command (just "AT") The ME must respond with "\r\nOK\r\n"
     * 
     * @throws IOException
     *             , InterruptedException
     */
    private void atNop() throws IOException, InterruptedException
    {
        at("");
        expectOk();
    }

    /**
     * Turn off ME echo mode. We need to distinguish between two valid reponses:
     * 1) The ME responds with ATEO\r\nOK\r\n if it was in echo-on mode 2) The
     * ME responds with just \r\nOK\r\n if it was in echo-off mode
     * 
     * @throws IOException
     *             , InterruptedException
     */
    private void atEchoOff() throws IOException, InterruptedException
    {
        at("E0");
        String s = serReadLineGSM();
        if (s.compareTo("ATE0") == 0)
        {
            // Device was in echo on mode
            log.debug("device was in echo on mode");
            expectLine("OK");
        }
        else
        {
            log.debug("device was in echo off mode");
            verifyLine(s, "");
            expectLine("OK");
        }
    }

    /**
     * Turn on ME echo mode
     * 
     * @throws IOException
     *             , InterruptedException
     */
    private void atEchoOn() throws IOException, InterruptedException
    {
        at("E1");
        expectOk();
    }

    /**
     * Disable auto-answering of incoming calls
     * 
     * @throws IOException
     *             , InterruptedException
     */
    private void atDisableAutoAnswer() throws IOException, InterruptedException
    {
        at("S0=0");
        expectOk();
    }

    /**
     * Queries the ME model identifier
     * 
     * @throws IOException
     *             , InterruptedException
     */
    private void atReadModelId() throws IOException, InterruptedException
    {
        at("+GMM");
        expectLineBreak();
        String modelId = serReadLineGSM();
        atCheckErr(modelId);
        log.debug("device model=\"" + modelId + "\"");
        expectOk();
    }

    /**
     * Queries the ME serial number
     * 
     * @throws IOException
     *             , InterruptedException
     */
    private void atReadSerialNumber() throws IOException, InterruptedException
    {
        at("+GSN");
        expectLineBreak();
        String serNumber = serReadLineGSM();
        atCheckErr(serNumber);
        log.debug("device serial=\"" + serNumber + "\"");
        expectOk();
    }

    /**
     * Checks whether the SIM card awaits PIN authentication.
     * 
     * @return true if the PIN has not been send to the ME, yet
     * @throws IOException
     *             , InterruptedException
     */
    private boolean atNeedAuthentication() throws IOException, InterruptedException
    {
        at("+CPIN?");
        expectLineBreak();
        String s = serReadLineGSM();
        expectOk();
        if (s.compareTo("+CPIN: SIM PIN") == 0)
        {
            return true;
        }
        else if (s.compareTo("+CPIN: READY") == 0)
        {
            return false;
        }
        throw new IOException("unhandled +CPIN return \"" + s + "\"");
    }

    /**
     * Checks the SIM retry counter
     * 
     * @return number of retries left
     * @throws IOException
     * @throws InterruptedException
     */
    private int atGetPinRetriesLeft() throws IOException, InterruptedException
    {
        at("^SPIC");
        expectLineBreak();
        String s = serReadLineGSM();
        expectOk();
        String expectedAnswer = "SPIC: ";

        int r = 0;

        // Das Akzent-Zeichen wird anscheinend falsch interpretiert. Kann nicht
        // sicher sein, dass ^SPIC oder ÜSPIC zurückkommt.
        int idxStart = s.indexOf(expectedAnswer);

        if (idxStart >= 0)
        {
            String retriesLeft = s
                            .substring(idxStart + expectedAnswer.length());

            r = new Integer(retriesLeft).intValue();
        }

        return r;
    }

    /**
     * Send PIN to SIM card
     * 
     * @throws IOException
     *             , InterruptedException
     */
    private void atAuthenticatePin() throws IOException, InterruptedException
    {
        at("+CPIN=\"" + sim_pin + "\"");
        expectOk();

        log.debug("waiting for SIM card to become ready");

        try
        {
            Thread.sleep(SIM_CARD_READY_DELAY);
        }
        catch (InterruptedException e)
        {
            log.error(e);
        }
    }

    /**
     * Switch the ME from PDU (ring tones etc..) to text message mode
     * 
     * @throws IOException
     *             , InterruptedException
     */
    private void atSetSMSTextMode() throws IOException, InterruptedException
    {
        at("+CMGF=1");
        expectOk();
    }

    /**
     * Retrieve a list of all short messages stored in both the ME and SIM card.
     * 
     * 2006-05-23 CST Major rewrite (Issue #247)
     * 
     * @param _type
     *            One of GETMSG_ALL, GETMSG_READ, GETMSG_UNREAD
     * @throws IOException
     *             , InterruptedException
     * @return Vector<ShortMessage>
     */
    private Vector<ShortMessage> atGetMessageList(String _type) throws IOException, InterruptedException
    {
        /**
         * Holds all messages that are queued for further processing ("inbox")
         */
        Vector<ShortMessage> new_messages = new Vector<ShortMessage>();

        at("+CMGL=\"" + _type + "\"");
        expectLineBreak();

        boolean bExpectMessage = false;
        boolean bGotEmptyLine = false;
        ShortMessage msg = null;
        String msgText = "";
        String line = "";

        do
        {
            // Zeile auslesen
            line = serReadLineGSM();

            if (line.startsWith("+CMGL:"))
            {
                // Neue Nachricht
                if (bExpectMessage)
                {
                    // vorherige Nachricht ablegen
                    log.debug("got message text \"" + msgText + "\"");
                    msg.setBody(msgText);
                    new_messages.add(msg);
                    msg = null;
                    msgText = "";
                }
                log.debug("got message header \"" + line + "\"");
                msg = newMessage(line);

                // CKL 2010-09-13: Anpassungen für Vodafone-SIM-Karten
                if (msg != null)
                {
                    bExpectMessage = true;
                }
            }
            else
            {

                // Haben wir eine Leerzeile und nun OK oder nur OK ohne
                // irgendwas, dann
                // sind wir fertig.
                if ((line.compareTo("OK") == 0)
                                && ((bGotEmptyLine) || (bExpectMessage == false)))
                {
                    // letzte Nachricht ablegen
                    if (bExpectMessage)
                    {
                        log.debug("got message text \"" + msgText + "\"");
                        msg.setBody(msgText);
                        new_messages.add(msg);
                    }

                    log.debug("got " + new_messages.size() + " messages");

                    // Empfangene Nachrichten aus dem
                    // GSM-Modem/SIM-Karten-Speicher
                    // l?schen
                    if (new_messages.size() > 0)
                    {
                        log.debug("deleting messages...");
                        atDeleteMessages(new_messages);
                    }

                    // Fertig
                    return new_messages;
                }

                // Leerzeilen erkennen
                if (line.compareTo("") == 0)
                {
                    bGotEmptyLine = true;
                }
                else
                {
                    bGotEmptyLine = false;

                    if (line.startsWith("+CIEV"))
                    {
                        // Event ignorieren
                        bForceGetMessageList = true;
                    }
                    else
                    {
                        // Zeile an Nachricht anh?ngen
                        line = stringToUnicode(line);

                        if (msgText.length() > 0)
                        {
                            msgText = msgText + " ";
                        }
                        msgText = msgText + line;
                    }
                }
            }
        }
        while (true);
    }

    /**
     * Delete the given short messages
     * 
     * @param _messages
     *            Vector<ShortMessage>
     */
    private void atDeleteMessages(Vector<ShortMessage> _messages) throws IOException, InterruptedException
    {
        int l = _messages.size();
        for (int i = 0; i < l; i++)
        {
            ShortMessage msg = (ShortMessage) _messages.get(i);
            at("+CMGD=" + msg.getId());
            // Thread.sleep(AT_CMGD_DELAY);
            expectUntilOk();
        }
        log.debug("deleted " + l + " messages");
        // _messages.clear();
    }

    /**
     * Parse the given header and create a new ShortMessage object. The header
     * uses the following format:
     * 
     * <pre>
     * +CMGL: 11,"REC READ","+491719562825",,"06/01/24,16:09:01+04"
     * </pre>
     * 
     * If CMGL header is <strong>not</strong> standard compliant, a
     * {@link ShortMessage} with current date and ID 1 will be returned. This
     * method does <strong>not</strong> throw any exception like
     * {@link ArrayIndexOutOfBoundsException}
     * 
     * @param _atHeader
     * @return
     */
    protected ShortMessage newMessage(String _cmglHeader)
    {
        int lastIdx = 0;
        int idx = _cmglHeader.indexOf(',', 0);
        int state = 0;
        String substrings[] = new String[6];

        try
        {
            while (idx != -1)
            {
                substrings[state++] = _cmglHeader.substring(lastIdx, idx);
                lastIdx = idx + 1;
                idx = _cmglHeader.indexOf(',', lastIdx);
            }
            substrings[5] = _cmglHeader.substring(lastIdx);
        }
        catch (Exception e)
        {
            log.error("Could not parse parts of CMGL header [" + _cmglHeader
                            + "]: " + e.getMessage());
        }
        
        // substrings[0]: +CMGL: 11
        // substrings[1]: "REC READ"
        // substrings[2]: "+491719562825"
        // substrings[3]:
        // substrings[4]: "06/01/24
        // substrings[5]: 16:09:01+04"

        ShortMessage msg = null;

        // CKL 2010-09-13: Anpassungen Vodafone-SIM-Karten
        if ((substrings[1] != null)
                        && (substrings[1].toLowerCase().contains("rec unread")))
        {
            String id = "0";
            String date = "";
            String phoneNumber = "";

            // Use exception handling for every field for the case the CMGL is
            // not standard compliant. This method MUST NOT
            // throw any exception
            try
            {
                id = substrings[0].substring(7);
            }
            catch (Exception e)
            {
                log.error("Failed to extract id from message header \""
                                + _cmglHeader + "\" [" + e.getMessage() + "]");
            }
            try
            {
                phoneNumber = substrings[2].substring(1,
                                substrings[2].length() - 1);
            }
            catch (Exception e)
            {
                log
                                .error("Failed to extract phone number from message header \""
                                                + _cmglHeader
                                                + "\" ["
                                                + e.getMessage() + "]");
            }

            try
            {
                date = substrings[4].substring(1)
                                + ","
                                + substrings[5].substring(0, substrings[5]
                                                .length() - 1);
            }
            catch (Exception e)
            {
                log.error("Failed to extract date from message header \""
                                + _cmglHeader + "\"" + e.getMessage() + "]");
            }

            msg = new ShortMessage(id, phoneNumber, date);
        }

        return msg;
    }

    /**
     * Prints debug output for all messages in new_messages Vector
     * 
     */
    public synchronized static void debugMessages(
                    Vector<ShortMessage> _messages, Logger log)
    {
        Vector<ShortMessage> v = _messages;
        int l = v.size();
        for (int i = 0; i < l; i++)
        {
            ShortMessage msg = (ShortMessage) v.get(i);
            log.debug("----------------------------------------");
            msg.debug();
        }
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

    /*
     * (non-Javadoc)
     * 
     * @see de.ecw.zabos.util.IWatchdog#setWatchdogFile(java.lang.String)
     */
    final public void setWatchdogFile(String watchdogFile)
    {
        this.watchdogFile = watchdogFile;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.ecw.zabos.util.IWatchdog#getWatchdogFile()
     */
    final public String getWatchdogFile()
    {
        return watchdogFile;
    }

    /**
     * Wird synchronisiert, da es sonst beim Beenden zu Problemen kommen könnte
     */
    synchronized public DAEMON_STATUS getDaemonStatus()
    {
        return daemonStatus;
    }

    /**
     * Ruft {@link #exitME()} auf und danach {@link #stop()}
     */
    public void free()
    {
        // Wenn der Thread gegenwärtig überhaupt noch läuft
        if ((getDaemonStatus() == DAEMON_STATUS.ONLINE))
        {
            daemonStatus = DAEMON_STATUS.SHUTTING_DOWN;
            exitME();
            super.stop();

            log.debug("MC35[" + portNumber + "] Treiber Thread gestoppt");
            daemonStatus = DAEMON_STATUS.OFFLINE;
        }
    }

    public void init() throws StdException
    {
        daemonStatus = DAEMON_STATUS.STARTING_UP;

        try
        {
            start();
            daemonStatus = DAEMON_STATUS.ONLINE;
        }
        catch (StdException e)
        {
            // Falls bei der Initialisierung ein Fehler aufgetreten ist,
            // stoppen. Sonst kann es sein, dass der COM-Port weiterhin belegt
            // ist.
            isCleanShutdown = false;
            stop();

            daemonStatus = DAEMON_STATUS.OFFLINE;
            throw e;
        }
    }

    public void setAssignedPhoneNumber(TelefonNummer assignedPhoneNumber)
    {
        this.assignedPhoneNumber = assignedPhoneNumber;
    }

    public TelefonNummer getAssignedPhoneNumber()
    {
        return assignedPhoneNumber;
    }
}
