package de.ecw.zabos;

import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.sql.resource.DBResource;
import de.ecw.zabos.sql.vo.SystemKonfigurationVO;

/**
 * Factory-Klasse zum Erzeugen des {@link SystemKonfigurationVO}
 * 
 * @author ckl
 */
public class SystemKonfigurationFactory
{
    /**
     * Lädt über die Datenbankverbindung die Systemkonfiguration
     * 
     * @param _dbResource
     * @return
     * @throws StdException
     */
    public static SystemKonfigurationVO createInstance(
                    final DBResource _dbResource) throws StdException
    {
        return _dbResource.getDaoFactory().getSystemKonfigurationDAO()
                        .readKonfiguration();
    }
}
