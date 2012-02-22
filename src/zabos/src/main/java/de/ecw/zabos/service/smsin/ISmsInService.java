package de.ecw.zabos.service.smsin;

import java.util.Vector;

import de.ecw.zabos.mc35.ShortMessage;
import de.ecw.zabos.sql.vo.SystemKonfigurationMc35VO;

/**
 * Interface zum Verarbeiten von SMSen die an das System gesendet werden
 * 
 * @author ckl
 * 
 */
public interface ISmsInService
{

    /**
     * Empfangene SMS Nachrichten in Datenbank ablegen
     * 
     * @param _konfigurationVO
     * @param _messages
     *            Vector<ShortMessage>
     */
    public void storeIncomingSMS(SystemKonfigurationMc35VO _konfigurationVO,
                    Vector<ShortMessage> _messages);

    /**
     * Verarbeitet alle ungelesenen SMS Nachrichten.
     */
    public void processSmsInbox();

}