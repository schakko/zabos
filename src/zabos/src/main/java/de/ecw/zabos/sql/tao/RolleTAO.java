package de.ecw.zabos.sql.tao;

import org.apache.log4j.Logger;

import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.sql.dao.RolleDAO;
import de.ecw.zabos.sql.resource.DBResource;
import de.ecw.zabos.sql.vo.RolleVO;
import de.ecw.zabos.types.id.RechtId;
import de.ecw.zabos.types.id.RolleId;

/**
 * Transaktionssichere Methoden für {@link RolleDAO} und Konsorten
 * 
 * @author ckl
 * 
 */
public class RolleTAO extends BaseTAO
{

    private final static Logger log = Logger.getLogger(RolleTAO.class);

    public RolleTAO(DBResource _dbresource)
    {
        super(_dbresource);
    }

    /**
     * Weist einer Rolle ein Recht zu.
     * 
     * @param _rechtId
     * @param _rolleId
     * @throws StdException
     */
    public boolean addRechtToRolle(RechtId _rechtId, RolleId _rolleId)
    {
        try
        {
            begin();
            RolleDAO rolleDAO = daoFactory.getRolleDAO();
            rolleDAO.addRechtToRolle(_rechtId, _rolleId);
            commit();
            return true;
        }
        catch (StdException e)
        {
            log.error(e);
            rollback();
            return false;
        }
    }

    /**
     * Legt eine neue Rolle an.
     * 
     */
    public RolleVO createRolle(RolleVO _rolleVO)
    {
        try
        {
            begin();
            RolleDAO rolleDAO = daoFactory.getRolleDAO();
            _rolleVO = rolleDAO.createRolle(_rolleVO);
            commit();

            rolleDAO.CACHE_FIND_ALL.update();

            return _rolleVO;
        }
        catch (StdException e)
        {
            log.error(e);
            rollback();
            return null;
        }
    }

    /**
     * Entzieht einer Rolle ein Recht.
     * 
     * @param _rechtId
     * @param _rolleId
     * @throws StdException
     */
    public boolean removeRechtFromRolle(RechtId _rechtId, RolleId _rolleId)
    {
        try
        {
            begin();
            RolleDAO rolleDAO = daoFactory.getRolleDAO();
            rolleDAO.removeRechtFromRolle(_rechtId, _rolleId);
            commit();
            return true;
        }
        catch (StdException e)
        {
            log.error(e);
            rollback();
            return false;
        }
    }

    /**
     * Entzieht einer Rolle *alle* Rechte.
     * 
     * @param _rolleId
     * @throws StdException
     */
    public boolean removeRechteFromRolle(RolleId _rolleId)
    {
        try
        {
            begin();
            RolleDAO rolleDAO = daoFactory.getRolleDAO();
            rolleDAO.removeRechteFromRolle(_rolleId);
            commit();
            return true;
        }
        catch (StdException e)
        {
            log.error(e);
            rollback();
            return false;
        }
    }

    /**
     * Löscht eine Rolle.
     * 
     * @param _rolleId
     * @throws StdException
     */
    public boolean removeRolle(RolleId _rolleId)
    {
        try
        {
            begin();
            RolleDAO rolleDAO = daoFactory.getRolleDAO();
            rolleDAO.removeRolle(_rolleId);
            commit();

            rolleDAO.CACHE_FIND_ALL.update();

            return true;
        }
        catch (StdException e)
        {
            log.error(e);
            rollback();
            return false;
        }
    }

    /**
     * Ändert eine Rolle.
     * 
     * @param _rolleVO
     * @return
     * @throws StdException
     */
    public RolleVO updateRolle(RolleVO _rolleVO)
    {
        try
        {
            begin();
            RolleDAO rolleDAO = daoFactory.getRolleDAO();
            _rolleVO = rolleDAO.updateRolle(_rolleVO);
            commit();

            rolleDAO.CACHE_FIND_ALL.update();

            return _rolleVO;
        }
        catch (StdException e)
        {
            log.error(e);
            rollback();
            return null;
        }
    }

}
