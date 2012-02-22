package de.ecw.zabos.sql.vo;

/**
 * Basis Klasse für alle VOs, die als gelöscht markiert werden können
 * 
 * @author bsp
 * 
 */
public abstract class DeletableBaseIdVO implements BaseIdVO
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
