package de.ecw.zabos.sql.vo;

import de.ecw.zabos.sql.vo.properties.IPropertyBeschreibung;

/**
 * Erweitert das Basis VO um ein Beschreibungsfeld
 * 
 * @author bsp
 * 
 */
public abstract class BaseIdDescVO implements BaseIdVO, IPropertyBeschreibung
{
    BaseIdDescVO()
    {
        // TODO Auto-generated constructor stub
    }
    
    private String beschreibung;

    public String getBeschreibung()
    {
        return beschreibung;
    }

    public void setBeschreibung(String _beschreibung)
    {
        beschreibung = _beschreibung;
    }

    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime
                        * result
                        + ((beschreibung == null) ? 0 : beschreibung.hashCode());
        return result;
    }

    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        BaseIdDescVO other = (BaseIdDescVO) obj;

        if (other.getBaseId().getLongValue() != this.getBaseId().getLongValue())
        {
            return false;
        }

        return true;
    }

}
