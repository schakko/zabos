package de.ecw.zabos.sql.dao;

import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.sql.util.DBConnection;
import de.ecw.zabos.sql.vo.BaseIdVO;
import de.ecw.zabos.sql.vo.ObjectFactory;
import de.ecw.zabos.types.id.BaseId;

/**
 * Basisklasse für alle DAOs, die create/update/delete unterstützen.
 * 
 * 
 * @author bsp
 * 
 */
public abstract class AbstractCreateUpdateDeleteDAO extends AbstractBaseDAO
{

    public AbstractCreateUpdateDeleteDAO(DBConnection _dbconnection, ObjectFactory _objectFactory)
    {
        super(_dbconnection, _objectFactory);
    }

    /**
     * Erstellt das VO
     * 
     * @param _vo
     * @return
     * @throws StdException
     */
    public abstract BaseIdVO create(BaseIdVO _vo) throws StdException;

    /**
     * Ändert das VO
     * 
     * @param _vo
     * @return
     * @throws StdException
     */
    public abstract BaseIdVO update(BaseIdVO _vo) throws StdException;

    /**
     * Löscht das VO bzw. setzt es als gelöscht
     * 
     * @param _id
     * @throws StdException
     */
    public abstract void delete(BaseId _id) throws StdException;

}
