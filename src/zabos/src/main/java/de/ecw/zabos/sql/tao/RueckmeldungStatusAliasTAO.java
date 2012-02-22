package de.ecw.zabos.sql.tao;

import org.apache.log4j.Logger;

import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.sql.dao.RueckmeldungStatusAliasDAO;
import de.ecw.zabos.sql.resource.DBResource;
import de.ecw.zabos.sql.vo.RueckmeldungStatusAliasVO;
import de.ecw.zabos.types.id.RueckmeldungStatusAliasId;

/**
 * Transaktionssichere Methoden für {@link RueckmeldungStatusAliasDAO}
 * 
 * @author ckl
 * 
 */

public class RueckmeldungStatusAliasTAO extends BaseTAO
{

    private final static Logger log = Logger.getLogger(RueckmeldungStatusAliasTAO.class);

    public RueckmeldungStatusAliasTAO(DBResource _dbresource)
    {
        super(_dbresource);
    }

    /**
     * Legt einen neuen Rueckmeldung-Status-Alias an.
     * 
     * @param _aliasVO
     * @return
     */
    public RueckmeldungStatusAliasVO createRueckmeldungStatusAlias(
                    RueckmeldungStatusAliasVO _aliasVO)
    {
        try
        {
            begin();

            RueckmeldungStatusAliasDAO aliasDAO = daoFactory
                            .getRueckmeldungStatusAliasDAO();
            _aliasVO = aliasDAO.createRueckmeldungStatusAlias(_aliasVO);

            commit();
            return _aliasVO;
        }
        catch (StdException e)
        {
            log.error(e);
            rollback();
            return null;
        }
    }

    /**
     * Löscht einen Rueckmeldung-Status-Alias.
     * 
     * @param _aliasId
     * @return
     */
    public boolean deleteRueckmeldungStatusAlias(
                    RueckmeldungStatusAliasId _aliasId)
    {
        try
        {
            begin();

            RueckmeldungStatusAliasDAO aliasDAO = daoFactory
                            .getRueckmeldungStatusAliasDAO();
            aliasDAO.deleteRueckmeldungStatusAlias(_aliasId);

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
     * Ändert einen Rueckmeldung-Status-Alias.
     * 
     * @param _aliasVO
     * @return
     */
    public RueckmeldungStatusAliasVO updateRueckmeldungStatusAlias(
                    RueckmeldungStatusAliasVO _aliasVO)
    {
        try
        {
            begin();

            RueckmeldungStatusAliasDAO aliasDAO = daoFactory
                            .getRueckmeldungStatusAliasDAO();
            _aliasVO = aliasDAO.updateRueckmeldungStatusAlias(_aliasVO);

            commit();
            return _aliasVO;
        }
        catch (StdException e)
        {
            log.error(e);
            rollback();
            return null;
        }
    }

}
