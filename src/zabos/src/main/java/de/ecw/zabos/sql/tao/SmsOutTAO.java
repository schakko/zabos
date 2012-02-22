package de.ecw.zabos.sql.tao;

import org.apache.log4j.Logger;

import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.sql.dao.PersonInAlarmDAO;
import de.ecw.zabos.sql.dao.SmsOutDAO;
import de.ecw.zabos.sql.dao.TelefonDAO;
import de.ecw.zabos.sql.resource.DBResource;
import de.ecw.zabos.sql.vo.SmsOutVO;
import de.ecw.zabos.sql.vo.TelefonVO;
import de.ecw.zabos.types.UnixTime;
import de.ecw.zabos.types.id.AlarmId;
import de.ecw.zabos.types.id.PersonId;
import de.ecw.zabos.types.id.SmsOutId;
import de.ecw.zabos.types.id.SmsOutStatusId;

/**
 * Transaktionssichere Methoden für {@link SmsOutDAO}
 * 
 * @author ckl
 * 
 */

public class SmsOutTAO extends BaseTAO
{

    private final static Logger log = Logger.getLogger(SmsOutTAO.class);

    public SmsOutTAO(final DBResource _dbresource)
    {
        super(_dbresource);
    }

    /**
     * Ändert den Versand-Status einer SMS Nachricht.
     * 
     * @param _smsOutId
     * @param _statusId
     */
    public void updateSmsOutStatus(SmsOutId _smsOutId, SmsOutStatusId _statusId)
    {
        try
        {
            begin();
            SmsOutDAO smsOutDAO = daoFactory.getSmsOutDAO();

            smsOutDAO.updateSmsOutStatus(_smsOutId, _statusId);
            commit();
        }
        catch (StdException e)
        {
            log.error(e);
            rollback();
        }
    }

    /**
     * Markiert eine Person als "Entwarnt"
     * 
     * @param _alarmId
     * @param _personId
     */
    public void markPersonAsEntwarnt(AlarmId _alarmId, PersonId _personId)
    {
        try
        {
            begin();
            PersonInAlarmDAO personInAlarmDAO = daoFactory
                            .getPersonInAlarmDAO();

            personInAlarmDAO.markPersonAsEntwarnt(_alarmId, _personId);
            commit();
        }
        catch (StdException e)
        {
            log.error(e);
            rollback();
        }
    }

    /**
     * Eine SMS-Nachricht an alle aktiven Telefone, die einer Person zugeordnet
     * sind, verschicken.
     * 
     * @param _personId
     * @param _nachricht
     */
    public boolean sendeNachrichtAnPerson(PersonId _personId,
                    String _nachricht, String _context, String _contextAlarm,
                    String _contextO, String _contextOE)
    {
        try
        {
            boolean r;
            begin();
            TelefonDAO telefonDAO = daoFactory.getTelefonDAO();
            SmsOutDAO smsOutDAO = daoFactory.getSmsOutDAO();

            TelefonVO[] telefone = telefonDAO
                            .findAktiveTelefoneByPersonId(_personId);
            if (telefone.length > 0)
            {
                for (int i = 0; i < telefone.length; i++)
                {
                    TelefonVO telefon = telefone[i];

                    // Bestaetigungs-SMS verschicken
                    SmsOutVO smsOutVO = daoFactory.getObjectFactory()
                                    .createSmsOut();
                    smsOutVO.setNachricht(_nachricht);
                    smsOutVO.setZeitpunkt(UnixTime.now());
                    smsOutVO.setTelefonId(telefon.getTelefonId());
                    smsOutVO.setContext(_context);
                    smsOutVO.setContextAlarm(_contextAlarm);
                    smsOutVO.setContextO(_contextO);
                    smsOutVO.setContextOE(_contextOE);
                    smsOutVO.setFestnetzSms(telefon.getNummer()
                                    .isFestnetzNummer());
                    smsOutDAO.createSmsOut(smsOutVO);
                }
                r = true;
            }
            else
            {
                log.debug("der Person id=" + _personId
                                + " ist kein Telefon zugeordnet!");
                r = false;
            }
            commit();
            return r;
        }
        catch (StdException e)
        {
            log.error(e);
            rollback();
            return false;
        }
    }
}
