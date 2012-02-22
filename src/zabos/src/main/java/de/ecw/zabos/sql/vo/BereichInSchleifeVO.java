package de.ecw.zabos.sql.vo;

import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.sql.Scheme;
import de.ecw.zabos.types.id.BaseId;
import de.ecw.zabos.types.id.BereichId;
import de.ecw.zabos.types.id.BereichInSchleifeId;
import de.ecw.zabos.types.id.FunktionstraegerId;
import de.ecw.zabos.types.id.SchleifeId;

/**
 * ValueObject fuer {@link Scheme#BEREICH_IN_SCHLEIFE_TABLE}
 * 
 * @author ckl
 * 
 */
public class BereichInSchleifeVO extends DeletableBaseIdVO
{
    BereichInSchleifeVO()
    {
    }

    private BereichInSchleifeId id;

    private BereichId bereichId;

    private FunktionstraegerId funktionstraegerId;

    private SchleifeId schleifeId;

    private int sollstaerke;

    public BaseId getBaseId()
    {
        return id;
    }

    public BereichInSchleifeId getBereichInSchleifeId()
    {
        return id;
    }

    public void setBereichInSchleifeId(BereichInSchleifeId _id) throws StdException
    {
        if (_id == null)
        {
            throw new StdException("primary key darf nicht null sein");
        }

        id = _id;
    }

    public void setBereichId(BereichId bereichId)
    {
        this.bereichId = bereichId;
    }

    public BereichId getBereichId()
    {
        return bereichId;
    }

    public void setFunktionstraegerId(FunktionstraegerId funktionstraegerId)
    {
        this.funktionstraegerId = funktionstraegerId;
    }

    public FunktionstraegerId getFunktionstraegerId()
    {
        return funktionstraegerId;
    }

    public void setSollstaerke(int sollstaerke)
    {
        this.sollstaerke = sollstaerke;
    }

    public int getSollstaerke()
    {
        return sollstaerke;
    }

    public void setSchleifeId(SchleifeId schleifeId)
    {
        this.schleifeId = schleifeId;
    }

    public SchleifeId getSchleifeId()
    {
        return schleifeId;
    }

    public String toString()
    {
        return "Schleife [" + getSchleifeId() + "] Bereich [" + getBereichId()
                        + "] Funktionstraeger [" + getFunktionstraegerId()
                        + "]";
    }
}
