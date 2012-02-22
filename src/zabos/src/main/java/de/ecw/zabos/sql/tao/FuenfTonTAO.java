package de.ecw.zabos.sql.tao;

import org.apache.log4j.Logger;

import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.sql.dao.FuenfTonDAO;
import de.ecw.zabos.sql.resource.DBResource;
import de.ecw.zabos.sql.vo.FuenfTonVO;

/**
 * Transaktionssichere Methoden für {@link FuenfTonDAO}
 * 
 * @author ckl
 * 
 */
public class FuenfTonTAO extends BaseTAO
{
    private final static Logger log = Logger.getLogger(FuenfTonTAO.class);

    public FuenfTonTAO(DBResource _dbresource)
    {
        super(_dbresource);
    }

    /**
     * Legt einen empfangen 5Ton in der Datenbank ab.
     * 
     * @param _fuenfTonVo
     * @return
     */
    public FuenfTonVO createFuenfTon(FuenfTonVO _fuenfTonVo)
    {
        try
        {
            begin();
            FuenfTonDAO fuenftonDAO = daoFactory.getFuenfTonDAO();
            _fuenfTonVo = fuenftonDAO.createFuenfTon(_fuenfTonVo);
            commit();
            return _fuenfTonVo;
        }
        catch (StdException e)
        {
            log.error(e);
            rollback();
            return null;
        }

    }

}
