package de.ecw.zabos.sql.vo;

import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.sql.Scheme;
import de.ecw.zabos.sql.vo.properties.IPropertyKuerzel;
import de.ecw.zabos.types.id.BaseId;
import de.ecw.zabos.types.id.FunktionstraegerId;
import de.ecw.zabos.util.StringUtils;

/**
 * ValueObject fuer {@link Scheme#FUNKTIONSTRAEGER_TABLE}
 * 
 * @author ckl
 */
public class FunktionstraegerVO extends DeletableBaseIdDescVO implements
                IPropertyKuerzel
{
    FunktionstraegerVO()
    {

    }

    private FunktionstraegerId id;

    private String kuerzel;

    public BaseId getBaseId()
    {
        return id;
    }

    public FunktionstraegerId getFunktionstraegerId()
    {
        return id;
    }

    /**
     * 
     * @param _funktionstraegerId
     * @throws StdException
     *             Wenn _id null
     */
    public void setFunktionstraegerId(FunktionstraegerId _funktionstraegerId) throws StdException
    {
        if (_funktionstraegerId == null)
        {
            throw new StdException("primary key darf nicht null sein");
        }
        id = _funktionstraegerId;
    }

    /**
     * 
     * @param _kuerzel
     * @throws StdException
     *             Wenn _kuerzel null oder leer
     */
    public void setKuerzel(String _kuerzel) throws StdException
    {
        if (StringUtils.isNullOrEmpty(_kuerzel))
        {
            throw new StdException("kuerzel darf nicht null oder leer sein");
        }
        kuerzel = _kuerzel;
    }

    public String getKuerzel()
    {
        return kuerzel;
    }

    public String toString()
    {
        return getBeschreibung() + " (" + getKuerzel() + ")";
    }
}
