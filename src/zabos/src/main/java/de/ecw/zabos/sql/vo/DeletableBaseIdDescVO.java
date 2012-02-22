package de.ecw.zabos.sql.vo;

import de.ecw.zabos.sql.vo.properties.IPropertyGeloescht;

/**
 * Basis Klasse für alle VOs, die als gelöscht markiert werden können und die
 * einen Beschreibungstext haben
 * 
 * @author bsp
 * 
 */
public abstract class DeletableBaseIdDescVO extends BaseIdDescVO implements
                IPropertyGeloescht
{

    private boolean geloescht;

    public boolean getGeloescht()
    {
        return geloescht;
    }

    public void setGeloescht(boolean _geloescht)
    {
        geloescht = _geloescht;
    }

}
