package de.ecw.zabos.sql.tao;

import org.apache.log4j.Logger;

import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.sql.dao.SystemKonfigurationDAO;
import de.ecw.zabos.sql.resource.DBResource;
import de.ecw.zabos.sql.vo.SystemKonfigurationMc35VO;
import de.ecw.zabos.sql.vo.SystemKonfigurationVO;
import de.ecw.zabos.types.UnixTime;

/**
 * Transaktionssichere Methoden für {@link SystemKonfigurationDAO}
 * 
 * @author ckl
 * 
 */

public class SystemKonfigurationTAO extends BaseTAO
{

    private final static Logger log = Logger
                    .getLogger(SystemKonfigurationTAO.class);

    public SystemKonfigurationTAO(DBResource _dbresource)
    {
        super(_dbresource);
    }

    /**
     * Ändert die Systemkonfiguration.
     * 
     * @param _vo
     * @return
     */
    public SystemKonfigurationVO updateSystemKonfiguration(
                    SystemKonfigurationVO _vo)
    {
        try
        {
            begin();
            SystemKonfigurationDAO systemKonfigurationDAO = daoFactory
                            .getSystemKonfigurationDAO();
            _vo = systemKonfigurationDAO.updateKonfiguration(_vo);
            commit();
            return _vo;
        }
        catch (StdException e)
        {
            log.error(e);
            rollback();
            return null;
        }
    }

    /**
     * Ändert die Konfiguration eines Modems
     * 
     * @param _vo
     * @return
     */
    public SystemKonfigurationMc35VO updateSystemKonfigurationMc35(
                    SystemKonfigurationMc35VO _vo)
    {
        try
        {
            begin();
            SystemKonfigurationDAO systemKonfigurationDAO = daoFactory
                            .getSystemKonfigurationDAO();
            _vo = systemKonfigurationDAO.updateKonfigurationMc35(_vo);
            commit();
            return _vo;
        }
        catch (StdException e)
        {
            log.error(e);
            rollback();
            return null;
        }
    }

    /**
     * Erstellt eine MC35-Konfiguration
     * 
     * @param _vo
     * @return
     */
    public SystemKonfigurationMc35VO createSystemKonfigurationMc35(
                    SystemKonfigurationMc35VO _vo)
    {
        try
        {
            begin();
            SystemKonfigurationDAO systemKonfigurationDAO = daoFactory
                            .getSystemKonfigurationDAO();
            _vo = systemKonfigurationDAO.createSystemKonfigurationMc35(_vo);
            commit();
            return _vo;
        }
        catch (StdException e)
        {
            log.error(e);
            rollback();
            return null;
        }
    }
    
    /**
     * Löscht eine MC35-Konfiguration
     * 
     * @param _vo
     */
    public void deleteSystemKonfigurationMc35(SystemKonfigurationMc35VO _vo)
    {
        try
        {
            begin();
            SystemKonfigurationDAO systemKonfigurationDAO = daoFactory
                            .getSystemKonfigurationDAO();
            systemKonfigurationDAO.deleteKonfigurationMc35(_vo);
            commit();
        }
        catch (StdException e)
        {
            log.error(e);
            rollback();
        }
    }

    /**
     * Überprüft, ob das System reaktiviert werden soll
     */
    public void ueberpruefeSystemReaktivierung()
    {
        try
        {
            begin();

            SystemKonfigurationDAO systemKonfigurationDAO = daoFactory
                            .getSystemKonfigurationDAO();

            UnixTime t = systemKonfigurationDAO
                            .getSystemReaktivierungsZeitpunkt();

            if (t != null)
            {
                UnixTime now = UnixTime.now();
                if (now.isLaterThan(t))
                {
                    // System wieder reaktivieren
                    log.debug("System wurde automatisch reaktiviert");
                    systemKonfigurationDAO
                                    .setSystemReaktivierungsZeitpunkt(null);
                }
            }
            commit();
        }
        catch (StdException e)
        {
            log.error(e);
            rollback();
        }
    }

    /**
     * Deaktiviert das System
     * 
     * @param _systemKonfigurationVO
     */
    public void deaktiviereSystem(SystemKonfigurationVO _systemKonfigurationVO)
    {
        try
        {
            begin();
            UnixTime to = new UnixTime(1000 * _systemKonfigurationVO
                            .getReaktivierungTimeout());
            UnixTime t = UnixTime.now();
            t.add(to);

            SystemKonfigurationDAO systemKonfigurationDAO = daoFactory
                            .getSystemKonfigurationDAO();
            systemKonfigurationDAO.setSystemReaktivierungsZeitpunkt(t);

            log.debug("System wurde deaktiviert");

            commit();
        }
        catch (StdException e)
        {
            log.error(e);
            rollback();
        }
    }

}
