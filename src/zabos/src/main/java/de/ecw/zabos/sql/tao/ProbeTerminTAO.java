package de.ecw.zabos.sql.tao;

import org.apache.log4j.Logger;

import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.sql.dao.ProbeTerminDAO;
import de.ecw.zabos.sql.resource.DBResource;
import de.ecw.zabos.sql.vo.ProbeTerminVO;
import de.ecw.zabos.types.id.ProbeTerminId;

/**
 * Transaktionssichere Methoden für {@link ProbeTerminDAO}
 * 
 * @author ckl
 * 
 */
public class ProbeTerminTAO extends BaseTAO
{

    private Logger log = Logger.getLogger(ProbeTerminTAO.class);

    public ProbeTerminTAO(DBResource _dbresource)
    {
        super(_dbresource);
    }

    /**
     * Legt einen neuen ProbeTermin an
     * 
     * @param _probeTerminVO
     * @return
     */
    public ProbeTerminVO createProbeTermin(ProbeTerminVO _probeTerminVO)
    {
        try
        {
            begin();
            ProbeTerminDAO probeTerminDAO = daoFactory.getProbeTerminDAO();
            _probeTerminVO = probeTerminDAO.createProbeTermin(_probeTerminVO);
            commit();
            return _probeTerminVO;
        }
        catch (StdException e)
        {
            log.error(e);
            rollback();
            return null;
        }
    }

    /**
     * Löscht einen ProbeTermin
     * 
     * @param _probeTerminVO
     * @return
     */
    public boolean deleteProbeTermin(ProbeTerminId _probeTerminId)
    {
        try
        {
            begin();
            ProbeTerminDAO probeTerminDAO = daoFactory.getProbeTerminDAO();
            probeTerminDAO.deleteProbeTerminById(_probeTerminId);
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
     * Ändert einen ProbeTermin
     * 
     * @param _probeTerminVO
     * @return
     */
    public ProbeTerminVO updateProbeTermin(ProbeTerminVO _probeTerminVO)
    {
        try
        {
            begin();
            ProbeTerminDAO probeTerminDAO = daoFactory.getProbeTerminDAO();
            _probeTerminVO = probeTerminDAO.updateProbeTermin(_probeTerminVO);
            commit();
            return _probeTerminVO;
        }
        catch (StdException e)
        {
            log.error(e);
            rollback();
            return null;
        }
    }

}
